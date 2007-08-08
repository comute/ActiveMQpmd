/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.discovery.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.apache.activemq.command.DiscoveryEvent;
import org.apache.activemq.transport.discovery.DiscoveryAgent;
import org.apache.activemq.transport.discovery.DiscoveryListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link DiscoveryAgent} using a multicast address and heartbeat packets
 * encoded using any wireformat, but openwire by default.
 * 
 * @version $Revision$
 */
public class MulticastDiscoveryAgent implements DiscoveryAgent, Runnable {
    private static final Log log = LogFactory.getLog(MulticastDiscoveryAgent.class);
    public static final String DEFAULT_DISCOVERY_URI_STRING = "multicast://239.255.2.3:6155";
    private static final String TYPE_SUFFIX = "ActiveMQ-4.";
    private static final String ALIVE = "alive.";
    private static final String DEAD = "dead.";
    private static final String DELIMITER = "%";
    private static final int BUFF_SIZE = 8192;
    private static final int DEFAULT_IDLE_TIME = 500;
    private static final int HEARTBEAT_MISS_BEFORE_DEATH = 10;

    private long initialReconnectDelay = 1000 * 5;
    private long maxReconnectDelay = 1000 * 30;
    private long backOffMultiplier = 2;
    private boolean useExponentialBackOff = false;
    private int maxReconnectAttempts;

    class RemoteBrokerData {
        final String brokerName;
        final String service;
        long lastHeartBeat;
        long recoveryTime;
        int failureCount;
        boolean failed;

        public RemoteBrokerData(String brokerName, String service) {
            this.brokerName = brokerName;
            this.service = service;
            this.lastHeartBeat = System.currentTimeMillis();
        }

        synchronized public void updateHeartBeat() {
            lastHeartBeat = System.currentTimeMillis();

            // Consider that the broker recovery has succeeded if it has not
            // failed in 60 seconds.
            if (!failed && failureCount > 0 && (lastHeartBeat - recoveryTime) > 1000 * 60) {
                if (log.isDebugEnabled())
                    log.debug("I now think that the " + service + " service has recovered.");
                failureCount = 0;
                recoveryTime = 0;
            }
        }

        synchronized public long getLastHeartBeat() {
            return lastHeartBeat;
        }

        synchronized public boolean markFailed() {
            if (!failed) {
                failed = true;
                failureCount++;

                long reconnectDelay;
                if (!useExponentialBackOff) {
                    reconnectDelay = initialReconnectDelay;
                } else {
                    reconnectDelay = (long)Math.pow(backOffMultiplier, failureCount);
                    if (reconnectDelay > maxReconnectDelay)
                        reconnectDelay = maxReconnectDelay;
                }

                if (log.isDebugEnabled())
                    log
                        .debug("Remote failure of "
                               + service
                               + " while still receiving multicast advertisements.  Advertising events will be suppressed for "
                               + reconnectDelay + " ms, the current failure count is: " + failureCount);

                recoveryTime = System.currentTimeMillis() + reconnectDelay;
                return true;
            }
            return false;
        }

        /**
         * @return true if this broker is marked failed and it is now the right
         *         time to start recovery.
         */
        synchronized public boolean doRecovery() {
            if (!failed)
                return false;

            // Are we done trying to recover this guy?
            if (maxReconnectAttempts > 0 && failureCount > maxReconnectAttempts) {
                if (log.isDebugEnabled())
                    log.debug("Max reconnect attempts of the " + service + " service has been reached.");
                return false;
            }

            // Is it not yet time?
            if (System.currentTimeMillis() < recoveryTime)
                return false;

            if (log.isDebugEnabled())
                log.debug("Resuming event advertisement of the " + service + " service.");

            failed = false;
            return true;
        }

        public boolean isFailed() {
            return failed;
        }
    }

    private int timeToLive = 1;
    private boolean loopBackMode = false;
    private Map brokersByService = new ConcurrentHashMap();
    private String group = "default";
    private String brokerName;
    private URI discoveryURI;
    private InetAddress inetAddress;
    private SocketAddress sockAddress;
    private DiscoveryListener discoveryListener;
    private String selfService;
    private MulticastSocket mcast;
    private Thread runner;
    private long keepAliveInterval = DEFAULT_IDLE_TIME;
    private long lastAdvertizeTime = 0;
    private AtomicBoolean started = new AtomicBoolean(false);
    private boolean reportAdvertizeFailed = true;

    private final Executor executor = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
                                                             new LinkedBlockingQueue(), new ThreadFactory() {
                                                                 public Thread newThread(Runnable runable) {
                                                                     Thread t = new Thread(runable,
                                                                                           "Multicast Discovery Agent Notifier");
                                                                     t.setDaemon(true);
                                                                     return t;
                                                                 }
                                                             });

    /**
     * Set the discovery listener
     * 
     * @param listener
     */
    public void setDiscoveryListener(DiscoveryListener listener) {
        this.discoveryListener = listener;
    }

    /**
     * register a service
     */
    public void registerService(String name) throws IOException {
        this.selfService = name;
        if (started.get()) {
            doAdvertizeSelf();
        }
    }

    /**
     * Get the group used for discovery
     * 
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set the group for discovery
     * 
     * @param group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return Returns the brokerName.
     */
    public String getBrokerName() {
        return brokerName;
    }

    /**
     * @param brokerName The brokerName to set.
     */
    public void setBrokerName(String brokerName) {
        if (brokerName != null) {
            brokerName = brokerName.replace('.', '-');
            brokerName = brokerName.replace(':', '-');
            brokerName = brokerName.replace('%', '-');
            this.brokerName = brokerName;
        }
    }

    /**
     * @return Returns the loopBackMode.
     */
    public boolean isLoopBackMode() {
        return loopBackMode;
    }

    /**
     * @param loopBackMode The loopBackMode to set.
     */
    public void setLoopBackMode(boolean loopBackMode) {
        this.loopBackMode = loopBackMode;
    }

    /**
     * @return Returns the timeToLive.
     */
    public int getTimeToLive() {
        return timeToLive;
    }

    /**
     * @param timeToLive The timeToLive to set.
     */
    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * @return the discoveryURI
     */
    public URI getDiscoveryURI() {
        return discoveryURI;
    }

    /**
     * Set the discoveryURI
     * 
     * @param discoveryURI
     */
    public void setDiscoveryURI(URI discoveryURI) {
        this.discoveryURI = discoveryURI;
    }

    public long getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    /**
     * start the discovery agent
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        if (started.compareAndSet(false, true)) {
            if (group == null || group.length() == 0) {
                throw new IOException("You must specify a group to discover");
            }
            if (brokerName == null || brokerName.length() == 0) {
                log.warn("brokerName not set");
            }
            String type = getType();
            if (!type.endsWith(".")) {
                log.warn("The type '" + type + "' should end with '.' to be a valid Discovery type");
                type += ".";
            }
            if (discoveryURI == null) {
                discoveryURI = new URI(DEFAULT_DISCOVERY_URI_STRING);
            }
            this.inetAddress = InetAddress.getByName(discoveryURI.getHost());
            this.sockAddress = new InetSocketAddress(this.inetAddress, discoveryURI.getPort());
            mcast = new MulticastSocket(discoveryURI.getPort());
            mcast.setLoopbackMode(loopBackMode);
            mcast.setTimeToLive(getTimeToLive());
            mcast.joinGroup(inetAddress);
            mcast.setSoTimeout((int)keepAliveInterval);
            runner = new Thread(this);
            runner.setName("MulticastDiscovery: " + selfService);
            runner.setDaemon(true);
            runner.start();
            doAdvertizeSelf();
        }
    }

    /**
     * stop the channel
     * 
     * @throws Exception
     */
    public void stop() throws Exception {
        if (started.compareAndSet(true, false)) {
            doAdvertizeSelf();
            mcast.close();
        }
    }

    public String getType() {
        return group + "." + TYPE_SUFFIX;
    }

    public void run() {
        byte[] buf = new byte[BUFF_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, 0, buf.length);
        while (started.get()) {
            doTimeKeepingServices();
            try {
                mcast.receive(packet);
                if (packet.getLength() > 0) {
                    String str = new String(packet.getData(), packet.getOffset(), packet.getLength());
                    processData(str);
                }
            } catch (SocketTimeoutException se) {
                // ignore
            } catch (IOException e) {
                if (started.get()) {
                    log.error("failed to process packet: " + e);
                }
            }
        }
    }

    private void processData(String str) {
        if (discoveryListener != null) {
            if (str.startsWith(getType())) {
                String payload = str.substring(getType().length());
                if (payload.startsWith(ALIVE)) {
                    String brokerName = getBrokerName(payload.substring(ALIVE.length()));
                    String service = payload.substring(ALIVE.length() + brokerName.length() + 2);
                    if (!brokerName.equals(this.brokerName)) {
                        processAlive(brokerName, service);
                    }
                } else {
                    String brokerName = getBrokerName(payload.substring(DEAD.length()));
                    String service = payload.substring(DEAD.length() + brokerName.length() + 2);
                    if (!brokerName.equals(this.brokerName)) {
                        processDead(brokerName, service);
                    }
                }
            }
        }
    }

    private void doTimeKeepingServices() {
        if (started.get()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime < lastAdvertizeTime || ((currentTime - keepAliveInterval) > lastAdvertizeTime)) {
                doAdvertizeSelf();
                lastAdvertizeTime = currentTime;
            }
            doExpireOldServices();
        }
    }

    private void doAdvertizeSelf() {
        if (selfService != null) {
            String payload = getType();
            payload += started.get() ? ALIVE : DEAD;
            payload += DELIMITER + brokerName + DELIMITER;
            payload += selfService;
            try {
                byte[] data = payload.getBytes();
                DatagramPacket packet = new DatagramPacket(data, 0, data.length, sockAddress);
                mcast.send(packet);
            } catch (IOException e) {
                // If a send fails, chances are all subsequent sends will fail
                // too.. No need to keep reporting the
                // same error over and over.
                if (reportAdvertizeFailed) {
                    reportAdvertizeFailed = false;
                    log.error("Failed to advertise our service: " + payload, e);
                    if ("Operation not permitted".equals(e.getMessage())) {
                        log.error("The 'Operation not permitted' error has been know to be caused by improper firewall/network setup.  "
                                  + "Please make sure that the OS is properly configured to allow multicast traffic over: " + mcast.getLocalAddress());
                    }
                }
            }
        }
    }

    private void processAlive(String brokerName, String service) {
        if (selfService == null || !service.equals(selfService)) {
            RemoteBrokerData data = (RemoteBrokerData)brokersByService.get(service);
            if (data == null) {
                data = new RemoteBrokerData(brokerName, service);
                brokersByService.put(service, data);
                ;
                fireServiceAddEvent(data);
                doAdvertizeSelf();

            } else {
                data.updateHeartBeat();
                if (data.doRecovery()) {
                    fireServiceAddEvent(data);
                }
            }
        }
    }

    private void processDead(String brokerName, String service) {
        if (!service.equals(selfService)) {
            RemoteBrokerData data = (RemoteBrokerData)brokersByService.remove(service);
            if (data != null && !data.isFailed()) {
                fireServiceRemovedEvent(data);
            }
        }
    }

    private void doExpireOldServices() {
        long expireTime = System.currentTimeMillis() - (keepAliveInterval * HEARTBEAT_MISS_BEFORE_DEATH);
        for (Iterator i = brokersByService.values().iterator(); i.hasNext();) {
            RemoteBrokerData data = (RemoteBrokerData)i.next();
            if (data.getLastHeartBeat() < expireTime) {
                processDead(brokerName, data.service);
            }
        }
    }

    private String getBrokerName(String str) {
        String result = null;
        int start = str.indexOf(DELIMITER);
        if (start >= 0) {
            int end = str.indexOf(DELIMITER, start + 1);
            result = str.substring(start + 1, end);
        }
        return result;
    }

    public void serviceFailed(DiscoveryEvent event) throws IOException {
        RemoteBrokerData data = (RemoteBrokerData)brokersByService.get(event.getServiceName());
        if (data != null && data.markFailed()) {
            fireServiceRemovedEvent(data);
        }
    }

    private void fireServiceRemovedEvent(RemoteBrokerData data) {
        if (discoveryListener != null) {
            final DiscoveryEvent event = new DiscoveryEvent(data.service);
            event.setBrokerName(data.brokerName);

            // Have the listener process the event async so that
            // he does not block this thread since we are doing time sensitive
            // processing of events.
            executor.execute(new Runnable() {
                public void run() {
                    DiscoveryListener discoveryListener = MulticastDiscoveryAgent.this.discoveryListener;
                    if (discoveryListener != null) {
                        discoveryListener.onServiceRemove(event);
                    }
                }
            });
        }
    }

    private void fireServiceAddEvent(RemoteBrokerData data) {
        if (discoveryListener != null) {
            final DiscoveryEvent event = new DiscoveryEvent(data.service);
            event.setBrokerName(data.brokerName);

            // Have the listener process the event async so that
            // he does not block this thread since we are doing time sensitive
            // processing of events.
            executor.execute(new Runnable() {
                public void run() {
                    DiscoveryListener discoveryListener = MulticastDiscoveryAgent.this.discoveryListener;
                    if (discoveryListener != null) {
                        discoveryListener.onServiceAdd(event);
                    }
                }
            });
        }
    }

    public long getBackOffMultiplier() {
        return backOffMultiplier;
    }

    public void setBackOffMultiplier(long backOffMultiplier) {
        this.backOffMultiplier = backOffMultiplier;
    }

    public long getInitialReconnectDelay() {
        return initialReconnectDelay;
    }

    public void setInitialReconnectDelay(long initialReconnectDelay) {
        this.initialReconnectDelay = initialReconnectDelay;
    }

    public int getMaxReconnectAttempts() {
        return maxReconnectAttempts;
    }

    public void setMaxReconnectAttempts(int maxReconnectAttempts) {
        this.maxReconnectAttempts = maxReconnectAttempts;
    }

    public long getMaxReconnectDelay() {
        return maxReconnectDelay;
    }

    public void setMaxReconnectDelay(long maxReconnectDelay) {
        this.maxReconnectDelay = maxReconnectDelay;
    }

    public boolean isUseExponentialBackOff() {
        return useExponentialBackOff;
    }

    public void setUseExponentialBackOff(boolean useExponentialBackOff) {
        this.useExponentialBackOff = useExponentialBackOff;
    }
}
