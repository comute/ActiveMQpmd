//
// Marshalling code for Open Wire Format for BrokerInfo
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
    public class BrokerInfoMarshaller : AbstractCommandMarshaller
    {


        public override Command CreateCommand() {
            return new BrokerInfo();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);

            BrokerInfo info = (BrokerInfo) command;
            info.BrokerId = ReadBrokerId(dataIn);
            info.BrokerURL = dataIn.ReadString();
            info.PeerBrokerInfos = ReadBrokerInfo[](dataIn);
            info.BrokerName = dataIn.ReadString();

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);

            BrokerInfo info = (BrokerInfo) command;
            WriteBrokerId(info.BrokerId, dataOut);
            dataOut.Write(info.BrokerURL);
            WriteBrokerInfo[](info.PeerBrokerInfos, dataOut);
            dataOut.Write(info.BrokerName);

        }
    }
}
