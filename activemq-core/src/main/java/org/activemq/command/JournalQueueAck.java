/**
* <a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>
*
* Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com
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
*
**/
package org.activemq.command;

/**
 * 
 * @openwire:marshaller
 * @version $Revision$
 */
public class JournalQueueAck implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE=CommandTypes.JOURNAL_REMOVE;

    ActiveMQDestination destination;
    MessageAck messageAck;
    
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @openwire:property version=1
     */
    public ActiveMQDestination getDestination() {
        return destination;
    }
    

    public void setDestination(ActiveMQDestination destination) {
        this.destination = destination;
    }
    

    /**
     * @openwire:property version=1
     */
    public MessageAck getMessageAck() {
        return messageAck;
    }
    

    public void setMessageAck(MessageAck messageAck) {
        this.messageAck = messageAck;
    }
    
    public boolean isMarshallAware() {
        return false;
    }
}
