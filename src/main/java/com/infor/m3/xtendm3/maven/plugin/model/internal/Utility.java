package com.infor.m3.xtendm3.maven.plugin.model.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Utility {
  private String name;
  private String sourceUuid;
}
