import java.util.*;

/** TUF 810 - Partition Array for Maximum Sum. Split into pieces of length <= k; each piece becomes its maximum. */
public class P810_PartitionArrayForMaximumSum {

    /** Approach 1: choose the length of the first piece, recurse on the rest. O(k^n) time, O(n) stack. */
    static int recursive(int[] arr, int k) {
        return solve(0, arr, k);                                // best sum for the whole array
    }

    static int solve(int i, int[] arr, int k) {
        if (i == arr.length) return 0;                          // nothing left: adds 0
        int maxVal = 0, best = 0;                               // max of the first piece, best total so far
        for (int len = 1; len <= k && i + len <= arr.length; len++) {   // first piece = arr[i .. i + len - 1]
            maxVal = Math.max(maxVal, arr[i + len - 1]);        // running maximum of arr[i .. i + len - 1]
            best = Math.max(best, maxVal * len + solve(i + len, arr, k));   // piece sum + best for the rest
        }
        return best;
    }

    /** Approach 2: cache the answer for every suffix start i. O(n * k) time, O(n) space. */
    static int memoization(int[] arr, int k) {
        int[] dp = new int[arr.length];                         // dp[i] = best sum for the suffix arr[i..]
        Arrays.fill(dp, -1);                                    // -1 marks a suffix not solved yet
        return memo(0, arr, k, dp);                             // best sum for the whole array
    }

    static int memo(int i, int[] arr, int k, int[] dp) {
        if (i == arr.length) return 0;                          // nothing left: adds 0
        if (dp[i] != -1) return dp[i];                          // already solved: reuse it
        int maxVal = 0, best = 0;
        for (int len = 1; len <= k && i + len <= arr.length; len++) {   // first piece = arr[i .. i + len - 1]
            maxVal = Math.max(maxVal, arr[i + len - 1]);        // running maximum of the piece
            best = Math.max(best, maxVal * len + memo(i + len, arr, k, dp));   // piece sum + best for the rest
        }
        return dp[i] = best;                                    // store the answer before returning it
    }

    /** Approach 3: fill dp[i] from the right end. O(n * k) time, O(n) space. */
    static int tabulation(int[] arr, int k) {
        int n = arr.length;
        int[] dp = new int[n + 1];                              // dp[n] = 0
        for (int i = n - 1; i >= 0; i--) {                      // suffixes from shortest to longest
            int maxVal = 0, best = 0;
            for (int len = 1; len <= k && i + len <= n; len++) {   // first piece = arr[i .. i + len - 1]
                maxVal = Math.max(maxVal, arr[i + len - 1]);    // running maximum of the piece
                best = Math.max(best, maxVal * len + dp[i + len]);   // dp[i + len] is already filled
            }
            dp[i] = best;                                       // best sum for the suffix arr[i..]
        }
        return dp[0];                                           // the whole array
    }

    /** Approach 4: dp[i] only reads dp[i + 1 .. i + k], so a ring of k + 1 cells is enough. O(n * k) time, O(k) space. */
    static int spaceOptimized(int[] arr, int k) {
        int n = arr.length;
        int[] dp = new int[k + 1];                              // dp[i % (k + 1)] holds f(i); f(n) starts as 0
        for (int i = n - 1; i >= 0; i--) {                      // same order as the table version
            int maxVal = 0, best = 0;
            for (int len = 1; len <= k && i + len <= n; len++) {   // first piece = arr[i .. i + len - 1]
                maxVal = Math.max(maxVal, arr[i + len - 1]);    // running maximum of the piece
                best = Math.max(best, maxVal * len + dp[(i + len) % (k + 1)]);   // f(i + len) sits at its ring slot
            }
            dp[i % (k + 1)] = best;                             // overwrites f(i + k + 1), which is no longer needed
        }
        return dp[0];                                           // f(0) is stored at slot 0 % (k + 1) = 0
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int k, int expected, boolean small) {
        String in = Arrays.toString(arr) + " k=" + k;
        if (small) check(recursive(arr, k) == expected, "recursive failed for " + in);
        check(memoization(arr, k) == expected, "memoization failed for " + in);
        check(tabulation(arr, k) == expected, "tabulation failed for " + in);
        check(spaceOptimized(arr, k) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 15, 7, 9, 2, 5, 10}, 3, 84, true);              // [15,15,15,9,10,10,10]
        verify(new int[]{1, 4, 1, 5, 7, 3, 6, 1, 9, 9, 3}, 4, 83, true);
        verify(new int[]{5, 1, 1}, 3, 15, true);                           // one piece of length 3
        verify(new int[]{3, 8, 2}, 1, 13, true);                           // k = 1: the plain sum
        verify(new int[]{1}, 1, 1, true);                                  // edge: single element
        verify(new int[]{}, 2, 0, true);                                   // edge: empty array
        verify(new int[]{2, 9, 2, 2, 9}, 2, 38, true);                     // [2,9][2][2,9] -> 18 + 2 + 18
        int[] big = new int[500];
        for (int i = 0; i < big.length; i++) big[i] = (i * 37) % 101;
        verify(big, 100, tabulation(big, 100), false);                     // large: memo, tab and ring agree

        Random rnd = new Random(810);
        for (int t = 0; t < 300; t++) {
            int[] arr = new int[rnd.nextInt(10)];
            for (int i = 0; i < arr.length; i++) arr[i] = rnd.nextInt(20);
            int k = 1 + rnd.nextInt(4);
            verify(arr, k, recursive(arr, k), true);
        }
        System.out.println("OK P810_PartitionArrayForMaximumSum");
    }
}
