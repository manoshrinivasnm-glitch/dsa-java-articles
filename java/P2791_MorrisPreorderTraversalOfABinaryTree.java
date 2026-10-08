import java.util.*;

/** TUF 2791 - Morris Preorder Traversal of a Binary Tree. Return the preorder sequence using O(1) extra space. */
public class P2791_MorrisPreorderTraversalOfABinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion. O(n) time, O(h) call-stack space. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        preorder(root, out);
        return out;
    }

    static void preorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        out.add(node.val);
        preorder(node.left, out);
        preorder(node.right, out);
    }

    /** Approach 2: explicit stack. O(n) time, O(h) space. */
    static List<Integer> iterativeStack(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            out.add(cur.val);
            if (cur.right != null) stack.push(cur.right);       // pushed first so the left child is popped first
            if (cur.left != null) stack.push(cur.left);
        }
        return out;
    }

    /** Approach 3: Morris traversal, temporary threads replace the stack. O(n) time, O(1) extra space. */
    static List<Integer> morris(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) {
                out.add(cur.val);
                cur = cur.right;
            } else {
                TreeNode pred = cur.left;                        // rightmost node of the left subtree
                while (pred.right != null && pred.right != cur) pred = pred.right;
                if (pred.right == null) {                        // first arrival: visit, leave a thread back, go left
                    out.add(cur.val);
                    pred.right = cur;
                    cur = cur.left;
                } else {                                         // second arrival via the thread: left side is done
                    pred.right = null;
                    cur = cur.right;
                }
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order; null means "no node here". */
    static TreeNode build(Integer... level) {
        if (level.length == 0 || level[0] == null) return null;
        TreeNode root = new TreeNode(level[0]);
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < level.length) {
            TreeNode cur = queue.poll();
            if (i < level.length && level[i] != null) {
                cur.left = new TreeNode(level[i]);
                queue.add(cur.left);
            }
            i++;
            if (i < level.length && level[i] != null) {
                cur.right = new TreeNode(level[i]);
                queue.add(cur.right);
            }
            i++;
        }
        return root;
    }

    /** Balanced tree over lo..hi; nodes are created in preorder, so their values are recorded as the expected answer. */
    static TreeNode balanced(int lo, int hi, List<Integer> creationOrder) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;
        TreeNode node = new TreeNode(mid);
        creationOrder.add(mid);
        node.left = balanced(lo, mid - 1, creationOrder);
        node.right = balanced(mid + 1, hi, creationOrder);
        return node;
    }

    static boolean same(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && same(a.left, b.left) && same(a.right, b.right);
    }

    static TreeNode copy(TreeNode node) {
        if (node == null) return null;
        TreeNode c = new TreeNode(node.val);
        c.left = copy(node.left);
        c.right = copy(node.right);
        return c;
    }

    static List<Integer> list(int... values) {
        List<Integer> out = new ArrayList<>();
        for (int v : values) out.add(v);
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode tree, List<Integer> expected, String label) {
        TreeNode original = copy(tree);
        check(recursive(tree).equals(expected), "recursive " + label);
        check(iterativeStack(tree).equals(expected), "iterativeStack " + label);
        check(morris(tree).equals(expected), "morris " + label);
        check(same(tree, original), "morris must restore the tree " + label);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, null, 6), list(1, 2, 4, 5, 3, 6), "example");
        verify(build(1, null, 2, 3), list(1, 2, 3), "LeetCode example");
        verify(build(1, 2, 3, 4, 5, 6, 7, null, null, 8, 9), list(1, 2, 4, 5, 8, 9, 3, 6, 7), "nested threads");
        verify(build(7), list(7), "single node");
        verify(build(), list(), "empty tree");                                   // edge case
        verify(build(1, 2, null, 3, null, 4), list(1, 2, 3, 4), "left chain");
        verify(build(1, null, 2, null, 3), list(1, 2, 3), "right chain");
        verify(build(5, 5, 5), list(5, 5, 5), "duplicate values");

        List<Integer> expected = new ArrayList<>();
        TreeNode big = balanced(1, 100_000, expected);
        verify(big, expected, "balanced tree of 100000 nodes");
        System.out.println("OK P2791_MorrisPreorderTraversalOfABinaryTree");
    }
}
