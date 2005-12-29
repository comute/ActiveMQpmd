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

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * @openwire:marshaller
 * @version $Revision: 1.5 $
 */
public class ActiveMQQueue extends ActiveMQDestination implements Queue {

    private static final long serialVersionUID = -3885260014960795889L;
    public static final byte DATA_STRUCTURE_TYPE=CommandTypes.ACTIVEMQ_QUEUE;

    public ActiveMQQueue() {
    }

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    public ActiveMQQueue(String name) {
        super(name);
    }

    public boolean isQueue() {
        return true;
    }

    public String getQueueName() throws JMSException {
        return getPhysicalName();
    }

    public byte getDestinationType() {
        return QUEUE_TYPE;
    }

    protected String getQualifiedPrefix() {
        return QUEUE_QUALIFIED_PREFIX;
    }

}
