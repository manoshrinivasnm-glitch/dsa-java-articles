import java.util.*;

/** TUF 329 - Palindrome partitioning II. Minimum cuts so that every piece of s is a palindrome. */
public class P329_PalindromePartitioningII {

    static boolean isPalindrome(String s, int i, int j) {
        while (i < j) if (s.charAt(i++) != s.charAt(j--)) return false;   // compare inward from both ends
        return true;
    }

    /** Approach 1: choose the first palindromic piece, recurse on the rest. Exponential time, O(n) stack. */
    static int recursive(String s) {
        return solve(0, s) - 1;                                 // k pieces need k - 1 cuts
    }

    static int solve(int i, String s) {
        if (i == s.length()) return 0;                          // nothing left: zero pieces
        int best = Integer.MAX_VALUE;                           // fewest palindromic pieces for s[i..]
        for (int j = i; j < s.length(); j++) {                  // s[i..j] is a candidate first piece
            if (isPalindrome(s, i, j)) best = Math.min(best, 1 + solve(j + 1, s));   // palindrome: 1 piece + rest
        }
        return best;
    }

    /** Approach 2: cache the answer for every suffix start i. O(n^3) time, O(n) space. */
    static int memoization(String s) {
        int[] dp = new int[s.length()];                         // dp[i] = fewest pieces for the suffix s[i..]
        Arrays.fill(dp, -1);                                    // -1 marks a suffix not solved yet
        return memo(0, s, dp) - 1;                              // k pieces need k - 1 cuts
    }

    static int memo(int i, String s, int[] dp) {
        if (i == s.length()) return 0;                          // nothing left: zero pieces
        if (dp[i] != -1) return dp[i];                          // already solved: reuse it
        int best = Integer.MAX_VALUE;
        for (int j = i; j < s.length(); j++) {                  // s[i..j] is a candidate first piece
            if (isPalindrome(s, i, j)) best = Math.min(best, 1 + memo(j + 1, s, dp));   // palindrome: 1 + the rest
        }
        return dp[i] = best;                                    // store the answer before returning it
    }

    /** Approach 3: fill dp[i] from the right end. O(n^3) time, O(n) space. */
    static int tabulation(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];                              // dp[n] = 0
        for (int i = n - 1; i >= 0; i--) {                      // suffixes from shortest to longest
            dp[i] = Integer.MAX_VALUE;                          // dp[i] = fewest pieces for s[i..]
            for (int j = i; j < n; j++) {
                if (isPalindrome(s, i, j)) dp[i] = Math.min(dp[i], 1 + dp[j + 1]);   // 1 piece + best of s[j+1..]
            }
        }
        return dp[0] - 1;                                       // k pieces need k - 1 cuts
    }

    /** Approach 4: build the palindrome table in the same loop, so each check is O(1). O(n^2) time and space. */
    static int optimal(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];                    // pal[i][j] = s[i..j] is a palindrome
        int[] dp = new int[n + 1];                              // dp[n] = 0
        for (int i = n - 1; i >= 0; i--) {                      // i goes down, so dp[j + 1] and row i + 1 are ready
            dp[i] = Integer.MAX_VALUE;                          // dp[i] = fewest pieces for s[i..]
            for (int j = i; j < n; j++) {
                // ends match and the inside (if any) is a palindrome
                if (s.charAt(i) == s.charAt(j) && (j - i < 2 || pal[i + 1][j - 1])) {
                    pal[i][j] = true;                           // row i + 1 was filled one iteration earlier
                    dp[i] = Math.min(dp[i], 1 + dp[j + 1]);     // one piece plus the best for s[j+1..]
                }
            }
        }
        return dp[0] - 1;                                       // k pieces need k - 1 cuts
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
