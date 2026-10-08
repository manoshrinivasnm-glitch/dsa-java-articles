# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 01-38, 42 | published |
| 39, 40, 41, 43 | agents finishing (last wave before the 50% session-limit stop) |
| 44-62 | not started (on hold until the user asks to continue) |

Publish a finished batch: `tools/publish.sh "Batch NN: <topic>" NN [NN...]` (validates, stages only that batch, rebuilds index, commits, pushes).
Then check GitHub Actions "Java solutions check": the run summary lists every failing class with its error.
