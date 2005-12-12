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

import org.activemq.broker.ConnectionContext;
import org.activemq.broker.region.policy.PolicyEntry;
import org.activemq.broker.region.policy.PolicyMap;
import org.activemq.command.ActiveMQDestination;
import org.activemq.command.ActiveMQQueue;
import org.activemq.command.ConsumerInfo;
import org.activemq.memory.UsageManager;
import org.activemq.store.MessageStore;
import org.activemq.store.PersistenceAdapter;
import org.activemq.thread.TaskRunnerFactory;

import javax.jms.InvalidSelectorException;

import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @version $Revision: 1.9 $
 */
public class QueueRegion extends AbstractRegion {

    private final PolicyMap policyMap;

    public QueueRegion(DestinationStatistics destinationStatistics, UsageManager memoryManager, TaskRunnerFactory taskRunnerFactory,
            PersistenceAdapter persistenceAdapter, PolicyMap policyMap) {
        super(destinationStatistics, memoryManager, taskRunnerFactory, persistenceAdapter);
        this.policyMap = policyMap;
    }

    public String toString() {
        return "QueueRegion: destinations=" + destinations.size() + ", subscriptions=" + subscriptions.size() + ", memory=" + memoryManager.getPercentUsage()
                + "%";
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    protected Destination createDestination(ActiveMQDestination destination) throws Throwable {
        MessageStore store = persistenceAdapter.createQueueMessageStore((ActiveMQQueue) destination);
        Queue queue = new Queue(destination, memoryManager, store, destinationStatistics, taskRunnerFactory);
        configureQueue(queue, destination);
        return queue;
    }

    protected void configureQueue(Queue queue, ActiveMQDestination destination) {
        if (policyMap != null) {
            PolicyEntry entry = policyMap.getEntryFor(destination);
            if (entry != null) {
                entry.configure(queue);
            }
        }
    }

    protected Subscription createSubscription(ConnectionContext context, ConsumerInfo info) throws InvalidSelectorException {
        if (info.isBrowser()) {
            return new QueueBrowserSubscription(context, info);
        }
        else {
            return new QueueSubscription(context, info);
        }
    }

    protected Set getInactiveDestinations() {
        Set inactiveDestinations = super.getInactiveDestinations();
        for (Iterator iter = inactiveDestinations.iterator(); iter.hasNext();) {
            ActiveMQDestination dest = (ActiveMQDestination) iter.next();
            if (!dest.isQueue())
                iter.remove();
        }
        return inactiveDestinations;
    }
}
