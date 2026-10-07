# DSA Java Articles

Self-written explanations and **Java** solutions for every problem in Striver's A2Z DSA sheet, the SDE sheet and Blind 75
(583 unique problems). Each article walks from brute force to the optimal approach with code, complexity and pitfalls.

Site: https://manoshrinivasnm-glitch.github.io/dsa-java-articles/

## How the pieces fit

| Path | What it is |
|---|---|
| `p/<id>.md` | One article per problem, named by the sheet's TUF ID. Rendered at `/p/<id>.html`. |
| `java/P<id>_<Name>.java` | All approaches for that problem plus a `main` with assertions. |
| `_data/problems.json` | Master list of the 583 problems (title, difficulty, topic, which sheets). |
| `tools/build_site.py` | Regenerates `index.md`, `all.md`, `topics/*.md` and prev/next links. Run before committing. |
| `tools/check_java.sh` | Compiles every Java file and runs its tests. Also runs in GitHub Actions on every push. |
| `ARTICLE_GUIDE.md` | The writing and coding standard every article follows. |

## Linking from the progress sheet

Every page URL is derived from the TUF ID, so a single formula fixes the whole Resource column.
With the TUF ID in column `M` (A2Z tab) or `K` (SDE and Blind 75 tabs):

```
=HYPERLINK("https://manoshrinivasnm-glitch.github.io/dsa-java-articles/p/"&M2&".html","Article")
```

## Verify locally (optional)

Needs any JDK 17+ on the PATH:

```
tools/check_java.sh            # all solutions
tools/check_java.sh P37_TwoSum # one solution
```
