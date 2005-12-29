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
package org.apache.activemq.filter;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.InitializingBean;

/**
 * A base class for entry objects used to construct a destination based policy
 * map.
 * 
 * @version $Revision: 1.1 $
 */
public abstract class DestinationMapEntry implements InitializingBean {

    private ActiveMQDestination destination;

    /**
     * A helper method to set the destination from a configuration file
     */
    public void setQueue(String name) {
        setDestination(new ActiveMQQueue(name));
    }

    /**
     * A helper method to set the destination from a configuration file
     */
    public void setTopic(String name) {
        setDestination(new ActiveMQTopic(name));
    }

    public ActiveMQDestination getDestination() {
        return destination;
    }

    public void setDestination(ActiveMQDestination destination) {
        this.destination = destination;
    }

    public void afterPropertiesSet() throws Exception {
        if (destination == null) {
            throw new IllegalArgumentException("You must specify the 'destination' property");
        }
    }

    public Object getValue() {
        return this;
    }
}
