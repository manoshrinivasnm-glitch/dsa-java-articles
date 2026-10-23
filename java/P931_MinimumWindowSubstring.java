import java.util.*;

/** TUF 931 - Minimum Window Substring. Shortest substring of s that contains every character of t (with multiplicity). */
public class P931_MinimumWindowSubstring {

    /** Approach 1: from every start, extend until all of t is covered; keep the shortest. O(n * (n + m)) time, O(1) space. */
    static String bruteForce(String s, String t) {
        int n = s.length(), m = t.length();
        if (m == 0 || m > n) return "";                   // empty t or t longer than s: no window
        int bestLen = Integer.MAX_VALUE, bestStart = -1;  // shortest window so far (start -1 = none yet)
        for (int i = 0; i < n; i++) {                     // try every start index i
            int[] need = new int[256];                    // need[c] = copies of c still required
            for (int c = 0; c < m; c++) need[t.charAt(c)]++;   // start by needing every character of t
            int missing = m;                              // characters of t not yet covered by s[i..j]
            for (int j = i; j < n; j++) {                 // extend the window s[i..j]
                if (need[s.charAt(j)] > 0) missing--;     // s[j] covers one still-missing character
                need[s.charAt(j)]--;                      // s[j] is used up (or is a surplus copy)
                if (missing == 0) {                       // shortest valid window that starts at i
                    if (j - i + 1 < bestLen) {            // shorter than the best so far
                        bestLen = j - i + 1;
                        bestStart = i;
                    }
                    break;                                // longer windows from i cannot be shorter
                }
            }
        }
        // "" when no window covers t
        return bestStart == -1 ? "" : s.substring(bestStart, bestStart + bestLen);
    }

    /** Approach 2: expand right until the window covers t, then shrink left while it still does. O(n + m) time, O(1) space. */
    static String optimal(String s, String t) {
        int n = s.length(), m = t.length();
        if (m == 0 || m > n) return "";                // empty t or t longer than s: no window
        int[] need = new int[256];                     // need[c] > 0: still missing; < 0: surplus copies in the window
        for (int c = 0; c < m; c++) need[t.charAt(c)]++;   // start by needing every character of t
        // missing = characters of t not yet in the window s[l..r]
        int missing = m, l = 0, bestLen = Integer.MAX_VALUE, bestStart = -1;
        for (int r = 0; r < n; r++) {                  // expand: bring s[r] into the window
            if (need[s.charAt(r)] > 0) missing--;      // s[r] covers one still-missing character
            need[s.charAt(r)]--;                       // s[r] is used up (or is a surplus copy)
            while (missing == 0) {                     // window s[l..r] covers t: record, then try to shrink
                if (r - l + 1 < bestLen) {             // new shortest window
                    bestLen = r - l + 1;
                    bestStart = l;
                }
                need[s.charAt(l)]++;                   // s[l] leaves the window
                if (need[s.charAt(l)] > 0) missing++;  // we just dropped a required copy
                l++;
            }
        }
        // "" when no window covers t
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
