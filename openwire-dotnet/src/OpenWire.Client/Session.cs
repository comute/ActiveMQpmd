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
using System;
using OpenWire.Client.Commands;
using OpenWire.Client.Core;
using System.Collections;

namespace OpenWire.Client
{
    /// <summary>
    /// Default provider of ISession
    /// </summary>
    public class Session : ISession
    {
        private Connection connection;
        private SessionInfo info;
        private AcknowledgementMode acknowledgementMode;
        private long consumerCounter;
        private long producerCounter;
        private int prefetchSize = 1000;
        private IDictionary consumers = new Hashtable();
        
        public Session(Connection connection, SessionInfo info, AcknowledgementMode acknowledgementMode)
        {
            this.connection = connection;
            this.info = info;
            this.acknowledgementMode = acknowledgementMode;
        }
        
        public void Dispose()
        {
            DisposeOf(info.SessionId);
        }
        
        public IMessageProducer CreateProducer()
        {
            return CreateProducer(null);
        }
        
        public IMessageProducer CreateProducer(IDestination destination)
        {
            ProducerInfo command = CreateProducerInfo(destination);
            connection.SyncRequest(command);
            return new MessageProducer(this, command);
        }
        
        
        
        public IMessageConsumer CreateConsumer(IDestination destination)
        {
            return CreateConsumer(destination, null);
        }
        
        public IMessageConsumer CreateConsumer(IDestination destination, string selector)
        {
            ConsumerInfo command = CreateConsumerInfo(destination, selector);
            ConsumerId consumerId = command.ConsumerId;
            
            try
            {
                MessageConsumer consumer = new MessageConsumer(this, command, acknowledgementMode);
                // lets register the consumer first in case we start dispatching messages immediately
                connection.AddConsumer(consumerId, consumer);
                
                connection.SyncRequest(command);
                
                consumers[consumerId] = consumer;
                return consumer;
            }
            catch (Exception e)
            {
                connection.RemoveConsumer(consumerId);
                throw e;
            }
        }
        
        public IMessageConsumer CreateDurableConsumer(ITopic destination, string name, string selector, bool noLocal)
        {
            ConsumerInfo command = CreateConsumerInfo(destination, selector);
            ConsumerId consumerId = command.ConsumerId;
            command.SubcriptionName = name;
            command.NoLocal = noLocal;
            
            try
            {
                MessageConsumer consumer = new MessageConsumer(this, command, acknowledgementMode);
                // lets register the consumer first in case we start dispatching messages immediately
                connection.AddConsumer(consumerId, consumer);
                
                connection.SyncRequest(command);
                
                consumers[consumerId] = consumer;
                return consumer;
            }
            catch (Exception e)
            {
                connection.RemoveConsumer(consumerId);
                throw e;
            }
        }
        
        public IQueue GetQueue(string name)
        {
            return new ActiveMQQueue(name);
        }
        
        public ITopic GetTopic(string name)
        {
            return new ActiveMQTopic(name);
        }
        
        public ITemporaryQueue CreateTemporaryQueue()
        {
            return new ActiveMQTempQueue(connection.CreateTemporaryDestinationName());
        }
        
        public ITemporaryTopic CreateTemporaryTopic()
        {
            return new ActiveMQTempTopic(connection.CreateTemporaryDestinationName());
        }
        
        
        
        public IMessage CreateMessage()
        {
            ActiveMQMessage answer = new ActiveMQMessage();
            Configure(answer);
            return answer;
        }
        
        
        public ITextMessage CreateTextMessage()
        {
            ActiveMQTextMessage answer = new ActiveMQTextMessage();
            Configure(answer);
            return answer;
        }
        
        public ITextMessage CreateTextMessage(string text)
        {
            ActiveMQTextMessage answer = new ActiveMQTextMessage(text);
            Configure(answer);
            return answer;
        }
        
        public IMapMessage CreateMapMessage()
        {
            return new ActiveMQMapMessage();
        }
        
        public IBytesMessage CreateBytesMessage()
        {
            return new ActiveMQBytesMessage();
        }
        
        public IBytesMessage CreateBytesMessage(byte[] body)
        {
            ActiveMQBytesMessage answer = new ActiveMQBytesMessage();
            answer.Content = body;
            return answer;
        }
        
        
        // Properties
        public Connection Connection
        {
            get {
                return connection;
            }
        }
        
        // Implementation methods
        public void DoSend(IDestination destination, IMessage message)
        {
            ActiveMQMessage command = ActiveMQMessage.Transform(message);
            // TODO complete packet
            connection.SyncRequest(command);
        }
        
        public void DisposeOf(DataStructure objectId)
        {
            // TODO dispose of all the session first?
            RemoveInfo command = new RemoveInfo();
            command.ObjectId = objectId;
            connection.SyncRequest(command);
        }
        
        public void DisposeOf(ConsumerId objectId)
        {
            consumers.Remove(objectId);
            connection.RemoveConsumer(objectId);
            RemoveInfo command = new RemoveInfo();
            command.ObjectId = objectId;
            connection.SyncRequest(command);
        }
        
        public void DispatchAsyncMessages(object state)
        {
            // lets iterate through each consumer created by this session
            // ensuring that they have all pending messages dispatched
            lock (this)
            {
                // lets ensure that only 1 thread dispatches messages in a consumer at once
                
                foreach (MessageConsumer consumer in consumers.Values)
                {
                    consumer.DispatchAsyncMessages();
                }
            }
        }
        
        
        protected virtual ConsumerInfo CreateConsumerInfo(IDestination destination, string selector)
        {
            ConsumerInfo answer = new ConsumerInfo();
            ConsumerId id = new ConsumerId();
            id.ConnectionId = info.SessionId.ConnectionId;
            id.SessionId = info.SessionId.Value;
            lock (this)
            {
                id.Value = ++consumerCounter;
            }
            answer.ConsumerId = id;
            answer.Destination = ActiveMQDestination.Transform(destination);
            answer.Selector = selector;
            answer.PrefetchSize = prefetchSize;
            
            // TODO configure other features on the consumer
            return answer;
        }
        
        protected virtual ProducerInfo CreateProducerInfo(IDestination destination)
        {
            ProducerInfo answer = new ProducerInfo();
            ProducerId id = new ProducerId();
            id.ConnectionId = info.SessionId.ConnectionId;
            id.SessionId = info.SessionId.Value;
            lock (this)
            {
                id.Value = ++producerCounter;
            }
            answer.ProducerId = id;
            answer.Destination = ActiveMQDestination.Transform(destination);
            return answer;
        }
        
        /// <summary>
        /// Configures the message command
        /// </summary>
        protected void Configure(ActiveMQMessage message)
        {
        }
    }
}
