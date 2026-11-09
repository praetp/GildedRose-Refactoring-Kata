# 9. Code quality tooling

Date: 2026-11-09

## Status

Accepted

## Context

Step 1 asks for tools such as Checkstyle, Error Prone and JaCoCo, with all warnings treated as errors unless there is
a good reason not to. The code base is tiny and fully covered by characterization tests (ADR 0008).

## Decision

`./gradlew build` (through `check`) runs all of the following and fails on any finding:

- **javac** with `-Xlint:all -Werror`: every compiler warning is an error.
- **Error Prone 2.52.0** (Gradle plugin `net.ltgt.errorprone` 5.2.0), default checks. Its warnings are compiler
  warnings, so `-Werror` makes them errors too.
- **Checkstyle 14.4.0** with Checkstyle's bundled Google style (`google_checks.xml`), copied to
  `config/checkstyle/checkstyle.xml` with these changes: severity `error` instead of `warning`; 4-space indentation
  (8 for line wraps) and 120 columns instead of 2/4 and 100, to match `.editorconfig` and the existing code.
  `maxWarnings = 0`. Applies to main and test code.
- **JaCoCo 0.8.16**: 100% line and 100% branch coverage of the production code, checked by
  `jacocoTestCoverageVerification`.
- **PIT 1.30.2** (Gradle plugin `info.solidsoft.pitest` 1.19.0, `pitest-junit5-plugin` 1.2.3): mutation testing with
  the default mutators; mutation threshold 100%. This automates the manual mutation check of ADR 0008.

Code changes forced by the tools:

- Error Prone (`IdentityBinaryExpression`) flagged `quality = quality - quality` in `GildedRose`; replaced by
  `quality = 0`, which is equivalent. Covered by the tests and the golden master.
- Javadoc added to `TexttestFixture`; static imports moved first in `GildedRoseTest`.

Accepted exceptions:

- `Item.java` may not be changed (kata rule). Its Checkstyle findings (missing Javadoc, 3-space indentation of
  `toString`) are suppressed for that file only, in `config/checkstyle/suppressions.xml`.
- The PIT Gradle plugin calls `Configuration.setVisible`, which Gradle 9.8.2 reports as deprecated (removal in
  Gradle 11). 1.19.0 is the latest plugin version; we cannot fix it ourselves. Gradle deprecation warnings are
  therefore not turned into errors (`--warning-mode fail` is not used).
- `pitest-junit5-plugin` 1.2.3 is built against JUnit Platform 1.9, not JUnit 6. It works with JUnit 6.1.3 here:
  PIT discovers and runs all tests, and kills every mutant.

Each check was verified to fail the build on a deliberate violation: a line over 120 columns (Checkstyle), an
uncovered method (JaCoCo), a raw type (`-Xlint`), and string comparison with `==` (Error Prone).

## Consequences

- Any style, compiler, coverage or mutation finding fails the build, locally and in CI alike.
- 100% coverage and 100% mutation score are strict; if a future change makes an uncovered line or a surviving mutant
  unavoidable, it must be excluded explicitly and justified in an ADR.
- A full `./gradlew build` takes about 15 seconds, mostly PIT.
