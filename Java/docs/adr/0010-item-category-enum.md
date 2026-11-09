# 10. Item rules as an enum with a method per constant

Date: 2026-11-09

## Status

Accepted. The handling of invalid input is amended by [0011](0011-invalid-input-outside-contract.md).

## Context

`updateQuality()` was one loop of deeply nested `if`s that mixed the rules of all item kinds, repeated the quality
limit checks (`< 50`, `> 0`) and compared item names in several places. Step 2 refactors it before the Conjured
feature (Step 3) is added. The kata forbids changing `Item` and the `items` field, and callers (`TexttestFixture`,
the tests) create plain `Item` objects that `GildedRose` must update in place.

Options considered:

1. **Subclasses of `Item`** (`AgedBrie extends Item`, ...), each overriding an update method.
2. **An enum of item categories, with a method body per constant**, outside `Item`.
3. **The same enum, with one `switch` expression** over it holding all rules.
4. **A strategy interface with one class per rule**, and a map from name to strategy.

## Decision

Option 2: package-private `enum ItemCategory { NORMAL, AGED_BRIE, BACKSTAGE_PASS, LEGENDARY }`. Each constant
overrides `abstract void update(Item)` with its own rule. `ItemCategory.of(Item)` maps the item's name to its
category; unknown names are `NORMAL`. `GildedRose.updateQuality()` is reduced to
`ItemCategory.of(item).update(item)` for each item.

Shared steps are small private helpers: `ageOneDay`, `isPastSellByDate`, `increaseQuality`, `decreaseQuality`. The
quality helpers keep quality within 0..50 but, like the original code, leave a quality that is already outside that
range (invalid input) unchanged instead of clamping it. A plain clamp would have been simpler, but it changes the
result for 2,240 of the start states below, and Step 2 must not change behaviour.

Rejected:

- **Subclasses (1)**: callers create plain `Item`s, so `GildedRose` would have to copy them into subclass instances
  and would then update the copies, not the caller's objects. The kind of an item follows from its name, i.e. from
  data, so a name-based factory is needed anyway. And extending `Item` couples our rules to the goblin's class, which
  goes against the kata's rule.
- **One switch (3)**: equally valid and exhaustive-checked by the compiler, but puts all rules in one method. A
  method per constant keeps each rule next to its name, like the spec does.
- **Strategy classes (4)**: more types and wiring for four small rules, with no benefit over an enum's closed set.

## Consequences

- Each rule reads like its paragraph in the spec; adding Conjured items (Step 3) is one new constant plus one line in
  `of()`, which is also where "what counts as Conjured" is decided.
- Name matching lives in one place.
- Verification: all characterization tests, the golden master and TextTest pass; JaCoCo 100%; PIT 24/24 mutants
  killed. In addition, old and new code were run side by side on 25,452 start states (7 names including Conjured and
  unknown names, `sellIn` -15..20, quality -10..90) for 25 days each: identical output. The same comparison detects
  the plain-clamp variant mentioned above, which the unit tests do not.
