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
package org.apache.activemq.command;


/**
 * @openwire:marshaller code="110"
 * @version $Revision: 1.12 $
 */
public class MessageId implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE=CommandTypes.MESSAGE_ID;
    
    protected ProducerId producerId;    
    protected long producerSequenceId;
    protected long brokerSequenceId;
    
    transient private String key;    
    transient private int hashCode;    

    public MessageId() {
        this.producerId = new ProducerId();
    }
    
    public MessageId(ProducerInfo producerInfo, long producerSequenceId) {
        this.producerId = producerInfo.getProducerId();
        this.producerSequenceId = producerSequenceId;
    }

    public MessageId(String messageKey) {
        setMessageKey(messageKey);
    }
    
    public MessageId(String producerId, long producerSequenceId) {
        this( new ProducerId(producerId), producerSequenceId);
    }
    
    public MessageId(ProducerId producerId, long producerSequenceId) {
        this.producerId=producerId;
        this.producerSequenceId = producerSequenceId;        
    }
    
    public void setMessageKey(String messageKey) {
        key = messageKey;
        // Parse off the sequenceId
        int p = messageKey.lastIndexOf(":");
        if( p >= 0 ) {
            producerSequenceId = Long.parseLong(messageKey.substring(p+1));
            messageKey = messageKey.substring(0,p);
        }
        producerId = new ProducerId(messageKey);
    }
    

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    public boolean equals(Object o) {        
        if( this == o )
            return true;
        if( o==null || o.getClass() != getClass() )
            return false;
        
        MessageId id = (MessageId) o;
        return producerSequenceId==id.producerSequenceId && producerId.equals(id.producerId);
    }
    
    public int hashCode() {
        if( hashCode == 0 ) {
            hashCode = producerId.hashCode() ^ (int)producerSequenceId;
        }
        return hashCode;
    }
    
    public String toString() {
        if(key==null) {
            key = producerId.toString()+":"+producerSequenceId;
        }
        return key;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public ProducerId getProducerId() {
        return producerId;
    }
    public void setProducerId(ProducerId producerId) {
        this.producerId = producerId;
    }

    /**
     * @openwire:property version=1
     */
    public long getProducerSequenceId() {
        return producerSequenceId;
    }
    public void setProducerSequenceId(long producerSequenceId) {
        this.producerSequenceId = producerSequenceId;
    }

    /**
     * @openwire:property version=1
     */
    public long getBrokerSequenceId() {
        return brokerSequenceId;
    }    
    public void setBrokerSequenceId(long brokerSequenceId) {
        this.brokerSequenceId = brokerSequenceId;
    }

    public boolean isMarshallAware() {
        return false;
    }
}
