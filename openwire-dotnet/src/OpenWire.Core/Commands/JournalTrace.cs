//
// Marshalling code for Open Wire Format for JournalTrace
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
    public class JournalTrace : AbstractCommand
    {
    			public const byte ID_JournalTrace = 53;
    			
        string message;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_JournalTrace;
        }


        // Properties

        public string Message
        {
            get
            {
                return message;
            }
            set
            {
                message = value;
            }            
        }

    }
}
