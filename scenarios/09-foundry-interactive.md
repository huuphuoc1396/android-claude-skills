# 09 - foundry, Interactive mode

Setup: `restore 02` (no `docs/features/<slug>/` for My List yet).

Prompt:

```
/foundry Users want to see the titles they favorited on a separate My List screen, opened from the
profile avatar on Home.
```

Choose Interactive and no commits. Answer spark, slice and blueprint as in 03–05. Type `continue` after
subtask 01. When offered a fresh session after subtask 02, accept it. If slice made only two subtasks,
stop the session after subtask 01 instead.

## Expect

- [ ] Asks mode and commit questions in one round at the start.
- [ ] After choosing Interactive: every spark question round, the slice outline preview, and each plan
      approval happen as in scenarios 03–05.
- [ ] Waits for "continue" between subtasks.
- [ ] After two subtasks, with more left, offers to continue in a fresh session.
- [ ] In the new session, `/foundry` resumes at the first subtask by number that is not `Done` instead of
      starting over.

## Findings

-
