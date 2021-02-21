package com.infor.m3.xtendm3.maven.plugin.util;

import com.infor.m3.xtendm3.maven.plugin.model.entity.*;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import org.apache.maven.plugin.MojoFailureException;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.util.Optional;

public class ExtensionSourceUtils {
  public ExtensionType resolveExtensionType(JavaClassSource source) throws MojoFailureException {
    String superType = source.getSuperType();
    Optional<ExtensionType> type = ExtensionType.resolveByType(superType);
    if (type.isPresent()) {
      return type.get();

    }
    AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_SUPERTYPE, superType, ExtensionType.getSuperTypes());
    throw new MojoFailureException("Code will not reach here");
  }

  public BaseExtensionMetadata getExtensionMetadata(XtendM3Metadata metadata, String extensionName) throws MojoFailureException {
    extensionName = extensionName.endsWith(".groovy") ? extensionName.substring(0, extensionName.indexOf(".groovy")) : extensionName;
    if (metadata.getExtensions() != null) {
      for (TriggerExtensionMetadata triggerExtensionMetadata : metadata.getExtensions()) {
        if (triggerExtensionMetadata.getName().equals(extensionName)) {
          return triggerExtensionMetadata;
        }
      }
    }

    if (metadata.getApis()!=null){
      for (ApiMetadata apiMetadata : metadata.getApis()) {
        if (apiMetadata.getTransactions() != null) {
          for(TransactionMetadata transactionMetadata : apiMetadata.getTransactions())
          if (transactionMetadata.getName().equals(extensionName)) {
            return transactionMetadata;
          }
        }
      }
    }
    AssertionUtils.getInstance().fail(ErrorCode.METADATA_MISSING, "extension", extensionName);
    throw new MojoFailureException("Code will not reach here");
  }

  public TransactionMetadata getTransactionMetadata(XtendM3Metadata metadata, String apiName) throws MojoFailureException {
    for (ApiMetadata apiMetadata : metadata.getApis()) {
      if (apiMetadata.getTransactions() != null) {
        for(TransactionMetadata transactionMetadata : apiMetadata.getTransactions())
          if (transactionMetadata.getName().equals(apiName)) {
            return transactionMetadata;
          }
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
