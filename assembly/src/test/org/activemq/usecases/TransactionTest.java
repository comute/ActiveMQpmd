/** 
 * 
 * Copyright 2004 Protique Ltd
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
package org.activemq.usecases;

import junit.framework.TestCase;
import org.activemq.ActiveMQConnectionFactory;
import org.activemq.command.ActiveMQQueue;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Date;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;

/**
 * @author pragmasoft
 * @version $Revision: 1.1.1.1 $
 */
public final class TransactionTest extends TestCase {

    private volatile String receivedText;

    private Session producerSession;
    private Session consumerSession;
    private Destination queue;

    private MessageProducer producer;
    private MessageConsumer consumer;
    private Connection connection;
    private CountDownLatch latch = new CountDownLatch(1);

    public void testTransaction() throws Exception {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        connection = factory.createConnection();
        queue = new ActiveMQQueue(getClass().getName() + "." + getName());

        producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        consumerSession = connection.createSession(true, 0);

        producer = producerSession.createProducer(queue);

        consumer = consumerSession.createConsumer(queue);
        consumer.setMessageListener(new MessageListener() {

            public void onMessage(Message m) {
                try {
                    TextMessage tm = (TextMessage) m;
                    receivedText = tm.getText();
                    latch.countDown();

                    System.out.println("consumer received message :" + receivedText);
                    consumerSession.commit();
                    System.out.println("committed transaction");
                }
                catch (JMSException e) {
                    try {
                        consumerSession.rollback();
                        System.out.println("rolled back transaction");
                    }
                    catch (JMSException e1) {
                        System.out.println(e1);
                        e1.printStackTrace();
                    }
                    System.out.println(e);
                    e.printStackTrace();
                }
            }
        });

        connection.start();

        TextMessage tm = null;
        try {
            tm = producerSession.createTextMessage();
            tm.setText("Hello, " + new Date());
            producer.send(tm);
            System.out.println("producer sent message :" + tm.getText());
        }
        catch (JMSException e) {
            e.printStackTrace();
        }

        System.out.println("Waiting for latch");
        latch.await();

        System.out.println("test completed, destination=" + receivedText);
    }

    protected void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
        super.tearDown();
    }
}