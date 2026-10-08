# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 01-26 | published |
| 27-38 | agents finishing; user asked to stop after these |
| 39-62 | not started (on hold until the user asks to continue) |

Publish a finished batch: `tools/publish.sh "Batch NN: <topic>" NN [NN...]` (validates, stages only that batch, rebuilds index, commits, pushes).
Then check GitHub Actions "Java solutions check": the run summary lists every failing class with its error.
