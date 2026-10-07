#!/usr/bin/env bash
# Compile every Java solution and run each class's main() (which contains its own assertions).
# Usage: tools/check_java.sh [P37_TwoSum ...]   (no args = all)
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JDK_BIN="$ROOT/tools/jdk/Contents/Home/bin"
if [ -x "$JDK_BIN/javac" ]; then export PATH="$JDK_BIN:$PATH"; fi
command -v javac >/dev/null || { echo "javac not found. Install a JDK or unpack Temurin into tools/jdk"; exit 2; }
mkdir -p "$ROOT/out"
if [ $# -eq 0 ]; then
  files=("$ROOT"/java/*.java)
else
  files=(); for c in "$@"; do files+=("$ROOT/java/$c.java"); done
fi
echo "compiling ${#files[@]} file(s)..."
javac -Xlint:-options -d "$ROOT/out" "${files[@]}"
fail=0; pass=0
for f in "${files[@]}"; do
  cls="$(basename "$f" .java)"
  if out="$(java -ea -cp "$ROOT/out" "$cls" 2>&1)"; then pass=$((pass+1)); else fail=$((fail+1)); echo "FAIL $cls"; echo "$out" | tail -5; fi
done
echo "passed $pass, failed $fail"
[ "$fail" -eq 0 ]
