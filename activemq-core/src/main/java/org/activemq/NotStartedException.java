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
package org.activemq;

import javax.jms.IllegalStateException;

/**
 * An exception thrown when an operation is invoked on a service
 * which has not yet been started.
 *
 * @version $Revision: 1.2 $
 */
public class NotStartedException extends IllegalStateException {

    private static final long serialVersionUID = -4907909323529887659L;

    public NotStartedException() {
        super("IllegalState: This service has not yet been started", "AMQ-1003");
    }
}
