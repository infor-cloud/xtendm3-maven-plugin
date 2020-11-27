package com.infor.m3.xtendm3.maven.plugin.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class ApiMetadata extends BaseExtensionMetadata {
  private String name;
  private List<TransactionExtensionMetadata> transactions;
}
