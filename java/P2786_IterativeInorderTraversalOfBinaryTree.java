import java.util.*;

/** TUF 2786 - Iterative Inorder Traversal of Binary Tree. Return the inorder (Left, Root, Right) sequence without recursion. */
public class P2786_IterativeInorderTraversalOfBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** A stack entry for the flag-based traversal: ready == true means "the left subtree is done, emit this node". */
    record Entry(TreeNode node, boolean ready) {}

    /** Approach 1: recursion, the reference answer the iterative versions must reproduce. O(n) time, O(h) call stack. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    private static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);                  // Left
        out.add(node.val);                        // Root
        inorder(node.right, out);                 // Right
    }

    /** Approach 2: the classic iterative version; stack the left spine, pop, visit, move right. O(n) time, O(h) space. */
    static List<Integer> iterative(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            if (cur != null) {
                stack.push(cur);                  // postpone cur until its left subtree is finished
                cur = cur.left;
            } else {
                cur = stack.pop();                // left subtree of this node is finished
                out.add(cur.val);
                cur = cur.right;                  // continue with the right subtree
            }
        }
        return out;
    }

    /** Approach 3: simulate the recursion with flagged entries; expand a node into Right, itself (ready), Left. O(n) time, O(h) space. */
    static List<Integer> iterativeWithFlags(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<Entry> stack = new ArrayDeque<>();
        stack.push(new Entry(root, false));
        while (!stack.isEmpty()) {
            Entry e = stack.pop();
            TreeNode node = e.node();
            if (e.ready()) {
                out.add(node.val);                // everything to its left has already been emitted
                continue;
            }
            // push in reverse of the wanted order, so they pop as Left, Node, Right
            if (node.right != null) stack.push(new Entry(node.right, false));
            stack.push(new Entry(node, true));
            if (node.left != null) stack.push(new Entry(node.left, false));
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Build a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int i = 1;
        while (!queue.isEmpty() && i < vals.length) {
            TreeNode node = queue.poll();
            if (i < vals.length && vals[i] != null) { node.left = new TreeNode(vals[i]); queue.offer(node.left); }
            i++;
            if (i < vals.length && vals[i] != null) { node.right = new TreeNode(vals[i]); queue.offer(node.right); }
            i++;
        }
        return root;
    }

    /** A chain of n nodes valued 1..n, each the left (or right) child of the previous one. Built without recursion. */
    static TreeNode chain(int n, boolean leftward) {
        TreeNode root = new TreeNode(1), cur = root;
        for (int v = 2; v <= n; v++) {
            TreeNode next = new TreeNode(v);
            if (leftward) cur.left = next; else cur.right = next;
            cur = next;
        }
        return root;
    }

    static List<Integer> range(int from, int to) {
        List<Integer> out = new ArrayList<>();
        if (from <= to) {
            for (int v = from; v <= to; v++) out.add(v);
        } else {
            for (int v = from; v >= to; v--) out.add(v);
        }
        return out;
    }

    static void verify(TreeNode root, List<Integer> expected) {
        List<Integer> r1 = recursive(root), r2 = iterative(root), r3 = iterativeWithFlags(root);
        check(r1.equals(expected), "recursive gave " + r1 + ", expected " + expected);
        check(r2.equals(expected), "iterative gave " + r2 + ", expected " + expected);
        check(r3.equals(expected), "iterativeWithFlags gave " + r3 + ", expected " + expected);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, 6, 7), List.of(4, 2, 5, 1, 6, 3, 7));
        verify(build(1, 2, 3, null, 4, 5, null, null, null, 6), List.of(2, 4, 1, 6, 5, 3));
        verify(build(1, null, 2, 3), List.of(1, 3, 2));
        verify(build(8, 3, 10, 1, 6, null, 14, null, null, 4, 7, 13), List.of(1, 3, 4, 6, 7, 8, 10, 13, 14)); // BST: sorted
        verify(build(), List.of());                                          // edge: empty tree
        verify(build(42), List.of(42));                                      // single node
        verify(build(1, 2, null, 3, null, 4), List.of(4, 3, 2, 1));          // left-skewed

        int n = 100_000;                                                     // deep trees: no call stack involved
        check(iterative(chain(n, true)).equals(range(n, 1)), "iterative on a deep left chain");
        check(iterative(chain(n, false)).equals(range(1, n)), "iterative on a deep right chain");
        check(iterativeWithFlags(chain(n, true)).equals(range(n, 1)), "iterativeWithFlags on a deep left chain");
        check(iterativeWithFlags(chain(n, false)).equals(range(1, n)), "iterativeWithFlags on a deep right chain");
        System.out.println("OK P2786_IterativeInorderTraversalOfBinaryTree");
    }
}
