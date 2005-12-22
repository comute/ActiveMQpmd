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
package org.activemq.network;

import javax.jms.DeliveryMode;

import org.activemq.broker.StubConnection;
import org.activemq.command.ActiveMQDestination;
import org.activemq.command.ConnectionInfo;
import org.activemq.command.ConsumerInfo;
import org.activemq.command.Message;
import org.activemq.command.ProducerInfo;
import org.activemq.command.SessionInfo;

import junit.framework.Test;


public class ForwardingBridgeTest extends NetworkTestSupport {
    
    public ActiveMQDestination destination;
    public byte destinationType;
    public int deliveryMode;
    private ForwardingBridge bridge;

    public void initCombosForTestAddConsumerThenSend() {    
        addCombinationValues( "deliveryMode", new Object[]{ 
                new Integer(DeliveryMode.NON_PERSISTENT), 
                new Integer(DeliveryMode.PERSISTENT)} );        
        addCombinationValues( "destinationType", new Object[]{ 
                new Byte(ActiveMQDestination.QUEUE_TYPE), 
                new Byte(ActiveMQDestination.TOPIC_TYPE), 
                } );
    }    
    public void testAddConsumerThenSend() throws Throwable {
        
        // Start a producer on local broker 
        StubConnection connection1 = createConnection();
        ConnectionInfo connectionInfo1 = createConnectionInfo();
        SessionInfo sessionInfo1 = createSessionInfo(connectionInfo1);        
        ProducerInfo producerInfo = createProducerInfo(sessionInfo1);
        connection1.send(connectionInfo1);
        connection1.send(sessionInfo1);
        connection1.send(producerInfo);

        destination = createDestinationInfo(connection1, connectionInfo1, destinationType);

        // Start a consumer on a remote broker
        StubConnection connection2 = createRemoteConnection();
        ConnectionInfo connectionInfo2 = createConnectionInfo();
        SessionInfo sessionInfo2 = createSessionInfo(connectionInfo2);        
        connection2.send(connectionInfo2);
        connection2.send(sessionInfo2);
        ConsumerInfo consumerInfo = createConsumerInfo(sessionInfo2, destination);        
        connection2.send(consumerInfo);
        
        // Send the message to the local boker.
        connection1.send(createMessage(producerInfo, destination, deliveryMode));
        
        // Make sure the message was delivered via the remote.
        Message m = receiveMessage(connection2);
        assertNotNull(m);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        bridge = new ForwardingBridge(createTransport(), createRemoteTransport());
        bridge.setClientId("local-remote-bridge");
        bridge.setDispatchAsync(false);
        bridge.start();
        
        // PATCH: Give forwarding bridge a chance to finish setting up
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    protected void tearDown() throws Exception {
        bridge.stop();
        super.tearDown();
    }
    
    public static Test suite() {
        return suite(ForwardingBridgeTest.class);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
