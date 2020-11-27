package com.infor.m3.xtendm3.maven.plugin.util;

import com.infor.m3.xtendm3.maven.plugin.model.entity.*;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ExtensionSourceUtils {
  public ExtensionType resolveExtensionType(JavaClassSource source) throws MojoFailureException {
    String superType = source.getSuperType();
    Optional<ExtensionType> type = ExtensionType.resolveByType(superType);
    if (!type.isPresent()) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_SUPERTYPE, superType, ExtensionType.getSuperTypes());
    }
    return type.get();
  }

  public TriggerExtensionMetadata getExtensionMetadata(XtendM3Metadata metadata, String extensionName) throws MojoFailureException {
    extensionName = extensionName.endsWith(".groovy") ? extensionName.substring(0, extensionName.indexOf(".groovy")) : extensionName;
    for (TriggerExtensionMetadata triggerExtensionMetadata : metadata.getExtensions()) {
      if (triggerExtensionMetadata.getName().equals(extensionName)) {
        return triggerExtensionMetadata;
      }
    }
    AssertionUtils.getInstance().fail(ErrorCode.METADATA_MISSING, "extension", extensionName);
    throw new MojoFailureException("Code will not reach here");
  }

  public TransactionExtensionMetadata getTransactionExtensionMetadata(XtendM3Metadata metadata, String extensionName) throws MojoFailureException {
    extensionName = extensionName.endsWith(".groovy") ? extensionName.substring(0, extensionName.indexOf(".groovy")) : extensionName;
    List<ApiMetadata> apisMetadata = metadata.getApis();
    for (ApiMetadata apiMetadata : apisMetadata) {
      for (TransactionExtensionMetadata transactionExtensionMetadata : apiMetadata.getTransactions()) {
        if (transactionExtensionMetadata.getName().equals(extensionName)) {
          transactionExtensionMetadata.setProgram(apiMetadata.getName());
          return transactionExtensionMetadata;
        }
      }
    }
    AssertionUtils.getInstance().fail(ErrorCode.METADATA_MISSING, "extension", extensionName);
    throw new MojoFailureException("Code will not reach here");
  }

  public ApiMetadata getApiMetadata(XtendM3Metadata metadata, String apiName) throws MojoFailureException {
    for (ApiMetadata apiMetadata : metadata.getApis()) {
      if (apiMetadata.getName().equals(apiName)) {
        return apiMetadata;
      }
    }
    AssertionUtils.getInstance().fail(ErrorCode.METADATA_MISSING, "API", apiName);
    throw new MojoFailureException("Code will not reach here");
  }

  public UtilityMetadata getUtilityMetadata(XtendM3Metadata metadata, String utilityName) throws MojoFailureException {
    for (UtilityMetadata utilityMetadata : metadata.getUtilities()) {
      if (utilityMetadata.getName().equals(utilityName)) {
        return utilityMetadata;
      }
    }
    AssertionUtils.getInstance().fail(ErrorCode.METADATA_MISSING, "utility", utilityName);
    throw new MojoFailureException("Code will not reach here");
  }

  public Optional<MethodSource<JavaClassSource>> getConstructor(JavaClassSource source) {
    for (MethodSource<JavaClassSource> method : source.getMethods()) {
      if (method.isConstructor()) {
        return Optional.of(method);
      }
    }
    return Optional.empty();
  }
}
