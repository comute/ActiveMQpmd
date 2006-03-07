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
package org.apache.activemq.command;

import org.activeio.ByteArrayInputStream;
import org.activeio.ByteArrayOutputStream;
import org.activeio.ByteSequence;
import org.activeio.command.WireFormat;
import org.apache.activemq.state.CommandVisitor;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.MarshallingSupport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @openwire:marshaller code="1"
 * @version $Revision$
 */
public class WireFormatInfo implements Command, MarshallAware {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.WIREFORMAT_INFO;
    static final private byte MAGIC[] = new byte[] { 'A', 'c', 't', 'i', 'v', 'e', 'M', 'Q' };

    protected byte magic[] = MAGIC;
    protected int version;
    protected transient HashMap properties;
    protected ByteSequence marshalledProperties;

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    public boolean isWireFormatInfo() {
        return true;
    }

    public boolean isMarshallAware() {
        return true;
    }


    /**
     * @openwire:property version=1 size=8 testSize=-1
     */
    public byte[] getMagic() {
        return magic;
    }
    public void setMagic(byte[] magic) {
        this.magic = magic;
    }

    /**
     * @openwire:property version=1
     */
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * @openwire:property version=1
     */
    public ByteSequence getMarshalledProperties() {
        return marshalledProperties;
    }
    public void setMarshalledProperties(ByteSequence marshalledProperties) {
        this.marshalledProperties = marshalledProperties;
    }

    //////////////////////
    // 
    // Implementation Methods.
    //
    //////////////////////
    
    public Object getProperty(String name) throws IOException {
        if( properties == null ) {
            if( marshalledProperties ==null )
                return null;
            properties = unmarsallProperties(marshalledProperties);
        }
        return properties.get(name);
    }
    
    public Map getProperties() throws IOException {
        if( properties == null ) {
            if( marshalledProperties==null )
                return Collections.EMPTY_MAP;
            properties = unmarsallProperties(marshalledProperties);
        }
        return Collections.unmodifiableMap(properties);
    }
    
    public void clearProperties() {
        marshalledProperties = null;
        properties=null;
    }

    public void setProperty(String name, Object value) throws IOException {
        lazyCreateProperties();
        properties.put(name, value);
    }

    protected void lazyCreateProperties() throws IOException {
        if( properties == null ) {
            if( marshalledProperties == null ) {
                properties = new HashMap();
            } else {
                properties = unmarsallProperties(marshalledProperties);
                marshalledProperties = null;
            }
        }
    }
    
    private HashMap unmarsallProperties(ByteSequence marshalledProperties) throws IOException {
        return MarshallingSupport.unmarshalPrimitiveMap(new DataInputStream(new ByteArrayInputStream(marshalledProperties)));
    }

    public void beforeMarshall(WireFormat wireFormat) throws IOException {
        // Need to marshal the properties.
        if( marshalledProperties==null && properties!=null ) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos);
            MarshallingSupport.marshalPrimitiveMap(properties, os);
            os.close();
            marshalledProperties = baos.toByteSequence();
        }
    }

    public void afterMarshall(WireFormat wireFormat) throws IOException {
    }

    public void beforeUnmarshall(WireFormat wireFormat) throws IOException {
    }

    public void afterUnmarshall(WireFormat wireFormat) throws IOException {
    }


    public boolean isValid() {
        return magic != null && Arrays.equals(magic, MAGIC);
    }

    public void setResponseRequired(boolean responseRequired) {
    }

    /**
     * @throws IOException 
     */
    public boolean isCacheEnabled() throws IOException {
        return Boolean.TRUE == getProperty("cache");
    }
    public void setCacheEnabled(boolean cacheEnabled) throws IOException {
        setProperty("cache", cacheEnabled ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @throws IOException 
     */
    public boolean isStackTraceEnabled() throws IOException {
        return Boolean.TRUE == getProperty("stackTrace");
    }
    public void setStackTraceEnabled(boolean stackTraceEnabled) throws IOException {
        setProperty("stackTrace", stackTraceEnabled ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @throws IOException 
     */
    public boolean isTcpNoDelayEnabled() throws IOException {
        return Boolean.TRUE == getProperty("tcpNoDelay");
    }
    public void setTcpNoDelayEnabled(boolean tcpNoDelayEnabled) throws IOException {
        setProperty("tcpNoDelay", tcpNoDelayEnabled ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @throws IOException 
     */
    public boolean isPrefixPacketSize() throws IOException {
        return Boolean.TRUE == getProperty("prefixPacketSize");
    }
    public void setPrefixPacketSize(boolean prefixPacketSize) throws IOException {
        setProperty("prefixPacketSize", prefixPacketSize ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @throws IOException 
     */
    public boolean isTightEncodingEnabled() throws IOException {
        return Boolean.TRUE == getProperty("tightEncoding");
    }
    public void setTightEncodingEnabled(boolean tightEncodingEnabled) throws IOException {
        setProperty("tightEncoding", tightEncodingEnabled ? Boolean.TRUE : Boolean.FALSE);
    }

    public Response visit(CommandVisitor visitor) throws Exception {
        return visitor.processWireFormat(this);
    }

    public String toString() {
        return IntrospectionSupport.toString(this, WireFormatInfo.class);
    }

    ///////////////////////////////////////////////////////////////
    //
    // This are not implemented.
    //
    ///////////////////////////////////////////////////////////////
    
    public void setCommandId(short value) {
    }
    public short getCommandId() {
        return 0;
    }
    public boolean isResponseRequired() {
        return false;
    }
    public boolean isResponse() {
        return false;
    }
    public boolean isBrokerInfo() {
        return false;
    }
    public boolean isMessageDispatch() {
        return false;
    }
    public boolean isMessage() {
        return false;
    }
    public boolean isMessageAck() {
        return false;
    }
    public boolean isMessageDispatchNotification(){
        return false;
    }
    public boolean isShutdownInfo(){
        return false;
    }
    public void setCachedMarshalledForm(WireFormat wireFormat, ByteSequence data) {
    }
    public ByteSequence getCachedMarshalledForm(WireFormat wireFormat) {
        return null;
    }

}
