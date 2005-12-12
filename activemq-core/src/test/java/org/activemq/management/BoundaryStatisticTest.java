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

public class BoundaryStatisticTest extends StatisticTestSupport {
    
    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(BoundaryStatisticTest.class);

    /**
     * Use case for BoundaryStatisticImpl class.
     * @throws Exception
     */
    public void testStatistic() throws Exception {
        BoundaryStatisticImpl stat = new BoundaryStatisticImpl("myBoundaryStat", "seconds", "myBoundaryStatDesc", 1000, 2000);
        assertStatistic(stat, "myBoundaryStat", "seconds", "myBoundaryStatDesc");

        assertEquals(1000, stat.getLowerBound());
        assertEquals(2000, stat.getUpperBound());

        log.info("Stat is: " + stat);
    }
}
