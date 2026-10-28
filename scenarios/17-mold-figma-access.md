# 17 - mold: Figma access and token rules

Checks how mold gets into Figma and handles the token, which needs no real design. Part C is optional and
needs a Figma file you can open.

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

## C - real access (optional)

In a new session with a Figma MCP connected or `FIGMA_TOKEN` set, run A's prompt with a link to a real
frame.

- [ ] Says which access it uses.
- [ ] Lists the frames with a structure call first; detail calls go to one screen frame at a time, never a
      whole page.
- [ ] With the REST API, saves raw responses outside the repo and filters them with `jq` or a script
      before reading.
- [ ] Screenshots land in `screens/`; each screen file's Key is the Figma node id.

## Findings

-
