import java.util.*;

/** TUF 2794 - Palindromic Substrings (LeetCode 647). Count the substrings of s that are palindromes; equal text at different positions counts separately. */
public class P2794_PalindromicSubstrings {

    /** Approach 1: check every substring with two pointers. O(n^3) time, O(1) extra space. */
    static int bruteForce(String s) {
        int n = s.length(), count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (isPalindrome(s, i, j)) count++;
            }
        }
        return count;
    }

    static boolean isPalindrome(String s, int lo, int hi) {
        while (lo < hi) {
            if (s.charAt(lo++) != s.charAt(hi--)) return false;
        }
        return true;
    }

    /** Approach 2: pal[i][j] from pal[i+1][j-1], i from right to left so the inner range is ready. O(n^2) time and space. */
    static int better(String s) {
        int n = s.length(), count = 0;
        boolean[][] pal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                pal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || pal[i + 1][j - 1]);
                if (pal[i][j]) count++;
            }
        }
        return count;
    }

    /** Approach 3: every palindrome has a centre; expand from all 2n - 1 centres and count each step. O(n^2) time, O(1) space. */
    static int optimal(String s) {
        int count = 0;
        for (int c = 0; c < s.length(); c++) {
            count += countFrom(s, c, c);            // odd lengths, centred on s[c]
            count += countFrom(s, c, c + 1);        // even lengths, centred between s[c] and s[c+1]
        }
        return count;
    }

    /** Each successful expansion step uncovers exactly one new palindrome around this centre. */
    static int countFrom(String s, int lo, int hi) {
        int found = 0;
        while (lo >= 0 && hi < s.length() && s.charAt(lo) == s.charAt(hi)) {
            found++;
            lo--;
            hi++;
        }
        return found;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        String in = "\"" + s + "\"";
        check(bruteForce(s) == expected, "bruteForce failed for " + in + ": " + bruteForce(s));
        check(better(s) == expected, "better failed for " + in + ": " + better(s));
        check(optimal(s) == expected, "optimal failed for " + in + ": " + optimal(s));
    }

    public static void main(String[] args) {
        verify("abc", 3);                           // only the single characters
        verify("aaa", 6);                           // a, a, a, aa, aa, aaa
        verify("abba", 6);                          // a, b, b, a, bb, abba
        verify("racecar", 10);                      // 7 singles + cec, aceca, racecar
        verify("aabaa", 9);                         // 5 singles + aa, aa, aba, aabaa
        verify("a", 1);                             // edge: one character
        verify("", 0);                              // edge: empty string

        // n(n+1)/2 for a string of one repeated letter: every substring is a palindrome.
        verify("z".repeat(300), 300 * 301 / 2);

        // Cross-check against brute force on seeded random strings.
        Random rnd = new Random(2794);
        for (int t = 0; t < 400; t++) {
            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(16);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
            String s = sb.toString();
            verify(s, bruteForce(s));
        }
        System.out.println("OK P2794_PalindromicSubstrings");
    }
}
