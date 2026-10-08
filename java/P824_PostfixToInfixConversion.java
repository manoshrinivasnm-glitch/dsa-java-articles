import java.util.*;

/** TUF 824 - Postfix to Infix Conversion. Produce the fully parenthesised infix form of a postfix expression. */
public class P824_PostfixToInfixConversion {

    static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    /** Approach 1: scan left to right with a stack of finished infix pieces. O(n^2) worst-case time (string copies), O(n) space. */
    static String stackOfStrings(String postfix) {
        Deque<String> st = new ArrayDeque<>();
        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            if (isOperand(c)) {
                st.push(String.valueOf(c));
            } else {
                String right = st.pop();             // pushed last, so it is the right operand
                String left = st.pop();
                st.push("(" + left + c + right + ")");
            }
        }
        return st.isEmpty() ? "" : st.pop();
    }

    /** Expression-tree node: an operand leaf, or an operator with two children. */
    static final class Node {
        final char symbol;
        final Node left, right;

        Node(char symbol, Node left, Node right) {
            this.symbol = symbol;
            this.left = left;
            this.right = right;
        }
    }

    /** Approach 2: build the expression tree, then write a bracketed inorder traversal into one StringBuilder. O(n) time, O(n) space. */
    static String expressionTree(String postfix) {
        if (postfix.isEmpty()) return "";
        Deque<Node> st = new ArrayDeque<>();
        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            if (isOperand(c)) {
                st.push(new Node(c, null, null));
            } else {
                Node right = st.pop();
                Node left = st.pop();
                st.push(new Node(c, left, right));
            }
        }
        StringBuilder out = new StringBuilder();
        inorder(st.pop(), out);
        return out.toString();
    }

    static void inorder(Node node, StringBuilder out) {
        if (node.left == null) {                     // a leaf is an operand
            out.append(node.symbol);
            return;
        }
        out.append('(');
        inorder(node.left, out);
        out.append(node.symbol);
        inorder(node.right, out);
        out.append(')');
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String postfix, String expected) {
        check(expected.equals(stackOfStrings(postfix)), "stackOfStrings failed on " + postfix);
        check(expected.equals(expressionTree(postfix)), "expressionTree failed on " + postfix);
    }

    public static void main(String[] args) {
        verify("ab*c+", "((a*b)+c)");
        verify("abc++", "(a+(b+c))");
        verify("AB+CD-*", "((A+B)*(C-D))");
        verify("ABC/-AK/L-*", "((A-(B/C))*((A/K)-L))");
        verify("ab+", "(a+b)");
        verify("12^3-", "((1^2)-3)");                  // digits are operands too
        verify("a", "a");                              // a lone operand gets no brackets
        verify("", "");                                // empty input
        int k = 1000;                                  // deep left-leaning chain
        verify("a" + "a+".repeat(k), "(".repeat(k) + "a" + "+a)".repeat(k));
        System.out.println("OK P824_PostfixToInfixConversion");
    }
}
