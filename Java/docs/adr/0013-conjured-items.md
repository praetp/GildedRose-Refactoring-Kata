# 13. Conjured items

Date: 2026-11-09

## Status

Accepted

## Context

Step 3 adds the new requirement: "Conjured" items degrade in quality twice as fast as normal items. The spec leaves
two things open: which items are conjured, and what "twice as fast" means after the sell-by date.

## Decision

- **Which items**: every item whose name starts with `Conjured`, case-sensitive (e.g. "Conjured Mana Cake",
  "Conjured Sword"). "Conjured" is read as a prefix that marks the kind of item. Names that merely contain the word
  ("Mana Cake Conjured") or spell it differently ("conjured Mana Cake") are normal items, in line with the exact,
  case-sensitive matching of the other names.
- **How fast**: twice the normal loss: 2 per day before the sell-by date, 4 per day after it, never below 0. This
  follows the reading of ADR 0008, where "twice as fast after the sell-by date" applies to the rate of change.
- **How**: a new `CONJURED` constant in `ItemCategory` (ADR 0010) whose rule is `2 * normalDegradation(item)`, so
  the code says "twice as fast as normal" literally. `ItemCategory.of()` checks the prefix after the exact names.
  The contract (ADR 0012) applies unchanged: quality 0..50.
- The tests were written first; the 8 that distinguish Conjured from normal items failed before the
  implementation.

The golden master (`../texttests/ThirtyDays/stdout.gr`) changes as a result, in exactly 4 lines, all for
"Conjured Mana Cake" (days 1 to 4: quality 4, 2, 0, 0 instead of 5, 4, 3, 1). The new output is approved
deliberately; every other line is unchanged.

## Consequences

- New conjured items need no code change, as long as their name starts with "Conjured".
- An item named e.g. "Conjured Aged Brie" would be a conjured *normal* item; the exact names are matched first, and
  a combination of kinds is not supported. The spec does not mention such items.
