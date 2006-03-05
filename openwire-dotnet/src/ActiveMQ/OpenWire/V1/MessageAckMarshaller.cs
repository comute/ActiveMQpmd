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

using ActiveMQ.Commands;
using ActiveMQ.OpenWire;
using ActiveMQ.OpenWire.V1;

namespace ActiveMQ.OpenWire.V1
{
  //
  //  Marshalling code for Open Wire Format for MessageAck
  //
  //
  //  NOTE!: This file is autogenerated - do not modify!
  //        if you need to make a change, please see the Groovy scripts in the
  //        activemq-core module
  //
  public class MessageAckMarshaller : BaseCommandMarshaller
  {


    public override DataStructure CreateObject() 
    {
        return new MessageAck();
    }

    public override byte GetDataStructureType() 
    {
        return MessageAck.ID_MessageAck;
    }

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void TightUnmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.TightUnmarshal(wireFormat, o, dataIn, bs);

        MessageAck info = (MessageAck)o;
        info.Destination = (ActiveMQDestination) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.TransactionId = (TransactionId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.ConsumerId = (ConsumerId) TightUnmarshalCachedObject(wireFormat, dataIn, bs);
        info.AckType = BaseDataStreamMarshaller.ReadByte(dataIn);
        info.FirstMessageId = (MessageId) TightUnmarshalNestedObject(wireFormat, dataIn, bs);
        info.LastMessageId = (MessageId) TightUnmarshalNestedObject(wireFormat, dataIn, bs);
        info.MessageCount = BaseDataStreamMarshaller.ReadInt(dataIn);

    }


    //
    // Write the booleans that this object uses to a BooleanStream
    //
    public override int TightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) {
        MessageAck info = (MessageAck)o;

        int rc = base.TightMarshal1(wireFormat, info, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.Destination, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.TransactionId, bs);
    rc += TightMarshalCachedObject1(wireFormat, info.ConsumerId, bs);
        rc += TightMarshalNestedObject1(wireFormat, info.FirstMessageId, bs);
    rc += TightMarshalNestedObject1(wireFormat, info.LastMessageId, bs);
    
        return rc + 5;
    }

    // 
    // Write a object instance to data output stream
    //
    public override void TightMarshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.TightMarshal2(wireFormat, o, dataOut, bs);

        MessageAck info = (MessageAck)o;
    TightMarshalCachedObject2(wireFormat, info.Destination, dataOut, bs);
    TightMarshalCachedObject2(wireFormat, info.TransactionId, dataOut, bs);
    TightMarshalCachedObject2(wireFormat, info.ConsumerId, dataOut, bs);
    BaseDataStreamMarshaller.WriteByte(info.AckType, dataOut);
    TightMarshalNestedObject2(wireFormat, info.FirstMessageId, dataOut, bs);
    TightMarshalNestedObject2(wireFormat, info.LastMessageId, dataOut, bs);
    BaseDataStreamMarshaller.WriteInt(info.MessageCount, dataOut);

    }
  }
}
