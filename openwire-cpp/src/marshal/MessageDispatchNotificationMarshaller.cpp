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
#include "marshal/MessageDispatchNotificationMarshaller.hpp"

using namespace apache::activemq::client::marshal;

/*
 *  Marshalling code for Open Wire Format for MessageDispatchNotification
 *
 * NOTE!: This file is autogenerated - do not modify!
 *        if you need to make a change, please see the Groovy scripts in the
 *        activemq-core module
 */

MessageDispatchNotificationMarshaller::MessageDispatchNotificationMarshaller()
{
    // no-op
}

MessageDispatchNotificationMarshaller::~MessageDispatchNotificationMarshaller()
{
    // no-op
}



DataStructure* MessageDispatchNotificationMarshaller::createObject() 
{
    return new MessageDispatchNotification();
}

byte MessageDispatchNotificationMarshaller::getDataStructureType() 
{
    return MessageDispatchNotification.ID_MessageDispatchNotification;
}

    /* 
     * Un-marshal an object instance from the data input stream
     */ 
void MessageDispatchNotificationMarshaller::unmarshal(OpenWireFormat& wireFormat, Object o, BinaryReader& dataIn, BooleanStream& bs) 
{
    base.unmarshal(wireFormat, o, dataIn, bs);

    MessageDispatchNotification& info = (MessageDispatchNotification&) o;
        info.setConsumerId((org.apache.activemq.command.ConsumerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDestination((org.apache.activemq.command.ActiveMQDestination) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDeliverySequenceId(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setMessageId((org.apache.activemq.command.MessageId) tightUnmarsalNestedObject(wireFormat, dataIn, bs));

}


/*
 * Write the booleans that this object uses to a BooleanStream
 */
int MessageDispatchNotificationMarshaller::marshal1(OpenWireFormat& wireFormat, Object& o, BooleanStream& bs) {
    MessageDispatchNotification& info = (MessageDispatchNotification&) o;

    int rc = base.marshal1(wireFormat, info, bs);
    rc += marshal1CachedObject(wireFormat, info.getConsumerId(), bs);
    rc += marshal1CachedObject(wireFormat, info.getDestination(), bs);
    rc += marshal1Long(wireFormat, info.getDeliverySequenceId(), bs);
    rc += marshal1NestedObject(wireFormat, info.getMessageId(), bs);

    return rc + 0;
}

/* 
 * Write a object instance to data output stream
 */
void MessageDispatchNotificationMarshaller::marshal2(OpenWireFormat& wireFormat, Object& o, BinaryWriter& dataOut, BooleanStream& bs) {
    base.marshal2(wireFormat, o, dataOut, bs);

    MessageDispatchNotification& info = (MessageDispatchNotification&) o;
    marshal2CachedObject(wireFormat, info.getConsumerId(), dataOut, bs);
    marshal2CachedObject(wireFormat, info.getDestination(), dataOut, bs);
    marshal2Long(wireFormat, info.getDeliverySequenceId(), dataOut, bs);
    marshal2NestedObject(wireFormat, info.getMessageId(), dataOut, bs);

}
