package org.apache.activemq.broker.replica;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.AutoFailTestSupport;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.replica.ReplicaPlugin;
import org.apache.activemq.replica.ReplicaRole;

import javax.jms.ConnectionFactory;
import javax.transaction.xa.Xid;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ReplicaPluginTestSupport extends AutoFailTestSupport {

    protected static final int LONG_TIMEOUT = 10000;
    protected static final int SHORT_TIMEOUT = 1000;

    private static final String FIRST_KAHADB_DIRECTORY = "target/activemq-data/first/";
    private static final String SECOND_KAHADB_DIRECTORY = "target/activemq-data/second/";

    protected String firstBindAddress = "vm://firstBroker";
    protected String firstReplicaBindAddress = "tcp://localhost:61610";
    protected String secondBindAddress = "vm://secondBroker";

    protected BrokerService firstBroker;
    protected BrokerService secondBroker;

    protected boolean useTopic;

    protected ConnectionFactory firstBrokerConnectionFactory;
    protected ConnectionFactory secondBrokerConnectionFactory;

    protected ActiveMQXAConnectionFactory firstBrokerXAConnectionFactory;
    protected ActiveMQXAConnectionFactory secondBrokerXAConnectionFactory;

    protected ActiveMQDestination destination;

    private static long txGenerator = 67;

    @Override
    protected void setUp() throws Exception {
        if (firstBroker == null) {
            firstBroker = createFirstBroker();
        }
        if (secondBroker == null) {
            secondBroker = createSecondBroker();
        }

        startFirstBroker();
        startSecondBroker();

        firstBrokerConnectionFactory = new ActiveMQConnectionFactory(firstBindAddress);
        secondBrokerConnectionFactory = new ActiveMQConnectionFactory(secondBindAddress);

        firstBrokerXAConnectionFactory = new ActiveMQXAConnectionFactory(firstBindAddress);
        secondBrokerXAConnectionFactory = new ActiveMQXAConnectionFactory(secondBindAddress);

        destination = createDestination();
    }

    @Override
    protected void tearDown() throws Exception {
        if (firstBroker != null) {
            try {
                firstBroker.stop();
            } catch (Exception e) {
            }
        }
        if (secondBroker != null) {
            try {
                secondBroker.stop();
            } catch (Exception e) {
            }
        }
    }

    protected BrokerService createFirstBroker() throws Exception {
        BrokerService answer = new BrokerService();
        answer.setUseJmx(true);
        answer.setPersistent(false);
        answer.getManagementContext().setCreateConnector(false);
        answer.addConnector(firstBindAddress);
        answer.setDataDirectory(FIRST_KAHADB_DIRECTORY);
        answer.setBrokerName("firstBroker");

        ReplicaPlugin replicaPlugin = new ReplicaPlugin();
        replicaPlugin.setRole(ReplicaRole.source);
        replicaPlugin.setTransportConnectorUri(firstReplicaBindAddress);

        answer.setPlugins(new BrokerPlugin[]{replicaPlugin});
        return answer;
    }

    protected BrokerService createSecondBroker() throws Exception {
        BrokerService answer = new BrokerService();
        answer.setUseJmx(true);
        answer.setPersistent(false);
        answer.getManagementContext().setCreateConnector(false);
        answer.addConnector(secondBindAddress);
        answer.setDataDirectory(SECOND_KAHADB_DIRECTORY);
        answer.setBrokerName("secondBroker");

        ReplicaPlugin replicaPlugin = new ReplicaPlugin();
        replicaPlugin.setRole(ReplicaRole.replica);
        replicaPlugin.setOtherBrokerUri(firstReplicaBindAddress);

        answer.setPlugins(new BrokerPlugin[]{replicaPlugin});
        return answer;
    }

    protected void startFirstBroker() throws Exception {
        firstBroker.start();
    }

    protected void startSecondBroker() throws Exception {
        secondBroker.start();
    }

    protected ActiveMQDestination createDestination() {
        return createDestination(getDestinationString());
    }

    protected ActiveMQDestination createDestination(String subject) {
        if (useTopic) {
            return new ActiveMQTopic(subject);
        } else {
            return new ActiveMQQueue(subject);
        }
    }

    protected String getDestinationString() {
        return getClass().getName() + "." + getName();
    }

    protected Xid createXid() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeLong(++txGenerator);
        os.close();
        final byte[] bs = baos.toByteArray();

        return new Xid() {

            public int getFormatId() {
                return 86;
            }


            public byte[] getGlobalTransactionId() {
                return bs;
            }


            public byte[] getBranchQualifier() {
                return bs;
            }
        };
    }
}