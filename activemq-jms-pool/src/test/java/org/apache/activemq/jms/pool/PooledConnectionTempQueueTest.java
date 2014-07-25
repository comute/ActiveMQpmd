package org.apache.activemq.jms.pool;

import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PooledConnectionTempQueueTest {

    private final Logger LOG = LoggerFactory.getLogger(PooledConnectionTempQueueTest.class);

    protected static final String SERVICE_QUEUE = "queue1";

    @Test
    public void testTempQueueIssue() throws JMSException, InterruptedException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        final PooledConnectionFactory cf = new PooledConnectionFactory();
        cf.setConnectionFactory(factory);

        Connection connection = cf.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        LOG.info("First connection was {}", connection);

        // This order seems to matter to reproduce the issue
        connection.close();
        session.close();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    receiveAndRespondWithMessageIdAsCorrelationId(cf, SERVICE_QUEUE);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        sendWithReplyToTemp(cf, SERVICE_QUEUE);
    }

    private void sendWithReplyToTemp(ConnectionFactory cf, String serviceQueue) throws JMSException,
        InterruptedException {
        Connection con = cf.createConnection();
        con.start();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue tempQueue = session.createTemporaryQueue();
        TextMessage msg = session.createTextMessage("Request");
        msg.setJMSReplyTo(tempQueue);
        MessageProducer producer = session.createProducer(session.createQueue(serviceQueue));
        producer.send(msg);

        // This sleep also seems to matter
        Thread.sleep(5000);

        MessageConsumer consumer = session.createConsumer(tempQueue);
        Message replyMsg = consumer.receive();
        System.out.println(replyMsg.getJMSCorrelationID());

        consumer.close();

        producer.close();
        session.close();
        con.close();
    }

    public void receiveAndRespondWithMessageIdAsCorrelationId(ConnectionFactory connectionFactory,
                                                              String queueName) throws JMSException {
        Connection con = connectionFactory.createConnection();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createQueue(queueName));
        final javax.jms.Message inMessage = consumer.receive();

        String requestMessageId = inMessage.getJMSMessageID();
        System.out.println("Received message " + requestMessageId);
        final TextMessage replyMessage = session.createTextMessage("Result");
        replyMessage.setJMSCorrelationID(inMessage.getJMSMessageID());
        final MessageProducer producer = session.createProducer(inMessage.getJMSReplyTo());
        System.out.println("Sending reply to " + inMessage.getJMSReplyTo());
        producer.send(replyMessage);

        producer.close();
        consumer.close();
        session.close();
        con.close();
    }

}
