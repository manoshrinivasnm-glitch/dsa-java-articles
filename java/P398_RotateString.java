import java.util.*;

/** TUF 398 - Rotate String (LeetCode 796). Can s become goal after some number of left shifts? */
public class P398_RotateString {

    /** Approach 1: build every rotation and compare it with goal. O(n^2) time, O(n) space. */
    static boolean bruteForce(String s, String goal) {
        if (s.length() != goal.length()) return false;   // different lengths can never match
        if (s.isEmpty()) return true;                    // two empty strings: zero shifts work
        int n = s.length();
        for (int k = 0; k < n; k++) {                    // k = number of left shifts
            String rotated = s.substring(k) + s.substring(0, k);   // the first k characters move to the end
            if (rotated.equals(goal)) return true;       // found a number of shifts that works
        }
        return false;                                    // no rotation equals goal
    }

    /** Approach 2: every rotation of s is a substring of s + s. Uses the library search. O(n) space; the library search is fast in practice but not guaranteed linear. */
    static boolean better(String s, String goal) {
        return s.length() == goal.length() && (s + s).contains(goal);   // rotations sit inside s + s
    }

    /** Approach 3: same idea with a KMP search, which guarantees O(n) time. O(n) space for the doubled text and the LPS table. */
    static boolean optimal(String s, String goal) {
        if (s.length() != goal.length()) return false;   // different lengths can never match
        if (s.isEmpty()) return true;                    // two empty strings: zero shifts work
        String text = s + s;                             // every rotation of s appears in s + s
        int[] lps = buildLps(goal);                      // where to fall back after a mismatch
        int j = 0;                                       // number of characters of goal matched so far
        for (int i = 0; i < text.length(); i++) {        // scan the doubled text once
            while (j > 0 && text.charAt(i) != goal.charAt(j)) j = lps[j - 1];   // mismatch: fall back via lps
            if (text.charAt(i) == goal.charAt(j)) j++;   // one more character matched
            if (j == goal.length()) return true;         // all of goal matched: rotation found
        }
        return false;                                    // goal never appears in s + s
    }

    /** lps[i] = length of the longest proper prefix of p[0..i] that is also a suffix of it. */
    static int[] buildLps(String p) {
        int[] lps = new int[p.length()];                 // lps[0] stays 0
        int len = 0;                                     // length of the border matched so far
        for (int i = 1; i < p.length(); i++) {           // extend one character at a time
            while (len > 0 && p.charAt(i) != p.charAt(len)) len = lps[len - 1];   // mismatch: shorter border
            if (p.charAt(i) == p.charAt(len)) len++;     // the border grows by one
            lps[i] = len;                                // best border for p[0..i]
        }
        return lps;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String goal, boolean expected) {
        String in = "(\"" + s + "\", \"" + goal + "\")";
        check(bruteForce(s, goal) == expected, "bruteForce failed on " + in);
        check(better(s, goal) == expected, "better failed on " + in);
        check(optimal(s, goal) == expected, "optimal failed on " + in);
    }

    public static void main(String[] args) {
        verify("abcde", "cdeab", true);
        verify("abcde", "abced", false);                // same letters, not a rotation
        verify("", "", true);                           // empty strings
        verify("a", "a", true);
        verify("aa", "a", false);                       // different lengths
        verify("aaab", "abaa", true);                   // repeated letters: KMP fallback is exercised
        verify("abc", "abc", true);                     // zero shifts
        verify("abab", "baba", true);
        verify("abcd", "abdc", false);
        verify("aaaa", "aaaa", true);
        System.out.println("OK P398_RotateString");
    }
}
