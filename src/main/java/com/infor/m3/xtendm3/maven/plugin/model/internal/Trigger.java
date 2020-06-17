package com.infor.m3.xtendm3.maven.plugin.model.internal;

import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionAdvice;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

@Data
@Builder
public class Trigger {
  private String name;
  private String method;
  private ExtensionAdvice advice;
  private Boolean active;
  private Long modified;
  private String modifiedBy;
  private String sourceUuid;
  private String programName;
  @Singular private Set<String> utilities;
  private Integer priority;
}
