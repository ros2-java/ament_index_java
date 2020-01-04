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

import java.io.IOException;
import java.lang.String;
import java.nio.file.Path;
import java.util.Hashtable;

/**
 * Implementation of the ament package index.
 *
 * See the following document for information on the purpose and design of the package index:
 *
 * https://github.com/ament/ament_cmake/blob/master/ament_cmake_core/doc/resource_index.md
 */
public class PackageIndex {
  public static final String PACKAGE_RESOURCE_TYPE = "packages";
  /**
   * Get the installation prefix of a package.
   *
   * @param packageName The name of the package. Must not be empty.
   * @return The installation prefix of the package, or null if the package is not found.
   */
  public static String
  getPackagePrefix(String packageName) throws AmentIndexException, IOException {
    Resource resource = ResourceIndex.getResource(PACKAGE_RESOURCE_TYPE, packageName);
    if (resource == null) {
      return null;
    }
    return resource.getPrefixPath();
  }

  /**
   * Get the share directory prefix of a package.
   *
   * @param packageName The name of the package. Must not be empty.
   * @return The share directory prefix of the package, or null if the package is not found.
   */
  public static String
  getPackageShareDirectory(String packageName) throws AmentIndexException, IOException {
    String packagePrefix = PackageIndex.getPackagePrefix(packageName);
    if (packagePrefix == null) {
      return null;
    }
    return Path.of(packagePrefix, "share", packageName).toString();
  }

  /**
   * Get all packages and their install prefixes.
   *
   * Only the install prefix for the first occurance of a package is returned.
   *
   * @return A map from package name to the install prefix.
   */
  public static Hashtable<String, String>
  getPackagesWithPrefixes() throws IOException {
    Hashtable<String, String> packages_with_prefixes = new Hashtable<String, String>();
    try {
      Resource[] resources =  ResourceIndex.getResources(PACKAGE_RESOURCE_TYPE);
      for (Resource resource : resources) {
        packages_with_prefixes.put(resource.getName(), resource.getPrefixPath());
      }
    } catch (AmentIndexException e) {
      // There must be no packages installed
    }
    return packages_with_prefixes;
  }

  /**
   * Register a package with a resource index.
   *
   * @param packageName The name of the package to register. Must not be empty.
   * @param prefixPath The prefix path to the ament index.
   * @return true if the package was registered successfully, false otherwise.
   */
  public static boolean
  registerPackage(String packageName, String prefixPath) throws AmentIndexException, IOException {
    return ResourceIndex.registerResource(PACKAGE_RESOURCE_TYPE, packageName, prefixPath);
  }
}
