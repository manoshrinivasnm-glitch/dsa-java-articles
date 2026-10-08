import java.util.*;

/**
 * TUF 981 - Z function. z[i] = length of the longest substring starting at i that is also a prefix of s (z[0] = 0).
 * Use it to return every 0-based index where pattern occurs in text.
 */
public class P981_ZFunction {

    /** Z-array straight from the definition: extend a match from every position. O(n^2) time. */
    static int[] zBrute(String s) {
        int n = s.length();
        int[] z = new int[n];
        for (int i = 1; i < n; i++) {
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
        }
        return z;
    }

    /** Z-array in O(n) by reusing the rightmost Z-box [l, r). */
    static int[] zOptimal(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;                                  // s[l..r) equals s[0..r-l), with r as large as possible
        for (int i = 1; i < n; i++) {
            if (i < r) z[i] = Math.min(r - i, z[i - l]);   // copy the answer from inside the prefix
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
            if (i + z[i] > r) {
                l = i;
                r = i + z[i];
            }
        }
        return z;
    }

    /** Pattern search, brute force: try every window. O(n * m) time. */
    static List<Integer> searchBrute(String text, String pattern) {
        List<Integer> res = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) return res;
        for (int i = 0; i + m <= n; i++) {
            if (text.regionMatches(i, pattern, 0, m)) res.add(i);
        }
        return res;
    }

    /** Pattern search with the Z-array of pattern + text. O(n + m) time and space. */
    static List<Integer> searchZ(String text, String pattern) {
        List<Integer> res = new ArrayList<>();
        int m = pattern.length();
        if (m == 0 || m > text.length()) return res;
        int[] z = zOptimal(pattern + text);
        for (int i = m; i < z.length; i++) {
            if (z[i] >= m) res.add(i - m);                 // the next m characters equal the pattern
        }
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyZ(String s, int[] expected) {
        check(Arrays.equals(zBrute(s), expected), "zBrute " + s);
        check(Arrays.equals(zOptimal(s), expected), "zOptimal " + s);
    }

    static void verifySearch(String text, String pattern, List<Integer> expected) {
        check(searchBrute(text, pattern).equals(expected), "searchBrute " + text + " / " + pattern);
        check(searchZ(text, pattern).equals(expected), "searchZ " + text + " / " + pattern);
    }

    public static void main(String[] args) {
        verifyZ("aabxaab", new int[]{0, 1, 0, 0, 3, 1, 0});
        verifyZ("aaaaa", new int[]{0, 4, 3, 2, 1});
        verifyZ("abacaba", new int[]{0, 0, 1, 0, 3, 0, 1});
        verifyZ("abcd", new int[]{0, 0, 0, 0});
        verifyZ("a", new int[]{0});
        verifyZ("", new int[]{});                                  // edge: empty string

        verifySearch("abracadabra", "abra", List.of(0, 7));
        verifySearch("aaaaa", "aa", List.of(0, 1, 2, 3));          // overlapping matches
        verifySearch("abc", "d", List.of());
        verifySearch("ab", "abc", List.of());                      // pattern longer than text
        verifySearch("a", "a", List.of(0));
        verifySearch("hello", "", List.of());                      // empty pattern: no matches by convention
        verifySearch("mississippi", "issi", List.of(1, 4));

        Random rnd = new Random(981);
        for (int trial = 0; trial < 500; trial++) {
            StringBuilder t = new StringBuilder(), p = new StringBuilder();
            int n = rnd.nextInt(30), m = 1 + rnd.nextInt(4);
            for (int i = 0; i < n; i++) t.append((char) ('a' + rnd.nextInt(2)));
            for (int i = 0; i < m; i++) p.append((char) ('a' + rnd.nextInt(2)));
            String ts = t.toString(), ps = p.toString();
            check(Arrays.equals(zBrute(ts), zOptimal(ts)), "random z " + ts);
            check(searchBrute(ts, ps).equals(searchZ(ts, ps)), "random search " + ts + " / " + ps);
        }
        int[] zBig = zOptimal("a".repeat(200_000));
        check(zBig[1] == 199_999 && zBig[199_999] == 1, "large all-equal input");
        System.out.println("OK P981_ZFunction");
    }
}
