import java.util.*;

/** TUF 2797 - Minimum sum path in the matrix. Cheapest top-left to bottom-right path moving only right or down. */
public class P2797_MinimumSumPathInTheMatrix {

    /** Approach 1: plain recursion from the target cell back to (0, 0). O(2^(m+n)) time, O(m+n) stack. */
    static int recursion(int[][] grid) {
        return solve(grid.length - 1, grid[0].length - 1, grid);
    }

    static int solve(int i, int j, int[][] grid) {
        if (i == 0 && j == 0) return grid[0][0];
        if (i < 0 || j < 0) return Integer.MAX_VALUE;        // off the grid: never the cheaper option
        int fromUp = solve(i - 1, j, grid);
        int fromLeft = solve(i, j - 1, grid);
        return grid[i][j] + Math.min(fromUp, fromLeft);
    }

    /** Approach 2: the same recursion with a memo table. O(m*n) time, O(m*n) space plus O(m+n) stack. */
    static int memoization(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(m - 1, n - 1, grid, dp);
    }

    static int memo(int i, int j, int[][] grid, int[][] dp) {
        if (i == 0 && j == 0) return grid[0][0];
        if (i < 0 || j < 0) return Integer.MAX_VALUE;
        if (dp[i][j] != -1) return dp[i][j];
        int fromUp = memo(i - 1, j, grid, dp);
        int fromLeft = memo(i, j - 1, grid, dp);
        return dp[i][j] = grid[i][j] + Math.min(fromUp, fromLeft);
    }

    /** Approach 3: bottom-up table filled row by row. O(m*n) time, O(m*n) space. */
    static int tabulation(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];                            // dp[i][j]: cheapest path sum from (0,0) to (i,j)
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) dp[i][j] = grid[0][0];
                else if (i == 0) dp[i][j] = grid[i][j] + dp[i][j - 1];     // first row: only from the left
                else if (j == 0) dp[i][j] = grid[i][j] + dp[i - 1][j];     // first column: only from above
                else dp[i][j] = grid[i][j] + Math.min(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[m - 1][n - 1];
    }

    /** Approach 4: keep a single row; row[j] is overwritten in place. O(m*n) time, O(n) space. */
    static int spaceOptimised(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] row = new int[n];                                // before the update row[j] is the cell above
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) row[j] = grid[0][0];
                else if (i == 0) row[j] = grid[i][j] + row[j - 1];
                else if (j == 0) row[j] = grid[i][j] + row[j];
                else row[j] = grid[i][j] + Math.min(row[j], row[j - 1]);
            }
        }
        return row[n - 1];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int expected) {
        check(recursion(grid) == expected, "recursion " + Arrays.deepToString(grid));
        verifyFast(grid, expected);
    }

    static void verifyFast(int[][] grid, int expected) {
        check(memoization(grid) == expected, "memoization " + grid.length + "x" + grid[0].length);
        check(tabulation(grid) == expected, "tabulation " + grid.length + "x" + grid[0].length);
        check(spaceOptimised(grid) == expected, "spaceOptimised " + grid.length + "x" + grid[0].length);
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}, 7);
        verify(new int[][]{{1, 2, 3}, {4, 5, 6}}, 12);
        verify(new int[][]{{5, 9, 6}, {11, 5, 2}}, 21);
        verify(new int[][]{{5}}, 5);                                   // single cell
        verify(new int[][]{{1, 2, 3, 4}}, 10);                         // single row
        verify(new int[][]{{1}, {2}, {3}}, 6);                         // single column
        verify(new int[][]{{0, 0}, {0, 0}}, 0);                        // all zeros
        verify(new int[][]{{1, 1, 100}, {2, 100, 100}, {1, 1, 1}}, 6);  // greedy "cheaper neighbour" gets 104
        verify(new int[][]{{1, 100, 1, 1}, {1, 100, 1, 100}, {1, 1, 1, 100}, {100, 100, 1, 1}}, 7);
        int[][] ones = new int[200][200];
        for (int[] r : ones) Arrays.fill(r, 1);
        verifyFast(ones, 399);                                         // any monotone path visits m + n - 1 cells
        System.out.println("OK P2797_MinimumSumPathInTheMatrix");
    }
}
