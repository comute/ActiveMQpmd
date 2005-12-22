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
package org.activemq.store;

import java.io.IOException;

import org.activemq.Service;
import org.activemq.command.TransactionId;


/**
 * Represents the durable store of the commit/rollback operations taken against the
 * broker.
 *
 * @version $Revision: 1.2 $
 */
public interface TransactionStore extends Service {

    public void prepare(TransactionId txid) throws IOException;    
    public void commit(TransactionId txid, boolean wasPrepared) throws IOException;    
    public void rollback(TransactionId txid) throws IOException; 
    public void recover(TransactionRecoveryListener listener) throws IOException;
    
}
