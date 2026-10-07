import java.util.*;

/** TUF 892 - Remove Outermost Parentheses (LeetCode 1021). Strip the outer pair of every primitive block of a valid parentheses string. */
public class P892_RemoveOutermostParentheses {

    /** Approach 1: cut the string into primitives first, then drop the first and last character of each. O(n) time, O(n) extra space. */
    static String bruteForce(String s) {
        List<String> primitives = new ArrayList<>();
        int depth = 0, start = 0;
        for (int i = 0; i < s.length(); i++) {
            depth += s.charAt(i) == '(' ? 1 : -1;
            if (depth == 0) {                              // s[start..i] is one primitive
                primitives.add(s.substring(start, i + 1));
                start = i + 1;
            }
        }
        StringBuilder out = new StringBuilder();
        for (String p : primitives) out.append(p, 1, p.length() - 1);   // drop p[0] and p[last]
        return out.toString();
    }

    /** Approach 2: explicit stack of open brackets. Keep a '(' only if something is already open; keep a ')' only if something stays open after it. O(n) time, O(n) space. */
    static String better(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        StringBuilder out = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '(') {
                if (!stack.isEmpty()) out.append(c);       // not an outermost opener
                stack.push(c);
            } else {
                stack.pop();
                if (!stack.isEmpty()) out.append(c);       // not an outermost closer
            }
        }
        return out.toString();
    }

    /** Approach 3: the stack only ever holds '(' so its size is all we need. O(n) time, O(1) extra space. */
    static String optimal(String s) {
        StringBuilder out = new StringBuilder();
        int depth = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                if (depth > 0) out.append(c);              // depth before the push
                depth++;
            } else {
                depth--;
                if (depth > 0) out.append(c);              // depth after the pop
            }
        }
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String expected) {
        check(expected.equals(bruteForce(s)), "bruteForce failed on \"" + s + "\": " + bruteForce(s));
        check(expected.equals(better(s)), "better failed on \"" + s + "\": " + better(s));
        check(expected.equals(optimal(s)), "optimal failed on \"" + s + "\": " + optimal(s));
    }

    public static void main(String[] args) {
        verify("(()())(())", "()()()");
        verify("(()())(())(()(()))", "()()()()(())");
        verify("()()", "");                                // every primitive is just an outer pair
        verify("", "");                                    // empty input
        verify("((()))", "(())");                          // one deeply nested primitive
        verify("(())(())", "()()");
        verify("()(())((()))", "()(())");
        System.out.println("OK P892_RemoveOutermostParentheses");
    }
}
