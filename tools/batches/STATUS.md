# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 01-43 | published (414 articles) |
| 44-62 | not started (paused at ~50% of the 5-hour session limit; resume when the user asks) |

Publish a finished batch: `tools/publish.sh "Batch NN: <topic>" NN [NN...]` (validates, stages only that batch, rebuilds index, commits, pushes).
Then check GitHub Actions "Java solutions check": the run summary lists every failing class with its error.
