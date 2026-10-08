import java.util.*;

/** TUF 2861 - Implement ATOI/STRSTR. Part A: parse a string to a clamped 32-bit int (LeetCode 8). Part B: first index of needle in haystack (LeetCode 28). */
public class P2861_ImplementATOISTRSTR {

    /** Part A: one left-to-right scan; test for overflow before multiplying. O(n) time, O(1) space. */
    static int myAtoi(String s) {
        int i = 0, n = s.length();
        while (i < n && s.charAt(i) == ' ') i++;                // 1. skip leading spaces
        int sign = 1;
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            if (s.charAt(i) == '-') sign = -1;                  // 2. at most one sign character
            i++;
        }
        int result = 0;
        while (i < n && s.charAt(i) >= '0' && s.charAt(i) <= '9') {   // 3. digits until the first non-digit
            int d = s.charAt(i) - '0';
            // 4. would result * 10 + d exceed Integer.MAX_VALUE? (MAX_VALUE = 214748364 * 10 + 7)
            if (result > Integer.MAX_VALUE / 10 || (result == Integer.MAX_VALUE / 10 && d > 7)) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            result = result * 10 + d;
            i++;
        }
        return sign * result;
    }

    /** Part B, approach 1: try every start position and compare character by character. O(n * m) time, O(1) space. */
    static int strStrBruteForce(String haystack, String needle) {
        int n = haystack.length(), m = needle.length();
        for (int start = 0; start + m <= n; start++) {
            int k = 0;
            while (k < m && haystack.charAt(start + k) == needle.charAt(k)) k++;
            if (k == m) return start;                           // all m characters matched
        }
        return -1;
    }

    /** Part B, approach 2: KMP. The LPS array says how far to fall back after a mismatch. O(n + m) time, O(m) space. */
    static int strStrKmp(String haystack, String needle) {
        int n = haystack.length(), m = needle.length();
        if (m == 0) return 0;
        int[] lps = buildLps(needle);
        int k = 0;                                              // characters of needle matched so far
        for (int i = 0; i < n; i++) {
            while (k > 0 && haystack.charAt(i) != needle.charAt(k)) k = lps[k - 1];
            if (haystack.charAt(i) == needle.charAt(k)) k++;
            if (k == m) return i - m + 1;
        }
        return -1;
    }

    /** lps[i] = length of the longest proper prefix of p[0..i] that is also a suffix of p[0..i]. */
    static int[] buildLps(String p) {
        int[] lps = new int[p.length()];
        int len = 0;                                            // length of the current matched prefix
        for (int i = 1; i < p.length(); i++) {
            while (len > 0 && p.charAt(i) != p.charAt(len)) len = lps[len - 1];
            if (p.charAt(i) == p.charAt(len)) len++;
            lps[i] = len;
        }
        return lps;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyAtoi(String s, int expected) {
        check(myAtoi(s) == expected, "myAtoi failed for \"" + s + "\": " + myAtoi(s));
    }

    static void verifyStrStr(String haystack, String needle, int expected) {
        String in = "\"" + haystack + "\", \"" + needle + "\"";
        check(strStrBruteForce(haystack, needle) == expected, "strStrBruteForce failed for " + in);
        check(strStrKmp(haystack, needle) == expected, "strStrKmp failed for " + in);
    }

    public static void main(String[] args) {
        verifyAtoi("42", 42);
        verifyAtoi("   -042", -42);                 // leading spaces and a leading zero
        verifyAtoi("1337c0d3", 1337);               // stop at the first non-digit
        verifyAtoi("0-1", 0);
        verifyAtoi("words and 987", 0);             // no digits before a letter
        verifyAtoi("+-12", 0);                      // two signs: the second is a non-digit
        verifyAtoi("-91283472332", Integer.MIN_VALUE);  // clamps low
        verifyAtoi("2147483648", Integer.MAX_VALUE);    // MAX_VALUE + 1 clamps high
        verifyAtoi("2147483647", Integer.MAX_VALUE);
        verifyAtoi("-2147483648", Integer.MIN_VALUE);   // exactly MIN_VALUE is representable
        verifyAtoi("-2147483647", -2147483647);
        verifyAtoi("00000000000000000000123", 123);     // many leading zeros never overflow
        verifyAtoi("\u0663", 0);                    // Arabic-Indic three is not an ASCII digit
        verifyAtoi("", 0);                          // edge: empty string
        verifyAtoi("   ", 0);                       // edge: only spaces

        verifyStrStr("sadbutsad", "sad", 0);
        verifyStrStr("leetcode", "leeto", -1);
        verifyStrStr("hello", "ll", 2);
        verifyStrStr("aaaaab", "aab", 3);           // brute force backs up; KMP falls back via lps
        verifyStrStr("mississippi", "issip", 4);
        verifyStrStr("abc", "abcd", -1);            // needle longer than haystack
        verifyStrStr("a", "a", 0);
        verifyStrStr("abc", "", 0);                 // edge: empty needle matches at 0
        verifyStrStr("", "a", -1);                  // edge: empty haystack

        // Cross-check both strStr methods against String.indexOf on seeded random inputs.
        Random rnd = new Random(2861);
        for (int t = 0; t < 2000; t++) {
            StringBuilder h = new StringBuilder(), p = new StringBuilder();
            int hl = rnd.nextInt(20), pl = rnd.nextInt(5);
            for (int i = 0; i < hl; i++) h.append((char) ('a' + rnd.nextInt(2)));
            for (int i = 0; i < pl; i++) p.append((char) ('a' + rnd.nextInt(2)));
            verifyStrStr(h.toString(), p.toString(), h.indexOf(p.toString()));
        }
        System.out.println("OK P2861_ImplementATOISTRSTR");
    }
}
