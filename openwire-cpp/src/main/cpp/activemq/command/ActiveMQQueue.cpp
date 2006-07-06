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
#include "activemq/command/ActiveMQQueue.hpp"

using namespace apache::activemq::command;

/*
 * 
 */
ActiveMQQueue::ActiveMQQueue()
   : ActiveMQDestination()
{
}

/*
 * 
 */
ActiveMQQueue::ActiveMQQueue(const char* name)
   : ActiveMQDestination(name)
{
}

/*
 * 
 */
ActiveMQQueue::~ActiveMQQueue()
{
}

/*
 * 
 */
unsigned char ActiveMQQueue::getDataStructureType()
{
    return ActiveMQQueue::TYPE ; 
}

/*
 * 
 */
p<string> ActiveMQQueue::getQueueName()
{
    return this->getPhysicalName() ;
}

/*
 * 
 */
int ActiveMQQueue::getDestinationType()
{
    return ActiveMQDestination::ACTIVEMQ_QUEUE ;
}

/*
 * 
 */
p<ActiveMQDestination> ActiveMQQueue::createDestination(const char* name)
{
    p<ActiveMQQueue> queue = new ActiveMQQueue(name) ;
    return queue ;
}
