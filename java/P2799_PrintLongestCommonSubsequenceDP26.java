import java.util.*;

/** TUF 2799 - Print Longest Common Subsequence (DP 26). Return one longest common subsequence of two strings ("" if none). */
public class P2799_PrintLongestCommonSubsequenceDP26 {

    /** Approach 1: recursion that returns the subsequence itself. Exponential time, O(n + m) stack. */
    static String bruteForce(String s1, String s2) {
        return build(s1.length(), s2.length(), s1, s2);  // start from the two full strings
    }

    /** One LCS of the prefixes s1[0..i) and s2[0..j). Ties go to the "drop from s2" branch. */
    static String build(int i, int j, String s1, String s2) {
        if (i == 0 || j == 0) return "";                 // an empty prefix shares nothing
        // last characters match: solve the shorter prefixes, then append this character
        if (s1.charAt(i - 1) == s2.charAt(j - 1)) return build(i - 1, j - 1, s1, s2) + s1.charAt(i - 1);
        String up = build(i - 1, j, s1, s2);             // drop s1's last character
        String left = build(i, j - 1, s1, s2);           // drop s2's last character
        return up.length() > left.length() ? up : left;  // keep the longer one; a tie goes to left
    }

    /** Approach 2: build the LCS length table, then walk back from dp[n][m]. O(n * m) time and space. */
    static String tabulation(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];              // dp[i][j] = LCS length of s1[0..i) and s2[0..j)
        for (int i = 1; i <= n; i++) {                   // fill the length table row by row
            for (int j = 1; j <= m; j++) {
                // last characters match: extend the diagonal
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) dp[i][j] = 1 + dp[i - 1][j - 1];
                // drop one char from s1 (up) or s2 (left)
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        char[] out = new char[dp[n][m]];                 // one slot per LCS character
        int k = out.length - 1, i = n, j = m;            // fill out from the back, starting at dp[n][m]
        while (i > 0 && j > 0) {                         // walk back until one prefix is empty
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {  // this character is part of the LCS
                out[k--] = s1.charAt(i - 1);             // record it; we meet the LCS in reverse
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {    // follow the larger neighbour
                i--;                                     // the answer came from the cell above
            } else {
                j--;                                     // the answer came from the cell on the left
            }
        }
        return new String(out);
    }

    /** Approach 3: Hirschberg's divide and conquer. O(n * m) time, O(n + m) extra space. */
    static String linearSpace(String s1, String s2) {
        StringBuilder out = new StringBuilder();             // collects the LCS characters left to right
        split(s1, 0, s1.length(), s2, 0, s2.length(), out);  // solve the whole of both strings
        return out.toString();
    }

    /** Appends one LCS of a[aLo..aHi) and b[bLo..bHi) to out. */
    static void split(String a, int aLo, int aHi, String b, int bLo, int bHi, StringBuilder out) {
        if (aLo >= aHi || bLo >= bHi) return;   // an empty range has no common subsequence
        if (aHi - aLo == 1) {                   // one character of a: it is the LCS if b contains it
            for (int k = bLo; k < bHi; k++) {   // scan b's range for that character
                if (b.charAt(k) == a.charAt(aLo)) {
                    out.append(a.charAt(aLo));  // one match is enough
                    return;
                }
            }
            return;                             // the character never appears in b's range
        }
        int mid = (aLo + aHi) / 2;              // split a's range in half
        int cut = bestCut(a, aLo, mid, aHi, b, bLo, bHi);  // where an optimal path crosses the middle row
        split(a, aLo, mid, b, bLo, cut, out);   // LCS = (LCS of the top half) + (LCS of the bottom half)
        split(a, mid, aHi, b, cut, bHi, out);   // then the bottom half on the rest of b
    }

    /** Where an optimal alignment crosses from a[aLo..mid) into a[mid..aHi): the split point in b. */
    static int bestCut(String a, int aLo, int mid, int aHi, String b, int bLo, int bHi) {
        int[] top = forwardRow(a, aLo, mid, b, bLo, bHi);       // top[k] = LCS(a[aLo..mid), first k chars of b)
        int[] bottom = backwardRow(a, mid, aHi, b, bLo, bHi);   // bottom[k] = LCS(a[mid..aHi), last k chars of b)
        int w = bHi - bLo, best = 0;    // w = width of b's range, best = chosen split
        for (int k = 1; k <= w; k++) {  // try every split: first k chars of b go to the top half
            // keep the split with the largest combined LCS
            if (top[k] + bottom[w - k] > top[best] + bottom[w - best]) best = k;
        }
        return bLo + best;              // convert back to an index in b
    }

    /** Last row of the LCS table of a[aLo..aHi) against prefixes of b[bLo..bHi), using two rows. */
    static int[] forwardRow(String a, int aLo, int aHi, String b, int bLo, int bHi) {
        int w = bHi - bLo;
        int[] prev = new int[w + 1], cur = new int[w + 1];    // prev = row i - 1, cur = row i being filled
        for (int i = aLo; i < aHi; i++) {                     // one row per character of a's range
            for (int k = 1; k <= w; k++) {                    // k = how many chars of b's range are used
                if (a.charAt(i) == b.charAt(bLo + k - 1)) cur[k] = 1 + prev[k - 1];  // match: diagonal + 1
                else cur[k] = Math.max(prev[k], cur[k - 1]);  // else best of up and left
            }
            int[] tmp = prev; prev = cur; cur = tmp;          // the row just built becomes "previous"
        }
        return prev;                                          // after the last swap prev holds the final row
    }

    /** The same scan run from the right ends: row[k] = LCS(a[aLo..aHi), last k chars of b[bLo..bHi)). */
    static int[] backwardRow(String a, int aLo, int aHi, String b, int bLo, int bHi) {
        int w = bHi - bLo;
        int[] prev = new int[w + 1], cur = new int[w + 1];
        for (int i = aHi - 1; i >= aLo; i--) {        // same table, filling from a's right end
            for (int k = 1; k <= w; k++) {            // k = how many chars from b's right end are used
                // match: kth char of b counted from the right
                if (a.charAt(i) == b.charAt(bHi - k)) cur[k] = 1 + prev[k - 1];
                else cur[k] = Math.max(prev[k], cur[k - 1]);  // else best of up and left
            }
            int[] tmp = prev; prev = cur; cur = tmp;  // the row just built becomes "previous"
        }
        return prev;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static boolean isSubsequence(String sub, String s) {
        int k = 0;
        for (int i = 0; i < s.length() && k < sub.length(); i++) if (s.charAt(i) == sub.charAt(k)) k++;
        return k == sub.length();
    }

    /** Every approach must return a common subsequence of the expected length; exact is non-null when the LCS is unique. */
    static void verify(String s1, String s2, int expectedLength, String exact) {
        String in = "\"" + s1 + "\", \"" + s2 + "\"";
        String[] results = {bruteForce(s1, s2), tabulation(s1, s2), linearSpace(s1, s2)};
        String[] names = {"bruteForce", "tabulation", "linearSpace"};
        for (int r = 0; r < results.length; r++) {
            String got = results[r];
            check(got.length() == expectedLength, names[r] + " wrong length for " + in + ": " + got);
            check(isSubsequence(got, s1) && isSubsequence(got, s2), names[r] + " not common for " + in + ": " + got);
            if (exact != null) check(got.equals(exact), names[r] + " expected " + exact + " for " + in + ": " + got);
        }
        check(results[0].equals(results[1]), "bruteForce and tabulation use the same tie-break for " + in);
    }

    public static void main(String[] args) {
        verify("abcde", "ace", 3, "ace");
        verify("AGGTAB", "GXTXAYB", 4, "GTAB");
        verify("abaaa", "baabaca", 4, null);         // several answers, e.g. "baaa" and "abaa"
        verify("abc", "def", 0, "");                 // nothing in common
        verify("", "abc", 0, "");                    // edge: empty string
        verify("x", "x", 1, "x");                    // edge: single matching character
        verify("abcd", "dcba", 1, null);             // any single character is an answer

        // Cross-check on seeded random strings: every result must be a valid LCS of the right length.
        Random rnd = new Random(2799);
        for (int t = 0; t < 300; t++) {
            String a = randomString(rnd, rnd.nextInt(10)), b = randomString(rnd, rnd.nextInt(10));
            verify(a, b, lcsLength(a, b), null);
        }
        // A longer pair for the two polynomial methods only.
        String a = randomString(rnd, 400), b = randomString(rnd, 300);
        int len = lcsLength(a, b);
        for (String got : new String[]{tabulation(a, b), linearSpace(a, b)}) {
            check(got.length() == len && isSubsequence(got, a) && isSubsequence(got, b), "long input failed");
        }
        System.out.println("OK P2799_PrintLongestCommonSubsequenceDP26");
    }

    static int lcsLength(String a, String b) {
        return forwardRow(a, 0, a.length(), b, 0, b.length())[b.length()];
    }

    static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
        return sb.toString();
    }
}
