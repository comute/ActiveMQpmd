/**
 *
 * Copyright 2004 The Apache Software Foundation
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
 */
package org.activemq.broker.region;

import java.util.Iterator;
import java.util.Set;

import javax.jms.JMSException;

import org.activemq.broker.ConnectionContext;
import org.activemq.command.ActiveMQDestination;
import org.activemq.command.ConsumerInfo;
import org.activemq.command.Message;
import org.activemq.command.MessageAck;
import org.activemq.command.RemoveSubscriptionInfo;
import org.activemq.filter.DestinationMap;
import org.activemq.memory.UsageManager;
import org.activemq.store.PersistenceAdapter;
import org.activemq.thread.TaskRunnerFactory;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @version $Revision: 1.14 $
 */
abstract public class AbstractRegion implements Region {
    
    protected final ConcurrentHashMap destinations = new ConcurrentHashMap();
    protected final DestinationMap destinationMap = new DestinationMap();
    protected final ConcurrentHashMap subscriptions = new ConcurrentHashMap();
    protected final UsageManager memoryManager;
    protected final PersistenceAdapter persistenceAdapter;
    protected final DestinationStatistics destinationStatistics;
    protected boolean autoCreateDestinations=true;
    protected final TaskRunnerFactory taskRunnerFactory;
    protected final Object destinationsMutex = new Object();
    
    public AbstractRegion(DestinationStatistics destinationStatistics, UsageManager memoryManager, TaskRunnerFactory taskRunnerFactory, PersistenceAdapter persistenceAdapter) {
        this.destinationStatistics = destinationStatistics;
        this.memoryManager = memoryManager;
        this.taskRunnerFactory = taskRunnerFactory;
        this.persistenceAdapter = persistenceAdapter;
    }

    public Destination addDestination(ConnectionContext context, ActiveMQDestination destination) throws Throwable {
        Destination dest = createDestination(destination);
        dest.start();
        synchronized(destinationsMutex){
            destinations.put(destination,dest);
            destinationMap.put(destination,dest);
        }

        // Add all consumers that are interested in the destination. 
        for (Iterator iter = subscriptions.values().iterator(); iter.hasNext();) {
            Subscription sub = (Subscription) iter.next();
            if( sub.matches(destination) ) {
                dest.addSubscription(context, sub);
            }
        }
        return dest;
    }

    public void removeDestination(ConnectionContext context, ActiveMQDestination destination, long timeout)
            throws Throwable {
        
        // The destination cannot be removed if there are any active subscriptions 
        for (Iterator iter = subscriptions.values().iterator(); iter.hasNext();) {
            Subscription sub = (Subscription) iter.next();
            if( sub.matches(destination) ) {
                throw new JMSException("Destination still has an active subscription: "+ destination);
            }
        }

        synchronized(destinationsMutex){
            Destination dest=(Destination) destinations.remove(destination);
            if(dest==null)
                throw new IllegalArgumentException("The destination does not exist: "+destination);

            destinationMap.removeAll(destination);
            dest.dispose(context);
            dest.stop();
        }
    }

    public void addConsumer(ConnectionContext context, ConsumerInfo info) throws Throwable {

        Subscription sub = createSubscription(context, info);

        // We may need to add some destinations that are in persistent store but not active 
        // in the broker.
        //
        // TODO: think about this a little more.  This is good cause destinations are not loaded into 
        // memory until a client needs to use the queue, but a management agent viewing the 
        // broker will not see a destination that exists in persistent store.  We may want to
        // eagerly load all destinations into the broker but have an inactive state for the
        // destination which has reduced memory usage.
        //
        if( persistenceAdapter!=null ) {
            Set inactiveDests = getInactiveDestinations();
            for (Iterator iter = inactiveDests.iterator(); iter.hasNext();) {
                ActiveMQDestination dest = (ActiveMQDestination) iter.next();
                if( sub.matches(dest) ) {
                    context.getBroker().addDestination(context, dest);
                }
            }
        }
        
        subscriptions.put(info.getConsumerId(), sub);

        // Add the subscription to all the matching queues.
        for (Iterator iter = destinationMap.get(info.getDestination()).iterator(); iter.hasNext();) {
            Destination dest = (Destination) iter.next();            
            dest.addSubscription(context, sub);
        }        

        if( info.isBrowser() ) {
            ((QueueBrowserSubscription)sub).browseDone();
        }
        
    }

    /**
     * @return
     */
    protected Set getInactiveDestinations() {
        Set inactiveDests = persistenceAdapter.getDestinations();
        inactiveDests.removeAll( destinations.keySet() );
        return inactiveDests;
    }
    
    public void removeConsumer(ConnectionContext context, ConsumerInfo info) throws Throwable {
        
        Subscription sub = (Subscription) subscriptions.remove(info.getConsumerId());
        if( sub==null )
            throw new IllegalArgumentException("The subscription does not exist: "+info.getConsumerId());
        
        // remove the subscription from all the matching queues.
        for (Iterator iter = destinationMap.get(info.getDestination()).iterator(); iter.hasNext();) {
            Destination dest = (Destination) iter.next();
            dest.removeSubscription(context, sub);
        }
    }

    public void removeSubscription(ConnectionContext context, RemoveSubscriptionInfo info) throws Throwable {
        throw new JMSException("Invalid operation.");
    }

    public void send(ConnectionContext context, Message messageSend)
            throws Throwable {
        Destination dest = lookup(context, messageSend.getDestination());
        dest.send(context, messageSend);
    }
    
    public void acknowledge(ConnectionContext context, MessageAck ack) throws Throwable {
        
        Subscription sub = (Subscription) subscriptions.get(ack.getConsumerId());
        if( sub==null )
            throw new IllegalArgumentException("The subscription does not exist: "+ack.getConsumerId());
        sub.acknowledge(context, ack);
        
    }

    protected Destination lookup(ConnectionContext context, ActiveMQDestination destination) throws Throwable {
        synchronized(destinationsMutex){
            Destination dest=(Destination) destinations.get(destination);
            if(dest==null){
                if(autoCreateDestinations){
                    // Try to auto create the destination... re-invoke broker from the
                    // top so that the proper security checks are performed.
                    context.getBroker().addDestination(context,destination);
                    // We should now have the dest created.
                    dest=(Destination) destinations.get(destination);
                }
                if(dest==null){
                    throw new JMSException("The destination "+destination+" does not exist.");
                }
            }
            return dest;
        }
    }
    
    public void gc() {
        for (Iterator iter = subscriptions.values().iterator(); iter.hasNext();) {
            Subscription sub = (Subscription) iter.next();
            sub.gc();
        }        
        for (Iterator iter = destinations.values()  .iterator(); iter.hasNext();) {
            Destination dest = (Destination) iter.next();
            dest.gc();
        }        
    }

    protected abstract Subscription createSubscription(ConnectionContext context, ConsumerInfo info) throws Throwable;
    abstract protected Destination createDestination(ActiveMQDestination destination) throws Throwable;

    public boolean isAutoCreateDestinations() {
        return autoCreateDestinations;
    }

    public void setAutoCreateDestinations(boolean autoCreateDestinations) {
        this.autoCreateDestinations = autoCreateDestinations;
    }
    

}
