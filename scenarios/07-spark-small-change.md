# 07 - small change skips slice

Setup: output of 01.

Prompt:

```
/spark Show how long ago each article was published, like "3 h ago", under the news site name.
```

## Expect

- [ ] Few questions (format, locale, update while the screen is open).
- [ ] Suggests `blueprint` directly, not `slice`.
- [ ] `/blueprint` on the task produces `implement-plan.md` (not `implement-plans/`) with one or two steps,
      reusing the existing `Article.publishedAt`.

## Findings

-
