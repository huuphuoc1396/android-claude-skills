#!/usr/bin/env bash
# Run scenarios with scenarios/runner/run.py, creating its virtualenv on first use.
#   scenarios/run.sh all | <numbers> | skill:<name> [--dry-run] [--figma-url <link>] ...
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"
venv=scenarios/runner/.venv
if [ ! -x "$venv/bin/python" ]; then
  uv venv -q "$venv"
  VIRTUAL_ENV=$venv uv pip install -q -r scenarios/runner/requirements.txt
fi
exec "$venv/bin/python" scenarios/runner/run.py "$@"
