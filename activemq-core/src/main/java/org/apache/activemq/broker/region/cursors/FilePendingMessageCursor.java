/**
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.apache.activemq.broker.region.cursors;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.Message;
import org.apache.activemq.kaha.ListContainer;
import org.apache.activemq.kaha.Store;
import org.apache.activemq.memory.UsageListener;
import org.apache.activemq.memory.UsageManager;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.store.kahadaptor.CommandMarshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * perist pending messages pending message (messages awaiting disptach to a consumer) cursor
 * 
 * @version $Revision$
 */
public class FilePendingMessageCursor extends AbstractPendingMessageCursor implements UsageListener{

    static private final Log log=LogFactory.getLog(FilePendingMessageCursor.class);
    private Store store;
    private String name;
    private LinkedList memoryList=new LinkedList();
    private ListContainer diskList;
    private Iterator iter=null;
    private Destination regionDestination;
    private ReentrantLock iterLock=new ReentrantLock();

    /**
     * @param name
     * @param store
     */
    public FilePendingMessageCursor(String name,Store store){
        this.name=name;
        this.store=store;
    }

    /**
     * @return true if there are no pending messages
     */
    public synchronized boolean isEmpty(){
        return memoryList.isEmpty()&&isDiskListEmpty();
    }

    /**
     * reset the cursor
     * 
     */
    public void reset(){
        try{
            iterLock.lockInterruptibly();
            iter=isDiskListEmpty()?memoryList.iterator():getDiskList().listIterator();
        }catch(InterruptedException e){
            log.warn("Failed to get lock ",e);
        }
    }

    public void release(){
        iterLock.unlock();
    }

    public synchronized void destroy(){
        for(Iterator i=memoryList.iterator();i.hasNext();){
            Message node=(Message)i.next();
            node.decrementReferenceCount();
        }
        memoryList.clear();
        if(!isDiskListEmpty()){
            getDiskList().clear();
        }
    }

    public synchronized LinkedList pageInList(int maxItems){
        LinkedList result=new LinkedList();
        int count=0;
        for(Iterator i=memoryList.iterator();i.hasNext()&&count<maxItems;){
            result.add(i.next());
            count++;
        }
        if(count<maxItems&&!isDiskListEmpty()){
            for(Iterator i=getDiskList().iterator();i.hasNext()&&count<maxItems;){
                Message message=(Message)i.next();
                message.setRegionDestination(regionDestination);
                message.incrementReferenceCount();
                result.add(message);
                count++;
            }
        }
        return result;
    }

    /**
     * add message to await dispatch
     * 
     * @param node
     */
    public synchronized void addMessageLast(MessageReference node){
        try{
            regionDestination=node.getMessage().getRegionDestination();
            if(isSpaceInMemoryList()){
                memoryList.add(node);
            }else{
                flushToDisk();
                node.decrementReferenceCount();
                getDiskList().addLast(node);
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * add message to await dispatch
     * 
     * @param node
     */
    public synchronized void addMessageFirst(MessageReference node){
        try{
            regionDestination=node.getMessage().getRegionDestination();
            if(isSpaceInMemoryList()){
                memoryList.addFirst(node);
            }else{
                flushToDisk();
                node.decrementReferenceCount();
                getDiskList().addFirst(node);
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * @return true if there pending messages to dispatch
     */
    public synchronized boolean hasNext(){
        return iter.hasNext();
    }

    /**
     * @return the next pending message
     */
    public synchronized MessageReference next(){
        Message message=(Message)iter.next();
        if(!isDiskListEmpty()){
            // got from disk
            message.setRegionDestination(regionDestination);
            message.incrementReferenceCount();
        }
        return message;
    }

    /**
     * remove the message at the cursor position
     * 
     */
    public synchronized void remove(){
        iter.remove();
    }

    /**
     * @param node
     * @see org.apache.activemq.broker.region.cursors.AbstractPendingMessageCursor#remove(org.apache.activemq.broker.region.MessageReference)
     */
    public synchronized void remove(MessageReference node){
        memoryList.remove(node);
        if(!isDiskListEmpty()){
            getDiskList().remove(node);
        }
    }

    /**
     * @return the number of pending messages
     */
    public synchronized int size(){
        return memoryList.size()+(isDiskListEmpty()?0:getDiskList().size());
    }

    /**
     * clear all pending messages
     * 
     */
    public synchronized void clear(){
        memoryList.clear();
        if(!isDiskListEmpty()){
            getDiskList().clear();
        }
    }

    public synchronized boolean isFull(){
        // we always have space - as we can persist to disk
        return false;
    }

    public void setUsageManager(UsageManager usageManager){
        super.setUsageManager(usageManager);
        usageManager.addUsageListener(this);
    }

    public void onMemoryUseChanged(UsageManager memoryManager,int oldPercentUsage,int newPercentUsage){
        if(newPercentUsage>=100){
            try{
                if(iterLock.tryLock(500,TimeUnit.MILLISECONDS)){
                    flushToDisk();
                    iterLock.unlock();
                }
            }catch(InterruptedException e){
                log.warn("caught an exception aquiring lock",e);
            }
        }
    }

    protected boolean isSpaceInMemoryList(){
        return hasSpace()&&isDiskListEmpty();
    }

    protected synchronized void flushToDisk(){
        for(Iterator i=memoryList.iterator();i.hasNext();){
            MessageReference node=(MessageReference)i.next();
            node.decrementReferenceCount();
            getDiskList().addLast(node);
        }
        memoryList.clear();
    }

    protected boolean isDiskListEmpty(){
        return diskList==null||diskList.isEmpty();
    }

    protected ListContainer getDiskList(){
        if(diskList==null){
            try{
                diskList=store.getListContainer(name);
                diskList.setMarshaller(new CommandMarshaller(new OpenWireFormat()));
                diskList.setMaximumCacheSize(0);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }
        return diskList;
    }
}
