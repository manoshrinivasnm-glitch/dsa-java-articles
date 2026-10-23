import java.util.*;

/** TUF 2394 - Count Number of Substrings. Count the substrings of a lowercase string that contain exactly k distinct characters. */
public class P2394_CountNumberOfSubstrings {

    /** Approach 1: enumerate every substring and count its distinct letters from scratch. O(n^3) time, O(1) space. */
    static long bruteForce(String s, int k) {
        int n = s.length();
        long count = 0;                                    // substrings with exactly k distinct letters
        for (int i = 0; i < n; i++) {                      // i = start of the substring
            for (int j = i; j < n; j++) {                  // j = end of the substring
                boolean[] seen = new boolean[26];          // which letters appear in s[i..j]
                int distinct = 0;
                for (int p = i; p <= j; p++) {             // recount s[i..j] from scratch
                    int c = s.charAt(p) - 'a';
                    if (!seen[c]) { seen[c] = true; distinct++; }   // first time this letter shows up
                }
                if (distinct == k) count++;                // this substring qualifies
            }
        }
        return count;
    }

    /** Approach 2: fix the start, extend the end one letter at a time and keep a running distinct count. O(n^2) time, O(1) space. */
    static long better(String s, int k) {
        int n = s.length();
        long count = 0;
        for (int i = 0; i < n; i++) {                      // fix the start i
            int[] freq = new int[26];                      // letter counts of the window s[i..j]
            int distinct = 0;
            for (int j = i; j < n; j++) {                  // grow the window one letter to the right
                int c = s.charAt(j) - 'a';
                if (freq[c]++ == 0) distinct++;            // a brand-new letter joins the window
                if (distinct == k) count++;                // s[i..j] has exactly k distinct letters
                else if (distinct > k) break;              // extending further can only add distinct letters
            }
        }
        return count;
    }

    /** Approach 3: exactly(k) = atMost(k) - atMost(k - 1), each computed with one sliding window. O(n) time, O(1) space. */
    static long optimal(String s, int k) {
        if (k <= 0) return 0;                              // cannot have zero or fewer distinct letters
        return atMost(s, k) - atMost(s, k - 1);            // at most k minus at most k-1 leaves exactly k
    }

    /** Number of substrings with at most k distinct characters. */
    private static long atMost(String s, int k) {
        if (k <= 0) return 0;                              // no non-empty substring has at most 0 letters
        int[] freq = new int[26];                          // letter counts inside the window [left, right]
        int distinct = 0;                                  // letters with a non-zero count
        long count = 0;
        for (int left = 0, right = 0; right < s.length(); right++) {   // extend the window to the right
            if (freq[s.charAt(right) - 'a']++ == 0) distinct++;   // adding s[right] may bring in a new letter
            while (distinct > k) {                         // shrink until the window is valid again
                if (--freq[s.charAt(left) - 'a'] == 0) distinct--;   // drop s[left]; maybe its last copy
                left++;
            }
            count += right - left + 1;                     // every substring ending at right that starts in [left, right]
        }
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int k, long expected) {
        check(bruteForce(s, k) == expected, "bruteForce(\"" + s + "\", " + k + ") != " + expected);
        check(better(s, k) == expected, "better(\"" + s + "\", " + k + ") != " + expected);
        check(optimal(s, k) == expected, "optimal(\"" + s + "\", " + k + ") != " + expected);
    }

    static String randomString(Random rnd, int n, int alphabet) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alphabet)));
        return sb.toString();
    }

    public static void main(String[] args) {
        verify("aba", 2, 3);                               // "ab", "ba", "aba"
        verify("abaaca", 1, 7);                            // "a", "b", "a", "a", "aa", "c", "a"
        verify("abc", 3, 1);
        verify("abc", 4, 0);                               // k larger than the number of distinct letters
        verify("aaaa", 1, 10);                             // every substring qualifies: n(n+1)/2
        verify("abcabc", 2, 5);
        verify("aab", 2, 2);
        verify("a", 1, 1);                                 // single character
        verify("a", 2, 0);
        verify("abc", 0, 0);                               // k = 0: no non-empty substring has zero distinct letters
        verify("abcd", 27, 0);                             // k beyond the alphabet
        Random rnd = new Random(42);
        for (int t = 0; t < 20; t++) {
            String s = randomString(rnd, 60, 4);
            for (int k = 0; k <= 5; k++) {
                long expected = bruteForce(s, k);
                check(better(s, k) == expected, "better on random " + s + " k=" + k);
                check(optimal(s, k) == expected, "optimal on random " + s + " k=" + k);
            }
        }
        String big = randomString(rnd, 5000, 26);
        check(optimal(big, 3) == better(big, 3), "optimal vs better on a 5000-char string");
        System.out.println("OK P2394_CountNumberOfSubstrings");
    }
}
