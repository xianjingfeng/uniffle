/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.uniffle.test;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import org.apache.spark.SparkConf;
import org.apache.spark.shuffle.RssSparkConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import org.apache.uniffle.common.rpc.ServerType;
import org.apache.uniffle.coordinator.CoordinatorConf;
import org.apache.uniffle.server.ShuffleServerConf;
import org.apache.uniffle.storage.util.StorageType;

public class SparkSQLWithDelegationShuffleManagerFallbackTest extends SparkSQLTest {

  @BeforeAll
  public static void setupServers(@TempDir File tmpDir) throws Exception {
    final String candidates =
        Objects.requireNonNull(
                SparkSQLWithDelegationShuffleManagerTest.class
                    .getClassLoader()
                    .getResource("candidates"))
            .getFile();
    CoordinatorConf coordinatorConf = coordinatorConfWithoutPort();
    coordinatorConf.setString(
        CoordinatorConf.COORDINATOR_ACCESS_CHECKERS.key(),
        "org.apache.uniffle.coordinator.access.checker.AccessCandidatesChecker,"
            + "org.apache.uniffle.coordinator.access.checker.AccessClusterLoadChecker");
    coordinatorConf.set(CoordinatorConf.COORDINATOR_ACCESS_CANDIDATES_PATH, candidates);
    coordinatorConf.set(CoordinatorConf.COORDINATOR_APP_EXPIRED, 5000L);
    coordinatorConf.set(CoordinatorConf.COORDINATOR_ACCESS_LOADCHECKER_SERVER_NUM_THRESHOLD, 1);
    Map<String, String> dynamicConf = Maps.newHashMap();
    dynamicConf.put(RssSparkConfig.RSS_STORAGE_TYPE.key(), StorageType.MEMORY_LOCALFILE.name());
    addDynamicConf(coordinatorConf, dynamicConf);
    storeCoordinatorConf(coordinatorConf);

    ShuffleServerConf shuffleServerConf =
        shuffleServerConfWithoutPort(0, tmpDir, ServerType.GRPC_NETTY);
    shuffleServerConf.set(ShuffleServerConf.SERVER_HEARTBEAT_INTERVAL, 1000L);
    shuffleServerConf.set(ShuffleServerConf.SERVER_APP_EXPIRED_WITHOUT_HEARTBEAT, 4000L);
    shuffleServerConf.setString(
        ShuffleServerConf.RSS_STORAGE_TYPE.key(), StorageType.LOCALFILE.name());
    shuffleServerConf.setString(ShuffleServerConf.SERVER_BUFFER_CAPACITY.key(), "512mb");
    storeShuffleServerConf(shuffleServerConf);

    startServersWithRandomPorts();
    Uninterruptibles.sleepUninterruptibly(1L, TimeUnit.SECONDS);
  }

  @Override
  public void updateRssStorage(SparkConf sparkConf) {
    sparkConf.set(RssSparkConfig.RSS_ACCESS_ID.key(), "wrong_id");
    sparkConf.set("spark.shuffle.manager", "org.apache.spark.shuffle.DelegationRssShuffleManager");
  }

  @Override
  public void checkShuffleData() throws Exception {}
}
