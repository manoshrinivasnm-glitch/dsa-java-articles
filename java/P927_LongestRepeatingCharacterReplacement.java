import java.util.*;

/** TUF 927 - Longest Repeating Character Replacement. Longest substring that becomes one repeated letter after at most k replacements. */
public class P927_LongestRepeatingCharacterReplacement {

    /** Approach 1: every start, extend while (length - most frequent count) <= k. O(n^2) time, O(26) space. */
    static int bruteForce(String s, int k) {
        int n = s.length(), best = 0;                  // best = longest valid substring so far
        for (int i = 0; i < n; i++) {                  // try every start index i
            int[] freq = new int[26];                  // letter counts inside s[i..j]
            int maxFreq = 0;                           // count of the most common letter in s[i..j]
            for (int j = i; j < n; j++) {
                // add s[j]; it may become the majority letter
                maxFreq = Math.max(maxFreq, ++freq[s.charAt(j) - 'A']);
                int changes = (j - i + 1) - maxFreq;   // letters that differ from the majority letter
                if (changes > k) break;                // needs more than k replacements: stop extending
                best = Math.max(best, j - i + 1);      // s[i..j] can become one repeated letter
            }
        }
        return best;
    }

    /** Approach 2: shrinking window; after each left step recompute the true max frequency. O(26 n) time, O(26) space. */
    static int better(String s, int k) {
        int[] freq = new int[26];                      // letter counts inside the window s[l..r]
        int l = 0, maxFreq = 0, best = 0;              // maxFreq = count of the window's most common letter
        for (int r = 0; r < s.length(); r++) {         // grow the window by one on the right
            // add s[r]; it may become the majority letter
            maxFreq = Math.max(maxFreq, ++freq[s.charAt(r) - 'A']);
            while ((r - l + 1) - maxFreq > k) {        // needs more than k replacements: shrink
                freq[s.charAt(l) - 'A']--;             // s[l] leaves the window
                l++;
                maxFreq = 0;                           // recount the majority from scratch
                for (int f : freq) maxFreq = Math.max(maxFreq, f);
            }
            best = Math.max(best, r - l + 1);          // the window is valid here
        }
        return best;
    }

    /** Approach 3: never-shrinking window with a max frequency that is never decreased. O(n) time, O(26) space. */
    static int optimal(String s, int k) {
        int[] freq = new int[26];                      // letter counts inside the window s[l..r]
        int l = 0, maxFreq = 0, best = 0;              // maxFreq = highest letter count ever seen
        for (int r = 0; r < s.length(); r++) {         // grow the window by one on the right
            maxFreq = Math.max(maxFreq, ++freq[s.charAt(r) - 'A']);   // add s[r]; maxFreq never goes down
            if ((r - l + 1) - maxFreq > k) {           // slide by one; maxFreq may now be stale, which is fine
                freq[s.charAt(l) - 'A']--;             // s[l] leaves the window
                l++;
            }
            best = Math.max(best, r - l + 1);          // window size never drops, so it tracks the best
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
        verify("ABAB", 2, 4);
        verify("AABABBA", 1, 4);
        verify("AAAA", 0, 4);                         // already uniform
        verify("ABCDE", 1, 2);                        // all different: one change joins two letters
        verify("ABBB", 0, 3);                         // k = 0: longest run
        verify("BAAAB", 2, 5);                        // k covers every minority letter
        verify("A", 0, 1);
        verify("", 2, 0);                             // empty string
        System.out.println("OK P927_LongestRepeatingCharacterReplacement");
    }
}
