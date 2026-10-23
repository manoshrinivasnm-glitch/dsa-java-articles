import java.util.*;

/** TUF 395 - Longest Common Prefix (LeetCode 14). Longest string that every input string starts with. */
public class P395_LongestCommonPrefix {

    /** Approach 1: try every prefix length from the longest possible down to 1. O(n * m^2) time, O(m) space, m = shortest string. */
    static String bruteForce(String[] strs) {
        if (strs.length == 0) return "";                 // no strings: empty prefix
        int minLen = Integer.MAX_VALUE;                  // the prefix can be no longer than the shortest string
        for (String s : strs) minLen = Math.min(minLen, s.length());
        for (int len = minLen; len >= 1; len--) {        // longest candidates first
            String candidate = strs[0].substring(0, len);   // the prefix must also be a prefix of strs[0]
            boolean all = true;                          // does every string start with it?
            for (String s : strs) {
                if (!s.startsWith(candidate)) { all = false; break; }   // one mismatch rules this length out
            }
            if (all) return candidate;                   // the first length that fits is the longest
        }
        return "";                                       // not even the first character is shared
    }

    /** Approach 2: sort; the common prefix of all strings is the common prefix of the first and the last. O(n * m log n) time, O(n) space. */
    static String better(String[] strs) {
        if (strs.length == 0) return "";                 // no strings: empty prefix
        String[] sorted = strs.clone();                  // sort a copy so the input is untouched
        Arrays.sort(sorted);
        String first = sorted[0], last = sorted[sorted.length - 1];   // smallest and largest in sorted order
        int i = 0;
        // advance while both still have characters and agree
        while (i < first.length() && i < last.length() && first.charAt(i) == last.charAt(i)) i++;
        return first.substring(0, i);                    // the first i characters are shared by all
    }

    /** Approach 3: vertical scan, one column at a time, stopping at the first mismatch. O(S) time where S = total characters, O(1) extra space. */
    static String optimal(String[] strs) {
        if (strs.length == 0) return "";                 // no strings: empty prefix
        for (int i = 0; i < strs[0].length(); i++) {     // column i of the first string
            char c = strs[0].charAt(i);                  // every string must have c at position i
            for (int k = 1; k < strs.length; k++) {      // compare against every other string
                // string k is too short or differs here: the prefix stops before i
                if (i == strs[k].length() || strs[k].charAt(i) != c) return strs[0].substring(0, i);
            }
        }
        return strs[0];                                  // the whole first string is a common prefix
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
