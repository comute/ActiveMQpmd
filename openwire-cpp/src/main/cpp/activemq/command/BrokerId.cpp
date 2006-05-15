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
#include "activemq/command/BrokerId.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Command and marshalling code for OpenWire format for BrokerId
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
BrokerId::BrokerId()
{
    this->value = NULL ;
}

BrokerId::~BrokerId()
{
}

unsigned char BrokerId::getDataStructureType()
{
    return BrokerId::TYPE ; 
}

        
p<string> BrokerId::getValue()
{
    return value ;
}

void BrokerId::setValue(p<string> value)
{
    this->value = value ;
}

int BrokerId::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)
{
    int size = 0 ;

    size += BaseDataStructure::marshal(marshaller, mode, ostream) ; 
    size += marshaller->marshalString(value, mode, ostream) ; 
    return size ;
}

void BrokerId::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)
{
    BaseDataStructure::unmarshal(marshaller, mode, istream) ; 
    value = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
}
