# 01 - scout

Setup: baseline.

Prompt:

```
/scout
```

Answer questions: say Hilt + Room + Retrofit are the target stack, nothing is legacy.

## Expect

- [ ] Asks for approval before writing; nothing written before the yes.
- [ ] `CLAUDE.md` at `sample-app/` root, about 100–150 lines.
- [ ] Commands verified with `--dry-run` or `tasks`, including `./gradlew testDebugUnitTest`.
- [ ] Setup names `TMDB_API_KEY` in the gitignored `local.properties`.
- [ ] "Before finishing a code change" split into a default and a full tier.
- [ ] Module table lists all 12 modules (`app`, 7 `core:*`, 4 `feature:*`) with correct allowed
      dependencies (features get `core:data` and `core:designsystem` through the feature plugin and never
      depend on `core:network`, `core:database`, or `core:datastore`).
- [ ] Mentions the convention plugins in `build-logic/` (`sample.android.feature` etc.).
- [ ] "Reuse before you create" names `Sample*` design-system components and states,
      `@Dispatcher(SampleDispatchers.IO)`, `MainDispatcherRule`, the `Test*Repository` doubles and
      `TestNetworkMonitor` in `core:testing`.
- [ ] "Do not" covers hand-editing `core/database/schemas/`, using `Dispatchers.IO` directly, and network
      or database models leaving `core`.
- [ ] Reference features: `feature:details` (most complete) and `feature:onboarding` (simplest), or a
      reasoned alternative.

## Findings

-
