/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activemq;


/**
 * The core lifecyle interface for ActiveMQ components.
 *  
 * If there was a standard way to do so, it'd be good to register this 
 * interface with Spring so it treats the start/stop methods as those of
 * {@link org.springframework.beans.factory.InitializingBean} 
 * and {@link org.springframework.beans.factory.DisposableBean}
 * 
 * @version $Revision: 1.1 $
 */
public interface Service {

    public void start() throws Exception;
    
    public void stop() throws Exception;
    
}
