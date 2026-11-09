# 12. Enforce the contract with exceptions

Date: 2026-11-09

## Status

Accepted. Supersedes the "no validation" part of [0011](0011-invalid-input-outside-contract.md).

## Context

ADR 0011 documented the contract of `updateQuality()` but left the result for invalid items undefined. `Item` has
public, mutable fields owned by the goblin, so anyone can put invalid values in at any time, and we do not control
where items come from. Silently producing nonsense for bad data hides the bug that caused it.

## Decision

`updateQuality()` enforces its contract and fails fast:

- A `null` item or `null` name: `NullPointerException` with a clear message (`Objects.requireNonNull`), the Java
  convention for null arguments.
- A quality outside 0..50: `IllegalArgumentException`, message `Quality must be 0 to 50: <item>`.
- A legendary item (Sulfuras) with a quality other than exactly 80: `IllegalArgumentException`, message
  `Quality of a legendary item must be 80: <item>`. The spec says its quality *is* 80.
- `sellIn` is not checked: any value is valid; negative means the sell-by date has passed.

All items are validated before any is updated, so the update is all-or-nothing: if one item is invalid, no item
changes. Each `ItemCategory` validates its own items: `validate(Item)` checks 0..50 by default, and `LEGENDARY`
overrides it. The quality helpers stay plain clamps (ADR 0011), as invalid data no longer reaches them.

The tests (`GildedRoseContractTest`) were written first and failed before the implementation.

## Consequences

- Bad data is reported at once, with the offending item in the message.
- One invalid item stops the update of the whole inventory; that is intended, as it is a bug to be fixed at the
  source. If the inn ever needs to keep going, skipping and logging invalid items would be the alternative.
- Each item category is looked up twice per update (validate, then update); negligible for an inn's inventory.
