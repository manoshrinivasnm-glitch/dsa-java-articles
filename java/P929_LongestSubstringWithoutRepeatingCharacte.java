import java.util.*;

/** TUF 929 - Longest Substring Without Repeating Characters. Length of the longest substring whose characters are all distinct. */
public class P929_LongestSubstringWithoutRepeatingCharacte {

    /** Approach 1: from every start, extend until a character repeats. O(n^2) time, O(min(n, alphabet)) space. */
    static int bruteForce(String s) {
        int n = s.length(), best = 0;
        for (int i = 0; i < n; i++) {
            Set<Character> seen = new HashSet<>();
            for (int j = i; j < n; j++) {
                if (!seen.add(s.charAt(j))) break;   // s[j] already in s[i..j-1]: every longer substring repeats too
                best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    /** Approach 2: sliding window with a set, shrinking from the left one step at a time. O(n) time (at most 2n steps), O(alphabet) space. */
    static int better(String s) {
        Set<Character> window = new HashSet<>();
        int left = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            while (window.contains(c)) {             // shrink until the old copy of c has left the window
                window.remove(s.charAt(left));
                left++;
            }
            window.add(c);
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    /** Approach 3: remember each character's last index and jump the left edge directly. O(n) time (n steps), O(alphabet) space. */
    static int optimal(String s) {
        Map<Character, Integer> last = new HashMap<>();   // character -> index of its latest occurrence
        int left = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            Integer prev = last.get(c);
            if (prev != null && prev >= left) left = prev + 1;   // jump past the old copy; never move left backwards
            last.put(c, right);
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        int[] got = {bruteForce(s), better(s), optimal(s)};
        for (int g : got) check(g == expected, "\"" + s + "\": expected " + expected + " got " + Arrays.toString(got));
    }

    public static void main(String[] args) {
        verify("abcabcbb", 3);                       // "abc"
        verify("bbbbb", 1);
        verify("pwwkew", 3);                         // "wke"; "pwke" is a subsequence, not a substring
        verify("", 0);                               // empty string
        verify(" ", 1);                              // a space is a character too
        verify("abba", 2);                           // stale index of 'a' must not pull left backwards
        verify("dvdf", 3);                           // "vdf"
        verify("tmmzuxt", 5);                        // "mzuxt"
        verify("abcdef", 6);                         // no repeats at all
        verify("a b!a", 4);                          // " b!a"
        Random rnd = new Random(2024);               // seeded cross-check against brute force
        for (int t = 0; t < 300; t++) {
            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(20);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(5)));
            String s = sb.toString();
            verify(s, bruteForce(s));
        }
        System.out.println("OK P929_LongestSubstringWithoutRepeatingCharacte");
    }
}
