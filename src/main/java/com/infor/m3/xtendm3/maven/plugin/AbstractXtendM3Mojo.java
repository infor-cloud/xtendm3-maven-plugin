package com.infor.m3.xtendm3.maven.plugin;

import com.google.gson.*;
import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.util.AssertionUtils;
import com.infor.m3.xtendm3.maven.plugin.util.ExtensionSourceUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractXtendM3Mojo extends AbstractMojo {
  private static final String METADATA_FILE_NAME = "metadata";
  private final ExtensionSourceUtils extensionSourceUtils;
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject mavenProject;

  public AbstractXtendM3Mojo() {
    AssertionUtils.initialize(this::getLog);
    extensionSourceUtils = new ExtensionSourceUtils();
  }

  public List<File> getExtensions() throws MojoFailureException {
    try (Stream<Path> paths = Files.walk(getExtensionsDirectoryPath(), 1)) {
      List<File> extensions = paths.map(Path::toFile).filter(File::isFile).collect(Collectors.toList());
      getLog().info(String.format("Found %d extension(s)", extensions.size()));
      return extensions;
    } catch (IOException exception) {
      AssertionUtils.getInstance().fail(ErrorCode.FILE_EXTENSION_LOOKUP_ERROR, getExtensionsDirectoryPath(), exception);
      return null; // Is never executed
    }
  }

  public XtendM3Metadata getMetadata() throws MojoFailureException {
    Path resources = Paths.get(mavenProject.getBasedir().getPath(), "src", "main", "resources");
    String metadataFileName = METADATA_FILE_NAME + ".yaml";
    String metadataAlternativeFileName = METADATA_FILE_NAME + ".yml";
    Path path = Paths.get(resources.toString(), metadataFileName);
    if (path.toFile().exists()) {
      getLog().info(String.format("Found metadata file: %s", path));
      return parseMetadata(path);
    }
    path = Paths.get(resources.toString(), metadataAlternativeFileName);
    if (path.toFile().exists()) {
      getLog().info(String.format("Found metadata file: %s", path));
      return parseMetadata(path);
    }
    AssertionUtils.getInstance().fail(ErrorCode.FILE_METADATA_NOT_FOUND_ERROR, metadataFileName, metadataAlternativeFileName);
    return null; // Is never executed
  }

  public Gson getGson() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(byte[].class, (JsonSerializer<byte[]>) (src, typeOfSrc, context) -> new JsonPrimitive(Base64.getEncoder().encodeToString(src)));
    builder.registerTypeAdapter(byte[].class, (JsonDeserializer<byte[]>) (json, typeOfT, context) -> Base64.getDecoder().decode(json.getAsString()));
    return builder.create();
  }

  public String getTargetDirectory() {
    return mavenProject.getModel().getBuild().getDirectory();
  }

  public ExtensionSourceUtils getExtensionSourceUtils() {
    return extensionSourceUtils;
  }

  private XtendM3Metadata parseMetadata(Path path) throws MojoFailureException {
    try {
      Yaml yaml = new Yaml();
      getLog().debug(String.format("Parsing metadata file: %s", path));
      return yaml.loadAs(new FileInputStream(path.toFile()), XtendM3Metadata.class);
    } catch (Exception e) {
      AssertionUtils.getInstance().fail(ErrorCode.METADATA_PARSE_ERROR, path, e);
      return null; // Is never executed
    }
  }

  private Path getExtensionsDirectoryPath() {
    Path path = Paths.get(mavenProject.getBasedir().getPath(), "src", "main", "groovy");
    getLog().debug(String.format("Using extensions directory: %s", path.toString()));
    return path;
  }

}
