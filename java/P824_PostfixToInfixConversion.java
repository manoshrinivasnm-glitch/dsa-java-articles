import java.util.*;

/** TUF 824 - Postfix to Infix Conversion. Produce the fully parenthesised infix form of a postfix expression. */
public class P824_PostfixToInfixConversion {

    static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    /** Approach 1: scan left to right with a stack of finished infix pieces. O(n^2) worst-case time (string copies), O(n) space. */
    static String stackOfStrings(String postfix) {
        Deque<String> st = new ArrayDeque<>();           // infix forms of the sub-expressions finished so far
        // left to right: operands arrive before their operator
        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            if (isOperand(c)) {                          // a single operand is already valid infix
                st.push(String.valueOf(c));
            } else {
                String right = st.pop();                 // pushed last, so it is the right operand
                String left = st.pop();                  // pushed earlier, so it is the left operand
                st.push("(" + left + c + right + ")");   // operator goes between, wrapped in brackets
            }
        }
        return st.isEmpty() ? "" : st.pop();             // the one piece left is the whole expression
    }

    /** Expression-tree node: an operand leaf, or an operator with two children. */
    static final class Node {
        final char symbol;                             // the operator, or the operand letter for a leaf
        final Node left, right;                        // both null for an operand leaf

        Node(char symbol, Node left, Node right) {
            this.symbol = symbol;
            this.left = left;
            this.right = right;
        }
    }

    /** Approach 2: build the expression tree, then write a bracketed inorder traversal into one StringBuilder. O(n) time, O(n) space. */
    static String expressionTree(String postfix) {
        if (postfix.isEmpty()) return "";              // empty input converts to empty output
        Deque<Node> st = new ArrayDeque<>();           // roots of the subtrees built so far
        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            if (isOperand(c)) {                        // an operand becomes a leaf
                st.push(new Node(c, null, null));
            } else {
                Node right = st.pop();                 // the most recent subtree is the right child
                Node left = st.pop();                  // the one before it is the left child
                st.push(new Node(c, left, right));     // the operator joins them into one subtree
            }
        }
        StringBuilder out = new StringBuilder();       // collects the infix output
        inorder(st.pop(), out);                        // the last node left is the root of the whole tree
        return out.toString();
    }

    static void inorder(Node node, StringBuilder out) {
        if (node.left == null) {                     // a leaf is an operand
            out.append(node.symbol);                 // an operand is written without brackets
            return;
        }
        out.append('(');                             // every operator gets its own pair of brackets
        inorder(node.left, out);                     // left operand first
        out.append(node.symbol);                     // then the operator
        inorder(node.right, out);                    // then the right operand
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
