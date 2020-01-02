package org.ros2.ament_index_java;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Implementation of the ament resource index.
 *
 * See the following document for information on the purpose and design of the resource index:
 *
 * https://github.com/ament/ament_cmake/blob/master/ament_cmake_core/doc/resource_index.md
 */
public class ResourceIndex {
  /**
   * The environment variable with a list of path prefixes for the ament index.
   */
  public static final String AMENT_PREFIX_PATH_ENV_VAR = "AMENT_PREFIX_PATH";

  /**
   * Path of the resource index relative to the AMENT_PREFIX_PATH.
   */
  public static final String RESOURCE_INDEX_SUBDIRECTORY = Path.of(
      "share", "ament_index", "resource_index").toString();

  /**
   * Get the content of a resource.
   *
   * @param resourceType The type of the resource. Must not be empty.
   * @param resourceName The name of the resource. Must not be empty.
   * @return The content of the resource or null if the resource is not found.
   */
  public static Resource getResource(String resourceType, String resourceName)
  throws AmentIndexException, IOException {
    if (resourceType.isEmpty()) {
      throw new AmentIndexException("resource type must not be empty");
    }
    if (resourceName.isEmpty()) {
      throw new AmentIndexException("resource name must not be empty");
    }

    String[] searchPaths = ResourceIndex.getAmentIndexPaths();
    for (String basePath : searchPaths) {
      Path resourcePath = Path.of(
          basePath, RESOURCE_INDEX_SUBDIRECTORY, resourceType, resourceName);
      File resourceFile = resourcePath.toFile();
      if (resourceFile.isFile()) {
        return new Resource(resourceType, resourceName, basePath, Files.readString(resourcePath));
      }
    }
    return null;
  }

  /**
   * Get all resource names, with install prefixes, for a given resource type.
   *
   * If the same resource exists in multiple prefix paths, only the resource from the first
   * prefix path is returned.
   *
   * @param resourceType The type of resource. Must not be empty.
   * @return An array of resources.
   */
  public static Resource[]
  getResources(String resourceType) throws AmentIndexException, IOException {
    if (resourceType.isEmpty()) {
      throw new AmentIndexException("resource type must not be empty");
    }

    String[] searchPaths = ResourceIndex.getAmentIndexPaths();

    ArrayList<Resource> resources = new ArrayList<Resource>();
    HashSet<String> names = new HashSet<String>();  // used to detect duplicates
    for (String basePath : searchPaths) {
      Path resourceDirectoryPath = Path.of(
          basePath, RESOURCE_INDEX_SUBDIRECTORY, resourceType);
      File resourceDirectoryFile = resourceDirectoryPath.toFile();
      // Ignore if not a directory
      if (!resourceDirectoryFile.isDirectory()) {
        continue;
      }
      File[] directoryFiles = resourceDirectoryFile.listFiles();
      for (File file : directoryFiles) {
        String fileName = file.getName();
        // Ignore files starting with a dot
        if (fileName.startsWith(".")) {
          continue;
        }
        // Only add the first occurance to the result
        if (!names.contains(fileName)) {
          names.add(fileName);
          resources.add(
              new Resource(resourceType, fileName, basePath, Files.readString(file.toPath())));
        }
      }
    }
    return resources.toArray(new Resource[resources.size()]);
  }

  /**
   * Get a list of ament index prefix paths.
   *
   * Paths are read from the environment variable @{link ResourceIndex.AMENT_PREFIX_PATH_ENV_VAR}.
   * @return An array of paths for the ament index.
   */
  public static String[] getAmentIndexPaths() throws AmentIndexException {
    String envValue = System.getenv(ResourceIndex.AMENT_PREFIX_PATH_ENV_VAR);
    if (null == envValue) {
      throw new AmentIndexException(
          String.format(
              "environment variable '%s' is not set", ResourceIndex.AMENT_PREFIX_PATH_ENV_VAR));
    }

    String[] searchPaths = envValue.split(File.pathSeparator);

    // Filter paths
    ArrayList<String> outputPaths = new ArrayList<String>(searchPaths.length);
    for (String path : searchPaths) {
      // Ignore blank tokens
      if (path.isBlank()) {
        continue;
      }
      // Ignore paths are are not directories
      File pathFile = new File(path);
      if (!pathFile.isDirectory()) {
        continue;
      }
      outputPaths.add(path);
    }
    for (String output : outputPaths) {
    }
    return outputPaths.toArray(searchPaths);
  }

  /**
   * Check if a resource exists.
   * @param resourceType The type of the resource. Must not be empty.
   * @param resourceName The name of the resource. Must not be empty.
   * @return The prefix path of the resource if it is found, or null if the resource is not found.
   */
  public static String
  hasResource(String resourceType, String resourceName) throws AmentIndexException {
    if (resourceType.isEmpty()) {
      throw new AmentIndexException("resource type must not be empty");
    }
    if (resourceName.isEmpty()) {
      throw new AmentIndexException("resource name must not be empty");
    }

    // Get search paths
    String[] searchPaths = ResourceIndex.getAmentIndexPaths();
    for (String basePath : searchPaths) {
      Path resourcePath = Path.of(
          basePath, RESOURCE_INDEX_SUBDIRECTORY, resourceType, resourceName);
      if (resourcePath.toFile().exists()) {
        return basePath;
      }
    }
    return null;
  }

  /**
   * Register a resource to the index.
   *
   * If the resource already exists, nothing happens and this method returns false.
   *
   * @param resourceType The type of the resource. Must not be empty.
   * @param resourceName The name of the resource. Must not be empty.
   * @param prefixPath The prefix path of the ament index.
   * @param content The content to write to the resource marker file.
   * @return true if the resource was registered successfully, false if the resource already
   *     exists.
   */
  public static boolean
  registerResource(String resourceType, String resourceName, String prefixPath, String content)
  throws AmentIndexException, IOException {
    if (resourceType.isEmpty()) {
      throw new AmentIndexException("resource type must not be empty");
    }
    if (resourceName.isEmpty()) {
      throw new AmentIndexException("resource name must not be empty");
    }

    String[] amentIndexPaths = ResourceIndex.getAmentIndexPaths();
    Path resourcePath = Path.of(
        prefixPath, RESOURCE_INDEX_SUBDIRECTORY, resourceType, resourceName);
    File resourceFile = resourcePath.toFile();

    // Ensure path to file exists
    resourceFile.getParentFile().mkdirs();

    // Create the file
    if (!resourceFile.createNewFile()) {
      // File already exists
      return false;
    }

    // Write content to file if given
    if (!content.isEmpty()) {
      Files.writeString(resourcePath, content);
    }

    return true;
  }

  /**
   * Register a resource to the index.
   *
   * If the resource already exists, nothing happens and this method returns false.
   *
   * @param resourceType The type of the resource. Must not be empty.
   * @param resourceName The name of the resource. Must not be empty.
   * @param prefixPath The prefix path of the ament index.
   * @return true if the resource was registered successfully, false if the resource already
   *     exists.
   */
  public static boolean
  registerResource(String resourceType, String resourceName, String prefixPath)
  throws AmentIndexException, IOException {
    return ResourceIndex.registerResource(resourceType, resourceName, prefixPath, new String());
  }

  /**
   * Register a resource to the index.
   *
   * If the resource already exists, nothing happens and this method returns false.
   *
   * @param resource The resource to register. Must not be null.
   * @return true if the resource was registered successfully, false if the resource already
   *     exists.
   */
  public static boolean
  registerResource(Resource resource) throws AmentIndexException, IOException {
    if (resource == null) {
      throw new AmentIndexException("resource object is null");
    }
    return ResourceIndex.registerResource(
      resource.getType(), resource.getName(), resource.getPrefixPath(), resource.getContent());
  }
}
