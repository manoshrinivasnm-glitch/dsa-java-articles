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
        Deque<String> operands = new ArrayDeque<>();   // prefix strings of the sub-expressions built so far
        Deque<Character> ops = new ArrayDeque<>();     // operators and '(' still waiting to be applied
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            if (isOperand(c)) {                        // an operand is already a finished prefix piece
                operands.push(String.valueOf(c));
            } else if (c == '(') {                     // '(' marks where a bracketed group starts
                ops.push(c);
            } else if (c == ')') {                     // closing bracket: finish the whole group
                while (ops.peek() != '(') reduce(operands, ops);   // apply every operator inside it
                ops.pop();                             // discard the matching '('
            } else {                                   // an operator arrives
                // first apply stacked operators that must happen before c
                while (!ops.isEmpty() && ops.peek() != '(' && appliesBefore(ops.peek(), c)) reduce(operands, ops);
                ops.push(c);                           // c now waits for its right operand
            }
        }
        while (!ops.isEmpty()) reduce(operands, ops);  // apply whatever operators are left
        return operands.isEmpty() ? "" : operands.pop();   // the one remaining string is the whole expression
    }

    /** True when the operator on the stack must be applied before the incoming one is pushed. */
    static boolean appliesBefore(char top, char incoming) {
        // different precedence: the stronger operator applies first
        if (precedence(top) != precedence(incoming)) return precedence(top) > precedence(incoming);
        return incoming != '^';                      // equal precedence: left-associative operators go first, '^' waits
    }

    /** Pops one operator and its two operands and pushes the combined prefix string. */
    static void reduce(Deque<String> operands, Deque<Character> ops) {
        char op = ops.pop();                           // the operator to apply
        String right = operands.pop();                 // pushed last, so it is the right operand
        String left = operands.pop();                  // pushed earlier, so it is the left operand
        operands.push(op + left + right);              // prefix: operator, then left, then right
    }

    /** Approach 2: reverse the infix and swap brackets, run infix-to-postfix with flipped tie-breaking, reverse the output. O(n) time, O(n) space. */
    static String optimal(String infix) {
        StringBuilder rev = new StringBuilder(infix).reverse();   // read the expression from right to left
        for (int i = 0; i < rev.length(); i++) {       // swap every bracket so groups still open first
            char c = rev.charAt(i);
            if (c == '(') rev.setCharAt(i, ')');
            else if (c == ')') rev.setCharAt(i, '(');
        }
        StringBuilder out = new StringBuilder(infix.length());   // postfix of the reversed input
        Deque<Character> ops = new ArrayDeque<>();     // operators and '(' waiting to be written
        for (int i = 0; i < rev.length(); i++) {
            char c = rev.charAt(i);
            if (isOperand(c)) {                        // operands go straight to the output
                out.append(c);
            } else if (c == '(') {                     // '(' marks where a bracketed group starts
                ops.push(c);
            } else if (c == ')') {                     // closing bracket: flush the group's operators
                while (ops.peek() != '(') out.append(ops.pop());
                ops.pop();                             // drop the '(' itself
            } else {
                // write out stacked operators that must come before c
                while (!ops.isEmpty() && ops.peek() != '(' && popsInReversed(ops.peek(), c)) out.append(ops.pop());
                ops.push(c);                           // c now waits for its right operand
            }
        }
        while (!ops.isEmpty()) out.append(ops.pop());  // flush the operators still waiting
        return out.reverse().toString();               // reversed postfix of the reversed input is the prefix
    }

    /** Pop rule for the reversed string: associativity is mirrored, so only '^' pops an equal-precedence operator. */
    static boolean popsInReversed(char top, char incoming) {
        // different precedence: the stronger operator is written first
        if (precedence(top) != precedence(incoming)) return precedence(top) > precedence(incoming);
        return incoming == '^';                        // equal precedence: only right-associative '^' pops
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
