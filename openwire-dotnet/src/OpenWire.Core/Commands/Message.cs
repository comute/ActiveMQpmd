//
// Marshalling code for Open Wire Format for Message
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
    public class Message : AbstractCommand
    {
        ProducerId producerId;
        ActiveMQDestination destination;
        TransactionId transactionId;
        ActiveMQDestination originalDestination;
        MessageId messageId;
        TransactionId originalTransactionId;
        string groupID;
        int groupSequence;
        string correlationId;
        bool persistent;
        long expiration;
        byte priority;
        ActiveMQDestination replyTo;
        long timestamp;
        string type;
        byte[] content;
        byte[] marshalledProperties;
        Command dataStructure;
        ConsumerId targetConsumerId;
        bool compressed;
        int redeliveryCounter;
        BrokerId[] brokerPath;
        long arrival;
        string userID;
        bool recievedByDFBridge;



        // TODO generate Equals method
        // TODO generate HashCode method
        // TODO generate ToString method


        public override int GetCommandType() {
            return 1;
        }


        // Properties


        public ProducerId ProducerId
        {
            get
            {
                return producerId;
            }
            set
            {
                producerId = value;
            }            
        }

        public ActiveMQDestination Destination
        {
            get
            {
                return destination;
            }
            set
            {
                destination = value;
            }            
        }

        public TransactionId TransactionId
        {
            get
            {
                return transactionId;
            }
            set
            {
                transactionId = value;
            }            
        }

        public ActiveMQDestination OriginalDestination
        {
            get
            {
                return originalDestination;
            }
            set
            {
                originalDestination = value;
            }            
        }

        public MessageId MessageId
        {
            get
            {
                return messageId;
            }
            set
            {
                messageId = value;
            }            
        }

        public TransactionId OriginalTransactionId
        {
            get
            {
                return originalTransactionId;
            }
            set
            {
                originalTransactionId = value;
            }            
        }

        public string GroupID
        {
            get
            {
                return groupID;
            }
            set
            {
                groupID = value;
            }            
        }

        public int GroupSequence
        {
            get
            {
                return groupSequence;
            }
            set
            {
                groupSequence = value;
            }            
        }

        public string CorrelationId
        {
            get
            {
                return correlationId;
            }
            set
            {
                correlationId = value;
            }            
        }

        public bool Persistent
        {
            get
            {
                return persistent;
            }
            set
            {
                persistent = value;
            }            
        }

        public long Expiration
        {
            get
            {
                return expiration;
            }
            set
            {
                expiration = value;
            }            
        }

        public byte Priority
        {
            get
            {
                return priority;
            }
            set
            {
                priority = value;
            }            
        }

        public ActiveMQDestination ReplyTo
        {
            get
            {
                return replyTo;
            }
            set
            {
                replyTo = value;
            }            
        }

        public long Timestamp
        {
            get
            {
                return timestamp;
            }
            set
            {
                timestamp = value;
            }            
        }

        public string Type
        {
            get
            {
                return type;
            }
            set
            {
                type = value;
            }            
        }

        public byte[] Content
        {
            get
            {
                return content;
            }
            set
            {
                content = value;
            }            
        }

        public byte[] MarshalledProperties
        {
            get
            {
                return marshalledProperties;
            }
            set
            {
                marshalledProperties = value;
            }            
        }

        public Command DataStructure
        {
            get
            {
                return dataStructure;
            }
            set
            {
                dataStructure = value;
            }            
        }

        public ConsumerId TargetConsumerId
        {
            get
            {
                return targetConsumerId;
            }
            set
            {
                targetConsumerId = value;
            }            
        }

        public bool Compressed
        {
            get
            {
                return compressed;
            }
            set
            {
                compressed = value;
            }            
        }

        public int RedeliveryCounter
        {
            get
            {
                return redeliveryCounter;
            }
            set
            {
                redeliveryCounter = value;
            }            
        }

        public BrokerId[] BrokerPath
        {
            get
            {
                return brokerPath;
            }
            set
            {
                brokerPath = value;
            }            
        }

        public long Arrival
        {
            get
            {
                return arrival;
            }
            set
            {
                arrival = value;
            }            
        }

        public string UserID
        {
            get
            {
                return userID;
            }
            set
            {
                userID = value;
            }            
        }

        public bool RecievedByDFBridge
        {
            get
            {
                return recievedByDFBridge;
            }
            set
            {
                recievedByDFBridge = value;
            }            
        }

    }
}
