#!/usr/bin/env bash
# Publish finished batches: validate, stage only their files, rebuild index, commit, push.
# Usage: tools/publish.sh "commit message" 13 14 ...
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"; cd "$ROOT"
msg="$1"; shift
python3 tools/validate.py "$@"
files=()
for b in "$@"; do
  while IFS= read -r id; do
    files+=("p/$id.md")
    j="$(sed -n 's/^java:[[:space:]]*//p' "p/$id.md" | head -1)"; [ -n "$j" ] && files+=("$j")
  done < <(python3 -c "import json,sys; [print(p['id']) for p in json.load(open('tools/batches/%02d.json' % int(sys.argv[1])))]" "$b")
done
git add -- "${files[@]}"
python3 tools/build_site.py
git add -- "${files[@]}" index.md all.md topics tools _data ARTICLE_GUIDE.md README.md .github 2>/dev/null || true
# prev/next stamping may touch already-published articles; stage those too (only tracked ones)
git diff --name-only -- p/ | xargs -r git add --
git -c commit.gpgsign=false commit -q -m "$msg

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
git push -q origin main
git log --oneline -1
