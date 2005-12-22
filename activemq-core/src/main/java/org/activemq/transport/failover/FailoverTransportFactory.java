/**
 *
 * Copyright 2004 The Apache Software Foundation
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
package org.activemq.transport.failover;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.activemq.transport.MutexTransport;
import org.activemq.transport.ResponseCorrelator;
import org.activemq.transport.Transport;
import org.activemq.transport.TransportFactory;
import org.activemq.transport.TransportServer;
import org.activemq.util.IntrospectionSupport;
import org.activemq.util.URISupport;
import org.activemq.util.URISupport.CompositeData;

public class FailoverTransportFactory extends TransportFactory {

    public Transport doConnect(URI location) throws IOException {
        try {
            Transport transport = createTransport(URISupport.parseComposite(location));
            transport =  new MutexTransport(transport);
            transport = new ResponseCorrelator(transport);
            return transport;
        } catch (URISyntaxException e) {
            throw new IOException("Invalid location: "+location);
        }
    }
    
    public Transport doCompositeConnect(URI location) throws IOException {
        try {
            return createTransport(URISupport.parseComposite(location));
        } catch (URISyntaxException e) {
            throw new IOException("Invalid location: "+location);
        }
    }

    /**
     * @param location
     * @return 
     * @throws IOException
     */
    public Transport createTransport(CompositeData compositData) throws IOException {
        FailoverTransport transport = createTransport(compositData.getParameters());
        transport.add(compositData.getComponents());
        return transport;
    }

    public FailoverTransport createTransport(Map parameters) throws IOException {
        FailoverTransport transport = new FailoverTransport();
        IntrospectionSupport.setProperties(transport, parameters);
        return transport;
    }

    public TransportServer doBind(String brokerId,URI location) throws IOException {
        throw new IOException("Invalid server URI: "+location);
    }

}
