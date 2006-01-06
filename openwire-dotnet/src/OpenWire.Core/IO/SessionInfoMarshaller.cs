//
// Marshalling code for Open Wire Format for SessionInfo
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
    public class SessionInfoMarshaller : AbstractCommandMarshaller
    {


        public override Command CreateCommand() {
            return new SessionInfo();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            SessionInfo info = (SessionInfo) command;
            info.SessionId = ReadSessionId(dataIn);

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            SessionInfo info = (SessionInfo) command;
            WriteSessionId(info.SessionId, dataOut);

        }
    }
}
