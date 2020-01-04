/* Copyright 2020 Open Source Robotics Foundation, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ros2.ament_index_java;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.File;
import java.lang.String;
import java.nio.file.Path;

public class AmentIndexTestFixture {
  @ClassRule public static final EnvironmentVariables environmentVariables =
      new EnvironmentVariables();

  public static String prefixPath1;

  public static String prefixPath2;

  public File[] filesCreated;

  @BeforeClass public static void setUpClass() {
    // Set AMENT_PREFIX_PATH to a some test directories
    Path testDirectory = Path.of("src", "test", "java", "org", "ros2", "ament_index_java");
    ResourceIndexTest.prefixPath1 = testDirectory.resolve("test_prefix1").toString();
    ResourceIndexTest.prefixPath2 = testDirectory.resolve("test_prefix2").toString();
    String envValue = prefixPath1 + File.pathSeparator + prefixPath2;
    environmentVariables.set("AMENT_PREFIX_PATH", envValue);
  }

  @After public void tearDown() {
    if (filesCreated != null) {
      for (File file : filesCreated) {
        if (file != null) {
          file.delete();
        }
      }
    }
  }
}

