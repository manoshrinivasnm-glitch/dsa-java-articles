import java.util.*;

/**
 * TUF 977 - KMP Algorithm or LPS array.
 * lps[i] = length of the longest proper prefix of s[0..i] that is also a suffix of s[0..i].
 * Use it to return every 0-based index where pattern occurs in text.
 */
public class P977_KMPAlgorithmOrLPSArray {

    /** LPS straight from the definition: try every border length from longest down. O(n^3) worst case. */
    static int[] lpsBrute(String s) {
        int n = s.length();
        int[] lps = new int[n];
        for (int i = 0; i < n; i++) {
            for (int len = i; len >= 1; len--) {           // proper: at most i for the prefix s[0..i] of length i + 1
                if (s.regionMatches(0, s, i - len + 1, len)) {
                    lps[i] = len;
                    break;
                }
            }
        }
        return lps;
    }

    /** LPS in O(n): extend the previous border, or fall back to the next shorter border. */
    static int[] lpsOptimal(String s) {
        int n = s.length();
        int[] lps = new int[n];
        int len = 0;                                       // length of the border of s[0..i-1]
        for (int i = 1; i < n; i++) {
            while (len > 0 && s.charAt(i) != s.charAt(len)) len = lps[len - 1];
            if (s.charAt(i) == s.charAt(len)) len++;
            lps[i] = len;
        }
        return lps;
    }

    /** Pattern search, brute force: restart the comparison at every window. O(n * m) time. */
    static List<Integer> searchBrute(String text, String pattern) {
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

    /** Pattern search with KMP: the text pointer never moves back. O(n + m) time, O(m) space. */
    static List<Integer> searchKmp(String text, String pattern) {
        List<Integer> res = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0 || m > n) return res;
        int[] lps = lpsOptimal(pattern);
        int j = 0;                                         // number of pattern characters matched so far
        for (int i = 0; i < n; i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) j = lps[j - 1];
            if (text.charAt(i) == pattern.charAt(j)) j++;
            if (j == m) {
                res.add(i - m + 1);
                j = lps[m - 1];                            // keep the border so overlapping matches are found
            }
        }
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyLps(String s, int[] expected) {
        check(Arrays.equals(lpsBrute(s), expected), "lpsBrute " + s);
        check(Arrays.equals(lpsOptimal(s), expected), "lpsOptimal " + s);
    }

    static void verifySearch(String text, String pattern, List<Integer> expected) {
        check(searchBrute(text, pattern).equals(expected), "searchBrute " + text + " / " + pattern);
        check(searchKmp(text, pattern).equals(expected), "searchKmp " + text + " / " + pattern);
    }

    public static void main(String[] args) {
        verifyLps("abab", new int[]{0, 0, 1, 2});
        verifyLps("aaaa", new int[]{0, 1, 2, 3});
        verifyLps("aabaaab", new int[]{0, 1, 0, 1, 2, 2, 3});
        verifyLps("abcabcd", new int[]{0, 0, 0, 1, 2, 3, 0});
        verifyLps("abacabab", new int[]{0, 0, 1, 0, 1, 2, 3, 2});
        verifyLps("a", new int[]{0});
        verifyLps("", new int[]{});                                // edge: empty string

        verifySearch("abracadabra", "abra", List.of(0, 7));
        verifySearch("aaaaa", "aa", List.of(0, 1, 2, 3));          // overlapping matches
        verifySearch("ababcabcabababd", "ababd", List.of(10));
        verifySearch("abc", "d", List.of());
        verifySearch("ab", "abc", List.of());                      // pattern longer than text
        verifySearch("hello", "", List.of());                      // empty pattern: no matches by convention
        verifySearch("mississippi", "issi", List.of(1, 4));

        Random rnd = new Random(977);
        for (int trial = 0; trial < 500; trial++) {
            StringBuilder t = new StringBuilder(), p = new StringBuilder();
            int n = rnd.nextInt(30), m = 1 + rnd.nextInt(5);
            for (int i = 0; i < n; i++) t.append((char) ('a' + rnd.nextInt(2)));
            for (int i = 0; i < m; i++) p.append((char) ('a' + rnd.nextInt(2)));
            String ts = t.toString(), ps = p.toString();
            check(Arrays.equals(lpsBrute(ts), lpsOptimal(ts)), "random lps " + ts);
            check(searchBrute(ts, ps).equals(searchKmp(ts, ps)), "random search " + ts + " / " + ps);
        }
        String text = "a".repeat(200_000);
        check(searchKmp(text, "a".repeat(1000)).size() == 199_001, "large overlapping input");
        System.out.println("OK P977_KMPAlgorithmOrLPSArray");
    }
}
