import java.util.*;

/** TUF 927 - Longest Repeating Character Replacement. Longest substring that becomes one repeated letter after at most k replacements. */
public class P927_LongestRepeatingCharacterReplacement {

    /** Approach 1: every start, extend while (length - most frequent count) <= k. O(n^2) time, O(26) space. */
    static int bruteForce(String s, int k) {
        int n = s.length(), best = 0;
        for (int i = 0; i < n; i++) {
            int[] freq = new int[26];
            int maxFreq = 0;
            for (int j = i; j < n; j++) {
                maxFreq = Math.max(maxFreq, ++freq[s.charAt(j) - 'A']);
                int changes = (j - i + 1) - maxFreq;   // letters that differ from the majority letter
                if (changes > k) break;
                best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    /** Approach 2: shrinking window; after each left step recompute the true max frequency. O(26 n) time, O(26) space. */
    static int better(String s, int k) {
        int[] freq = new int[26];
        int l = 0, maxFreq = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            maxFreq = Math.max(maxFreq, ++freq[s.charAt(r) - 'A']);
            while ((r - l + 1) - maxFreq > k) {
                freq[s.charAt(l) - 'A']--;
                l++;
                maxFreq = 0;
                for (int f : freq) maxFreq = Math.max(maxFreq, f);
            }
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    /** Approach 3: never-shrinking window with a max frequency that is never decreased. O(n) time, O(26) space. */
    static int optimal(String s, int k) {
        int[] freq = new int[26];
        int l = 0, maxFreq = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            maxFreq = Math.max(maxFreq, ++freq[s.charAt(r) - 'A']);
            if ((r - l + 1) - maxFreq > k) {           // slide by one; maxFreq may now be stale, which is fine
                freq[s.charAt(l) - 'A']--;
                l++;
            }
            best = Math.max(best, r - l + 1);
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
