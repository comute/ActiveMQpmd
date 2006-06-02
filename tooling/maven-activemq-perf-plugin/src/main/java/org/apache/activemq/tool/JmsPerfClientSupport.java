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
package org.apache.activemq.tool;

import java.util.Map;
import java.util.HashMap;

public class JmsPerfClientSupport extends JmsConfigurableClientSupport implements PerfMeasurable {

    protected long throughput   = 0;

    protected PerfEventListener listener = null;

    public void reset() {
        setThroughput(0);
    }

    public long getThroughput() {
        return throughput;
    }

    public synchronized void setThroughput(long val) {
        this.throughput = val;
    }

    public synchronized void incThroughput() {
        throughput++;
    }

    public synchronized void incThroughput(long val) {
        throughput += val;
    }

    public void setPerfEventListener(PerfEventListener listener) {
        this.listener = listener;
    }

    public PerfEventListener getPerfEventListener() {
        return listener;
    }

    public Map getClientSettings() {
        Map settings = new HashMap();
        settings.put("client.server", getServerType());
        settings.put("client.factoryClass", getFactoryClass());
        settings.put("client.clientID", getClientID());
        settings.putAll(getFactorySettings());
        settings.putAll(getConnectionSettings());
        settings.putAll(getSessionSettings());
        settings.putAll(getQueueSettings());
        settings.putAll(getTopicSettings());
        settings.putAll(getProducerSettings());
        settings.putAll(getConsumerSettings());
        settings.putAll(getMessageSettings());

        return settings;
    }
}
