import java.util.*;

/** TUF 314 - Wildcard Matching (LeetCode 44). '?' matches any one character, '*' matches any sequence (also empty); the whole of s must match. */
public class P314_WildcardMatching {

    /** Approach 1: recursion on prefix lengths. Exponential time, O(n + m) stack. */
    static boolean recursive(String s, String p) {
        return solve(s.length(), p.length(), s, p);
    }

    /** Does the prefix s[0..i) match the pattern prefix p[0..j)? */
    static boolean solve(int i, int j, String s, String p) {
        if (j == 0) return i == 0;                               // empty pattern matches only the empty string
        if (i == 0) return allStars(p, j);                       // empty string: the pattern must be all '*'
        char pc = p.charAt(j - 1);
        if (pc == '?' || pc == s.charAt(i - 1)) return solve(i - 1, j - 1, s, p);
        if (pc == '*') return solve(i, j - 1, s, p)              // the star matches nothing
                           || solve(i - 1, j, s, p);             // the star swallows s[i - 1] and may continue
        return false;                                            // two different letters
    }

    /** True when p[0..j) consists only of '*'. */
    static boolean allStars(String p, int j) {
        for (int k = 0; k < j; k++) if (p.charAt(k) != '*') return false;
        return true;
    }

    /** Approach 2: memoization on (i, j); 0 = unknown, 1 = false, 2 = true. O(n * m) time and space. */
    static boolean memoization(String s, String p) {
        int[][] dp = new int[s.length() + 1][p.length() + 1];
        return memo(s.length(), p.length(), s, p, dp);
    }

    static boolean memo(int i, int j, String s, String p, int[][] dp) {
        if (j == 0) return i == 0;
        if (i == 0) return allStars(p, j);
        if (dp[i][j] != 0) return dp[i][j] == 2;
        char pc = p.charAt(j - 1);
        boolean ok;
        if (pc == '?' || pc == s.charAt(i - 1)) ok = memo(i - 1, j - 1, s, p, dp);
        else if (pc == '*') ok = memo(i, j - 1, s, p, dp) || memo(i - 1, j, s, p, dp);
        else ok = false;
        dp[i][j] = ok ? 2 : 1;
        return ok;
    }

    /** Approach 3: tabulation. Row 0 is true while the pattern is a run of stars; column 0 is false below row 0. O(n * m) time and space. */
    static boolean tabulation(String s, String p) {
        int n = s.length(), m = p.length();
        boolean[][] dp = new boolean[n + 1][m + 1];
        dp[0][0] = true;
        for (int j = 1; j <= m; j++) dp[0][j] = dp[0][j - 1] && p.charAt(j - 1) == '*';
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '?' || pc == s.charAt(i - 1)) dp[i][j] = dp[i - 1][j - 1];
                else if (pc == '*') dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                else dp[i][j] = false;
            }
        }
        return dp[n][m];
    }

    /** Approach 4: two rows; cur[0] is false for every non-empty prefix of s. O(n * m) time, O(m) space. */
    static boolean spaceOptimized(String s, String p) {
        int n = s.length(), m = p.length();
        boolean[] prev = new boolean[m + 1], cur = new boolean[m + 1];
        prev[0] = true;
        for (int j = 1; j <= m; j++) prev[j] = prev[j - 1] && p.charAt(j - 1) == '*';
        for (int i = 1; i <= n; i++) {
            cur[0] = false;                                      // a non-empty string never matches an empty pattern
            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '?' || pc == s.charAt(i - 1)) cur[j] = prev[j - 1];
                else if (pc == '*') cur[j] = cur[j - 1] || prev[j];
                else cur[j] = false;
            }
            boolean[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m];
    }

    /** Approach 5: greedy two pointers that only ever backtrack to the most recent star. O(n * m) worst case, O(1) space. */
    static boolean greedy(String s, String p) {
        int i = 0, j = 0, star = -1, mark = 0;                   // star = index of the last '*', mark = where it started in s
        while (i < s.length()) {
            if (j < p.length() && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) {
                i++;
                j++;
            } else if (j < p.length() && p.charAt(j) == '*') {
                star = j++;                                      // first let the star match nothing
                mark = i;
            } else if (star != -1) {
                j = star + 1;                                    // stuck: let the last star swallow one more character
                i = ++mark;
            } else {
                return false;
            }
        }
        while (j < p.length() && p.charAt(j) == '*') j++;        // trailing stars can match the empty rest
        return j == p.length();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String p, boolean expected) {
        String in = "\"" + s + "\" vs \"" + p + "\"";
        check(recursive(s, p) == expected, "recursive failed for " + in);
        check(memoization(s, p) == expected, "memoization failed for " + in);
        check(tabulation(s, p) == expected, "tabulation failed for " + in);
        check(spaceOptimized(s, p) == expected, "spaceOptimized failed for " + in);
        check(greedy(s, p) == expected, "greedy failed for " + in);
    }

    public static void main(String[] args) {
        verify("aa", "a", false);                   // the pattern must cover all of s
        verify("aa", "*", true);
        verify("cb", "?a", false);                  // '?' matches c, but a != b
        verify("adceb", "*a*b", true);              // first star = "", second star = "dce"
        verify("acdcb", "a*c?b", false);
        verify("abcabczzzde", "*abc???de*", true);
        verify("", "", true);                       // edge: both empty
        verify("", "***", true);                    // edge: stars can match nothing
        verify("", "?", false);                     // edge: '?' needs exactly one character
        verify("a", "", false);                     // edge: empty pattern
        verify("mississippi", "m??*ss*?i*pi", false);

        // Cross-check on seeded random strings and patterns over {a, b, ?, *}. The independent oracle is
        // String.matches with '?' -> "." and '*' -> ".*", which is safe because 'a' and 'b' are not regex metacharacters.
        Random rnd = new Random(314);
        String patternChars = "ab?*";
        for (int t = 0; t < 400; t++) {
            StringBuilder s = new StringBuilder(), p = new StringBuilder();
            int n = rnd.nextInt(8), m = rnd.nextInt(7);
            for (int k = 0; k < n; k++) s.append((char) ('a' + rnd.nextInt(2)));
            for (int k = 0; k < m; k++) p.append(patternChars.charAt(rnd.nextInt(4)));
            String regex = p.toString().replace("?", ".").replace("*", ".*");
            verify(s.toString(), p.toString(), s.toString().matches(regex));
        }
        System.out.println("OK P314_WildcardMatching");
    }
}
