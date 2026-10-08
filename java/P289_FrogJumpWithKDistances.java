import java.util.*;

/** TUF 289 - Frog Jump with K distances. From stair i the frog may jump to i+1 .. i+k; a jump costs |h[i] - h[j]|. Minimum energy to reach stair n-1. */
public class P289_FrogJumpWithKDistances {

    /** Approach 1: recursion over the length of the last jump. O(k^n) time in the worst case, O(n) stack. */
    static int recursive(int[] h, int k) {
        return solve(h.length - 1, h, k);
    }

    /** Minimum energy to reach stair i from stair 0. */
    static int solve(int i, int[] h, int k) {
        if (i == 0) return 0;
        int best = Integer.MAX_VALUE;
        for (int j = 1; j <= k && i - j >= 0; j++) {            // last jump came from i - j
            best = Math.min(best, solve(i - j, h, k) + Math.abs(h[i] - h[i - j]));
        }
        return best;
    }

    /** Approach 2: memoization. O(n * k) time, O(n) space for the table and the stack. */
    static int memoization(int[] h, int k) {
        int[] dp = new int[h.length];
        Arrays.fill(dp, -1);
        return memo(h.length - 1, h, k, dp);
    }

    static int memo(int i, int[] h, int k, int[] dp) {
        if (i == 0) return 0;
        if (dp[i] != -1) return dp[i];
        int best = Integer.MAX_VALUE;
        for (int j = 1; j <= k && i - j >= 0; j++) {
            best = Math.min(best, memo(i - j, h, k, dp) + Math.abs(h[i] - h[i - j]));
        }
        return dp[i] = best;
    }

    /** Approach 3: tabulation from stair 0 upward. O(n * k) time, O(n) space. */
    static int tabulation(int[] h, int k) {
        int n = h.length;
        int[] dp = new int[n];
        dp[0] = 0;
        for (int i = 1; i < n; i++) {
            int best = Integer.MAX_VALUE;
            for (int j = 1; j <= k && i - j >= 0; j++) {
                best = Math.min(best, dp[i - j] + Math.abs(h[i] - h[i - j]));
            }
            dp[i] = best;
        }
        return dp[n - 1];
    }

    /** Approach 4: only the last k cells are ever read, so keep them in a ring buffer. O(n * k) time, O(min(n, k)) space. */
    static int ringBuffer(int[] h, int k) {
        int n = h.length;
        int size = Math.min(n, k);
        int[] ring = new int[size];                             // ring[i % size] holds dp[i]
        ring[0] = 0;
        for (int i = 1; i < n; i++) {
            int best = Integer.MAX_VALUE;
            for (int j = 1; j <= k && i - j >= 0; j++) {
                best = Math.min(best, ring[(i - j) % size] + Math.abs(h[i] - h[i - j]));
            }
            ring[i % size] = best;                              // overwrites dp[i - size], which is no longer needed
        }
        return ring[(n - 1) % size];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] h, int k, int expected) {
        String in = Arrays.toString(h) + ", k = " + k;
        check(recursive(h, k) == expected, "recursive failed for " + in);
        check(memoization(h, k) == expected, "memoization failed for " + in);
        check(tabulation(h, k) == expected, "tabulation failed for " + in);
        check(ringBuffer(h, k) == expected, "ringBuffer failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{10, 30, 40, 50, 20}, 3, 30);           // 0 -> 1 -> 4: 20 + 10
        verify(new int[]{10, 20, 10}, 1, 20);                   // k = 1 forces every stair
        verify(new int[]{40, 10, 20, 70, 80, 10, 20, 70, 80, 60}, 4, 40);
        verify(new int[]{30, 10, 60, 10, 60, 50}, 2, 40);       // k = 2 is the plain Frog Jump
        verify(new int[]{10, 100, 10, 50}, 5, 40);              // k larger than n: jump straight to the end
        verify(new int[]{7}, 3, 0);                             // edge: a single stair
        verify(new int[]{1, 1000, 1000, 1000, 1}, 4, 0);        // one long jump avoids every peak

        // Cross-check every approach on seeded random inputs (small enough for plain recursion).
        Random rnd = new Random(289);
        for (int t = 0; t < 200; t++) {
            int[] h = new int[1 + rnd.nextInt(12)];
            for (int i = 0; i < h.length; i++) h[i] = rnd.nextInt(100);
            int k = 1 + rnd.nextInt(5);
            verify(h, k, tabulation(h, k));
        }
        System.out.println("OK P289_FrogJumpWithKDistances");
    }
}
