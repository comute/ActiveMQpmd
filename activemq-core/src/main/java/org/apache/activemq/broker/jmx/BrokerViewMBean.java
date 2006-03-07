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
import org.apache.activemq.Service;

public interface BrokerViewMBean extends Service {

	/**
	 * @return The unique id of the broker.
	 */
    public abstract String getBrokerId();

    /**
     * The Broker will fush it's caches so that the garbage
     * collector can recalaim more memory.
     * 
     * @throws Exception
     */
    public void gc() throws Exception;
    
    
    public void resetStatistics();
    
    public long getTotalEnqueueCount();
    public long getTotalDequeueCount();
    public long getTotalConsumerCount();
    public long getTotalMessageCount();
    
    public int getMemoryPercentageUsed();
    public long getMemoryLimit();
    public void setMemoryLimit(long limit);

    /**
     * Shuts down the JVM.
     * @param exitCode the exit code that will be reported by the JVM process when it exits.
     */
    public void terminateJVM(int exitCode);
    
    /**
     * Stop the broker and all it's comonents.
     */
    public void stop() throws Exception;
    
    public ObjectName[] getTopics();
    public ObjectName[] getQueues();
    public ObjectName[] getTemporaryTopics();
    public ObjectName[] getTemporaryQueues();
    
    public ObjectName[] getTopicSubscribers();
    public ObjectName[] getDurableTopicSubscribers();
    public ObjectName[] getInactiveDurableTopicSubscribers();
    public ObjectName[] getQueueSubscribers();
    public ObjectName[] getTemporaryTopicSubscribers();
    public ObjectName[] getTemporaryQueueSubscribers();
    
    /** 
     * Adds a Topic destination to the broker.
     * @param name The name of the Topic
     * @throws Exception
     */
    public void addTopic(String name) throws Exception;

    /**
     * Adds a Queue destination to the broker.
     * @param name The name of the Queue
     * @throws Exception
     */
    public void addQueue(String name) throws Exception;

    /** 
     * Removes a Topic destination from the broker.
     * @param name The name of the Topic
     * @throws Exception
     */
    public void removeTopic(String name) throws Exception;

    /**
     * Removes a Queue destination from the broker.
     * @param name The name of the Queue
     * @throws Exception
     */
    public void removeQueue(String name) throws Exception;
    
}