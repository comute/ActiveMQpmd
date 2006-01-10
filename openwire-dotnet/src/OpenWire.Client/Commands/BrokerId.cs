//
// Marshalling code for Open Wire Format for BrokerId
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;

using OpenWire.Client;
using OpenWire.Client.Core;

namespace OpenWire.Client.Commands
{
    public class BrokerId : AbstractCommand
    {
    			public const byte ID_BrokerId = 124;
    			
        string value;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_BrokerId;
        }


        // Properties

        public string Value
        {
            get { return value; }
            set { this.value = value; }            
        }

    }
}
