package com.infor.m3.xtendm3.maven.plugin.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class TriggerExtensionMetadata extends BaseExtensionMetadata {
  private String name;
  private List<TriggerMetadata> triggers;
  private List<String> utilities;
  private Integer priority;
  private String market;
}
