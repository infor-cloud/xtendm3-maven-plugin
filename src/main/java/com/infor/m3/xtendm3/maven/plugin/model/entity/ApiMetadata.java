package com.infor.m3.xtendm3.maven.plugin.model.entity;

import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import lombok.Data;

import java.util.List;

@Data
public class ApiMetadata extends BaseExtensionMetadata {

  private List<TransactionMetadata> transactions;
  public  ApiMetadata(){
    super.setExtensionType(ExtensionType.TRANSACTION);
  }
}
