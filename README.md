[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![Java CD](https://github.com/infor-cloud/xtendm3-maven-plugin/workflows/Java%20CD/badge.svg?event=push)
[![Maven Central](https://img.shields.io/maven-central/v/com.infor.m3/xtendm3-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.infor.m3%22%20AND%20a:%22xtendm3-maven-plugin%22)
# XtendM3 Maven Plugin

## Introduction
This plugin adds support for linting extensions locally based on similar (though not identical and nor complete) security 
rules, checks and constraints for XtendM3 also support exporting extensions to the format where they can be imported.

## Usage
Add the plugin to your `pom.xml` file in the plugins section similar to below to run the linter automatically on compile and packaging the project.
```xml
      <plugin>
        <groupId>com.infor.m3</groupId>
        <artifactId>xtendm3-maven-plugin</artifactId>
        <version>0.1.2</version>
        <executions>
          <execution>
            <phase>compile</phase>
            <goals>
              <goal>lint</goal>
            </goals>
          </execution>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>export</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```

You can also run the goals separately:

* `./mvnw xtendm3:lint` for linting and verifying extensions
* `./mvnw xtendm3:export` for exporting the extensions
* `./mvnw xtendm3:help` to seek help
