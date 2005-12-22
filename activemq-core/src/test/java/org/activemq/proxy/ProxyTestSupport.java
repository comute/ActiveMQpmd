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
package org.activemq.proxy;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import org.activemq.broker.BrokerService;
import org.activemq.broker.BrokerTestSupport;
import org.activemq.broker.StubConnection;
import org.activemq.broker.TransportConnector;
import org.activemq.memory.UsageManager;
import org.activemq.store.PersistenceAdapter;
import org.activemq.transport.Transport;
import org.activemq.transport.TransportFactory;

public class ProxyTestSupport extends BrokerTestSupport {
    
    protected ArrayList connections = new ArrayList();
    
    protected TransportConnector connector;

    protected PersistenceAdapter remotePersistenceAdapter;
    protected BrokerService remoteBroker;
    protected UsageManager remoteMemoryManager;
    protected TransportConnector remoteConnector;
    private ProxyConnector proxyConnector;
    private ProxyConnector remoteProxyConnector;

    protected BrokerService createBroker() throws Exception {
        BrokerService service = new BrokerService();
        service.setBrokerName("broker1");
        service.setPersistent(false);

        connector = service.addConnector(getLocalURI());
        proxyConnector=new ProxyConnector();
        proxyConnector.setBind(new URI(getLocalProxyURI()));
        proxyConnector.setRemote(new URI("fanout:static://"+getRemoteURI()));
        service.addProxyConnector(proxyConnector);
        
        return service;
    }

    protected BrokerService createRemoteBroker() throws Exception {
        BrokerService service = new BrokerService();
        service.setBrokerName("broker2");
        service.setPersistent(false);

        remoteConnector = service.addConnector(getRemoteURI());
        remoteProxyConnector = new ProxyConnector();
        remoteProxyConnector.setBind(new URI(getRemoteProxyURI()));
        remoteProxyConnector.setRemote(new URI("fanout:static://"+getLocalURI()));
        service.addProxyConnector(remoteProxyConnector);
        
        return service;
    }
    

    protected void setUp() throws Exception {
        super.setUp();
        remoteBroker = createRemoteBroker();
        remoteBroker.start();
    }
    
    protected void tearDown() throws Exception {
        for (Iterator iter = connections.iterator(); iter.hasNext();) {
            StubConnection connection = (StubConnection) iter.next();
            connection.stop();
            iter.remove();
        }
        remoteBroker.stop();
        super.tearDown();
    }

    protected String getRemoteURI() {
        return "tcp://localhost:7001";
    }

    protected String getLocalURI() {
        return "tcp://localhost:6001";
    }

    protected String getRemoteProxyURI() {
        return "tcp://localhost:7002";
    }

    protected String getLocalProxyURI() {
        return "tcp://localhost:6002";
    }

    protected StubConnection createConnection() throws Exception {
        Transport transport = TransportFactory.connect(connector.getServer().getConnectURI());
        StubConnection connection = new StubConnection(transport);
        connections.add(connection);
        return connection;
    }

    protected StubConnection createRemoteConnection() throws Exception {
        Transport transport = TransportFactory.connect(remoteConnector.getServer().getConnectURI());
        StubConnection connection = new StubConnection(transport);
        connections.add(connection);
        return connection;
    }

    protected StubConnection createProxyConnection() throws Exception {
        Transport transport = TransportFactory.connect(proxyConnector.getServer().getConnectURI());
        StubConnection connection = new StubConnection(transport);
        connections.add(connection);
        return connection;
    }

    protected StubConnection createRemoteProxyConnection() throws Exception {
        Transport transport = TransportFactory.connect(remoteProxyConnector.getServer().getConnectURI());
        StubConnection connection = new StubConnection(transport);
        connections.add(connection);
        return connection;
    }
    
}
