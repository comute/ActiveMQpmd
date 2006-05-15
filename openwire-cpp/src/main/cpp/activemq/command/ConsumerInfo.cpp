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
#include "activemq/command/ConsumerInfo.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Command and marshalling code for OpenWire format for ConsumerInfo
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
ConsumerInfo::ConsumerInfo()
{
    this->consumerId = NULL ;
    this->browser = false ;
    this->destination = NULL ;
    this->prefetchSize = 0 ;
    this->maximumPendingMessageLimit = 0 ;
    this->dispatchAsync = false ;
    this->selector = NULL ;
    this->subcriptionName = NULL ;
    this->noLocal = false ;
    this->exclusive = false ;
    this->retroactive = false ;
    this->priority = 0 ;
    this->brokerPath = NULL ;
    this->additionalPredicate = NULL ;
    this->networkSubscription = false ;
    this->optimizedAcknowledge = false ;
    this->noRangeAcks = false ;
}

ConsumerInfo::~ConsumerInfo()
{
}

unsigned char ConsumerInfo::getDataStructureType()
{
    return ConsumerInfo::TYPE ; 
}

        
p<ConsumerId> ConsumerInfo::getConsumerId()
{
    return consumerId ;
}

void ConsumerInfo::setConsumerId(p<ConsumerId> consumerId)
{
    this->consumerId = consumerId ;
}

        
bool ConsumerInfo::getBrowser()
{
    return browser ;
}

void ConsumerInfo::setBrowser(bool browser)
{
    this->browser = browser ;
}

        
p<ActiveMQDestination> ConsumerInfo::getDestination()
{
    return destination ;
}

void ConsumerInfo::setDestination(p<ActiveMQDestination> destination)
{
    this->destination = destination ;
}

        
int ConsumerInfo::getPrefetchSize()
{
    return prefetchSize ;
}

void ConsumerInfo::setPrefetchSize(int prefetchSize)
{
    this->prefetchSize = prefetchSize ;
}

        
int ConsumerInfo::getMaximumPendingMessageLimit()
{
    return maximumPendingMessageLimit ;
}

void ConsumerInfo::setMaximumPendingMessageLimit(int maximumPendingMessageLimit)
{
    this->maximumPendingMessageLimit = maximumPendingMessageLimit ;
}

        
bool ConsumerInfo::getDispatchAsync()
{
    return dispatchAsync ;
}

void ConsumerInfo::setDispatchAsync(bool dispatchAsync)
{
    this->dispatchAsync = dispatchAsync ;
}

        
p<string> ConsumerInfo::getSelector()
{
    return selector ;
}

void ConsumerInfo::setSelector(p<string> selector)
{
    this->selector = selector ;
}

        
p<string> ConsumerInfo::getSubcriptionName()
{
    return subcriptionName ;
}

void ConsumerInfo::setSubcriptionName(p<string> subcriptionName)
{
    this->subcriptionName = subcriptionName ;
}

        
bool ConsumerInfo::getNoLocal()
{
    return noLocal ;
}

void ConsumerInfo::setNoLocal(bool noLocal)
{
    this->noLocal = noLocal ;
}

        
bool ConsumerInfo::getExclusive()
{
    return exclusive ;
}

void ConsumerInfo::setExclusive(bool exclusive)
{
    this->exclusive = exclusive ;
}

        
bool ConsumerInfo::getRetroactive()
{
    return retroactive ;
}

void ConsumerInfo::setRetroactive(bool retroactive)
{
    this->retroactive = retroactive ;
}

        
char ConsumerInfo::getPriority()
{
    return priority ;
}

void ConsumerInfo::setPriority(char priority)
{
    this->priority = priority ;
}

        
array<BrokerId> ConsumerInfo::getBrokerPath()
{
    return brokerPath ;
}

void ConsumerInfo::setBrokerPath(array<BrokerId> brokerPath)
{
    this->brokerPath = brokerPath ;
}

        
p<BooleanExpression> ConsumerInfo::getAdditionalPredicate()
{
    return additionalPredicate ;
}

void ConsumerInfo::setAdditionalPredicate(p<BooleanExpression> additionalPredicate)
{
    this->additionalPredicate = additionalPredicate ;
}

        
bool ConsumerInfo::getNetworkSubscription()
{
    return networkSubscription ;
}

void ConsumerInfo::setNetworkSubscription(bool networkSubscription)
{
    this->networkSubscription = networkSubscription ;
}

        
bool ConsumerInfo::getOptimizedAcknowledge()
{
    return optimizedAcknowledge ;
}

void ConsumerInfo::setOptimizedAcknowledge(bool optimizedAcknowledge)
{
    this->optimizedAcknowledge = optimizedAcknowledge ;
}

        
bool ConsumerInfo::getNoRangeAcks()
{
    return noRangeAcks ;
}

void ConsumerInfo::setNoRangeAcks(bool noRangeAcks)
{
    this->noRangeAcks = noRangeAcks ;
}

int ConsumerInfo::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)
{
    int size = 0 ;

    size += BaseCommand::marshal(marshaller, mode, ostream) ; 
    size += marshaller->marshalObject(consumerId, mode, ostream) ; 
    size += marshaller->marshalBoolean(browser, mode, ostream) ; 
    size += marshaller->marshalObject(destination, mode, ostream) ; 
    size += marshaller->marshalInt(prefetchSize, mode, ostream) ; 
    size += marshaller->marshalInt(maximumPendingMessageLimit, mode, ostream) ; 
    size += marshaller->marshalBoolean(dispatchAsync, mode, ostream) ; 
    size += marshaller->marshalString(selector, mode, ostream) ; 
    size += marshaller->marshalString(subcriptionName, mode, ostream) ; 
    size += marshaller->marshalBoolean(noLocal, mode, ostream) ; 
    size += marshaller->marshalBoolean(exclusive, mode, ostream) ; 
    size += marshaller->marshalBoolean(retroactive, mode, ostream) ; 
    size += marshaller->marshalByte(priority, mode, ostream) ; 
    size += marshaller->marshalObjectArray(brokerPath, mode, ostream) ; 
    size += marshaller->marshalObject(additionalPredicate, mode, ostream) ; 
    size += marshaller->marshalBoolean(networkSubscription, mode, ostream) ; 
    size += marshaller->marshalBoolean(optimizedAcknowledge, mode, ostream) ; 
    size += marshaller->marshalBoolean(noRangeAcks, mode, ostream) ; 
    return size ;
}

void ConsumerInfo::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)
{
    BaseCommand::unmarshal(marshaller, mode, istream) ; 
    consumerId = p_cast<ConsumerId>(marshaller->unmarshalObject(mode, istream)) ; 
    browser = (marshaller->unmarshalBoolean(mode, istream)) ; 
    destination = p_cast<ActiveMQDestination>(marshaller->unmarshalObject(mode, istream)) ; 
    prefetchSize = (marshaller->unmarshalInt(mode, istream)) ; 
    maximumPendingMessageLimit = (marshaller->unmarshalInt(mode, istream)) ; 
    dispatchAsync = (marshaller->unmarshalBoolean(mode, istream)) ; 
    selector = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
    subcriptionName = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
    noLocal = (marshaller->unmarshalBoolean(mode, istream)) ; 
    exclusive = (marshaller->unmarshalBoolean(mode, istream)) ; 
    retroactive = (marshaller->unmarshalBoolean(mode, istream)) ; 
    priority = (marshaller->unmarshalByte(mode, istream)) ; 
    brokerPath = array_cast<BrokerId>(marshaller->unmarshalObjectArray(mode, istream)) ; 
    additionalPredicate = p_cast<BooleanExpression>(marshaller->unmarshalObject(mode, istream)) ; 
    networkSubscription = (marshaller->unmarshalBoolean(mode, istream)) ; 
    optimizedAcknowledge = (marshaller->unmarshalBoolean(mode, istream)) ; 
    noRangeAcks = (marshaller->unmarshalBoolean(mode, istream)) ; 
}
