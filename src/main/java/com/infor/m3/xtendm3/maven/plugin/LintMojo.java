package com.infor.m3.xtendm3.maven.plugin;

import com.infor.m3.xtendm3.maven.plugin.linter.ExtensionLinter;
import com.infor.m3.xtendm3.maven.plugin.util.AssertionUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "lint", aggregator = true, defaultPhase = LifecyclePhase.PACKAGE)
public class LintMojo extends AbstractXtendM3Mojo {

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().debug("Initializing utilities");
    AssertionUtils.initialize(this::getLog);
    ExtensionLinter linter = new ExtensionLinter(this);
    getLog().info("Running linter on project");
    linter.lint();
  }

}
