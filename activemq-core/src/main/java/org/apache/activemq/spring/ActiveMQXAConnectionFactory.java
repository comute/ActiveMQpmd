/*
 * Copyright 2005-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.spring;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * A <a href="http://www.springframework.org/">Spring</a> enhanced XA connection
 * factory which will automatically use the Spring bean name as the clientIDPrefix property
 * so that connections created have client IDs related to your Spring.xml file for
 * easier comprehension from <a href="http://incubator.apache.org/activemq/jmx.html">JMX</a>.
 * 
 * @version $Revision: $
 */
public class ActiveMQXAConnectionFactory extends org.apache.activemq.ActiveMQXAConnectionFactory implements InitializingBean, BeanNameAware {

    private String beanName;
    private boolean useBeanNameAsClientIdPrefix;
    
    public void afterPropertiesSet() throws Exception {
        if (isUseBeanNameAsClientIdPrefix() && getClientIDPrefix() == null) {
            setClientIDPrefix(getBeanName());
        }
    }

    public String getBeanName() {
        return beanName;
    }
    
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isUseBeanNameAsClientIdPrefix() {
        return useBeanNameAsClientIdPrefix;
    }

    public void setUseBeanNameAsClientIdPrefix(boolean useBeanNameAsClientIdPrefix) {
        this.useBeanNameAsClientIdPrefix = useBeanNameAsClientIdPrefix;
    }
}
