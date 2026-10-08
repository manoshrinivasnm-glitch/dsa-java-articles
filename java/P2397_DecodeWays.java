import java.util.*;

/** TUF 2397 - Decode Ways. 'A' = 1 ... 'Z' = 26. Count the ways to split a digit string into valid letter codes. */
public class P2397_DecodeWays {

    /** True when s[i] s[i + 1] is a valid two-digit code, i.e. 10..26 (the caller has already checked s[i] != '0'). */
    static boolean twoDigitCode(String s, int i) {
        return i + 1 < s.length() && (s.charAt(i) - '0') * 10 + (s.charAt(i + 1) - '0') <= 26;
    }

    /** Approach 1: recursion. ways(i) = ways(i + 1) for a one-digit code, plus ways(i + 2) for a two-digit code. O(2^n) time, O(n) stack. */
    static int recursion(String s) {
        return solve(0, s);
    }

    static int solve(int i, String s) {
        if (i == s.length()) return 1;                         // consumed everything: one complete decoding
        if (s.charAt(i) == '0') return 0;                      // no code starts with 0
        int ways = solve(i + 1, s);
        if (twoDigitCode(s, i)) ways += solve(i + 2, s);
        return ways;
    }

    /** Approach 2: memoization. Only n + 1 distinct suffixes exist. O(n) time, O(n) space plus O(n) stack. */
    static int memoization(String s) {
        int[] memo = new int[s.length() + 1];
        Arrays.fill(memo, -1);
        return solveMemo(0, s, memo);
    }

    static int solveMemo(int i, String s, int[] memo) {
        if (i == s.length()) return 1;
        if (s.charAt(i) == '0') return 0;
        if (memo[i] != -1) return memo[i];
        int ways = solveMemo(i + 1, s, memo);
        if (twoDigitCode(s, i)) ways += solveMemo(i + 2, s, memo);
        return memo[i] = ways;
    }

    /** Approach 3: tabulation from the right. dp[i] = number of ways to decode s[i..]. O(n) time, O(n) space. */
    static int tabulation(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[n] = 1;                                             // the empty suffix has exactly one decoding
        for (int i = n - 1; i >= 0; i--) {
            if (s.charAt(i) == '0') continue;                  // dp[i] stays 0
            dp[i] = dp[i + 1];
            if (twoDigitCode(s, i)) dp[i] += dp[i + 2];
        }
        return dp[0];
    }

    /** Approach 4: dp[i] only needs dp[i + 1] and dp[i + 2], so keep two variables. O(n) time, O(1) space. */
    static int spaceOptimised(String s) {
        int next1 = 1, next2 = 0;                              // next1 = dp[i + 1], next2 = dp[i + 2]
        for (int i = s.length() - 1; i >= 0; i--) {
            int cur = 0;
            if (s.charAt(i) != '0') {
                cur = next1;
                if (twoDigitCode(s, i)) cur += next2;
            }
            next2 = next1;
            next1 = cur;
        }
        return next1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        check(recursion(s) == expected, "recursion \"" + s + "\" got " + recursion(s));
        check(memoization(s) == expected, "memoization \"" + s + "\" got " + memoization(s));
        check(tabulation(s) == expected, "tabulation \"" + s + "\" got " + tabulation(s));
        check(spaceOptimised(s) == expected, "spaceOptimised \"" + s + "\" got " + spaceOptimised(s));
    }

    public static void main(String[] args) {
        verify("12", 2);                                   // "AB" or "L"
        verify("226", 3);                                  // 2 2 6, 22 6, 2 26
        verify("06", 0);                                   // leading zero
        verify("0", 0);
        verify("10", 1);                                   // only "J"
        verify("100", 0);                                  // the second 0 cannot be placed
        verify("2101", 1);                                 // 2 10 1
        verify("11106", 2);                                // 1 1 10 6, 11 10 6
        verify("27", 1);                                   // 27 > 26
        verify("1", 1);
        verify("1111111111", 89);                          // Fibonacci growth
        verify("301", 0);                                  // "30" is not a code

        String ones = "1".repeat(45);                      // answer F(46) = 1836311903 still fits in an int
        check(memoization(ones) == 1836311903, "memoization on 45 ones");
        check(tabulation(ones) == 1836311903, "tabulation on 45 ones");
        check(spaceOptimised(ones) == 1836311903, "spaceOptimised on 45 ones");
        System.out.println("OK P2397_DecodeWays");
    }
}
