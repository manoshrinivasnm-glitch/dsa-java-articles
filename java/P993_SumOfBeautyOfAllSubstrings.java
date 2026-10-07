import java.util.*;

/** TUF 993 - Sum of Beauty of All Substrings. Beauty = (highest letter frequency) - (lowest non-zero letter frequency); sum it over every substring. */
public class P993_SumOfBeautyOfAllSubstrings {

    /** Approach 1: for every substring count its letters from scratch and take max - min. O(n^3) time, O(1) space. */
    static long bruteForce(String s) {
        int n = s.length();
        long total = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                int[] freq = new int[26];
                for (int p = i; p <= j; p++) freq[s.charAt(p) - 'a']++;
                total += beauty(freq);
            }
        }
        return total;
    }

    /** max - min over the non-zero entries of a 26-letter frequency table (0 for an empty table). */
    private static int beauty(int[] freq) {
        int max = 0, min = Integer.MAX_VALUE;
        for (int f : freq) {
            if (f == 0) continue;
            if (f > max) max = f;
            if (f < min) min = f;
        }
        return max == 0 ? 0 : max - min;
    }

    /** Approach 2: fix the start, extend the end one letter at a time, keep the table and rescan its 26 slots. O(26 n^2) time, O(1) space. */
    static long better(String s) {
        int n = s.length();
        long total = 0;
        for (int i = 0; i < n; i++) {
            int[] freq = new int[26];
            for (int j = i; j < n; j++) {
                freq[s.charAt(j) - 'a']++;
                total += beauty(freq);
            }
        }
        return total;
    }

    /** Approach 3: also track how many letters have each frequency, so max and min update in O(1) per step. O(n^2) time, O(n) space. */
    static long optimal(String s) {
        int n = s.length();
        long total = 0;
        for (int i = 0; i < n; i++) {
            int[] freq = new int[26];
            int[] lettersWith = new int[n + 2];            // lettersWith[f] = how many letters occur exactly f times
            int maxF = 0, minF = 0;
            for (int j = i; j < n; j++) {
                int c = s.charAt(j) - 'a';
                int f = freq[c];                           // old frequency of this letter
                if (f > 0) lettersWith[f]--;
                freq[c] = f + 1;
                lettersWith[f + 1]++;
                if (f + 1 > maxF) maxF = f + 1;
                if (f == 0) minF = 1;                      // a letter seen for the first time is the rarest
                else if (f == minF && lettersWith[f] == 0) minF = f + 1;
                total += maxF - minF;
            }
        }
        return total;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, long expected) {
        check(bruteForce(s) == expected, "bruteForce(\"" + s + "\") = " + bruteForce(s) + " != " + expected);
        check(better(s) == expected, "better(\"" + s + "\") = " + better(s) + " != " + expected);
        check(optimal(s) == expected, "optimal(\"" + s + "\") = " + optimal(s) + " != " + expected);
    }

    static String randomString(Random rnd, int n, int alphabet) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alphabet)));
        return sb.toString();
    }

    public static void main(String[] args) {
        verify("aabcb", 5);                                // "aab", "aabc", "aabcb", "abcb", "bcb" each contribute 1
        verify("aabcbaa", 17);
        verify("a", 0);                                    // one letter: max == min
        verify("", 0);                                     // no substrings at all
        verify("ab", 0);                                   // every substring has all frequencies equal
        verify("aab", 1);                                  // only "aab" (a=2, b=1) has beauty
        verify("aaaa", 0);                                 // a single distinct letter never has beauty
        verify("zzzy", 3);                                 // "zzy" -> 1, "zzzy" -> 2
        verify("abcabc", 5);
        Random rnd = new Random(2024);
        for (int t = 0; t < 150; t++) {
            String s = randomString(rnd, 1 + rnd.nextInt(40), 2 + rnd.nextInt(4));
            long expected = bruteForce(s);
            check(better(s) == expected, "better on random " + s);
            check(optimal(s) == expected, "optimal on random " + s);
        }
        String big = randomString(rnd, 500, 26);           // the largest input the problem allows
        check(optimal(big) == better(big), "optimal vs better on a 500-letter string");
        System.out.println("OK P993_SumOfBeautyOfAllSubstrings");
    }
}
