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

package org.apache.uniffle.server.buffer;

import org.junit.jupiter.api.BeforeAll;

import org.apache.uniffle.server.buffer.lab.ChunkCreator;
import org.apache.uniffle.server.buffer.lab.LABShuffleBufferWithLinkedList;

public class LABShuffleBufferWithLinkedListTest extends ShuffleBufferWithLinkedListTest {
  @BeforeAll
  public static void setUp() {
    ChunkCreator.initialize(1024 * 3, 1024 * 1024, 512);
  }

  @Override
  protected ShuffleBuffer createShuffleBuffer() {
    return new LABShuffleBufferWithLinkedList();
  }
}
