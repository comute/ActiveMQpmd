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
using System.IO;

using OpenWire.Core;
using OpenWire.Core.Commands;
using OpenWire.Core.IO;

namespace OpenWire.Core.IO
{
    public class JournalTraceMarshaller : AbstractCommandMarshaller
    {


        public override Command CreateCommand() {
            return new JournalTrace();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            JournalTrace info = (JournalTrace) command;
            info.Message = dataIn.ReadString();

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            JournalTrace info = (JournalTrace) command;
            dataOut.Write(info.Message);

        }
    }
}
