/**
 *
 * Copyright 2004 The Apache Software Foundation
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
package org.activemq.broker;

import java.io.IOException;

import junit.framework.Test;

import org.activeio.command.WireFormat;
import org.activemq.command.Command;
import org.activemq.command.Response;
import org.activemq.openwire.OpenWireFormat;

/**
 * Runs against the broker but marshals all request and response commands.
 * 
 * @version $Revision$
 */
public class MarshallingBrokerTest extends BrokerTest {

    public WireFormat wireFormat = new OpenWireFormat();
    
    public void initCombos() {
        addCombinationValues( "wireFormat", new Object[]{ 
                new OpenWireFormat(true), 
                new OpenWireFormat(false),
                });        
    }
    
    protected StubConnection createConnection() throws Exception {
        return new StubConnection(broker) {
            public Response request(Command command) throws Throwable {
                Response r = super.request((Command) wireFormat.unmarshal(wireFormat.marshal(command)));
                if( r != null ) {
                    r = (Response) wireFormat.unmarshal(wireFormat.marshal(r));
                }
                return r;
            }
            public void send(Command command) throws Throwable {
                super.send((Command) wireFormat.unmarshal(wireFormat.marshal(command)));
            }
            protected void dispatch(Command command) throws InterruptedException, IOException {
                super.dispatch((Command) wireFormat.unmarshal(wireFormat.marshal(command)));
            };
        };
    }
    public static Test suite() {
        return suite(MarshallingBrokerTest.class);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    

}
