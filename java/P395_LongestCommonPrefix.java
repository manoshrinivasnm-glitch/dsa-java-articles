import java.util.*;

/** TUF 395 - Longest Common Prefix (LeetCode 14). Longest string that every input string starts with. */
public class P395_LongestCommonPrefix {

    /** Approach 1: try every prefix length from the longest possible down to 1. O(n * m^2) time, O(m) space, m = shortest string. */
    static String bruteForce(String[] strs) {
        if (strs.length == 0) return "";
        int minLen = Integer.MAX_VALUE;
        for (String s : strs) minLen = Math.min(minLen, s.length());
        for (int len = minLen; len >= 1; len--) {
            String candidate = strs[0].substring(0, len);
            boolean all = true;
            for (String s : strs) {
                if (!s.startsWith(candidate)) { all = false; break; }
            }
            if (all) return candidate;
        }
        return "";
    }

    /** Approach 2: sort; the common prefix of all strings is the common prefix of the first and the last. O(n * m log n) time, O(n) space. */
    static String better(String[] strs) {
        if (strs.length == 0) return "";
        String[] sorted = strs.clone();
        Arrays.sort(sorted);
        String first = sorted[0], last = sorted[sorted.length - 1];
        int i = 0;
        while (i < first.length() && i < last.length() && first.charAt(i) == last.charAt(i)) i++;
        return first.substring(0, i);
    }

    /** Approach 3: vertical scan, one column at a time, stopping at the first mismatch. O(S) time where S = total characters, O(1) extra space. */
    static String optimal(String[] strs) {
        if (strs.length == 0) return "";
        for (int i = 0; i < strs[0].length(); i++) {
            char c = strs[0].charAt(i);
            for (int k = 1; k < strs.length; k++) {
                if (i == strs[k].length() || strs[k].charAt(i) != c) return strs[0].substring(0, i);
            }
        }
        return strs[0];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String[] strs, String expected) {
        String in = Arrays.toString(strs);
        check(expected.equals(bruteForce(strs)), "bruteForce failed on " + in + ": " + bruteForce(strs));
        check(expected.equals(better(strs)), "better failed on " + in + ": " + better(strs));
        check(expected.equals(optimal(strs)), "optimal failed on " + in + ": " + optimal(strs));
    }

    public static void main(String[] args) {
        verify(new String[]{"flower", "flow", "flight"}, "fl");
        verify(new String[]{"dog", "racecar", "car"}, "");
        verify(new String[]{"interspecies", "interstellar", "interstate"}, "inters");
        verify(new String[]{"a"}, "a");                            // single string
        verify(new String[]{}, "");                                // no strings at all
        verify(new String[]{"", "abc"}, "");                       // an empty string kills the prefix
        verify(new String[]{"abc", "abc", "abc"}, "abc");          // identical strings
        verify(new String[]{"ab", "abc", "a"}, "a");               // shortest string is a prefix of the others
        System.out.println("OK P395_LongestCommonPrefix");
    }
}
