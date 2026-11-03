# 2. Single agent instruction file

Date: 2026-11-03

## Status

Accepted

## Context

Claude Code automatically loads `CLAUDE.md`. The project goals and working rules were already written in `AGENT.md`.
Keeping two files with overlapping content would let them drift apart.

## Decision

`AGENT.md` is the only instruction file. `CLAUDE.md` is a symbolic link to `AGENT.md`.

The kata requirements live in the parent repo. `GildedRoseRequirements.md` in this project is a symbolic link to
`../GildedRoseRequirements.md`, so the spec is available here without copying it.

## Consequences

- There is one file to edit; Claude Code still picks it up.
- Symlinks need a filesystem that supports them (fine on Linux and macOS; on Windows git must be configured for them).
