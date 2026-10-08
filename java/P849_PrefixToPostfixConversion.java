import java.util.*;

/** TUF 849 - Prefix to Postfix Conversion. Operands are single letters or digits; every other character is a binary operator. */
public class P849_PrefixToPostfixConversion {

    static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    /** Approach 1: scan right to left with a stack of finished postfix pieces. O(n^2) worst-case time (string copies), O(n) space. */
    static String stackOfStrings(String prefix) {
        Deque<String> st = new ArrayDeque<>();
        for (int i = prefix.length() - 1; i >= 0; i--) {
            char c = prefix.charAt(i);
            if (isOperand(c)) {
                st.push(String.valueOf(c));
            } else {
                String left = st.pop();              // pushed last, so it is the left operand
                String right = st.pop();
                st.push(left + right + c);
            }
        }
        return st.isEmpty() ? "" : st.pop();
    }

    /** Approach 2: recursive descent, left to right, writing straight into one StringBuilder. O(n) time, O(n) recursion depth. */
    static String recursiveDescent(String prefix) {
        if (prefix.isEmpty()) return "";
        StringBuilder out = new StringBuilder(prefix.length());
        parse(prefix, 0, out);
        return out.toString();
    }

    /** Converts the prefix expression starting at index i, appends its postfix form, returns the index just past it. */
    static int parse(String s, int i, StringBuilder out) {
        char c = s.charAt(i);
        if (isOperand(c)) {
            out.append(c);
            return i + 1;
        }
        int afterLeft = parse(s, i + 1, out);      // left operand starts right after the operator
        int afterRight = parse(s, afterLeft, out); // right operand starts where the left one ended
        out.append(c);                             // in postfix the operator comes last
        return afterRight;
    }

    /** Approach 3: iterative, a stack of operators each counting how many operands it has received. O(n) time, O(n) space. */
    static String optimal(String prefix) {
        StringBuilder out = new StringBuilder(prefix.length());
        Deque<int[]> pending = new ArrayDeque<>();   // {operator, operands completed so far}
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!isOperand(c)) {
                pending.push(new int[]{c, 0});
                continue;
            }
            out.append(c);
            // a complete operand just ended; an operator that now has both operands is complete too
            while (!pending.isEmpty() && ++pending.peek()[1] == 2) {
                out.append((char) pending.pop()[0]);
            }
        }
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String prefix, String expected) {
        check(expected.equals(stackOfStrings(prefix)), "stackOfStrings failed on " + prefix);
        check(expected.equals(recursiveDescent(prefix)), "recursiveDescent failed on " + prefix);
        check(expected.equals(optimal(prefix)), "optimal failed on " + prefix);
    }

    public static void main(String[] args) {
        verify("*+AB-CD", "AB+CD-*");
        verify("*-A/BC-/AKL", "ABC/-AK/L-*");
        verify("+AB", "AB+");
        verify("-+a*bcd", "abc*+d-");
        verify("^^abc", "ab^c^");                      // (a^b)^c
        verify("^a^bc", "abc^^");                      // a^(b^c)
        verify("+12", "12+");                          // digits are operands too
        verify("A", "A");                              // a lone operand
        verify("", "");                                // empty input
        int k = 1000;                                  // deep left-leaning chain
        verify("+".repeat(k) + "a".repeat(k + 1), "a" + "a+".repeat(k));
        System.out.println("OK P849_PrefixToPostfixConversion");
    }
}
