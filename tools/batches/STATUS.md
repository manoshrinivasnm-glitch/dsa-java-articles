# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 01-23 | published |
| 24-35 | stopped mid-write at the user's request; draft files may exist on disk (uncommitted). Resume each with the "RESUMING AN INTERRUPTED RUN" agent prompt, which reviews existing drafts first |
| 36-62 | not started |

Publish a finished batch: `tools/publish.sh "Batch NN: <topic>" NN [NN...]` (validates, stages only that batch, rebuilds index, commits, pushes).
Then check GitHub Actions "Java solutions check": the run summary lists every failing class with its error.
