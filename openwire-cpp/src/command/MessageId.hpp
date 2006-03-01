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
#ifndef MessageId_hpp_
#define MessageId_hpp_

#include <string>
#include "command/AbstractCommand.hpp"
    
#include "command/ProducerId.hpp"

#include "util/ifr/ap"
#include "util/ifr/p"

namespace apache
{
  namespace activemq
  {
    namespace client
    {
      namespace command
      {
        using namespace ifr;
        using namespace std;
        using namespace apache::activemq::client;

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
class MessageId : public AbstractCommand
{
private:
    p<ProducerId> producerId ;
    long long producerSequenceId ;
    long long brokerSequenceId ;

public:
    const static int TYPE = 110;

public:
    MessageId() ;
    virtual ~MessageId() ;

    virtual int getCommandType() ;

    virtual p<ProducerId> getProducerId() ;
    virtual void setProducerId(p<ProducerId> producerId) ;

    virtual long long getProducerSequenceId() ;
    virtual void setProducerSequenceId(long long producerSequenceId) ;

    virtual long long getBrokerSequenceId() ;
    virtual void setBrokerSequenceId(long long brokerSequenceId) ;


} ;

/* namespace */
      }
    }
  }
}

#endif /*MessageId_hpp_*/
