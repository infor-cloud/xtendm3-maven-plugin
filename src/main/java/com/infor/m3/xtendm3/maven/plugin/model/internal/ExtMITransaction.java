package com.infor.m3.xtendm3.maven.plugin.model.internal;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class ExtMITransaction {
  private String sourceUuid;
  private String name;
  private String program;
  private String description;
  private Boolean active;
  private Boolean multi;
  private Long modified;
  private String modifiedBy;
  private List<ExtMIField> outputFields;
  private List<ExtMIField> inputFields;
  @Singular private Set<String> utilities;
}
