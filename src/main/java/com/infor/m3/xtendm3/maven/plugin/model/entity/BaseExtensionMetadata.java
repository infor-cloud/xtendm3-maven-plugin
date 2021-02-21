package com.infor.m3.xtendm3.maven.plugin.model.entity;

import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import lombok.Data;

@Data
public abstract class BaseExtensionMetadata {
  public com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType ExtensionType;
  protected String name;
}
