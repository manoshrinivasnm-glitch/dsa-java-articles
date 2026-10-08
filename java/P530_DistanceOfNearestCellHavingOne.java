import java.util.*;

/** TUF 530 - Distance of nearest cell having one. For every cell return the Manhattan distance to the closest 1 (-1 if the grid has no 1). */
public class P530_DistanceOfNearestCellHavingOne {

    /** Approach 1: for every cell, measure the distance to every 1 and keep the minimum. O((n*m)^2) time, O(1) extra space. */
    static int[][] bruteForce(int[][] grid) {
        int n = grid.length, m = grid[0].length;
        int[][] dist = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int best = Integer.MAX_VALUE;
                for (int r = 0; r < n; r++) {
                    for (int c = 0; c < m; c++) {
                        if (grid[r][c] == 1) best = Math.min(best, Math.abs(i - r) + Math.abs(j - c));
                    }
                }
                dist[i][j] = best == Integer.MAX_VALUE ? -1 : best;
            }
        }
        return dist;
    }

    /** Approach 2: multi-source BFS that starts from every 1 at once. O(n*m) time, O(n*m) space. */
    static int[][] optimal(int[][] grid) {
        int n = grid.length, m = grid[0].length;
        int[][] dist = new int[n][m];
        Deque<int[]> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 1) {
                    dist[i][j] = 0;
                    q.offer(new int[]{i, j});            // every 1 is a source at distance 0
                } else {
                    dist[i][j] = -1;                     // -1 = not reached yet
                }
            }
        }
        int[] dr = {-1, 0, 1, 0}, dc = {0, 1, 0, -1};
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            for (int k = 0; k < 4; k++) {
                int r = cell[0] + dr[k], c = cell[1] + dc[k];
                if (r >= 0 && r < n && c >= 0 && c < m && dist[r][c] == -1) {
                    dist[r][c] = dist[cell[0]][cell[1]] + 1;
                    q.offer(new int[]{r, c});
                }
            }
        }
        return dist;
    }

    /** Approach 3: two DP sweeps, top-left to bottom-right and back. O(n*m) time, O(1) extra space besides the answer. */
    static int[][] twoPassDp(int[][] grid) {
        int n = grid.length, m = grid[0].length, INF = n + m;   // larger than any real distance
        int[][] d = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 1) { d[i][j] = 0; continue; }
                d[i][j] = INF;
                if (i > 0) d[i][j] = Math.min(d[i][j], d[i - 1][j] + 1);      // best 1 reached from above
                if (j > 0) d[i][j] = Math.min(d[i][j], d[i][j - 1] + 1);      // best 1 reached from the left
            }
        }
        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                if (i < n - 1) d[i][j] = Math.min(d[i][j], d[i + 1][j] + 1);  // from below
                if (j < m - 1) d[i][j] = Math.min(d[i][j], d[i][j + 1] + 1);  // from the right
            }
        }
        for (int[] row : d) {
            for (int j = 0; j < m; j++) if (row[j] >= INF) row[j] = -1;     // no 1 anywhere
        }
        return d;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int[][] expected) {
        String tag = Arrays.deepToString(grid);
        check(Arrays.deepEquals(bruteForce(grid), expected), "bruteForce " + tag);
        check(Arrays.deepEquals(optimal(grid), expected), "optimal " + tag);
        check(Arrays.deepEquals(twoPassDp(grid), expected), "twoPassDp " + tag);
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 1, 1, 0}, {1, 1, 0, 0}, {0, 0, 1, 1}},
               new int[][]{{1, 0, 0, 1}, {0, 0, 1, 1}, {1, 1, 0, 0}});
        verify(new int[][]{{1, 0, 1}, {1, 1, 0}, {1, 0, 0}},
               new int[][]{{0, 1, 0}, {0, 0, 1}, {0, 1, 2}});
        verify(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}},
               new int[][]{{2, 1, 2}, {1, 0, 1}, {2, 1, 2}});
        verify(new int[][]{{0, 0, 0, 1}}, new int[][]{{3, 2, 1, 0}});            // single row
        verify(new int[][]{{1}}, new int[][]{{0}});                              // single cell
        verify(new int[][]{{0, 0}, {0, 0}}, new int[][]{{-1, -1}, {-1, -1}});    // no 1 at all
        Random rnd = new Random(530);                                            // seeded random grids vs brute force
        for (int t = 0; t < 30; t++) {
            int n = 1 + rnd.nextInt(25), m = 1 + rnd.nextInt(25);
            int[][] g = new int[n][m];
            for (int[] row : g) for (int j = 0; j < m; j++) row[j] = rnd.nextInt(10) == 0 ? 1 : 0;
            verify(g, bruteForce(g));
        }
        System.out.println("OK P530_DistanceOfNearestCellHavingOne");
    }
}
