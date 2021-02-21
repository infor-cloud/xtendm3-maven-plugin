package com.infor.m3.xtendm3.maven.plugin.model.entity;

import com.infor.m3.xtendm3.maven.plugin.model.type.TransactionType;
import lombok.Data;

import java.util.List;

@Data
public class TransactionExtensionMetadata extends BaseExtensionMetadata {
  private String program;
  private String name;
  private String description;
  private TransactionType type;
  private List<TransactionFieldMetadata> inputs;
  private List<TransactionFieldMetadata> outputs;
}
