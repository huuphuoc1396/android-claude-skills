# 09 - foundry, Interactive mode

Setup: output of 01 and 02, no `docs/`.

Prompt:

```
/foundry Users want to bookmark articles from the news list and see them on a separate Bookmarks screen.
```

## Expect

- [ ] Asks mode and commit questions in one round at the start.
- [ ] After choosing Interactive: every spark question round, the slice outline preview, and each plan
      approval happen as in scenarios 03–05.
- [ ] Waits for "continue" between subtasks.
- [ ] Interrupt after subtask 01, start a new session, run `/foundry` again: resumes at subtask 02
      instead of starting over.

## Findings

-
