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
#ifndef TransactionInfo_hpp_
#define TransactionInfo_hpp_

#include <string>

/* we could cut this down  - for now include all possible headers */
#include "command/BaseCommand.hpp"
#include "command/BrokerId.hpp"
#include "command/ConnectionId.hpp"
#include "command/ConsumerId.hpp"
#include "command/ProducerId.hpp"
#include "command/SessionId.hpp"

#include "command/BaseCommand.hpp"
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
        using namespace apache::activemq::client;

/*
 *
 *  Marshalling code for Open Wire Format for TransactionInfo
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
class TransactionInfo : public BaseCommand
{
private:
    p<ConnectionId> connectionId ;
    p<TransactionId> transactionId ;
    byte type ;

public:
    const static int TYPE = 7;

public:
    TransactionInfo() ;
    virtual ~TransactionInfo() ;


    virtual p<ConnectionId> getConnectionId() ;
    virtual void setConnectionId(p<ConnectionId> connectionId) ;

    virtual p<TransactionId> getTransactionId() ;
    virtual void setTransactionId(p<TransactionId> transactionId) ;

    virtual byte getType() ;
    virtual void setType(byte type) ;


} ;

/* namespace */
      }
    }
  }
}

#endif /*TransactionInfo_hpp_*/
