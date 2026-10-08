import java.util.*;

/** TUF 62 - Binary Tree to Doubly Linked List. In place: left = previous, right = next, nodes in inorder; return the head. */
public class P62_BinaryTreeToDoublyLinkedList {

    static class Node {
        int data;
        Node left, right;
        Node(int data) { this.data = data; }
    }

    /** Approach 1: brute force, record the inorder sequence of nodes, then relink them. O(n) time, O(n) space. */
    static Node bruteForce(Node root) {
        List<Node> order = new ArrayList<>();
        collectInorder(root, order);
        for (int i = 0; i < order.size(); i++) {
            Node node = order.get(i);
            node.left = i > 0 ? order.get(i - 1) : null;
            node.right = i + 1 < order.size() ? order.get(i + 1) : null;
        }
        return order.isEmpty() ? null : order.get(0);
    }

    static void collectInorder(Node node, List<Node> order) {
        if (node == null) return;
        collectInorder(node.left, order);
        order.add(node);
        collectInorder(node.right, order);
    }

    /** Approach 2: better, inorder recursion that links every node to the previously visited one. O(n) time, O(h) stack. */
    static Node better(Node root) {
        Node[] state = new Node[2];                         // state[0] = head, state[1] = prev (last node linked)
        link(root, state);
        return state[0];
    }

    static void link(Node node, Node[] state) {
        if (node == null) return;
        link(node.left, state);                             // the whole left subtree is linked before node
        Node prev = state[1];
        node.left = prev;                                   // safe: node.left is no longer needed
        if (prev == null) state[0] = node; else prev.right = node;
        state[1] = node;
        link(node.right, state);                            // node.right was read before anyone overwrote it
    }

    /** Approach 3: optimal, right-rotate the tree into an inorder "vine", then fill in back links. O(n) time, O(1) extra space. */
    static Node optimal(Node root) {
        Node dummy = new Node(0);                           // dummy.right is always the head of the vine
        dummy.right = root;
        Node tail = dummy;                                  // last node already in its final position
        Node rest = root;                                   // subtree still to be straightened
        while (rest != null) {
            if (rest.left == null) {                        // nothing in this subtree is smaller: rest is next
                tail = rest;
                rest = rest.right;
            } else {                                        // right rotation: the left child moves up
                Node l = rest.left;
                rest.left = l.right;
                l.right = rest;
                rest = l;
                tail.right = l;
            }
        }
        Node prev = null;
        for (Node cur = dummy.right; cur != null; cur = cur.right) {
            cur.left = prev;                                // second pass: add the backward links
            prev = cur;
        }
        return dummy.right;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, null meaning "no child". */
    static Node build(Integer[] level) {
        if (level.length == 0 || level[0] == null) return null;
        Node root = new Node(level[0]);
        ArrayDeque<Node> queue = new ArrayDeque<>();
        queue.offer(root);
        int i = 1;
        while (!queue.isEmpty() && i < level.length) {
            Node cur = queue.poll();
            if (i < level.length && level[i] != null) { cur.left = new Node(level[i]); queue.offer(cur.left); }
            i++;
            if (i < level.length && level[i] != null) { cur.right = new Node(level[i]); queue.offer(cur.right); }
            i++;
        }
        return root;
    }

    static void allNodes(Node node, Set<Node> out) {
        if (node == null) return;
        out.add(node);
        allNodes(node.left, out);
        allNodes(node.right, out);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Checks that head is a valid doubly linked list holding expected, made of exactly the original nodes. */
    static void checkList(Node head, int[] expected, Set<Node> original, String name) {
        if (expected.length == 0) { check(head == null, name + ": expected empty list"); return; }
        check(head != null && head.left == null, name + ": head must exist and have no previous node");
        Node prev = null, cur = head;
        for (int i = 0; i < expected.length; i++) {
            check(cur != null, name + ": list too short at " + i);
            check(cur.data == expected[i], name + ": value " + cur.data + " at " + i + ", expected " + expected[i]);
            check(cur.left == prev, name + ": broken back link at " + i);
            check(original.contains(cur), name + ": node was not part of the original tree");
            prev = cur;
            cur = cur.right;
        }
        check(cur == null, name + ": list longer than expected");
    }

    interface Converter { Node convert(Node root); }

    static void verify(Integer[] level, int[] expectedInorder) {
        Map<String, Converter> approaches = new LinkedHashMap<>();
        approaches.put("bruteForce", P62_BinaryTreeToDoublyLinkedList::bruteForce);
        approaches.put("better", P62_BinaryTreeToDoublyLinkedList::better);
        approaches.put("optimal", P62_BinaryTreeToDoublyLinkedList::optimal);
        for (Map.Entry<String, Converter> e : approaches.entrySet()) {
            Node root = build(level);                       // fresh tree: every approach mutates it
            Set<Node> original = Collections.newSetFromMap(new IdentityHashMap<>());
            allNodes(root, original);
            checkList(e.getValue().convert(root), expectedInorder, original, e.getKey());
        }
    }

    public static void main(String[] args) {
        verify(new Integer[]{10, 12, 15, 25, 30, 36}, new int[]{25, 12, 30, 10, 36, 15});
        verify(new Integer[]{1, 3, 2}, new int[]{3, 1, 2});
        verify(new Integer[]{}, new int[]{});                                   // empty tree
        verify(new Integer[]{7}, new int[]{7});                                  // single node
        verify(new Integer[]{5, 4, null, 3, null, 2}, new int[]{2, 3, 4, 5});     // left-skewed
        verify(new Integer[]{1, null, 2, null, 3}, new int[]{1, 2, 3});           // right-skewed
        verify(new Integer[]{4, 2, 6, 1, 3, 5, 7}, new int[]{1, 2, 3, 4, 5, 6, 7});
        verify(new Integer[]{1, 2, null, null, 3, 4, null, null, 5}, new int[]{2, 4, 5, 3, 1}); // zig-zag
        verify(new Integer[]{2, 2, 2, 2, null, null, 2}, new int[]{2, 2, 2, 2, 2});  // duplicate values

        // a larger BST built from a seeded sequence: inorder must be sorted
        Random rng = new Random(62);
        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < 400; i++) keys.add(i);
        Collections.shuffle(keys, rng);
        int[] sorted = new int[400];
        for (int i = 0; i < 400; i++) sorted[i] = i;
        Converter[] all = {P62_BinaryTreeToDoublyLinkedList::bruteForce, P62_BinaryTreeToDoublyLinkedList::better,
                           P62_BinaryTreeToDoublyLinkedList::optimal};
        for (Converter c : all) {
            Node root = null;
            for (int key : keys) root = insertBst(root, key);
            Set<Node> original = Collections.newSetFromMap(new IdentityHashMap<>());
            allNodes(root, original);
            checkList(c.convert(root), sorted, original, "bst-400");
        }
        System.out.println("OK P62_BinaryTreeToDoublyLinkedList");
    }

    static Node insertBst(Node root, int key) {
        if (root == null) return new Node(key);
        if (key < root.data) root.left = insertBst(root.left, key); else root.right = insertBst(root.right, key);
        return root;
    }
}
