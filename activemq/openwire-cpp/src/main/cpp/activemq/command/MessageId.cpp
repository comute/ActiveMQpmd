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
#include "activemq/command/MessageId.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Marshalling code for Open Wire Format for MessageId
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
MessageId::MessageId()
{
    this->producerId = NULL ;
    this->producerSequenceId = 0 ;
    this->brokerSequenceId = 0 ;
}

MessageId::~MessageId()
{
}

unsigned char MessageId::getDataStructureType()
{
    return MessageId::TYPE ; 
}

        
p<ProducerId> MessageId::getProducerId()
{
    return producerId ;
}

void MessageId::setProducerId(p<ProducerId> producerId)
{
    this->producerId = producerId ;
}

        
long long MessageId::getProducerSequenceId()
{
    return producerSequenceId ;
}

void MessageId::setProducerSequenceId(long long producerSequenceId)
{
    this->producerSequenceId = producerSequenceId ;
}

        
long long MessageId::getBrokerSequenceId()
{
    return brokerSequenceId ;
}

void MessageId::setBrokerSequenceId(long long brokerSequenceId)
{
    this->brokerSequenceId = brokerSequenceId ;
}

int MessageId::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> writer) throw (IOException)
{
    int size = 0 ;

    size += marshaller->marshalObject(producerId, mode, writer) ; 
    size += marshaller->marshalLong(producerSequenceId, mode, writer) ; 
    size += marshaller->marshalLong(brokerSequenceId, mode, writer) ; 
    return size ;
}

void MessageId::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> reader) throw (IOException)
{
    producerId = p_cast<ProducerId>(marshaller->unmarshalObject(mode, reader)) ; 
    producerSequenceId = (marshaller->unmarshalLong(mode, reader)) ; 
    brokerSequenceId = (marshaller->unmarshalLong(mode, reader)) ; 
}
