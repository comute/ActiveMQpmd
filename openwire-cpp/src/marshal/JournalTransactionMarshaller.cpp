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
#include "marshal/JournalTransactionMarshaller.hpp"

using namespace apache::activemq::client::marshal;

/*
 *  Marshalling code for Open Wire Format for JournalTransaction
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-core module
 */

JournalTransactionMarshaller::JournalTransactionMarshaller()
{
    // no-op
}

JournalTransactionMarshaller::~JournalTransactionMarshaller()
{
    // no-op
}



DataStructure* JournalTransactionMarshaller::createObject() 
{
    return new JournalTransaction();
}

byte JournalTransactionMarshaller::getDataStructureType() 
{
    return JournalTransaction.ID_JournalTransaction;
}

    /* 
     * Un-marshal an object instance from the data input stream
     */ 
void JournalTransactionMarshaller::unmarshal(OpenWireFormat& wireFormat, Object o, BinaryReader& dataIn, BooleanStream& bs) 
{
    base.unmarshal(wireFormat, o, dataIn, bs);

    JournalTransaction& info = (JournalTransaction&) o;
        info.setTransactionId((TransactionId) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setType(dataIn.readByte());
        info.setWasPrepared(bs.readBoolean());

}


/*
 * Write the booleans that this object uses to a BooleanStream
 */
int JournalTransactionMarshaller::marshal1(OpenWireFormat& wireFormat, Object& o, BooleanStream& bs) {
    JournalTransaction& info = (JournalTransaction&) o;

    int rc = base.marshal1(wireFormat, info, bs);
    rc += marshal1NestedObject(wireFormat, info.getTransactionId(), bs);
        bs.writeBoolean(info.getWasPrepared());

    return rc + 1;
}

/* 
 * Write a object instance to data output stream
 */
void JournalTransactionMarshaller::marshal2(OpenWireFormat& wireFormat, Object& o, BinaryWriter& dataOut, BooleanStream& bs) {
    base.marshal2(wireFormat, o, dataOut, bs);

    JournalTransaction& info = (JournalTransaction&) o;
    marshal2NestedObject(wireFormat, info.getTransactionId(), dataOut, bs);
    DataStreamMarshaller.writeByte(info.getType(), dataOut);
    bs.readBoolean();

}
