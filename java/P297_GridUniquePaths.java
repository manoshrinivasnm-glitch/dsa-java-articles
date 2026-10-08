import java.util.*;

/** TUF 297 - Grid unique paths. Count the paths from the top-left to the bottom-right cell of an m x n grid moving only right or down. */
public class P297_GridUniquePaths {

    /** Approach 1: plain recursion on the last move. Exponential time, O(m + n) stack. */
    static int bruteForce(int m, int n) {
        return countFrom(m - 1, n - 1);
    }

    static int countFrom(int r, int c) {
        if (r == 0 || c == 0) return 1;              // first row or first column: one straight path
        return countFrom(r - 1, c) + countFrom(r, c - 1);
    }

    /** Approach 2: the same recursion, caching each cell's answer. O(m * n) time, O(m * n) space. */
    static int memoized(int m, int n) {
        int[][] memo = new int[m][n];                // 0 means "not computed yet"; real answers are >= 1
        return countFromMemo(m - 1, n - 1, memo);
    }

    static int countFromMemo(int r, int c, int[][] memo) {
        if (r == 0 || c == 0) return 1;
        if (memo[r][c] != 0) return memo[r][c];
        return memo[r][c] = countFromMemo(r - 1, c, memo) + countFromMemo(r, c - 1, memo);
    }

    /** Approach 3: bottom-up table kept as a single row. O(m * n) time, O(n) space. */
    static int tabulation(int m, int n) {
        int[] row = new int[n];
        Arrays.fill(row, 1);                         // first row: exactly one way to reach each cell
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                row[c] += row[c - 1];                // old row[c] = from above, row[c - 1] = from the left
            }
        }
        return row[n - 1];
    }

    /** Approach 4: choose which of the m + n - 2 moves go down. O(min(m, n)) time, O(1) space. */
    static int optimal(int m, int n) {
        int total = m + n - 2;                       // every path has exactly this many moves
        int k = Math.min(m - 1, n - 1);              // pick positions for the rarer kind of move
        long ways = 1;
        for (int i = 1; i <= k; i++) {
            ways = ways * (total - k + i) / i;       // now ways == C(total - k + i, i), always an integer
        }
        return (int) ways;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int m, int n, int expected, boolean includeBrute) {
        int[] got = includeBrute
                ? new int[]{bruteForce(m, n), memoized(m, n), tabulation(m, n), optimal(m, n)}
                : new int[]{memoized(m, n), tabulation(m, n), optimal(m, n)};
        for (int g : got) check(g == expected, m + "x" + n + ": expected " + expected + " got " + Arrays.toString(got));
    }

    public static void main(String[] args) {
        verify(3, 7, 28, true);
        verify(7, 3, 28, true);                       // symmetric in m and n
        verify(3, 2, 3, true);
        verify(2, 2, 2, true);
        verify(1, 1, 1, true);                        // start is the goal: one empty path
        verify(1, 9, 1, true);                        // single row
        verify(6, 1, 1, true);                        // single column
        verify(10, 10, 48620, true);
        verify(23, 12, 193536720, false);             // too many paths for plain recursion
        verify(51, 9, 1916797311, false);             // answer close to Integer.MAX_VALUE
        for (int m = 1; m <= 9; m++) {                // cross-check every small grid
            for (int n = 1; n <= 9; n++) verify(m, n, optimal(m, n), true);
        }
        System.out.println("OK P297_GridUniquePaths");
    }
}
