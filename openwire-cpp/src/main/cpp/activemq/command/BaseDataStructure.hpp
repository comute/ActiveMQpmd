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
#ifndef ActiveMQ_BaseDataStructure_hpp_
#define ActiveMQ_BaseDataStructure_hpp_

#include <string>
#include "activemq/IDataStructure.hpp"
#include "ppr/io/IOutputStream.hpp"
#include "ppr/io/IInputStream.hpp"
#include "ppr/io/IOException.hpp"
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
      using namespace apache::ppr::io;

/*
 * 
 */
class BaseDataStructure : public IDataStructure
{
protected:
    BaseDataStructure() { } ;

public:
    virtual unsigned char getDataStructureType() ;

    virtual int marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw(IOException) ;
    virtual void unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw(IOException) ;

    static p<IDataStructure> createObject(unsigned char type) ;
    static p<string> getDataStructureTypeAsString(unsigned char type) ;
} ;

/* namespace */
    }
  }
}

#endif /*ActiveMQ_BaseDataStructure_hpp_*/
