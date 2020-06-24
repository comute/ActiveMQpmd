/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.junit.Assert.assertEquals;

/**
 Non transactional concurrent producer/consumer to single dest
 */
@RunWith(Parameterized.class)
public class AMQ5266SingleDestTest {
    static Logger LOG = LoggerFactory.getLogger(AMQ5266SingleDestTest.class);
    String activemqURL;
    BrokerService brokerService;

    public int numDests = 1;
    public int messageSize = 10*1000;

    @Parameterized.Parameter(0)
    public int publisherMessagesPerThread = 1000;

    @Parameterized.Parameter(1)
    public int publisherThreadCount = 20;

    @Parameterized.Parameter(2)
    public int consumerThreadsPerQueue = 5;

    @Parameterized.Parameter(3)
    public int destMemoryLimit = 50 * 1024;

    @Parameterized.Parameter(4)
    public boolean useCache = true;

    @Parameterized.Parameter(5)
    public TestSupport.PersistenceAdapterChoice persistenceAdapterChoice = TestSupport.PersistenceAdapterChoice.KahaDB;

    @Parameterized.Parameter(6)
    public boolean optimizeDispatch = false;

    @Parameterized.Parameters(name="#{0},producerThreads:{1},consumerThreads:{2},mL:{3},useCache:{4},useDefaultStore:{5},optimizedDispatch:{6}")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
//IC see: https://issues.apache.org/jira/browse/AMQ-5266
               {1000,  40,  40,   1024*1024*1,  true, TestSupport.PersistenceAdapterChoice.KahaDB, false},
               {1000,  40,  40,   1024*1024*1,  true, TestSupport.PersistenceAdapterChoice.LevelDB, false},
               {1000,  40,  40,   1024*1024*1,  true, TestSupport.PersistenceAdapterChoice.JDBC, false},
        });
    }

    public int consumerBatchSize = 25;

    @BeforeClass
    public static void derbyTestMode() throws Exception {
        System.setProperty("derby.system.durability","test");
    }

    @Before
    public void startBroker() throws Exception {
        brokerService = new BrokerService();

//IC see: https://issues.apache.org/jira/browse/AMQ-5266
//IC see: https://issues.apache.org/jira/browse/AMQ-4485
        TestSupport.setPersistenceAdapter(brokerService, persistenceAdapterChoice);
        brokerService.setDeleteAllMessagesOnStartup(true);
        brokerService.setUseJmx(false);
        brokerService.setAdvisorySupport(false);


        PolicyMap policyMap = new PolicyMap();
        PolicyEntry defaultEntry = new PolicyEntry();
        defaultEntry.setUseConsumerPriority(false); // java.lang.IllegalArgumentException: Comparison method violates its general contract!
        defaultEntry.setMaxProducersToAudit(publisherThreadCount);
        defaultEntry.setEnableAudit(true);
        defaultEntry.setUseCache(useCache);
        defaultEntry.setMaxPageSize(1000);
        defaultEntry.setOptimizedDispatch(optimizeDispatch);
        defaultEntry.setMemoryLimit(destMemoryLimit);
        defaultEntry.setExpireMessagesPeriod(0);
        policyMap.setDefaultEntry(defaultEntry);
        brokerService.setDestinationPolicy(policyMap);

        brokerService.getSystemUsage().getMemoryUsage().setLimit(64 * 1024 * 1024);
//IC see: https://issues.apache.org/jira/browse/AMQ-5266
//IC see: https://issues.apache.org/jira/browse/AMQ-4485

        TransportConnector transportConnector = brokerService.addConnector("tcp://0.0.0.0:0");
        brokerService.start();
        activemqURL = transportConnector.getPublishableConnectString();
        activemqURL += "?jms.watchTopicAdvisories=false"; // ensure all messages are queue or dlq messages
    }

    @After
    public void stopBroker() throws Exception {
        if (brokerService != null) {
            brokerService.stop();
        }
    }

    @Test
    public void test() throws Exception {

        String activemqQueues = "activemq";
        for (int i=1;i<numDests;i++) {
            activemqQueues +=",activemq"+i;
        }

        int consumerWaitForConsumption = 5 * 60 * 1000;

        ExportQueuePublisher publisher = null;
        ExportQueueConsumer consumer = null;

        LOG.info("Publisher will publish " + (publisherMessagesPerThread * publisherThreadCount) + " messages to each queue specified.");
        LOG.info("\nBuilding Publisher...");

        publisher = new ExportQueuePublisher(activemqURL, activemqQueues, publisherMessagesPerThread, publisherThreadCount);

        LOG.info("Building Consumer...");

        consumer = new ExportQueueConsumer(activemqURL, activemqQueues, consumerThreadsPerQueue, consumerBatchSize, publisherMessagesPerThread * publisherThreadCount);

        long totalStart = System.currentTimeMillis();

        LOG.info("Starting Publisher...");

        publisher.start();

        LOG.info("Starting Consumer...");

        consumer.start();

        int distinctPublishedCount = 0;


        LOG.info("Waiting For Publisher Completion...");

        publisher.waitForCompletion();

        List publishedIds = publisher.getIDs();
        distinctPublishedCount = new TreeSet(publishedIds).size();

        LOG.info("Publisher Complete. Published: " + publishedIds.size() + ", Distinct IDs Published: " + distinctPublishedCount);
        LOG.info("Publisher duration: {}", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - totalStart));


        long endWait = System.currentTimeMillis() + consumerWaitForConsumption;
        while (!consumer.completed() && System.currentTimeMillis() < endWait) {
            try {
                int secs = (int) (endWait - System.currentTimeMillis()) / 1000;
                LOG.info("Waiting For Consumer Completion. Time left: " + secs + " secs");
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }

        LOG.info("\nConsumer Complete: " + consumer.completed() +", Shutting Down.");

        LOG.info("Total duration: {}", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - totalStart));

        consumer.shutdown();

        TimeUnit.SECONDS.sleep(2);

        LOG.info("Consumer Stats:");

        for (Map.Entry<String, List<String>> entry : consumer.getIDs().entrySet()) {

            List<String> idList = entry.getValue();

            int distinctConsumed = new TreeSet<String>(idList).size();

            StringBuilder sb = new StringBuilder();
            sb.append("   Queue: " + entry.getKey() +
                    " -> Total Messages Consumed: " + idList.size() +
                    ", Distinct IDs Consumed: " + distinctConsumed);

            int diff = distinctPublishedCount - distinctConsumed;
            sb.append(" ( " + (diff > 0 ? diff : "NO") + " STUCK MESSAGES " + " ) ");
            LOG.info(sb.toString());

            assertEquals("expect to get all messages!", 0, diff);

        }

        // verify empty dlq
//IC see: https://issues.apache.org/jira/browse/AMQ-5266
//IC see: https://issues.apache.org/jira/browse/AMQ-4485
        assertEquals("No pending messages", 0l, ((RegionBroker) brokerService.getRegionBroker()).getDestinationStatistics().getMessages().getCount());
    }

    public class ExportQueuePublisher {

        private final String amqUser = ActiveMQConnection.DEFAULT_USER;
        private final String amqPassword = ActiveMQConnection.DEFAULT_PASSWORD;
        private ActiveMQConnectionFactory connectionFactory = null;
        private String activemqURL = null;
        private String activemqQueues = null;
        // Collection of distinct IDs that the publisher has published.
        // After a message is published, its UUID will be written to this list for tracking.
        // This list of IDs (or distinct count) will be used to compare to the consumed list of IDs.
        //private Set<String> ids = Collections.synchronizedSet(new TreeSet<String>());
        private List<String> ids = Collections.synchronizedList(new ArrayList<String>());
        private List<PublisherThread> threads;

        public ExportQueuePublisher(String activemqURL, String activemqQueues, int messagesPerThread, int threadCount) throws Exception {

            this.activemqURL = activemqURL;
            this.activemqQueues = activemqQueues;

            threads = new ArrayList<PublisherThread>();

            // Build the threads and tell them how many messages to publish
            for (int i = 0; i < threadCount; i++) {
                PublisherThread pt = new PublisherThread(messagesPerThread);
                threads.add(pt);
            }
        }

        public List<String> getIDs() {
            return ids;
        }

        // Kick off threads
        public void start() throws Exception {

            for (PublisherThread pt : threads) {
                pt.start();
            }
        }

        // Wait for threads to complete. They will complete once they've published all of their messages.
        public void waitForCompletion() throws Exception {

            for (PublisherThread pt : threads) {
                pt.join();
                pt.close();
            }
        }

        private Session newSession(QueueConnection queueConnection) throws Exception {
            return queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }

        private synchronized QueueConnection newQueueConnection() throws Exception {

            if (connectionFactory == null) {
                connectionFactory = new ActiveMQConnectionFactory(amqUser, amqPassword, activemqURL);
            }

            // Set the redelivery count to -1 (infinite), or else messages will start dropping
            // after the queue has had a certain number of failures (default is 6)
            RedeliveryPolicy policy = connectionFactory.getRedeliveryPolicy();
            policy.setMaximumRedeliveries(-1);

            QueueConnection amqConnection = connectionFactory.createQueueConnection();
            amqConnection.start();
            return amqConnection;
        }

        private class PublisherThread extends Thread {

            private int count;
            private QueueConnection qc;
            private Session session;
            private MessageProducer mp;

            private PublisherThread(int count) throws Exception {

                this.count = count;

                // Each Thread has its own Connection and Session, so no sync worries
                qc = newQueueConnection();
                session = newSession(qc);

                // In our code, when publishing to multiple queues,
                // we're using composite destinations like below
                Queue q = new ActiveMQQueue(activemqQueues);
                mp = session.createProducer(q);
            }

            public void run() {

                try {

                    // Loop until we've published enough messages
                    while (count-- > 0) {

                        TextMessage tm = session.createTextMessage(getMessageText());
                        String id = UUID.randomUUID().toString();
                        tm.setStringProperty("KEY", id);
                        ids.add(id);                            // keep track of the key to compare against consumer

                        mp.send(tm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Called by waitForCompletion
            public void close() {

                try {
                    mp.close();
                } catch (Exception e) {
                }

                try {
                    session.close();
                } catch (Exception e) {
                }

                try {
                    qc.close();
                } catch (Exception e) {
                }
            }
        }

    }

    String messageText;
    private String getMessageText() {

        if (messageText == null) {

            synchronized (this) {

                if (messageText == null) {

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < messageSize; i++) {
                        sb.append("X");
                    }
                    messageText = sb.toString();
                }
            }
        }

        return messageText;
    }


    public class ExportQueueConsumer {

        private final String amqUser = ActiveMQConnection.DEFAULT_USER;
        private final String amqPassword = ActiveMQConnection.DEFAULT_PASSWORD;
        private final int totalToExpect;
        private ActiveMQConnectionFactory connectionFactory = null;
        private String activemqURL = null;
        private String activemqQueues = null;
        private String[] queues = null;
        // Map of IDs that were consumed, keyed by queue name.
        // We'll compare these against what was published to know if any got stuck or dropped.
        private Map<String, List<String>> idsByQueue = new HashMap<String, List<String>>();
        private Map<String, List<ConsumerThread>> threads;

        public ExportQueueConsumer(String activemqURL, String activemqQueues, int threadsPerQueue, int batchSize, int totalToExpect) throws Exception {

            this.activemqURL = activemqURL;
            this.activemqQueues = activemqQueues;
            this.totalToExpect = totalToExpect;

            queues = this.activemqQueues.split(",");

            for (int i = 0; i < queues.length; i++) {
                queues[i] = queues[i].trim();
            }

            threads = new HashMap<String, List<ConsumerThread>>();

            // For each queue, create a list of threads and set up the list of ids
            for (String q : queues) {

                List<ConsumerThread> list = new ArrayList<ConsumerThread>();

                idsByQueue.put(q, Collections.synchronizedList(new ArrayList<String>()));

                for (int i = 0; i < threadsPerQueue; i++) {
                    list.add(new ConsumerThread(q, batchSize));
                }

                threads.put(q, list);
            }
        }

        public Map<String, List<String>> getIDs() {
            return idsByQueue;
        }

        // Start the threads
        public void start() throws Exception {

            for (List<ConsumerThread> list : threads.values()) {

                for (ConsumerThread ct : list) {

                    ct.start();
                }
            }
        }

        // Tell the threads to stop
        // Then wait for them to stop
        public void shutdown() throws Exception {

            for (List<ConsumerThread> list : threads.values()) {

                for (ConsumerThread ct : list) {

                    ct.shutdown();
                }
            }

            for (List<ConsumerThread> list : threads.values()) {

                for (ConsumerThread ct : list) {

                    ct.join();
                }
            }
        }

        private Session newSession(QueueConnection queueConnection) throws Exception {
            return queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }

        private synchronized QueueConnection newQueueConnection() throws Exception {

            if (connectionFactory == null) {
                connectionFactory = new ActiveMQConnectionFactory(amqUser, amqPassword, activemqURL);
            }

            // Set the redelivery count to -1 (infinite), or else messages will start dropping
            // after the queue has had a certain number of failures (default is 6)
            RedeliveryPolicy policy = connectionFactory.getRedeliveryPolicy();
            policy.setMaximumRedeliveries(-1);

            QueueConnection amqConnection = connectionFactory.createQueueConnection();
            amqConnection.start();
            return amqConnection;
        }

        public boolean completed() {
            for (List<ConsumerThread> list : threads.values()) {

                for (ConsumerThread ct : list) {

                    if (ct.isAlive()) {
                        LOG.info("thread for {} is still alive.", ct.qName);
                        return false;
                    }
                }
            }
            return true;
        }

        private class ConsumerThread extends Thread {

            private int batchSize;
            private QueueConnection qc;
            private Session session;
            private MessageConsumer mc;
            private List<String> idList;
            private boolean shutdown = false;
            private String qName;

            private ConsumerThread(String queueName, int batchSize) throws Exception {

                this.batchSize = batchSize;

                // Each thread has its own connection and session
                qName = queueName;
                qc = newQueueConnection();
                session = newSession(qc);
                Queue q = session.createQueue(queueName + "?consumer.prefetchSize=" + batchSize);
                mc = session.createConsumer(q);

                idList = idsByQueue.get(queueName);
            }

            public void run() {

                try {

                    int count = 0;

                    // Keep reading as long as it hasn't been told to shutdown
                    while (!shutdown) {

                        if (idList.size() >= totalToExpect) {
                            LOG.info("Got {} for q: {}", +idList.size(), qName);
                            break;
                        }
                        Message m = mc.receive(4000);

                        if (m != null) {

                            // We received a non-null message, add the ID to our list

                            idList.add(m.getStringProperty("KEY"));

                            count++;

                            // If we've reached our batch size, commit the batch and reset the count

                            if (count == batchSize) {
                                count = 0;
                            }
                        } else {

                            // We didn't receive anything this time, commit any current batch and reset the count

                            count = 0;

                            // Sleep a little before trying to read after not getting a message

                            try {
                                if (idList.size() < totalToExpect) {
                                    LOG.info("did not receive on {}, current count: {}", qName, idList.size());
                                }
                                //sleep(3000);
                            } catch (Exception e) {
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    // Once we exit, close everything
                    close();
                }
            }

            public void shutdown() {
                shutdown = true;
            }

            public void close() {

                try {
                    mc.close();
                } catch (Exception e) {
                }

                try {
                    session.close();
                } catch (Exception e) {
                }

                try {
                    qc.close();
                } catch (Exception e) {

                }
            }
        }
    }
}
