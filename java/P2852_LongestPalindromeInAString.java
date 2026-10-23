import java.util.*;

/** TUF 2852 - Longest Palindrome in a string (LeetCode 5). Return the longest palindromic substring; on ties, the leftmost one. */
public class P2852_LongestPalindromeInAString {

    /** Approach 1: test every substring with two pointers. O(n^3) time, O(1) extra space. */
    static String bruteForce(String s) {
        int n = s.length(), bestStart = 0, bestLen = 0;         // best palindrome so far: start, length
        for (int i = 0; i < n; i++) {                           // every possible start index
            for (int j = i; j < n; j++) {                       // every end index from i onward
                int len = j - i + 1;
                if (len > bestLen && isPalindrome(s, i, j)) {   // strict '>' keeps the leftmost on ties
                    bestStart = i;
                    bestLen = len;
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    static boolean isPalindrome(String s, int lo, int hi) {
        while (lo < hi) {                                         // walk both ends toward the middle
            if (s.charAt(lo++) != s.charAt(hi--)) return false;   // a mismatched pair means not a palindrome
        }
        return true;
    }

    /** Approach 2: pal[i][j] = s[i] == s[j] and pal[i+1][j-1], filled by increasing length. O(n^2) time and space. */
    static String better(String s) {
        int n = s.length(), bestStart = 0, bestLen = 0;   // best palindrome so far: start, length
        boolean[][] pal = new boolean[n][n];              // pal[i][j] is true when s[i..j] is a palindrome
        for (int len = 1; len <= n; len++) {              // short lengths first, so pal[i+1][j-1] is ready
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;                      // end index of the window of this length
                // ends match and the inside is a palindrome (or empty)
                pal[i][j] = s.charAt(i) == s.charAt(j) && (len <= 2 || pal[i + 1][j - 1]);
                if (pal[i][j] && len > bestLen) {         // strict '>' keeps the leftmost on ties
                    bestStart = i;
                    bestLen = len;
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    /** Approach 3: expand around each of the 2n - 1 centres. O(n^2) time, O(1) extra space. */
    static String optimal(String s) {
        int n = s.length(), bestStart = 0, bestLen = 0;
        for (int c = 0; c < n; c++) {               // try every index as a centre
            int odd = expand(s, c, c);              // centre on a character: "aba"
            int even = expand(s, c, c + 1);         // centre between two characters: "abba"
            if (odd > bestLen) {                    // odd palindrome beats the best: record it
                bestLen = odd;
                bestStart = c - odd / 2;
            }
            if (even > bestLen) {                   // even palindrome beats the best: record it
                bestLen = even;
                bestStart = c - even / 2 + 1;
            }
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    /** Grows the window [lo, hi] while it stays a palindrome and returns the length of the last valid window. */
    static int expand(String s, int lo, int hi) {
        // stretch outward while the two ends match
        while (lo >= 0 && hi < s.length() && s.charAt(lo) == s.charAt(hi)) {
            lo--;
            hi++;
        }
        return hi - lo - 1;   // lo and hi overshot by one each, so the length is hi - lo - 1
    }

    /** Approach 4: Manacher's algorithm reuses mirror radii inside the rightmost palindrome. O(n) time and space. */
    static String manacher(String s) {
        int n = s.length();
        if (n == 0) return "";                     // empty string: the answer is empty
        char[] t = new char[2 * n + 1];            // "abc" -> "|a|b|c|": every palindrome in t has odd length
        for (int i = 0; i < t.length; i++) t[i] = (i % 2 == 0) ? '|' : s.charAt(i / 2);
        int[] rad = new int[t.length];             // rad[i] = how far the palindrome centred at i reaches on each side
        int centre = 0, right = 0;                 // the palindrome reaching furthest right is t[2*centre-right .. right]
        int bestCentre = 0;                        // centre in t of the longest palindrome so far
        for (int i = 0; i < t.length; i++) {       // compute rad[i] for every position of t
            if (i < right) rad[i] = Math.min(right - i, rad[2 * centre - i]);   // copy from the mirror, capped at the boundary
            // try to grow beyond what the mirror guaranteed
            while (i - rad[i] - 1 >= 0 && i + rad[i] + 1 < t.length && t[i - rad[i] - 1] == t[i + rad[i] + 1]) rad[i]++;
            if (i + rad[i] > right) {              // reaches further right: becomes the new frame
                centre = i;
                right = i + rad[i];
            }
            if (rad[i] > rad[bestCentre]) bestCentre = i;   // remember the widest palindrome seen
        }
        int start = (bestCentre - rad[bestCentre]) / 2;   // rad in t equals the palindrome's length in s
        return s.substring(start, start + rad[bestCentre]);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String expected) {
        String in = "\"" + s + "\"";
        check(bruteForce(s).equals(expected), "bruteForce failed for " + in + ": " + bruteForce(s));
        check(better(s).equals(expected), "better failed for " + in + ": " + better(s));
        check(optimal(s).equals(expected), "optimal failed for " + in + ": " + optimal(s));
        check(manacher(s).equals(expected), "manacher failed for " + in + ": " + manacher(s));
    }

    public static void main(String[] args) {
        verify("babad", "bab");                     // "aba" is equally long; the leftmost is returned
        verify("cbbd", "bb");                       // even-length answer
        verify("forgeeksskeegfor", "geeksskeeg");
        verify("racecar", "racecar");               // whole string
        verify("abc", "a");                         // no palindrome longer than one character
        verify("aaaa", "aaaa");                     // all equal characters
        verify("a", "a");                           // edge: single character
        verify("", "");                             // edge: empty string

        // Cross-check every approach against brute force on seeded random strings.
        Random rnd = new Random(2852);
        for (int t = 0; t < 400; t++) {
            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(16);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
            String s = sb.toString();
            verify(s, bruteForce(s));
        }
        System.out.println("OK P2852_LongestPalindromeInAString");
    }
}
