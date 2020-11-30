package com.infor.m3.xtendm3.maven.plugin.exporter.transformer;

import com.infor.m3.xtendm3.maven.plugin.model.entity.BaseExtensionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.internal.Extension;
import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

public interface ExtensionFactory {
  String DEFAULT_CREATOR = "XTENDM3DEV";
  String DEFAULT_UPDATER = "XTENDM3DEV";
  String DEFAULT_BE_VERSION = "";

  static ExtensionFactory getInstance(ExtensionType type) {
    if (type.equals(ExtensionType.TRANSACTION)) {
      return new TransactionExtensionFactory();
    } else if (type.equals(ExtensionType.TRIGGER)) {
      return new TriggerExtensionFactory();
    } else {
      return null;
    }
  }

  Extension create(BaseExtensionMetadata extensionMetadata, File extension) throws MojoFailureException;
}
