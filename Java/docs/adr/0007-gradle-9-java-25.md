# 7. Migrate to Gradle 9.8.2 and Java 25

Date: 2026-11-03

## Status

Accepted

## Context

Step 0 asks to migrate to the latest Gradle and to port the code to Java 25. The starting point was Gradle 8.11,
`sourceCompatibility = '1.8'` and JUnit 5.6.2. Gradle 8.11 cannot run on Java 25, and the build script used syntax
that Gradle 9 removed (top-level `sourceCompatibility`, `task ...`, `JavaExec.main`).

`README.md` and `texttest_rig.py` (used by TextTest via `../texttests/config.gr`) call a Gradle task `text`, but
`build.gradle` defined it as `texttest`.

## Decision

- Gradle wrapper 9.8.2 (latest release on 2026-11-03), with `distributionSha256Sum` so the download is verified.
- Java 25 through a Gradle Java toolchain (`java.toolchain.languageVersion = 25`) rather than
  `sourceCompatibility`, so the build uses JDK 25 whichever JDK starts Gradle.
- JUnit 6.1.3 (latest) imported through the `junit-bom` platform, so the JUnit modules share one version.
  `junit-platform-launcher` is added as `testRuntimeOnly`, which Gradle 9 requires.
- The fixture task is renamed from `texttest` to `text`, matching `README.md` and `texttest_rig.py`.
- The build script uses the lazy task API (`tasks.register`, `tasks.named`).

The production code needed no changes: it compiles on Java 25 as is. Modernising it is left to Step 2.

## Consequences

- `./gradlew clean build --warning-mode all` passes with no deprecation warnings.
- `./gradlew -q text | diff - ../texttests/ThirtyDays/stdout.gr` and `texttest_rig.py` both reproduce the golden
  master exactly.
- Building requires a JDK 25. Gradle can only auto-download one if a toolchain resolver plugin is added; for now
  it must be installed locally.
