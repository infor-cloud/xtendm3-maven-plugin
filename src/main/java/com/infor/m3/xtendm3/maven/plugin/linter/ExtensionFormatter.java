package com.infor.m3.xtendm3.maven.plugin.linter;

import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

class ExtensionFormatter {
  private final Supplier<Log> logger;

  ExtensionFormatter(Supplier<Log> logger) {
    this.logger = logger;
  }


  public void format(XtendM3Metadata metadata, List<File> extensions) throws MojoFailureException {
  }
}
