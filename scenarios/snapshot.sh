#!/usr/bin/env bash
# Save and restore the state of sample-app/ between scenarios, as local tags `snap/<name>`.
# The tags point at commits on no branch, so main and the index are never touched.
#
#   scenarios/snapshot.sh save <name>      # e.g. save 03 after scenario 03 passes
#   scenarios/snapshot.sh restore <name>   # `baseline` restores the sample-baseline tag
#   scenarios/snapshot.sh list
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"

usage() {
  echo "usage: $0 save <name> | restore <name> | list" >&2
  exit 2
}

ref() {
  if [ "$1" = baseline ]; then echo sample-baseline; else echo "snap/$1"; fi
}

case "${1:-}" in
  save)
    name=${2:-}; [ -n "$name" ] && [ "$name" != baseline ] || usage
    tmp=$(mktemp -d); trap 'rm -rf "$tmp"' EXIT
    export GIT_INDEX_FILE=$tmp/index
    git read-tree HEAD
    git add -A -- sample-app
    tree=$(git write-tree)
    unset GIT_INDEX_FILE
    commit=$(git commit-tree "$tree" -p HEAD -m "snapshot $name")
    git tag -f "snap/$name" "$commit" >/dev/null
    echo "Saved sample-app/ as snap/$name"
    ;;
  restore)
    name=${2:-}; [ -n "$name" ] || usage
    source=$(ref "$name")
    git rev-parse -q --verify "refs/tags/$source" >/dev/null || { echo "No snapshot $source" >&2; exit 1; }
    # Clean first: files the snapshot adds are untracked, so cleaning afterwards would delete them.
    git clean -fdq -- sample-app
    git restore --source="$source" --worktree -- sample-app
    echo "Restored sample-app/ from $source"
    ;;
  list)
    git tag -l 'snap/*' --format='%(refname:short)  %(creatordate:short)  %(subject)'
    ;;
  *)
    usage
    ;;
esac
