import java.util.*;

/** TUF 674 - Maximum Nesting Depth of the Parentheses. Deepest level of nested '(' in a valid parentheses string. */
public class P674_MaximumNestingDepthOfTheParentheses {

    /** Approach 1: for every '(' recount how many brackets are still open at that point. O(n^2) time, O(1) space. */
    static int bruteForce(String s) {
        int n = s.length(), best = 0;
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) != '(') continue;
            int open = 0;                                  // unmatched '(' in s[0..i], including this one
            for (int j = 0; j <= i; j++) {
                char c = s.charAt(j);
                if (c == '(') open++;
                else if (c == ')') open--;
            }
            best = Math.max(best, open);
        }
        return best;
    }

    /** Approach 2: push every '(' on a stack, pop on ')', remember the tallest the stack ever gets. O(n) time, O(n) space. */
    static int better(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        int best = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                stack.push(c);
                best = Math.max(best, stack.size());
            } else if (c == ')') {
                stack.pop();
            }
        }
        return best;
    }

    /** Approach 3: the stack only ever holds '(', so its size is all that matters; keep a counter instead. O(n) time, O(1) space. */
    static int optimal(String s) {
        int depth = 0, best = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                depth++;
                if (depth > best) best = depth;
            } else if (c == ')') {
                depth--;
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        check(bruteForce(s) == expected, "bruteForce(\"" + s + "\") != " + expected);
        check(better(s) == expected, "better(\"" + s + "\") != " + expected);
        check(optimal(s) == expected, "optimal(\"" + s + "\") != " + expected);
    }

    public static void main(String[] args) {
        verify("(1+(2*3)+((8)/4))+1", 3);
        verify("(1)+((2))+(((3)))", 3);
        verify("1+(2*3)/(2-1)", 1);
        verify("1", 0);                                    // no brackets at all
        verify("", 0);                                     // the empty string is a valid parentheses string
        verify("((((()))))", 5);
        verify("()()()", 1);                               // siblings do not add depth
        verify("(()(()))", 3);
        verify("((a)+((b)(c)))", 3);
        System.out.println("OK P674_MaximumNestingDepthOfTheParentheses");
    }
}
