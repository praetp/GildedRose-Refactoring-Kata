# 14. Match "Conjured" as a whole word

Date: 2026-11-09

## Status

Accepted. Refines [0013](0013-conjured-items.md).

## Context

ADR 0013 classified every name starting with `Conjured` as a conjured item. The code review of Step 3 pointed out
that a plain prefix also matches unrelated names that only begin with those letters, such as "Conjuredite Ore" or
"ConjuredX".

## Decision

An item is conjured if its name starts with the whole word `Conjured`: the name is exactly `Conjured`, or starts
with `Conjured ` (followed by a space). Still case-sensitive. "Conjured Mana Cake" and "Conjured Sword" are
conjured; "Conjuredite Ore" and "ConjuredX" are normal items.

The two new test cases were added first and failed before the change.

## Consequences

- Accidental matches on longer words are excluded.
- A name such as "Conjured-Sword" (other separator) is a normal item; the spec gives no such names.
