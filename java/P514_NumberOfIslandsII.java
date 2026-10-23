import java.util.*;

/** TUF 514 - Number of islands II. Cells of an all-water n x m grid turn into land one by one; report the island count after each operation. */
public class P514_NumberOfIslandsII {

    static final int[] DR = {-1, 1, 0, 0}, DC = {0, 0, -1, 1};

    /** Approach 1: after every operation, recount islands with BFS. O(k * n * m) time, O(n * m) space. */
    static List<Integer> bruteForce(int n, int m, int[][] operators) {
        boolean[][] land = new boolean[n][m];                // land[r][c] = true once the cell is land
        List<Integer> answer = new ArrayList<>();            // island count after each operation
        for (int[] op : operators) {                         // apply the operations in order
            land[op[0]][op[1]] = true;                       // this cell becomes land
            answer.add(countIslands(land));                  // recount everything from scratch
        }
        return answer;
    }

    static int countIslands(boolean[][] land) {
        int n = land.length, m = land[0].length, count = 0;   // count = islands found so far
        boolean[][] seen = new boolean[n][m];                // land cells already flooded
        Deque<int[]> queue = new ArrayDeque<>();             // BFS queue of cells to flood
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (!land[r][c] || seen[r][c]) continue;     // water, or part of an island already counted
                count++;                                     // new island found, flood it
                seen[r][c] = true;
                queue.add(new int[]{r, c});
                while (!queue.isEmpty()) {
                    int[] cell = queue.poll();               // next cell of this island
                    for (int k = 0; k < 4; k++) {            // look at the 4 neighbours
                        int nr = cell[0] + DR[k], nc = cell[1] + DC[k];   // neighbour position
                        // inside the grid, land, and not flooded yet
                        if (nr >= 0 && nr < n && nc >= 0 && nc < m && land[nr][nc] && !seen[nr][nc]) {
                            seen[nr][nc] = true;
                            queue.add(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
        return count;
    }

    /** Disjoint Set Union with union by size and path compression. */
    static final class DisjointSet {
        final int[] parent, size;                              // parent links; size[r] = size of root r's set

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {                      // every element starts as its own set
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
            return parent[x];                                  // parent[x] is now the root itself
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);                    // roots of the two sets
            if (ra == rb) return false;                        // already in the same set
            if (size[ra] < size[rb]) {                         // keep ra as the root of the larger set
                int t = ra;
                ra = rb;
                rb = t;
            }
            parent[rb] = ra;                                   // hang the smaller tree under the larger
            size[ra] += size[rb];                              // ra's set absorbs rb's elements
            return true;                                       // two different sets were merged
        }
    }

    /** Approach 2: Disjoint Set; each new cell is +1 island, each successful union with a land neighbour is -1. O(n * m + k * alpha) time, O(n * m) space. */
    static List<Integer> disjointSet(int n, int m, int[][] operators) {
        DisjointSet ds = new DisjointSet(n * m);             // one set per cell, id = r * m + c
        boolean[][] land = new boolean[n][m];                // cells that are land so far
        List<Integer> answer = new ArrayList<>();
        int islands = 0;                                     // current number of islands
        for (int[] op : operators) {                         // apply the operations in order
            int r = op[0], c = op[1];
            if (land[r][c]) {                                // repeated operation: nothing changes
                answer.add(islands);
                continue;
            }
            land[r][c] = true;                               // the cell becomes land
            islands++;                                       // ... and on its own it is a new island
            for (int k = 0; k < 4; k++) {                    // look at the 4 neighbours
                int nr = r + DR[k], nc = c + DC[k];
                if (nr >= 0 && nr < n && nc >= 0 && nc < m && land[nr][nc]) {   // in the grid, already land
                    if (ds.union(r * m + c, nr * m + nc)) islands--;   // two islands joined into one
                }
            }
            answer.add(islands);                             // island count after this operation
        }
        return answer;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int m, int[][] ops, List<Integer> expected) {
        List<Integer> a = bruteForce(n, m, ops), b = disjointSet(n, m, ops);
        check(a.equals(expected), "bruteForce got " + a + " expected " + expected);
        check(b.equals(expected), "disjointSet got " + b + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(4, 5, new int[][]{ {1, 1}, {0, 1}, {3, 3}, {3, 4} }, List.of(1, 1, 2, 2));
        verify(4, 5, new int[][]{ {0, 0}, {0, 0}, {1, 1}, {1, 0}, {0, 1}, {3, 3}, {3, 4}, {3, 2}, {2, 2}, {1, 2}, {0, 0} },
                List.of(1, 1, 2, 1, 1, 2, 2, 2, 2, 1, 1));                     // includes repeated cells
        verify(3, 3, new int[][]{ {0, 0}, {0, 1}, {1, 2}, {2, 1} }, List.of(1, 1, 2, 3));
        verify(3, 3, new int[][]{ {0, 1}, {1, 0}, {1, 2}, {2, 1}, {1, 1} }, List.of(1, 2, 3, 4, 1));  // centre joins four
        verify(1, 1, new int[][]{ {0, 0} }, List.of(1));                       // single cell
        verify(2, 2, new int[][]{}, List.of());                                // no operations
        // seeded cross-check
        Random rnd = new Random(514);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(6), m = 1 + rnd.nextInt(6), k = rnd.nextInt(30);
            int[][] ops = new int[k][];
            for (int i = 0; i < k; i++) ops[i] = new int[]{rnd.nextInt(n), rnd.nextInt(m)};
            check(bruteForce(n, m, ops).equals(disjointSet(n, m, ops)), "random mismatch at trial " + t);
        }
        System.out.println("OK P514_NumberOfIslandsII");
    }
}
