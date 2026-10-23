import java.util.*;

/** TUF 928 - Longest Substring With At Most K Distinct Characters. */
public class P928_LongestSubstringWithAtMostKDistinctChara {

    /** Approach 1: every start, extend while the substring has at most k distinct characters. O(n^2) time, O(1) space (256-entry table). */
    static int bruteForce(String s, int k) {
        int n = s.length(), best = 0;                         // best = longest valid substring so far
        for (int i = 0; i < n; i++) {                         // try every start index i
            int[] freq = new int[256];                        // character counts in s[i..j]
            int distinct = 0;                                 // distinct characters in s[i..j]
            for (int j = i; j < n; j++) {
                if (freq[s.charAt(j)]++ == 0) distinct++;     // first copy of this character
                if (distinct > k) break;                      // too many distinct: stop extending
                best = Math.max(best, j - i + 1);             // s[i..j] has at most k distinct characters
            }
        }
        return best;
    }

    /** Approach 2: sliding window that shrinks with a while loop until at most k distinct remain. O(2n) time, O(1) space. */
    static int better(String s, int k) {
        int[] freq = new int[256];                            // character counts inside the window s[l..r]
        int l = 0, distinct = 0, best = 0;                    // distinct = characters with a nonzero count
        for (int r = 0; r < s.length(); r++) {                // grow the window by one on the right
            if (freq[s.charAt(r)]++ == 0) distinct++;         // s[r] is a new character for the window
            while (distinct > k) {                            // too many distinct: shrink from the left
                if (--freq[s.charAt(l)] == 0) distinct--;     // last copy of s[l] left the window
                l++;
            }
            best = Math.max(best, r - l + 1);                 // the window is valid here
        }
        return best;
    }

    /** Approach 3: window that never shrinks; when invalid it slides right by one. O(n) time, O(1) space. */
    static int optimal(String s, int k) {
        int[] freq = new int[256];                            // character counts inside the window s[l..r]
        int l = 0, distinct = 0, best = 0;                    // distinct = characters with a nonzero count
        for (int r = 0; r < s.length(); r++) {                // grow the window by one on the right
            if (freq[s.charAt(r)]++ == 0) distinct++;         // s[r] is a new character for the window
            if (distinct > k) {                               // slide: drop one character from the left
                if (--freq[s.charAt(l)] == 0) distinct--;     // last copy of s[l] left the window
                l++;
            }
            if (distinct <= k) best = Math.max(best, r - l + 1);   // only a valid window may set a new best
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int k, int expected) {
        String tag = "\"" + s + "\" k=" + k + " expected " + expected;
        check(bruteForce(s, k) == expected, "bruteForce " + tag);
        check(better(s, k) == expected, "better " + tag);
        check(optimal(s, k) == expected, "optimal " + tag);
    }

    public static void main(String[] args) {
        verify("eceba", 2, 3);                        // "ece"
        verify("aa", 1, 2);
        verify("aaabbccd", 2, 5);                     // "aaabb"
        verify("abaccc", 2, 4);                       // "accc"
        verify("abcabc", 3, 6);                       // k covers every character
        verify("abc", 0, 0);                          // k = 0: only the empty substring
        verify("", 3, 0);                             // empty string
        System.out.println("OK P928_LongestSubstringWithAtMostKDistinctChara");
    }
}
