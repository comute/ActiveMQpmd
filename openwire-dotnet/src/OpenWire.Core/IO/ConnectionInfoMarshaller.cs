//
// Marshalling code for Open Wire Format for ConnectionInfo
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
    public class ConnectionInfoMarshaller : AbstractCommandMarshaller
    {

        public override Command CreateCommand() {
            return new ConnectionInfo();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);
            ConnectionInfo info = (ConnectionInfo) command;
            info.setConnectionId((org.apache.activemq.command.ConnectionId) readObject(dataIn));
            info.setClientId(dataIn.readUTF());
            info.setPassword(dataIn.readUTF());
            info.setUserName(dataIn.readUTF());
            info.setBrokerPath((org.apache.activemq.command.BrokerId[]) readObject(dataIn));

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);
            ConnectionInfo info = (ConnectionInfo) command;
            writeObject(info.getConnectionId(), dataOut);
            writeUTF(info.getClientId(), dataOut);
            writeUTF(info.getPassword(), dataOut);
            writeUTF(info.getUserName(), dataOut);
            writeObject(info.getBrokerPath(), dataOut);

        }
    }
}
