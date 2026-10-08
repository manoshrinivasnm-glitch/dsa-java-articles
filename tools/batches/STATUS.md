# Batch status (62 batches, tools/batches/NN.json)

| Batch | State |
|---|---|
| 01-50, 53 | published (483 articles) |
| 51, 52, 54, 55, 56, 57, 58, 59, 60, 61, 62 | not started (paused at the user's 60% session-limit cap) |

Publish a finished batch: `tools/publish.sh "Batch NN: <topic>" NN` (validates, compiles and runs the batch's Java locally, stages only that batch, rebuilds index, commits, pushes).
