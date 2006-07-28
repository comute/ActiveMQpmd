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
#include "activemq/command/ExceptionResponse.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Command and marshalling code for OpenWire format for ExceptionResponse
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
ExceptionResponse::ExceptionResponse()
{
    this->exception = NULL ;
}

ExceptionResponse::~ExceptionResponse()
{
}

unsigned char ExceptionResponse::getDataStructureType()
{
    return ExceptionResponse::TYPE ; 
}

        
p<BrokerError> ExceptionResponse::getException()
{
    return exception ;
}

void ExceptionResponse::setException(p<BrokerError> exception)
{
    this->exception = exception ;
}

int ExceptionResponse::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)
{
    int size = 0 ;

    size += Response::marshal(marshaller, mode, ostream) ; 
    size += marshaller->marshalObject(exception, mode, ostream) ; 
    return size ;
}

void ExceptionResponse::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)
{
    Response::unmarshal(marshaller, mode, istream) ; 
    exception = p_cast<BrokerError>(marshaller->unmarshalObject(mode, istream)) ; 
}
