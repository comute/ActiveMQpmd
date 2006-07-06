/*
* Copyright 2006 The Apache Software Foundation or its licensors, as
* applicable.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-core module
//

using System;
using System.Collections;
using System.IO;

using ActiveMQ.Commands;
using ActiveMQ.OpenWire;
using ActiveMQ.OpenWire.V1;

namespace ActiveMQ.OpenWire.V1
{
  /// <summary>
  ///  Marshalling code for Open Wire Format for ConsumerControl
  /// </summary>
  class ConsumerControlMarshaller : BaseCommandMarshaller
  {


    public override DataStructure CreateObject() 
    {
        return new ConsumerControl();
    }

    public override byte GetDataStructureType() 
    {
        return ConsumerControl.ID_ConsumerControl;
    }

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void TightUnmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn, BooleanStream bs) 
    {
        base.TightUnmarshal(wireFormat, o, dataIn, bs);

        ConsumerControl info = (ConsumerControl)o;
        info.Close = bs.ReadBoolean();
        info.ConsumerId = (ConsumerId) TightUnmarshalNestedObject(wireFormat, dataIn, bs);
        info.Prefetch = dataIn.ReadInt32();

    }

    //
    // Write the booleans that this object uses to a BooleanStream
    //
    public override int TightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) {
        ConsumerControl info = (ConsumerControl)o;

        int rc = base.TightMarshal1(wireFormat, info, bs);
        bs.WriteBoolean(info.Close);
        rc += TightMarshalNestedObject1(wireFormat, (DataStructure)info.ConsumerId, bs);

        return rc + 4;
    }

    // 
    // Write a object instance to data output stream
    //
    public override void TightMarshal2(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut, BooleanStream bs) {
        base.TightMarshal2(wireFormat, o, dataOut, bs);

        ConsumerControl info = (ConsumerControl)o;
        bs.ReadBoolean();
        TightMarshalNestedObject2(wireFormat, (DataStructure)info.ConsumerId, dataOut, bs);
        dataOut.Write(info.Prefetch);

    }

    // 
    // Un-marshal an object instance from the data input stream
    // 
    public override void LooseUnmarshal(OpenWireFormat wireFormat, Object o, BinaryReader dataIn) 
    {
        base.LooseUnmarshal(wireFormat, o, dataIn);

        ConsumerControl info = (ConsumerControl)o;
        info.Close = dataIn.ReadBoolean();
        info.ConsumerId = (ConsumerId) LooseUnmarshalNestedObject(wireFormat, dataIn);
        info.Prefetch = dataIn.ReadInt32();

    }

    // 
    // Write a object instance to data output stream
    //
    public override void LooseMarshal(OpenWireFormat wireFormat, Object o, BinaryWriter dataOut) {

        ConsumerControl info = (ConsumerControl)o;

        base.LooseMarshal(wireFormat, o, dataOut);
        dataOut.Write(info.Close);
        LooseMarshalNestedObject(wireFormat, (DataStructure)info.ConsumerId, dataOut);
        dataOut.Write(info.Prefetch);

    }
    
  }
}
