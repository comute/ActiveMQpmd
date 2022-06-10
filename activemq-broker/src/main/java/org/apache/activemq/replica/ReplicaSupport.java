package org.apache.activemq.replica;

public class ReplicaSupport {

    private ReplicaSupport() {
        // Intentionally hidden
    }

    public static final String REPLICATION_QUEUE_NAME = "ActiveMQ.Plugin.Replication.Queue";
    public static final String REPLICATION_PLUGIN_USER_NAME = "replication_plugin";

    public static final String TRANSACTION_ONE_PHASE_PROPERTY = "TRANSACTION_ONE_PHASE_PROPERTY";
    public static final String MESSAGE_IDS_PROPERTY = "MESSAGE_IDS_PROPERTY";
}