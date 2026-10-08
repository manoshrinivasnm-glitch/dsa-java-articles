import java.util.*;

/**
 * TUF 320 - Partition a set into two subsets with minimum absolute sum difference.
 * Put every element of the non-negative array arr into one of two groups; minimise |sum1 - sum2|.
 */
public class P320_PartitionASetIntoTwoSubsetsWithMinimumAb {

    /** Approach 1: recursion that tries every subset as group 1. O(2^n) time, O(n) stack. */
    static int recursive(int[] arr) {
        int total = 0;
        for (int x : arr) total += x;
        return solve(0, 0, arr, total);
    }

    /** Smallest |total - 2 * finalSum| reachable when elements i.. are still undecided and group 1 holds s so far. */
    static int solve(int i, int s, int[] arr, int total) {
        if (i == arr.length) return Math.abs(total - 2 * s);         // group 2 holds total - s
        int take = solve(i + 1, s + arr[i], arr, total);
        int skip = solve(i + 1, s, arr, total);
        return Math.min(take, skip);
    }

    /** Approach 2: memoization on (i, s). O(n * total) time, O(n * total) space plus the stack. */
    static int memoization(int[] arr) {
        int total = 0;
        for (int x : arr) total += x;
        int[][] dp = new int[arr.length][total + 1];
        for (int[] row : dp) Arrays.fill(row, -1);                     // differences are >= 0, so -1 = unknown
        return memo(0, 0, arr, total, dp);
    }

    static int memo(int i, int s, int[] arr, int total, int[][] dp) {
        if (i == arr.length) return Math.abs(total - 2 * s);
        if (dp[i][s] != -1) return dp[i][s];
        int take = memo(i + 1, s + arr[i], arr, total, dp);
        int skip = memo(i + 1, s, arr, total, dp);
        return dp[i][s] = Math.min(take, skip);
    }

    /** Approach 3: subset-sum table for every target, then scan the last row. O(n * total) time and space. */
    static int tabulation(int[] arr) {
        int n = arr.length, total = 0;
        for (int x : arr) total += x;
        boolean[][] dp = new boolean[n][total + 1];                    // dp[i][t]: some subset of arr[0..i] sums to t
        for (int i = 0; i < n; i++) dp[i][0] = true;
        dp[0][arr[0]] = true;                                          // arr[0] <= total always
        for (int i = 1; i < n; i++) {
            for (int t = 1; t <= total; t++) {
                dp[i][t] = dp[i - 1][t] || (arr[i] <= t && dp[i - 1][t - arr[i]]);
            }
        }
        int best = Integer.MAX_VALUE;
        for (int s1 = 0; s1 <= total / 2; s1++) {                      // the smaller group is at most half
            if (dp[n - 1][s1]) best = Math.min(best, total - 2 * s1);
        }
        return best;
    }

    /** Approach 4: one reachable-sums array, updated right to left. O(n * total) time, O(total) space. */
    static int spaceOptimized(int[] arr) {
        int total = 0;
        for (int x : arr) total += x;
        int half = total / 2;
        boolean[] reach = new boolean[half + 1];                       // only sums up to half can be the smaller group
        reach[0] = true;
        for (int x : arr) {
            for (int t = half; t >= x; t--) {
                if (reach[t - x]) reach[t] = true;
            }
        }
        for (int s1 = half; s1 >= 0; s1--) {                           // the largest reachable s1 <= half wins
            if (reach[s1]) return total - 2 * s1;
        }
        return total;                                                  // unreachable: reach[0] is always true
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int expected) {
        String in = Arrays.toString(arr);
        check(recursive(arr) == expected, "recursive failed for " + in);
        check(memoization(arr) == expected, "memoization failed for " + in);
        check(tabulation(arr) == expected, "tabulation failed for " + in);
        check(spaceOptimized(arr) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 6, 11, 5}, 1);             // {1, 5, 6} = 12 and {11} = 11
        verify(new int[]{1, 2, 3, 4}, 0);              // {1, 4} and {2, 3}
        verify(new int[]{8, 6, 5}, 3);                 // {8} = 8 and {6, 5} = 11
        verify(new int[]{3, 1, 5, 2, 8}, 1);           // total 19: {3, 5, 2} = 10 and {1, 8} = 9
        verify(new int[]{7}, 7);                       // edge: a single element, the other group is empty
        verify(new int[]{0, 0}, 0);                    // edge: zeros
        verify(new int[]{1, 100}, 99);                 // one big element dominates
        verify(new int[]{2, 2, 2, 2, 2, 2, 2}, 2);     // seven equal elements: 8 versus 6

        // Cross-check on seeded random arrays, zeros included.
        Random rnd = new Random(320);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(12);
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(20);
            verify(arr, recursive(arr));
        }
        System.out.println("OK P320_PartitionASetIntoTwoSubsetsWithMinimumAb");
    }
}
