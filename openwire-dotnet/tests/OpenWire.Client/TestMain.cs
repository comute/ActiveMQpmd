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
using System.IO;

using OpenWire.Client;
using OpenWire.Client.Core;
using OpenWire.Client.Commands;

namespace openwire_dotnet
{
    public class TestMain
    {
        public static void Main(string[] args)
        {
            try
            {
                Console.WriteLine("About to connect to ActiveMQ");
                
                IConnectionFactory factory = new ConnectionFactory("localhost", 61616);
                
                Console.WriteLine("Worked!");
                
                using (IConnection connection = factory.CreateConnection())
                {
                    Console.WriteLine("Created a connection!");
                    
                    ISession session = connection.CreateSession();
                    Console.WriteLine("Created a session: " + session);
                    
                    IDestination destination = session.GetQueue("FOO.BAR");
                    Console.WriteLine("Using destination: " + destination);
                    
                    IMessageConsumer consumer = session.CreateConsumer(destination);
                    
                    IMessageProducer producer = session.CreateProducer(destination);
                    string expected = "Hello World!";
                    
                    
                    ITextMessage request = session.CreateTextMessage(expected);
                    
                    producer.Send(request);
                    
                    Console.WriteLine("### About to receive message...");
                    ActiveMQTextMessage message = (ActiveMQTextMessage) consumer.Receive();
                    if (message == null)
                    {
                        Console.WriteLine("### No message!!");
                    }
                    else
                    {
                        Console.WriteLine("### Received message: " + message + " of type: " + message.GetType());
                        String actual = message.Text;
                        
                        Console.WriteLine("### Message text is: " + actual);
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Caught: " + e);
                Console.WriteLine("Stack: " + e.StackTrace);
            }
        }
    }
}
