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
#ifndef ConsumerInfo_hpp_
#define ConsumerInfo_hpp_

#include <string>
#include "command/BaseCommand.hpp"
    
#include "command/ConsumerId.hpp"
#include "command/ActiveMQDestination.hpp"
#include "command/BrokerId.hpp"

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
 *  Marshalling code for Open Wire Format for ConsumerInfo
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
class ConsumerInfo : public BaseCommand
{
private:
    p<ConsumerId> consumerId ;
    bool browser ;
    p<ActiveMQDestination> destination ;
    int prefetchSize ;
    bool dispatchAsync ;
    p<string> selector ;
    p<string> subcriptionName ;
    bool noLocal ;
    bool exclusive ;
    bool retroactive ;
    char priority ;
    ap<BrokerId> brokerPath ;
    bool networkSubscription ;

public:
    const static int TYPE = 5;

public:
    ConsumerInfo() ;
    virtual ~ConsumerInfo() ;

    virtual int getCommandType() ;

    virtual p<ConsumerId> getConsumerId() ;
    virtual void setConsumerId(p<ConsumerId> consumerId) ;

    virtual bool getBrowser() ;
    virtual void setBrowser(bool browser) ;

    virtual p<ActiveMQDestination> getDestination() ;
    virtual void setDestination(p<ActiveMQDestination> destination) ;

    virtual int getPrefetchSize() ;
    virtual void setPrefetchSize(int prefetchSize) ;

    virtual bool getDispatchAsync() ;
    virtual void setDispatchAsync(bool dispatchAsync) ;

    virtual p<string> getSelector() ;
    virtual void setSelector(p<string> selector) ;

    virtual p<string> getSubcriptionName() ;
    virtual void setSubcriptionName(p<string> subcriptionName) ;

    virtual bool getNoLocal() ;
    virtual void setNoLocal(bool noLocal) ;

    virtual bool getExclusive() ;
    virtual void setExclusive(bool exclusive) ;

    virtual bool getRetroactive() ;
    virtual void setRetroactive(bool retroactive) ;

    virtual char getPriority() ;
    virtual void setPriority(char priority) ;

    virtual ap<BrokerId> getBrokerPath() ;
    virtual void setBrokerPath(ap<BrokerId> brokerPath) ;

    virtual bool getNetworkSubscription() ;
    virtual void setNetworkSubscription(bool networkSubscription) ;


} ;

/* namespace */
      }
    }
  }
}

#endif /*ConsumerInfo_hpp_*/
