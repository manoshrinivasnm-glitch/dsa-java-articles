import java.util.*;

/** TUF 2809 - Check if two strings are anagram of each other (LeetCode 242). Same multiset of characters? */
public class P2809_CheckIfTwoStringsAreAnagramOfEachOther {

    /** Approach 1: sort both strings and compare. O(n log n) time, O(n) space for the char arrays. */
    static boolean bruteForce(String s, String t) {
        if (s.length() != t.length()) return false;
        char[] a = s.toCharArray(), b = t.toCharArray();
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }

    /** Approach 2: count the characters of s in a map, then consume them with t. Works for any alphabet. O(n) time, O(k) space. */
    static boolean better(String s, String t) {
        if (s.length() != t.length()) return false;
        Map<Character, Integer> count = new HashMap<>();
        for (char c : s.toCharArray()) count.merge(c, 1, Integer::sum);
        for (char c : t.toCharArray()) {
            Integer left = count.get(c);
            if (left == null) return false;                    // t has a character s does not have (any more)
            if (left == 1) count.remove(c); else count.put(c, left - 1);
        }
        return count.isEmpty();
    }

    /** Approach 3: one frequency array for lowercase letters: +1 for s, -1 for t, all entries must end at 0. O(n) time, O(1) space. */
    static boolean optimal(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] freq = new int[26];
        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i) - 'a']++;
            freq[t.charAt(i) - 'a']--;
        }
        for (int f : freq) if (f != 0) return false;
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String t, boolean expected) {
        String in = "(\"" + s + "\", \"" + t + "\")";
        check(bruteForce(s, t) == expected, "bruteForce failed on " + in);
        check(better(s, t) == expected, "better failed on " + in);
        check(optimal(s, t) == expected, "optimal failed on " + in);
    }

    public static void main(String[] args) {
        verify("anagram", "nagaram", true);
        verify("rat", "car", false);
        verify("", "", true);                           // empty strings are anagrams of each other
        verify("a", "ab", false);                       // different lengths
        verify("aacc", "ccac", false);                  // same letters, different counts
        verify("listen", "silent", true);
        verify("abc", "cba", true);
        verify("aab", "abb", false);
        System.out.println("OK P2809_CheckIfTwoStringsAreAnagramOfEachOther");
    }
}
