import java.util.*;

/** TUF 2854 - Palindrome Partitioning (MCM Variation). Fewest cuts so that every piece of s is a palindrome. */
public class P2854_PalindromePartitioningMCMVariation {

    static boolean isPalindrome(String s, int i, int j) {
        while (i < j) {
            if (s.charAt(i++) != s.charAt(j--)) return false;
        }
        return true;
    }

    /** Approach 1: choose the first palindromic piece, recurse on the rest. Exponential time, O(n) stack. */
    static int recursion(String s) {
        return pieces(0, s) - 1;                                  // cuts = pieces - 1
    }

    static int pieces(int i, String s) {                          // fewest palindromic pieces for s[i..]
        if (i == s.length()) return 0;
        int best = Integer.MAX_VALUE;
        for (int j = i; j < s.length(); j++) {
            if (isPalindrome(s, i, j)) best = Math.min(best, 1 + pieces(j + 1, s));
        }
        return best;
    }

    /** Approach 2: memoise pieces(i). O(n^3) time worst case (n states, n ends, O(n) check), O(n) space. */
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

    /** Approach 3: bottom-up from the right end. O(n^3) time worst case, O(n) space. */
    static int tabulation(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];                                // dp[i]: fewest pieces for s[i..n-1]; dp[n] = 0
        for (int i = n - 1; i >= 0; i--) {
            int best = Integer.MAX_VALUE;
            for (int j = i; j < n; j++) {
                if (isPalindrome(s, i, j)) best = Math.min(best, 1 + dp[j + 1]);
            }
            dp[i] = best;
        }
        return dp[0] - 1;
    }

    /** Approach 4: tabulation with an O(1) palindrome lookup built in the same loop. O(n^2) time, O(n^2) space. */
    static int optimal(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];                      // pal[i][j]: s[i..j] is a palindrome
        int[] dp = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            int best = Integer.MAX_VALUE;
            for (int j = i; j < n; j++) {
                // ends match and the inside is empty, one character, or already known to be a palindrome
                if (s.charAt(i) == s.charAt(j) && (j - i < 2 || pal[i + 1][j - 1])) {
                    pal[i][j] = true;
                    best = Math.min(best, 1 + dp[j + 1]);
                }
            }
            dp[i] = best;
        }
        return dp[0] - 1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        check(recursion(s) == expected, "recursion " + s);
        verifyFast(s, expected);
    }

    static void verifyFast(String s, int expected) {
        String tag = s.length() > 20 ? s.substring(0, 20) + "... (" + s.length() + ")" : s;
        check(memoization(s) == expected, "memoization " + tag);
        check(tabulation(s) == expected, "tabulation " + tag);
        check(optimal(s) == expected, "optimal " + tag);
    }

    public static void main(String[] args) {
        verify("aab", 1);                                         // aa | b
        verify("bababcbadcede", 4);                               // bab | abcba | d | c | ede
        verify("ababbbabbababa", 3);                              // a | babbbab | b | ababa
        verify("a", 0);                                           // single character: already a palindrome
        verify("aaaa", 0);                                        // whole string is a palindrome
        verify("abc", 2);                                         // all distinct: n - 1 cuts
        verify("cdd", 1);
        verify("aaba", 1);                                        // a | aba; longest-prefix greedy gives aa | b | a
        verify("noonabbad", 2);                                   // noon | abba | d
        verify("racecarannakayak", 2);                            // racecar | anna | kayak
        verifyFast("abcd".repeat(100), 399);                      // no palindrome longer than one character
        verifyFast("ab".repeat(150), 1);                          // a | bab...b
        verifyFast("x".repeat(500), 0);                           // worst case for the O(n) palindrome check
        System.out.println("OK P2854_PalindromePartitioningMCMVariation");
    }
}
