import java.util.*;

/** TUF 966 - Balanced Parentheses (LeetCode 20, Valid Parentheses). Is every bracket closed by its partner in the right order? */
public class P966_BalancedParanthesis {

    /** Approach 1: repeatedly delete adjacent matching pairs until nothing changes; balanced iff nothing is left. O(n^2). */
    static boolean bruteForce(String s) {
        String cur = s;
        while (true) {
            String next = cur.replace("()", "").replace("[]", "").replace("{}", "");
            if (next.length() == cur.length()) break;      // no adjacent pair left to remove
            cur = next;
        }
        return cur.isEmpty();
    }

    /** Approach 2: push every opener; a closer must match the opener on top of the stack. O(n) time, O(n) space. */
    static boolean optimal(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;         // a closer with nothing open
                char open = stack.pop();
                if (!matches(open, c)) return false;       // the wrong kind of closer
            }
        }
        return stack.isEmpty();                            // anything still open was never closed
    }

    static boolean matches(char open, char close) {
        return (open == '(' && close == ')') || (open == '[' && close == ']') || (open == '{' && close == '}');
    }

    /** Approach 3: push the closer we expect instead of the opener, so a closer only has to equal the top. O(n). */
    static boolean optimalExpectedCloser(String s) {
        if (s.length() % 2 != 0) return false;             // an odd number of brackets can never pair up
        Deque<Character> expected = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '(' -> expected.push(')');
                case '[' -> expected.push(']');
                case '{' -> expected.push('}');
                default -> {
                    if (expected.isEmpty() || expected.pop() != c) return false;
                }
            }
        }
        return expected.isEmpty();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, boolean expected) {
        check(bruteForce(s) == expected, "bruteForce(" + s + ") should be " + expected);
        check(optimal(s) == expected, "optimal(" + s + ") should be " + expected);
        check(optimalExpectedCloser(s) == expected, "optimalExpectedCloser(" + s + ") should be " + expected);
    }

    public static void main(String[] args) {
        verify("()", true);
        verify("()[]{}", true);
        verify("(]", false);
        verify("([)]", false);                             // right brackets, wrong order
        verify("{[]}", true);
        verify("([{}])", true);
        verify("", true);                                  // edge: nothing to balance
        verify("(", false);                                // edge: a lone opener
        verify(")", false);                                // edge: a lone closer
        verify("((", false);
        verify("))((", false);                             // closers before openers
        verify("(()", false);                              // odd length
        verify("{[()()]}[]{}", true);
        verify("(".repeat(2000) + ")".repeat(2000), true); // deep nesting
        verify("()".repeat(2000), true);                   // long and flat
        verify("(".repeat(2000) + ")".repeat(1999) + "]", false);
        System.out.println("OK P966_BalancedParanthesis");
    }
}
