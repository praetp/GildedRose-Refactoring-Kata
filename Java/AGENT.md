# Introduction
This is a small toy project given to me by a potential future employer
as a homework for me.

It is the Java variant of Emily Bache's GildedRose Refactoring Kata. The parent directory (`..`) is a
multi-language repo; only `Java/` is in scope.


## Goals
Demonstrate we can implement a small project with an LLM-agent.
Document every decision in the process in an 'Architectural Decision Record' (ADR).
This will help the agent to steer the implementation and avoids asking the same question again.
Moreover, it will help the reviewers of this project to understand decisions.
ADRs live in `docs/adr/` (see [ADR 0001](docs/adr/0001-record-architecture-decisions.md)). Check them before
asking the user about something that may already be decided.

Every step we take must be well understood before we continue.
Every commit we make must contain code that builds and passes all tests.
If needed, amend the commit until it does.

## Standards
Functional correctness, readability, maintainability must be considered while developing.

All documents we produce (Markdown, ADRs, etc.) must not exceed 120 columns per line.


## Requirements
### Step 0
Make sure the current code base is migrated to the latest version of Gradle and port it to Java 25.

### Step 1
Prepare code development. Put tools like checkstyle, errorprone, jacoco etc in place so we raise the quality of the
produced code.
All warnings must be treated as errors unless we have a good reason to ignore it.

### Step 2
Implement the functional business requirements as stated in @GildedRoseRequirements.md.
Review the code with the builtin /code-review skill and address concerns.
Prompt the user if you doubt.

### Step 3
Make a webapp ? TBD


## Commands

Gradle is the only build tool (see ADR 0006).

```bash
./gradlew build                                              # compile + test
./gradlew test                                               # all tests
./gradlew test --tests com.gildedrose.GildedRoseTest         # one class
./gradlew test --tests 'com.gildedrose.GildedRoseTest.foo'   # one method
./gradlew -q texttest                                        # run TexttestFixture for 30 days
./gradlew -q texttest --args 10                              # custom day count
```

Known inconsistency: `README.md` and `texttest_rig.py` call a Gradle task named `text`, but `build.gradle` defines
`texttest`.

Starting state, before Step 0: Gradle wrapper 8.11, `sourceCompatibility = '1.8'`, JUnit 5.6.2. The installed JDK is 25.


## Architecture

- `src/main/java/com/gildedrose/GildedRose.java` holds all the business logic: the nested if/else in
  `updateQuality()`. This is the code to refactor.
- `src/main/java/com/gildedrose/Item.java` must not be changed. That includes its public mutable fields `name`,
  `sellIn` and `quality`, and the `items` field on `GildedRose`. This is a hard constraint of the kata.
  `updateQuality` and `items` may be made static.
- `src/test/java/com/gildedrose/GildedRoseTest.java` is a placeholder test that fails on purpose
  (`assertEquals("fixme", …)`).
- `src/test/java/com/gildedrose/TexttestFixture.java` runs `updateQuality` once a day and prints the item state for
  each day. It drives the approval tests.

### Approval / golden-master tests

`../texttests/ThirtyDays/stdout.gr` holds the approved output of `TexttestFixture` for 30 days (`options.gr` = `30`).
It records the legacy behaviour, so a behaviour-preserving refactor must reproduce it exactly. The one exception is
"Conjured" items: the legacy code doesn't implement them, so their output is expected to change once the Conjured
requirement is added. To check without installing TextTest:

```bash
./gradlew -q texttest | diff - ../texttests/ThirtyDays/stdout.gr
```

To use TextTest itself, uncomment the Java executable lines in `../texttests/config.gr`. See `README.md`.

### Domain rules to watch for

- Quality is capped to the range 0..50. Sulfuras is the exception: it is always 80 and its `sellIn` never changes.
- After the sell-by date, quality degrades twice as fast.
- Aged Brie gains quality, and the legacy code makes it gain +2 per day after the sell-by date.
- Backstage passes gain +2 at 10 days or less and +3 at 5 days or less, then drop to 0 after the concert.
- Conjured items degrade twice as fast as normal items. This is the new feature.
- Items are matched by exact name string, e.g. `"Backstage passes to a TAFKAL80ETC concert"` and
  `"Sulfuras, Hand of Ragnaros"`.
