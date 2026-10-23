import java.util.*;

/** TUF 638 - Longest Palindromic Substring. Return the longest contiguous substring of s that reads the same both ways (the leftmost one on ties). */
public class P638_LongestPalindromicSubstring {

    /** Approach 1: test every substring from scratch. O(n^3) time, O(1) space. */
    static String bruteForce(String s) {
        int n = s.length();
        int bestStart = 0, bestLen = 0;                    // start and length of the best palindrome so far
        for (int i = 0; i < n; i++) {                      // i = start of the substring
            for (int j = i; j < n; j++) {                  // j = end of the substring
                if (j - i + 1 > bestLen && isPalindrome(s, i, j)) {   // only test longer candidates
                    bestStart = i;
                    bestLen = j - i + 1;
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLen);   // the leftmost longest palindrome
    }

    /** True if s[i..j] reads the same from both ends. */
    private static boolean isPalindrome(String s, int i, int j) {
        while (i < j) {                                    // compare from both ends towards the middle
            if (s.charAt(i) != s.charAt(j)) return false;   // a mismatched pair: not a palindrome
            i++;
            j--;
        }
        return true;                                       // every pair matched
    }

    /** Approach 2: dp[i][j] = "s[i..j] is a palindrome", filled by increasing length. O(n^2) time, O(n^2) space. */
    static String better(String s) {
        int n = s.length();
        if (n == 0) return "";                             // empty input has no palindrome to return
        boolean[][] dp = new boolean[n][n];                // dp[i][j] = true if s[i..j] is a palindrome
        int bestStart = 0, bestLen = 1;                    // any single letter is a palindrome of length 1
        for (int len = 1; len <= n; len++) {               // shorter substrings are filled first
            for (int i = 0; i + len - 1 < n; i++) {        // i = start of the substring of length len
                int j = i + len - 1;                       // j = its end
                if (s.charAt(i) != s.charAt(j)) continue;   // ends differ: cannot be a palindrome
                // ends match: length 1 or 2 is a palindrome, a longer one needs its inside to be one
                dp[i][j] = len <= 2 || dp[i + 1][j - 1];
                if (dp[i][j] && len > bestLen) {           // a longer palindrome than any seen so far
                    bestStart = i;
                    bestLen = len;
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    /** Approach 3: every palindrome has a centre; expand outwards from each of the 2n - 1 centres. O(n^2) time, O(1) space. */
    static String optimal(String s) {
        int n = s.length();
        if (n == 0) return "";                             // empty input has no palindrome to return
        int bestStart = 0, bestLen = 1;
        for (int c = 0; c < n; c++) {                      // centres: s[c] and the gap after it
            int odd = expand(s, c, c);                     // odd length, centred on s[c]
            if (odd > bestLen) {
                bestLen = odd;
                bestStart = c - odd / 2;                   // the odd palindrome starts odd/2 letters before c
            }
            int even = expand(s, c, c + 1);                // even length, centred between s[c] and s[c + 1]
            if (even > bestLen) {
                bestLen = even;
                bestStart = c - even / 2 + 1;              // the even palindrome's left half ends at c
            }
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    /** Length of the longest palindrome grown from the centre (lo, hi): lo == hi for odd length, hi == lo + 1 for even. */
    private static int expand(String s, int lo, int hi) {
        // grow outwards while both ends are inside s and still match
        while (lo >= 0 && hi < s.length() && s.charAt(lo) == s.charAt(hi)) {
            lo--;
            hi++;
        }
        return hi - lo - 1;                                // lo and hi went one step too far, so drop both
    }

    /** Approach 4: Manacher's algorithm; the radius already known for the mirror centre is reused, so the total expansion work is linear. O(n) time, O(n) space. */
    static String manacher(String s) {
        int n = s.length();
        if (n == 0) return "";                             // empty input has no palindrome to return
        char[] t = new char[2 * n + 3];                    // ^ # s0 # s1 # ... # s(n-1) # $
        t[0] = '^';                                        // sentinels stop expansion at the ends
        t[2 * n + 2] = '$';
        for (int i = 0; i < n; i++) {
            t[2 * i + 1] = '#';                            // '#' makes every palindrome in t odd
            t[2 * i + 2] = s.charAt(i);
        }
        t[2 * n + 1] = '#';
        int m = t.length;
        int[] p = new int[m];                              // p[i] = radius of the palindrome centred at t[i]
        int center = 0, right = 0;                         // the palindrome reaching furthest right so far
        int bestLen = 0, bestCenter = 0;                   // longest radius seen and where it is centred
        for (int i = 1; i < m - 1; i++) {                  // every real position of t (skip the sentinels)
            int mirror = 2 * center - i;                   // i reflected around center
            // inside the known palindrome: start from the mirror's radius, capped at its right edge
            if (i < right) p[i] = Math.min(right - i, p[mirror]);
            while (t[i + p[i] + 1] == t[i - p[i] - 1]) p[i]++;   // try to grow beyond what is already known
            if (i + p[i] > right) {                        // reaches further right: new reference
                center = i;
                right = i + p[i];
            }
            if (p[i] > bestLen) {                          // longest palindrome so far
                bestLen = p[i];
                bestCenter = i;
            }
        }
        int start = (bestCenter - bestLen) / 2;            // radius in t equals length in s
        return s.substring(start, start + bestLen);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String expected) {
        check(bruteForce(s).equals(expected), "bruteForce(\"" + s + "\") = " + bruteForce(s) + " != " + expected);
        check(better(s).equals(expected), "better(\"" + s + "\") = " + better(s) + " != " + expected);
        check(optimal(s).equals(expected), "optimal(\"" + s + "\") = " + optimal(s) + " != " + expected);
        check(manacher(s).equals(expected), "manacher(\"" + s + "\") = " + manacher(s) + " != " + expected);
    }

    static String randomString(Random rnd, int n, int alphabet) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alphabet)));
        return sb.toString();
    }

    public static void main(String[] args) {
        verify("babad", "bab");                            // "aba" is equally long; the leftmost one is returned
        verify("cbbd", "bb");                              // even length
        verify("a", "a");
        verify("", "");                                    // empty input
        verify("ac", "a");                                 // no palindrome longer than one letter
        verify("forgeeksskeegfor", "geeksskeeg");
        verify("aaaa", "aaaa");                            // the whole string
        verify("abacdfgdcaba", "aba");
        verify("bananas", "anana");
        verify("abcba", "abcba");
        verify("abb", "bb");
        verify("aacabdkacaa", "aca");
        Random rnd = new Random(7);
        for (int t = 0; t < 300; t++) {
            String s = randomString(rnd, 1 + rnd.nextInt(30), 2 + rnd.nextInt(2));
            String expected = bruteForce(s);
            check(better(s).equals(expected), "better on random " + s);
            check(optimal(s).equals(expected), "optimal on random " + s);
            check(manacher(s).equals(expected), "manacher on random " + s);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5000; i++) sb.append('a');
        String big = sb.toString();                        // worst case for centre expansion: every centre grows to the edge
        check(better(big).length() == 5000, "dp on 5000 a's");
        check(optimal(big).length() == 5000, "expand around centre on 5000 a's");
        check(manacher(big).length() == 5000, "manacher on 5000 a's");
        System.out.println("OK P638_LongestPalindromicSubstring");
    }
}
