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

package org.apache.activemq.broker.region;

import org.apache.activemq.management.CountStatisticImpl;
import org.apache.activemq.management.PollCountStatisticImpl;
import org.apache.activemq.management.StatsImpl;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.activemq.management.*;

/**
 * The J2EE Statistics for the a Destination.
 *
 *
 */
public class DestinationStatistics extends StatsImpl {

    protected CountStatisticImpl enqueues;
    protected CountStatisticImpl dequeues;
    protected CountStatisticImpl forwards;
    protected CountStatisticImpl consumers;
    protected CountStatisticImpl producers;
    protected CountStatisticImpl messages;
    protected PollCountStatisticImpl messagesCached;
    protected CountStatisticImpl dispatched;
    protected CountStatisticImpl duplicateFromStore;
    protected CountStatisticImpl inflight;
    protected CountStatisticImpl expired;
    protected TimeStatisticImpl processTime;
    protected CountStatisticImpl blockedSends;
    protected TimeStatisticImpl blockedTime;
    protected SizeStatisticImpl messageSize;
    protected CountStatisticImpl maxUncommittedExceededCount;

    // [AMQ-9437] Advanced Network Statistics are optionally enabled
    protected final AtomicBoolean advancedNetworkStatisticsEnabled = new AtomicBoolean(false);
    protected CountStatisticImpl networkEnqueues;
    protected CountStatisticImpl networkDequeues;

    // [AMQ-8463] Advanced Message Statistics are optionally enabled
    protected final AtomicBoolean advancedMessageStatisticsEnabled = new AtomicBoolean(false);
    protected UnsampledStatisticImpl<Long> enqueuedMessageBrokerInTime;
    protected UnsampledStatisticImpl<String> enqueuedMessageClientID;
    protected UnsampledStatisticImpl<String> enqueuedMessageID;
    protected UnsampledStatisticImpl<Long> enqueuedMessageTimestamp;
    protected UnsampledStatisticImpl<Long> dequeuedMessageBrokerInTime;
    protected UnsampledStatisticImpl<Long> dequeuedMessageBrokerOutTime;
    protected UnsampledStatisticImpl<String> dequeuedMessageClientID;
    protected UnsampledStatisticImpl<String> dequeuedMessageID;
    protected UnsampledStatisticImpl<Long> dequeuedMessageTimestamp;

    public DestinationStatistics() {

        enqueues = new CountStatisticImpl("enqueues", "The number of messages that have been sent to the destination");
        dispatched = new CountStatisticImpl("dispatched", "The number of messages that have been dispatched from the destination");
        dequeues = new CountStatisticImpl("dequeues", "The number of messages that have been acknowledged from the destination");
        duplicateFromStore = new CountStatisticImpl("duplicateFromStore", "The number of duplicate messages that have been paged-in from the store for this destination");
        forwards = new CountStatisticImpl("forwards", "The number of messages that have been forwarded to a networked broker from the destination");
        inflight = new CountStatisticImpl("inflight", "The number of messages dispatched but awaiting acknowledgement");
        expired = new CountStatisticImpl("expired", "The number of messages that have expired");

        consumers = new CountStatisticImpl("consumers", "The number of consumers that that are subscribing to messages from the destination");
        consumers.setDoReset(false);
        producers = new CountStatisticImpl("producers", "The number of producers that that are publishing messages to the destination");
        producers.setDoReset(false);
        messages = new CountStatisticImpl("messages", "The number of messages that that are being held by the destination");
        messages.setDoReset(false);
        messagesCached = new PollCountStatisticImpl("messagesCached", "The number of messages that are held in the destination's memory cache");
        processTime = new TimeStatisticImpl("processTime", "information around length of time messages are held by a destination");
        blockedSends = new CountStatisticImpl("blockedSends", "number of messages that have to wait for flow control");
        blockedTime = new TimeStatisticImpl("blockedTime","amount of time messages are blocked for flow control");
        messageSize = new SizeStatisticImpl("messageSize","Size of messages passing through the destination");
        maxUncommittedExceededCount = new CountStatisticImpl("maxUncommittedExceededCount", "number of times maxUncommittedCount has been exceeded");

        addStatistics(Set.of(enqueues, dispatched, dequeues, duplicateFromStore, inflight, expired, consumers,
                producers, messages, messagesCached, processTime, blockedSends, blockedTime, messageSize, maxUncommittedExceededCount));

        if(advancedNetworkStatisticsEnabled.get()) {
            enableAdvancedNetworkStatistics();
        }

        if(advancedMessageStatisticsEnabled.get()) {
            enableAdvancedMessageStatistics();
        }
    }

    public CountStatisticImpl getEnqueues() {
        return enqueues;
    }

    public CountStatisticImpl getDequeues() {
        return dequeues;
    }

    public CountStatisticImpl getForwards() {
        return forwards;
    }

    public CountStatisticImpl getInflight() {
        return inflight;
    }

    public CountStatisticImpl getExpired() {
        return expired;
    }

    public CountStatisticImpl getConsumers() {
        return consumers;
    }

    public CountStatisticImpl getProducers() {
        return producers;
    }

    public PollCountStatisticImpl getMessagesCached() {
        return messagesCached;
    }

    public CountStatisticImpl getMessages() {
        return messages;
    }

    public void setMessagesCached(PollCountStatisticImpl messagesCached) {
        this.messagesCached = messagesCached;
    }

    public CountStatisticImpl getDispatched() {
        return dispatched;
    }

    public CountStatisticImpl getDuplicateFromStore() {
        return duplicateFromStore;
    }

    public TimeStatisticImpl getProcessTime() {
        return this.processTime;
    }

    public CountStatisticImpl getBlockedSends(){
        return this.blockedSends;
    }
    public TimeStatisticImpl getBlockedTime(){
        return this.blockedTime;
    }
    public SizeStatisticImpl getMessageSize(){
        return this.messageSize;
    }

    public CountStatisticImpl getMaxUncommittedExceededCount(){
        return this.maxUncommittedExceededCount;
    }

    // FIXME: Ugh.. all these would need to be behind a lock as well
    public CountStatisticImpl getNetworkEnqueues() {
        return networkEnqueues;
    }

    public CountStatisticImpl getNetworkDequeues() {
        return networkDequeues;
    }

    public UnsampledStatistic<Long> getEnqueuedMessageBrokerInTime() {
        return enqueuedMessageBrokerInTime;
    }

    public UnsampledStatistic<String> getEnqueuedMessageClientID() {
        return enqueuedMessageClientID;
    }

    public UnsampledStatistic<String> getEnqueuedMessageID() {
        return enqueuedMessageID;
    }

    public UnsampledStatistic<Long> getEnqueuedMessageTimestamp() {
        return enqueuedMessageTimestamp;
    }

    public UnsampledStatistic<Long> getDequeuedMessageBrokerInTime() {
        return dequeuedMessageBrokerInTime;
    }

    public UnsampledStatistic<Long> getDequeuedMessageBrokerOutTime() {
        return dequeuedMessageBrokerOutTime;
    }

    public UnsampledStatistic<String> getDequeuedMessageClientID() {
        return dequeuedMessageClientID;
    }

    public UnsampledStatistic<String> getDequeuedMessageID() {
        return dequeuedMessageID;
    }

    public UnsampledStatistic<Long> getDequeuedMessageTimestamp() {
        return dequeuedMessageTimestamp;
    }

    public void reset() {
        if (this.isDoReset()) {
            super.reset();
            enqueues.reset();
            dequeues.reset();
            forwards.reset();
            dispatched.reset();
            duplicateFromStore.reset();
            inflight.reset();
            expired.reset();
            blockedSends.reset();
            blockedTime.reset();
            messageSize.reset();
            maxUncommittedExceededCount.reset();

            if(advancedMessageStatisticsEnabled.get()) {
                enqueuedMessageBrokerInTime.reset();
                enqueuedMessageClientID.reset();
                enqueuedMessageID.reset();
                enqueuedMessageTimestamp.reset();
                dequeuedMessageBrokerInTime.reset();
                dequeuedMessageBrokerOutTime.reset();
                dequeuedMessageClientID.reset();
                dequeuedMessageID.reset();
                dequeuedMessageTimestamp.reset();
            }

            if(advancedNetworkStatisticsEnabled.get()) {
                networkEnqueues.reset();
                networkDequeues.reset();
            }
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        enqueues.setEnabled(enabled);
        dispatched.setEnabled(enabled);
        dequeues.setEnabled(enabled);
        duplicateFromStore.setEnabled(enabled);
        forwards.setEnabled(enabled);
        inflight.setEnabled(enabled);
        expired.setEnabled(true);
        consumers.setEnabled(enabled);
        producers.setEnabled(enabled);
        messages.setEnabled(enabled);
        messagesCached.setEnabled(enabled);
        processTime.setEnabled(enabled);
        blockedSends.setEnabled(enabled);
        blockedTime.setEnabled(enabled);
        messageSize.setEnabled(enabled);
        maxUncommittedExceededCount.setEnabled(enabled);

        // [AMQ-9437] Advanced Network Statistics
        networkEnqueues.setEnabled(enabled);
        networkDequeues.setEnabled(enabled);

        // [AMQ-9437] Advanced Message Statistics
        enqueuedMessageBrokerInTime.setEnabled(enabled);
        enqueuedMessageClientID.setEnabled(enabled);
        enqueuedMessageID.setEnabled(enabled);
        enqueuedMessageTimestamp.setEnabled(enabled);
        dequeuedMessageBrokerInTime.setEnabled(enabled);
        dequeuedMessageBrokerOutTime.setEnabled(enabled);
        dequeuedMessageClientID.setEnabled(enabled);
        dequeuedMessageID.setEnabled(enabled);
        dequeuedMessageTimestamp.setEnabled(enabled);

    }

    public void setParent(DestinationStatistics parent) {
        if (parent != null) {
            enqueues.setParent(parent.enqueues);
            dispatched.setParent(parent.dispatched);
            dequeues.setParent(parent.dequeues);
            duplicateFromStore.setParent(parent.duplicateFromStore);
            forwards.setParent(parent.forwards);
            inflight.setParent(parent.inflight);
            expired.setParent(parent.expired);
            consumers.setParent(parent.consumers);
            producers.setParent(parent.producers);
            messagesCached.setParent(parent.messagesCached);
            messages.setParent(parent.messages);
            processTime.setParent(parent.processTime);
            blockedSends.setParent(parent.blockedSends);
            blockedTime.setParent(parent.blockedTime);
            messageSize.setParent(parent.messageSize);
            maxUncommittedExceededCount.setParent(parent.maxUncommittedExceededCount);
            networkEnqueues.setParent(parent.networkEnqueues);
            networkDequeues.setParent(parent.networkDequeues);
            // [AMQ-9437] Advanced Message Statistics do not parent.
        } else {
            enqueues.setParent(null);
            dispatched.setParent(null);
            dequeues.setParent(null);
            duplicateFromStore.setParent(null);
            forwards.setParent(null);
            inflight.setParent(null);
            expired.setParent(null);
            consumers.setParent(null);
            producers.setParent(null);
            messagesCached.setParent(null);
            messages.setParent(null);
            processTime.setParent(null);
            blockedSends.setParent(null);
            blockedTime.setParent(null);
            messageSize.setParent(null);
            maxUncommittedExceededCount.setParent(null);
            networkEnqueues.setParent(null);
            networkDequeues.setParent(null);
            // [AMQ-9437] Advanced Message Statistics do not parent.
        }
    }

    // FIXME: This needs to use a reentrant lock instead 
    public synchronized void setAdvancedMessageStatisticsEnabled(boolean advancedMessageStatisticsEnabled) {
        synchronized(this.advancedMessageStatisticsEnabled) {
            if(!this.advancedMessageStatisticsEnabled.getAndSet(advancedMessageStatisticsEnabled)) {
                enableAdvancedMessageStatistics();
            } else {
                disableAdvancedMessageStatistics();
            }
        }
    }

    public synchronized void setAdvancedNetworkStatisticsEnabled(boolean advancedNetworkStatisticsEnabled) {
        synchronized(this.advancedNetworkStatisticsEnabled) {
            if(this.advancedNetworkStatisticsEnabled.getAndSet(advancedNetworkStatisticsEnabled)) {
                enableAdvancedNetworkStatistics();
            } else {
                disableAdvancedNetworkStatistics();
            }
        }
    }

    private void enableAdvancedMessageStatistics() {
        enqueuedMessageBrokerInTime = new UnsampledStatisticImpl<>("enqueuedMessageBrokerInTime", "ms", "Broker in time (ms) of last enqueued message to the destination", Long.valueOf(0l));
        enqueuedMessageClientID = new UnsampledStatisticImpl<>("enqueuedMessageClientID", "id", "ClientID of last enqueued message to the destination", null);
        enqueuedMessageID = new UnsampledStatisticImpl<>("enqueuedMessageID", "id", "MessageID of last enqueued message to the destination", null);
        enqueuedMessageTimestamp = new UnsampledStatisticImpl<>("enqueuedMessageTimestamp", "ms", "Message timestamp of last enqueued message to the destination", Long.valueOf(0l));

        dequeuedMessageBrokerInTime = new UnsampledStatisticImpl<>("dequeuedMessageBrokerInTime", "ms", "Broker in time (ms) of last dequeued message to the destination", Long.valueOf(0l));
        dequeuedMessageBrokerOutTime = new UnsampledStatisticImpl<>("dequeuedMessageBrokerOutTime", "ms", "Broker out time (ms) of last dequeued message to the destination", Long.valueOf(0l));
        dequeuedMessageClientID = new UnsampledStatisticImpl<>("dequeuedMessageClientID", "id", "ClientID of last dequeued message to the destination", null);
        dequeuedMessageID = new UnsampledStatisticImpl<>("dequeuedMessageID", "id", "MessageID of last dequeued message to the destination", null);
        dequeuedMessageTimestamp = new UnsampledStatisticImpl<>("dequeuedMessageTimestamp", "ms", "Message timestamp of last dequeued message to the destination", Long.valueOf(0l));

        addStatistics(Set.of(enqueuedMessageBrokerInTime, enqueuedMessageClientID, enqueuedMessageID, enqueuedMessageTimestamp,
                dequeuedMessageBrokerInTime, dequeuedMessageBrokerOutTime, dequeuedMessageClientID, dequeuedMessageID, dequeuedMessageTimestamp));
    }

    private void disableAdvancedMessageStatistics() {
        removeStatistics(Set.of(enqueuedMessageBrokerInTime, enqueuedMessageClientID, enqueuedMessageID, enqueuedMessageTimestamp,
                dequeuedMessageBrokerInTime, dequeuedMessageBrokerOutTime, dequeuedMessageClientID, dequeuedMessageID, dequeuedMessageTimestamp));

        enqueuedMessageBrokerInTime = null;
        enqueuedMessageClientID = null;
        enqueuedMessageID = null;
        enqueuedMessageTimestamp = null;

        dequeuedMessageBrokerInTime = null;
        dequeuedMessageBrokerOutTime = null;
        dequeuedMessageClientID = null;
        dequeuedMessageID = null;
        dequeuedMessageTimestamp = null;
    }

    private void enableAdvancedNetworkStatistics() {
        networkEnqueues = new CountStatisticImpl("networkEnqueues", "The number of messages that have been sent to the destination via network connection");
        networkDequeues = new CountStatisticImpl("networkDequeues", "The number of messages that have been acknowledged from the destination via network connection");
        addStatistics(Set.of(networkEnqueues, networkDequeues));
    }

    private void disableAdvancedNetworkStatistics() {
        removeStatistics(Set.of(networkEnqueues, networkDequeues));
        networkEnqueues = null;
        networkDequeues = null;
    }
}
