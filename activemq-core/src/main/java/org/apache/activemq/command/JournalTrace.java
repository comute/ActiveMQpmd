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

/**
 * 
 * @openwire:marshaller code="53"
 * @version $Revision: 1.6 $
 */
public class JournalTrace extends DataStructureSupport implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE=CommandTypes.JOURNAL_TRACE;

    private String message;
    
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @openwire:property version=1
     */
    public String getMessage() {
        return message;
    }

    /**
     * @openwire:property version=1
     */
    public void setMessage(String message) {
        this.message = message;
    }    

    public boolean isMarshallAware() {
        return false;
    }
}
