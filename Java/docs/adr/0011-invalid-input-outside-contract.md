# 11. Invalid input is outside the contract

Date: 2026-11-09

## Status

Accepted. Amends [0010](0010-item-category-enum.md).

## Context

ADR 0010 kept the original code's behaviour for invalid input: a quality already outside 0..50 was left unchanged
rather than clamped (a Brie with quality 60 stays at 60). That made the quality helpers awkward
(`Math.max(quality, Math.min(50, quality + amount))`) to preserve behaviour that no caller, test or fixture relies on.

The spec says quality is never negative and never above 50, and that Sulfuras' quality is 80. Items that break this
are invalid data. Options: keep preserving the old behaviour, reject invalid items with an exception, or declare them
outside the contract.

## Decision

Invalid input is outside the contract of `GildedRose.updateQuality()`, and the result for it is undefined. The
contract is stated in its Javadoc: every item has a non-null name and a quality of 0 to 50, except legendary items
(Sulfuras), whose quality is 80. Keeping to it is the caller's responsibility.

- No validation and no exception.
- The quality helpers become plain clamps: `Math.min(50, quality + amount)` and `Math.max(0, quality - amount)`.

Rejected:

- **Preserving the old behaviour**: complicates the code for undefined input nobody produces.
- **Throwing an exception**: more code and tests for a case the spec does not ask for, and one bad record would stop
  the update of the whole inventory. It can still be added later if callers turn out to send invalid data.

## Consequences

- Simpler helpers; each rule only has to be right for valid items.
- Valid input behaves exactly as before: old and new code give identical output for all 11,052 valid start states
  (7 names, `sellIn` -15..20, quality 0..50, Sulfuras 80) over 25 days. All tests, the golden master and TextTest
  pass; JaCoCo 100%; PIT 24/24 killed.
- Out-of-contract input now gives different, clamped results (3,680 of the 25,452 start states of ADR 0010 differ).
