import java.util.*;

/** TUF 636 - Longest Increasing Subsequence. Return the length of the longest strictly increasing subsequence. */
public class P636_LongestIncreasingSubsequence {

    /** Approach 1: recursion on (index, index of the last element taken). O(2^n) time, O(n) stack. */
    static int recursive(int[] a) {
        return solve(0, -1, a);
    }

    /** Longest increasing subsequence of a[i..] whose elements all exceed a[prev] (prev = -1: nothing taken yet). */
    static int solve(int i, int prev, int[] a) {
        if (i == a.length) return 0;
        int best = solve(i + 1, prev, a);                               // skip a[i]
        if (prev == -1 || a[i] > a[prev]) {
            best = Math.max(best, 1 + solve(i + 1, i, a));              // take a[i]
        }
        return best;
    }

    /** Approach 2: memoization; prev is stored shifted by one so that -1 fits in column 0. O(n^2) time, O(n^2) space. */
    static int memoization(int[] a) {
        int n = a.length;
        int[][] dp = new int[n][n + 1];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(0, -1, a, dp);
    }

    static int memo(int i, int prev, int[] a, int[][] dp) {
        if (i == a.length) return 0;
        if (dp[i][prev + 1] != -1) return dp[i][prev + 1];
        int best = memo(i + 1, prev, a, dp);
        if (prev == -1 || a[i] > a[prev]) best = Math.max(best, 1 + memo(i + 1, i, a, dp));
        return dp[i][prev + 1] = best;
    }

    /** Approach 3: tabulation, i from n - 1 down, prev from i - 1 down to -1. O(n^2) time, O(n^2) space. */
    static int tabulation(int[] a) {
        int n = a.length;
        int[][] dp = new int[n + 1][n + 1];                             // dp[n][*] = 0
        for (int i = n - 1; i >= 0; i--) {
            for (int prev = i - 1; prev >= -1; prev--) {
                int best = dp[i + 1][prev + 1];
                if (prev == -1 || a[i] > a[prev]) best = Math.max(best, 1 + dp[i + 1][i + 1]);
                dp[i][prev + 1] = best;
            }
        }
        return dp[0][0];
    }

    /** Approach 4: row i only reads row i + 1, so keep two rows. O(n^2) time, O(n) space. */
    static int spaceOptimized(int[] a) {
        int n = a.length;
        int[] next = new int[n + 1], cur = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            for (int prev = i - 1; prev >= -1; prev--) {
                int best = next[prev + 1];
                if (prev == -1 || a[i] > a[prev]) best = Math.max(best, 1 + next[i + 1]);
                cur[prev + 1] = best;
            }
            int[] t = next; next = cur; cur = t;
        }
        return next[0];
    }

    /** Approach 5: dp[i] = length of the longest increasing subsequence that ends at index i. O(n^2) time, O(n) space. */
    static int endingAt(int[] a) {
        int n = a.length, best = 0;
        int[] dp = new int[n];
        for (int i = 0; i < n; i++) {
            dp[i] = 1;                                                  // a[i] alone
            for (int j = 0; j < i; j++) {
                if (a[j] < a[i]) dp[i] = Math.max(dp[i], dp[j] + 1);    // extend a subsequence ending at j
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    /** Approach 6: tails[k] = smallest possible last value of an increasing subsequence of length k + 1. O(n log n) time, O(n) space. */
    static int binarySearch(int[] a) {
        int[] tails = new int[a.length];
        int len = 0;
        for (int x : a) {
            int lo = 0, hi = len;                                       // first k with tails[k] >= x
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < x) lo = mid + 1; else hi = mid;
            }
            tails[lo] = x;
            if (lo == len) len++;
        }
        return len;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int expected) {
        String in = Arrays.toString(a);
        check(recursive(a) == expected, "recursive failed for " + in);
        check(memoization(a) == expected, "memoization failed for " + in);
        check(tabulation(a) == expected, "tabulation failed for " + in);
        check(spaceOptimized(a) == expected, "spaceOptimized failed for " + in);
        check(endingAt(a) == expected, "endingAt failed for " + in);
        check(binarySearch(a) == expected, "binarySearch failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{10, 9, 2, 5, 3, 7, 101, 18}, 4);       // e.g. 2, 3, 7, 18
        verify(new int[]{0, 1, 0, 3, 2, 3}, 4);                 // 0, 1, 2, 3
        verify(new int[]{7, 7, 7, 7}, 1);                       // strictly increasing: equal values do not chain
        verify(new int[]{1, 2, 3, 4, 5}, 5);                    // already sorted
        verify(new int[]{5, 4, 3, 2, 1}, 1);                    // reverse sorted
        verify(new int[]{-3, -1, -2, 0, Integer.MAX_VALUE, Integer.MIN_VALUE}, 4); // negatives and extremes
        verify(new int[]{42}, 1);                               // edge: one element
        verify(new int[]{}, 0);                                 // edge: empty

        Random rnd = new Random(636);
        for (int t = 0; t < 300; t++) {
            int[] a = new int[rnd.nextInt(13)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(20) - 5;
            verify(a, recursive(a));
        }
        int[] big = new int[2500];
        for (int i = 0; i < big.length; i++) big[i] = (i * 7919) % 2503;
        check(tabulation(big) == binarySearch(big) && spaceOptimized(big) == endingAt(big)
                && endingAt(big) == binarySearch(big), "large input mismatch");
        System.out.println("OK P636_LongestIncreasingSubsequence");
    }
}
