//
// Marshalling code for Open Wire Format for ActiveMQTopic
//
//
// NOTE!: This file is autogenerated - do not modify!
//        if you need to make a change, please see the Groovy scripts in the
//        activemq-openwire module
//

using System;
using System.Collections;
using System.IO;

using OpenWire.Core;
using OpenWire.Core.Commands;
using OpenWire.Core.IO;

namespace OpenWire.Core.IO
{
    public class ActiveMQTopicMarshaller : AbstractCommandMarshaller
    {

        public override Command CreateCommand() {
            return new ActiveMQTopic();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            super.buildCommand(command, dataIn);
            ActiveMQTopic info = (ActiveMQTopic) command;

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            super.writeCommand(command, dataOut);
            ActiveMQTopic info = (ActiveMQTopic) command;

        }
    }
}
