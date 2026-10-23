import java.util.*;

/** TUF 4 - Word Break. Can s be split into a sequence of dictionary words? Words may be reused any number of times. */
public class P4_WordBreak {

    /** Approach 1: try every dictionary word as the next piece and recurse on the rest. Exponential in the worst case. */
    static boolean bruteForce(String s, List<String> wordDict) {
        return canBreakFrom(s, 0, wordDict);             // can the whole string, from index 0, be split?
    }

    private static boolean canBreakFrom(String s, int start, List<String> dict) {
        if (start == s.length()) return true;            // everything before `start` was covered by words
        for (String w : dict) {                          // try each word as the next piece
            // the word matches at `start` and the remainder can also be split
            if (!w.isEmpty() && s.startsWith(w, start) && canBreakFrom(s, start + w.length(), dict)) return true;
        }
        return false;                                    // no word leads to a full split from here
    }

    /** Approach 2: the same recursion memoised on the start index (top-down DP). O(n^2) substring checks, O(n) memo. */
    static boolean better(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);      // O(1) lookup of any substring
        Boolean[] memo = new Boolean[s.length() + 1];    // null = not computed yet
        return canBreakMemo(s, 0, dict, memo);
    }

    private static boolean canBreakMemo(String s, int start, Set<String> dict, Boolean[] memo) {
        if (start == s.length()) return true;            // consumed the whole string
        if (memo[start] != null) return memo[start];     // this suffix was already solved
        for (int end = start + 1; end <= s.length(); end++) {   // every possible first word s[start, end)
            // s[start, end) is a word and the rest from `end` can be split too
            if (dict.contains(s.substring(start, end)) && canBreakMemo(s, end, dict, memo)) {
                memo[start] = true;
                return true;
            }
        }
        memo[start] = false;                             // remember the failure so it is never recomputed
        return false;
    }

    /** Approach 3: bottom-up DP. dp[i] is true when the prefix s[0, i) can be segmented; only words of length <= maxLen can end at i. */
    static boolean optimal(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);
        int maxLen = 0;                                  // longest dictionary word
        for (String w : dict) maxLen = Math.max(maxLen, w.length());
        int n = s.length();
        // dp[i]: the first i characters can be split into words
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;                                    // the empty prefix is trivially segmented
        for (int i = 1; i <= n; i++) {                   // decide each prefix length i in turn
            // j is where the last word starts; it is never longer than maxLen
            for (int j = i - 1; j >= 0 && i - j <= maxLen; j--) {
                if (dp[j] && dict.contains(s.substring(j, i))) {   // split prefix + one more word s[j, i)
                    dp[i] = true;
                    break;                               // one valid last word is enough
                }
            }
        }
        return dp[n];                                    // answer for the whole string
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, List<String> dict, boolean expected) {
        check(bruteForce(s, dict) == expected, "bruteForce wrong for \"" + s + "\"");
        check(better(s, dict) == expected, "better wrong for \"" + s + "\"");
        check(optimal(s, dict) == expected, "optimal wrong for \"" + s + "\"");
    }

    public static void main(String[] args) {
        verify("leetcode", List.of("leet", "code"), true);
        verify("applepenapple", List.of("apple", "pen"), true);              // "apple" is reused
        verify("catsandog", List.of("cats", "dog", "sand", "and", "cat"), false);
        verify("cars", List.of("car", "ca", "rs"), true);                    // greedy "car" fails, "ca" + "rs" works
        verify("", List.of("a"), true);                                      // empty string: nothing to cover
        verify("a", List.of("b"), false);
        verify("aaaaaaaaaaaab", List.of("a", "aa", "aaa"), false);           // exponential for the brute force
        verify("aaaaaaaaaaaa", List.of("a", "aa", "aaa"), true);
        verify("abcd", List.of("abcd"), true);                               // the whole string is one word
        verify("abcd", List.of("ab", "abc", "cd", "d"), true);               // needs the shorter first word
        System.out.println("OK P4_WordBreak");
    }
}
