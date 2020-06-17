package com.infor.m3.xtendm3.maven.plugin.exporter;

import com.google.gson.Gson;
import com.infor.m3.xtendm3.maven.plugin.AbstractXtendM3Mojo;
import com.infor.m3.xtendm3.maven.plugin.exporter.transformer.ExtensionFactory;
import com.infor.m3.xtendm3.maven.plugin.model.entity.TriggerExtensionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import com.infor.m3.xtendm3.maven.plugin.model.internal.Extension;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import com.infor.m3.xtendm3.maven.plugin.util.ArchiveUtils;
import com.infor.m3.xtendm3.maven.plugin.util.AssertionUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExtensionExporter {
  private final Supplier<Log> logger;
  private final AbstractXtendM3Mojo abstractXtendM3Mojo;


  public ExtensionExporter(Supplier<Log> logger, AbstractXtendM3Mojo abstractXtendM3Mojo) {
    this.logger = logger;
    this.abstractXtendM3Mojo = abstractXtendM3Mojo;
  }

  public void export() throws MojoFailureException {
    logger.get().info("Preparing for exporting extensions");
    XtendM3Metadata metadata = abstractXtendM3Mojo.getMetadata();
    List<File> extensions = abstractXtendM3Mojo.getExtensions();
    logger.get().info(String.format("Found %d extensions to export", extensions.size()));
    Map<String, Extension> toExport = new HashMap<>();
    for (File extension : extensions) {
      TriggerExtensionMetadata triggerExtensionMetadata = abstractXtendM3Mojo.getExtensionSourceUtils().getExtensionMetadata(metadata, extension.getName());
      ExtensionFactory factory = ExtensionFactory.getInstance(ExtensionType.TRIGGER);
      Extension ex = factory.create(triggerExtensionMetadata, extension);
      toExport.put(triggerExtensionMetadata.getName(), ex);
    }
    doExport(toExport);
  }

  private void doExport(Map<String, Extension> extensions) throws MojoFailureException {
    Gson gson = abstractXtendM3Mojo.getGson();
    Map<String, String> content = extensions.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> gson.toJson(e.getValue())));
    String target = abstractXtendM3Mojo.getTargetDirectory();
    String parent = "exported";
    for (Map.Entry<String, String> entry : content.entrySet()) {
      File file = Paths.get(target, parent, entry.getKey() + ".json").toFile();
      logger.get().info(String.format("writing %s", file.getPath()));
      write(file, entry.getValue());
    }
    String archiveName = parent + "-" + LocalDateTime.now().toString() + ".zip";
    new ArchiveUtils(logger).zipDirectory(Paths.get(target, archiveName).toString(), Paths.get(target, parent).toString());
  }

  private void write(File file, String content) throws MojoFailureException {
    try {
      Files.createDirectories(file.toPath().getParent());
      file.createNewFile();
    } catch (IOException e) {
      AssertionUtils.getInstance().fail(ErrorCode.INTERNAL_ERROR, e);
    }
    try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(file))) {
      printWriter.println(content);
    } catch (IOException e) {
      AssertionUtils.getInstance().fail(ErrorCode.INTERNAL_ERROR, e);
    }
  }
}
