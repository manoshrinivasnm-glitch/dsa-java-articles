import java.util.*;

/** TUF 979 - Rabin Karp Algorithm. Return every 0-based index where pattern occurs in text (overlaps allowed). */
public class P979_RabinKarpAlgorithm {

    /** Approach 1: compare the pattern against every window character by character. O(n * m) time, O(1) extra. */
    static List<Integer> bruteForce(String text, String pattern) {
        List<Integer> res = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) return res;
        for (int i = 0; i + m <= n; i++) {
            int k = 0;
            while (k < m && text.charAt(i + k) == pattern.charAt(k)) k++;
            if (k == m) res.add(i);
        }
        return res;
    }

    static final long MOD = 1_000_000_007L;
    static final long BASE = 256;

    /** Approach 2: rolling hash of each window, full comparison only when hashes agree. O(n + m) expected. */
    static List<Integer> rabinKarp(String text, String pattern) {
        List<Integer> res = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0 || m > n) return res;
        long high = 1;                                   // BASE^(m-1): weight of the window's first char
        for (int i = 1; i < m; i++) high = high * BASE % MOD;
        long hp = 0, hw = 0;                             // hash of pattern, hash of current window
        for (int i = 0; i < m; i++) {
            hp = (hp * BASE + pattern.charAt(i)) % MOD;
            hw = (hw * BASE + text.charAt(i)) % MOD;
        }
        for (int i = 0; ; i++) {
            if (hw == hp && text.regionMatches(i, pattern, 0, m)) res.add(i);   // verify: hashes can collide
            if (i + m == n) break;
            hw = (hw - text.charAt(i) * high % MOD + MOD) % MOD;               // remove text[i]
            hw = (hw * BASE + text.charAt(i + m)) % MOD;                        // append text[i + m]
        }
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String text, String pattern, List<Integer> expected) {
        check(bruteForce(text, pattern).equals(expected), "bruteForce " + text + " / " + pattern);
        check(rabinKarp(text, pattern).equals(expected), "rabinKarp " + text + " / " + pattern);
    }

    public static void main(String[] args) {
        verify("abracadabra", "abra", List.of(0, 7));
        verify("geeksforgeeks", "geek", List.of(0, 8));
        verify("aaaaa", "aa", List.of(0, 1, 2, 3));           // overlapping matches
        verify("abc", "d", List.of());                         // no match
        verify("ab", "abc", List.of());                        // pattern longer than text
        verify("a", "a", List.of(0));                          // single character
        verify("", "a", List.of());                            // empty text
        verify("hello", "", List.of());                        // empty pattern: no matches by convention
        verify("mississippi", "issi", List.of(1, 4));

        Random rnd = new Random(979);
        for (int trial = 0; trial < 500; trial++) {
            StringBuilder t = new StringBuilder(), p = new StringBuilder();
            int n = rnd.nextInt(30), m = 1 + rnd.nextInt(4);
            for (int i = 0; i < n; i++) t.append((char) ('a' + rnd.nextInt(2)));
            for (int i = 0; i < m; i++) p.append((char) ('a' + rnd.nextInt(2)));
            check(bruteForce(t.toString(), p.toString()).equals(rabinKarp(t.toString(), p.toString())), "random " + t + " / " + p);
        }
        String big = "ab".repeat(100_000);
        check(rabinKarp(big, "ba").size() == 99_999, "large input");
        System.out.println("OK P979_RabinKarpAlgorithm");
    }
}
