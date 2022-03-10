package com.infor.m3.xtendm3.maven.plugin.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class XtendM3Metadata extends BaseExtensionMetadata {
  private String metadataVersion;
  private List<TriggerExtensionMetadata> extensions;
  private List<ApiMetadata> apis;
  private List<UtilityMetadata> utilities;
  private List<BatchExtensionMetadata> batches;
}
