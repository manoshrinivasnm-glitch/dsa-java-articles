# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 01-12 | published |
| 13-24 | agents writing (13-17 resumed after interruption) |
| 25-62 | pending |

Publish a finished batch: `tools/publish.sh "Batch NN: <topic>" NN [NN...]` (validates, stages only that batch, rebuilds index, commits, pushes).
Then check GitHub Actions "Java solutions check": the run summary lists every failing class with its error.
