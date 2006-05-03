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
package org.apache.activemq.systest;

import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * A factory to create destinations which works in any version of any JMS provider
 * 
 * @version $Revision: 1.1 $
 */
public interface DestinationFactory {
    
    public Destination createDestination(String physicalName, int destinationType) throws JMSException;
    
    public static final int TOPIC = 1;
    public static final int QUEUE = 2;
    public static final int TEMPORARY_TOPIC = 3;
    public static final int TEMPORARY_QUEUE = 4;
}
