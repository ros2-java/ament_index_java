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

import java.lang.String;

public class Resource {
  private final String type;
  private final String name;
  private final String prefixPath;
  private final String content;

  /**
   * Construct a resource.
   */
  public Resource(String type, String name, String prefixPath, String content) {
    this.type = type;
    this.name = name;
    this.prefixPath = prefixPath;
    this.content = content;
  }

  /**
   * Construct a resource with no content.
   */
  public Resource(String type, String name, String prefixPath) {
    this(type, name, prefixPath, "");
  }

  /**
   * @return The type of the resource.
   */
  public String getType() {
    return this.type;
  }

  /**
   * @return The name of the resource.
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return The prefix path of the resource.
   */
  public String getPrefixPath() {
    return this.prefixPath;
  }

  /**
   * @return The content of the resource.
   */
  public String getContent() {
    return this.content;
  }
}
