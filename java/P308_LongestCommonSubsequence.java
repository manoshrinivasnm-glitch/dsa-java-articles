import java.util.*;

/** TUF 308 - Longest Common Subsequence (LeetCode 1143). Length of the longest sequence that is a subsequence of both strings. */
public class P308_LongestCommonSubsequence {

    /** Approach 1: recursion on prefix lengths. O(2^(n+m)) time, O(n + m) stack. */
    static int recursive(String s1, String s2) {
        return solve(s1.length(), s2.length(), s1, s2);  // start from the two full strings
    }

    /** LCS length of the prefixes s1[0..i) and s2[0..j). */
    static int solve(int i, int j, String s1, String s2) {
        if (i == 0 || j == 0) return 0;                          // an empty prefix shares nothing
        // last characters match: they end the LCS, so count them and shrink both
        if (s1.charAt(i - 1) == s2.charAt(j - 1)) return 1 + solve(i - 1, j - 1, s1, s2);
        // mismatch: drop the last char of one string and keep the better result
        return Math.max(solve(i - 1, j, s1, s2), solve(i, j - 1, s1, s2));
    }

    /** Approach 2: memoization on (i, j). O(n * m) time, O(n * m) space plus the stack. */
    static int memoization(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];         // dp[i][j] caches solve(i, j)
        for (int[] row : dp) Arrays.fill(row, -1);  // -1 marks a state not solved yet
        return memo(n, m, s1, s2, dp);
    }

    static int memo(int i, int j, String s1, String s2, int[][] dp) {
        if (i == 0 || j == 0) return 0;       // an empty prefix shares nothing
        if (dp[i][j] != -1) return dp[i][j];  // already solved: reuse the stored answer
        // last characters match: count them and shrink both prefixes
        if (s1.charAt(i - 1) == s2.charAt(j - 1)) return dp[i][j] = 1 + memo(i - 1, j - 1, s1, s2, dp);
        // mismatch: drop one last char and keep the better result
        return dp[i][j] = Math.max(memo(i - 1, j, s1, s2, dp), memo(i, j - 1, s1, s2, dp));
    }

    /** Approach 3: tabulation, row 0 and column 0 are the empty-prefix base cases. O(n * m) time and space. */
    static int tabulation(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];  // dp[0][*] = dp[*][0] = 0 by default
        for (int i = 1; i <= n; i++) {       // grow the prefix of s1 one char at a time
            for (int j = 1; j <= m; j++) {   // every prefix length of s2
                // last characters match: extend the LCS of both shorter prefixes
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) dp[i][j] = 1 + dp[i - 1][j - 1];
                // drop one char from s1 (up) or s2 (left)
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[n][m];                     // LCS of the two full strings
    }

    /** Approach 4: keep only the previous row and the current row. O(n * m) time, O(m) space. */
    static int spaceOptimized(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[] prev = new int[m + 1], cur = new int[m + 1];    // prev = row i - 1, cur = row i being filled
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                // last characters match: diagonal of the previous row plus one
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) cur[j] = 1 + prev[j - 1];
                else cur[j] = Math.max(prev[j], cur[j - 1]);  // up = prev[j], left = cur[j - 1]
            }
            int[] tmp = prev; prev = cur; cur = tmp;          // the row just built becomes "previous"
        }
        return prev[m];                                       // after the last swap prev holds row n
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s1, String s2, int expected) {
        String in = "\"" + s1 + "\", \"" + s2 + "\"";
        check(recursive(s1, s2) == expected, "recursive failed for " + in);
        check(memoization(s1, s2) == expected, "memoization failed for " + in);
        check(tabulation(s1, s2) == expected, "tabulation failed for " + in);
        check(spaceOptimized(s1, s2) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify("abcde", "ace", 3);                  // "ace"
        verify("abc", "abc", 3);                    // identical strings
        verify("abc", "def", 0);                    // nothing in common
        verify("AGGTAB", "GXTXAYB", 4);             // "GTAB"
        verify("bl", "yby", 1);                     // "b"
        verify("", "abc", 0);                       // edge: empty string
        verify("", "", 0);                          // edge: both empty
        verify("aaaa", "aa", 2);                    // repeated characters

        // Cross-check every approach on seeded random strings over a small alphabet.
        Random rnd = new Random(308);
        for (int t = 0; t < 200; t++) {
            String a = randomString(rnd, rnd.nextInt(9)), b = randomString(rnd, rnd.nextInt(9));
            verify(a, b, tabulation(a, b));
        }
        System.out.println("OK P308_LongestCommonSubsequence");
    }

    static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
        return sb.toString();
    }
}
