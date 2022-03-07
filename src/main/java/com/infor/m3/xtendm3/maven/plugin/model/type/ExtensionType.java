package com.infor.m3.xtendm3.maven.plugin.model.type;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum ExtensionType {
  TRIGGER("ExtendM3Trigger"), TRANSACTION("ExtendM3Transaction"), UTILITY("ExtendM3Utility"), BATCH("ExtendM3Batch");

  private final String superType;

  ExtensionType(String superType) {
    this.superType = superType;
  }

  public static Optional<ExtensionType> resolveByType(String superType) {
    for (ExtensionType extensionType : ExtensionType.values()) {
      if (extensionType.superType.equals(superType)) {
        return Optional.of(extensionType);
      }
    }
    return Optional.empty();
  }

  public static Set<String> getSuperTypes() {
    return Arrays.stream(ExtensionType.values()).map(ExtensionType::getSuperType).collect(Collectors.toSet());
  }

  public String getSuperType() {
    return superType;
  }
}
