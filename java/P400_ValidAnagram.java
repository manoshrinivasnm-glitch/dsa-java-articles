import java.util.*;

/** TUF 400 - Valid Anagram (LeetCode 242). Is t a rearrangement of s, using every letter exactly as often? */
public class P400_ValidAnagram {

    /** Approach 1: for each letter of s, cross off one unused equal letter of t. O(n^2) time, O(n) space. */
    static boolean bruteForce(String s, String t) {
        if (s.length() != t.length()) return false;
        boolean[] used = new boolean[t.length()];
        for (int i = 0; i < s.length(); i++) {
            boolean found = false;
            for (int j = 0; j < t.length(); j++) {
                if (!used[j] && t.charAt(j) == s.charAt(i)) {
                    used[j] = true;                     // this letter of t is now spent
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    /** Approach 2: anagrams become the same string once their letters are sorted. O(n log n) time, O(n) space. */
    static boolean better(String s, String t) {
        if (s.length() != t.length()) return false;
        char[] a = s.toCharArray(), b = t.toCharArray();
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }

    /** Approach 3: one array of 26 counters, +1 for s and -1 for t; all must end at zero. O(n) time, O(1) space. */
    static boolean optimal(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;
            count[t.charAt(i) - 'a']--;
        }
        for (int c : count) {
            if (c != 0) return false;
        }
        return true;
    }

    /** Follow-up: arbitrary Unicode. Count code points (not chars) in a hash map. O(n) time, O(distinct symbols) space. */
    static boolean unicode(String s, String t) {
        Map<Integer, Integer> count = new HashMap<>();
        s.codePoints().forEach(cp -> count.merge(cp, 1, Integer::sum));
        for (int cp : t.codePoints().toArray()) {
            Integer c = count.get(cp);
            if (c == null) return false;                // t uses a symbol s has run out of
            if (c == 1) count.remove(cp);
            else count.put(cp, c - 1);
        }
        return count.isEmpty();                         // anything left over appears in s but not in t
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String t, boolean expected) {
        String in = "\"" + s + "\", \"" + t + "\"";
        check(bruteForce(s, t) == expected, "bruteForce failed for " + in);
        check(better(s, t) == expected, "better failed for " + in);
        check(optimal(s, t) == expected, "optimal failed for " + in);
        check(unicode(s, t) == expected, "unicode failed for " + in);
    }

    public static void main(String[] args) {
        verify("anagram", "nagaram", true);
        verify("rat", "car", false);
        verify("listen", "silent", true);
        verify("aacc", "ccac", false);              // same set of letters, different counts
        verify("a", "ab", false);                   // lengths differ
        verify("ab", "a", false);
        verify("abc", "abc", true);                 // a word is an anagram of itself
        verify("", "", true);                       // edge: two empty strings

        // Unicode follow-up: only the code-point method is meant for these.
        check(unicode("\u00E9t\u00E9", "t\u00E9\u00E9"), "accented letters");
        check(unicode("\uD83D\uDE00ab", "b\uD83D\uDE00a"), "emoji (surrogate pair) moved around");
        // Same multiset of UTF-16 chars, different code points: U+1F600 U+1F300 vs U+1F700 U+1F200.
        check(!unicode("\uD83D\uDE00\uD83C\uDF00", "\uD83D\uDF00\uD83C\uDE00"), "surrogate halves swapped between symbols");
        check(better("\uD83D\uDE00\uD83C\uDF00", "\uD83D\uDF00\uD83C\uDE00"), "char-level sorting is fooled by that pair");

        // Cross-check on seeded random lowercase pairs: half are true shuffles, half are random.
        Random rnd = new Random(400);
        for (int k = 0; k < 1000; k++) {
            int len = rnd.nextInt(10);
            char[] a = new char[len];
            for (int i = 0; i < len; i++) a[i] = (char) ('a' + rnd.nextInt(3));
            char[] b = a.clone();
            if (k % 2 == 0) {
                for (int i = len - 1; i > 0; i--) {     // Fisher-Yates shuffle with the seeded generator
                    int j = rnd.nextInt(i + 1);
                    char tmp = b[i]; b[i] = b[j]; b[j] = tmp;
                }
            } else {
                for (int i = 0; i < len; i++) b[i] = (char) ('a' + rnd.nextInt(3));
            }
            String s = new String(a), t = new String(b);
            verify(s, t, better(s, t));
        }
        System.out.println("OK P400_ValidAnagram");
    }
}
