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
package org.activemq.broker.jmx;

import org.activemq.network.NetworkConnector;

public class NetworkConnectorView implements NetworkConnectorViewMBean {

    private final NetworkConnector connector;

    public NetworkConnectorView(NetworkConnector connector) {
        this.connector = connector;
    }
    
    public void start() throws Exception {
        connector.start();
    }

    public void stop() throws Exception {
        connector.stop();
    }
}
