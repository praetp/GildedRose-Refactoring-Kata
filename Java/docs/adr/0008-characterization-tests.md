# 8. Characterization tests before refactoring

Date: 2026-11-09

## Status

Accepted

## Context

Before refactoring `GildedRose.updateQuality()` we want fast, precise unit tests in addition to the golden master
(`../texttests/ThirtyDays/stdout.gr`). The golden master covers only one fixed set of items over 30 days and says
little about *which* rule broke when it fails.

Apart from Conjured items, the legacy code implements the spec. Where the spec is ambiguous the code picks one
reading: Aged Brie gains 2 per day after its sell-by date (the spec says items degrade "twice as fast" then, but not
whether that also applies to Brie increasing). A refactoring must not change behaviour, so the tests describe what
the code does, and pin these readings too.

## Decision

- Replace the kata's disabled placeholder (ADR 0004) with characterization tests in `GildedRoseTest`.
- The tests pin down the current behaviour of the legacy code, including how it resolves ambiguities in the spec.
  Such cases are marked in their display name.
- One `@Nested` class per item type, `@ParameterizedTest` with `@CsvSource` rows of
  `sellIn, quality -> sellIn, quality` for a single day. Cases sit on the rule boundaries: sell-by date (1, 0, -1),
  backstage thresholds (11/10, 6/5), and the quality limits 0 and 50.
- Aged Brie gaining 2 per day after its sell-by date is the intended rule, not just legacy behaviour: "twice as
  fast after the sell-by date" applies to the rate of change, whether quality goes down or (for Brie) up.
- Conjured items are not covered: the legacy code treats them as normal items, which the new feature will change.
  They are added test-first when the feature is implemented.
- Invalid input (quality above 50 for non-legendary items, negative quality) is not covered: the spec does not define
  it.
- JUnit only, no extra assertion library.

The tests were checked by hand-made mutations of the legacy code (boundaries `< 11`, `< 6`, `< 0`, the 0 and 50
limits, the backstage drop, the `sellIn` decrement, the Sulfuras guard): every mutation made at least one test fail.

## Consequences

- A failing test names the rule and the boundary that broke.
- The code's reading of ambiguous rules is pinned too; changing it later is a deliberate, visible decision.
- Automated mutation testing (e.g. PIT) could replace the manual check; that belongs to the tooling in Step 1.
