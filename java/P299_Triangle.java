import java.util.*;

/** TUF 299 - Triangle (LeetCode 120). Minimum path sum from the top to the bottom row; from (i, j) step to (i+1, j) or (i+1, j+1). */
public class P299_Triangle {

    /** Approach 1: recursion from the apex. O(2^n) time, O(n) stack. */
    static int recursive(int[][] triangle) {
        return solve(0, 0, triangle);
    }

    /** Minimum sum of a path from (i, j) down to any cell of the last row. */
    static int solve(int i, int j, int[][] triangle) {
        if (i == triangle.length - 1) return triangle[i][j];    // last row: the path ends here
        int down = solve(i + 1, j, triangle);
        int diagonal = solve(i + 1, j + 1, triangle);
        return triangle[i][j] + Math.min(down, diagonal);
    }

    /** Approach 2: memoization on the n(n+1)/2 cells. O(n^2) time, O(n^2) space plus the stack. */
    static int memoization(int[][] triangle) {
        int n = triangle.length;
        int[][] dp = new int[n][];
        for (int i = 0; i < n; i++) {
            dp[i] = new int[i + 1];
            Arrays.fill(dp[i], Integer.MAX_VALUE);              // MAX_VALUE = not computed (sums can be negative)
        }
        return memo(0, 0, triangle, dp);
    }

    static int memo(int i, int j, int[][] triangle, int[][] dp) {
        if (i == triangle.length - 1) return triangle[i][j];
        if (dp[i][j] != Integer.MAX_VALUE) return dp[i][j];
        int down = memo(i + 1, j, triangle, dp);
        int diagonal = memo(i + 1, j + 1, triangle, dp);
        return dp[i][j] = triangle[i][j] + Math.min(down, diagonal);
    }

    /** Approach 3: tabulation from the last row up to the apex. O(n^2) time, O(n^2) space. */
    static int tabulation(int[][] triangle) {
        int n = triangle.length;
        int[][] dp = new int[n][];
        dp[n - 1] = triangle[n - 1].clone();                    // base case: the last row costs itself
        for (int i = n - 2; i >= 0; i--) {
            dp[i] = new int[i + 1];
            for (int j = 0; j <= i; j++) {
                dp[i][j] = triangle[i][j] + Math.min(dp[i + 1][j], dp[i + 1][j + 1]);
            }
        }
        return dp[0][0];
    }

    /** Approach 4: one array of the row below, updated left to right in place. O(n^2) time, O(n) space. */
    static int spaceOptimized(int[][] triangle) {
        int n = triangle.length;
        int[] below = triangle[n - 1].clone();
        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                below[j] = triangle[i][j] + Math.min(below[j], below[j + 1]);  // below[j + 1] is not overwritten yet
            }
        }
        return below[0];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] triangle, int expected) {
        String in = Arrays.deepToString(triangle);
        check(recursive(triangle) == expected, "recursive failed for " + in);
        check(memoization(triangle) == expected, "memoization failed for " + in);
        check(tabulation(triangle) == expected, "tabulation failed for " + in);
        check(spaceOptimized(triangle) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{2}, {3, 4}, {6, 5, 7}, {4, 1, 8, 3}}, 11);      // 2 + 3 + 5 + 1
        verify(new int[][]{{1}, {2, 3}}, 3);
        verify(new int[][]{{-1}, {2, 3}, {1, -1, -3}}, -1);                 // -1 + 3 + (-3)
        verify(new int[][]{{1}, {2, 9}, {10, 1, 1}, {10, 10, 10, 1}}, 12);  // greedy takes 2 and ends at 14; best is 1 + 9 + 1 + 1
        verify(new int[][]{{-10}}, -10);                                    // edge: a single row
        verify(new int[][]{{0}, {0, 0}, {0, 0, 0}}, 0);                     // all zeros

        // Cross-check every approach on seeded random triangles (small enough for plain recursion).
        Random rnd = new Random(299);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(10);
            int[][] triangle = new int[n][];
            for (int i = 0; i < n; i++) {
                triangle[i] = new int[i + 1];
                for (int j = 0; j <= i; j++) triangle[i][j] = rnd.nextInt(41) - 20;
            }
            verify(triangle, tabulation(triangle));
        }
        System.out.println("OK P299_Triangle");
    }
}
