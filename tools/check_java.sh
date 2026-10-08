#!/usr/bin/env bash
# Compile every Java solution and run each class's main() (which contains its own assertions).
# Usage: tools/check_java.sh [P37_TwoSum ...]   (no args = all)
# Reports every failing class by name; on GitHub Actions also writes annotations and a step summary.
set -uo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JDK_BIN="$ROOT/tools/jdk/Contents/Home/bin"
if [ -x "$JDK_BIN/javac" ]; then export PATH="$JDK_BIN:$PATH"; fi
# Fall back to the JDK bundled with the VS Code Java extension, if present.
if ! javac -version >/dev/null 2>&1; then
  for d in "$HOME"/.vscode/extensions/redhat.java-*/jre/*/bin; do [ -x "$d/javac" ] && export PATH="$d:$PATH" && break; done
fi
javac -version >/dev/null 2>&1 || { echo "javac not found. Install a JDK 17+"; exit 2; }
TIMEOUT=""; command -v timeout >/dev/null && TIMEOUT="timeout 20"
OUT="$ROOT/out"; rm -rf "$OUT"; mkdir -p "$OUT/classes"
if [ $# -eq 0 ]; then files=("$ROOT"/java/*.java); else files=(); for c in "$@"; do files+=("$ROOT/java/$c.java"); done; fi

annotate() { # file title message
  [ -n "${GITHUB_ACTIONS:-}" ] || return 0
  local msg="${3//'%'/'%25'}"; msg="${msg//$'\n'/'%0A'}"
  echo "::error file=java/$1.java,title=$2::$msg"
}
compile_failed=(); run_failed=(); passed=0
echo "compiling ${#files[@]} file(s)..."
if ! javac -nowarn -Xlint:-options -d "$OUT/classes" "${files[@]}" 2> "$OUT/javac_all.txt"; then
  echo "batch compile failed; compiling files one by one to isolate errors"
  for f in "${files[@]}"; do
    cls="$(basename "$f" .java)"
    if ! javac -nowarn -Xlint:-options -d "$OUT/classes" "$f" 2> "$OUT/$cls.javac.txt"; then
      compile_failed+=("$cls")
      echo "COMPILE FAIL $cls"; head -20 "$OUT/$cls.javac.txt"
      annotate "$cls" "compile error" "$(head -8 "$OUT/$cls.javac.txt")"
    fi
  done
fi
for f in "${files[@]}"; do
  cls="$(basename "$f" .java)"
  [[ " ${compile_failed[*]-} " == *" $cls "* ]] && continue
  if out="$($TIMEOUT java -ea -cp "$OUT/classes" "$cls" 2>&1)" && grep -q "OK $cls" <<<"$out"; then
    passed=$((passed+1))
  else
    run_failed+=("$cls"); echo "RUN FAIL $cls"; echo "$out" | tail -8; echo "$out" | tail -15 > "$OUT/$cls.run.txt"
    annotate "$cls" "test failure" "$(echo "$out" | grep -m3 -E 'Exception|Error|at ' ; echo "$out" | tail -2)"
  fi
done
total=${#files[@]}; nc=${#compile_failed[@]}; nr=${#run_failed[@]}
echo "passed $passed / $total, compile failures $nc, test failures $nr"
if [ -n "${GITHUB_STEP_SUMMARY:-}" ]; then
  {
    echo "## Java solutions: $passed / $total passed"
    if [ "$nc" -gt 0 ]; then echo; echo "### Compile failures ($nc)"; for c in "${compile_failed[@]}"; do echo "- \`$c\`"; echo '```'; head -12 "$OUT/$c.javac.txt"; echo '```'; done; fi
    if [ "$nr" -gt 0 ]; then echo; echo "### Test failures ($nr)"; for c in "${run_failed[@]}"; do echo "- \`$c\`"; echo '```'; cat "$OUT/$c.run.txt"; echo '```'; done; fi
  } >> "$GITHUB_STEP_SUMMARY"
fi
[ "$nc" -eq 0 ] && [ "$nr" -eq 0 ]
