import java.util.*;

/** TUF 325 - Unbounded knapsack. Unlimited copies of each item (wt[i], val[i]); maximise total value with total weight <= cap. */
public class P325_UnboundedKnapsack {

    /** Approach 1: recursion over (item index, remaining capacity); a taken item stays available. Exponential time. */
    static int recursive(int[] wt, int[] val, int cap) {
        return solve(wt.length - 1, cap, wt, val);
    }

    /** Best value using items 0..i with capacity w left. */
    static int solve(int i, int w, int[] wt, int[] val) {
        if (i == 0) return (w / wt[0]) * val[0];                     // only item 0 left: take as many as fit
        int notTake = solve(i - 1, w, wt, val);
        int take = wt[i] <= w ? val[i] + solve(i, w - wt[i], wt, val) : 0;   // stay on i: unlimited copies
        return Math.max(notTake, take);
    }

    /** Approach 2: memoization on (i, w). O(n * cap) time, O(n * cap) space plus the stack. */
    static int memoization(int[] wt, int[] val, int cap) {
        int[][] dp = new int[wt.length][cap + 1];
        for (int[] row : dp) Arrays.fill(row, -1);                       // values are >= 0, so -1 = unknown
        return memo(wt.length - 1, cap, wt, val, dp);
    }

    static int memo(int i, int w, int[] wt, int[] val, int[][] dp) {
        if (i == 0) return (w / wt[0]) * val[0];
        if (dp[i][w] != -1) return dp[i][w];
        int notTake = memo(i - 1, w, wt, val, dp);
        int take = wt[i] <= w ? val[i] + memo(i, w - wt[i], wt, val, dp) : 0;
        return dp[i][w] = Math.max(notTake, take);
    }

    /** Approach 3: tabulation, dp[i][w] = best value from items 0..i with capacity w. O(n * cap) time and space. */
    static int tabulation(int[] wt, int[] val, int cap) {
        int n = wt.length;
        int[][] dp = new int[n][cap + 1];
        for (int w = 0; w <= cap; w++) dp[0][w] = (w / wt[0]) * val[0];
        for (int i = 1; i < n; i++) {
            for (int w = 0; w <= cap; w++) {
                int notTake = dp[i - 1][w];
                int take = wt[i] <= w ? val[i] + dp[i][w - wt[i]] : 0;     // same row: item i may be taken again
                dp[i][w] = Math.max(notTake, take);
            }
        }
        return dp[n - 1][cap];
    }

    /** Approach 4: one array, capacities left to right. O(n * cap) time, O(cap) space. */
    static int spaceOptimized(int[] wt, int[] val, int cap) {
        int[] best = new int[cap + 1];                                   // with no items, every capacity is worth 0
        for (int i = 0; i < wt.length; i++) {
            for (int w = wt[i]; w <= cap; w++) {                         // left to right: best[w - wt[i]] may hold item i
                best[w] = Math.max(best[w], val[i] + best[w - wt[i]]);
            }
        }
        return best[cap];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] wt, int[] val, int cap, int expected) {
        String in = Arrays.toString(wt) + " " + Arrays.toString(val) + " cap=" + cap;
        check(recursive(wt, val, cap) == expected, "recursive failed for " + in);
        check(memoization(wt, val, cap) == expected, "memoization failed for " + in);
        check(tabulation(wt, val, cap) == expected, "tabulation failed for " + in);
        check(spaceOptimized(wt, val, cap) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 4, 6}, new int[]{5, 11, 13}, 10, 27);     // two of weight 4 and one of weight 2
        verify(new int[]{2, 1}, new int[]{1, 1}, 3, 3);               // three copies of the weight-1 item
        verify(new int[]{1, 3, 4, 5}, new int[]{6, 1, 7, 7}, 8, 48);  // eight copies of weight 1, value 6
        verify(new int[]{5, 6}, new int[]{10, 13}, 10, 20);           // best ratio (6, 13) first would give only 13
        verify(new int[]{3, 4}, new int[]{5, 6}, 2, 0);               // edge: nothing fits
        verify(new int[]{7}, new int[]{9}, 0, 0);                     // edge: capacity 0
        verify(new int[]{7}, new int[]{9}, 20, 18);                   // single item: floor(20 / 7) = 2 copies
        verify(new int[]{3, 3}, new int[]{4, 5}, 9, 15);              // same weight: always the more valuable copy

        // Cross-check on seeded random inputs.
        Random rnd = new Random(325);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(4), cap = rnd.nextInt(20);
            int[] wt = new int[n], val = new int[n];
            for (int i = 0; i < n; i++) { wt[i] = 1 + rnd.nextInt(8); val[i] = rnd.nextInt(20); }
            verify(wt, val, cap, spaceOptimized(wt, val, cap));
        }
        System.out.println("OK P325_UnboundedKnapsack");
    }
}
