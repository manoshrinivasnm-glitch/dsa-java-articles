import java.util.*;

/** TUF 287 - Climbing Stairs (LeetCode 70). Count the distinct ways to climb n steps taking 1 or 2 steps at a time. */
public class P287_ClimbingStairs {

    /** Approach 1: recursion on the last move (a 1-step or a 2-step). O(2^n) time, O(n) stack. */
    static long recursive(int n) {
        if (n <= 1) return 1;                                   // step 0 or step 1: exactly one way
        return recursive(n - 1) + recursive(n - 2);
    }

    /** Approach 2: memoization, each ways(i) computed once. O(n) time, O(n) space for the table and the stack. */
    static long memoization(int n) {
        long[] dp = new long[n + 1];
        Arrays.fill(dp, -1);                                    // -1 = not computed yet
        return ways(n, dp);
    }

    static long ways(int i, long[] dp) {
        if (i <= 1) return 1;
        if (dp[i] != -1) return dp[i];
        return dp[i] = ways(i - 1, dp) + ways(i - 2, dp);
    }

    /** Approach 3: tabulation from the bottom step upward. O(n) time, O(n) space. */
    static long tabulation(int n) {
        long[] dp = new long[Math.max(n + 1, 2)];
        dp[0] = 1;                                              // standing on the ground: one way (do nothing)
        dp[1] = 1;                                              // a single 1-step
        for (int i = 2; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
        return dp[n];
    }

    /** Approach 4: keep only the last two table cells. O(n) time, O(1) space. */
    static long spaceOptimized(int n) {
        long prev2 = 1, prev = 1;                               // ways(i - 2) and ways(i - 1), starting at i = 2
        for (int i = 2; i <= n; i++) {
            long cur = prev + prev2;
            prev2 = prev;
            prev = cur;
        }
        return prev;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyAll(int n, long expected) {
        check(recursive(n) == expected, "recursive failed for n = " + n);
        verifyFast(n, expected);
    }

    static void verifyFast(int n, long expected) {
        check(memoization(n) == expected, "memoization failed for n = " + n);
        check(tabulation(n) == expected, "tabulation failed for n = " + n);
        check(spaceOptimized(n) == expected, "spaceOptimized failed for n = " + n);
    }

    public static void main(String[] args) {
        verifyAll(0, 1);                                        // edge: zero steps, the empty plan
        verifyAll(1, 1);                                        // edge: smallest LeetCode input
        verifyAll(2, 2);                                        // 1+1, 2
        verifyAll(3, 3);                                        // 1+1+1, 1+2, 2+1
        verifyAll(4, 5);
        verifyAll(5, 8);
        verifyAll(10, 89);
        verifyAll(30, 1_346_269);                               // about 2.7 million recursive calls, still fast
        verifyFast(45, 1_836_311_903L);                         // largest LeetCode input, still fits in int
        verifyFast(90, 4_660_046_610_375_530_309L);             // needs long
        System.out.println("OK P287_ClimbingStairs");
    }
}
