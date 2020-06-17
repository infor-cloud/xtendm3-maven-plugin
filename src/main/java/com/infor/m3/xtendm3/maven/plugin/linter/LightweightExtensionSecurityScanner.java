package com.infor.m3.xtendm3.maven.plugin.linter;

import com.infor.m3.xtendm3.maven.plugin.linter.validator.AbstractExtensionValidator;
import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import com.infor.m3.xtendm3.maven.plugin.util.AssertionUtils;
import com.infor.m3.xtendm3.maven.plugin.util.ExtensionSourceUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.ParserException;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

class LightweightExtensionSecurityScanner {
  private final Supplier<Log> logger;
  private final ExtensionSourceUtils extensionSourceUtils;

  LightweightExtensionSecurityScanner(Supplier<Log> logger) {
    this.logger = logger;
    extensionSourceUtils = new ExtensionSourceUtils();
  }

  void scan(XtendM3Metadata metadata, List<File> extensions) throws MojoFailureException {
    int size = extensions.size();
    int current = 0;
    for (File extension : extensions) {
      logger.get().info(String.format("Scanning %d out of %d extensions", ++current, size));
      scanSourceCode(metadata, extension);
    }
  }

  void scanSourceCode(XtendM3Metadata metadata, File extension) throws MojoFailureException {
    try {
      logger.get().info(String.format("Parsing source code for %s", extension.getName()));
      JavaClassSource source = Roaster.parse(JavaClassSource.class, extension);
      ExtensionType type = extensionSourceUtils.resolveExtensionType(source);
      AbstractExtensionValidator.create(type, logger).validate(metadata, source);
    } catch (IOException | ParserException | IllegalStateException exception) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_PARSE_ERROR, extension.getPath(), exception);
    }
  }
}
