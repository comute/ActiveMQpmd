/**
 * 
 * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com
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
package org.activemq.memory.list;

import org.activemq.broker.region.MessageReference;
import org.activemq.broker.region.Subscription;

import java.util.List;

/**
 * A container of messages which is used to store messages and then 
 * replay them later for a given subscription.
 * 
 * @version $Revision: 1.1 $
 */
public interface MessageList {

    void add(MessageReference node);

    /**
     * Returns the current list of MessageReference objects for the given subscription
     */
    List getMessages(Subscription sub);

    void clear();
}
