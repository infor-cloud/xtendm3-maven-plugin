package com.infor.m3.xtendm3.maven.plugin.model.internal;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

@Data
@Builder
public class Batch {
  private String name;
  private String description;
  private Boolean active;
  private Long modified;
  private String modifiedBy;
  private String sourceUuid;
  @Singular
  private Set<String> utilities;
  private String market;
}
