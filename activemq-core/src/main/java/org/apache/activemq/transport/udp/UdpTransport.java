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
package org.apache.activemq.transport.udp;

import org.apache.activemq.Service;
import org.apache.activemq.command.Command;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportThreadSupport;
import org.apache.activemq.transport.udp.replay.DatagramReplayStrategy;
import org.apache.activemq.transport.udp.replay.ExceptionIfDroppedPacketStrategy;
import org.apache.activemq.util.ServiceStopper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;

/**
 * An implementation of the {@link Transport} interface using raw UDP
 * 
 * @version $Revision$
 */
public class UdpTransport extends TransportThreadSupport implements Transport, Service, Runnable {
    private static final Log log = LogFactory.getLog(UdpTransport.class);

    private CommandChannel commandChannel;
    private OpenWireFormat wireFormat;
    private ByteBufferPool bufferPool;
    private DatagramReplayStrategy replayStrategy = new ExceptionIfDroppedPacketStrategy();
    private int datagramSize = 4 * 1024;
    private long maxInactivityDuration = 0; // 30000;
    private InetSocketAddress targetAddress;
    private DatagramChannel channel;
    private boolean trace = false;
    private boolean useLocalHost = true;
    private int port;
    private CommandProcessor commandProcessor = new CommandProcessor() {
        public void process(Command command, SocketAddress address) {
            doConsume(command);
        }};

    protected UdpTransport(OpenWireFormat wireFormat) throws IOException {
        this.wireFormat = wireFormat;
    }

    public UdpTransport(OpenWireFormat wireFormat, URI remoteLocation) throws UnknownHostException, IOException {
        this(wireFormat);
        this.targetAddress = createAddress(remoteLocation);
    }

    public UdpTransport(OpenWireFormat wireFormat, InetSocketAddress socketAddress) throws IOException {
        this(wireFormat);
        this.targetAddress = socketAddress;
    }
    
    /**
     * A one way asynchronous send
     */
    public void oneway(Command command) throws IOException {
        oneway(command, targetAddress);
    }

    /**
     * A one way asynchronous send to a given address
     */
    public void oneway(Command command, InetSocketAddress address) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Sending oneway from port: " + port + " to target: " + targetAddress);
        }
        checkStarted(command);
        commandChannel.write(command, address);
    }

    /**
     * @return pretty print of 'this'
     */
    public String toString() {
        return "udp://" + targetAddress + "?port=" + port;
    }

    /**
     * reads packets from a Socket
     */
    public void run() {
        log.trace("Consumer thread starting for: " + toString());
        while (!isStopped()) {
            try {
                commandChannel.read(commandProcessor);
            }
            /*
             * catch (SocketTimeoutException e) { } catch
             * (InterruptedIOException e) { }
             */
            catch (AsynchronousCloseException e) {
                try {
                    stop();
                }
                catch (Exception e2) {
                    log.warn("Caught while closing: " + e2 + ". Now Closed", e2);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    stop();
                }
                catch (Exception e2) {
                    log.warn("Caught while closing: " + e2 + ". Now Closed", e2);
                }
                if (e instanceof IOException) {
                    onException((IOException) e);
                }
                else {
                    log.error(e);
                }
            }
        }
    }

    // Properties
    // -------------------------------------------------------------------------
    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    public long getMaxInactivityDuration() {
        return maxInactivityDuration;
    }

    public DatagramChannel getChannel() {
        return channel;
    }

    public void setChannel(DatagramChannel channel) {
        this.channel = channel;
    }

    /**
     * Sets the maximum inactivity duration
     */
    public void setMaxInactivityDuration(long maxInactivityDuration) {
        this.maxInactivityDuration = maxInactivityDuration;
    }

    public boolean isUseLocalHost() {
        return useLocalHost;
    }

    /**
     * Sets whether 'localhost' or the actual local host name should be used to
     * make local connections. On some operating systems such as Macs its not
     * possible to connect as the local host name so localhost is better.
     */
    public void setUseLocalHost(boolean useLocalHost) {
        this.useLocalHost = useLocalHost;
    }

    public CommandChannel getCommandChannel() {
        return commandChannel;
    }

    /**
     * Sets the implementation of the command channel to use.
     */
    public void setCommandChannel(CommandChannel commandChannel) {
        this.commandChannel = commandChannel;
    }

    public DatagramReplayStrategy getReplayStrategy() {
        return replayStrategy;
    }

    /**
     * Sets the strategy used to replay missed datagrams
     */
    public void setReplayStrategy(DatagramReplayStrategy replayStrategy) {
        this.replayStrategy = replayStrategy;
    }

    public int getPort() {
        return port;
    }

    /**
     * Sets the port to connect on
     */
    public void setPort(int port) {
        this.port = port;
    }

    
    // Implementation methods
    // -------------------------------------------------------------------------
    protected CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    protected void setCommandProcessor(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }
    
    /**
     * Creates an address from the given URI
     */
    protected InetSocketAddress createAddress(URI remoteLocation) throws UnknownHostException, IOException {
        String host = resolveHostName(remoteLocation.getHost());
        return new InetSocketAddress(host, remoteLocation.getPort());
    }

    protected String resolveHostName(String host) throws UnknownHostException {
        String localName = InetAddress.getLocalHost().getHostName();
        if (localName != null && isUseLocalHost()) {
            if (localName.equals(host)) {
                return "localhost";
            }
        }
        return host;
    }

    protected void doStart() throws Exception {
        SocketAddress localAddress = new InetSocketAddress(port);
        channel = DatagramChannel.open();
        channel.configureBlocking(true);

        // TODO
        // connect to default target address to avoid security checks each time
        // channel = channel.connect(targetAddress);
        
        DatagramSocket socket = channel.socket();
        socket.bind(localAddress);
        if (port == 0) {
            port = socket.getLocalPort();
        }
        
        if (bufferPool == null) {
            bufferPool = new DefaultBufferPool();
        }
        commandChannel = new CommandChannel(channel, wireFormat, bufferPool, datagramSize, replayStrategy, targetAddress);
        commandChannel.start();

        super.doStart();
    }

    protected void doStop(ServiceStopper stopper) throws Exception {
        if (channel != null) {
            channel.close();
        }
    }

}
