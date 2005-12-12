/**
* <a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>
*
* Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com
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
*
**/
package org.activemq.transport;

import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.FutureTask;

import org.activemq.command.Response;
import org.activemq.util.IOExceptionSupport;

import java.io.IOException;
import java.io.InterruptedIOException;

public class FutureResponse extends FutureTask {
    
    private static final Callable EMPTY_CALLABLE = new Callable() {
        public Object call() throws Exception {
            return null;
        }};
    
    public FutureResponse() {
        super(EMPTY_CALLABLE);
    }

    public synchronized Response getResult() throws IOException {
        try {
            return (Response) super.get();
        } catch (InterruptedException e) {
            throw new InterruptedIOException("Interrupted.");
        } catch (ExecutionException e) {
            Throwable target = e.getCause();
            if( target instanceof IOException ) {
                throw (IOException)target;
            } else {
                throw IOExceptionSupport.create(target);
            }
        }
    }
    
    public synchronized void set(Object result) {
        super.set(result);
    }
}
