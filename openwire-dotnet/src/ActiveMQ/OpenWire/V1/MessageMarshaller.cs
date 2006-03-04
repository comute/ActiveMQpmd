//
//
// Copyright 2005-2006 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

using System;
using System.Collections;
using System.IO;

using ActiveMQ.OpenWire;
using ActiveMQ.OpenWire.Commands;
using ActiveMQ.OpenWire.V1;

namespace ActiveMQ.OpenWire.V1
{
  //
  //  Marshalling code for Open Wire Format for Message
  //
  //
  //  NOTE!: This file is autogenerated - do not modify!
  //        if you need to make a change, please see the Groovy scripts in the
  //        activemq-core module
  //
  public abstract class MessageMarshaller : BaseCommandMarshaller
  {

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void TightUnmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.TightUnmarshal(wireFormat, o, dataIn, bs);

        Message info = (Message)o;
        info.ProducerId = (ProducerId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.Destination = (ActiveMQDestination) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.TransactionId = (TransactionId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.OriginalDestination = (ActiveMQDestination) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.MessageId = (MessageId) TightUnmarshalNestedObject(wireFormat, dataIn, bs);
        info.OriginalTransactionId = (TransactionId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.GroupID = TightUnmarshalString(dataIn, bs);
        info.GroupSequence = BaseDataStreamMarshaller.ReadInt(dataIn);
        info.CorrelationId = TightUnmarshalString(dataIn, bs);
        info.Persistent = bs.ReadBoolean();
        info.Expiration = TightUnmarshalLong(wireFormat, dataIn, bs);
        info.Priority = BaseDataStreamMarshaller.ReadByte(dataIn);
        info.ReplyTo = (ActiveMQDestination) TightUnmarshalNestedObject(wireFormat, dataIn, bs);
        info.Timestamp = TightUnmarshalLong(wireFormat, dataIn, bs);
        info.Type = TightUnmarshalString(dataIn, bs);
        info.Content = ReadBytes(dataIn, bs.ReadBoolean());
        info.MarshalledProperties = ReadBytes(dataIn, bs.ReadBoolean());
        info.DataStructure = (DataStructure) TightUnmarshalNestedObject(wireFormat, dataIn, bs);
        info.TargetConsumerId = (ConsumerId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.Compressed = bs.ReadBoolean();
        info.RedeliveryCounter = BaseDataStreamMarshaller.ReadInt(dataIn);

        if (bs.ReadBoolean()) {
            short size = BaseDataStreamMarshaller.ReadShort(dataIn);
            BrokerId[] value = new BrokerId[size];
            for( int i=0; i < size; i++ ) {
                value[i] = (BrokerId) TightUnmarshalNestedObject(wireFormat,dataIn, bs);
            }
            info.BrokerPath = value;
        }
        else {
            info.BrokerPath = null;
        }
        info.Arrival = TightUnmarshalLong(wireFormat, dataIn, bs);
        info.UserID = TightUnmarshalString(dataIn, bs);
        info.RecievedByDFBridge = bs.ReadBoolean();

    }


    //
    // Write the booleans that this object uses to a BooleanStream
    //
    public override int TightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) {
        Message info = (Message)o;

        int rc = base.TightMarshal1(wireFormat, info, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.ProducerId, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.Destination, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.TransactionId, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.OriginalDestination, bs);
    rc += TightMarshalNestedObject1(wireFormat, info.MessageId, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.OriginalTransactionId, bs);
    rc += TightMarshalString1(info.GroupID, bs);
        rc += TightMarshalString1(info.CorrelationId, bs);
    bs.WriteBoolean(info.Persistent);
    rc += TightMarshalLong1(wireFormat, info.Expiration, bs);
        rc += TightMarshalNestedObject1(wireFormat, info.ReplyTo, bs);
    rc += TightMarshalLong1(wireFormat, info.Timestamp, bs);
    rc += TightMarshalString1(info.Type, bs);
    bs.WriteBoolean(info.Content!=null);
        rc += info.Content==null ? 0 : info.Content.Length+4;
    bs.WriteBoolean(info.MarshalledProperties!=null);
        rc += info.MarshalledProperties==null ? 0 : info.MarshalledProperties.Length+4;
    rc += TightMarshalNestedObject1(wireFormat, info.DataStructure, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.TargetConsumerId, bs);
    bs.WriteBoolean(info.Compressed);
        rc += TightMarshalObjectArray1(wireFormat, info.BrokerPath, bs);
    rc += TightMarshalLong1(wireFormat, info.Arrival, bs);
    rc += TightMarshalString1(info.UserID, bs);
    bs.WriteBoolean(info.RecievedByDFBridge);

        return rc + 9;
    }

    // 
    // Write a object instance to data output stream
    //
    public override void TightMarshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.TightMarshal2(wireFormat, o, dataOut, bs);

        Message info = (Message)o;
    TightMarshalCachedObject2(wireFormat, info.ProducerId, dataOut, bs);
    TightMarshalCachedObject2(wireFormat, info.Destination, dataOut, bs);
    TightMarshalCachedObject2(wireFormat, info.TransactionId, dataOut, bs);
    TightMarshalCachedObject2(wireFormat, info.OriginalDestination, dataOut, bs);
    TightMarshalNestedObject2(wireFormat, info.MessageId, dataOut, bs);
    TightMarshalCachedObject2(wireFormat, info.OriginalTransactionId, dataOut, bs);
    TightMarshalString2(info.GroupID, dataOut, bs);
    BaseDataStreamMarshaller.WriteInt(info.GroupSequence, dataOut);
    TightMarshalString2(info.CorrelationId, dataOut, bs);
    bs.ReadBoolean();
    TightMarshalLong2(wireFormat, info.Expiration, dataOut, bs);
    BaseDataStreamMarshaller.WriteByte(info.Priority, dataOut);
    TightMarshalNestedObject2(wireFormat, info.ReplyTo, dataOut, bs);
    TightMarshalLong2(wireFormat, info.Timestamp, dataOut, bs);
    TightMarshalString2(info.Type, dataOut, bs);
    if(bs.ReadBoolean()) {
           BaseDataStreamMarshaller.WriteInt(info.Content.Length, dataOut);
           dataOut.Write(info.Content);
        }
    if(bs.ReadBoolean()) {
           BaseDataStreamMarshaller.WriteInt(info.MarshalledProperties.Length, dataOut);
           dataOut.Write(info.MarshalledProperties);
        }
    TightMarshalNestedObject2(wireFormat, info.DataStructure, dataOut, bs);
    TightMarshalCachedObject2(wireFormat, info.TargetConsumerId, dataOut, bs);
    bs.ReadBoolean();
    BaseDataStreamMarshaller.WriteInt(info.RedeliveryCounter, dataOut);
    TightMarshalObjectArray2(wireFormat, info.BrokerPath, dataOut, bs);
    TightMarshalLong2(wireFormat, info.Arrival, dataOut, bs);
    TightMarshalString2(info.UserID, dataOut, bs);
    bs.ReadBoolean();

    }
  }
}
