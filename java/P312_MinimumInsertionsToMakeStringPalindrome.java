import java.util.*;

/** TUF 312 - Minimum characters to insert at the beginning of s to make it a palindrome (SDE sheet, String Part-II). Answer = n - longest palindromic prefix. */
public class P312_MinimumInsertionsToMakeStringPalindrome {

    /** Approach 1: test prefixes from the longest down; the first palindrome is the one to keep. O(n^2) time, O(1) space. */
    static int bruteForce(String s) {
        int n = s.length();
        for (int k = n; k > 0; k--) {
            if (isPalindrome(s, 0, k - 1)) return n - k;   // keep s[0..k), mirror the other n - k characters in front
        }
        return 0;                                          // only reached when s is empty
    }

    static boolean isPalindrome(String s, int lo, int hi) {
        while (lo < hi) {
            if (s.charAt(lo++) != s.charAt(hi--)) return false;
        }
        return true;
    }

    /** Approach 2: rolling hashes of each prefix read forwards and backwards; equal hashes mean a palindrome. O(n) time, O(1) space. */
    static int rollingHash(String s) {
        final long B = 131, M1 = 1_000_000_007L, M2 = 998_244_353L;   // base and two prime moduli
        long fwd1 = 0, fwd2 = 0, rev1 = 0, rev2 = 0, pow1 = 1, pow2 = 1;
        int best = 0;                                      // longest palindromic prefix found so far
        for (int i = 0; i < s.length(); i++) {
            long c = s.charAt(i);
            fwd1 = (fwd1 * B + c) % M1;                    // s[0..i] read left to right: new character is the lowest power
            fwd2 = (fwd2 * B + c) % M2;
            rev1 = (rev1 + c * pow1) % M1;                 // s[0..i] read right to left: new character is the highest power
            rev2 = (rev2 + c * pow2) % M2;
            pow1 = pow1 * B % M1;
            pow2 = pow2 * B % M2;
            if (fwd1 == rev1 && fwd2 == rev2) best = i + 1;
        }
        return s.length() - best;
    }

    /** Approach 3: KMP prefix function of s + '$' + reverse(s); its last value is the longest palindromic prefix. O(n) time and space. */
    static int optimal(String s) {
        int n = s.length();
        if (n == 0) return 0;
        String t = s + '$' + new StringBuilder(s).reverse();   // '$' must not occur in s
        int[] lps = new int[t.length()];
        int len = 0;
        for (int i = 1; i < t.length(); i++) {
            while (len > 0 && t.charAt(i) != t.charAt(len)) len = lps[len - 1];
            if (t.charAt(i) == t.charAt(len)) len++;
            lps[i] = len;
        }
        return n - lps[t.length() - 1];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        String in = "\"" + s + "\"";
        check(bruteForce(s) == expected, "bruteForce failed for " + in + ": " + bruteForce(s));
        check(rollingHash(s) == expected, "rollingHash failed for " + in + ": " + rollingHash(s));
        check(optimal(s) == expected, "optimal failed for " + in + ": " + optimal(s));
    }

    public static void main(String[] args) {
        verify("abc", 2);                           // "cbabc"
        verify("aacecaaa", 1);                      // "aaacecaaa": the prefix "aacecaa" is kept
        verify("abcd", 3);                          // "dcbabcd"
        verify("abab", 1);                          // "babab"
        verify("baaa", 3);                          // only "b" is a palindromic prefix: "aaabaaa"
        verify("aaaabaaa", 4);                      // brute force worst-case shape: long checks that fail at the b
        verify("abb", 2);                           // "bbabb" (inserting anywhere would need only 1)
        verify("aba", 0);                           // already a palindrome
        verify("AACECAAAA", 2);                     // uppercase works too: "AACECAA" is the longest palindromic prefix
        verify("a", 0);                             // edge: one character
        verify("", 0);                              // edge: empty string

        // Cross-check against brute force on seeded random strings.
        Random rnd = new Random(312);
        for (int t = 0; t < 1000; t++) {
            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(18);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(2)));
            String s = sb.toString();
            verify(s, bruteForce(s));
        }
        System.out.println("OK P312_MinimumInsertionsToMakeStringPalindrome");
    }
}
