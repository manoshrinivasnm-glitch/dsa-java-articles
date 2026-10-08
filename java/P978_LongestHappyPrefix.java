import java.util.*;

/** TUF 978 - Longest happy prefix. Return the longest proper prefix of s that is also a suffix ("" if none). */
public class P978_LongestHappyPrefix {

    /** Approach 1: try every length from n - 1 down and compare directly. O(n^2) time, O(1) extra. */
    static String bruteForce(String s) {
        int n = s.length();
        for (int len = n - 1; len >= 1; len--) {
            if (s.regionMatches(0, s, n - len, len)) return s.substring(0, len);
        }
        return "";
    }

    /** Approach 2: compare prefix and suffix hashes in O(1), verify only on a hash match. O(n) expected. */
    static String better(String s) {
        final long MOD = 1_000_000_007L, BASE = 131;
        int n = s.length();
        long[] h = new long[n + 1], pw = new long[n + 1];  // h[i] = hash of s[0..i), pw[i] = BASE^i
        pw[0] = 1;
        for (int i = 0; i < n; i++) {
            h[i + 1] = (h[i] * BASE + s.charAt(i)) % MOD;
            pw[i + 1] = pw[i] * BASE % MOD;
        }
        for (int len = n - 1; len >= 1; len--) {
            long suffix = ((h[n] - h[n - len] * pw[len]) % MOD + MOD) % MOD;   // hash of s[n-len..n)
            if (h[len] == suffix && s.regionMatches(0, s, n - len, len)) return s.substring(0, len);
        }
        return "";
    }

    /** Approach 3: the last entry of the KMP LPS array is exactly the answer's length. O(n) time, O(n) space. */
    static String optimal(String s) {
        int n = s.length();
        if (n == 0) return "";
        int[] lps = new int[n];
        int len = 0;
        for (int i = 1; i < n; i++) {
            while (len > 0 && s.charAt(i) != s.charAt(len)) len = lps[len - 1];
            if (s.charAt(i) == s.charAt(len)) len++;
            lps[i] = len;
        }
        return s.substring(0, lps[n - 1]);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String expected) {
        check(bruteForce(s).equals(expected), "bruteForce " + s);
        check(better(s).equals(expected), "better " + s);
        check(optimal(s).equals(expected), "optimal " + s);
    }

    public static void main(String[] args) {
        verify("level", "l");
        verify("ababab", "abab");                     // prefix and suffix may overlap
        verify("leetcodeleet", "leet");
        verify("aaaa", "aaa");
        verify("abc", "");                            // no happy prefix
        verify("a", "");                              // the whole string is not a proper prefix
        verify("", "");                               // edge: empty string
        verify("aabaaab", "aab");

        Random rnd = new Random(978);
        for (int trial = 0; trial < 500; trial++) {
            int n = rnd.nextInt(25);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(2)));
            String s = sb.toString(), b = bruteForce(s);
            check(better(s).equals(b) && optimal(s).equals(b), "random " + s);
        }
        String big = "a".repeat(100_000 - 1) + "b";   // quadratic for the brute force, linear for the others
        check(better(big).isEmpty() && optimal(big).isEmpty(), "large input without a border");
        String big2 = "ab".repeat(50_000);
        check(better(big2).length() == 99_998 && optimal(big2).length() == 99_998, "large periodic input");
        System.out.println("OK P978_LongestHappyPrefix");
    }
}
