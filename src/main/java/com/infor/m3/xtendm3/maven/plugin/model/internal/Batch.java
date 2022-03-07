package com.infor.m3.xtendm3.maven.plugin.model.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Batch {
  private String name;
  private String description;
}
