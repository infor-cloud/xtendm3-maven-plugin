package com.infor.m3.xtendm3.maven.plugin.linter.validator;

import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.function.Supplier;

public class TransactionExtensionValidator extends AbstractExtensionValidator {

  protected TransactionExtensionValidator(Supplier<Log> logger) {
    super(logger);
  }

  @Override
  protected void validateMetadata(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {

  }

  @Override
  protected void validateMemberFields(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {

  }

  @Override
  protected void validateConstructor(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {

  }

  @Override
  protected void validateMemberMethods(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {

  }

}
