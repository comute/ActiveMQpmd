package org.apache.activemq.replica;

public enum ReplicaEventType {
    DESTINATION_UPSERT,
    DESTINATION_DELETE,
    MESSAGE_SEND,
    MESSAGES_DROPPED,
    TRANSACTION_BEGIN,
    TRANSACTION_PREPARE,
    TRANSACTION_ROLLBACK,
    TRANSACTION_COMMIT,
    TRANSACTION_FORGET;

    static final String EVENT_TYPE_PROPERTY = "ActiveMQ.Replication.EventType";
}