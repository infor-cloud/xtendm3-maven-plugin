package com.infor.m3.xtendm3.maven.plugin.model.entity;

import com.infor.m3.xtendm3.maven.plugin.model.type.TransactionFieldType;
import lombok.Data;

@Data
public class TransactionFieldMetadata {
  private String name;
  private String description;
  private int length;
  private Boolean mandatory;
  private TransactionFieldType type;
  public Character getFieldType() {
    if (type == TransactionFieldType.NUMERIC){
      return 'N';
    }
    return 'A';
  }
}
