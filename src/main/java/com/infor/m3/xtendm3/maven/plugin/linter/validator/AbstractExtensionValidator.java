package com.infor.m3.xtendm3.maven.plugin.linter.validator;

import com.infor.m3.xtendm3.maven.plugin.linter.BasicSecurityRuleSet;
import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import com.infor.m3.xtendm3.maven.plugin.util.AssertionUtils;
import com.infor.m3.xtendm3.maven.plugin.util.ExtensionSourceUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractExtensionValidator {
  protected final Supplier<Log> logger;
  protected final BasicSecurityRuleSet securityRuleSet;
  protected final ExtensionSourceUtils extensionSourceUtils;

  protected AbstractExtensionValidator(Supplier<Log> logger) {
    this.logger = logger;
    securityRuleSet = new BasicSecurityRuleSet();
    extensionSourceUtils = new ExtensionSourceUtils();
  }

  public static AbstractExtensionValidator create(ExtensionType extensionType, Supplier<Log> logger) throws MojoFailureException {
    if (extensionType == ExtensionType.TRIGGER) {
      return new TriggerExtensionValidator(logger);
    }
    if (extensionType == ExtensionType.TRANSACTION) {
      return new TransactionExtensionValidator(logger);
    }
    if (extensionType == ExtensionType.UTILITY) {
      return new UtilityExtensionValidator(logger);
    }
    if (extensionType == ExtensionType.BATCH) {
      return new BatchExtensionValidator(logger);
    }
    AssertionUtils.getInstance().fail(ErrorCode.CODE_UNHANDLED_EXTENSION_TYPE_ERROR, extensionType);
    throw new MojoFailureException("Code will not reach here");
  }

  public void validate(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    validatePackage(source);
    validateImports(source);
    validateInterfaces(source);
    validateMetadata(metadata, source);
    validateMemberFields(metadata, source);
    validateConstructor(metadata, source);
    validateMemberMethods(metadata, source);
  }

  protected abstract void validateMetadata(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException;

  protected abstract void validateMemberFields(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException;

  protected abstract void validateConstructor(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException;

  protected abstract void validateMemberMethods(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException;

  private void validatePackage(JavaClassSource source) throws MojoFailureException {
    String sourcePackage = source.getPackage();
    if (sourcePackage != null) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_PACKAGE, sourcePackage, source.getName());
    }
  }

  private void validateImports(JavaClassSource source) throws MojoFailureException {
    for (Import anImport : source.getImports()) {
      validateImport(anImport, source);
    }
  }

  private void validateImport(Import anImport, JavaClassSource source) throws MojoFailureException {
    for (String wp : securityRuleSet.getWhiteListedPackages()) {
      if (anImport.getPackage().startsWith(wp)) {
        return;
      }
    }
    for (String wc : securityRuleSet.getWhiteListedClasses()) {
      if (anImport.getQualifiedName().equals(wc)) {
        return;
      }
    }
    AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_IMPORT, anImport, source.getName());
  }

  private void validateInterfaces(JavaClassSource source) throws MojoFailureException {
    List<String> interfaces = source.getInterfaces();
    if (!interfaces.isEmpty()) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_INTERFACE, interfaces, source.getName());
    }
  }
}
