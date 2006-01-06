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

using OpenWire.Core;

namespace OpenWire.Core.Commands
{
    public class XATransactionId : AbstractCommand
    {
        int formatId;
        byte[] globalTransactionId;
        byte[] branchQualifier;



        // TODO generate Equals method
        // TODO generate GetHashCode method
        // TODO generate ToString method


        public override int GetCommandType() {
            return 1;
        }


        // Properties

        public int FormatId
        {
            get
            {
                return formatId;
            }
            set
            {
                formatId = value;
            }            
        }

        public byte[] GlobalTransactionId
        {
            get
            {
                return globalTransactionId;
            }
            set
            {
                globalTransactionId = value;
            }            
        }

        public byte[] BranchQualifier
        {
            get
            {
                return branchQualifier;
            }
            set
            {
                branchQualifier = value;
            }            
        }

    }
}
