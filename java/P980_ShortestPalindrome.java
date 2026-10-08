import java.util.*;

/**
 * TUF 980 - Shortest Palindrome. Add the fewest characters in front of s (lowercase letters) to make it a
 * palindrome, and return that palindrome. Equivalent: keep the longest palindromic prefix, mirror the rest.
 */
public class P980_ShortestPalindrome {

    /** Approach 1: test prefixes from longest to shortest until one is a palindrome. O(n^2) time. */
    static String bruteForce(String s) {
        int k = s.length();                          // candidate length of the palindromic prefix
        while (k > 0 && !isPalindrome(s, 0, k - 1)) k--;
        return new StringBuilder(s.substring(k)).reverse() + s;
    }

    static boolean isPalindrome(String s, int lo, int hi) {
        while (lo < hi) {
            if (s.charAt(lo++) != s.charAt(hi--)) return false;
        }
        return true;
    }

    /** Approach 2: the LPS array of s + "#" + reverse(s) gives the longest palindromic prefix. O(n) time. */
    static String optimal(String s) {
        String rev = new StringBuilder(s).reverse().toString();
        String t = s + "#" + rev;                    // '#' never occurs in s, so no border can cross it
        int[] lps = new int[t.length()];
        int len = 0;
        for (int i = 1; i < t.length(); i++) {
            while (len > 0 && t.charAt(i) != t.charAt(len)) len = lps[len - 1];
            if (t.charAt(i) == t.charAt(len)) len++;
            lps[i] = len;
        }
        int k = lps[t.length() - 1];                 // length of the longest palindromic prefix of s
        return rev.substring(0, s.length() - k) + s;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String expected) {
        check(bruteForce(s).equals(expected), "bruteForce " + s);
        check(optimal(s).equals(expected), "optimal " + s);
    }

    public static void main(String[] args) {
        verify("aacecaaa", "aaacecaaa");
        verify("abcd", "dcbabcd");
        verify("", "");                               // edge: empty string
        verify("a", "a");                             // a single character is a palindrome
        verify("aba", "aba");                         // already a palindrome: add nothing
        verify("abb", "bbabb");
        verify("aaba", "abaaba");

        Random rnd = new Random(980);
        for (int trial = 0; trial < 500; trial++) {
            int n = rnd.nextInt(16);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(2)));
            String s = sb.toString(), b = bruteForce(s), o = optimal(s);
            check(b.equals(o), "random " + s);
            check(isPalindrome(o, 0, o.length() - 1) && o.endsWith(s), "result is a palindrome ending with s");
        }
        String big = "a".repeat(49_999) + "b" + "a".repeat(50_000);       // one added character makes it a palindrome
        check(optimal(big).length() == big.length() + 1, "large input");
        System.out.println("OK P980_ShortestPalindrome");
    }
}
