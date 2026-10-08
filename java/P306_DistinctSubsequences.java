import java.util.*;

/** TUF 306 - Distinct Subsequences (LeetCode 115). Count the ways to pick a subsequence of s equal to t, modulo 1e9 + 7. */
public class P306_DistinctSubsequences {

    static final int MOD = 1_000_000_007;

    /** Approach 1: recursion on prefix lengths. O(2^n) time, O(n) stack. */
    static int recursive(String s, String t) {
        return solve(s.length(), t.length(), s, t);
    }

    /** Ways to form t[0..j) from characters of s[0..i). */
    static int solve(int i, int j, String s, String t) {
        if (j == 0) return 1;                                    // all of t is matched: one way (pick nothing more)
        if (i == 0) return 0;                                    // t still has characters but s is used up
        int skip = solve(i - 1, j, s, t);                        // s[i - 1] is not used for t[j - 1]
        if (s.charAt(i - 1) != t.charAt(j - 1)) return skip;
        int use = solve(i - 1, j - 1, s, t);                     // s[i - 1] is matched to t[j - 1]
        return (use + skip) % MOD;
    }

    /** Approach 2: memoization on (i, j). O(n * m) time, O(n * m) space plus the stack. */
    static int memoization(String s, String t) {
        int n = s.length(), m = t.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(n, m, s, t, dp);
    }

    static int memo(int i, int j, String s, String t, int[][] dp) {
        if (j == 0) return 1;
        if (i == 0) return 0;
        if (dp[i][j] != -1) return dp[i][j];
        int skip = memo(i - 1, j, s, t, dp);
        if (s.charAt(i - 1) != t.charAt(j - 1)) return dp[i][j] = skip;
        int use = memo(i - 1, j - 1, s, t, dp);
        return dp[i][j] = (use + skip) % MOD;
    }

    /** Approach 3: tabulation; column 0 is all ones, the rest of row 0 is zero. O(n * m) time and space. */
    static int tabulation(String s, String t) {
        int n = s.length(), m = t.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = 1;               // the empty t is formed exactly once
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                dp[i][j] = dp[i - 1][j];
                if (s.charAt(i - 1) == t.charAt(j - 1)) dp[i][j] = (dp[i][j] + dp[i - 1][j - 1]) % MOD;
            }
        }
        return dp[n][m];
    }

    /** Approach 4: one array, j from right to left so ways[j - 1] still holds the previous row. O(n * m) time, O(m) space. */
    static int spaceOptimized(String s, String t) {
        int n = s.length(), m = t.length();
        int[] ways = new int[m + 1];
        ways[0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = m; j >= 1; j--) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) ways[j] = (ways[j] + ways[j - 1]) % MOD;
            }
        }
        return ways[m];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String t, int expected) {
        String in = "\"" + s + "\", \"" + t + "\"";
        check(recursive(s, t) == expected, "recursive failed for " + in);
        verifyPolynomial(s, t, expected);
    }

    /** The three polynomial approaches only, for inputs too large for plain recursion. */
    static void verifyPolynomial(String s, String t, int expected) {
        String in = "\"" + s + "\", \"" + t + "\"";
        check(memoization(s, t) == expected, "memoization failed for " + in);
        check(tabulation(s, t) == expected, "tabulation failed for " + in);
        check(spaceOptimized(s, t) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify("rabbbit", "rabbit", 3);             // choose which of the three b's to skip
        verify("babgbag", "bag", 5);
        verify("aaaaa", "aa", 10);                  // C(5, 2)
        verify("aaa", "aaaa", 0);                   // t longer than s
        verify("abc", "", 1);                       // edge: empty t is formed once
        verify("", "a", 0);                         // edge: empty s
        verify("", "", 1);                          // edge: both empty
        verify("abcde", "ace", 1);

        // C(200, 100) is far beyond long; the answer must be that binomial modulo 1e9 + 7.
        String s = "a".repeat(200), t = "a".repeat(100);
        verifyPolynomial(s, t, binomialMod(200, 100));

        // Cross-check every approach on seeded random strings over a small alphabet.
        Random rnd = new Random(306);
        for (int k = 0; k < 300; k++) {
            String a = randomString(rnd, rnd.nextInt(13)), b = randomString(rnd, rnd.nextInt(5));
            verify(a, b, tabulation(a, b));
        }
        System.out.println("OK P306_DistinctSubsequences");
    }

    /** C(n, k) mod MOD from Pascal's triangle, an independent check for the all-'a' case. */
    static int binomialMod(int n, int k) {
        int[] row = new int[k + 1];
        row[0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = Math.min(i, k); j >= 1; j--) row[j] = (row[j] + row[j - 1]) % MOD;
        }
        return row[k];
    }

    static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(2)));
        return sb.toString();
    }
}
