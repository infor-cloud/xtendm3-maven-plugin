package com.infor.m3.xtendm3.maven.plugin.model.entity;

import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionAdvice;
import com.infor.m3.xtendm3.maven.plugin.model.type.ProgramType;
import com.infor.m3.xtendm3.maven.plugin.model.type.TriggerType;
import lombok.Data;

@Data
public class TriggerMetadata {
  private String program;
  private ProgramType programType;
  private TriggerType type;
  private String method; // Only on METHOD TRIGGERS
  private String transaction; // Only on TRANSACTION TRIGGERS
  private ExtensionAdvice advice;
}
