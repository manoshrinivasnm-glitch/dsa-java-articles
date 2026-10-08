import java.util.*;

/** TUF 545 - Valid Parenthesis Checker (LeetCode 678). '*' may act as '(', ')' or nothing; can the string be balanced? */
public class P545_ValidParanthesisChecker {

    /** Approach 1: try all three meanings of every '*'. O(3^k * n) time for k stars, O(n) stack. */
    static boolean bruteForce(String s) {
        return tryAll(s, 0, 0);
    }

    static boolean tryAll(String s, int i, int open) {
        if (open < 0) return false;                     // a ')' arrived with nothing to close
        if (i == s.length()) return open == 0;
        char c = s.charAt(i);
        if (c == '(') return tryAll(s, i + 1, open + 1);
        if (c == ')') return tryAll(s, i + 1, open - 1);
        return tryAll(s, i + 1, open + 1)               // '*' as '('
                || tryAll(s, i + 1, open - 1)           // '*' as ')'
                || tryAll(s, i + 1, open);              // '*' as empty
    }

    /** Approach 2: the same recursion memoised on (index, open count). O(n^2) time, O(n^2) space. */
    static boolean better(String s) {
        int n = s.length();
        Boolean[][] memo = new Boolean[n + 1][n + 1];
        return solve(s, 0, 0, memo);
    }

    static boolean solve(String s, int i, int open, Boolean[][] memo) {
        if (open < 0) return false;
        if (i == s.length()) return open == 0;
        if (memo[i][open] != null) return memo[i][open];
        char c = s.charAt(i);
        boolean ans;
        if (c == '(') ans = solve(s, i + 1, open + 1, memo);
        else if (c == ')') ans = solve(s, i + 1, open - 1, memo);
        else ans = solve(s, i + 1, open + 1, memo) || solve(s, i + 1, open - 1, memo) || solve(s, i + 1, open, memo);
        memo[i][open] = ans;
        return ans;
    }

    /** Approach 3: greedy, track the range [lo, hi] of possible open counts. O(n) time, O(1) space. */
    static boolean optimal(String s) {
        int lo = 0, hi = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                lo++;
                hi++;
            } else if (c == ')') {
                lo--;
                hi--;
            } else {                    // '*': one fewer if it closes, one more if it opens
                lo--;
                hi++;
            }
            if (hi < 0) return false;   // too many ')' even if every '*' opened
            if (lo < 0) lo = 0;         // a negative count is not a real state; drop it
        }
        return lo == 0;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, boolean expected) {
        check(bruteForce(s) == expected, "bruteForce wrong for \"" + s + "\"");
        check(better(s) == expected, "better wrong for \"" + s + "\"");
        check(optimal(s) == expected, "optimal wrong for \"" + s + "\"");
    }

    public static void main(String[] args) {
        verify("()", true);
        verify("(*)", true);
        verify("(*))", true);                   // '*' acts as '('
        verify("(*()", true);                   // '*' acts as ')'
        verify("*)", true);
        verify("(*", true);
        verify("*", true);                      // '*' acts as empty
        verify("(()*)(()*)**", true);
        verify(")(", false);                    // order matters, not only counts
        verify("(((*)", false);                 // not enough closers
        verify("**((", false);                  // trailing '(' can never be closed
        verify("(*)*)(", false);
        verify("((*)(*))((*", false);
        verify(")", false);
        verify("", true);                       // empty string is balanced
        verify("*()(())*()(()()((()(()()*)(*(())((((((((()*)(()(*)", false);
        System.out.println("OK P545_ValidParanthesisChecker");
    }
}
