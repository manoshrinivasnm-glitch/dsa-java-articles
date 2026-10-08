# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 01-24 | published |
| 25-35 | agents resumed 2026-10-08 (finishing drafts) |
| 36 | agent writing |
| 37-62 | not started |

Publish a finished batch: `tools/publish.sh "Batch NN: <topic>" NN [NN...]` (validates, stages only that batch, rebuilds index, commits, pushes).
Then check GitHub Actions "Java solutions check": the run summary lists every failing class with its error.
