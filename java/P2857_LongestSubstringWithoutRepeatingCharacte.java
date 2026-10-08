import java.util.*;

/** TUF 2857 - Longest Substring Without Repeating Characters. Length of the longest substring with all distinct characters. */
public class P2857_LongestSubstringWithoutRepeatingCharacte {

    /** Approach 1: grow a substring from every start until a character repeats. O(n^2) time, O(1) space (256-entry table). */
    static int bruteForce(String s) {
        int n = s.length(), best = 0;
        for (int i = 0; i < n; i++) {
            boolean[] seen = new boolean[256];
            for (int j = i; j < n; j++) {
                char c = s.charAt(j);
                if (seen[c]) break;                    // s[i..j] would contain c twice
                seen[c] = true;
                best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    /** Approach 2: sliding window with a set; shrink from the left one step at a time. O(2n) time, O(min(n, alphabet)) space. */
    static int better(String s) {
        Set<Character> window = new HashSet<>();
        int l = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            while (window.contains(c)) {               // drop characters until the old copy of c is gone
                window.remove(s.charAt(l));
                l++;
            }
            window.add(c);
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    /** Approach 3: remember the last index of every character and jump the left edge past it. O(n) time, O(1) space. */
    static int optimal(String s) {
        int[] last = new int[256];
        Arrays.fill(last, -1);
        int l = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            if (last[c] >= l) l = last[c] + 1;        // previous copy is inside the window: jump past it
            last[c] = r;
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        check(bruteForce(s) == expected, "bruteForce \"" + s + "\" expected " + expected + " got " + bruteForce(s));
        check(better(s) == expected, "better \"" + s + "\" expected " + expected + " got " + better(s));
        check(optimal(s) == expected, "optimal \"" + s + "\" expected " + expected + " got " + optimal(s));
    }

    public static void main(String[] args) {
        verify("abcabcbb", 3);
        verify("bbbbb", 1);
        verify("pwwkew", 3);                // answer is "wke"; "pwke" is a subsequence, not a substring
        verify("", 0);                      // empty string
        verify(" ", 1);                     // a space is a character too
        verify("abba", 2);                  // last['a'] = 0 is left of the window when the second 'a' arrives
        verify("cadbzabcd", 5);
        verify("dvdf", 3);
        System.out.println("OK P2857_LongestSubstringWithoutRepeatingCharacte");
    }
}
