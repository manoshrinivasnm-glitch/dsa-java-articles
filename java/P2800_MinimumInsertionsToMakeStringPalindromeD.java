import java.util.*;

/** TUF 2800 - Minimum insertions to make a string palindrome (DP 29, LeetCode 1312). Fewest characters to insert anywhere so s becomes a palindrome. */
public class P2800_MinimumInsertionsToMakeStringPalindromeD {

    /** Approach 1: recursion on the range s[i..j]. O(2^n) time, O(n) stack. */
    static int recursive(String s) {
        return solve(0, s.length() - 1, s);
    }

    /** Fewest insertions that turn s[i..j] (inclusive) into a palindrome. */
    static int solve(int i, int j, String s) {
        if (i >= j) return 0;                                    // empty or one character: already a palindrome
        if (s.charAt(i) == s.charAt(j)) return solve(i + 1, j - 1, s);   // the ends already pair up
        return 1 + Math.min(solve(i + 1, j, s),                  // insert a copy of s[i] after j
                            solve(i, j - 1, s));                 // insert a copy of s[j] before i
    }

    /** Approach 2: memoization on (i, j). O(n^2) time, O(n^2) space plus the stack. */
    static int memoization(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(0, n - 1, s, dp);
    }

    static int memo(int i, int j, String s, int[][] dp) {
        if (i >= j) return 0;
        if (dp[i][j] != -1) return dp[i][j];
        if (s.charAt(i) == s.charAt(j)) return dp[i][j] = memo(i + 1, j - 1, s, dp);
        return dp[i][j] = 1 + Math.min(memo(i + 1, j, s, dp), memo(i, j - 1, s, dp));
    }

    /** Approach 3: tabulation, i from right to left, j from left to right. O(n^2) time and space. */
    static int tabulation(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int[][] dp = new int[n][n];                              // dp[i][i] = 0 and i > j entries stay 0
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) dp[i][j] = dp[i + 1][j - 1];
                else dp[i][j] = 1 + Math.min(dp[i + 1][j], dp[i][j - 1]);
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
            cur[i] = 0;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) cur[j] = j - 1 > i ? next[j - 1] : 0;  // j == i + 1: nothing inside
                else cur[j] = 1 + Math.min(next[j], cur[j - 1]);
            }
            int[] tmp = next; next = cur; cur = tmp;
        }
        return next[n - 1];
    }

    /** Approach 5: keep the longest palindromic subsequence, insert a partner for everything else. O(n^2) time, O(n) space. */
    static int viaLps(String s) {
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
        return n - prev[n];                                      // prev[n] = LCS(s, reverse(s)) = LPS(s)
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
        check(viaLps(s) == expected, "viaLps failed for " + in);
    }

    public static void main(String[] args) {
        verify("zzazz", 0);                         // already a palindrome
        verify("mbadm", 2);                         // "mbdadbm" or "mdbabdm"
        verify("leetcode", 5);                      // "leetcodocteel"
        verify("abcaa", 2);                         // LPS "aca" or "aba" has length 3
        verify("abcd", 3);                          // all distinct: mirror three of them
        verify("ab", 1);
        verify("a", 0);                             // edge: one character
        verify("", 0);                              // edge: empty string

        // Cross-check every approach on seeded random strings over a small alphabet.
        Random rnd = new Random(2800);
        for (int t = 0; t < 300; t++) {
            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(14);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
            String s = sb.toString();
            verify(s, tabulation(s));
        }
        System.out.println("OK P2800_MinimumInsertionsToMakeStringPalindromeD");
    }
}
