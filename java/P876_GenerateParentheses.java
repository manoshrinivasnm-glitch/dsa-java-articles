import java.util.*;

/** TUF 876 - Generate Parentheses. Every well-formed string made of exactly n '(' and n ')'. */
public class P876_GenerateParentheses {

    /** Approach 1: try all 2^(2n) bracket strings and keep the balanced ones. O(2^(2n) * n) time. */
    static List<String> bruteForce(int n) {
        List<String> result = new ArrayList<>();
        int len = 2 * n;
        for (int mask = 0; mask < (1 << len); mask++) {
            char[] s = new char[len];
            for (int i = 0; i < len; i++) s[i] = ((mask >> (len - 1 - i)) & 1) == 0 ? '(' : ')';
            if (isBalanced(s)) result.add(new String(s));
        }
        return result;
    }

    /** Balanced: the running count of unmatched '(' never drops below 0 and ends at 0. */
    static boolean isBalanced(char[] s) {
        int open = 0;
        for (char c : s) {
            open += (c == '(') ? 1 : -1;
            if (open < 0) return false;                      // a ')' with nothing to close
        }
        return open == 0;
    }

    /** Approach 2: backtracking. Add '(' while some remain; add ')' only when it closes an unmatched '('. Output-sensitive O(4^n / sqrt(n)) time, O(n) stack. */
    static List<String> optimal(int n) {
        List<String> result = new ArrayList<>();
        backtrack(new StringBuilder(), 0, 0, n, result);
        return result;
    }

    static void backtrack(StringBuilder current, int open, int close, int n, List<String> result) {
        if (current.length() == 2 * n) {                     // base case: all 2n characters placed
            result.add(current.toString());
            return;
        }
        if (open < n) {                                      // choice 1: open another bracket
            current.append('(');
            backtrack(current, open + 1, close, n, result);
            current.deleteCharAt(current.length() - 1);      // undo before trying the other choice
        }
        if (close < open) {                                  // choice 2: close one, only if something is open
            current.append(')');
            backtrack(current, open, close + 1, n, result);
            current.deleteCharAt(current.length() - 1);
        }
    }

    /** Approach 3: structural recursion turned into a table. Every balanced string is "(" + A + ")" + B with A and B balanced; build the answers for k pairs from smaller k. */
    static List<String> byClosure(int n) {
        List<List<String>> table = new ArrayList<>();        // table.get(k) = all balanced strings with k pairs
        table.add(List.of(""));
        for (int k = 1; k <= n; k++) {
            List<String> level = new ArrayList<>();
            for (int inside = 0; inside < k; inside++) {    // pairs inside the first bracket; k - 1 - inside follow it
                for (String a : table.get(inside)) {
                    for (String b : table.get(k - 1 - inside)) level.add("(" + a + ")" + b);
                }
            }
            table.add(level);
        }
        return table.get(n);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, List<String> expected) {
        check(bruteForce(n).equals(expected), "bruteForce(" + n + ")");
        check(optimal(n).equals(expected), "optimal(" + n + ")");
        List<String> closure = new ArrayList<>(byClosure(n));
        Collections.sort(closure);                           // the closure order differs; compare as sorted lists
        check(closure.equals(expected), "byClosure(" + n + ")");
    }

    public static void main(String[] args) {
        verify(0, List.of(""));                              // zero pairs: only the empty string
        verify(1, List.of("()"));
        verify(2, List.of("(())", "()()"));
        verify(3, List.of("((()))", "(()())", "(())()", "()(())", "()()()"));
        int[] catalan = {1, 1, 2, 5, 14, 42, 132, 429, 1430};
        for (int n = 4; n <= 8; n++) {
            List<String> expected = bruteForce(n);
            check(expected.size() == catalan[n], "Catalan(" + n + ")");
            verify(n, expected);
        }
        System.out.println("OK P876_GenerateParentheses");
    }
}
