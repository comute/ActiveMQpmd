/**
 * <a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>
 *
 * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/
package org.activemq.broker.region;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.activemq.broker.ConnectionContext;
import org.activemq.broker.region.policy.DispatchPolicy;
import org.activemq.broker.region.policy.RoundRobinDispatchPolicy;
import org.activemq.command.ActiveMQDestination;
import org.activemq.command.ConsumerId;
import org.activemq.command.Message;
import org.activemq.command.MessageAck;
import org.activemq.command.MessageId;
import org.activemq.filter.MessageEvaluationContext;
import org.activemq.memory.UsageManager;
import org.activemq.store.MessageRecoveryListener;
import org.activemq.store.MessageStore;
import org.activemq.thread.TaskRunnerFactory;
import org.activemq.thread.Valve;
import org.activemq.transaction.Synchronization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Queue is a List of MessageEntry objects that are dispatched to matching
 * subscriptions.
 * 
 * @version $Revision: 1.28 $
 */
public class Queue implements Destination {

    private final Log log;

    protected final ActiveMQDestination destination;
    protected final CopyOnWriteArrayList consumers = new CopyOnWriteArrayList();
    protected final LinkedList messages = new LinkedList();
    protected final Valve dispatchValve = new Valve(true);
    protected final UsageManager usageManager;
    protected final DestinationStatistics destinationStatistics = new DestinationStatistics();

    private Subscription exclusiveOwner;
    private final ConcurrentHashMap messageGroupOwners = new ConcurrentHashMap();

    protected long garbageSize = 0;
    protected long garbageSizeBeforeCollection = 1000;
    private DispatchPolicy dispatchPolicy = new RoundRobinDispatchPolicy();
    protected final MessageStore store;
    protected int highestSubscriptionPriority;

    public Queue(ActiveMQDestination destination, final UsageManager memoryManager, MessageStore store,
            DestinationStatistics parentStats, TaskRunnerFactory taskFactory) throws Throwable {
        this.destination = destination;
        this.usageManager = memoryManager;
        this.store = store;

        destinationStatistics.setParent(parentStats);
        this.log = LogFactory.getLog(getClass().getName() + "." + destination.getPhysicalName());

        if (store != null) {
            // Restore the persistent messages.
            store.recover(new MessageRecoveryListener() {
                public void recoverMessage(Message message) {
                    message.setRegionDestination(Queue.this);
                    MessageReference reference = createMessageReference(message);
                    messages.add(reference);
                    reference.decrementReferenceCount();
                    destinationStatistics.getMessages().increment();
                }

                public void recoverMessageReference(String messageReference) throws Throwable {
                    throw new RuntimeException("Should not be called.");
                }
            });
        }
    }

    public synchronized boolean lock(MessageReference node, Subscription sub) {
        if (exclusiveOwner == sub)
            return true;
        if (exclusiveOwner != null)
            return false;
        if (sub.getConsumerInfo().getPriority() != highestSubscriptionPriority)
            return false;
        if (sub.getConsumerInfo().isExclusive()) {
            exclusiveOwner = sub;
        }
        return true;
    }

    public void addSubscription(ConnectionContext context, Subscription sub) throws Throwable {
        sub.add(context, this);
        destinationStatistics.getConsumers().increment();

        // synchronize with dispatch method so that no new messages are sent
        // while
        // setting up a subscription. avoid out of order messages, duplicates
        // etc.
        dispatchValve.turnOff();

        MessageEvaluationContext msgContext = context.getMessageEvaluationContext();
        try {
            consumers.add(sub);

            highestSubscriptionPriority = calcHighestSubscriptionPriority();
            msgContext.setDestination(destination);

            synchronized (messages) {
                // Add all the matching messages in the queue to the
                // subscription.
                for (Iterator iter = messages.iterator(); iter.hasNext();) {

                    IndirectMessageReference node = (IndirectMessageReference) iter.next();
                    if (node.isDropped() ) {
                        continue;
                    }

                    try {
                        msgContext.setMessageReference(node);
                        if (sub.matches(node, msgContext)) {
                            sub.add(node);
                        }
                    }
                    catch (IOException e) {
                        log.warn("Could not load message: " + e, e);
                    }
                }
            }

        }
        finally {
            msgContext.clear();
            dispatchValve.turnOn();
        }
    }

    public void removeSubscription(ConnectionContext context, Subscription sub) throws Throwable {

        destinationStatistics.getConsumers().decrement();

        // synchronize with dispatch method so that no new messages are sent
        // while
        // removing up a subscription.
        dispatchValve.turnOff();
        try {

            consumers.remove(sub);
            sub.remove(context, this);

            highestSubscriptionPriority = calcHighestSubscriptionPriority();

            boolean wasExclusiveOwner = false;
            if (exclusiveOwner == sub) {
                exclusiveOwner = null;
                wasExclusiveOwner = true;
            }

            HashSet ownedGroups = new HashSet();
            ConsumerId consumerId = sub.getConsumerInfo().getConsumerId();
            for (Iterator iter = messageGroupOwners.keySet().iterator(); iter.hasNext();) {
                String group = (String) iter.next();
                ConsumerId owner = (ConsumerId) messageGroupOwners.get(group);
                if (owner.equals(consumerId)) {
                    ownedGroups.add(group);
                    iter.remove();
                }
            }

            synchronized (messages) {
                if (!sub.getConsumerInfo().isBrowser()) {
                    MessageEvaluationContext msgContext = context.getMessageEvaluationContext();
                    try {
                        msgContext.setDestination(destination);
                        
                        for (Iterator iter = messages.iterator(); iter.hasNext();) {
                            IndirectMessageReference node = (IndirectMessageReference) iter.next();                            
                            if (node.isDropped() ) {
                                continue;
                            }
    
                            String groupID = node.getGroupID();
    
                            // Re-deliver all messages that the sub locked
                            if (node.getLockOwner() == sub || wasExclusiveOwner || (groupID != null && ownedGroups.contains(groupID))) {
                                node.incrementRedeliveryCounter();
                                node.unlock();
                                msgContext.setMessageReference(node);
                                dispatchPolicy.dispatch(context, node, msgContext, consumers);
                            }
                        }
                    } finally {
                        msgContext.clear();
                    }
                }
            }

        }
        finally {
            dispatchValve.turnOn();
        }

    }

    public void send(final ConnectionContext context, final Message message) throws Throwable {

        if( context.isProducerFlowControl() )
            usageManager.waitForSpace();
        
        message.setRegionDestination(this);

        if (store != null && message.isPersistent())
            store.addMessage(context, message);

        final MessageReference node = createMessageReference(message);
        try {

            if (context.isInTransaction()) {
                context.getTransaction().addSynchronization(new Synchronization() {
                    public void afterCommit() throws Throwable {
                        dispatch(context, node, message);
                    }
                });
            }
            else {
                dispatch(context, node, message);
            }
        } finally {
            node.decrementReferenceCount();
        }
    }

    public void dispose(ConnectionContext context) throws IOException {
        if (store != null) {
            store.removeAllMessages(context);
        }
        destinationStatistics.setParent(null);
    }

    public void dropEvent() {
        // TODO: need to also decrement when messages expire.
        destinationStatistics.getMessages().decrement();
        synchronized (messages) {
            garbageSize++;
            if (garbageSize > garbageSizeBeforeCollection) {
                gc();
            }
        }
    }

    public void gc() {
        synchronized (messages) {
            for (Iterator iter = messages.iterator(); iter.hasNext();) {
                // Remove dropped messages from the queue.
                IndirectMessageReference node = (IndirectMessageReference) iter.next();
                if (node.isDropped()) {                    
                    garbageSize--;
                    iter.remove();
                    continue;
                }
            }
        }
    }

    public void acknowledge(ConnectionContext context, Subscription sub, final MessageAck ack, final MessageReference node) throws IOException {
        if (store != null && node.isPersistent()) {
            store.removeMessage(context, ack);
        }
    }

    public Message loadMessage(MessageId messageId) throws IOException {
        Message msg = store.getMessage(messageId);
        if( msg!=null ) {
            msg.setRegionDestination(this);
        }
        return msg;
    }

    public String toString() {
        return "Queue: destination=" + destination.getPhysicalName() + ", subscriptions=" + consumers.size() + ", memory=" + usageManager.getPercentUsage()
                + "%, size=" + messages.size() + ", in flight groups=" + messageGroupOwners.size();
    }

    public void start() throws Exception {
    }

    public void stop() throws Exception {
    }

    // Properties
    // -------------------------------------------------------------------------
    public ActiveMQDestination getActiveMQDestination() {
        return destination;
    }

    public UsageManager getUsageManager() {
        return usageManager;
    }

    public DestinationStatistics getDestinationStatistics() {
        return destinationStatistics;
    }

    public ConcurrentHashMap getMessageGroupOwners() {
        return messageGroupOwners;
    }

    public DispatchPolicy getDispatchPolicy() {
        return dispatchPolicy;
    }

    public void setDispatchPolicy(DispatchPolicy dispatchPolicy) {
        this.dispatchPolicy = dispatchPolicy;
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    private MessageReference createMessageReference(Message message) {
        return new IndirectMessageReference(this, message);
    }

    private void dispatch(ConnectionContext context, MessageReference node, Message message) throws Throwable {
        dispatchValve.increment();
        MessageEvaluationContext msgContext = context.getMessageEvaluationContext();
        try {
            destinationStatistics.onMessageEnqueue(message);
            synchronized (messages) {
                messages.add(node);
            }

            if (consumers.isEmpty())
                return;

            msgContext.setDestination(destination);
            msgContext.setMessageReference(node);

            dispatchPolicy.dispatch(context, node, msgContext, consumers);
        }
        finally {
            msgContext.clear();
            dispatchValve.decrement();
        }
    }

    private int calcHighestSubscriptionPriority() {
        int rc = Integer.MIN_VALUE;
        for (Iterator iter = consumers.iterator(); iter.hasNext();) {
            Subscription sub = (Subscription) iter.next();
            if (sub.getConsumerInfo().getPriority() > rc) {
                rc = sub.getConsumerInfo().getPriority();
            }
        }
        return rc;
    }

    public MessageStore getMessageStore() {
        return store;
    }

}
