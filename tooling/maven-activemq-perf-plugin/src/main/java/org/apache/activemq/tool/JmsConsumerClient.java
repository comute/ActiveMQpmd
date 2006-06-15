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

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Topic;

public class JmsConsumerClient extends JmsPerformanceSupport {
    private static final Log log = LogFactory.getLog(JmsConsumerClient.class);

    private static final String PREFIX_CONFIG_CONSUMER = "consumer.";
    public static final String TIME_BASED_RECEIVING = "time";
    public static final String COUNT_BASED_RECEIVING = "count";

    protected Properties jmsConsumerSettings = new Properties();
    protected MessageConsumer jmsConsumer;

    protected boolean durable = false;
    protected boolean asyncRecv = true;

    protected long recvCount = 1000000;       // Receive a million messages by default
    protected long recvDuration = 5 * 60 * 1000; // Receive for 5 mins by default
    protected String recvType = TIME_BASED_RECEIVING;

    public void receiveMessages() throws JMSException {
        if (listener != null) {
            listener.onConfigEnd(this);
        }
        if (isAsyncRecv()) {
            receiveAsyncMessages();
        } else {
            receiveSyncMessages();
        }
    }

    public void receiveSyncMessages() throws JMSException {
        if (getJmsConsumer() == null) {
            createJmsConsumer();
        }

        try {
            getConnection().start();
            if (listener != null) {
                listener.onConsumeStart(this);
            }
            if (getRecvType().equalsIgnoreCase(TIME_BASED_RECEIVING)) {
                long endTime = System.currentTimeMillis() + getRecvDuration();
                while (System.currentTimeMillis() < endTime) {
                    getJmsConsumer().receive();
                    incThroughput();
                }
            } else {
                int count = 0;
                while (count < getRecvCount()) {
                    getJmsConsumer().receive();
                    incThroughput();
                    count++;
                }
            }
        } finally {
            if (listener != null) {
                listener.onConsumeEnd(this);
            }
            getConnection().close();
        }
    }

    public void receiveAsyncMessages() throws JMSException {
        if (getJmsConsumer() == null) {
            createJmsConsumer();
        }

        if (getRecvType().equalsIgnoreCase(TIME_BASED_RECEIVING)) {
            getJmsConsumer().setMessageListener(new MessageListener() {
                public void onMessage(Message msg) {
                    incThroughput();
                }
            });

            try {
                getConnection().start();
                if (listener != null) {
                    listener.onConsumeStart(this);
                }
                try {
                    Thread.sleep(getRecvDuration());
                } catch (InterruptedException e) {
                    throw new JMSException("JMS consumer thread sleep has been interrupted. Message: " + e.getMessage());
                }
            } finally {
                if (listener != null) {
                    listener.onConsumeEnd(this);
                }
                getConnection().close();
            }
        } else {
            final AtomicInteger count = new AtomicInteger(0);
            getJmsConsumer().setMessageListener(new MessageListener() {
                public void onMessage(Message msg) {
                    incThroughput();
                    count.incrementAndGet();
                    count.notify();
                }
            });

            try {
                getConnection().start();
                if (listener != null) {
                    listener.onConsumeStart(this);
                }
                try {
                    while (count.get() < getRecvCount()) {
                        count.wait();
                    }
                } catch (InterruptedException e) {
                    throw new JMSException("JMS consumer thread wait has been interrupted. Message: " + e.getMessage());
                }
            } finally {
                if (listener != null) {
                    listener.onConsumeEnd(this);
                }
                getConnection().close();
            }
        }
    }
    
    public Connection getConnection() throws JMSException {
    	Connection c = super.getConnection();
    	if (c.getClientID() == null && isDurable()) {
    		c.setClientID(getClientName());
    	}
    	return c;
    }
    
    public MessageConsumer createJmsConsumer() throws JMSException {
        Destination[] dest = createDestination();
        return createJmsConsumer(dest[0]);
    }

    public MessageConsumer createJmsConsumer(Destination dest) throws JMSException {
        if (isDurable()) {
            String clientName = getClientName();
            if (clientName == null) {
                clientName = "JmsConsumer";
            }
            jmsConsumer = getSession().createDurableSubscriber((Topic) dest, clientName);
        } else {
            jmsConsumer = getSession().createConsumer(dest);
        }
        return jmsConsumer;
    }

    public MessageConsumer createJmsConsumer(Destination dest, String selector, boolean noLocal) throws JMSException {
        if (isDurable()) {
            String clientName = getClientName();
            if (clientName == null) {
                clientName = "JmsConsumer";
            }
            jmsConsumer = getSession().createDurableSubscriber((Topic) dest, clientName, selector, noLocal);
        } else {
            jmsConsumer = getSession().createConsumer(dest, selector, noLocal);
        }
        return jmsConsumer;
    }

    public MessageConsumer getJmsConsumer() {
        return jmsConsumer;
    }

    public Properties getJmsConsumerSettings() {
        return jmsConsumerSettings;
    }

    public void setJmsConsumerSettings(Properties jmsConsumerSettings) {
        this.jmsConsumerSettings = jmsConsumerSettings;
        ReflectionUtil.configureClass(this, jmsConsumerSettings);
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isAsyncRecv() {
        return asyncRecv;
    }

    public void setAsyncRecv(boolean asyncRecv) {
        this.asyncRecv = asyncRecv;
    }

    public long getRecvCount() {
        return recvCount;
    }

    public void setRecvCount(long recvCount) {
        this.recvCount = recvCount;
    }

    public long getRecvDuration() {
        return recvDuration;
    }

    public void setRecvDuration(long recvDuration) {
        this.recvDuration = recvDuration;
    }

    public String getRecvType() {
        return recvType;
    }

    public void setRecvType(String recvType) {
        this.recvType = recvType;
    }

    public Properties getSettings() {
        Properties allSettings = new Properties(jmsConsumerSettings);
        allSettings.putAll(super.getSettings());
        return allSettings;
    }

    public void setSettings(Properties settings) {
        super.setSettings(settings);
        ReflectionUtil.configureClass(this, jmsConsumerSettings);
    }

    public void setProperty(String key, String value) {
        if (key.startsWith(PREFIX_CONFIG_CONSUMER)) {
            jmsConsumerSettings.setProperty(key, value);
        } else {
            super.setProperty(key, value);
        }
    }

    public static void main(String[] args) throws JMSException {

        Properties samplerSettings = new Properties();
        Properties consumerSettings = new Properties();

        for (int i = 0; i < args.length; i++) {
            // Get property define options only
            int index = args[i].indexOf("=");
            String key = args[i].substring(0, index);
            String val = args[i].substring(index + 1);
            if (key.startsWith("sampler.")) {
                samplerSettings.setProperty(key, val);
            } else {
                consumerSettings.setProperty(key, val);
            }

        }

        JmsConsumerClient client = new JmsConsumerClient();
        client.setSettings(consumerSettings);

        PerfMeasurementTool sampler = new PerfMeasurementTool();
        sampler.setSamplerSettings(samplerSettings);
        sampler.registerClient(client);
        sampler.startSampler();

        client.setPerfEventListener(sampler);
        client.receiveMessages();
    }
}
