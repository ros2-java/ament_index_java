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

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.String;
import java.nio.file.Path;
import java.util.Hashtable;

public class PackageIndexTest extends AmentIndexTestFixture {
  @Test public void testGetPackagePrefix() throws Exception {
    // Non-existent package
    {
      String result = PackageIndex.getPackagePrefix("not_a_package");
      assertNull(result);
    }
    // Existing package
    {
      String result = PackageIndex.getPackagePrefix("foo");
      assertEquals(prefixPath1, result);
    }
  }

  @Test public void testGetPackageShareDirectory() throws Exception {
    // Non-existent package
    {
      String result = PackageIndex.getPackageShareDirectory("not_a_package");
      assertNull(result);
    }
    // Existing package
    {
      String result = PackageIndex.getPackageShareDirectory("foo");
      String expected = prefixPath1 + File.separator +"share" + File.separator + "foo";
      assertEquals(expected, result);
    }
  }

  @Test public void testGetPackagesWithPrefixes() throws Exception {
    Hashtable<String, String> result = PackageIndex.getPackagesWithPrefixes();
    assertEquals(2, result.size());
    assertTrue(result.containsKey("foo"));
    assertTrue(result.containsKey("bar"));
    assertEquals(prefixPath1, result.get("foo"));
    assertEquals(prefixPath1, result.get("bar"));
  }

  @Test public void testRegisterPackage() throws Exception {
    // Bookeeping for cleanup
    filesCreated = new File[3];

    Path packageIndexPrefix1 = Path.of(
        prefixPath1, "share", "ament_index", "resource_index", "packages");
    Path packageIndexPrefix2 = Path.of(
        prefixPath2, "share", "ament_index", "resource_index", "packages");

    // Register a package that already exists
    {
      boolean result = PackageIndex.registerPackage("foo", prefixPath1);
      assertFalse(result);
    }
    // Register a package in a existing index
    {
      filesCreated[0] = packageIndexPrefix1.resolve(Path.of("new_package")).toFile();
      boolean result = PackageIndex.registerPackage("new_package", prefixPath1);
      assertTrue(result);
      // Confirm the new file was created
      assertTrue(filesCreated[0].exists());
    }
    // Register a package in a new index
    {
      filesCreated[1] = packageIndexPrefix2.resolve(Path.of("new_package")).toFile();
      filesCreated[2] = packageIndexPrefix2.toFile();
      boolean result = PackageIndex.registerPackage("new_package", prefixPath2);
      assertTrue(result);
      // Confirm the new file was created
      assertTrue(filesCreated[1].exists());
    }
  }
}
