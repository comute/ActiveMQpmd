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
package org.apache.activemq.filter;

import java.util.List;

import org.apache.activemq.filter.DestinationMap;

/**
 * Represents a destination based policy
 * 
 * @version $Revision: 1.1 $
 */
public class DummyPolicy extends DestinationMap {

    protected Class getEntryClass() {
        return DummyPolicyEntry.class;
    }

    public void setEntries(List entries) {
        super.setEntries(entries);
    }
    
}
