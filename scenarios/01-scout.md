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
- [ ] "Before finishing a code change" split into a default and a full tier.
- [ ] Module table lists all 8 modules with correct allowed dependencies (features never depend on
      `core:network` or `core:database`).
- [ ] Mentions the convention plugins in `build-logic/` (`sample.android.feature` etc.).
- [ ] "Reuse before you create" names `Sample*` design-system states, `@Dispatcher(SampleDispatchers.IO)`,
      `MainDispatcherRule`, `TestArticleRepository`.
- [ ] "Do not" covers hand-editing `core/database/schemas/` and using `Dispatchers.IO` directly.
- [ ] Reference feature: `feature:news`.

## Findings

-
