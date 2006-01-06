//
// Marshalling code for Open Wire Format for ConsumerId
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;

using OpenWire.Core;

namespace OpenWire.Core.Commands
{
    public class ConsumerId : AbstractCommand
    {
        string connectionId;
        long sessionId;
        long consumerId;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override int GetCommandType() {
            return 1;
        }


        // Properties

        public string ConnectionId
        {
            get
            {
                return connectionId;
            }
            set
            {
                connectionId = value;
            }            
        }

        public long SessionId
        {
            get
            {
                return sessionId;
            }
            set
            {
                sessionId = value;
            }            
        }

        public long ConsumerIdValue
        {
            get
            {
                return consumerId;
            }
            set
            {
                consumerId = value;
            }            
        }

    }
}
