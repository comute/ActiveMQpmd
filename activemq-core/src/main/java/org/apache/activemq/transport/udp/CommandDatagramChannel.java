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

import edu.emory.mathcs.backport.java.util.concurrent.Future;

import org.activeio.ByteArrayInputStream;
import org.activeio.ByteArrayOutputStream;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.Endpoint;
import org.apache.activemq.command.LastPartialCommand;
import org.apache.activemq.command.PartialCommand;
import org.apache.activemq.openwire.BooleanStream;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;

/**
 * A strategy for reading datagrams and de-fragmenting them together.
 * 
 * @version $Revision$
 */
public class CommandDatagramChannel implements CommandChannel {

    private static final Log log = LogFactory.getLog(CommandDatagramChannel.class);

    private final UdpTransport transport;
    private final String name;
    private DatagramChannel channel;
    private OpenWireFormat wireFormat;
    private ByteBufferPool bufferPool;
    private int datagramSize = 4 * 1024;
    private SocketAddress targetAddress;
    private DatagramHeaderMarshaller headerMarshaller;

    // reading
    private Object readLock = new Object();
    private ByteBuffer readBuffer;

    // writing
    private Object writeLock = new Object();
    private ByteBuffer writeBuffer;
    private int defaultMarshalBufferSize = 64 * 1024;


    public CommandDatagramChannel(UdpTransport transport, DatagramChannel channel, OpenWireFormat wireFormat, ByteBufferPool bufferPool, int datagramSize,
            SocketAddress targetAddress, DatagramHeaderMarshaller headerMarshaller) {
        this.transport = transport;
        this.channel = channel;
        this.wireFormat = wireFormat;
        this.bufferPool = bufferPool;
        this.datagramSize = datagramSize;
        this.targetAddress = targetAddress;
        this.headerMarshaller = headerMarshaller;
        this.name = transport.toString();
    }

    public String toString() {
        return "CommandChannel#" + name;
    }

    public void start() throws Exception {
        bufferPool.setDefaultSize(datagramSize);
        bufferPool.start();
        readBuffer = bufferPool.borrowBuffer();
        writeBuffer = bufferPool.borrowBuffer();
    }

    public void stop() throws Exception {
        bufferPool.stop();
    }

    public Command read() throws IOException {
        Command answer = null;
        Endpoint from = null;
        synchronized (readLock) {
            while (true) {
                readBuffer.clear();
                SocketAddress address = channel.receive(readBuffer);

                readBuffer.flip();

                if (readBuffer.limit() == 0) {
                    continue;
                }
                from = headerMarshaller.createEndpoint(readBuffer, address);
 
                int remaining = readBuffer.remaining();
                byte[] data = new byte[remaining];
                readBuffer.get(data);

                // TODO could use a DataInput implementation that talks direct
                // to
                // the ByteBuffer to avoid object allocation and unnecessary
                // buffering?
                DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
                answer = (Command) wireFormat.unmarshal(dataIn);
                break;
            }
        }
        if (answer != null) {
            answer.setFrom(from);
            
            if (log.isDebugEnabled()) {
                log.debug("Channel: " + name + " received from: " + from + " about to process: " + answer);
            }
        }
        return answer;
    }

    public void write(Command command, SocketAddress address, Map requestMap, Future future) throws IOException {
        synchronized (writeLock) {

            if (!command.isWireFormatInfo() && command.getCommandId() == 0) {
                command.setCommandId(transport.getNextCommandId());
            }
            ByteArrayOutputStream largeBuffer = new ByteArrayOutputStream(defaultMarshalBufferSize);
            wireFormat.marshal(command, new DataOutputStream(largeBuffer));
            byte[] data = largeBuffer.toByteArray();
            int size = data.length;

            writeBuffer.clear();
            headerMarshaller.writeHeader(command, writeBuffer);

            if (size > writeBuffer.remaining()) {
                // lets split the command up into chunks
                int offset = 0;
                boolean lastFragment = false;
                for (int fragment = 0, length = data.length; !lastFragment; fragment++) {
                    // write the header
                    if (fragment > 0) {
                        writeBuffer.clear();
                        headerMarshaller.writeHeader(command, writeBuffer);
                    }

                    int chunkSize = writeBuffer.remaining();

                    // we need to remove the amount of overhead to write the
                    // partial command

                    // lets write the flags in there
                    BooleanStream bs = null;
                    if (wireFormat.isTightEncodingEnabled()) {
                        bs = new BooleanStream();
                        bs.writeBoolean(true); // the partial data byte[] is
                        // never null
                    }

                    // lets remove the header of the partial command
                    // which is the byte for the type and an int for the size of
                    // the byte[]
                    chunkSize -= 1 // the data type
                    + 4 // the command ID
                    + 4; // the size of the partial data

                    // the boolean flags
                    if (bs != null) {
                        chunkSize -= bs.marshalledSize();
                    }
                    else {
                        chunkSize -= 1;
                    }

                    if (!wireFormat.isSizePrefixDisabled()) {
                        // lets write the size of the command buffer
                        writeBuffer.putInt(chunkSize);
                        chunkSize -= 4;
                    }

                    lastFragment = offset + chunkSize >= length;
                    if (chunkSize + offset > length) {
                        chunkSize = length - offset;
                    }

                    writeBuffer.put(PartialCommand.DATA_STRUCTURE_TYPE);

                    if (bs != null) {
                        bs.marshal(writeBuffer);
                    }

                    int commandId = command.getCommandId();
                    if (fragment > 0) {
                        commandId = transport.getNextCommandId();
                    }
                    writeBuffer.putInt(commandId);
                    if (bs == null) {
                        writeBuffer.put((byte) 1);
                    }

                    // size of byte array
                    writeBuffer.putInt(chunkSize);

                    // now the data
                    writeBuffer.put(data, offset, chunkSize);

                    offset += chunkSize;
                    sendWriteBuffer(address);
                }

                // now lets write the last partial command
                command = new LastPartialCommand(command.isResponseRequired());
                command.setCommandId(transport.getNextCommandId());
                
                largeBuffer = new ByteArrayOutputStream(defaultMarshalBufferSize);
                wireFormat.marshal(command, new DataOutputStream(largeBuffer));
                data = largeBuffer.toByteArray();

                writeBuffer.clear();
                headerMarshaller.writeHeader(command, writeBuffer);
            }

            writeBuffer.put(data);

            if (command.isResponseRequired()) {
                requestMap.put(new Integer(command.getCommandId()), future);
            }
            sendWriteBuffer(address);
        }
    }

    // Properties
    // -------------------------------------------------------------------------

    public int getDatagramSize() {
        return datagramSize;
    }

    public void setDatagramSize(int datagramSize) {
        this.datagramSize = datagramSize;
    }

    public ByteBufferPool getBufferPool() {
        return bufferPool;
    }

    /**
     * Sets the implementation of the byte buffer pool to use
     */
    public void setBufferPool(ByteBufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    public DatagramHeaderMarshaller getHeaderMarshaller() {
        return headerMarshaller;
    }

    public void setHeaderMarshaller(DatagramHeaderMarshaller headerMarshaller) {
        this.headerMarshaller = headerMarshaller;
    }

    
    public SocketAddress getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(SocketAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    protected void sendWriteBuffer(SocketAddress address) throws IOException {
        writeBuffer.flip();

        if (log.isDebugEnabled()) {
            log.debug("Channel: " + name + " sending datagram to: " + address);
        }
        channel.send(writeBuffer, address);
    }

}
