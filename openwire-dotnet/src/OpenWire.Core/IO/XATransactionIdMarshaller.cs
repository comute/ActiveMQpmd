//
// Marshalling code for Open Wire Format for XATransactionId
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
    public class XATransactionIdMarshaller : AbstractCommandMarshaller
    {

        public override Command CreateCommand() {
            return new XATransactionId();
        }

        public override void BuildCommand(Command command, BinaryReader dataIn) {
            base.BuildCommand(command, dataIn);
            XATransactionId info = (XATransactionId) command;
            info.setFormatId(dataIn.readInt());
            info.setGlobalTransactionId((byte[]) readObject(dataIn));
            info.setBranchQualifier((byte[]) readObject(dataIn));

        }

        public override void WriteCommand(Command command, BinaryWriter dataOut) {
            base.WriteCommand(command, dataOut);
            XATransactionId info = (XATransactionId) command;
            dataOut.writeInt(info.getFormatId());
            writeObject(info.getGlobalTransactionId(), dataOut);
            writeObject(info.getBranchQualifier(), dataOut);

        }
    }
}
