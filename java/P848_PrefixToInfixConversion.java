import java.util.*;

/** TUF 848 - Prefix to Infix Conversion. Operands are single letters or digits; operators + - * / ^. The infix result is fully parenthesised. */
public class P848_PrefixToInfixConversion {

    static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    /** Approach 1: recursion from the left. An operator is followed by its left and then its right operand expression. O(n) tokens, O(depth) stack. */
    static String recursive(String prefix) {
        int[] pos = {0};                                        // cursor shared by all recursive calls
        String result = parse(prefix, pos);
        if (pos[0] != prefix.length()) throw new IllegalArgumentException("leftover tokens from index " + pos[0]);
        return result;
    }

    private static String parse(String s, int[] pos) {
        if (pos[0] >= s.length()) throw new IllegalArgumentException("expression ended early");
        char c = s.charAt(pos[0]++);
        if (!isOperator(c)) return String.valueOf(c);           // an operand is a complete expression by itself
        String left = parse(s, pos);                            // in prefix the left operand comes first
        String right = parse(s, pos);
        return "(" + left + c + right + ")";
    }

    /** Approach 2: scan from the right with a stack. Operands are pushed; an operator pops left then right and pushes the combined infix. O(n) tokens. */
    static String optimal(String prefix) {
        Deque<String> stack = new ArrayDeque<>();
        for (int i = prefix.length() - 1; i >= 0; i--) {
            char c = prefix.charAt(i);
            if (isOperator(c)) {
                if (stack.size() < 2) throw new IllegalArgumentException("operator '" + c + "' at " + i + " lacks operands");
                String left = stack.pop();                      // nearer to the operator = left operand
                String right = stack.pop();
                stack.push("(" + left + c + right + ")");
            } else {
                stack.push(String.valueOf(c));
            }
        }
        if (stack.size() != 1) throw new IllegalArgumentException("malformed prefix expression");
        return stack.pop();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String prefix, String expected) {
        check(recursive(prefix).equals(expected), "recursive(" + prefix + ") = " + recursive(prefix) + ", expected " + expected);
        check(optimal(prefix).equals(expected), "optimal(" + prefix + ") = " + optimal(prefix) + ", expected " + expected);
    }

    static boolean rejects(String prefix) {
        boolean a = false, b = false;
        try {
            recursive(prefix);
        } catch (IllegalArgumentException e) {
            a = true;
        }
        try {
            optimal(prefix);
        } catch (IllegalArgumentException e) {
            b = true;
        }
        return a && b;
    }

    public static void main(String[] args) {
        verify("*-A/BC-/AKL", "((A-(B/C))*((A/K)-L))");        // the classic textbook example
        verify("+ab", "(a+b)");
        verify("*+ab-cd", "((a+b)*(c-d))");
        verify("-+a*bc/de", "((a+(b*c))-(d/e))");
        verify("^a^bc", "(a^(b^c))");                           // nested on the right
        verify("^^abc", "((a^b)^c)");                           // nested on the left
        verify("+12", "(1+2)");                                 // digits are operands too
        verify("a", "a");                                       // edge: a lone operand needs no parentheses
        verify("-a-b-cd", "(a-(b-(c-d)))");                     // right-leaning chain
        verify("---abcd", "(((a-b)-c)-d)");                     // left-leaning chain
        check(rejects("+a"), "missing operand must be rejected");
        check(rejects("ab"), "two operands without an operator must be rejected");
        check(rejects(""), "empty input must be rejected");
        check(rejects("+ab+"), "trailing operator must be rejected");
        System.out.println("OK P848_PrefixToInfixConversion");
    }
}
