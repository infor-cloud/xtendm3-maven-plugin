package com.infor.m3.xtendm3.maven.plugin.exporter.transformer;

import com.infor.m3.xtendm3.maven.plugin.model.entity.ApiMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.BaseExtensionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.TransactionFieldMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.TransactionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.internal.*;
import com.infor.m3.xtendm3.maven.plugin.model.type.APIVersion;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.model.type.TransactionType;
import com.infor.m3.xtendm3.maven.plugin.util.AssertionUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionExtensionFactory implements ExtensionFactory {
  @Override
  public Extension create(BaseExtensionMetadata extensionMetadata, File extension) throws MojoFailureException {
    TransactionMetadata transactionMetadata = (TransactionMetadata) extensionMetadata;
    // find the transaction with the name of the file within the API metadata
//    List<TransactionMetadata>  metadata  = apiMetadata.getTransactions();
//    String extensionName = extension.getName().replace(".groovy", "");
//    List<TransactionMetadata> transactionsWithName =  metadata.stream().filter(x->x.getName().equals(extensionName)).collect(Collectors.toList());

//    if (transactionsWithName.size() == 0) {
//      throw new MojoFailureException("Transaction Not found in yaml " + extensionName);
//    }
//    TransactionMetadata transactionMetadata = extensionMetadata;//transactionsWithName.get(0);

    Source source = buildSource(extension);
    List<ExtMIField> inputFields = buildInputFields(transactionMetadata.getInputs());
    List<ExtMIField> outputFields = buildOutputFields(transactionMetadata.getOutputs());

    ExtMITransaction transaction = buildTransaction(transactionMetadata, source, inputFields, outputFields);
    ProgramModule programModule = buildProgramModule(transactionMetadata, transaction);
    return buildExtension(Collections.singletonList(programModule), Collections.singletonList(source));
  }

  private List<ExtMIField> buildInputFields(List<TransactionFieldMetadata> inputFieldsMetadata) {
    List<ExtMIField> inputFields = new ArrayList<>();
    if (inputFieldsMetadata != null) {
      for (TransactionFieldMetadata fieldMetadata : inputFieldsMetadata) {
        inputFields.add(ExtMIField.builder()
          .name(fieldMetadata.getName())
          .description(fieldMetadata.getDescription())
          .length(fieldMetadata.getLength())
          .type(fieldMetadata.getFieldType())
          .mandatory(fieldMetadata.getMandatory())
          .build());
      }
    }
    return inputFields;
  }

  private List<ExtMIField> buildOutputFields(List<TransactionFieldMetadata> outputFieldsMetadata) {
    List<ExtMIField> outputFields = new ArrayList<>();
    if (outputFieldsMetadata != null) {
      for (TransactionFieldMetadata fieldMetadata : outputFieldsMetadata) {
        outputFields.add(ExtMIField.builder()
          .name(fieldMetadata.getName())
          .description(fieldMetadata.getDescription())
          .length(fieldMetadata.getLength())
          .type(fieldMetadata.getFieldType())
          .mandatory(fieldMetadata.getMandatory())
          .build());
      }
    }
    return outputFields;
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

  private ExtMITransaction buildTransaction(TransactionMetadata metadata, Source source, List<ExtMIField> inputFields, List<ExtMIField> outputFields) {
    return ExtMITransaction.builder()
      .name(metadata.getName())
      .program(metadata.getProgram())
      .description(metadata.getDescription())
      .active(false)
      .multi(metadata.getType().equals(TransactionType.MULTI))
      .modified(Instant.now().toEpochMilli())
      .modifiedBy(DEFAULT_UPDATER)
      .sourceUuid(source.getUuid())
      .outputFields(outputFields)
      .inputFields(inputFields)
      .utilities(Collections.emptySet())
      .build();
  }

  private ProgramModule buildProgramModule(TransactionMetadata metadata, ExtMITransaction transaction) {
    return ProgramModule.builder()
      .program(metadata.getProgram())
      .transactions(Collections.singletonMap(transaction.getName(), transaction))
      .triggers(Collections.emptyMap())
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
