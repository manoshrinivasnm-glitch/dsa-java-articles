# Article and solution guide

Every problem gets two files, both keyed by its TUF ID (the `id` field in `_data/problems.json`):

| File | Purpose |
|---|---|
| `p/<id>.md` | The article. Jekyll renders it at `/p/<id>.html`. |
| `java/P<id>_<CamelName>.java` | All approaches as static methods in one class, plus a `main` with assertions. |

## Article structure (`p/<id>.md`)

```
---
title: "Two Sum"
id: 37
difficulty: Easy
topic: "Solve Problems on Arrays [Easy -> Medium -> Hard]"   # from problems.json
section: "Medium"                                            # from problems.json
sheets: [a2z, sde, blind75]                                  # keys of problems.json "sheets"
leetcode: https://leetcode.com/problems/two-sum/             # omit if none
java: java/P37_TwoSum.java
---

## Problem
Own-words statement, constraints, then 2 examples each with a one-line explanation.

## Approach 1: Brute force
**Intuition.** 2-4 sentences: the obvious thing and why it is slow.
**Algorithm.** Numbered steps.
```java
// code copied verbatim from the Java file's method
```
**Complexity.** Time O(..), Space O(..), with one line of justification each.

## Approach 2: Better           (only when a genuinely distinct middle step exists)
same structure

## Approach 3: Optimal
same structure, plus a short **Dry run** table on example 1 when it helps.

## Edge cases and pitfalls
Bullets: empty input, duplicates, overflow (int vs long), negative numbers, off-by-one.

## Related problems
Bullets linking to other articles by id: [Title]({{ "/p/<id>.html" | relative_url }})
```

Rules:
- Explain like a strong mentor: why the approach works, not just what it does. No filler, no marketing.
- Theory rows (patterns, "Introduction to DP", "MST theory", language basics) get a **concept article**: explanation, worked examples, and Java code for every pattern or construct shown.
- Code blocks in the article must be byte-identical to the methods in the Java file (copy them after the tests pass).
- Never write `{{` or `{%` inside code unless escaped with `{% raw %}...{% endraw %}` (Liquid runs on the page).
- Keep each article self-contained; do not assume the reader has read earlier ones.

## Java file rules (`java/P<id>_<CamelName>.java`)

- Java 21, single public class named exactly like the file, no package statement.
- One static method per approach: `bruteForce(...)`, `better(...)`, `optimal(...)`; descriptive names are fine when there are several optimal variants (e.g. `optimalHashMap`).
- Helper types (ListNode, TreeNode, Pair) are nested static classes inside the same file so every file compiles on its own.
- `public static void main(String[] args)` runs at least 4 test cases through every approach using the `check` helper below, including an edge case. Print `OK <ClassName>` at the end.
- No `Scanner`/stdin, no randomness without a fixed seed, run time under 2 seconds.

```java
static void check(boolean cond, String msg) {
    if (!cond) throw new AssertionError(msg);
}
```

Verify with `tools/check_java.sh P37_TwoSum` (all files: `tools/check_java.sh`). Then run `python3 tools/build_site.py` to refresh the index and prev/next links.

## Code comments (every Java block shown in an article)

Every important line of code shown in an article carries a short plain-English comment saying what it
does or why. Model: `java/P188_CloneGraph.java` and `p/188.md`.

- Comment the lines a reader would pause on: what a key variable or data structure holds, what a loop
  walks over, what case an `if` detects, why a pointer moves or a value updates, what a non-obvious
  `return` gives back, and every base case in recursion.
- Skip lines that explain themselves: braces, `i++`, `queue.add(x)` right after the comment that
  explains the queue, a plain `return ans;`.
- Explain intent, not syntax: `// skip duplicates so each triplet is reported once`, not `// increment j`.
- Short, present tense, ASCII only (write `->`, not an arrow character), no Liquid (`{{` or `{%`).
- Trailing comment on the same line when the whole line stays under about 110 characters; otherwise
  put the comment on its own line directly above, at the same indentation. Align trailing comments
  within a method when it is easy to do.
- Never put a new comment line above a method signature or class declaration (the Javadoc stays
  where it is); comments go inside the body or at the end of a line.
- Change comments only. No reformatting, renaming, reordering or literal changes. Keep existing
  comments, improving them only if they are unclear.

Workflow per problem: edit `java/P<id>_*.java`, then
`python3 tools/codecomments.py check <id>` (must report 0 code changes),
`python3 tools/codecomments.py sync <id>` (rewrites the article's java blocks from the Java file),
then compile and run the class.
