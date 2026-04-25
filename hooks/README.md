# Git Hooks

Install the versioned hooks once per clone:

```sh
git config core.hooksPath hooks
```

The `pre-commit` hook runs `ktlintCheck` and blocks the commit when ktlint fails.
