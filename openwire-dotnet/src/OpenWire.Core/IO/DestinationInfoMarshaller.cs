//
// Marshalling code for Open Wire Format for DestinationInfo
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
    public class DestinationInfoMarshaller : AbstractCommandMarshaller
    {

        public override Command CreateCommand() {
            return new DestinationInfo();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            super.buildCommand(command, dataIn);
            DestinationInfo info = (DestinationInfo) command;
            info.setConnectionId((org.apache.activemq.command.ConnectionId) readObject(dataIn));
            info.setDestination((org.apache.activemq.command.ActiveMQDestination) readObject(dataIn));
            info.setOperationType(dataIn.readByte());
            info.setTimeout(dataIn.readLong());
            info.setBrokerPath((org.apache.activemq.command.BrokerId[]) readObject(dataIn));

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            super.writeCommand(command, dataOut);
            DestinationInfo info = (DestinationInfo) command;
            writeObject(info.getConnectionId(), dataOut);
            writeObject(info.getDestination(), dataOut);
            dataOut.writeByte(info.getOperationType());
            dataOut.writeLong(info.getTimeout());
            writeObject(info.getBrokerPath(), dataOut);

        }
    }
}
