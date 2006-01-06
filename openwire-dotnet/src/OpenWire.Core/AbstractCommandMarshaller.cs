using System;
using System.IO;

namespace OpenWire.Core
{
	/// <summary>
	/// A base class with useful implementation inheritence methods 
	/// for creating marshallers of the OpenWire protocol
	/// </summary>
	public abstract class AbstractCommandMarshaller {

        public abstract Command CreateCommand();

        public virtual void BuildCommand(Command command, BinaryReader dataIn) 
        {
        }
        
        public virtual void WriteCommand(Command command, BinaryWriter dataOut)
        {
        }
        
        protected virtual BrokerId ReadBrokerId(BinaryReader dataIn)
        {
        	   return brokerIDMarshaller.ReadCommand();
        }
        
        protected virtual void WriteBrokerId(BrokerId command, BinaryWriter dataOut)
        {
            brokerIDMarshaller.WriteCommand(command, dataOut);
        }
        
	}
}

