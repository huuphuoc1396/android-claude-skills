---
name: implement
description: Execute an approved implement plan in an Android project: write production code and unit tests step by step, compiling as it goes. Does not commit. Use when asked to implement a plan file.
---

# Implement

Goal: turn an approved plan into working code and unit tests, step by step, so that every step leaves the
project compiling, the unit tests pass at the end, and the final diff matches the plan.

Do not re-ask anything answered in the linked task.md / subtask / plan.

## Before starting

1. Read the plan (`docs/features/<feature>/implement-plan.md` or `implement-plans/NN-<slug>.md`) and the
   requirement it links to.
   - If `Status` is not `Approved`, ask the user to confirm the plan first.
   - If no plan exists, suggest `implement-plan` and stop.
2. Read `CLAUDE.md` and the `.claude/rules/*.md` matching the modules in the plan's Changes overview.
3. Check `git status`. If there are unrelated uncommitted changes, tell the user before touching anything.
4. Set the plan `Status: In progress` and, if the plan came from a subtask, the subtask's
   `Status: In progress`.

## For each step, in order

1. **Read before writing.** Open the files to modify and the reference/reuse components the step names.
   Match the surrounding code: naming, structure, comment density, idioms.
2. **Write the code** for the step — only what the step describes.
3. **Write the unit tests** listed in the step, using the project's test doubles and rules (no new mocking
   library unless the project uses one).
4. **Compile** the modules the step touched, main and unit test sources, without running tests (e.g.
   `./gradlew :<module>:compile<Variant>Kotlin :<module>:compile<Variant>UnitTestKotlin`; use the
   project's equivalent tasks if it differs). This catches errors before later steps build on them.
   If the step changed the public API of a shared module (`core:*`, `common`, or any module others depend
   on), also compile the modules that depend on it; find them by grepping build files for
   `project(":<module>")` or the matching type-safe accessor.
5. **Fix until it compiles.** Read the error and fix the cause; never suppress a compiler error without a
   reason. After 3 failed attempts on the same error, stop and report it with the output.
6. **Tick the step** in the plan (`### Step 1: ... ✅`) and move on.

Unit tests run once, in Finish. Run long Gradle commands in the background when they may exceed ~2 minutes.

Do **not** run UI tests, instrumented tests, or screenshot verification — they are too slow for this loop.
If a step changes UI that has screenshot tests, note in the final report that screenshots need to be
verified or re-recorded.

## Deviations from the plan

- **Small** (a renamed class, a missing import or dependency line, a different but equivalent existing
  helper, an extra private function): do it, and add a line to a `## Deviations` section at the end of the
  plan.
- **Large** (a component the plan relies on doesn't exist or doesn't fit, a new module or library, a schema
  change not planned, any change to behavior or acceptance criteria): **stop**, explain what you found and
  the options, and wait for the user. Update the plan with the decision before continuing.

## Guardrails

- Only change files the plan lists (plus build files, DI, and resources they need). No drive-by refactors.
- No speculative code: no unused parameters, abstractions, or TODO hooks "for later".
- Follow every "Do not" in CLAUDE.md (generated files, baselines, design system, dependency direction).
- New source files get the project's license header / formatting.

## Finish

1. Run the **default tier** of CLAUDE.md's "Before finishing a code change" over every touched module,
   plus the dependent modules of any shared module whose public API changed (build, unit tests,
   auto-format, baselines if dependencies changed). Run the full tier (format check,
   lint, screenshot, instrumented) only if the user asks for it.
2. **Fix failing tests.** Use the plan's Traceability table to find the step behind each failure, fix the
   cause in code (never delete or weaken a test to pass), and re-run only the failing modules. After 3
   failed attempts on the same failure, stop and report it with the output.
3. If formatting changed files, re-run the touched modules' build and unit tests once.
4. Walk the plan's Traceability table: confirm each AC/EC has code and a passing test (or a manual check).
5. Set the plan `Status: Done` and, if the input was a subtask, its `Status: Done`.
6. Report to the user:
   - Steps done, files changed (grouped by module), deviations.
   - Commands run and their results, stated honestly (pass / fail with output).
   - What was not verified: UI / instrumented / screenshot tests, manual "How to test" steps.
   - Do not commit. Ask whether to commit once they have reviewed, using the commit style in CLAUDE.md
     (or the style of recent `git log` if CLAUDE.md doesn't state one).
