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
package org.apache.activemq.network.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
/**
 * A Destination bridge is used to bridge between to different JMS systems
 * 
 * @version $Revision: 1.1.1.1 $
 */
class QueueBridge extends DestinationBridge{
    protected Queue consumerQueue;
    protected Queue producerQueue;
    protected QueueSession consumerSession;
    protected QueueSession producerSession;
   
    protected String selector;
    protected QueueSender producer;
    protected QueueConnection consumerConnection;
    protected QueueConnection producerConnection;
    protected JmsQueueConnector jmsQueueConnector;

    public void stop() throws Exception{
        super.stop();
        if(consumerSession!=null){
            consumerSession.close();
        }
        if(producerSession!=null){
            producerSession.close();
        }
    }
    
    protected void setJmsQueueConnector(JmsQueueConnector connector){
        this.jmsQueueConnector = connector;
    }

    protected MessageConsumer createConsumer() throws JMSException{
        // set up the consumer
        consumerSession=consumerConnection.createQueueSession(false,Session.CLIENT_ACKNOWLEDGE);
        producerSession=producerConnection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer=null;
        
            if(selector!=null&&selector.length()>0){
                consumer=consumerSession.createReceiver(consumerQueue,selector);
            }else{
                consumer=consumerSession.createReceiver(consumerQueue);
            }
       
        return consumer;
    }
    
    protected MessageProducer createProducer() throws JMSException{
        producer = producerSession.createSender(null);
        return producer;
    }
    
        
    protected Destination processReplyToDestination (Destination destination){
        Queue queue = (Queue)destination;
        return jmsQueueConnector.createReplyToQueueBridge(queue, getConsumerConnection(), getProducerConnection());
    }
    
    
    
    protected void sendMessage(Message message) throws JMSException{
        producer.send(producerQueue,message);
    }

    /**
     * @return Returns the consumerConnection.
     */
    public QueueConnection getConsumerConnection(){
        return consumerConnection;
    }

    /**
     * @param consumerConnection The consumerConnection to set.
     */
    public void setConsumerConnection(QueueConnection consumerConnection){
        this.consumerConnection=consumerConnection;
    }

    /**
     * @return Returns the consumerQueue.
     */
    public Queue getConsumerQueue(){
        return consumerQueue;
    }

    /**
     * @param consumerQueue The consumerQueue to set.
     */
    public void setConsumerQueue(Queue consumerQueue){
        this.consumerQueue=consumerQueue;
    }

    /**
     * @return Returns the producerConnection.
     */
    public QueueConnection getProducerConnection(){
        return producerConnection;
    }

    /**
     * @param producerConnection The producerConnection to set.
     */
    public void setProducerConnection(QueueConnection producerConnection){
        this.producerConnection=producerConnection;
    }

    /**
     * @return Returns the producerQueue.
     */
    public Queue getProducerQueue(){
        return producerQueue;
    }

    /**
     * @param producerQueue The producerQueue to set.
     */
    public void setProducerQueue(Queue producerQueue){
        this.producerQueue=producerQueue;
    }

    /**
     * @return Returns the selector.
     */
    public String getSelector(){
        return selector;
    }

    /**
     * @param selector The selector to set.
     */
    public void setSelector(String selector){
        this.selector=selector;
    }
    
  
}