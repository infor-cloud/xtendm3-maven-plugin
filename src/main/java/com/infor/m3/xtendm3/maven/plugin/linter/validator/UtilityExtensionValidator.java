package com.infor.m3.xtendm3.maven.plugin.linter.validator;

import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.List;
import java.util.function.Supplier;

public class UtilityExtensionValidator extends AbstractExtensionValidator {

  protected UtilityExtensionValidator(Supplier<Log> logger) {
    super(logger);
  }

  @Override
  protected void validateMetadata(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    // Nothing to validate for utilities
  }

  @Override
  protected void validateMemberFields(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    List<FieldSource<JavaClassSource>> fieldSources = source.getFields();
    if (!fieldSources.isEmpty()) {
      throw new MojoFailureException("Utilities must not contain any fields");
    }
  }

  @Override
  protected void validateConstructor(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    if (extensionSourceUtils.getConstructor(source).isPresent()) {
      throw new MojoFailureException("Utilities must not contain a constructor");
    }
  }

  @Override
  protected void validateMemberMethods(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    // Methods are allowed
  }
}
