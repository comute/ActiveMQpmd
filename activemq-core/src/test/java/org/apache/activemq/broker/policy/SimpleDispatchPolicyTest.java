/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
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
package org.apache.activemq.broker.policy;

import org.apache.activemq.broker.QueueSubscriptionTest;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.SimpleDispatchPolicy;

import java.util.Iterator;
import java.util.List;

public class SimpleDispatchPolicyTest extends QueueSubscriptionTest {

    protected BrokerService createBroker() throws Exception {
        BrokerService broker = super.createBroker();

        PolicyEntry policy = new PolicyEntry();
        policy.setDispatchPolicy(new SimpleDispatchPolicy());

        PolicyMap pMap = new PolicyMap();
        pMap.setDefaultEntry(policy);

        broker.setDestinationPolicy(pMap);

        return broker;
    }

    public void testOneProducerTwoConsumersSmallMessagesLargePrefetch() throws Exception {
        super.testOneProducerTwoConsumersSmallMessagesLargePrefetch();

        // One consumer should have received all messages, and the rest none
        assertOneConsumerReceivedAllMessages(messageCount);
    }

    public void testOneProducerTwoConsumersLargeMessagesLargePrefetch() throws Exception {
        super.testOneProducerTwoConsumersLargeMessagesLargePrefetch();

        // One consumer should have received all messages, and the rest none
        assertOneConsumerReceivedAllMessages(messageCount);
    }

    public void assertOneConsumerReceivedAllMessages(int messageCount) throws Exception {
        boolean found = false;
        for (Iterator i=consumers.keySet().iterator(); i.hasNext();) {
            List messageList = (List)consumers.get(i.next());
            if (messageList.size() > 0) {
                if (found) {
                    fail("No other consumers should have received any messages");
                } else {
                    assertTrue("Consumer should have received all " + messageCount + " messages. Actual messages received is " + messageList.size(), messageList.size()==messageCount);
                    found = true;
                }
            }
        }

        if (!found) {
            fail("At least one consumer should have received all messages");
        }
    }
}
