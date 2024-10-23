/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.management;

/**
 * A long statistic implementation
 */
public class LongStatisticImpl extends UnsampledStatisticImpl implements LongStatistic, Resettable {

    // Note: [AMQ-8463] Adding volatile to 'value' would be more accurate, but performance impact
    private Long value = null;

    public LongStatisticImpl(String name, String description) {
        this(name, "value", description);
    }

    public LongStatisticImpl(String name, String unit, String description) {
        super(name, unit, description);
    }

    @Override
    public void reset() {
        if (isDoReset()) {
            value = null;
        }
    }

    @Override
    public Long getValue() {
        return (value != null ? value : 0l);
    }

    public void setValue(Long value) {
        if (isEnabled()) {
            this.value = value; 
        }
    }

    protected void appendFieldDescription(StringBuffer buffer) {
        buffer.append(" value: ");
        buffer.append(value);
        super.appendFieldDescription(buffer);
    }
}
