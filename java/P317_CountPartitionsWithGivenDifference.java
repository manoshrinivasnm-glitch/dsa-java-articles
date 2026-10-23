import java.util.*;

/**
 * TUF 317 - Count partitions with given difference. Count the ways to put every element of the non-negative array arr
 * into S1 or S2 so that sum(S1) - sum(S2) == d (with sum(S1) >= sum(S2)), modulo 1e9+7.
 */
public class P317_CountPartitionsWithGivenDifference {

    static final int MOD = 1_000_000_007;

    /** Approach 1: recursion that sends each element to S1 or S2 and checks the difference at the end. O(2^n) time. */
    static int recursive(int[] arr, int d) {
        int total = 0;
        for (int x : arr) total += x;                   // sum of every element
        return assign(0, 0, arr, total, d);             // start with S1 empty
    }

    /** Ways to place arr[i..] given that S1 already holds s1; S2 holds everything else. */
    static int assign(int i, int s1, int[] arr, int total, int d) {
        if (i == arr.length) return (s1 - (total - s1) == d) ? 1 : 0;   // all placed: does the difference match?
        int toS1 = assign(i + 1, s1 + arr[i], arr, total, d);   // arr[i] goes to S1
        int toS2 = assign(i + 1, s1, arr, total, d);            // arr[i] goes to S2
        return (toS1 + toS2) % MOD;
    }

    /** The reduced target: sum(S2) = (total - d) / 2, or -1 when no partition can exist. */
    static int smallerSum(int[] arr, int d) {
        int total = 0;
        for (int x : arr) total += x;                   // sum of every element
        if (d > total || (total - d) % 2 != 0) return -1;   // S2 sum would be negative or not a whole number
        return (total - d) / 2;                         // from S1 + S2 = total and S1 - S2 = d
    }

    /** Approach 2: memoization of "count subsets with sum target" on (prefix length, t). O(n * target) time and space. */
    static int memoization(int[] arr, int d) {
        int target = smallerSum(arr, d);                // count subsets that can be S2
        if (target < 0) return 0;                       // no valid partition exists
        int[][] dp = new int[arr.length + 1][target + 1];   // dp[i][t]: subsets of first i, sum t
        for (int[] row : dp) Arrays.fill(row, -1);      // counts are >= 0, so -1 = unknown
        return memo(arr.length, target, arr, dp);
    }

    /** Subsets of the first i elements with sum exactly t. */
    static int memo(int i, int t, int[] arr, int[][] dp) {
        if (i == 0) return t == 0 ? 1 : 0;              // prefix-length base case: zeros are counted correctly
        if (dp[i][t] != -1) return dp[i][t];            // state solved before: reuse it
        int notTake = memo(i - 1, t, arr, dp);          // leave arr[i - 1] out
        int take = arr[i - 1] <= t ? memo(i - 1, t - arr[i - 1], arr, dp) : 0;   // use it, if it fits
        return dp[i][t] = (notTake + take) % MOD;       // store the combined count
    }

    /** Approach 3: tabulation of the same count. O(n * target) time and space. */
    static int tabulation(int[] arr, int d) {
        int target = smallerSum(arr, d);                // count subsets that can be S2
        if (target < 0) return 0;                       // no valid partition exists
        int n = arr.length;
        int[][] dp = new int[n + 1][target + 1];        // dp[i][t] = subsets of the first i elements with sum t
        dp[0][0] = 1;                                   // the empty prefix has one subset: {}
        for (int i = 1; i <= n; i++) {                  // bring in element arr[i - 1]
            for (int t = 0; t <= target; t++) {         // t starts at 0 so zeros are counted
                int notTake = dp[i - 1][t];             // subsets that leave arr[i - 1] out
                int take = arr[i - 1] <= t ? dp[i - 1][t - arr[i - 1]] : 0;   // subsets that use it
                dp[i][t] = (notTake + take) % MOD;
            }
        }
        return dp[n][target];                           // all elements, sum = target
    }

    /** Approach 4: one array updated from right to left. O(n * target) time, O(target) space. */
    static int spaceOptimized(int[] arr, int d) {
        int target = smallerSum(arr, d);                // count subsets that can be S2
        if (target < 0) return 0;                       // no valid partition exists
        int[] ways = new int[target + 1];               // ways[t] = subsets of the elements so far with sum t
        ways[0] = 1;                                    // the empty subset
        for (int x : arr) {
            // right to left, so ways[t - x] does not include x yet
            for (int t = target; t >= x; t--) ways[t] = (ways[t] + ways[t - x]) % MOD;
        }
        return ways[target];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int d, int expected) {
        String in = Arrays.toString(arr) + " d=" + d;
        check(recursive(arr, d) == expected, "recursive failed for " + in);
        check(memoization(arr, d) == expected, "memoization failed for " + in);
        check(tabulation(arr, d) == expected, "tabulation failed for " + in);
        check(spaceOptimized(arr, d) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{5, 2, 6, 4}, 3, 1);            // S1 = {6, 4} = 10, S2 = {5, 2} = 7
        verify(new int[]{1, 1, 1, 1}, 0, 6);            // choose which two of the four 1s form S2
        verify(new int[]{1, 1, 2, 3}, 1, 3);            // S2 sum 3: {1a, 2}, {1b, 2}, {3}
        verify(new int[]{1, 2, 3}, 1, 0);               // total 6, 6 - 1 is odd: impossible
        verify(new int[]{3}, 5, 0);                     // edge: d larger than the total
        verify(new int[]{3}, 3, 1);                     // edge: S1 = {3}, S2 = {}
        verify(new int[]{0, 0, 1}, 1, 4);               // zeros may sit on either side
        verify(new int[]{0}, 0, 2);                     // edge: {0} | {} and {} | {0}

        // Cross-check on seeded random arrays, zeros included.
        Random rnd = new Random(317);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(12), d = rnd.nextInt(20);
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(8);
            verify(arr, d, recursive(arr, d));
        }
        System.out.println("OK P317_CountPartitionsWithGivenDifference");
    }
}
