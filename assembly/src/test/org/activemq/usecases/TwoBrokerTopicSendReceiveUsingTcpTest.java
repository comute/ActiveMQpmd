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
package org.apache.activemq.activemq.usecases;

import javax.jms.JMSException;

import org.apache.activemq.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.activemq.xbean.BrokerFactoryBean;
import org.apache.activemq.activemq.broker.BrokerService;
import org.apache.activemq.activemq.broker.TransportConnector;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @version $Revision: 1.1.1.1 $
 */
public class TwoBrokerTopicSendReceiveUsingTcpTest extends TwoBrokerTopicSendReceiveTest {
    private BrokerService receiverBroker;
    private BrokerService senderBroker;

    protected void setUp() throws Exception {
        BrokerFactoryBean brokerFactory;

        brokerFactory = new BrokerFactoryBean(new ClassPathResource("org/activemq/usecases/receiver.xml"));
        brokerFactory.afterPropertiesSet();
        receiverBroker = brokerFactory.getBroker();

        brokerFactory = new BrokerFactoryBean(new ClassPathResource("org/activemq/usecases/sender.xml"));
        brokerFactory.afterPropertiesSet();
        senderBroker = brokerFactory.getBroker();

        super.setUp();
        Thread.sleep(2000);
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        if (receiverBroker != null) {
            receiverBroker.stop();
        }
        if (senderBroker != null) {
            senderBroker.stop();
        }
    }

    
    protected ActiveMQConnectionFactory createReceiverConnectionFactory() throws JMSException {
        try {
            ActiveMQConnectionFactory fac =  new ActiveMQConnectionFactory(((TransportConnector)receiverBroker.getTransportConnectors().get(0)).getConnectUri());
            return fac;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected ActiveMQConnectionFactory createSenderConnectionFactory() throws JMSException {
        try {
            ActiveMQConnectionFactory fac = new ActiveMQConnectionFactory(((TransportConnector)senderBroker.getTransportConnectors().get(0)).getConnectUri());
            return fac;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
