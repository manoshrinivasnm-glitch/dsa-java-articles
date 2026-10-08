import java.util.*;

/** TUF 298 - Minimum Falling Path Sum (LeetCode 931). Start anywhere in row 0, each step goes down, down-left or down-right. */
public class P298_MinimumFallingPathSum {

    static final int INF = 1_000_000_000;                       // "impossible": larger than any real path sum

    /** Approach 1: recursion from every cell of the last row. O(m * 3^n) time, O(n) stack. */
    static int recursive(int[][] matrix) {
        int n = matrix.length, m = matrix[0].length;
        int best = Integer.MAX_VALUE;
        for (int j = 0; j < m; j++) best = Math.min(best, solve(n - 1, j, matrix));
        return best;
    }

    /** Minimum sum of a falling path that starts somewhere in row 0 and ends at (i, j). */
    static int solve(int i, int j, int[][] matrix) {
        if (j < 0 || j >= matrix[0].length) return INF;         // stepped off the side
        if (i == 0) return matrix[0][j];                        // a path may start at any cell of row 0
        int up = solve(i - 1, j, matrix);
        int upLeft = solve(i - 1, j - 1, matrix);
        int upRight = solve(i - 1, j + 1, matrix);
        return matrix[i][j] + Math.min(up, Math.min(upLeft, upRight));
    }

    /** Approach 2: memoization on the n x m cells. O(n * m) time, O(n * m) space plus the stack. */
    static int memoization(int[][] matrix) {
        int n = matrix.length, m = matrix[0].length;
        int[][] dp = new int[n][m];
        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE);   // MAX_VALUE = not computed (sums can be negative)
        int best = Integer.MAX_VALUE;
        for (int j = 0; j < m; j++) best = Math.min(best, memo(n - 1, j, matrix, dp));
        return best;
    }

    static int memo(int i, int j, int[][] matrix, int[][] dp) {
        if (j < 0 || j >= matrix[0].length) return INF;
        if (i == 0) return matrix[0][j];
        if (dp[i][j] != Integer.MAX_VALUE) return dp[i][j];
        int up = memo(i - 1, j, matrix, dp);
        int upLeft = memo(i - 1, j - 1, matrix, dp);
        int upRight = memo(i - 1, j + 1, matrix, dp);
        return dp[i][j] = matrix[i][j] + Math.min(up, Math.min(upLeft, upRight));
    }

    /** Approach 3: tabulation from row 0 downward. O(n * m) time, O(n * m) space. */
    static int tabulation(int[][] matrix) {
        int n = matrix.length, m = matrix[0].length;
        int[][] dp = new int[n][m];
        for (int j = 0; j < m; j++) dp[0][j] = matrix[0][j];
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int up = dp[i - 1][j];
                int upLeft = j > 0 ? dp[i - 1][j - 1] : INF;
                int upRight = j < m - 1 ? dp[i - 1][j + 1] : INF;
                dp[i][j] = matrix[i][j] + Math.min(up, Math.min(upLeft, upRight));
            }
        }
        int best = Integer.MAX_VALUE;
        for (int j = 0; j < m; j++) best = Math.min(best, dp[n - 1][j]);
        return best;
    }

    /** Approach 4: row i reads only row i - 1, so keep two rows. O(n * m) time, O(m) space. */
    static int spaceOptimized(int[][] matrix) {
        int n = matrix.length, m = matrix[0].length;
        int[] prev = matrix[0].clone();
        for (int i = 1; i < n; i++) {
            int[] cur = new int[m];
            for (int j = 0; j < m; j++) {
                int up = prev[j];
                int upLeft = j > 0 ? prev[j - 1] : INF;
                int upRight = j < m - 1 ? prev[j + 1] : INF;
                cur[j] = matrix[i][j] + Math.min(up, Math.min(upLeft, upRight));
            }
            prev = cur;
        }
        int best = Integer.MAX_VALUE;
        for (int v : prev) best = Math.min(best, v);
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] matrix, int expected) {
        String in = Arrays.deepToString(matrix);
        check(recursive(matrix) == expected, "recursive failed for " + in);
        check(memoization(matrix) == expected, "memoization failed for " + in);
        check(tabulation(matrix) == expected, "tabulation failed for " + in);
        check(spaceOptimized(matrix) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{2, 1, 3}, {6, 5, 4}, {7, 8, 9}}, 13);            // 1 -> 5 -> 7 or 1 -> 4 -> 8
        verify(new int[][]{{-19, 57}, {-40, -5}}, -59);                      // negatives
        verify(new int[][]{{100, -42, -46, -41}, {31, 97, 10, -10}, {-58, -51, 82, 89}, {51, 81, 69, -51}}, -36);
        verify(new int[][]{{1, 100, 100}, {100, 100, 1}, {1, 100, 100}}, 102); // the 1 in row 1 is two columns away from both 1s
        verify(new int[][]{{1, 2, 3}, {9, 9, 1}}, 3);                        // greedy starts at 1 and pays 10
        verify(new int[][]{{7}}, 7);                                         // edge: 1 x 1
        verify(new int[][]{{5, 3, 8, 1}}, 1);                                // edge: one row, pick its minimum
        verify(new int[][]{{4}, {-2}, {6}}, 8);                              // edge: one column, a single path

        // Cross-check every approach on seeded random matrices (small enough for plain recursion).
        Random rnd = new Random(298);
        for (int t = 0; t < 200; t++) {
            int[][] matrix = new int[1 + rnd.nextInt(6)][1 + rnd.nextInt(5)];
            for (int[] row : matrix) for (int j = 0; j < row.length; j++) row[j] = rnd.nextInt(201) - 100;
            verify(matrix, tabulation(matrix));
        }
        System.out.println("OK P298_MinimumFallingPathSum");
    }
}
