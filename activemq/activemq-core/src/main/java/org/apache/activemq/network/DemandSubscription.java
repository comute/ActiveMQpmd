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
package org.apache.activemq.network;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArraySet;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConsumerInfo;

import java.util.Set;

/**
 * Represents a network bridge interface
 * 
 * @version $Revision: 1.1 $
 */
public class DemandSubscription{
    private ConsumerInfo remoteInfo;
    private ConsumerInfo localInfo;
    private Set remoteSubsIds = new CopyOnWriteArraySet();
    private AtomicInteger dispatched = new AtomicInteger(0);

    DemandSubscription(ConsumerInfo info){
        remoteInfo=info;
        localInfo=info.copy();
        localInfo.setBrokerPath(info.getBrokerPath());
        remoteSubsIds.add(info.getConsumerId());
    } 

    /**
     * Increment the consumers associated with this subscription
     * @param id
     * @return true if added
     */
    public boolean add(ConsumerId id){
        return remoteSubsIds.add(id);
    }
    
    /**
     * Increment the consumers associated with this subscription
     * @param id
     * @return true if added
     */
    public boolean remove(ConsumerId id){
        return remoteSubsIds.remove(id);
    }
    
    /**
     * @return true if there are no interested consumers
     */
    public boolean isEmpty(){
        return remoteSubsIds.isEmpty();
    }
    
    
    /**
     * @return Returns the dispatched.
     */
    public int getDispatched(){
        return dispatched.get();
    }

    /**
     * @param dispatched The dispatched to set.
     */
    public void setDispatched(int dispatched){
        this.dispatched.set(dispatched);
    }
    
    /**
     * @return dispatched count after incremented
     */
    public int incrementDispatched(){
        return dispatched.incrementAndGet();
    }

    /**
     * @return Returns the localInfo.
     */
    public ConsumerInfo getLocalInfo(){
        return localInfo;
    }

    /**
     * @param localInfo The localInfo to set.
     */
    public void setLocalInfo(ConsumerInfo localInfo){
        this.localInfo=localInfo;
    }

    /**
     * @return Returns the remoteInfo.
     */
    public ConsumerInfo getRemoteInfo(){
        return remoteInfo;
    }

    /**
     * @param remoteInfo The remoteInfo to set.
     */
    public void setRemoteInfo(ConsumerInfo remoteInfo){
        this.remoteInfo=remoteInfo;
    }
    
}
