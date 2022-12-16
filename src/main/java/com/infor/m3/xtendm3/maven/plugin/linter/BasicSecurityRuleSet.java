package com.infor.m3.xtendm3.maven.plugin.linter;

import com.infor.m3.xtendm3.maven.plugin.model.type.ExtensionType;
import com.infor.m3.xtendm3.maven.plugin.model.type.ProgramType;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

public class BasicSecurityRuleSet {
  private final Set<String> whiteListedPackages;
  private final Set<String> whiteListedClasses;
  private final Set<String> allowedSuperTypes;
  private final Set<String> allowedApiInjections;
  private final Map<ExtensionType, List<String>> forbiddenApisByExtensionType;
  private final Map<ProgramType, List<String>> forbiddenApisByProgramType;

  public BasicSecurityRuleSet() {
    whiteListedPackages = Collections.unmodifiableSet(buildWhiteListedPackages());
    whiteListedClasses = Collections.unmodifiableSet(buildWhiteListedClasses());
    allowedSuperTypes = Collections.unmodifiableSet(buildAllowedSuperTypes());
    allowedApiInjections = Collections.unmodifiableSet(buildAllowedApiInjections());
    forbiddenApisByExtensionType = Collections.unmodifiableMap(buildForbiddenApisByExtensionType());
    forbiddenApisByProgramType = Collections.unmodifiableMap(buildForbiddenApisByProgramType());
  }

  public Set<String> getWhiteListedPackages() {
    return whiteListedPackages;
  }

  public Set<String> getWhiteListedClasses() {
    return whiteListedClasses;
  }

  public Set<String> getAllowedSuperTypes() {
    return allowedSuperTypes;
  }

  public Set<String> getAllowedApiInjections() {
    return allowedApiInjections;
  }

  public Map<ExtensionType, List<String>> getForbiddenApisByExtensionType() {
    return forbiddenApisByExtensionType;
  }

  public Map<ProgramType, List<String>> getForbiddenApisByProgramType() {
    return forbiddenApisByProgramType;
  }

  public Set<String> getInjectableApisByProgramType(ProgramType type) {
    Set<String> injectables = new HashSet<>(allowedApiInjections);
    injectables.removeAll(forbiddenApisByProgramType.get(type));
    return injectables;
  }

  public Set<String> getInjectableApisByExtensionType(ExtensionType type) {
    Set<String> injectables = new HashSet<>(allowedApiInjections);
    injectables.removeAll(forbiddenApisByExtensionType.get(type));
    return injectables;
  }

  private Set<String> buildWhiteListedPackages() {
    Set<String> whiteListedPackages = new HashSet<>();
    whiteListedPackages.add("java.math");
    whiteListedPackages.add("java.time");
    whiteListedPackages.add("java.text");
    whiteListedPackages.add("java.util");
    whiteListedPackages.add("groovy.xml");
    whiteListedPackages.add("groovy.json");
    whiteListedPackages.add("groovy.util.slurpersupport");
    return whiteListedPackages;
  }

  private Set<String> buildWhiteListedClasses() {
    Set<String> whiteListedClasses = new HashSet<>();
    whiteListedClasses.add(Writer.class.getName());
    whiteListedClasses.add(PrintWriter.class.getName());
    whiteListedClasses.add(Reader.class.getName());
    whiteListedClasses.add(BufferedReader.class.getName());
    return whiteListedClasses;
  }

  private Set<String> buildAllowedSuperTypes() {
    Set<String> superTypes = new HashSet<>();
    superTypes.add("ExtendM3Trigger");
    superTypes.add("ExtendM3Transaction");
    superTypes.add("ExtendM3Utility");
    return superTypes;
  }

  private Set<String> buildAllowedApiInjections() {
    Set<String> allowedApiInjections = new HashSet<>();
    allowedApiInjections.add("DatabaseAPI");
    allowedApiInjections.add("ExtensionAPI");
    allowedApiInjections.add("InteractiveAPI");
    allowedApiInjections.add("LoggerAPI");
    allowedApiInjections.add("MethodAPI");
    allowedApiInjections.add("MICallerAPI");
    allowedApiInjections.add("ProgramAPI");
    allowedApiInjections.add("SessionAPI");
    allowedApiInjections.add("IonAPI");
    allowedApiInjections.add("UtilityAPI");
    allowedApiInjections.add("MIAPI");
    allowedApiInjections.add("TextFilesAPI");
    allowedApiInjections.add("TransactionAPI");
    return allowedApiInjections;
  }

  private Map<ExtensionType, List<String>> buildForbiddenApisByExtensionType() {
    Map<ExtensionType, List<String>> forbiddenApisByExtensionType = new HashMap<>();
    forbiddenApisByExtensionType.put(ExtensionType.TRIGGER, Collections.singletonList("MIAPI"));
    List<String> forbids = new ArrayList<>();
    forbids.add("InteractiveAPI");
    forbids.add("MethodAPI");
    forbids.add("TransactionAPI");
    forbiddenApisByExtensionType.put(ExtensionType.TRANSACTION, forbids);
    forbiddenApisByExtensionType.put(ExtensionType.UTILITY, new ArrayList<>(buildAllowedApiInjections()));
    return forbiddenApisByExtensionType;
  }

  private Map<ProgramType, List<String>> buildForbiddenApisByProgramType() {
    Map<ProgramType, List<String>> forbiddenApisByProgramType = new HashMap<>();
    List<String> forbids = new ArrayList<>();
    forbids.add("MethodAPI");
    forbids.add("MIAPI");
    forbids.add("TransactionAPI");
    forbiddenApisByProgramType.put(ProgramType.INTERACTIVE, forbids);
    forbids = new ArrayList<>();
    forbids.add("InteractiveAPI");
    forbids.add("MIAPI");
    forbids.add("TransactionAPI");
    forbiddenApisByProgramType.put(ProgramType.BATCH, forbids);
    forbids = new ArrayList<>();
    forbids.add("MIAPI");
    forbids.add("InteractiveAPI");
    forbids.add("MethodAPI");
    forbiddenApisByProgramType.put(ProgramType.MI, forbids);
    return forbiddenApisByProgramType;
  }
}
