import java.util.*;

/** TUF 2804 - Subset sum equal to target (DP-14). Does some subset of the positive array arr sum to exactly k? */
public class P2804_SubsetSumEqualToTargetDP14 {

    /** Approach 1: pick / not-pick recursion. O(2^n) time, O(n) stack. */
    static boolean recursive(int[] arr, int k) {
        return solve(arr.length - 1, k, arr);
    }

    /** Can some subset of arr[0..i] sum to exactly t? */
    static boolean solve(int i, int t, int[] arr) {
        if (t == 0) return true;                       // the empty subset already works
        if (i == 0) return arr[0] == t;                // one element left: take it or leave it
        boolean notTake = solve(i - 1, t, arr);
        boolean take = arr[i] <= t && solve(i - 1, t - arr[i], arr);
        return notTake || take;
    }

    /** Approach 2: memoization on (i, t). O(n * k) time, O(n * k) space plus the stack. */
    static boolean memoization(int[] arr, int k) {
        int[][] dp = new int[arr.length][k + 1];       // -1 = unknown, 0 = false, 1 = true
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(arr.length - 1, k, arr, dp);
    }

    static boolean memo(int i, int t, int[] arr, int[][] dp) {
        if (t == 0) return true;
        if (i == 0) return arr[0] == t;
        if (dp[i][t] != -1) return dp[i][t] == 1;
        boolean notTake = memo(i - 1, t, arr, dp);
        boolean take = arr[i] <= t && memo(i - 1, t - arr[i], arr, dp);
        dp[i][t] = (notTake || take) ? 1 : 0;
        return notTake || take;
    }

    /** Approach 3: tabulation, dp[i][t] = some subset of arr[0..i] sums to t. O(n * k) time and space. */
    static boolean tabulation(int[] arr, int k) {
        int n = arr.length;
        boolean[][] dp = new boolean[n][k + 1];
        for (int i = 0; i < n; i++) dp[i][0] = true;   // target 0: take nothing
        if (arr[0] <= k) dp[0][arr[0]] = true;         // row 0: only arr[0] itself is reachable
        for (int i = 1; i < n; i++) {
            for (int t = 1; t <= k; t++) {
                boolean notTake = dp[i - 1][t];
                boolean take = arr[i] <= t && dp[i - 1][t - arr[i]];
                dp[i][t] = notTake || take;
            }
        }
        return dp[n - 1][k];
    }

    /** Approach 4: keep only the previous row. O(n * k) time, O(k) space. */
    static boolean spaceOptimized(int[] arr, int k) {
        int n = arr.length;
        boolean[] prev = new boolean[k + 1];
        prev[0] = true;
        if (arr[0] <= k) prev[arr[0]] = true;
        for (int i = 1; i < n; i++) {
            boolean[] cur = new boolean[k + 1];
            cur[0] = true;
            for (int t = 1; t <= k; t++) {
                boolean notTake = prev[t];
                boolean take = arr[i] <= t && prev[t - arr[i]];
                cur[t] = notTake || take;
            }
            prev = cur;
        }
        return prev[k];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int k, boolean expected) {
        String in = Arrays.toString(arr) + " k=" + k;
        check(recursive(arr, k) == expected, "recursive failed for " + in);
        check(memoization(arr, k) == expected, "memoization failed for " + in);
        check(tabulation(arr, k) == expected, "tabulation failed for " + in);
        check(spaceOptimized(arr, k) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4}, 4, true);            // {4} or {1, 3}
        verify(new int[]{2, 5, 1, 6, 7}, 4, false);        // no combination makes 4
        verify(new int[]{3, 34, 4, 12, 5, 2}, 9, true);    // {4, 5}
        verify(new int[]{3, 34, 4, 12, 5, 2}, 30, false);
        verify(new int[]{5}, 5, true);                     // edge: single element equal to k
        verify(new int[]{5}, 3, false);                    // edge: single element, no match
        verify(new int[]{1000000000, 1, 2}, 3, true);      // an element far above k is simply never taken
        verify(new int[]{4, 6}, 0, true);                  // edge: k = 0 is met by the empty subset
        verify(new int[]{1, 1, 1, 1}, 4, true);            // duplicates: all four ones

        // Cross-check on seeded random arrays.
        Random rnd = new Random(2804);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(12), k = rnd.nextInt(40);
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = 1 + rnd.nextInt(15);
            boolean brute = false;                          // enumerate all 2^n subsets with a bitmask
            for (int mask = 0; mask < (1 << n) && !brute; mask++) {
                int s = 0;
                for (int i = 0; i < n; i++) if ((mask >> i & 1) == 1) s += arr[i];
                brute = s == k;
            }
            verify(arr, k, brute);
        }
        System.out.println("OK P2804_SubsetSumEqualToTargetDP14");
    }
}
