package com.infor.m3.xtendm3.maven.plugin.util;

import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

public class AssertionUtils {
  private static final String MESSAGES_PROPERTIES = "messages.properties";
  private static AssertionUtils assertionUtils;
  private final Supplier<Log> logger;
  private final Properties messages;
  private final String prettyFormat;

  private AssertionUtils(Supplier<Log> logger) {
    this.logger = logger;
    messages = getMessages();
    prettyFormat = messages.getProperty("ERROR_MESSAGE_FORMAT");
  }

  public static void initialize(Supplier<Log> logger) {
    assertionUtils = new AssertionUtils(logger);

  }

  public static AssertionUtils getInstance() {
    if (assertionUtils == null) {
      throw new RuntimeException("Utility is not initialized");
    }
    return assertionUtils;
  }

  private Properties getMessages() {
    try {
      Properties messages = new Properties();
      messages.load(this.getClass().getClassLoader().getResourceAsStream(MESSAGES_PROPERTIES));
      return messages;
    } catch (IOException e) {
      logger.get().error(e);
      throw new RuntimeException("Could not load messages properties", e);
    }
  }

  public void assertNotNullNorEmpty(String property, ErrorCode errorCode, Object... parameters) throws MojoFailureException {
    if (property == null || property.trim().isEmpty()) {
      doThrow(errorCode, parameters);
    }
  }

  public void assertNotNull(Object property, ErrorCode errorCode, Object... parameters) throws MojoFailureException {
    if (property == null) {
      doThrow(errorCode, parameters);
    }
  }

  public void assertNull(Object property, ErrorCode errorCode, Object... parameters) throws MojoFailureException {
    if (property != null) {
      doThrow(errorCode, parameters);
    }
  }

  public void fail(ErrorCode errorCode, Object... parameters) throws MojoFailureException {
    doThrow(errorCode, parameters);
  }

  private void doThrow(ErrorCode errorCode, Object... parameters) throws MojoFailureException {
    throw new MojoFailureException(prettify(errorCode, parameters));
  }

  private String prettify(ErrorCode errorCode, Object... parameters) {
    String shortDescription = MessageFormat.format(messages.getProperty(errorCode.name(), "N/A"), parameters);
    String longDescription = messages.getProperty(errorCode.name() + "_LONG", "N/A");
    String resolution = messages.getProperty(errorCode.name() + "_RESOLUTION", "N/A");
    String cause = getCause(parameters).orElse("N/A");
    return MessageFormat.format(prettyFormat, errorCode.getCategory(), errorCode.name(), shortDescription, longDescription, resolution, cause);
  }

  private Optional<String> getCause(Object... parameters) {
    if (parameters == null || parameters.length == 0) {
      return Optional.empty();
    }
    Object lastParam = parameters[parameters.length - 1];
    if (!(lastParam instanceof Throwable)) {
      return Optional.empty();
    }
    StringWriter stringWriter = new StringWriter();
    ((Throwable) (lastParam)).printStackTrace(new PrintWriter(stringWriter));
    return Optional.of(stringWriter.toString());
  }
}
