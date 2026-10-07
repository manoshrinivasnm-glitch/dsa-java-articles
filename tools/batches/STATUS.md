# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 02, 03, 04, 05, 06 | done, validated, pushed |
| 01, 07-17 | agents were writing when the session hit its usage limit; files on disk may be complete or partial per batch. Run `python3 tools/validate.py <n>` per batch, then build_site, commit, push |
| 18-62 | pending |

Resume loop: validate batch -> `python3 tools/build_site.py` -> commit -> push -> check Actions "Java solutions check" -> fix failures -> launch next batch agents (prompt in memory / earlier session).
