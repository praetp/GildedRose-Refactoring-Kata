# 3. Maximum line length for documents

Date: 2026-11-03

## Status

Accepted

## Context

Long lines are hard to read in editors, terminals and diffs.

## Decision

All documents we produce (Markdown, ADRs, etc.) are at most 120 columns per line. `.editorconfig` sets
`max_line_length = 120` for `*.md` so editors show and apply the limit.

Upstream kata files we did not write (`README.md`, `GildedRoseRequirements.md`) are left unchanged.

## Consequences

- Prose must be wrapped by hand or by the editor.
- Diffs of documents stay readable.
