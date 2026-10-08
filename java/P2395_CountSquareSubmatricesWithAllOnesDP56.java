import java.util.*;

/** TUF 2395 - Count Square Submatrices with All Ones. Count every all-ones square of every size. */
public class P2395_CountSquareSubmatricesWithAllOnesDP56 {

    /** Approach 1: grow a square from every top-left corner until it hits a 0. O(n m min(n,m)^2) time, O(1) space. */
    static int bruteForce(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length, total = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                for (int s = 1; i + s <= n && j + s <= m; s++) {
                    boolean ok = true;                          // only the new bottom row and right column are unchecked
                    for (int t = 0; t < s && ok; t++) {
                        if (matrix[i + s - 1][j + t] == 0 || matrix[i + t][j + s - 1] == 0) ok = false;
                    }
                    if (!ok) break;                             // a bigger square would contain this 0 too
                    total++;
                }
            }
        }
        return total;
    }

    /** Approach 2: f(i, j) = side of the largest all-ones square ending at (i, j); sum over all cells. Exponential. */
    static int recursive(int[][] matrix) {
        int total = 0;
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[0].length; j++) total += solve(i, j, matrix);
        return total;
    }

    static int solve(int i, int j, int[][] matrix) {
        if (matrix[i][j] == 0) return 0;
        if (i == 0 || j == 0) return 1;                         // first row or column: only the 1x1 square fits
        return 1 + Math.min(solve(i - 1, j, matrix), Math.min(solve(i, j - 1, matrix), solve(i - 1, j - 1, matrix)));
    }

    /** Approach 3: the same recursion with a cache. O(n m) time, O(n m) space. */
    static int memoization(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length, total = 0;
        int[][] dp = new int[n][m];
        for (int[] row : dp) Arrays.fill(row, -1);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) total += memo(i, j, matrix, dp);
        return total;
    }

    static int memo(int i, int j, int[][] matrix, int[][] dp) {
        if (matrix[i][j] == 0) return 0;
        if (i == 0 || j == 0) return 1;
        if (dp[i][j] != -1) return dp[i][j];
        return dp[i][j] = 1 + Math.min(memo(i - 1, j, matrix, dp), Math.min(memo(i, j - 1, matrix, dp), memo(i - 1, j - 1, matrix, dp)));
    }

    /** Approach 4: bottom-up, top-left to bottom-right. O(n m) time, O(n m) space. */
    static int tabulation(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length, total = 0;
        int[][] dp = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (matrix[i][j] == 0) dp[i][j] = 0;
                else if (i == 0 || j == 0) dp[i][j] = 1;
                else dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                total += dp[i][j];                              // dp[i][j] squares have (i, j) as bottom-right corner
            }
        }
        return total;
    }

    /** Approach 5: one row of the table plus the saved diagonal. O(n m) time, O(m) space. */
    static int spaceOptimized(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length, total = 0;
        int[] dp = new int[m + 1];                              // dp[j + 1] is column j; dp[0] stays 0 as a wall
        for (int i = 0; i < n; i++) {
            int diag = 0;                                       // previous row's value at column j - 1
            for (int j = 0; j < m; j++) {
                int above = dp[j + 1];                          // still the previous row's value
                dp[j + 1] = matrix[i][j] == 0 ? 0 : 1 + Math.min(above, Math.min(dp[j], diag));
                diag = above;
                total += dp[j + 1];
            }
        }
        return total;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] matrix, int expected) {
        String in = Arrays.deepToString(matrix);
        check(bruteForce(matrix) == expected, "bruteForce failed for " + in);
        check(recursive(matrix) == expected, "recursive failed for " + in);
        check(memoization(matrix) == expected, "memoization failed for " + in);
        check(tabulation(matrix) == expected, "tabulation failed for " + in);
        check(spaceOptimized(matrix) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 1, 1, 1}, {1, 1, 1, 1}, {0, 1, 1, 1}}, 15);  // 10 + 4 + 1
        verify(new int[][]{{1, 0, 1}, {1, 1, 0}, {1, 1, 0}}, 7);            // 6 + 1
        verify(new int[][]{{1, 1}, {1, 1}}, 5);                             // 4 + 1
        verify(new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, 14);           // 9 + 4 + 1
        verify(new int[][]{{1, 1, 1, 1}}, 4);                               // a single row
        verify(new int[][]{{0}}, 0);                                        // edge: no ones
        verify(new int[][]{{1}}, 1);                                        // edge: single one
        verify(new int[][]{}, 0);                                           // edge: empty matrix

        Random rnd = new Random(2395);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(5), m = 1 + rnd.nextInt(5);
            int[][] g = new int[n][m];
            for (int[] row : g) for (int j = 0; j < m; j++) row[j] = rnd.nextInt(5) == 0 ? 0 : 1;
            verify(g, bruteForce(g));
        }
        System.out.println("OK P2395_CountSquareSubmatricesWithAllOnesDP56");
    }
}
