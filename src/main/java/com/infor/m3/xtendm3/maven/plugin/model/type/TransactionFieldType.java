package com.infor.m3.xtendm3.maven.plugin.model.type;

public enum TransactionFieldType {
  ALPHANUMERIC("A"), NUMERIC("N");
  private final String code;

  TransactionFieldType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
