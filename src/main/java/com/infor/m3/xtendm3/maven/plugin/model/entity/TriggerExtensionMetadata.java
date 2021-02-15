package com.infor.m3.xtendm3.maven.plugin.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class TriggerExtensionMetadata extends BaseExtensionMetadata {

  private List<TriggerMetadata> triggers;
  private List<String> utilities;
  private Integer priority;

  public  TriggerExtensionMetadata(){
    super.setExtensionType(ExtensionType.TRIGGER);
  }
}
