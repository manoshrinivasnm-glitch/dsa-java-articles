import java.util.*;

/** TUF 586 - Infix to Postfix Conversion. Operands are single letters or digits; operators + - * / ^ and parentheses. */
public class P586_InfixToPostfixConversion {

    /** Binding strength of an operator; -1 for anything that is not an operator. */
    static int precedence(char op) {
        return switch (op) {  // higher number = binds tighter
            case '^' -> 3;    // power binds tightest
            case '*', '/' -> 2;
            case '+', '-' -> 1;
            default -> -1;    // not an operator (operand or parenthesis)
        };
    }

    static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);  // single letters or digits are operands
    }

    /** Approach 1: recursive descent. One method per precedence level; each emits its operands first and its operator last. O(n). */
    static String recursiveDescent(String infix) {
        Parser p = new Parser(infix);
        p.expr();  // parse everything, emitting postfix along the way
        // leftover input means it was not one complete expression
        if (p.pos != infix.length()) throw new IllegalArgumentException("unexpected '" + infix.charAt(p.pos) + "' at " + p.pos);
        return p.out.toString();
    }

    /** Grammar: expr -> term {(+|-) term}; term -> factor {(*|/) factor}; factor -> base [^ factor]; base -> operand | ( expr ). */
    static class Parser {
        final String s;                                 // the infix input
        int pos = 0;                                    // cursor: index of the next unread character
        final StringBuilder out = new StringBuilder();  // postfix output, built left to right

        Parser(String s) {
            this.s = s;
        }

        char peek() {
            return pos < s.length() ? s.charAt(pos) : '\0';  // '\0' marks the end of the input
        }

        void expr() {                                 // left-associative + and -
            term();                                   // left operand
            while (peek() == '+' || peek() == '-') {  // another + or - follows
                char op = s.charAt(pos++);            // consume the operator
                term();                               // its right operand
                out.append(op);                       // both operands are out, now the operator
            }
        }

        void term() {                                 // left-associative * and /
            factor();                                 // left operand
            while (peek() == '*' || peek() == '/') {  // another * or / follows
                char op = s.charAt(pos++);
                factor();                             // its right operand
                out.append(op);                       // operator after both operands
            }
        }

        void factor() {                               // right-associative ^: a^b^c means a^(b^c)
            base();                                   // the base being raised
            if (peek() == '^') {                      // an exponent follows
                pos++;                                // consume the '^'
                factor();                             // recurse, do not loop, so the right side binds first
                out.append('^');                      // operator after both operands
            }
        }

        void base() {
            char c = peek();            // the next character decides the case
            if (c == '(') {             // a parenthesised sub-expression
                pos++;
                expr();                 // parse the inside as a full expression
                // the group must be closed
                if (peek() != ')') throw new IllegalArgumentException("missing ')' at " + pos);
                pos++;                  // consume the ')'
            } else if (isOperand(c)) {  // an operand goes straight to the output
                out.append(c);
                pos++;
            } else {
                throw new IllegalArgumentException("expected an operand at " + pos);
            }
        }
    }

    /** Approach 2: shunting-yard. Operands go straight out; an operator first pops stronger (or equal, if left-associative) operators. O(n). */
    static String optimal(String infix) {
        if (infix.isEmpty()) throw new IllegalArgumentException("empty expression");
        StringBuilder out = new StringBuilder();    // the postfix result
        Deque<Character> ops = new ArrayDeque<>();  // operators and '(' barriers still waiting
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            if (isOperand(c)) {                     // operands keep their order, so they go straight out
                out.append(c);
            } else if (c == '(') {                  // '(' is a barrier that nothing pops past
                ops.push(c);
            } else if (c == ')') {                  // ')' closes the innermost group
                // flush the operators inside the group
                while (!ops.isEmpty() && ops.peek() != '(') out.append(ops.pop());
                if (ops.isEmpty()) throw new IllegalArgumentException("unmatched ')' at " + i);
                ops.pop();                          // discard the '('
            } else if (precedence(c) > 0) {         // a real operator
                // pop what binds tighter, or equally tight unless c is the right-associative '^'
                while (!ops.isEmpty() && ops.peek() != '('
                        && (precedence(ops.peek()) > precedence(c)
                            || (precedence(ops.peek()) == precedence(c) && c != '^'))) {
                    out.append(ops.pop());          // it binds at least as tightly and came first
                }
                ops.push(c);                        // c waits on the stack for its right operand
            } else {
                throw new IllegalArgumentException("unexpected '" + c + "' at " + i);
            }
        }
        while (!ops.isEmpty()) {                    // flush the operators that are left
            if (ops.peek() == '(') throw new IllegalArgumentException("unmatched '('");
            out.append(ops.pop());
        }
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String infix, String expected) {
        check(recursiveDescent(infix).equals(expected), "recursiveDescent(" + infix + ") = " + recursiveDescent(infix) + ", expected " + expected);
        check(optimal(infix).equals(expected), "optimal(" + infix + ") = " + optimal(infix) + ", expected " + expected);
    }

    static boolean rejects(String infix) {
        boolean a = false, b = false;
        try {
            recursiveDescent(infix);
        } catch (IllegalArgumentException e) {
            a = true;
        }
        try {
            optimal(infix);
        } catch (IllegalArgumentException e) {
            b = true;
        }
        return a && b;
    }

    public static void main(String[] args) {
        verify("a+b*(c^d-e)^(f+g*h)-i", "abcd^e-fgh*+^*+i-");   // the classic textbook example
        verify("(A+B)*(C-D)", "AB+CD-*");
        verify("A*(B+C)/D", "ABC+*D/");
        verify("a+b*c", "abc*+");                               // * binds tighter than +
        verify("a*b+c", "ab*c+");
        verify("a-b-c", "ab-c-");                               // left-associative: (a-b)-c
        verify("a^b^c", "abc^^");                               // right-associative: a^(b^c)
        verify("a^b*c", "ab^c*");
        verify("(a+b)^(c-d)^e", "ab+cd-e^^");
        verify("1+2*3", "123*+");                               // digits are operands too
        verify("a", "a");                                       // edge: a single operand
        verify("((a))", "a");                                   // edge: redundant parentheses vanish
        verify("(a+b)", "ab+");
        check(rejects("(a+b"), "unmatched '(' must be rejected");
        check(rejects("a+b)"), "unmatched ')' must be rejected");
        check(rejects(""), "empty input must be rejected");
        System.out.println("OK P586_InfixToPostfixConversion");
    }
}
