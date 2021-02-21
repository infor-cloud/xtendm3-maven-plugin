package com.infor.m3.xtendm3.maven.plugin.linter.validator;

import com.infor.m3.xtendm3.maven.plugin.model.entity.TriggerExtensionMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.TriggerMetadata;
import com.infor.m3.xtendm3.maven.plugin.model.entity.XtendM3Metadata;
import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import com.infor.m3.xtendm3.maven.plugin.model.type.ProgramType;
import com.infor.m3.xtendm3.maven.plugin.model.type.TriggerType;
import com.infor.m3.xtendm3.maven.plugin.util.AssertionUtils;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.Parameter;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TriggerExtensionValidator extends AbstractExtensionValidator {

  protected TriggerExtensionValidator(Supplier<Log> logger) {
    super(logger);
  }

  @Override
  protected void validateMetadata(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    logger.get().info(String.format("Validating metadata for extension %s", source.getName()));
    TriggerExtensionMetadata triggerExtensionMetadata = (TriggerExtensionMetadata) extensionSourceUtils.getExtensionMetadata(metadata, source.getName());
    List<TriggerMetadata> triggerMetadata = triggerExtensionMetadata.getTriggers();
    if (triggerMetadata.isEmpty()) {
      AssertionUtils.getInstance().fail(ErrorCode.METADATA_TRIGGER_MISSING, triggerExtensionMetadata.getName());
    }
    if (triggerMetadata.size() > 1) {
      AssertionUtils.getInstance().fail(ErrorCode.METADATA_TRIGGER_CONFLICT, triggerExtensionMetadata.getName());
    }
    TriggerMetadata trigger = triggerMetadata.get(0);
    AssertionUtils.getInstance().assertNotNullNorEmpty(trigger.getProgram(), ErrorCode.METADATA_TRIGGER_PROPERTY_MISSING, "program", triggerExtensionMetadata.getName());
    AssertionUtils.getInstance().assertNotNull(trigger.getAdvice(), ErrorCode.METADATA_TRIGGER_PROPERTY_MISSING, "advice", triggerExtensionMetadata.getName());
    AssertionUtils.getInstance().assertNotNull(trigger.getProgramType(), ErrorCode.METADATA_TRIGGER_PROPERTY_MISSING, "programType", triggerExtensionMetadata.getName());
    if (trigger.getType() == TriggerType.METHOD) {
      AssertionUtils.getInstance().assertNotNullNorEmpty(trigger.getMethod(), ErrorCode.METADATA_TRIGGER_PROPERTY_MISSING, "method", triggerExtensionMetadata.getName());
      AssertionUtils.getInstance().assertNull(trigger.getTransaction(), ErrorCode.METADATA_INVALID_PROPERTY_FOR_TRIGGER_TYPE, "transaction", triggerExtensionMetadata.getName());
    }
    if (trigger.getType() == TriggerType.TRANSACTION) {
      AssertionUtils.getInstance().assertNotNullNorEmpty(trigger.getTransaction(), ErrorCode.METADATA_TRIGGER_PROPERTY_MISSING, "transaction", triggerExtensionMetadata.getName());
      AssertionUtils.getInstance().assertNull(trigger.getMethod(), ErrorCode.METADATA_INVALID_PROPERTY_FOR_TRIGGER_TYPE, "method", triggerExtensionMetadata.getName());
    }
  }

  @Override
  protected void validateMemberFields(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    logger.get().info(String.format("Validating member fields for extension %s", source.getName()));
    TriggerExtensionMetadata triggerExtensionMetadata = (TriggerExtensionMetadata) extensionSourceUtils.getExtensionMetadata(metadata, source.getName());
    List<TriggerMetadata> triggerMetadata = triggerExtensionMetadata.getTriggers();
    TriggerMetadata trigger = triggerMetadata.get(0);
    ProgramType programType = trigger.getProgramType();
    Set<String> declaredTypes = source.getFields().stream().map(Field::getType).map(Type::getName).collect(Collectors.toSet());
    List<String> forbiddenTypes = securityRuleSet.getForbiddenApisByExtensionType().get(ExtensionType.TRIGGER);
    Set<String> condemnedTypes = declaredTypes.stream().filter(forbiddenTypes::contains).collect(Collectors.toSet());
    if (!condemnedTypes.isEmpty()) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_DECLARATION_ERROR, condemnedTypes, triggerExtensionMetadata.getName());
    }
    forbiddenTypes = securityRuleSet.getForbiddenApisByProgramType().get(programType);
    condemnedTypes = declaredTypes.stream().filter(forbiddenTypes::contains).collect(Collectors.toSet());
    if (!Collections.disjoint(forbiddenTypes, declaredTypes)) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_DECLARATION_ERROR, condemnedTypes, triggerExtensionMetadata.getName());
    }
  }

  @Override
  protected void validateConstructor(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    logger.get().info(String.format("Validating constructor for extension %s", source.getName()));
    Optional<MethodSource<JavaClassSource>> constructor = extensionSourceUtils.getConstructor(source);
    if (!constructor.isPresent()) {
      return;
    }
    TriggerExtensionMetadata triggerExtensionMetadata = (TriggerExtensionMetadata) extensionSourceUtils.getExtensionMetadata(metadata, source.getName());
    List<TriggerMetadata> triggerMetadata = triggerExtensionMetadata.getTriggers();
    TriggerMetadata trigger = triggerMetadata.get(0);
    ProgramType programType = trigger.getProgramType();
    Set<String> paramTypes = constructor.get().getParameters().stream().map(Parameter::getType).map(Type::getName).collect(Collectors.toSet());
    Set<String> allowedTypes = securityRuleSet.getInjectableApisByExtensionType(ExtensionType.TRIGGER);
    Set<String> condemnedTypes = paramTypes.stream().filter(((Predicate<String>) allowedTypes::contains).negate()).collect(Collectors.toSet());
    if (!condemnedTypes.isEmpty()) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_INJECTION_ERROR, condemnedTypes, allowedTypes);
    }
    allowedTypes = securityRuleSet.getInjectableApisByProgramType(programType);
    condemnedTypes = paramTypes.stream().filter(((Predicate<String>) allowedTypes::contains).negate()).collect(Collectors.toSet());
    if (!condemnedTypes.isEmpty()) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_FORBIDDEN_INJECTION_ERROR, condemnedTypes, allowedTypes);
    }
  }

  @Override
  protected void validateMemberMethods(XtendM3Metadata metadata, JavaClassSource source) throws MojoFailureException {
    logger.get().info(String.format("Validating member fields for extension %s", source.getName()));
    if (!source.hasMethodSignature("main")) {
      AssertionUtils.getInstance().fail(ErrorCode.CODE_MISSING_MAIN_METHOD_ERROR);
    }
  }
}
