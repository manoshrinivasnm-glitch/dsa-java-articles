import java.util.*;

/** TUF 310 - Longest Palindromic Subsequence (LeetCode 516). Length of the longest subsequence of s that reads the same both ways. */
public class P310_LongestPalindromicSubsequence {

    /** Approach 1: recursion on the range s[i..j]. O(2^n) time, O(n) stack. */
    static int recursive(String s) {
        return solve(0, s.length() - 1, s);
    }

    /** Longest palindromic subsequence inside s[i..j] (inclusive). */
    static int solve(int i, int j, String s) {
        if (i > j) return 0;                                     // empty range
        if (i == j) return 1;                                    // a single character is a palindrome
        if (s.charAt(i) == s.charAt(j)) return 2 + solve(i + 1, j - 1, s);   // use both ends as the outer pair
        return Math.max(solve(i + 1, j, s), solve(i, j - 1, s)); // at least one end is unused: drop it
    }

    /** Approach 2: memoization on (i, j). O(n^2) time, O(n^2) space plus the stack. */
    static int memoization(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(0, n - 1, s, dp);
    }

    static int memo(int i, int j, String s, int[][] dp) {
        if (i > j) return 0;
        if (i == j) return 1;
        if (dp[i][j] != -1) return dp[i][j];
        if (s.charAt(i) == s.charAt(j)) return dp[i][j] = 2 + memo(i + 1, j - 1, s, dp);
        return dp[i][j] = Math.max(memo(i + 1, j, s, dp), memo(i, j - 1, s, dp));
    }

    /** Approach 3: tabulation, i from right to left so that row i + 1 is ready. O(n^2) time and space. */
    static int tabulation(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int[][] dp = new int[n][n];                              // entries with i > j stay 0 (empty range)
        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) dp[i][j] = 2 + dp[i + 1][j - 1];
                else dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
            }
        }
        return dp[0][n - 1];
    }

    /** Approach 4: row i only reads row i + 1, so keep two rows. O(n^2) time, O(n) space. */
    static int spaceOptimized(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int[] next = new int[n], cur = new int[n];               // next = row i + 1, cur = row i
        for (int i = n - 1; i >= 0; i--) {
            cur[i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) cur[j] = 2 + (j - 1 > i ? next[j - 1] : 0);  // j == i + 1: nothing inside
                else cur[j] = Math.max(next[j], cur[j - 1]);
            }
            int[] tmp = next; next = cur; cur = tmp;
        }
        return next[n - 1];
    }

    /** Approach 5: a palindrome is a common subsequence of s and reverse(s), so reuse LCS. O(n^2) time, O(n) space. */
    static int viaLcs(String s) {
        String r = new StringBuilder(s).reverse().toString();
        int n = s.length();
        int[] prev = new int[n + 1], cur = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == r.charAt(j - 1)) cur[j] = 1 + prev[j - 1];
                else cur[j] = Math.max(prev[j], cur[j - 1]);
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[n];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        String in = "\"" + s + "\"";
        check(recursive(s) == expected, "recursive failed for " + in);
        check(memoization(s) == expected, "memoization failed for " + in);
        check(tabulation(s) == expected, "tabulation failed for " + in);
        check(spaceOptimized(s) == expected, "spaceOptimized failed for " + in);
        check(viaLcs(s) == expected, "viaLcs failed for " + in);
    }

    public static void main(String[] args) {
        verify("bbbab", 4);                         // "bbbb"
        verify("cbbd", 2);                          // "bb"
        verify("bbabcbcab", 7);                     // "babcbab"
        verify("agbdba", 5);                        // "abdba"
        verify("abcd", 1);                          // all distinct: any single character
        verify("a", 1);                             // edge: one character
        verify("", 0);                              // edge: empty string
        verify("racecar", 7);                       // already a palindrome

        // Cross-check every approach on seeded random strings over a small alphabet.
        Random rnd = new Random(310);
        for (int t = 0; t < 300; t++) {
            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(14);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
            String s = sb.toString();
            verify(s, tabulation(s));
        }
        System.out.println("OK P310_LongestPalindromicSubsequence");
    }
}
