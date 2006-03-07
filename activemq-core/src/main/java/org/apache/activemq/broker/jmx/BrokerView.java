/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
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
package org.apache.activemq.broker.jmx;

import javax.management.ObjectName;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

public class BrokerView implements BrokerViewMBean {
    
    private final ManagedRegionBroker broker;
	private final BrokerService brokerService;

    public BrokerView(BrokerService brokerService, ManagedRegionBroker managedBroker) throws Exception {
        this.brokerService = brokerService;
		this.broker = managedBroker;
    }
    
    public String getBrokerId() {
        return broker.getBrokerId().toString();
    }
    
    public void gc() throws Exception {
    	brokerService.getBroker().gc();
    }

    public void start() throws Exception {
    	brokerService.start();
    }
    
    public void stop() throws Exception {
    	brokerService.stop();
    }
    
    public long getTotalEnqueueCount() {
        return broker.getDestinationStatistics().getEnqueues().getCount();    
    }
    public long getTotalDequeueCount() {
        return broker.getDestinationStatistics().getDequeues().getCount();
    }
    public long getTotalConsumerCount() {
        return broker.getDestinationStatistics().getConsumers().getCount();
    }
    public long getTotalMessageCount() {
        return broker.getDestinationStatistics().getMessages().getCount();
    }    
    public long getTotalMessagesCached() {
        return broker.getDestinationStatistics().getMessagesCached().getCount();
    }

    public int getMemoryPercentageUsed() {
        return brokerService.getMemoryManager().getPercentUsage();
    }
    public long getMemoryLimit() {
        return brokerService.getMemoryManager().getLimit();
    }
    public void setMemoryLimit(long limit) {
    	brokerService.getMemoryManager().setLimit(limit);
    }
    
    public void resetStatistics() {
        broker.getDestinationStatistics().reset();
    }

    public void terminateJVM(int exitCode) {
        System.exit(exitCode);
    }

    public ObjectName[] getTopics(){
        return broker.getTopics();
    }

    public ObjectName[] getQueues(){
        return broker.getQueues();
    }

    public ObjectName[] getTemporaryTopics(){
        return broker.getTemporaryTopics();
    }

    public ObjectName[] getTemporaryQueues(){
        return broker.getTemporaryQueues();
    }

    public ObjectName[] getTopicSubscribers(){
      return broker.getTemporaryTopicSubscribers();
    }

    public ObjectName[] getDurableTopicSubscribers(){
        return broker.getDurableTopicSubscribers();
    }

    public ObjectName[] getQueueSubscribers(){
       return broker.getQueueSubscribers();
    }

    public ObjectName[] getTemporaryTopicSubscribers(){
        return broker.getTemporaryTopicSubscribers();
    }

    public ObjectName[] getTemporaryQueueSubscribers(){
        return broker.getTemporaryQueueSubscribers();
    }
    
    public ObjectName[] getInactiveDurableTopicSubscribers(){
        return broker.getInactiveDurableTopicSubscribers();
    }

    public void addTopic(String name) throws Exception {
        broker.addDestination(getConnectionContext(broker.getContextBroker()), new ActiveMQTopic(name));
    }

    public void addQueue(String name) throws Exception {
        broker.addDestination(getConnectionContext(broker.getContextBroker()), new ActiveMQQueue(name));
    }

    public void removeTopic(String name) throws Exception {
        broker.removeDestination(getConnectionContext(broker.getContextBroker()), new ActiveMQTopic(name), 1000);
    }

    public void removeQueue(String name) throws Exception {
        broker.removeDestination(getConnectionContext(broker.getContextBroker()), new ActiveMQQueue(name), 1000);
    }
    
    static public ConnectionContext getConnectionContext(Broker broker) {
        ConnectionContext context = new ConnectionContext();
        context.setBroker(broker);
        return context;
    }

}
