import java.util.*;

/** TUF 587 - Infix to Prefix Conversion. Operands are single letters or digits; operators + - * / ^ ; brackets allowed. */
public class P587_InfixToPrefixConversion {

    static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    static int precedence(char op) {
        return switch (op) {
            case '^' -> 3;
            case '*', '/' -> 2;
            case '+', '-' -> 1;
            default -> -1;
        };
    }

    /** Approach 1: two stacks, operators and finished prefix strings; apply an operator as soon as it is safe. O(n^2) worst-case time, O(n) space. */
    static String twoStacks(String infix) {
        Deque<String> operands = new ArrayDeque<>();
        Deque<Character> ops = new ArrayDeque<>();
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            if (isOperand(c)) {
                operands.push(String.valueOf(c));
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (ops.peek() != '(') reduce(operands, ops);
                ops.pop();                           // discard the matching '('
            } else {
                while (!ops.isEmpty() && ops.peek() != '(' && appliesBefore(ops.peek(), c)) reduce(operands, ops);
                ops.push(c);
            }
        }
        while (!ops.isEmpty()) reduce(operands, ops);
        return operands.isEmpty() ? "" : operands.pop();
    }

    /** True when the operator on the stack must be applied before the incoming one is pushed. */
    static boolean appliesBefore(char top, char incoming) {
        if (precedence(top) != precedence(incoming)) return precedence(top) > precedence(incoming);
        return incoming != '^';                      // equal precedence: left-associative operators go first, '^' waits
    }

    /** Pops one operator and its two operands and pushes the combined prefix string. */
    static void reduce(Deque<String> operands, Deque<Character> ops) {
        char op = ops.pop();
        String right = operands.pop();
        String left = operands.pop();
        operands.push(op + left + right);
    }

    /** Approach 2: reverse the infix and swap brackets, run infix-to-postfix with flipped tie-breaking, reverse the output. O(n) time, O(n) space. */
    static String optimal(String infix) {
        StringBuilder rev = new StringBuilder(infix).reverse();
        for (int i = 0; i < rev.length(); i++) {
            char c = rev.charAt(i);
            if (c == '(') rev.setCharAt(i, ')');
            else if (c == ')') rev.setCharAt(i, '(');
        }
        StringBuilder out = new StringBuilder(infix.length());
        Deque<Character> ops = new ArrayDeque<>();
        for (int i = 0; i < rev.length(); i++) {
            char c = rev.charAt(i);
            if (isOperand(c)) {
                out.append(c);
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (ops.peek() != '(') out.append(ops.pop());
                ops.pop();
            } else {
                while (!ops.isEmpty() && ops.peek() != '(' && popsInReversed(ops.peek(), c)) out.append(ops.pop());
                ops.push(c);
            }
        }
        while (!ops.isEmpty()) out.append(ops.pop());
        return out.reverse().toString();
    }

    /** Pop rule for the reversed string: associativity is mirrored, so only '^' pops an equal-precedence operator. */
    static boolean popsInReversed(char top, char incoming) {
        if (precedence(top) != precedence(incoming)) return precedence(top) > precedence(incoming);
        return incoming == '^';
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String infix, String expected) {
        check(expected.equals(twoStacks(infix)), "twoStacks failed on " + infix + ": " + twoStacks(infix));
        check(expected.equals(optimal(infix)), "optimal failed on " + infix + ": " + optimal(infix));
    }

    public static void main(String[] args) {
        verify("x+y*z/w+u", "++x/*yzwu");
        verify("(A-B/C)*(A/K-L)", "*-A/BC-/AKL");
        verify("a+b*(c^d-e)^(f+g*h)-i", "-+a*b^-^cde+f*ghi");
        verify("a-b-c", "--abc");                      // left-associative: (a-b)-c
        verify("a^b^c", "^a^bc");                      // right-associative: a^(b^c)
        verify("(a-b)-(c-d)", "--ab-cd");
        verify("((a+b))", "+ab");                      // redundant brackets
        verify("1+2*3", "+1*23");                      // digits are operands too
        verify("a", "a");                              // a lone operand
        verify("", "");                                // empty input
        System.out.println("OK P587_InfixToPrefixConversion");
    }
}
