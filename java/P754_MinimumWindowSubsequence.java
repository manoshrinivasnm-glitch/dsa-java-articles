import java.util.*;

/** TUF 754 - Minimum Window Subsequence. Shortest substring of s1 that has s2 as a subsequence (leftmost on ties), or "". */
public class P754_MinimumWindowSubsequence {

    /** Approach 1: from every start that matches s2[0], match s2 greedily to the right. O(n^2) time, O(1) space. */
    static String bruteForce(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if (m == 0) return "";
        int bestStart = -1, bestLen = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (s1.charAt(i) != s2.charAt(0)) continue;
            int j = 0, p = i;
            while (p < n && j < m) {                   // greedy: take each s2 character as early as possible
                if (s1.charAt(p) == s2.charAt(j)) j++;
                p++;
            }
            if (j < m) break;                          // s2 cannot be finished from here, nor from any later start
            if (p - i < bestLen) {                     // window is s1[i..p-1]
                bestLen = p - i;
                bestStart = i;
            }
        }
        return bestStart == -1 ? "" : s1.substring(bestStart, bestStart + bestLen);
    }

    /** Approach 2: scan forward to the earliest end, then backward to the latest start, then restart after that start. O(n^2) worst case, O(1) space. */
    static String twoPointers(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if (m == 0) return "";
        int bestStart = -1, bestLen = Integer.MAX_VALUE;
        int i = 0;
        while (i < n) {
            int j = 0;
            while (i < n) {                            // forward: find where s2 is completed
                if (s1.charAt(i) == s2.charAt(j)) {
                    j++;
                    if (j == m) break;
                }
                i++;
            }
            if (i == n) break;                         // s2 cannot be completed any more
            int end = i;
            j = m - 1;
            while (j >= 0) {                           // backward: match s2 from its last character
                if (s1.charAt(i) == s2.charAt(j)) j--;
                i--;
            }
            int start = i + 1;                         // latest start whose window ends at 'end'
            if (end - start + 1 < bestLen) {
                bestLen = end - start + 1;
                bestStart = start;
            }
            i = start + 1;                             // next window must start strictly later
        }
        return bestStart == -1 ? "" : s1.substring(bestStart, bestStart + bestLen);
    }

    /** Approach 3: DP over s1 keeping, for each prefix of s2, the latest start index that can match it. O(n * m) time, O(m) space. */
    static String optimal(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if (m == 0) return "";
        int[] start = new int[m];                      // start[j]: latest p with s2[0..j] a subsequence of s1[p..i], or -1
        Arrays.fill(start, -1);
        int bestStart = -1, bestLen = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            char c = s1.charAt(i);
            for (int j = m - 1; j >= 0; j--) {         // right to left so start[j - 1] still refers to s1[..i-1]
                if (s2.charAt(j) == c) start[j] = (j == 0) ? i : start[j - 1];
            }
            if (start[m - 1] != -1 && i - start[m - 1] + 1 < bestLen) {
                bestLen = i - start[m - 1] + 1;
                bestStart = start[m - 1];
            }
        }
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
