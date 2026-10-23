import java.util.*;

/** TUF 754 - Minimum Window Subsequence. Shortest substring of s1 that has s2 as a subsequence (leftmost on ties), or "". */
public class P754_MinimumWindowSubsequence {

    /** Approach 1: from every start that matches s2[0], match s2 greedily to the right. O(n^2) time, O(1) space. */
    static String bruteForce(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if (m == 0) return "";                         // empty s2: nothing to match
        int bestStart = -1, bestLen = Integer.MAX_VALUE;   // best window so far (start -1 = none yet)
        for (int i = 0; i < n; i++) {                  // try every start index i
            if (s1.charAt(i) != s2.charAt(0)) continue;   // a shortest window always starts with s2[0]
            int j = 0, p = i;                          // j walks s2, p walks s1 from i
            while (p < n && j < m) {                   // greedy: take each s2 character as early as possible
                if (s1.charAt(p) == s2.charAt(j)) j++;   // matched s2[j]; move on to the next one
                p++;
            }
            if (j < m) break;                          // s2 cannot be finished from here, nor from any later start
            if (p - i < bestLen) {                     // window is s1[i..p-1]
                bestLen = p - i;
                bestStart = i;
            }
        }
        // "" when no window exists
        return bestStart == -1 ? "" : s1.substring(bestStart, bestStart + bestLen);
    }

    /** Approach 2: scan forward to the earliest end, then backward to the latest start, then restart after that start. O(n^2) worst case, O(1) space. */
    static String twoPointers(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if (m == 0) return "";                            // empty s2: nothing to match
        int bestStart = -1, bestLen = Integer.MAX_VALUE;  // best window so far (start -1 = none yet)
        int i = 0;                                        // i scans s1; each round finds one window
        while (i < n) {
            int j = 0;                                    // j = index in s2 to match next
            while (i < n) {                               // forward: find where s2 is completed
                if (s1.charAt(i) == s2.charAt(j)) {
                    j++;
                    if (j == m) break;                    // s1[i] completes s2: the window ends at i
                }
                i++;
            }
            if (i == n) break;                            // s2 cannot be completed any more
            int end = i;                                  // right end of this candidate window
            j = m - 1;                                    // begin with the last character of s2
            while (j >= 0) {                              // backward: match s2 from its last character
                if (s1.charAt(i) == s2.charAt(j)) j--;    // matched s2[j]; step back to s2[j - 1]
                i--;
            }
            int start = i + 1;                            // latest start whose window ends at 'end'
            if (end - start + 1 < bestLen) {              // shorter window found
                bestLen = end - start + 1;
                bestStart = start;
            }
            i = start + 1;                                // next window must start strictly later
        }
        // "" when no window exists
        return bestStart == -1 ? "" : s1.substring(bestStart, bestStart + bestLen);
    }

    /** Approach 3: DP over s1 keeping, for each prefix of s2, the latest start index that can match it. O(n * m) time, O(m) space. */
    static String optimal(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if (m == 0) return "";                         // empty s2: nothing to match
        int[] start = new int[m];                      // start[j]: latest p with s2[0..j] a subsequence of s1[p..i], or -1
        Arrays.fill(start, -1);                        // -1: s2[0..j] not matched yet
        int bestStart = -1, bestLen = Integer.MAX_VALUE;   // best window so far (start -1 = none yet)
        for (int i = 0; i < n; i++) {                  // extend s1 one character at a time
            char c = s1.charAt(i);
            for (int j = m - 1; j >= 0; j--) {         // right to left so start[j - 1] still refers to s1[..i-1]
                // s1[i] extends the match of s2[0..j-1], or starts a new match of s2[0] at i
                if (s2.charAt(j) == c) start[j] = (j == 0) ? i : start[j - 1];
            }
            // all of s2 fits in s1[start[m - 1]..i]: keep it if shorter
            if (start[m - 1] != -1 && i - start[m - 1] + 1 < bestLen) {
                bestLen = i - start[m - 1] + 1;
                bestStart = start[m - 1];
            }
        }
        // "" when no window exists
        return bestStart == -1 ? "" : s1.substring(bestStart, bestStart + bestLen);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s1, String s2, String expected) {
        String tag = "s1=\"" + s1 + "\" s2=\"" + s2 + "\" expected \"" + expected + "\"";
        check(bruteForce(s1, s2).equals(expected), "bruteForce " + tag + " got \"" + bruteForce(s1, s2) + "\"");
        check(twoPointers(s1, s2).equals(expected), "twoPointers " + tag + " got \"" + twoPointers(s1, s2) + "\"");
        check(optimal(s1, s2).equals(expected), "optimal " + tag + " got \"" + optimal(s1, s2) + "\"");
    }

    public static void main(String[] args) {
        verify("abcdebdde", "bde", "bcde");            // "bdde" has the same length but starts later
        verify("jmeqksfrsdcmsiwvaovztaqenprpvnbstl", "u", "");   // no 'u' at all
        verify("geeksforgeeks", "eksrg", "eksforg");
        verify("abcd", "abcd", "abcd");                // the whole string
        verify("cnhczmccqouqadqtmjjzl", "mm", "mccqouqadqtm");
        verify("fgrqsqsnodwmxzkzxwqegkndaa", "fnok", "fgrqsqsnodwmxzk");
        verify("a", "aa", "");                         // s2 longer than s1
        System.out.println("OK P754_MinimumWindowSubsequence");
    }
}
