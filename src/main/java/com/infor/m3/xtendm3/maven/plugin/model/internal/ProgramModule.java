package com.infor.m3.xtendm3.maven.plugin.model.internal;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

@Data
@Builder
public class ProgramModule {
  private String program;
  @Singular private Map<String, Trigger> triggers;
  @Singular private Map<String, ExtMITransaction> transactions;
  @Singular private Map<String, Batch> batches;
  @Singular private Map<String, Utility> utilities;
}
