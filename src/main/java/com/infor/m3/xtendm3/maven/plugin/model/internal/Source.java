package com.infor.m3.xtendm3.maven.plugin.model.internal;

import com.infor.m3.xtendm3.maven.plugin.model.type.APIVersion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Source {
  private String uuid;
  private Long updated;
  private String updatedBy;
  private Long created;
  private String createdBy;
  private APIVersion apiVersion;
  private String beVersion;
  private String codeHash;
  private byte[] code;
}
