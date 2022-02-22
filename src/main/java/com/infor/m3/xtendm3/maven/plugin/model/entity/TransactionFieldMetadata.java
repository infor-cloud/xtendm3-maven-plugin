package com.infor.m3.xtendm3.maven.plugin.model.entity;

import com.infor.m3.xtendm3.maven.plugin.model.type.TransactionFieldType;
import lombok.Data;

@Data
public class TransactionFieldMetadata {
  private String name;
  private String description;
  private Integer length;
  private Boolean mandatory = false;
  private TransactionFieldType type;
}
