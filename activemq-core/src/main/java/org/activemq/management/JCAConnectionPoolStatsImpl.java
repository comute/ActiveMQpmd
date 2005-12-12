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
package org.activemq.management;


/**
 * Statistics for a JCA connection pool
 *
 * @version $Revision: 1.2 $
 */
public class JCAConnectionPoolStatsImpl extends JCAConnectionStatsImpl {
    private CountStatisticImpl closeCount;
    private CountStatisticImpl createCount;
    private BoundedRangeStatisticImpl freePoolSize;
    private BoundedRangeStatisticImpl poolSize;
    private RangeStatisticImpl waitingThreadCount;

    public JCAConnectionPoolStatsImpl(String connectionFactory, String managedConnectionFactory, TimeStatisticImpl waitTime, TimeStatisticImpl useTime, CountStatisticImpl closeCount, CountStatisticImpl createCount, BoundedRangeStatisticImpl freePoolSize, BoundedRangeStatisticImpl poolSize, RangeStatisticImpl waitingThreadCount) {
        super(connectionFactory, managedConnectionFactory, waitTime, useTime);
        this.closeCount = closeCount;
        this.createCount = createCount;
        this.freePoolSize = freePoolSize;
        this.poolSize = poolSize;
        this.waitingThreadCount = waitingThreadCount;

        // lets add named stats
        addStatistic("freePoolSize", freePoolSize);
        addStatistic("poolSize", poolSize);
        addStatistic("waitingThreadCount", waitingThreadCount);
    }

    public CountStatisticImpl getCloseCount() {
        return closeCount;
    }

    public CountStatisticImpl getCreateCount() {
        return createCount;
    }

    public BoundedRangeStatisticImpl getFreePoolSize() {
        return freePoolSize;
    }

    public BoundedRangeStatisticImpl getPoolSize() {
        return poolSize;
    }

    public RangeStatisticImpl getWaitingThreadCount() {
        return waitingThreadCount;
    }

}
