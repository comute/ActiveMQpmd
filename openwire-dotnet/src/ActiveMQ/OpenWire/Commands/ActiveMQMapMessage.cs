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
using System.Collections;

using ActiveMQ;
using ActiveMQ.OpenWire;

namespace ActiveMQ.OpenWire.Commands
{
    public class ActiveMQMapMessage : ActiveMQMessage, IMapMessage
    {
        public const byte ID_ActiveMQMapMessage = 25;
        
        private PrimitiveMap body;
        
        
        public override byte GetDataStructureType()
        {
            return ID_ActiveMQMapMessage;
        }
        
        public IPrimitiveMap Body
        {
            get {
                if (body == null)
                {
                    body = PrimitiveMap.Unmarshal(Content);
                }
                return body;
            }
        }
        
        public override void BeforeMarshall(OpenWireFormat wireFormat)
        {
            if (body == null)
            {
                Content = null;
            }
            else
            {
                Content = body.Marshal();
            }
            
            Console.WriteLine("BeforeMarshalling, content is: " + Content);

            base.BeforeMarshall(wireFormat);
        }
        
    }
}
