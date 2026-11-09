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

Note on order: the characterization tests (ADR 0008) were brought forward from Step 2 and written before Step 1,
so the code changes that Step 1's tooling may force are protected by tests.

### Step 1
Prepare code development. Put tools like checkstyle, errorprone, jacoco etc in place so we raise the quality of the
produced code.
All warnings must be treated as errors unless we have a good reason to ignore it.

### Step 2
Refactor the existing code before adding the new feature, without changing its behaviour.
The characterization tests, the golden master (TextTest) and all quality checks must stay green after every commit.
The design is the user's decision and is recorded in an ADR.

### Step 3
Implement the functional business requirements as stated in @GildedRoseRequirements.md.
Review the code with the builtin /code-review skill and address concerns.
Prompt the user if you doubt.

### Step 4
Make a webapp ? TBD


## Commands

Gradle is the only build tool (see ADR 0006).

```bash
./gradlew build                                              # compile + test + all quality checks (ADR 0009)
./gradlew checkstyleMain checkstyleTest                      # style only
./gradlew jacocoTestReport                                   # coverage report: build/reports/jacoco/test/html
./gradlew pitest                                             # mutation testing: build/reports/pitest
./gradlew test                                               # all tests
./gradlew test --tests com.gildedrose.GildedRoseTest         # one class
./gradlew test --tests 'com.gildedrose.GildedRoseTest.foo'   # one method
./gradlew -q text                                            # run TexttestFixture for 30 days
./gradlew -q text --args 10                                  # custom day count
```

Toolchain: Gradle 9.8.2 (wrapper), Java 25 (Gradle toolchain), JUnit 6.1.3 (via BOM). See ADR 0007.

Every warning fails the build: javac `-Xlint:all -Werror`, Error Prone, Checkstyle (Google style, 4 spaces,
120 columns, `config/checkstyle/`), JaCoCo 100% line + branch, PIT 100% mutation score. `Item.java` is exempt from
Checkstyle only, because it may not be changed. See ADR 0009.


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
./gradlew -q text | diff - ../texttests/ThirtyDays/stdout.gr
```

TextTest itself (`../texttests/config.gr` points at `Java/texttest_rig.py`, which runs the Gradle `text` task):

```bash
(cd .. && ./start_texttest.sh)          # creates ../venv and installs TextTest on first use, then runs the suite
(cd .. && venv/bin/texttest -d . -con)  # same, once TextTest is installed
```

On a failure TextTest asks `View details(v), Approve(a) or continue(any other key)?`. Never approve while
refactoring: that overwrites the golden master.

### Domain rules to watch for

- Quality is capped to the range 0..50. Sulfuras is the exception: it is always 80 and its `sellIn` never changes.
- After the sell-by date, quality degrades twice as fast.
- Aged Brie gains quality, and the legacy code makes it gain +2 per day after the sell-by date.
- Backstage passes gain +2 at 10 days or less and +3 at 5 days or less, then drop to 0 after the concert.
- Conjured items degrade twice as fast as normal items. This is the new feature.
- Items are matched by exact name string, e.g. `"Backstage passes to a TAFKAL80ETC concert"` and
  `"Sulfuras, Hand of Ragnaros"`.
