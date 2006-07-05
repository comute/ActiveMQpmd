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
#ifndef ActiveMQ_ExceptionResponse_hpp_
#define ActiveMQ_ExceptionResponse_hpp_

// Turn off warning message for ignored exception specification
#ifdef _MSC_VER
#pragma warning( disable : 4290 )
#endif

#include <string>
#include "activemq/command/Response.hpp"
#include "activemq/command/BrokerError.hpp"

#include "activemq/protocol/IMarshaller.hpp"
#include "ppr/io/IOutputStream.hpp"
#include "ppr/io/IInputStream.hpp"
#include "ppr/io/IOException.hpp"
#include "ppr/util/ifr/array"
#include "ppr/util/ifr/p"

namespace apache
{
  namespace activemq
  {
    namespace command
    {
      using namespace ifr;
      using namespace std;
      using namespace apache::activemq;
      using namespace apache::activemq::protocol;
      using namespace apache::ppr::io;

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
class ExceptionResponse : public Response
{
protected:
    p<BrokerError> exception ;

public:
    const static unsigned char TYPE = 31;

public:
    ExceptionResponse() ;
    virtual ~ExceptionResponse() ;

    virtual unsigned char getDataStructureType() ;

    virtual p<BrokerError> getException() ;
    virtual void setException(p<BrokerError> exception) ;

    virtual int marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException) ;
    virtual void unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException) ;
} ;

/* namespace */
    }
  }
}

#endif /*ActiveMQ_ExceptionResponse_hpp_*/
