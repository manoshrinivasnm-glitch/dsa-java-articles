import java.util.*;

/** TUF 288 - Frog Jump. Minimum energy to go from stair 0 to stair n-1 with jumps of 1 or 2; a jump i -> j costs |h[i] - h[j]|. */
public class P288_FrogJump {

    /** Approach 1: recursion over the last jump into stair i. O(2^n) time, O(n) stack. */
    static int recursive(int[] h) {
        return solve(h.length - 1, h);
    }

    /** Minimum energy to reach stair i from stair 0. */
    static int solve(int i, int[] h) {
        if (i == 0) return 0;                                   // already standing on stair 0
        int one = solve(i - 1, h) + Math.abs(h[i] - h[i - 1]);  // last jump came from i - 1
        int two = Integer.MAX_VALUE;
        if (i > 1) two = solve(i - 2, h) + Math.abs(h[i] - h[i - 2]); // last jump came from i - 2
        return Math.min(one, two);
    }

    /** Approach 2: memoization, each stair solved once. O(n) time, O(n) space for the table and the stack. */
    static int memoization(int[] h) {
        int[] dp = new int[h.length];
        Arrays.fill(dp, -1);                                    // -1 = not computed (real costs are >= 0)
        return memo(h.length - 1, h, dp);
    }

    static int memo(int i, int[] h, int[] dp) {
        if (i == 0) return 0;
        if (dp[i] != -1) return dp[i];
        int one = memo(i - 1, h, dp) + Math.abs(h[i] - h[i - 1]);
        int two = Integer.MAX_VALUE;
        if (i > 1) two = memo(i - 2, h, dp) + Math.abs(h[i] - h[i - 2]);
        return dp[i] = Math.min(one, two);
    }

    /** Approach 3: tabulation from stair 0 upward. O(n) time, O(n) space, no recursion. */
    static int tabulation(int[] h) {
        int n = h.length;
        int[] dp = new int[n];
        dp[0] = 0;
        for (int i = 1; i < n; i++) {
            int one = dp[i - 1] + Math.abs(h[i] - h[i - 1]);
            int two = Integer.MAX_VALUE;
            if (i > 1) two = dp[i - 2] + Math.abs(h[i] - h[i - 2]);
            dp[i] = Math.min(one, two);
        }
        return dp[n - 1];
    }

    /** Approach 4: dp[i] reads only dp[i - 1] and dp[i - 2], so keep two variables. O(n) time, O(1) space. */
    static int spaceOptimized(int[] h) {
        int prev2 = 0, prev = 0;                                // dp[i - 2] and dp[i - 1]
        for (int i = 1; i < h.length; i++) {
            int one = prev + Math.abs(h[i] - h[i - 1]);
            int two = Integer.MAX_VALUE;
            if (i > 1) two = prev2 + Math.abs(h[i] - h[i - 2]);
            int cur = Math.min(one, two);
            prev2 = prev;
            prev = cur;
        }
        return prev;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] h, int expected) {
        String in = Arrays.toString(h);
        check(recursive(h) == expected, "recursive failed for " + in);
        check(memoization(h) == expected, "memoization failed for " + in);
        check(tabulation(h) == expected, "tabulation failed for " + in);
        check(spaceOptimized(h) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{10, 20, 30, 10}, 20);                  // 0 -> 1 -> 3: 10 + 10
        verify(new int[]{10, 50, 10}, 0);                       // 0 -> 2 directly
        verify(new int[]{30, 10, 60, 10, 60, 50}, 40);          // greedy would pay 60 here
        verify(new int[]{7, 4, 4, 2, 6, 6, 3, 4}, 7);
        verify(new int[]{5, 9}, 4);                             // only one jump possible
        verify(new int[]{7}, 0);                                // edge: already at the last stair
        verify(new int[]{4, 4, 4, 4, 4}, 0);                    // flat staircase

        // Cross-check every approach on seeded random staircases (small enough for plain recursion).
        Random rnd = new Random(288);
        for (int t = 0; t < 200; t++) {
            int[] h = new int[1 + rnd.nextInt(18)];
            for (int i = 0; i < h.length; i++) h[i] = rnd.nextInt(100);
            verify(h, tabulation(h));
        }
        System.out.println("OK P288_FrogJump");
    }
}
