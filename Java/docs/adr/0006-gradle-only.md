# 6. Gradle as the only build tool

Date: 2026-11-03

## Status

Accepted

## Context

The kata ships both a Gradle build (`build.gradle`, `gradlew`) and a Maven build (`pom.xml`, `mvnw`, `.mvn/`). They
were already out of sync (JUnit 5.6.2 vs 5.8.2). Step 0 (latest Gradle, Java 25) and Step 1 (checkstyle, errorprone,
jacoco, warnings as errors) would have to be done twice to keep both working.

## Decision

Use Gradle only. Remove `pom.xml`, `mvnw`, `mvnw.cmd` and `.mvn/`, and the Maven `target/` entry in `.gitignore`.

`../texttests/environment.gr` still lists `Java/target/...` on its CLASSPATH. That file is shared with the other
language variants in the parent repo, so it is left unchanged; the extra entries are harmless.

## Consequences

- One build to migrate, configure and keep green.
- Developers who prefer Maven need to use Gradle instead.
