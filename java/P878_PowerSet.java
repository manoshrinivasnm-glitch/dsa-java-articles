import java.util.*;

/** TUF 878 - Power Set. Every subsequence of a string (its power set), generated three ways. */
public class P878_PowerSet {

    /** Approach 1: one bit per character; the bits of each number from 0 to 2^n - 1 say which characters to keep. O(2^n * n) time. */
    static List<String> bitmask(String s) {
        int n = s.length();
        List<String> result = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) sb.append(s.charAt(i));   // bit i set: take s[i]
            }
            result.add(sb.toString());
        }
        return result;
    }

    /** Approach 2: take / not-take recursion over the index. O(2^n * n) time, O(n) stack. */
    static List<String> recursive(String s) {
        List<String> result = new ArrayList<>();
        generate(s, 0, new StringBuilder(), result);
        return result;
    }

    static void generate(String s, int index, StringBuilder current, List<String> result) {
        if (index == s.length()) {                            // base case: a decision was made for every character
            result.add(current.toString());
            return;
        }
        current.append(s.charAt(index));                      // choice 1: take s[index]
        generate(s, index + 1, current, result);
        current.deleteCharAt(current.length() - 1);           // undo, so the next branch starts from the same prefix
        generate(s, index + 1, current, result);              // choice 2: leave s[index] out
    }

    /** Approach 3: iterative doubling. Start with {""}; each character appends itself to a copy of every subsequence found so far. O(2^n * n) time. */
    static List<String> cascading(String s) {
        List<String> result = new ArrayList<>();
        result.add("");                                       // the power set of the empty prefix
        for (int i = 0; i < s.length(); i++) {
            int size = result.size();                         // extend only the subsequences that existed before this character
            for (int j = 0; j < size; j++) result.add(result.get(j) + s.charAt(i));
        }
        return result;
    }

    /** The GFG variant of the problem: non-empty subsequences in lexicographic order. */
    static List<String> sortedNonEmpty(String s) {
        List<String> all = recursive(s);
        all.remove("");                                       // exactly one empty subsequence exists
        Collections.sort(all);
        return all;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<String> sorted(List<String> list) {
        List<String> copy = new ArrayList<>(list);
        Collections.sort(copy);
        return copy;
    }

    static void verify(String s, List<String> expectedSorted) {
        check(sorted(bitmask(s)).equals(expectedSorted), "bitmask(\"" + s + "\")");
        check(sorted(recursive(s)).equals(expectedSorted), "recursive(\"" + s + "\")");
        check(sorted(cascading(s)).equals(expectedSorted), "cascading(\"" + s + "\")");
        check(bitmask(s).size() == (1 << s.length()), "2^n subsequences for \"" + s + "\"");
    }

    public static void main(String[] args) {
        verify("abc", List.of("", "a", "ab", "abc", "ac", "b", "bc", "c"));
        verify("", List.of(""));                                              // the empty string has one subsequence
        verify("a", List.of("", "a"));
        verify("ba", List.of("", "a", "b", "ba"));                            // order is preserved: "ab" never appears
        verify("aab", List.of("", "a", "a", "aa", "aab", "ab", "ab", "b"));   // repeated letters give repeated subsequences
        verify("abcd", List.of("", "a", "ab", "abc", "abcd", "abd", "ac", "acd", "ad", "b", "bc", "bcd", "bd", "c", "cd", "d"));
        verify("0123456789", sorted(bitmask("0123456789")));                  // 1024 subsequences: all three generators agree
        check(recursive("abc").equals(List.of("abc", "ab", "ac", "a", "bc", "b", "c", "")), "take-first order");
        check(bitmask("abc").equals(List.of("", "a", "b", "ab", "c", "ac", "bc", "abc")), "bitmask order");
        check(cascading("abc").equals(bitmask("abc")), "cascading produces the bitmask order");
        check(sortedNonEmpty("abc").equals(List.of("a", "ab", "abc", "ac", "b", "bc", "c")), "GFG output for abc");
        check(sortedNonEmpty("").isEmpty(), "GFG output for the empty string");
        System.out.println("OK P878_PowerSet");
    }
}
