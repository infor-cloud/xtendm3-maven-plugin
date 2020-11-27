package com.infor.m3.xtendm3.maven.plugin.model.internal;

import com.infor.m3.xtendm3.maven.plugin.model.type.TransactionFieldType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExtMIField {
  private String name;
  private String description;
  private Integer length;
  private Boolean mandatory;
  private String type;
}
