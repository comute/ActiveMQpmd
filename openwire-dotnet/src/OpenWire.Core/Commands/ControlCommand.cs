//
// Marshalling code for Open Wire Format for ControlCommand
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
    public class ControlCommand : BaseCommand
    {
    			public const byte ID_ControlCommand = 14;
    			
        string command;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override byte GetCommandType() {
            return ID_ControlCommand;
        }


        // Properties

        public string Command
        {
            get { return command; }
            set { this.command = value; }            
        }

    }
}
