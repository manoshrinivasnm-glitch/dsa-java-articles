import java.util.*;

/** TUF 393 - Isomorphic String (LeetCode 205). Can the characters of s be replaced, one-to-one, to obtain t? */
public class P393_IsomorphicString {

    /** Approach 1: compare every pair of positions. Positions equal in s must be equal in t and vice versa. O(n^2) time, O(1) space. */
    static boolean bruteForce(String s, String t) {
        if (s.length() != t.length()) return false;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                boolean sameInS = s.charAt(i) == s.charAt(j);
                boolean sameInT = t.charAt(i) == t.charAt(j);
                if (sameInS != sameInT) return false;
            }
        }
        return true;
    }

    /** Approach 2: two hash maps, s -> t and t -> s, checked at every position. O(n) time, O(k) space for k distinct characters. */
    static boolean better(String s, String t) {
        if (s.length() != t.length()) return false;
        Map<Character, Character> forward = new HashMap<>();
        Map<Character, Character> backward = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char a = s.charAt(i), b = t.charAt(i);
            Character mappedA = forward.get(a);
            if (mappedA == null) {
                if (backward.containsKey(b)) return false;   // b is already the image of another character
                forward.put(a, b);
                backward.put(b, a);
            } else if (mappedA != b) {
                return false;                                  // a was mapped to a different character earlier
            }
        }
        return true;
    }

    /** Approach 3: remember the last position of each character in both strings; they must always agree. O(n) time, O(1) space (two fixed arrays). */
    static boolean optimal(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] lastS = new int[256], lastT = new int[256];     // last position + 1; 0 means not seen yet
        for (int i = 0; i < s.length(); i++) {
            int a = s.charAt(i), b = t.charAt(i);
            if (lastS[a] != lastT[b]) return false;
            lastS[a] = i + 1;
            lastT[b] = i + 1;
        }
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
        verify("egg", "add", true);
        verify("foo", "bar", false);                    // o would have to map to both a and r
        verify("paper", "title", true);
        verify("badc", "baba", false);                  // d and b would both map to b
        verify("", "", true);                           // empty strings
        verify("ab", "aa", false);                      // two different characters cannot share an image
        verify("a", "a", true);
        verify("ab", "a", false);                       // different lengths
        verify("abcabc", "xyzxyz", true);
        System.out.println("OK P393_IsomorphicString");
    }
}
