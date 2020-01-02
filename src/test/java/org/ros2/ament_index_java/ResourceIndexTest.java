package org.ros2.ament_index_java;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.File;
import java.lang.String;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;

public class ResourceIndexTest extends AmentIndexTestFixture {

  @Test public void testGetResource() throws Exception {
    // Get non-existent resource type
    {
      Resource result = ResourceIndex.getResource("this_type_does_not_exist", "foo");
      assertNull(result);
    }
    // Get non-existent resource name
    {
      Resource result = ResourceIndex.getResource("packages", "this_name_does_not_exist");
      assertNull(result);
    }
    // Get empty marker
    {
      Resource result = ResourceIndex.getResource("packages", "foo");
      assertEquals("packages", result.getType());
      assertEquals("foo", result.getName());
      assertEquals(prefixPath1, result.getPrefixPath());
      assertEquals("", result.getContent());
    }
    // Get marker with content
    {
      Resource result = ResourceIndex.getResource("foo_type", "foo");
      assertEquals("foo_type", result.getType());
      assertEquals("foo", result.getName());
      assertEquals(prefixPath2, result.getPrefixPath());
      assertEquals("This is dummy content for test purposes.\n", result.getContent());
    }
    // Get marker with multi-line content
    {
      Resource result = ResourceIndex.getResource("bar_type", "bar");
      assertEquals("bar_type", result.getType());
      assertEquals("bar", result.getName());
      assertEquals(prefixPath2, result.getPrefixPath());
      assertEquals("Multi-line\ntest\ndata\n.\n", result.getContent());
    }
    // With the same marker in both prefix paths, assert we get the content from the first prefix
    {
      Resource result = ResourceIndex.getResource("test_duplicate_resource", "foo");
      assertEquals("test_duplicate_resource", result.getType());
      assertEquals("foo", result.getName());
      assertEquals(prefixPath1, result.getPrefixPath());
      assertEquals("This content is from the first prefix path\n", result.getContent());
    }
  }

  @Test public void testGetResources() throws Exception{
    // Get non-existent type
    {
      Resource[] result = ResourceIndex.getResources("this_type_does_not_exist");
      assertEquals(0, result.length);
    }
    // Get an existing type
    {
      Resource[] result = ResourceIndex.getResources("foo_type");
      assertEquals(1, result.length);
      assertEquals("foo", result[0].getName());
      assertEquals(prefixPath2, result[0].getPrefixPath());
    }
    // Get a type that exists in multiple prefix paths
    {
      Resource[] result = ResourceIndex.getResources("test_duplicate_resource");
      assertEquals(1, result.length);
      assertEquals("foo", result[0].getName());
      // We should get the first prefix path
      assertEquals(prefixPath1, result[0].getPrefixPath());
    }
  }

  @Test public void testGetAmentIndexPaths() throws Exception {
    String[] paths = ResourceIndex.getAmentIndexPaths();
    assertEquals(2, paths.length);
    assertEquals(prefixPath1, paths[0]);
    assertEquals(prefixPath2, paths[1]);
  }

  @Test public void testHasResource() throws Exception {
    // Non-existent resource type
    {
      String result = ResourceIndex.hasResource("this_type_does_not_exist", "foo");
      assertNull(result);
    }
    // Non-existent resource name
    {
      String result = ResourceIndex.hasResource("packages", "this_name_does_not_exist");
      assertNull(result);
    }
    // Existing resource
    {
      String result = ResourceIndex.hasResource("packages", "foo");
      assertEquals(prefixPath1, result);
    }
  }

  @Test public void testRegisterResource() throws Exception {
    // Bookeeping for cleanup
    filesCreated = new File[3];

    Path resourceIndexPrefix = Path.of(
        prefixPath1, "share", "ament_index", "resource_index");

    // Register an existing resource
    {
      boolean result = ResourceIndex.registerResource(
          "packages", "foo", prefixPath1);
      assertFalse(result);
    }
    // Register a resource to an existing type
    {
      filesCreated[0] = resourceIndexPrefix.resolve(
          Path.of("packages", "new_package_resource")).toFile();
      boolean result = ResourceIndex.registerResource(
          "packages", "new_package_resource", prefixPath1);
      assertTrue(result);
      // Confirm the new file was created
      assertTrue(filesCreated[0].exists());
    }
    // Register a resource and a new type with content
    {
      filesCreated[1] = resourceIndexPrefix.resolve(
          Path.of("new_type", "new_package_resource")).toFile();
      filesCreated[2] = resourceIndexPrefix.resolve("new_type").toFile();
      String contentWritten = "Some bogus content to write to the file.";
      boolean result = ResourceIndex.registerResource(
          "new_type", "new_package_resource", prefixPath1, contentWritten);
      assertTrue(result);
      // Confirm the new file was created and the content is correct
      assertTrue(filesCreated[1].exists());
      String contentRead = Files.readString(filesCreated[1].toPath());
      assertEquals(contentWritten, contentRead);
    }
  }
}
