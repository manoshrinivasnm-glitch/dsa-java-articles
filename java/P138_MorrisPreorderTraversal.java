import java.util.*;

/** TUF 138 - Morris Preorder Traversal. Return the preorder sequence of a binary tree using O(1) extra space. */
public class P138_MorrisPreorderTraversal {

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

    /** Approach 2: explicit stack, right child pushed before left. O(n) time, O(h) space. */
    static List<Integer> iterative(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            out.add(node.val);
            if (node.right != null) stack.push(node.right);
            if (node.left != null) stack.push(node.left);
        }
        return out;
    }

    /** Approach 3: Morris traversal; record a node on its first arrival. O(n) time, O(1) extra space. */
    static List<Integer> morris(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) {                  // no left subtree: visit and move right
                out.add(cur.val);
                cur = cur.right;                     // real child or a thread back up
                continue;
            }
            TreeNode pred = cur.left;                // rightmost node of the left subtree
            while (pred.right != null && pred.right != cur) pred = pred.right;
            if (pred.right == null) {                // first arrival: preorder visits the node now
                out.add(cur.val);
                pred.right = cur;                    // thread to find the way back after the left subtree
                cur = cur.left;
            } else {                                 // second arrival: already visited, just clean up
                pred.right = null;
                cur = cur.right;
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

    /** Tree whose preorder is lo, lo + 1, ..., hi: root lo, left half and right half of the rest, recursively. */
    static TreeNode preorderNumbered(int lo, int hi) {
        if (lo > hi) return null;
        TreeNode node = new TreeNode(lo);
        int leftSize = (hi - lo) / 2;
        node.left = preorderNumbered(lo + 1, lo + leftSize);
        node.right = preorderNumbered(lo + leftSize + 1, hi);
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
        check(iterative(tree).equals(expected), "iterative " + label);
        check(morris(tree).equals(expected), "morris " + label);
        check(same(tree, original), "morris must leave the tree unchanged " + label);
        check(morris(tree).equals(expected), "morris second run " + label);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, null, 6), list(1, 2, 4, 5, 3, 6), "example 1");
        verify(build(1, null, 2, 3), list(1, 2, 3), "example 2");
        verify(build(), list(), "empty tree");                                         // edge case
        verify(build(9), list(9), "single node");
        verify(build(1, 2, null, 3, null, 4), list(1, 2, 3, 4), "left chain");
        verify(build(1, null, 2, null, 3), list(1, 2, 3), "right chain");
        verify(build(1, 2, 3, 4, 5, 6, 7, null, null, 8, 9), list(1, 2, 4, 5, 8, 9, 3, 6, 7), "nested threads");
        verify(build(2, 2, 2, -1), list(2, 2, -1, 2), "duplicates and negatives");

        int n = 100_000;
        List<Integer> expected = new ArrayList<>();
        for (int v = 1; v <= n; v++) expected.add(v);
        verify(preorderNumbered(1, n), expected, "balanced tree of 100000 nodes");
        System.out.println("OK P138_MorrisPreorderTraversal");
    }
}
