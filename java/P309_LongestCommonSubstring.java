import java.util.*;

/** TUF 309 - Longest Common Substring. Length of the longest contiguous block of characters present in both strings. */
public class P309_LongestCommonSubstring {

    /** Approach 1: try every pair of start positions and extend while the characters agree. O(n * m * min(n, m)) time, O(1) space. */
    static int bruteForce(String s1, String s2) {
        int n = s1.length(), m = s2.length(), best = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int len = 0;
                while (i + len < n && j + len < m && s1.charAt(i + len) == s2.charAt(j + len)) len++;
                best = Math.max(best, len);
            }
        }
        return best;
    }

    /** Approach 2: dp[i][j] = length of the common block ending exactly at s1[i - 1] and s2[j - 1]. O(n * m) time and space. */
    static int tabulation(String s1, String s2) {
        int n = s1.length(), m = s2.length(), best = 0;
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];             // extend the block that ended one step earlier
                    best = Math.max(best, dp[i][j]);
                } else {
                    dp[i][j] = 0;                                // a mismatch breaks every block ending here
                }
            }
        }
        return best;
    }

    /** Approach 3: only the previous row is ever read, so keep two rows. O(n * m) time, O(m) space. */
    static int spaceOptimized(String s1, String s2) {
        int n = s1.length(), m = s2.length(), best = 0;
        int[] prev = new int[m + 1], cur = new int[m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    cur[j] = 1 + prev[j - 1];
                    best = Math.max(best, cur[j]);
                } else {
                    cur[j] = 0;                                  // must be reset: the array holds an older row
                }
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return best;
    }

    /** Approach 4: walk each diagonal of the table with a single running counter. O(n * m) time, O(1) space. */
    static int diagonalScan(String s1, String s2) {
        int n = s1.length(), m = s2.length(), best = 0;
        for (int shift = -(n - 1); shift <= m - 1; shift++) {    // compare s1[i] with s2[i + shift]
            int run = 0;
            for (int i = Math.max(0, -shift); i < n && i + shift < m; i++) {
                run = s1.charAt(i) == s2.charAt(i + shift) ? run + 1 : 0;
                best = Math.max(best, run);
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s1, String s2, int expected) {
        String in = "\"" + s1 + "\", \"" + s2 + "\"";
        check(bruteForce(s1, s2) == expected, "bruteForce failed for " + in);
        check(tabulation(s1, s2) == expected, "tabulation failed for " + in);
        check(spaceOptimized(s1, s2) == expected, "spaceOptimized failed for " + in);
        check(diagonalScan(s1, s2) == expected, "diagonalScan failed for " + in);
    }

    public static void main(String[] args) {
        verify("abcjklp", "acjkp", 3);              // "cjk"
        verify("ABCDGH", "ACDGHR", 4);              // "CDGH"
        verify("wasdijkl", "wsdjkl", 3);            // "jkl"
        verify("abab", "bab", 3);                   // "bab"
        verify("abcde", "ace", 1);                  // a common subsequence of 3, but no common block longer than 1
        verify("abc", "def", 0);                    // nothing in common
        verify("", "abc", 0);                       // edge: empty string
        verify("aaaa", "aa", 2);                    // repeated characters
        verify("same", "same", 4);                  // identical strings

        // Cross-check every approach on seeded random strings over a small alphabet.
        Random rnd = new Random(309);
        for (int t = 0; t < 300; t++) {
            String a = randomString(rnd, rnd.nextInt(15)), b = randomString(rnd, rnd.nextInt(15));
            verify(a, b, bruteForce(a, b));
        }
        System.out.println("OK P309_LongestCommonSubstring");
    }

    static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(2)));
        return sb.toString();
    }
}
