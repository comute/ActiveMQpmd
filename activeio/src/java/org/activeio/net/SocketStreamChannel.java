/** 
 * 
 * Copyright 2004 Hiram Chirino
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

package org.activeio.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.activeio.StreamChannel;

/**
 * A {@see org.activeio.StreamChannel} implementation that uses a {@see java.net.Socket}
 *  to talk to the network.
 * 
 * @version $Revision$
 */
public class SocketStreamChannel implements StreamChannel, SocketMetadata {

    private final Socket socket;
    private final OutputStream out;
    private final InputStream in;    
    private boolean disposed;
    private int curentSoTimeout;

    protected SocketStreamChannel(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();        
    }

    protected void setSoTimeout(int i) throws SocketException {
        if( curentSoTimeout != i ) {
            socket.setSoTimeout(i);
            curentSoTimeout = i;
        }
    }
    
    /**
     * @see org.activeio.Channel#flush()
     */
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * @see org.activeio.Disposable#dispose()
     */
    public void dispose() {
        if (disposed)
            return;

        try {
            out.close();
        } catch (IOException ignore) {
        }
        try {
            in.close();
        } catch (IOException ignore) {
        }
        try {
            socket.close();
        } catch (IOException ignore) {
        }
        disposed = true;
    }

    public void start() throws IOException {
    }
    public void stop(long timeout) throws IOException {
    }
    
    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }
    public boolean getKeepAlive() throws SocketException {
        return socket.getKeepAlive();
    }
    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }
    public int getLocalPort() {
        return socket.getLocalPort();
    }
    public SocketAddress getLocalSocketAddress() {
        return socket.getLocalSocketAddress();
    }
    public boolean getOOBInline() throws SocketException {
        return socket.getOOBInline();
    }
    public int getPort() {
        return socket.getPort();
    }
    public int getReceiveBufferSize() throws SocketException {
        return socket.getReceiveBufferSize();
    }
    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }
    public boolean getReuseAddress() throws SocketException {
        return socket.getReuseAddress();
    }
    public int getSendBufferSize() throws SocketException {
        return socket.getSendBufferSize();
    }
    public int getSoLinger() throws SocketException {
        return socket.getSoLinger();
    }
    public int getSoTimeout() throws SocketException {
        return socket.getSoTimeout();
    }
    public boolean getTcpNoDelay() throws SocketException {
        return socket.getTcpNoDelay();
    }
    public int getTrafficClass() throws SocketException {
        return socket.getTrafficClass();
    }
    public boolean isBound() {
        return socket.isBound();
    }
    public boolean isClosed() {
        return socket.isClosed();
    }
    public boolean isConnected() {
        return socket.isConnected();
    }
    public void setKeepAlive(boolean on) throws SocketException {
        socket.setKeepAlive(on);
    }
    public void setOOBInline(boolean on) throws SocketException {
        socket.setOOBInline(on);
    }
    public void setReceiveBufferSize(int size) throws SocketException {
        socket.setReceiveBufferSize(size);
    }
    public void setReuseAddress(boolean on) throws SocketException {
        socket.setReuseAddress(on);
    }
    public void setSendBufferSize(int size) throws SocketException {
        socket.setSendBufferSize(size);
    }
    public void setSoLinger(boolean on, int linger) throws SocketException {
        socket.setSoLinger(on, linger);
    }
    public void setTcpNoDelay(boolean on) throws SocketException {
        socket.setTcpNoDelay(on);
    }
    public void setTrafficClass(int tc) throws SocketException {
        socket.setTrafficClass(tc);
    }
    
    public Object getAdapter(Class target) {
        if( target.isAssignableFrom(getClass()) ) {
            return this;
        }
        return null;
    }

    public String toString() {
        return "Socket Connection: "+getLocalSocketAddress()+" -> "+getRemoteSocketAddress();
    }

    public void write(byte[] data, int pos, int length) throws IOException {
        out.write(data,pos,length);
    }

    public void write(byte[] data) throws IOException {
        out.write(data);
    }

    public void write(int data) throws IOException {
        out.write(data);
    }

    public int available() throws IOException {
        return in.available();
    }

    public void mark(int pos) {
        in.mark(pos);
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public int read(byte[] data, int offset, int length) throws IOException {
        try {
            return in.read(data, offset, length);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int read(byte[] data) throws IOException {
        return in.read(data);
    }

    public void reset() throws IOException {
        in.reset();
    }

    public long skip(long count) throws IOException {
        return in.skip(count);
    }

    public int read() throws IOException {
        return in.read();
    }

    public Socket getSocket() {
        return socket;
    }
}