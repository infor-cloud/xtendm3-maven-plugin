package com.infor.m3.xtendm3.maven.plugin.model.entity;

import lombok.Data;

@Data
public class BatchExtensionMetadata extends BaseExtensionMetadata {
  private String name;
  private String description;
}
