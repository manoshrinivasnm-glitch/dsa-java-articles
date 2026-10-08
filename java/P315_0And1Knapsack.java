import java.util.*;

/** TUF 315 - 0/1 Knapsack. Items with weights wt[i] and values val[i]; each item taken at most once. Maximum total value with total weight <= W. */
public class P315_0And1Knapsack {

    /** Approach 1: plain recursion. solve(i, cap) = best value using items 0..i with capacity cap. O(2^n) time, O(n) stack. */
    static int recursion(int W, int[] wt, int[] val) {
        return solve(wt.length - 1, W, wt, val);
    }

    static int solve(int i, int cap, int[] wt, int[] val) {
        if (i < 0) return 0;                                   // no items left
        int skip = solve(i - 1, cap, wt, val);
        int take = wt[i] <= cap ? val[i] + solve(i - 1, cap - wt[i], wt, val) : Integer.MIN_VALUE;
        return Math.max(skip, take);
    }

    /** Approach 2: memoization. Only n * (W + 1) distinct states exist; cache each one. O(n * W) time, O(n * W) space plus O(n) stack. */
    static int memoization(int W, int[] wt, int[] val) {
        int n = wt.length;
        int[][] memo = new int[n][W + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solveMemo(n - 1, W, wt, val, memo);
    }

    static int solveMemo(int i, int cap, int[] wt, int[] val, int[][] memo) {
        if (i < 0) return 0;
        if (memo[i][cap] != -1) return memo[i][cap];
        int skip = solveMemo(i - 1, cap, wt, val, memo);
        int take = wt[i] <= cap ? val[i] + solveMemo(i - 1, cap - wt[i], wt, val, memo) : Integer.MIN_VALUE;
        return memo[i][cap] = Math.max(skip, take);
    }

    /** Approach 3: tabulation. dp[i][c] = best value using the first i items with capacity c. O(n * W) time and space. */
    static int tabulation(int W, int[] wt, int[] val) {
        int n = wt.length;
        int[][] dp = new int[n + 1][W + 1];                    // row 0: no items, value 0
        for (int i = 1; i <= n; i++) {
            for (int c = 0; c <= W; c++) {
                dp[i][c] = dp[i - 1][c];                       // skip item i - 1
                if (wt[i - 1] <= c) dp[i][c] = Math.max(dp[i][c], val[i - 1] + dp[i - 1][c - wt[i - 1]]);
            }
        }
        return dp[n][W];
    }

    /** Approach 4: one array, capacity scanned from high to low so every read still sees the previous row. O(n * W) time, O(W) space. */
    static int spaceOptimised(int W, int[] wt, int[] val) {
        int[] dp = new int[W + 1];
        for (int i = 0; i < wt.length; i++) {
            for (int c = W; c >= wt[i]; c--) {                 // right to left: dp[c - wt[i]] is still the old value
                dp[c] = Math.max(dp[c], val[i] + dp[c - wt[i]]);
            }
        }
        return dp[W];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int W, int[] wt, int[] val, int expected) {
        String in = "W=" + W + " wt=" + Arrays.toString(wt) + " val=" + Arrays.toString(val);
        check(recursion(W, wt, val) == expected, "recursion " + in);
        check(memoization(W, wt, val) == expected, "memoization " + in);
        check(tabulation(W, wt, val) == expected, "tabulation " + in);
        check(spaceOptimised(W, wt, val) == expected, "spaceOptimised " + in);
    }

    public static void main(String[] args) {
        verify(4, new int[]{4, 5, 1}, new int[]{1, 2, 3}, 3);
        verify(3, new int[]{4, 5, 6}, new int[]{1, 2, 3}, 0);                      // nothing fits
        verify(50, new int[]{10, 20, 30}, new int[]{60, 100, 120}, 220);
        verify(0, new int[]{1, 2}, new int[]{5, 6}, 0);                            // zero capacity
        verify(10, new int[]{}, new int[]{}, 0);                                   // no items
        verify(5, new int[]{1, 2, 3}, new int[]{10, 15, 40}, 55);
        verify(10, new int[]{5, 4, 6, 3}, new int[]{10, 40, 30, 50}, 90);
        verify(10, new int[]{6, 5, 5}, new int[]{30, 20, 20}, 40);                 // best ratio first would give 30
        verify(7, new int[]{3, 3, 3}, new int[]{5, 5, 5}, 10);                     // identical items, each used once

        // seeded random instances: all four must agree
        Random rnd = new Random(315);
        for (int t = 0; t < 300; t++) {
            int n = rnd.nextInt(13), W = rnd.nextInt(40);
            int[] wt = new int[n], val = new int[n];
            for (int i = 0; i < n; i++) {
                wt[i] = 1 + rnd.nextInt(15);
                val[i] = rnd.nextInt(50);
            }
            verify(W, wt, val, recursion(W, wt, val));
        }
        System.out.println("OK P315_0And1Knapsack");
    }
}
