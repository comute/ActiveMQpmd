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
#include "activemq/command/MessageDispatch.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Command and marshalling code for OpenWire format for MessageDispatch
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
MessageDispatch::MessageDispatch()
{
    this->consumerId = NULL ;
    this->destination = NULL ;
    this->message = NULL ;
    this->redeliveryCounter = 0 ;
}

MessageDispatch::~MessageDispatch()
{
}

unsigned char MessageDispatch::getDataStructureType()
{
    return MessageDispatch::TYPE ; 
}

        
p<ConsumerId> MessageDispatch::getConsumerId()
{
    return consumerId ;
}

void MessageDispatch::setConsumerId(p<ConsumerId> consumerId)
{
    this->consumerId = consumerId ;
}

        
p<ActiveMQDestination> MessageDispatch::getDestination()
{
    return destination ;
}

void MessageDispatch::setDestination(p<ActiveMQDestination> destination)
{
    this->destination = destination ;
}

        
p<Message> MessageDispatch::getMessage()
{
    return message ;
}

void MessageDispatch::setMessage(p<Message> message)
{
    this->message = message ;
}

        
int MessageDispatch::getRedeliveryCounter()
{
    return redeliveryCounter ;
}

void MessageDispatch::setRedeliveryCounter(int redeliveryCounter)
{
    this->redeliveryCounter = redeliveryCounter ;
}

int MessageDispatch::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)
{
    int size = 0 ;

    size += BaseCommand::marshal(marshaller, mode, ostream) ; 
    size += marshaller->marshalObject(consumerId, mode, ostream) ; 
    size += marshaller->marshalObject(destination, mode, ostream) ; 
    size += marshaller->marshalObject(message, mode, ostream) ; 
    size += marshaller->marshalInt(redeliveryCounter, mode, ostream) ; 
    return size ;
}

void MessageDispatch::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)
{
    BaseCommand::unmarshal(marshaller, mode, istream) ; 
    consumerId = p_cast<ConsumerId>(marshaller->unmarshalObject(mode, istream)) ; 
    destination = p_cast<ActiveMQDestination>(marshaller->unmarshalObject(mode, istream)) ; 
    message = p_cast<Message>(marshaller->unmarshalObject(mode, istream)) ; 
    redeliveryCounter = (marshaller->unmarshalInt(mode, istream)) ; 
}
