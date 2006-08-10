/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "marshal/ActiveMQMessageMarshaller.hpp"

using namespace apache::activemq::client::marshal;

/*
 *  Marshalling code for Open Wire Format for ActiveMQMessage
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-core module
 */

ActiveMQMessageMarshaller::ActiveMQMessageMarshaller()
{
    // no-op
}

ActiveMQMessageMarshaller::~ActiveMQMessageMarshaller()
{
    // no-op
}



IDataStructure* ActiveMQMessageMarshaller::createObject() 
{
    return new ActiveMQMessage();
}

char ActiveMQMessageMarshaller::getDataStructureType() 
{
    return ActiveMQMessage.ID_ActiveMQMessage;
}

    /* 
     * Un-marshal an object instance from the data input stream
     */ 
void ActiveMQMessageMarshaller::unmarshal(ProtocolFormat& wireFormat, Object o, BinaryReader& dataIn, BooleanStream& bs) 
{
    base.unmarshal(wireFormat, o, dataIn, bs);

    ActiveMQMessage& info = (ActiveMQMessage&) o;

    info.beforeUnmarshall(wireFormat);
        

    info.afterUnmarshall(wireFormat);

}


/*
 * Write the booleans that this object uses to a BooleanStream
 */
int ActiveMQMessageMarshaller::marshal1(ProtocolFormat& wireFormat, Object& o, BooleanStream& bs) {
    ActiveMQMessage& info = (ActiveMQMessage&) o;

    info.beforeMarshall(wireFormat);

    int rc = base.marshal1(wireFormat, info, bs);

    return rc + 0;
}

/* 
 * Write a object instance to data output stream
 */
void ActiveMQMessageMarshaller::marshal2(ProtocolFormat& wireFormat, Object& o, BinaryWriter& dataOut, BooleanStream& bs) {
    base.marshal2(wireFormat, o, dataOut, bs);

    ActiveMQMessage& info = (ActiveMQMessage&) o;

    info.afterMarshall(wireFormat);

}
