import java.util.*;

/** TUF 313 - Shortest Common Supersequence (LeetCode 1092). Return a shortest string that has both s1 and s2 as subsequences. */
public class P313_ShortestCommonSupersequence {

    /** Approach 1: recursion that returns the supersequence of two prefixes. Exponential time, O(n + m) stack. */
    static String bruteForce(String s1, String s2) {
        return build(s1.length(), s2.length(), s1, s2);
    }

    /** A shortest common supersequence of s1[0..i) and s2[0..j). Ties go to "s2's last character goes last". */
    static String build(int i, int j, String s1, String s2) {
        if (i == 0) return s2.substring(0, j);                   // nothing left of s1: the rest of s2 is needed as is
        if (j == 0) return s1.substring(0, i);
        if (s1.charAt(i - 1) == s2.charAt(j - 1)) return build(i - 1, j - 1, s1, s2) + s1.charAt(i - 1);  // one copy serves both
        String up = build(i - 1, j, s1, s2) + s1.charAt(i - 1);  // s1's last character ends the answer
        String left = build(i, j - 1, s1, s2) + s2.charAt(j - 1);// s2's last character ends the answer
        return up.length() < left.length() ? up : left;
    }

    /** Approach 2: LCS table, then walk back from dp[n][m] writing every character once. O(n * m) time and space. */
    static String optimal(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) dp[i][j] = 1 + dp[i - 1][j - 1];
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        StringBuilder sb = new StringBuilder();                  // built backwards, reversed at the end
        int i = n, j = m;
        while (i > 0 && j > 0) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {          // an LCS character: write it once for both strings
                sb.append(s1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                sb.append(s1.charAt(i - 1));                     // s1's character is not shared: write it alone
                i--;
            } else {
                sb.append(s2.charAt(j - 1));
                j--;
            }
        }
        while (i > 0) sb.append(s1.charAt(--i));                 // whatever is left of either string
        while (j > 0) sb.append(s2.charAt(--j));
        return sb.reverse().toString();
    }

    /** Length only: n + m - LCS, with the two-row LCS. O(n * m) time, O(m) space. */
    static int shortestLength(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[] prev = new int[m + 1], cur = new int[m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) cur[j] = 1 + prev[j - 1];
                else cur[j] = Math.max(prev[j], cur[j - 1]);
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return n + m - prev[m];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static boolean isSubsequence(String sub, String s) {
        int k = 0;
        for (int i = 0; i < s.length() && k < sub.length(); i++) if (s.charAt(i) == sub.charAt(k)) k++;
        return k == sub.length();
    }

    /** Every approach must return a common supersequence of the expected length; exact is non-null when the answer is unique. */
    static void verify(String s1, String s2, int expectedLength, String exact) {
        String in = "\"" + s1 + "\", \"" + s2 + "\"";
        check(shortestLength(s1, s2) == expectedLength, "shortestLength failed for " + in);
        String[] results = {bruteForce(s1, s2), optimal(s1, s2)};
        String[] names = {"bruteForce", "optimal"};
        for (int r = 0; r < results.length; r++) {
            String got = results[r];
            check(got.length() == expectedLength, names[r] + " wrong length for " + in + ": " + got);
            check(isSubsequence(s1, got) && isSubsequence(s2, got), names[r] + " not a supersequence for " + in + ": " + got);
            if (exact != null) check(got.equals(exact), names[r] + " expected " + exact + " for " + in + ": " + got);
        }
        check(results[0].equals(results[1]), "bruteForce and optimal use the same tie-break for " + in);
    }

    public static void main(String[] args) {
        verify("abac", "cab", 5, null);             // e.g. "cabac"
        verify("brute", "groot", 8, null);          // e.g. "bgruoote"
        verify("aaaaaaaa", "aaaaaaaa", 8, "aaaaaaaa");   // identical strings
        verify("abc", "def", 6, null);              // nothing shared: interleave all six
        verify("", "abc", 3, "abc");                // edge: one string empty
        verify("", "", 0, "");                      // edge: both empty
        verify("ab", "b", 2, "ab");                 // one string is a subsequence of the other

        // Cross-check on seeded random strings: every result must be a valid shortest supersequence.
        Random rnd = new Random(313);
        for (int t = 0; t < 200; t++) {
            String a = randomString(rnd, rnd.nextInt(9)), b = randomString(rnd, rnd.nextInt(9));
            verify(a, b, shortestLength(a, b), null);
        }
        System.out.println("OK P313_ShortestCommonSupersequence");
    }

    static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
        return sb.toString();
    }
}
