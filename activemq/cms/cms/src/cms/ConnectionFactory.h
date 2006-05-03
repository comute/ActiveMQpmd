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

#ifndef _CMS_CONNECTIONFACTORY_H_
#define _CMS_CONNECTIONFACTORY_H_
 
#include <cms/CMSException.h>

namespace cms{
	
	// Forward declarations.
	class Connection;
	
	/**
	 * A ConnectionFactory object encapsulates a set of connection 
	 * configuration parameters that has been defined by an administrator. 
	 * A client uses it to create a connection with a CMS provider.
	 */
	class ConnectionFactory{
		
	public:
	
		virtual ~ConnectionFactory(){}
		
		virtual Connection* createConnection() throw( CMSException ) = 0;
		virtual Connection* createConnection( const char* userName, 
			const char* password ) throw( CMSException ) = 0;
	};
}

#endif /*_CMS_CONNECTIONFACTORY_H_*/
