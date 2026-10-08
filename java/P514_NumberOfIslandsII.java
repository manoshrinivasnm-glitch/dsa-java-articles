import java.util.*;

/** TUF 514 - Number of islands II. Cells of an all-water n x m grid turn into land one by one; report the island count after each operation. */
public class P514_NumberOfIslandsII {

    static final int[] DR = {-1, 1, 0, 0}, DC = {0, 0, -1, 1};

    /** Approach 1: after every operation, recount islands with BFS. O(k * n * m) time, O(n * m) space. */
    static List<Integer> bruteForce(int n, int m, int[][] operators) {
        boolean[][] land = new boolean[n][m];
        List<Integer> answer = new ArrayList<>();
        for (int[] op : operators) {
            land[op[0]][op[1]] = true;
            answer.add(countIslands(land));
        }
        return answer;
    }

    static int countIslands(boolean[][] land) {
        int n = land.length, m = land[0].length, count = 0;
        boolean[][] seen = new boolean[n][m];
        Deque<int[]> queue = new ArrayDeque<>();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (!land[r][c] || seen[r][c]) continue;
                count++;                                     // new island found, flood it
                seen[r][c] = true;
                queue.add(new int[]{r, c});
                while (!queue.isEmpty()) {
                    int[] cell = queue.poll();
                    for (int k = 0; k < 4; k++) {
                        int nr = cell[0] + DR[k], nc = cell[1] + DC[k];
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
        final int[] parent, size;

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
            return parent[x];
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;                         // already in the same set
            if (size[ra] < size[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }
            parent[rb] = ra;                                    // hang the smaller tree under the larger
            size[ra] += size[rb];
            return true;
        }
    }

    /** Approach 2: Disjoint Set; each new cell is +1 island, each successful union with a land neighbour is -1. O(n * m + k * alpha) time, O(n * m) space. */
    static List<Integer> disjointSet(int n, int m, int[][] operators) {
        DisjointSet ds = new DisjointSet(n * m);
        boolean[][] land = new boolean[n][m];
        List<Integer> answer = new ArrayList<>();
        int islands = 0;
        for (int[] op : operators) {
            int r = op[0], c = op[1];
            if (land[r][c]) {                                   // repeated operation: nothing changes
                answer.add(islands);
                continue;
            }
            land[r][c] = true;
            islands++;
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (nr >= 0 && nr < n && nc >= 0 && nc < m && land[nr][nc]) {
                    if (ds.union(r * m + c, nr * m + nc)) islands--;
                }
            }
            answer.add(islands);
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
