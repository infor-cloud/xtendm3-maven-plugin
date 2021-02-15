package com.infor.m3.xtendm3.maven.plugin.model.entity;

import com.infor.m3.xtendm3.maven.plugin.model.type.TransactionType;
import lombok.Data;

import java.util.List;

@Data
public class TransactionMetadata extends  BaseExtensionMetadata{
  private String name;
  private String program;
  private String description;
  private TransactionType type;
  private List<TransactionFieldMetadata> inputs;
  private List<TransactionFieldMetadata> outputs;

  public  TransactionMetadata(){
    super.setExtensionType(ExtensionType.TRANSACTION);
  }
}
