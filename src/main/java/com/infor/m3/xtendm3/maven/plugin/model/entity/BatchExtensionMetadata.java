package com.infor.m3.xtendm3.maven.plugin.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class BatchExtensionMetadata extends BaseExtensionMetadata {
  private String name;
  private List<String> utilities;
  private List<BatchMetadata> batches;
}
