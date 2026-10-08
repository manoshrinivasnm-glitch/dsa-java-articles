import java.util.*;

/** TUF 825 - Postfix to Prefix Conversion. Operands are single letters or digits; every other character is a binary operator. */
public class P825_PostfixToPrefixConversion {

    static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    /** Approach 1: scan left to right with a stack of finished prefix pieces. O(n^2) worst-case time (string copies), O(n) space. */
    static String stackOfStrings(String postfix) {
        Deque<String> st = new ArrayDeque<>();
        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            if (isOperand(c)) {
                st.push(String.valueOf(c));
            } else {
                String right = st.pop();             // pushed last, so it is the right operand
                String left = st.pop();
                st.push(c + left + right);
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

    /** Approach 2: build the expression tree once, then emit its preorder traversal. O(n) time, O(n) space. */
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
        StringBuilder out = new StringBuilder(postfix.length());
        Deque<Node> todo = new ArrayDeque<>();
        todo.push(st.pop());
        while (!todo.isEmpty()) {                    // iterative preorder: node, left subtree, right subtree
            Node node = todo.pop();
            out.append(node.symbol);
            if (node.right != null) todo.push(node.right);   // pushed first, so it is visited after the left
            if (node.left != null) todo.push(node.left);
        }
        return out.toString();
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
        verify("AB+CD-*", "*+AB-CD");
        verify("ABC/-AK/L-*", "*-A/BC-/AKL");
        verify("ab+c*", "*+abc");
        verify("abc*+d-", "-+a*bcd");
        verify("ab^c^", "^^abc");                      // (a^b)^c
        verify("abc^^", "^a^bc");                      // a^(b^c)
        verify("12+", "+12");                          // digits are operands too
        verify("A", "A");                              // a lone operand
        verify("", "");                                // empty input
        int k = 1000;                                  // deep left-leaning chain
        verify("a" + "a+".repeat(k), "+".repeat(k) + "a".repeat(k + 1));
        System.out.println("OK P825_PostfixToPrefixConversion");
    }
}
