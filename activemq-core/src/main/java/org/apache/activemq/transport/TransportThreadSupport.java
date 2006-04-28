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
package org.apache.activemq.transport;



/**
 * A useful base class for a transport implementation which has a background
 * reading thread.
 * 
 * @version $Revision: 1.1 $
 */
public abstract class TransportThreadSupport extends TransportSupport implements Runnable {

    private boolean daemon = false;
    private Thread runner;

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    protected void doStart() throws Exception {
        runner = new Thread(this, "ActiveMQ Transport: "+toString());
        runner.setDaemon(daemon);
        runner.start();
    }
}
