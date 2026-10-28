# 09 - foundry, Interactive mode

Setup: output of 01 and 02, no `docs/features/<slug>/` for My List.

Prompt:

```
/foundry Users want to see the titles they favorited on a separate My List screen, opened from the
profile avatar on Home.
```

## Expect

- [ ] Asks mode and commit questions in one round at the start.
- [ ] After choosing Interactive: every spark question round, the slice outline preview, and each plan
      approval happen as in scenarios 03–05.
- [ ] Waits for "continue" between subtasks.
- [ ] After two subtasks, with more left, offers to continue in a fresh session.
- [ ] Interrupt after subtask 01, start a new session, run `/foundry` again: resumes at the first subtask
      by number that is not `Done` (02) instead of starting over.

## Findings

-
