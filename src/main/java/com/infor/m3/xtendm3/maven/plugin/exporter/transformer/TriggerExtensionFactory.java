package com.infor.m3.xtendm3.maven.plugin.exporter.transformer;

import com.infor.m3.xtendm3.maven.plugin.model.entity.BaseExtensionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.TriggerExtensionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.TriggerMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.internal.Extension;
import com.infor.m3.xtendm3.maven.plugin.model.internal.ProgramModule;
import com.infor.m3.xtendm3.maven.plugin.model.internal.Source;
import com.infor.m3.xtendm3.maven.plugin.model.internal.Trigger;
import com.infor.m3.xtendm3.maven.plugin.model.type.APIVersion;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.util.AssertionUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class TriggerExtensionFactory implements ExtensionFactory {

  @Override
  public Extension create(BaseExtensionMetadata extensionMetadata, File extension) throws MojoFailureException {
    TriggerExtensionMetadata metadata = (TriggerExtensionMetadata) extensionMetadata;
    Source source = buildSource(extension);
    Trigger trigger = buildTrigger(metadata, source);
    ProgramModule programModule = buildProgramModule(metadata, trigger);
    return buildExtension(Collections.singletonList(programModule), Collections.singletonList(source));
  }

  private Source buildSource(File extension) throws MojoFailureException {
    try {
      return Source.builder()
        .apiVersion(APIVersion.UNKNOWN)
        .beVersion(DEFAULT_BE_VERSION)
        .created(Instant.now().toEpochMilli())
        .updated(Instant.now().toEpochMilli())
        .createdBy(DEFAULT_CREATOR)
        .updatedBy(DEFAULT_UPDATER)
        .uuid(UUID.randomUUID().toString())
        .code(Files.readAllBytes(extension.toPath()))
        .build();
    } catch (IOException e) {
      AssertionUtils.getInstance().fail(ErrorCode.FILE_EXTENSION_LOOKUP_ERROR, extension.getName());
    }
    throw new MojoFailureException("Code will not reach here");
  }

  private Trigger buildTrigger(TriggerExtensionMetadata metadata, Source source) {
    TriggerMetadata triggerMetadata = metadata.getTriggers().get(0);
    return Trigger.builder()
      .name(metadata.getName())
      .advice(triggerMetadata.getAdvice())
      .method(triggerMetadata.getMethod())
      .priority(metadata.getPriority())
      .active(false)
      .modified(Instant.now().toEpochMilli())
      .modifiedBy(DEFAULT_UPDATER)
      .sourceUuid(source.getUuid())
      .programName(triggerMetadata.getProgram())
      .utilities(Collections.emptyList())
      .priority(metadata.getPriority())
      .build();
  }

  private ProgramModule buildProgramModule(TriggerExtensionMetadata metadata, Trigger trigger) {
    TriggerMetadata triggerMetadata = metadata.getTriggers().get(0);
    return ProgramModule.builder()
      .program(triggerMetadata.getProgram())
      .transactions(Collections.emptyMap())
      .triggers(Collections.singletonMap(trigger.getName(), trigger))
      .build();
  }

  private Extension buildExtension(List<ProgramModule> programModules, List<Source> sources) {
    return Extension.builder()
      .programModules(programModules.stream().collect(Collectors.toMap(ProgramModule::getProgram, module -> module)))
      .sources(sources.stream().collect(Collectors.toMap(Source::getUuid, source -> source)))
      .utilities(Collections.emptyMap())
      .build();
  }
}
