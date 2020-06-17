package com.infor.m3.xtendm3.maven.plugin.linter;

import com.infor.m3.xtendm3.maven.plugin.AbstractXtendM3Mojo;
import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

public class ExtensionLinter {
  private final Supplier<Log> logger;
  private final AbstractXtendM3Mojo abstractXtendM3Mojo;
  private final LightweightExtensionSecurityScanner extensionSecurityScanner;
  private final ExtensionFormatter extensionFormatter;

  public ExtensionLinter(AbstractXtendM3Mojo abstractXtendM3Mojo) {
    this.abstractXtendM3Mojo = abstractXtendM3Mojo;
    logger = abstractXtendM3Mojo::getLog;
    extensionSecurityScanner = new LightweightExtensionSecurityScanner(abstractXtendM3Mojo::getLog);
    extensionFormatter = new ExtensionFormatter(abstractXtendM3Mojo::getLog);
  }

  public void lint() throws MojoFailureException {
    XtendM3Metadata metadata = abstractXtendM3Mojo.getMetadata();
    abstractXtendM3Mojo.getLog().debug(String.format("Parsed metadata\n%s", metadata));
    List<File> extensions = abstractXtendM3Mojo.getExtensions();
    logger.get().info(String.format("Performing security scan and static analysis of sources and metadata for %d extensions", extensions.size()));
    extensionSecurityScanner.scan(metadata, extensions);
    extensionFormatter.format(metadata, extensions);
  }
}
