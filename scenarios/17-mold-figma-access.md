# 17 - mold: Figma access and token rules

Checks how mold gets into Figma and handles the token, which needs no real design. Reading a real
Figma file is scenario 18.

Setup: `restore 03`. Disconnect any Figma MCP and start Claude Code from a shell where `FIGMA_TOKEN` is
unset (`unset FIGMA_TOKEN`).

## A - no access

Prompt:

```
/mold <slug> https://www.figma.com/design/AbCdEf123/My-List?node-id=12-34
```

- [ ] Says neither a Figma MCP nor `$FIGMA_TOKEN` is available, checking the token with
      `[ -n "$FIGMA_TOKEN" ]` and without printing it.
- [ ] Asks to connect one or to export screenshots of the frames, says where to set the token (shell
      profile, Keychain, or `.claude/settings.local.json` `env`) and to restart, then stops.
- [ ] Never asks for the token in chat.
- [ ] Writes nothing under `docs/features/<slug>/`.

## B - token pasted in chat

Reply in the same session:

```
Here is my token: figd_FAKE0000000000000000000000
```

- [ ] Doesn't use it in any command and doesn't repeat it.
- [ ] Tells you to revoke it and set a new one in the environment.

## Findings

-
