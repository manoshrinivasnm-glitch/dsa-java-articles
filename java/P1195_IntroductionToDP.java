import java.util.*;

/** TUF 1195 - Introduction to DP. Fibonacci computed four ways: plain recursion, memoization, tabulation, space optimization. */
public class P1195_IntroductionToDP {

    static long calls = 0;                                      // how many times plainRecursion was entered
    static long memoCalls = 0;                                  // how many times memo was entered

    /** Plain recursion straight from the recurrence. O(2^n) time, O(n) stack. */
    static long plainRecursion(int n) {
        calls++;
        if (n <= 1) return n;                                   // base cases: F(0) = 0, F(1) = 1
        return plainRecursion(n - 1) + plainRecursion(n - 2);
    }

    /** Memoization (top-down): the same recursion, but every F(i) is stored the first time it is computed. O(n) time, O(n) space. */
    static long memoization(int n) {
        long[] dp = new long[n + 1];
        Arrays.fill(dp, -1);                                    // -1 means "not computed yet"
        return memo(n, dp);
    }

    static long memo(int n, long[] dp) {
        memoCalls++;
        if (n <= 1) return n;
        if (dp[n] != -1) return dp[n];                          // solved before: answer in O(1)
        return dp[n] = memo(n - 1, dp) + memo(n - 2, dp);      // solve once, store, return
    }

    /** Tabulation (bottom-up): fill dp[0..n] from the base cases upward. O(n) time, O(n) space, no recursion. */
    static long tabulation(int n) {
        if (n <= 1) return n;
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
        return dp[n];
    }

    /** Space optimization: dp[i] only reads dp[i - 1] and dp[i - 2], so keep just those two. O(n) time, O(1) space. */
    static long spaceOptimized(int n) {
        if (n <= 1) return n;
        long prev2 = 0, prev = 1;                               // F(i - 2) and F(i - 1), starting at i = 2
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
        check(plainRecursion(n) == expected, "plainRecursion failed for n = " + n);
        verifyFast(n, expected);
    }

    static void verifyFast(int n, long expected) {
        check(memoization(n) == expected, "memoization failed for n = " + n);
        check(tabulation(n) == expected, "tabulation failed for n = " + n);
        check(spaceOptimized(n) == expected, "spaceOptimized failed for n = " + n);
    }

    public static void main(String[] args) {
        verifyAll(0, 0);                                        // edge: first base case
        verifyAll(1, 1);                                        // edge: second base case
        verifyAll(2, 1);
        verifyAll(5, 5);
        verifyAll(10, 55);
        verifyAll(20, 6_765);
        verifyAll(30, 832_040);
        verifyFast(50, 12_586_269_025L);
        verifyFast(90, 2_880_067_194_370_816_120L);            // plain recursion would need ~10^19 calls here

        // The call counts are the whole point of DP: 2F(n+1) - 1 calls without a cache, 2n - 1 calls with one.
        calls = 0;
        plainRecursion(20);
        check(calls == 2 * 10_946 - 1, "plain recursion should make 21891 calls for n = 20, made " + calls);
        memoCalls = 0;
        memoization(20);
        check(memoCalls == 2 * 20 - 1, "memoization should make 39 calls for n = 20, made " + memoCalls);
        System.out.println("OK P1195_IntroductionToDP");
    }
}
