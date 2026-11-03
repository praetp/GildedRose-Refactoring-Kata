# 4. Disable the kata's placeholder test

Date: 2026-11-03

## Status

Accepted

## Context

The kata ships `GildedRoseTest.foo`, which fails on purpose (`assertEquals("fixme", ...)`) to prompt the developer to
write real tests. Our rule is that every commit builds and passes all tests, so this test would break every commit
from Step 0 onwards.

## Decision

Mark the test with `@Disabled` and a reason pointing to this ADR. It is not deleted, so it still shows up as skipped
in test reports. It is replaced by real tests in Step 2. Until then, the golden-master output in
`../texttests/ThirtyDays/stdout.gr` is the safety net for behaviour.

## Consequences

- The build is green from the start and stays a meaningful signal.
- A skipped test is visible in reports, so it will not be forgotten.
