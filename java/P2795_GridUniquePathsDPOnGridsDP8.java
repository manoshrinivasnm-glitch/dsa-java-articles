import java.util.*;

/** TUF 2795 - Grid Unique Paths (LeetCode 62). Count right/down paths from the top-left to the bottom-right cell of an m x n grid. */
public class P2795_GridUniquePathsDPOnGridsDP8 {

    /** Approach 1: recursion from the target back to the start. O(2^(m+n)) time, O(m + n) stack. */
    static int recursive(int m, int n) {
        return paths(m - 1, n - 1);
    }

    /** Number of paths from (0, 0) to (i, j). */
    static int paths(int i, int j) {
        if (i == 0 && j == 0) return 1;                         // reached the start
        if (i < 0 || j < 0) return 0;                           // walked off the grid
        return paths(i - 1, j) + paths(i, j - 1);               // arrived from above or from the left
    }

    /** Approach 2: memoization on the m x n cells. O(m * n) time, O(m * n) space plus the stack. */
    static int memoization(int m, int n) {
        int[][] dp = new int[m][n];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(m - 1, n - 1, dp);
    }

    static int memo(int i, int j, int[][] dp) {
        if (i == 0 && j == 0) return 1;
        if (i < 0 || j < 0) return 0;
        if (dp[i][j] != -1) return dp[i][j];
        return dp[i][j] = memo(i - 1, j, dp) + memo(i, j - 1, dp);
    }

    /** Approach 3: tabulation row by row. O(m * n) time, O(m * n) space. */
    static int tabulation(int m, int n) {
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) { dp[i][j] = 1; continue; }
                int up = i > 0 ? dp[i - 1][j] : 0;
                int left = j > 0 ? dp[i][j - 1] : 0;
                dp[i][j] = up + left;
            }
        }
        return dp[m - 1][n - 1];
    }

    /** Approach 4: one row is enough; row[j] still holds the value from above when we update it. O(m * n) time, O(n) space. */
    static int spaceOptimized(int m, int n) {
        int[] row = new int[n];
        row[0] = 1;
        for (int i = 0; i < m; i++) {
            for (int j = 1; j < n; j++) {
                row[j] += row[j - 1];                           // from above (old row[j]) + from the left (new row[j - 1])
            }
        }
        return row[n - 1];
    }

    /** Approach 5: every path is m-1 downs and n-1 rights in some order, so the answer is C(m+n-2, min(m-1, n-1)). O(min(m, n)) time, O(1) space. */
    static int combinatorics(int m, int n) {
        int total = m + n - 2;
        int r = Math.min(m - 1, n - 1);
        long result = 1;
        for (int i = 1; i <= r; i++) {
            result = result * (total - r + i) / i;              // result is now C(total - r + i, i): always an integer
        }
        return (int) result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyAll(int m, int n, int expected) {
        check(recursive(m, n) == expected, "recursive failed for " + m + " x " + n);
        verifyFast(m, n, expected);
    }

    static void verifyFast(int m, int n, int expected) {
        String in = m + " x " + n;
        check(memoization(m, n) == expected, "memoization failed for " + in);
        check(tabulation(m, n) == expected, "tabulation failed for " + in);
        check(spaceOptimized(m, n) == expected, "spaceOptimized failed for " + in);
        check(combinatorics(m, n) == expected, "combinatorics failed for " + in);
    }

    public static void main(String[] args) {
        verifyAll(3, 7, 28);
        verifyAll(3, 2, 3);                                     // DDR, DRD, RDD
        verifyAll(2, 2, 2);
        verifyAll(3, 3, 6);
        verifyAll(1, 1, 1);                                     // edge: start is the target
        verifyAll(1, 5, 1);                                     // edge: single row, only rights
        verifyAll(6, 1, 1);                                     // edge: single column, only downs
        verifyAll(10, 10, 48_620);
        verifyFast(17, 17, 601_080_390);
        verifyFast(51, 9, 1_916_797_311);                       // close to Integer.MAX_VALUE
        verifyFast(100, 2, 100);
        System.out.println("OK P2795_GridUniquePathsDPOnGridsDP8");
    }
}
