import java.util.*;

/**
 * TUF 399 - Sort Characters by Frequency (LeetCode 451).
 * Rearrange s so that characters appear in decreasing order of frequency.
 * Ties are broken by the smaller character first so that every approach produces the same string.
 */
public class P399_SortCharactersByFrequency {

    /** Approach 1: count, then repeatedly pick the most frequent remaining character (selection sort on the counts). O(n + k^2) time, O(1) space for k <= 128 distinct ASCII characters. */
    static String bruteForce(String s) {
        int[] freq = new int[128];
        for (char c : s.toCharArray()) freq[c]++;
        StringBuilder out = new StringBuilder();
        while (true) {
            int bestChar = -1;
            for (int c = 0; c < 128; c++) {
                if (freq[c] > 0 && (bestChar == -1 || freq[c] > freq[bestChar])) bestChar = c;
            }
            if (bestChar == -1) break;                         // nothing left
            for (int k = 0; k < freq[bestChar]; k++) out.append((char) bestChar);
            freq[bestChar] = 0;
        }
        return out.toString();
    }

    /** Approach 2: count in a map, sort the distinct characters by frequency, then expand. O(n + k log k) time, O(k) space. */
    static String better(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
        List<Character> chars = new ArrayList<>(freq.keySet());
        chars.sort((a, b) -> {
            int fa = freq.get(a), fb = freq.get(b);
            if (fa != fb) return Integer.compare(fb, fa);      // higher frequency first
            return Character.compare(a, b);                    // tie: smaller character first
        });
        StringBuilder out = new StringBuilder();
        for (char c : chars) {
            for (int k = 0; k < freq.get(c); k++) out.append(c);
        }
        return out.toString();
    }

    /** Approach 3: bucket sort on the frequency, which is at most n. O(n + k) time, O(n + k) space. */
    static String optimal(String s) {
        int n = s.length();
        int[] freq = new int[128];
        for (char c : s.toCharArray()) freq[c]++;
        List<List<Character>> buckets = new ArrayList<>();      // buckets.get(f) = characters that occur exactly f times
        for (int f = 0; f <= n; f++) buckets.add(new ArrayList<>());
        for (int c = 0; c < 128; c++) {
            if (freq[c] > 0) buckets.get(freq[c]).add((char) c);
        }
        StringBuilder out = new StringBuilder();
        for (int f = n; f >= 1; f--) {
            for (char c : buckets.get(f)) {
                for (int k = 0; k < f; k++) out.append(c);
            }
        }
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String expected) {
        check(expected.equals(bruteForce(s)), "bruteForce failed on \"" + s + "\": " + bruteForce(s));
        check(expected.equals(better(s)), "better failed on \"" + s + "\": " + better(s));
        check(expected.equals(optimal(s)), "optimal failed on \"" + s + "\": " + optimal(s));
    }

    public static void main(String[] args) {
        verify("tree", "eert");                             // e twice, then r and t once each
        verify("cccaaa", "aaaccc");                         // equal counts: smaller character first
        verify("Aabb", "bbAa");                             // 'A' (65) sorts before 'a' (97)
        verify("", "");                                     // empty
        verify("a", "a");                                   // single character
        verify("abc", "abc");                               // all counts equal
        verify("loveleetcode", "eeeelloocdtv");
        verify("2a2b2", "222ab");                           // digits are ordinary characters
        System.out.println("OK P399_SortCharactersByFrequency");
    }
}
