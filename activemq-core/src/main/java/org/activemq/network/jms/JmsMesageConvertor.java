/**
 * <a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>
 * 
 * Copyright 2005 (C) Simula Labs Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 */
package org.activemq.network.jms;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Converts Message from one JMS to another
 * 
 * @version $Revision: 1.1.1.1 $
 */
public interface JmsMesageConvertor {
    
    /**
     * Convert a foreign JMS Message to a native ActiveMQ Message
     * @param message
     * @return the converted message
     * @throws JMSException
     */
    public Message convert(Message message) throws JMSException;
    
   
}