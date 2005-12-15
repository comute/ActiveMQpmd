/**
 * 
 * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com
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
package org.activemq.xbean;

import org.activemq.broker.Broker;
import org.activemq.broker.BrokerService;
import org.activemq.broker.ConnectionContext;
import org.activemq.broker.region.Topic;
import org.activemq.broker.region.policy.DispatchPolicy;
import org.activemq.broker.region.policy.LastImageSubscriptionRecoveryPolicy;
import org.activemq.broker.region.policy.RoundRobinDispatchPolicy;
import org.activemq.broker.region.policy.StrictOrderDispatchPolicy;
import org.activemq.broker.region.policy.SubscriptionRecoveryPolicy;
import org.activemq.broker.region.policy.TimedSubscriptionRecoveryPolicy;
import org.activemq.command.ActiveMQTopic;
import org.activemq.command.ConnectionId;
import org.activemq.command.ConnectionInfo;
import org.springframework.core.io.ClassPathResource;

import junit.framework.TestCase;

/**
 * 
 * @version $Revision: 1.1 $
 */
public class XBeanConfigTest extends TestCase {

    protected BrokerService brokerService;
    protected Broker broker;
    protected ConnectionContext context;
    protected ConnectionInfo info;

    public void testBrokerConfiguredCorrectly() throws Throwable {


        Topic topic = (Topic) broker.addDestination(context, new ActiveMQTopic("FOO.BAR"));
        DispatchPolicy dispatchPolicy = topic.getDispatchPolicy();
        assertTrue("dispatchPolicy should be RoundRobinDispatchPolicy: " + dispatchPolicy, dispatchPolicy instanceof RoundRobinDispatchPolicy);
        
        SubscriptionRecoveryPolicy subscriptionRecoveryPolicy = topic.getSubscriptionRecoveryPolicy();
        assertTrue("subscriptionRecoveryPolicy should be LastImageSubscriptionRecoveryPolicy: " + subscriptionRecoveryPolicy,
                subscriptionRecoveryPolicy instanceof LastImageSubscriptionRecoveryPolicy);
        
        System.out.println("destination: " + topic);
        System.out.println("dispatchPolicy: " + dispatchPolicy);
        System.out.println("subscriptionRecoveryPolicy: " + subscriptionRecoveryPolicy);

        topic = (Topic) broker.addDestination(context, new ActiveMQTopic("ORDERS.BOOKS"));
        dispatchPolicy = topic.getDispatchPolicy();
        assertTrue("dispatchPolicy should be StrictOrderDispatchPolicy: " + dispatchPolicy, dispatchPolicy instanceof StrictOrderDispatchPolicy);

        subscriptionRecoveryPolicy = topic.getSubscriptionRecoveryPolicy();
        assertTrue("subscriptionRecoveryPolicy should be TimedSubscriptionRecoveryPolicy: " + subscriptionRecoveryPolicy,
                subscriptionRecoveryPolicy instanceof TimedSubscriptionRecoveryPolicy);
        TimedSubscriptionRecoveryPolicy timedSubcriptionPolicy = (TimedSubscriptionRecoveryPolicy) subscriptionRecoveryPolicy;
        assertEquals("getRecoverDuration()", 60000, timedSubcriptionPolicy.getRecoverDuration());
        
        System.out.println("destination: " + topic);
        System.out.println("dispatchPolicy: " + dispatchPolicy);
        System.out.println("subscriptionRecoveryPolicy: " + subscriptionRecoveryPolicy);
    }

    protected void setUp() throws Exception {
        brokerService = createBroker();
        broker = brokerService.getBroker();

        // started automatically
        //brokerService.start();

        context = new ConnectionContext();
        context.setBroker(broker);
        info = new ConnectionInfo();
        info.setClientId("James");
        info.setUserName("James");
        info.setConnectionId(new ConnectionId("1234"));
        
        try {
            broker.addConnection(context, info);
        }
        catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        assertNotNull("No broker created!");
    }

    protected void tearDown() throws Exception {
        if (brokerService != null) {
            brokerService.stop();
        }
    }

    protected BrokerService createBroker() throws Exception {
        BrokerFactoryBean factory = new BrokerFactoryBean(new ClassPathResource("org/activemq/xbean/activemq-policy.xml"));
        factory.afterPropertiesSet();
        return factory.getBroker();
    }

}
