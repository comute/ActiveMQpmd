/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
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
package org.apache.activemq.tool;

import org.apache.activemq.tool.properties.JmsClientSystemProperties;
import org.apache.activemq.tool.properties.JmsProducerSystemProperties;
import org.apache.activemq.tool.properties.JmsProducerProperties;
import org.apache.activemq.tool.properties.JmsClientProperties;
import org.apache.activemq.tool.sampler.ThroughputSamplerTask;

import javax.jms.JMSException;
import java.util.Properties;

public class JmsProducerSystem extends AbstractJmsClientSystem {
    protected JmsProducerSystemProperties sysTest = new JmsProducerSystemProperties();
    protected JmsProducerProperties producer = new JmsProducerProperties();

    public JmsClientSystemProperties getSysTest() {
        return sysTest;
    }

    public void setSysTest(JmsClientSystemProperties sysTestProps) {
        sysTest = (JmsProducerSystemProperties)sysTestProps;
    }

    public JmsClientProperties getJmsClientProperties() {
        return getProducer();
    }

    public JmsProducerProperties getProducer() {
        return producer;
    }

    public void setProducer(JmsProducerProperties producer) {
        this.producer = producer;
    }

    protected void runJmsClient(String clientName, int clientDestIndex, int clientDestCount) {
        ThroughputSamplerTask sampler = getTpSampler();

        JmsProducerClient producerClient = new JmsProducerClient(producer, jmsConnFactory);
        producerClient.setClientName(clientName);

        if (sampler != null) {
            sampler.registerClient(producerClient);
        }

        try {
            producerClient.sendMessages(clientDestIndex, clientDestCount);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        for (int i=0; i<args.length; i++) {
            int index = args[i].indexOf("=");
            String key = args[i].substring(0, index);
            String val = args[i].substring(index + 1);
            props.setProperty(key, val);
        }

        JmsProducerSystem sys = new JmsProducerSystem();
        sys.configureProperties(props);

        try {
            sys.runSystemTest();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
