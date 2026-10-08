import java.util.*;

/** TUF 127 - Check for balanced binary tree. A tree is balanced when, at every node, the heights of the two subtrees differ by at most 1. */
public class P127_CheckForBalancedBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Height in nodes: 0 for an empty tree, 1 for a single node. O(size of subtree). */
    static int height(TreeNode node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /** Approach 1: brute force. At every node compare the heights of its two subtrees, then check both children the same way. O(n^2) worst case, O(h) stack. */
    static boolean bruteForce(TreeNode root) {
        if (root == null) return true;
        if (Math.abs(height(root.left) - height(root.right)) > 1) return false;
        return bruteForce(root.left) && bruteForce(root.right);
    }

    /** Approach 2: one post-order pass. Return the height of a balanced subtree, or -1 the moment any subtree is unbalanced. O(n) time, O(h) stack. */
    static boolean optimal(TreeNode root) {
        return checkHeight(root) != -1;
    }

    static int checkHeight(TreeNode node) {
        if (node == null) return 0;
        int lh = checkHeight(node.left);
        if (lh == -1) return -1;
        int rh = checkHeight(node.right);
        if (rh == -1) return -1;
        if (Math.abs(lh - rh) > 1) return -1;
        return 1 + Math.max(lh, rh);
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode build(Integer[] a) {
        if (a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Deque<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < a.length) {
            TreeNode node = q.poll();
            if (i < a.length && a[i] != null) { node.left = new TreeNode(a[i]); q.add(node.left); }
            i++;
            if (i < a.length && a[i] != null) { node.right = new TreeNode(a[i]); q.add(node.right); }
            i++;
        }
        return root;
    }

    static TreeNode randomTree(Random rnd, int depth) {
        if (depth == 0 || rnd.nextInt(3) == 0) return null;
        TreeNode node = new TreeNode(rnd.nextInt(100));
        node.left = randomTree(rnd, depth - 1);
        node.right = randomTree(rnd, depth - 1);
        return node;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] level, boolean expected) {
        TreeNode root = build(level);
        String s = Arrays.toString(level);
        check(bruteForce(root) == expected, "bruteForce " + s);
        check(optimal(root) == expected, "optimal " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{3, 9, 20, null, null, 15, 7}, true);                    // heights 1 and 2 at the root
        verify(new Integer[]{1, 2, 2, 3, 3, null, null, 4, 4}, false);               // root: left height 3, right height 1
        verify(new Integer[]{}, true);                                               // edge: empty tree is balanced
        verify(new Integer[]{1}, true);                                              // single node
        verify(new Integer[]{1, 2, 2, 3, null, null, 3, 4, null, null, 4}, false);   // root looks fine (3 vs 3) but both children are not
        verify(new Integer[]{1, 2, 3, 4, 5, 6, null, 8}, true);
        verify(new Integer[]{1, null, 2, null, 3}, false);                           // right-skewed chain of 3
        verify(new Integer[]{1, 2}, true);                                           // difference of exactly 1 is allowed

        // seeded random trees: both methods must agree
        Random rnd = new Random(127);
        int balanced = 0;
        for (int t = 0; t < 500; t++) {
            TreeNode root = randomTree(rnd, 6);
            boolean b = bruteForce(root);
            check(optimal(root) == b, "random tree #" + t);
            if (b) balanced++;
        }
        check(balanced > 0 && balanced < 500, "random trees should include both outcomes");
        System.out.println("OK P127_CheckForBalancedBinaryTree");
    }
}
