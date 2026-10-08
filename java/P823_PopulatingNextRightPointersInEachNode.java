import java.util.*;

/** TUF 823 - Populating Next Right Pointers in Each Node. In a perfect binary tree, point every node's next to its right neighbour on the same level. */
public class P823_PopulatingNextRightPointersInEachNode {

    static class Node {
        int val;
        Node left, right, next;
        Node(int val) { this.val = val; }
    }

    /** Approach 1: level order traversal with a queue; link each node to the one dequeued after it. O(n) time, O(n) space. */
    static Node levelOrder(Node root) {
        if (root == null) return null;
        Deque<Node> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            Node prev = null;
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                if (prev != null) prev.next = node;          // the last node of the level keeps next == null
                prev = node;
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
        }
        return root;
    }

    /** Approach 2: recursion; a node wires its own children, using its next pointer for the gap between cousins. O(n) time, O(log n) stack. */
    static Node recursive(Node root) {
        connect(root);
        return root;
    }

    static void connect(Node node) {
        if (node == null || node.left == null) return;      // perfect tree: no left child means a leaf
        node.left.next = node.right;                         // siblings
        if (node.next != null) node.right.next = node.next.left;   // cousins across the parent boundary
        connect(node.left);
        connect(node.right);
    }

    /** Approach 3: walk each level as a linked list (already built) and wire the level below. O(n) time, O(1) extra space. */
    static Node optimal(Node root) {
        Node leftmost = root;
        while (leftmost != null && leftmost.left != null) {
            for (Node cur = leftmost; cur != null; cur = cur.next) {
                cur.left.next = cur.right;
                if (cur.next != null) cur.right.next = cur.next.left;
            }
            leftmost = leftmost.left;                        // first node of the next level
        }
        return root;
    }

    // ---------------------------------------------------------------- helpers
    /** Perfect tree with n = 2^d - 1 nodes valued 1..n in level order (node k has children 2k and 2k + 1). */
    static Node perfect(int n) {
        Node[] nodes = new Node[n + 1];
        for (int k = 1; k <= n; k++) nodes[k] = new Node(k);
        for (int k = 1; 2 * k + 1 <= n; k++) {
            nodes[k].left = nodes[2 * k];
            nodes[k].right = nodes[2 * k + 1];
        }
        return n == 0 ? null : nodes[1];
    }

    /** LeetCode serialization: each level read through next pointers, followed by '#'. */
    static String serialize(Node root) {
        StringJoiner sj = new StringJoiner(",");
        for (Node level = root; level != null; level = level.left) {
            for (Node cur = level; cur != null; cur = cur.next) sj.add(String.valueOf(cur.val));
            sj.add("#");
        }
        return sj.toString();
    }

    /** Expected serialization for perfect(n): levels 1, 2-3, 4-7, ... each closed with '#'. */
    static String expected(int n) {
        StringJoiner sj = new StringJoiner(",");
        for (int start = 1; start <= n; start *= 2) {
            for (int v = start; v < 2 * start; v++) sj.add(String.valueOf(v));
            sj.add("#");
        }
        return sj.toString();
    }

    static boolean noNextPointers(Node node) {
        if (node == null) return true;
        return node.next == null && noNextPointers(node.left) && noNextPointers(node.right);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        Node a = perfect(n), b = perfect(n), c = perfect(n);
        check(noNextPointers(a), "fresh tree must have no next pointers");
        check(levelOrder(a) == a && serialize(a).equals(expected), "levelOrder n=" + n);
        check(recursive(b) == b && serialize(b).equals(expected), "recursive n=" + n);
        check(optimal(c) == c && serialize(c).equals(expected), "optimal n=" + n);
    }

    public static void main(String[] args) {
        verify(7, "1,#,2,3,#,4,5,6,7,#");                         // example 1
        verify(0, "");                                             // edge case: empty tree
        verify(1, "1,#");                                          // single node
        verify(3, "1,#,2,3,#");
        verify(15, "1,#,2,3,#,4,5,6,7,#,8,9,10,11,12,13,14,15,#");
        for (int d = 1; d <= 17; d++) {
            int n = (1 << d) - 1;
            verify(n, expected(n));                                 // up to 131071 nodes
        }
        System.out.println("OK P823_PopulatingNextRightPointersInEachNode");
    }
}
