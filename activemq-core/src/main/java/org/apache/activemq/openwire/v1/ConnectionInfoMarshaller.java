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

package org.apache.activemq.openwire.v1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.activemq.openwire.*;
import org.apache.activemq.command.*;


/**
 * Marshalling code for Open Wire Format for ConnectionInfoMarshaller
 *
 *
 * NOTE!: This file is auto generated - do not modify!
 *        if you need to make a change, please see the modify the groovy scripts in the
 *        under src/gram/script and then use maven openwire:generate to regenerate 
 *        this file.
 *
 * @version $Revision$
 */
public class ConnectionInfoMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure we marshal
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return ConnectionInfo.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new object instance
     */
    public DataStructure createObject() {
        return new ConnectionInfo();
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o the object to un-marshal
     * @param dataIn the data input stream to build the object from
     * @throws IOException
     */
    public void tightUnmarshal(OpenWireFormat wireFormat, Object o, DataInputStream dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);

        ConnectionInfo info = (ConnectionInfo)o;
        info.setConnectionId((ConnectionId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setClientId(tightUnmarshalString(dataIn, bs));
        info.setPassword(tightUnmarshalString(dataIn, bs));
        info.setUserName(tightUnmarshalString(dataIn, bs));

        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for( int i=0; i < size; i++ ) {
                value[i] = (BrokerId) tightUnmarsalNestedObject(wireFormat,dataIn, bs);
            }
            info.setBrokerPath(value);
        }
        else {
            info.setBrokerPath(null);
        }

    }


    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    public int tightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) throws IOException {

        ConnectionInfo info = (ConnectionInfo)o;

        int rc = super.tightMarshal1(wireFormat, o, bs);
    rc += tightMarshalCachedObject1(wireFormat, info.getConnectionId(), bs);
    rc += tightMarshalString1(info.getClientId(), bs);
    rc += tightMarshalString1(info.getPassword(), bs);
    rc += tightMarshalString1(info.getUserName(), bs);
    rc += tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);

        return rc + 0;
    }

    /**
     * Write a object instance to data output stream
     *
     * @param o the instance to be marshaled
     * @param dataOut the output stream
     * @throws IOException thrown if an error occurs
     */
    public void tightMarshal2(OpenWireFormat wireFormat, Object o, DataOutputStream dataOut, BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);

        ConnectionInfo info = (ConnectionInfo)o;
    tightMarshalCachedObject2(wireFormat, info.getConnectionId(), dataOut, bs);
    tightMarshalString2(info.getClientId(), dataOut, bs);
    tightMarshalString2(info.getPassword(), dataOut, bs);
    tightMarshalString2(info.getUserName(), dataOut, bs);
    tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);

    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o the object to un-marshal
     * @param dataIn the data input stream to build the object from
     * @throws IOException
     */
    public void looseUnmarshal(OpenWireFormat wireFormat, Object o, DataInputStream dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);

        ConnectionInfo info = (ConnectionInfo)o;
        info.setConnectionId((ConnectionId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setClientId(looseUnmarshalString(dataIn));
        info.setPassword(looseUnmarshalString(dataIn));
        info.setUserName(looseUnmarshalString(dataIn));

        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for( int i=0; i < size; i++ ) {
                value[i] = (BrokerId) looseUnmarsalNestedObject(wireFormat,dataIn);
            }
            info.setBrokerPath(value);
        }
        else {
            info.setBrokerPath(null);
        }

    }


    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object o, DataOutputStream dataOut) throws IOException {

        ConnectionInfo info = (ConnectionInfo)o;

        super.looseMarshal(wireFormat, o, dataOut);
    looseMarshalCachedObject(wireFormat, info.getConnectionId(), dataOut);
    looseMarshalString(info.getClientId(), dataOut);
    looseMarshalString(info.getPassword(), dataOut);
    looseMarshalString(info.getUserName(), dataOut);
    looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);

    }
}
