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
