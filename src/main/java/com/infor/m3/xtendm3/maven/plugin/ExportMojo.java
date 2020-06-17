package com.infor.m3.xtendm3.maven.plugin;

import com.infor.m3.xtendm3.maven.plugin.exporter.ExtensionExporter;
import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import com.infor.m3.xtendm3.maven.plugin.util.ExtensionSourceUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.List;

@Mojo(name = "export", aggregator = true, defaultPhase = LifecyclePhase.PACKAGE)
public class ExportMojo extends AbstractXtendM3Mojo {

  public void execute() throws MojoFailureException {
    ExtensionSourceUtils utils = new ExtensionSourceUtils();
    List<File> extensions = getExtensions();
    getLog().info(extensions.toString());
    XtendM3Metadata metadata = getMetadata();
    getLog().info(metadata.toString());
    ExtensionExporter extensionExporter = new ExtensionExporter(this::getLog, this);
    extensionExporter.export();
  }
}
