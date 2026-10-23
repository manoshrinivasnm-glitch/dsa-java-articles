import java.util.*;

/** TUF 300 - Unique Paths II (LeetCode 63). Count right/down paths through a grid where 1 marks a blocked cell. */
public class P300_UniquePathsII {

    /** Approach 1: recursion from the target back to the start. O(2^(m+n)) time, O(m + n) stack. */
    static int recursive(int[][] grid) {
        return paths(grid.length - 1, grid[0].length - 1, grid);   // count paths into the bottom-right cell
    }

    /** Number of obstacle-free paths from (0, 0) to (i, j). */
    static int paths(int i, int j, int[][] grid) {
        if (i < 0 || j < 0 || grid[i][j] == 1) return 0;       // off the grid or blocked: no path goes through here
        if (i == 0 && j == 0) return 1;                         // reached the (open) start
        return paths(i - 1, j, grid) + paths(i, j - 1, grid);   // arrived from above or from the left
    }

    /** Approach 2: memoization on the m x n cells. O(m * n) time, O(m * n) space plus the stack. */
    static int memoization(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];                             // dp[i][j] = paths from (0, 0) to (i, j)
        for (int[] row : dp) Arrays.fill(row, -1);              // -1 = not computed yet
        return memo(m - 1, n - 1, grid, dp);
    }

    static int memo(int i, int j, int[][] grid, int[][] dp) {
        if (i < 0 || j < 0 || grid[i][j] == 1) return 0;       // off the grid or blocked: no paths
        if (i == 0 && j == 0) return 1;                         // reached the (open) start
        if (dp[i][j] != -1) return dp[i][j];                    // cell solved before: reuse it
        return dp[i][j] = memo(i - 1, j, grid, dp) + memo(i, j - 1, grid, dp);   // above + left, stored
    }

    /** Approach 3: tabulation row by row. O(m * n) time, O(m * n) space. */
    static int tabulation(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];                             // dp[i][j] = paths from (0, 0) to (i, j)
        for (int i = 0; i < m; i++) {                           // row by row, so above and left are ready
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) { dp[i][j] = 0; continue; }  // blocked cells hold 0 paths
                if (i == 0 && j == 0) { dp[i][j] = 1; continue; } // the open start: one empty path
                int up = i > 0 ? dp[i - 1][j] : 0;              // paths arriving from above (none on row 0)
                int left = j > 0 ? dp[i][j - 1] : 0;            // paths arriving from the left (none on column 0)
                dp[i][j] = up + left;
            }
        }
        return dp[m - 1][n - 1];                                // paths into the bottom-right cell
    }

    /** Approach 4: a single row of the table. O(m * n) time, O(n) space. */
    static int spaceOptimized(int[][] grid) {
        int n = grid[0].length;
        int[] row = new int[n];                                 // paths into each cell of the current row
        row[0] = 1;                                             // one way to "arrive" at the start, cleared below if it is blocked
        for (int[] cells : grid) {                              // sweep the grid one row at a time
            for (int j = 0; j < n; j++) {
                if (cells[j] == 1) row[j] = 0;                  // a blocked cell cuts every path through it
                else if (j > 0) row[j] += row[j - 1];           // from above (old row[j]) + from the left
            }
        }
        return row[n - 1];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int expected) {
        String in = Arrays.deepToString(grid);
        check(recursive(grid) == expected, "recursive failed for " + in);
        check(memoization(grid) == expected, "memoization failed for " + in);
        check(tabulation(grid) == expected, "tabulation failed for " + in);
        check(spaceOptimized(grid) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}, 2);           // around the centre either way
        verify(new int[][]{{0, 1}, {0, 0}}, 1);
        verify(new int[][]{{0, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 0, 1}, {1, 0, 0, 0}}, 3);
        verify(new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, 6);           // no obstacles: same as Unique Paths
        verify(new int[][]{{1}}, 0);                                        // edge: start blocked
        verify(new int[][]{{0}}, 1);                                        // edge: 1 x 1 free grid
        verify(new int[][]{{0, 0}, {0, 1}}, 0);                             // edge: target blocked
        verify(new int[][]{{0, 0, 1, 0, 0}}, 0);                            // single row cut by a wall
        verify(new int[][]{{0, 1, 0}, {1, 0, 0}, {0, 0, 0}}, 0);            // start is boxed in

        // Cross-check every approach on seeded random grids (small enough for plain recursion).
        Random rnd = new Random(300);
        for (int t = 0; t < 300; t++) {
            int[][] grid = new int[1 + rnd.nextInt(6)][1 + rnd.nextInt(6)];
            for (int[] row : grid) for (int j = 0; j < row.length; j++) row[j] = rnd.nextInt(5) == 0 ? 1 : 0;
            verify(grid, tabulation(grid));
        }
        System.out.println("OK P300_UniquePathsII");
    }
}
