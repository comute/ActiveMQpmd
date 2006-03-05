//
//
// Copyright 2005-2006 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

// Marshalling code for Open Wire Format for ExceptionResponse
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;
using System.IO;

using ActiveMQ.Commands;
using ActiveMQ.OpenWire;
using ActiveMQ.OpenWire.V1;

namespace ActiveMQ.OpenWire.V1
{
    public class MarshallerFactory
    {
        public void configure(OpenWireFormat format) 
        {

            format.addMarshaller(new MessageIdMarshaller());
            format.addMarshaller(new BrokerInfoMarshaller());
            format.addMarshaller(new ActiveMQTempQueueMarshaller());
            format.addMarshaller(new LocalTransactionIdMarshaller());
            format.addMarshaller(new RemoveSubscriptionInfoMarshaller());
            format.addMarshaller(new IntegerResponseMarshaller());
            format.addMarshaller(new ActiveMQQueueMarshaller());
            format.addMarshaller(new DestinationInfoMarshaller());
            format.addMarshaller(new ActiveMQBytesMessageMarshaller());
            format.addMarshaller(new ShutdownInfoMarshaller());
            format.addMarshaller(new DataResponseMarshaller());
            format.addMarshaller(new SessionIdMarshaller());
            format.addMarshaller(new DataArrayResponseMarshaller());
            format.addMarshaller(new JournalQueueAckMarshaller());
            format.addMarshaller(new WireFormatInfoMarshaller());
            format.addMarshaller(new ResponseMarshaller());
            format.addMarshaller(new ConnectionErrorMarshaller());
            format.addMarshaller(new ActiveMQObjectMessageMarshaller());
            format.addMarshaller(new ConsumerInfoMarshaller());
            format.addMarshaller(new ActiveMQTempTopicMarshaller());
            format.addMarshaller(new ConnectionIdMarshaller());
            format.addMarshaller(new DiscoveryEventMarshaller());
            format.addMarshaller(new ConnectionInfoMarshaller());
            format.addMarshaller(new KeepAliveInfoMarshaller());
            format.addMarshaller(new XATransactionIdMarshaller());
            format.addMarshaller(new JournalTraceMarshaller());
            format.addMarshaller(new FlushCommandMarshaller());
            format.addMarshaller(new ConsumerIdMarshaller());
            format.addMarshaller(new JournalTopicAckMarshaller());
            format.addMarshaller(new ActiveMQTextMessageMarshaller());
            format.addMarshaller(new BrokerIdMarshaller());
            format.addMarshaller(new MessageDispatchMarshaller());
            format.addMarshaller(new ProducerInfoMarshaller());
            format.addMarshaller(new SubscriptionInfoMarshaller());
            format.addMarshaller(new ActiveMQMapMessageMarshaller());
            format.addMarshaller(new MessageDispatchNotificationMarshaller());
            format.addMarshaller(new SessionInfoMarshaller());
            format.addMarshaller(new ActiveMQMessageMarshaller());
            format.addMarshaller(new TransactionInfoMarshaller());
            format.addMarshaller(new ActiveMQStreamMessageMarshaller());
            format.addMarshaller(new MessageAckMarshaller());
            format.addMarshaller(new ProducerIdMarshaller());
            format.addMarshaller(new ActiveMQTopicMarshaller());
            format.addMarshaller(new JournalTransactionMarshaller());
            format.addMarshaller(new RemoveInfoMarshaller());
            format.addMarshaller(new ControlCommandMarshaller());
            format.addMarshaller(new ExceptionResponseMarshaller());
    	}
    }
}
