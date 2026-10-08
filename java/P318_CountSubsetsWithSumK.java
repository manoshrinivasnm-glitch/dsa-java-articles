import java.util.*;

/** TUF 318 - Count subsets with sum K. Number of subsets (by index) of the non-negative array arr that sum to k, modulo 1e9+7. */
public class P318_CountSubsetsWithSumK {

    static final int MOD = 1_000_000_007;

    /** Approach 1: pick / not-pick recursion over the first i elements. O(2^n) time, O(n) stack. */
    static int recursive(int[] arr, int k) {
        return count(arr.length, k, arr);
    }

    /** Number of subsets of the first i elements (arr[0..i-1]) whose sum is exactly t. */
    static int count(int i, int t, int[] arr) {
        if (i == 0) return t == 0 ? 1 : 0;              // no elements left: only the empty subset, with sum 0
        int notTake = count(i - 1, t, arr);
        int take = arr[i - 1] <= t ? count(i - 1, t - arr[i - 1], arr) : 0;
        return (notTake + take) % MOD;
    }

    /** Approach 2: memoization on (i, t). O(n * k) time, O(n * k) space plus the stack. */
    static int memoization(int[] arr, int k) {
        int[][] dp = new int[arr.length + 1][k + 1];
        for (int[] row : dp) Arrays.fill(row, -1);      // counts are >= 0, so -1 = unknown
        return memo(arr.length, k, arr, dp);
    }

    static int memo(int i, int t, int[] arr, int[][] dp) {
        if (i == 0) return t == 0 ? 1 : 0;
        if (dp[i][t] != -1) return dp[i][t];
        int notTake = memo(i - 1, t, arr, dp);
        int take = arr[i - 1] <= t ? memo(i - 1, t - arr[i - 1], arr, dp) : 0;
        return dp[i][t] = (notTake + take) % MOD;
    }

    /** Approach 3: tabulation, dp[i][t] = subsets of the first i elements with sum t. O(n * k) time and space. */
    static int tabulation(int[] arr, int k) {
        int n = arr.length;
        int[][] dp = new int[n + 1][k + 1];
        dp[0][0] = 1;                                   // the empty prefix has exactly one subset: {}
        for (int i = 1; i <= n; i++) {
            for (int t = 0; t <= k; t++) {              // t starts at 0: zeros add subsets with sum 0
                int notTake = dp[i - 1][t];
                int take = arr[i - 1] <= t ? dp[i - 1][t - arr[i - 1]] : 0;
                dp[i][t] = (notTake + take) % MOD;
            }
        }
        return dp[n][k];
    }

    /** Approach 4: one array updated from right to left. O(n * k) time, O(k) space. */
    static int spaceOptimized(int[] arr, int k) {
        int[] ways = new int[k + 1];
        ways[0] = 1;
        for (int x : arr) {
            for (int t = k; t >= x; t--) {              // right to left: ways[t - x] still excludes x
                ways[t] = (ways[t] + ways[t - x]) % MOD;
            }
        }
        return ways[k];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int k, int expected) {
        String in = Arrays.toString(arr) + " k=" + k;
        check(recursive(arr, k) == expected, "recursive failed for " + in);
        check(memoization(arr, k) == expected, "memoization failed for " + in);
        check(tabulation(arr, k) == expected, "tabulation failed for " + in);
        check(spaceOptimized(arr, k) == expected, "spaceOptimized failed for " + in);
    }

    /** For inputs too large for plain recursion. */
    static void verifyFast(int[] arr, int k, int expected) {
        String in = "n=" + arr.length + " k=" + k;
        check(memoization(arr, k) == expected, "memoization failed for " + in);
        check(tabulation(arr, k) == expected, "tabulation failed for " + in);
        check(spaceOptimized(arr, k) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 2, 3}, 3, 3);                 // {1, 2a}, {1, 2b}, {3}
        verify(new int[]{1, 1, 4, 5}, 5, 3);                 // {1a, 4}, {1b, 4}, {5}
        verify(new int[]{2, 3, 5, 6, 8, 10}, 10, 3);         // {2, 8}, {10}, {2, 3, 5}
        verify(new int[]{0, 0, 1}, 1, 4);                    // each zero may or may not join {1}
        verify(new int[]{0, 0, 0}, 0, 8);                    // edge: all 2^3 subsets, including {}, sum to 0
        verify(new int[]{5}, 3, 0);                          // edge: no subset
        verify(new int[]{4}, 4, 1);                          // single element equal to k
        verify(new int[]{1, 2, 3}, 0, 1);                    // k = 0: only the empty subset

        // Modulo: 60 zeros and k = 0 give 2^60 subsets.
        int[] zeros = new int[60];
        long expect = 1;
        for (int i = 0; i < 60; i++) expect = expect * 2 % MOD;
        verifyFast(zeros, 0, (int) expect);

        // Cross-check on seeded random arrays (zeros included) against a bitmask enumeration.
        Random rnd = new Random(318);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(12), k = rnd.nextInt(25);
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(10);
            int brute = 0;
            for (int mask = 0; mask < (1 << n); mask++) {
                int s = 0;
                for (int i = 0; i < n; i++) if ((mask >> i & 1) == 1) s += arr[i];
                if (s == k) brute++;
            }
            verify(arr, k, brute);
        }
        System.out.println("OK P318_CountSubsetsWithSumK");
    }
}
