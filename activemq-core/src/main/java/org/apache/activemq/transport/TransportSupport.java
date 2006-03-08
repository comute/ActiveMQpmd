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

import org.apache.activemq.command.Command;
import org.apache.activemq.command.Response;
import org.apache.activemq.util.ServiceSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * A useful base class for transport implementations.
 * 
 * @version $Revision: 1.1 $
 */
public abstract class TransportSupport extends ServiceSupport implements Transport {
    private static final Log log = LogFactory.getLog(TransportSupport.class);

    private TransportListener transportListener;

    public TransportListener getTransportListener() {
        return transportListener;
    }

    /**
     * Registers an inbound command listener
     * 
     * @param commandListener
     */
    public void setTransportListener(TransportListener commandListener) {
        this.transportListener = commandListener;
    }

    /**
     * narrow acceptance
     * 
     * @param target
     * @return 'this' if assignable
     */
    public Object narrow(Class target) {
        boolean assignableFrom = target.isAssignableFrom(getClass());
        if (assignableFrom) {
            return this;
        }
        return null;
    }

    public FutureResponse asyncRequest(Command command) throws IOException {
        throw new AssertionError("Unsupported Method");
    }

    public Response request(Command command) throws IOException {
        throw new AssertionError("Unsupported Method");
    }

    /**
     * Process the inbound command
     */
    public void doConsume(Command command) {
        if (command != null) {
            if (transportListener != null) {
                transportListener.onCommand(command);
            }
            else {
                log.error("No transportListener available to process inbound command: " + command);
            }
        }
    }

    /**
     * Passes any IO exceptions into the transport listener
     */
    public void onException(IOException e) {
        if (transportListener != null) {
            transportListener.onException(e);
        }
    }

}
