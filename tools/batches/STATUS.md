# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 01-43, 46 | published (425 articles) |
| 44, 45, 47 | stopped mid-write at the 50% session-limit stop; drafts on disk, uncommitted. Resume with the "RESUMING AN INTERRUPTED RUN" prompt |
| 48-62 | not started |

Publish a finished batch: `tools/publish.sh "Batch NN: <topic>" NN [NN...]` (validates, stages only that batch, rebuilds index, commits, pushes).
Then check GitHub Actions "Java solutions check": the run summary lists every failing class with its error.
