package com.infor.m3.xtendm3.maven.plugin.model.internal;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

@Data
@Builder
public class Extension {
  @Singular private Map<String, ProgramModule> programModules;
  @Singular private Map<String, Utility> utilities;
  @Singular private Map<String, Source> sources;
}
