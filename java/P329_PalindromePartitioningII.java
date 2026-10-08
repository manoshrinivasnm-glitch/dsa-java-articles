import java.util.*;

/** TUF 329 - Palindrome partitioning II. Minimum cuts so that every piece of s is a palindrome. */
public class P329_PalindromePartitioningII {

    static boolean isPalindrome(String s, int i, int j) {
        while (i < j) if (s.charAt(i++) != s.charAt(j--)) return false;
        return true;
    }

    /** Approach 1: choose the first palindromic piece, recurse on the rest. Exponential time, O(n) stack. */
    static int recursive(String s) {
        return solve(0, s) - 1;                                 // k pieces need k - 1 cuts
    }

    static int solve(int i, String s) {
        if (i == s.length()) return 0;                          // nothing left: zero pieces
        int best = Integer.MAX_VALUE;
        for (int j = i; j < s.length(); j++) {
            if (isPalindrome(s, i, j)) best = Math.min(best, 1 + solve(j + 1, s));
        }
        return best;
    }

    /** Approach 2: cache the answer for every suffix start i. O(n^3) time, O(n) space. */
    static int memoization(String s) {
        int[] dp = new int[s.length()];
        Arrays.fill(dp, -1);
        return memo(0, s, dp) - 1;
    }

    static int memo(int i, String s, int[] dp) {
        if (i == s.length()) return 0;
        if (dp[i] != -1) return dp[i];
        int best = Integer.MAX_VALUE;
        for (int j = i; j < s.length(); j++) {
            if (isPalindrome(s, i, j)) best = Math.min(best, 1 + memo(j + 1, s, dp));
        }
        return dp[i] = best;
    }

    /** Approach 3: fill dp[i] from the right end. O(n^3) time, O(n) space. */
    static int tabulation(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];                              // dp[n] = 0
        for (int i = n - 1; i >= 0; i--) {
            dp[i] = Integer.MAX_VALUE;
            for (int j = i; j < n; j++) {
                if (isPalindrome(s, i, j)) dp[i] = Math.min(dp[i], 1 + dp[j + 1]);
            }
        }
        return dp[0] - 1;
    }

    /** Approach 4: build the palindrome table in the same loop, so each check is O(1). O(n^2) time and space. */
    static int optimal(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];
        int[] dp = new int[n + 1];                              // dp[n] = 0
        for (int i = n - 1; i >= 0; i--) {
            dp[i] = Integer.MAX_VALUE;
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i < 2 || pal[i + 1][j - 1])) {
                    pal[i][j] = true;                           // row i + 1 was filled one iteration earlier
                    dp[i] = Math.min(dp[i], 1 + dp[j + 1]);
                }
            }
        }
        return dp[0] - 1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected, boolean small) {
        if (small) check(recursive(s) == expected, "recursive failed for " + s);
        check(memoization(s) == expected, "memoization failed for " + s);
        check(tabulation(s) == expected, "tabulation failed for " + s);
        check(optimal(s) == expected, "optimal failed for " + s);
    }

    public static void main(String[] args) {
        verify("aab", 1, true);                                 // aa | b
        verify("bababcbadcede", 4, true);                       // bab | abcba | d | c | ede
        verify("noonabbad", 2, true);                           // noon | abba | d
        verify("abcde", 4, true);                               // no piece longer than one letter helps
        verify("ab", 1, true);
        verify("aaaa", 0, true);                                // already a palindrome
        verify("a", 0, true);                                   // edge: a single character
        verify("cabababcbc", 3, true);                          // c | ababa | bcb | c
        verify("aaba", 1, true);                                // a | aba; greedy longest prefix "aa" needs 2
        verify("a".repeat(600), 0, false);                      // large: only the polynomial methods
        verify("ab".repeat(300), 1, false);                     // "abab...a" | "b"

        Random rnd = new Random(329);
        for (int t = 0; t < 300; t++) {
            StringBuilder sb = new StringBuilder();
            int len = 1 + rnd.nextInt(12);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
            verify(sb.toString(), recursive(sb.toString()), true);
        }
        System.out.println("OK P329_PalindromePartitioningII");
    }
}
