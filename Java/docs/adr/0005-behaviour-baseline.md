# 5. Behaviour baseline before migration

Date: 2026-11-03

## Status

Accepted

## Context

Before migrating Gradle and Java (Step 0) we want proof that the starting code matches the approved golden master,
so any later difference is known to come from our changes.

The original build (Gradle 8.11) cannot run on Java 25, and the only JDK with a compiler on the development machine is
JDK 25 (the JDK 21 install is a runtime only). A Gradle baseline build is therefore not possible without installing
another JDK.

## Decision

Take the baseline without Gradle: compile the sources with `javac --release 8` from JDK 25, run `TexttestFixture 30`
and diff its output against `../texttests/ThirtyDays/stdout.gr`.

Result on 2026-11-03: the output is identical (373 lines).

After Step 0 the same check runs through Gradle:

```bash
./gradlew -q texttest | diff - ../texttests/ThirtyDays/stdout.gr
```

## Consequences

- The legacy behaviour is confirmed to match the golden master, so it is a valid safety net for refactoring.
- The unit tests (only the disabled placeholder, see ADR 0004) are first run by Gradle after Step 0.
