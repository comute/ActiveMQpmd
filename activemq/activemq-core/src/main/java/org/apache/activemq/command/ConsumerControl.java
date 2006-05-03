/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

/**
 * Used to start and stop transports as well as terminating clients.
 * 
 * @openwire:marshaller code="17"
 * 
 * @version $Revision: 1.1 $
 */
public class ConsumerControl extends BaseCommand {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.CONSUMER_CONTROL;

    protected ConsumerId consumerId;
    protected boolean close;
    protected int prefetch;
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    

   

    public Response visit(CommandVisitor visitor) throws Exception {
    return null;
    }





    /**
     * @openwire:property version=1
     * @return Returns the close.
     */
    public boolean isClose(){
        return close;
    }





    /**
     * @param close The close to set.
     */
    public void setClose(boolean close){
        this.close=close;
    }





    /**
     * @openwire:property version=1
     * @return Returns the consumerId.
     */
    public ConsumerId getConsumerId(){
        return consumerId;
    }





    /**
     * @param consumerId The consumerId to set.
     */
    public void setConsumerId(ConsumerId consumerId){
        this.consumerId=consumerId;
    }





    /**
     * @openwire:property version=1
     * @return Returns the prefetch.
     */
    public int getPrefetch(){
        return prefetch;
    }





    /**
     * @param prefetch The prefetch to set.
     */
    public void setPrefetch(int prefetch){
        this.prefetch=prefetch;
    }
}
