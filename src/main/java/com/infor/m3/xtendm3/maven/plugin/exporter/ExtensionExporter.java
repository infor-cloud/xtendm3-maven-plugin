package com.infor.m3.xtendm3.maven.plugin.exporter;

import com.google.gson.Gson;
import com.infor.m3.xtendm3.maven.plugin.AbstractXtendM3Mojo;
import com.infor.m3.xtendm3.maven.plugin.exporter.transformer.ExtensionFactory;
import com.infor.m3.xtendm3.maven.plugin.model.entity.ApiMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.TransactionExtensionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.TriggerExtensionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.BatchExtensionMetadata;
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
    if (metadata.getExtensions() != null) {
      for (TriggerExtensionMetadata triggerExtension : metadata.getExtensions()) {
        for (File extension : extensions) {
          if (triggerExtension.getName().equals(extension.getName().substring(0, extension.getName().indexOf('.')))) {
            TriggerExtensionMetadata triggerExtensionMetadata = abstractXtendM3Mojo.getExtensionSourceUtils().getExtensionMetadata(metadata, extension.getName());
            ExtensionFactory factory = ExtensionFactory.getInstance(ExtensionType.TRIGGER);
            if (factory != null) {
              Extension ex = factory.create(triggerExtensionMetadata, extension);
              toExport.put(triggerExtensionMetadata.getName(), ex);
            }
            break;
          }
        }
      }
    }
    if (metadata.getApis() != null) {
      for (ApiMetadata apiMetadata : metadata.getApis()) {
        for (TransactionExtensionMetadata transactionExtension : apiMetadata.getTransactions()) {
          for (File extension : extensions) {
            if (transactionExtension.getName().equals(extension.getName().substring(0, extension.getName().indexOf('.')))) {
              TransactionExtensionMetadata transactionExtensionMetadata = abstractXtendM3Mojo.getExtensionSourceUtils().getTransactionExtensionMetadata(metadata, extension.getName());
              ExtensionFactory factory = ExtensionFactory.getInstance(ExtensionType.TRANSACTION);
              if (factory != null) {
                Extension ex = factory.create(transactionExtensionMetadata, extension);
                toExport.put(transactionExtensionMetadata.getName(), ex);
              }
              break;
            }
          }
        }
      }
    }
    if (metadata.getBatches() != null) {
      for (BatchExtensionMetadata batchExtension : metadata.getBatches()) {
        for (File extension : extensions) {
          if (batchExtension.getName().equals(extension.getName().substring(0, extension.getName().indexOf('.')))) {
            BatchExtensionMetadata batchExtensionMetadata = abstractXtendM3Mojo.getExtensionSourceUtils().getBatchExtensionMetadata(metadata, extension.getName());
            ExtensionFactory factory = ExtensionFactory.getInstance(ExtensionType.BATCH);
            if (factory != null) {
              Extension ex = factory.create(batchExtensionMetadata, extension);
              toExport.put(batchExtensionMetadata.getName(), ex);
            }
            break;
          }
        }
      }
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
    String archiveName = parent + ".zip";
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
