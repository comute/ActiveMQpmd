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

import org.apache.activemq.state.CommandVisitor;

/**
 * @openwire:marshaller code="10"
 * @version $Revision$
 */
public class KeepAliveInfo implements Command {

    public static final byte DATA_STRUCTURE_TYPE=CommandTypes.KEEP_ALIVE_INFO;
    
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    public void setCommandId(short value) {
    }

    public short getCommandId() {
        return 0;
    }

    public void setResponseRequired(boolean responseRequired) {
    }

    public boolean isResponseRequired() {
        return false;
    }

    public boolean isResponse() {
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

    public boolean isBrokerInfo() {
        return false;
    }

    public boolean isWireFormatInfo() {
        return false;
    }

    public Response visit(CommandVisitor visitor) throws Throwable {
        return visitor.processKeepAlive( this );
    }

    public boolean isMarshallAware() {
        return false;
    }

    public boolean isMessageDispatchNotification(){
        return false;
    }
    
    public boolean isShutdownInfo(){
        return false;
    }

}
