import java.util.*;

/** TUF 133 - Inorder Traversal. Return the values of a binary tree in inorder: left subtree, node, right subtree. */
public class P133_InorderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion. O(n) time, O(h) call-stack space. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);       // 1. everything smaller in a BST, everything "before" in general
        out.add(node.val);             // 2. the node itself
        inorder(node.right, out);      // 3. everything after
    }

    /** Approach 2: explicit stack that plays the role of the call stack. O(n) time, O(h) space. */
    static List<Integer> iterative(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {          // push the whole left spine; each node waits for its left subtree
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();             // its left subtree is finished, so visit it
            out.add(cur.val);
            cur = cur.right;               // then traverse its right subtree the same way
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

    /** Chain where every node only has a left child: root n, then n - 1, ..., 1. Inorder is 1..n. */
    static TreeNode leftChain(int n) {
        TreeNode root = null;
        for (int v = 1; v <= n; v++) {
            TreeNode node = new TreeNode(v);
            node.left = root;
            root = node;
        }
        return root;
    }

    /** Balanced search tree over lo..hi, so its inorder sequence is lo, lo + 1, ..., hi. */
    static TreeNode balanced(int lo, int hi) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;
        TreeNode node = new TreeNode(mid);
        node.left = balanced(lo, mid - 1);
        node.right = balanced(mid + 1, hi);
        return node;
    }

    static List<Integer> list(int... values) {
        List<Integer> out = new ArrayList<>();
        for (int v : values) out.add(v);
        return out;
    }

    static List<Integer> range(int lo, int hi) {
        List<Integer> out = new ArrayList<>();
        for (int v = lo; v <= hi; v++) out.add(v);
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode tree, List<Integer> expected, String label) {
        check(recursive(tree).equals(expected), "recursive " + label);
        check(iterative(tree).equals(expected), "iterative " + label);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, null, 6), list(4, 2, 5, 1, 3, 6), "example 1");
        verify(build(1, null, 2, 3), list(1, 3, 2), "example 2");
        verify(build(), list(), "empty tree");                                     // edge case
        verify(build(7), list(7), "single node");
        verify(build(1, 2, null, 3, null, 4), list(4, 3, 2, 1), "left chain");
        verify(build(1, null, 2, null, 3), list(1, 2, 3), "right chain");
        verify(build(0, -5, 5, -5, 0), list(-5, -5, 0, 0, 5), "duplicates and negatives");
        verify(leftChain(5_000), range(1, 5_000), "deep left chain of 5000 nodes");
        verify(balanced(1, 100_000), range(1, 100_000), "balanced tree of 100000 nodes");
        System.out.println("OK P133_InorderTraversal");
    }
}
