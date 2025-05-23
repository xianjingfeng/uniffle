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

package org.apache.spark.shuffle.writer;

import java.util.ArrayList;
import java.util.List;

import org.apache.uniffle.common.ShuffleBlockInfo;

public class AddBlockEvent {

  private String taskId;
  private int stageAttemptNumber;
  private List<ShuffleBlockInfo> shuffleDataInfoList;
  private List<Runnable> processedCallbackChain;
  private WriteBufferManager bufferManager;

  public AddBlockEvent(String taskId, List<ShuffleBlockInfo> shuffleDataInfoList) {
    this(taskId, 0, shuffleDataInfoList, null);
  }

  public AddBlockEvent(
      String taskId,
      int stageAttemptNumber,
      List<ShuffleBlockInfo> shuffleDataInfoList,
      WriteBufferManager writeBufferManager) {
    this.taskId = taskId;
    this.stageAttemptNumber = stageAttemptNumber;
    this.shuffleDataInfoList = shuffleDataInfoList;
    this.processedCallbackChain = new ArrayList<>();
    this.bufferManager = writeBufferManager;
  }

  /** @param callback, should not throw any exception and execute fast. */
  public void addCallback(Runnable callback) {
    processedCallbackChain.add(callback);
  }

  public String getTaskId() {
    return taskId;
  }

  public int getStageAttemptNumber() {
    return stageAttemptNumber;
  }

  public List<ShuffleBlockInfo> getShuffleDataInfoList() {
    return shuffleDataInfoList;
  }

  public List<Runnable> getProcessedCallbackChain() {
    return processedCallbackChain;
  }

  public WriteBufferManager getBufferManager() {
    return bufferManager;
  }

  @Override
  public String toString() {
    return "AddBlockEvent: TaskId[" + taskId + "], " + shuffleDataInfoList;
  }
}
