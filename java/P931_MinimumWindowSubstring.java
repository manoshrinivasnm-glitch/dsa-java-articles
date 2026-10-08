import java.util.*;

/** TUF 931 - Minimum Window Substring. Shortest substring of s that contains every character of t (with multiplicity). */
public class P931_MinimumWindowSubstring {

    /** Approach 1: from every start, extend until all of t is covered; keep the shortest. O(n * (n + m)) time, O(1) space. */
    static String bruteForce(String s, String t) {
        int n = s.length(), m = t.length();
        if (m == 0 || m > n) return "";
        int bestLen = Integer.MAX_VALUE, bestStart = -1;
        for (int i = 0; i < n; i++) {
            int[] need = new int[256];
            for (int c = 0; c < m; c++) need[t.charAt(c)]++;
            int missing = m;                           // characters of t not yet covered by s[i..j]
            for (int j = i; j < n; j++) {
                if (need[s.charAt(j)] > 0) missing--;
                need[s.charAt(j)]--;
                if (missing == 0) {                    // shortest valid window that starts at i
                    if (j - i + 1 < bestLen) {
                        bestLen = j - i + 1;
                        bestStart = i;
                    }
                    break;
                }
            }
        }
        return bestStart == -1 ? "" : s.substring(bestStart, bestStart + bestLen);
    }

    /** Approach 2: expand right until the window covers t, then shrink left while it still does. O(n + m) time, O(1) space. */
    static String optimal(String s, String t) {
        int n = s.length(), m = t.length();
        if (m == 0 || m > n) return "";
        int[] need = new int[256];                     // need[c] > 0: still missing; < 0: surplus copies in the window
        for (int c = 0; c < m; c++) need[t.charAt(c)]++;
        int missing = m, l = 0, bestLen = Integer.MAX_VALUE, bestStart = -1;
        for (int r = 0; r < n; r++) {
            if (need[s.charAt(r)] > 0) missing--;
            need[s.charAt(r)]--;
            while (missing == 0) {                     // window s[l..r] covers t: record, then try to shrink
                if (r - l + 1 < bestLen) {
                    bestLen = r - l + 1;
                    bestStart = l;
                }
                need[s.charAt(l)]++;
                if (need[s.charAt(l)] > 0) missing++;  // we just dropped a required copy
                l++;
            }
        }
        return bestStart == -1 ? "" : s.substring(bestStart, bestStart + bestLen);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String t, String expected) {
        String tag = "s=\"" + s + "\" t=\"" + t + "\" expected \"" + expected + "\"";
        check(bruteForce(s, t).equals(expected), "bruteForce " + tag + " got \"" + bruteForce(s, t) + "\"");
        check(optimal(s, t).equals(expected), "optimal " + tag + " got \"" + optimal(s, t) + "\"");
    }

    public static void main(String[] args) {
        verify("ADOBECODEBANC", "ABC", "BANC");
        verify("a", "a", "a");
        verify("a", "aa", "");                         // t needs two a's, s has one
        verify("aa", "aa", "aa");                      // duplicates in t must be matched by duplicates in s
        verify("ab", "b", "b");
        verify("ddaaabbca", "abc", "bca");
        verify("cabwefgewcwaefgcf", "cae", "cwae");
        verify("", "a", "");                           // empty s
        System.out.println("OK P931_MinimumWindowSubstring");
    }
}
