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
package org.apache.activemq.transport;

import org.apache.activemq.Service;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.Response;

import java.io.IOException;

/**
 * Represents the client side of a transport allowing messages
 * to be sent synchronously, asynchronously and consumed.
 *
 * @version $Revision: 1.5 $
 */
public interface Transport extends Service {

    /**
     * A one way asynchronous send
     */
    public void oneway(Command command) throws IOException;

    /**
     * An asynchronous request response where the Receipt will be returned
     * in the future
     */
    public FutureResponse asyncRequest(Command command) throws IOException;

    /**
     * A synchronous request response
     */
    public Response request(Command command) throws IOException;

    /**
     * Returns the current transport listener
     */
    public TransportListener getTransportListener();

    /**
     * Registers an inbound command listener
     */
    public void setTransportListener(TransportListener commandListener);
    
    public Object narrow(Class target);

}
