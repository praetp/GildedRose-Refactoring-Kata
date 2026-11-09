# 1. Record architecture decisions

Date: 2026-11-03

## Status

Accepted

## Context

This project is built together with an LLM agent. Decisions must be traceable so that the agent does not ask the same
question twice and reviewers can understand why the code looks the way it does.

## Decision

We record every decision as an Architectural Decision Record (ADR) in `docs/adr/`, using Michael Nygard's format
(Status, Context, Decision, Consequences). Files are numbered sequentially: `NNNN-short-title.md`. New ADRs start
from `template.md`. An ADR that is replaced is not deleted; its status becomes "Superseded by NNNN".

## Consequences

- Every non-trivial choice costs a short write-up, but the history of decisions lives next to the code.
- The agent must check existing ADRs before asking the user about something that may already be decided.
