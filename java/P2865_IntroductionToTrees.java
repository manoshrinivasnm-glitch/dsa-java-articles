import java.util.*;

/** TUF 2865 - Introduction to Trees. Tree vocabulary, the binary-tree node, measurements, and checks for the named kinds of binary tree. */
public class P2865_IntroductionToTrees {

    /** A binary-tree node: one value and references to at most two children (null when a child is absent). */
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /** The example tree from the article, built by hand with nested constructors. */
    static TreeNode exampleTree() {
        return new TreeNode(1,
                new TreeNode(2, new TreeNode(4), new TreeNode(5, new TreeNode(7), null)),
                new TreeNode(3, null, new TreeNode(6)));
    }

    /** Number of nodes: the root plus the sizes of its two subtrees. O(n) time, O(h) call stack. */
    static int size(TreeNode root) {
        if (root == null) return 0;
        return 1 + size(root.left) + size(root.right);
    }

    /** Number of edges: every non-root node has exactly one edge to its parent, so this is always size - 1 for a non-empty tree. */
    static int countEdges(TreeNode root) {
        if (root == null) return 0;
        int here = (root.left != null ? 1 : 0) + (root.right != null ? 1 : 0);
        return here + countEdges(root.left) + countEdges(root.right);
    }

    /** Height counted in levels (nodes on the longest root-to-leaf path): empty tree 0, single node 1. O(n) time. */
    static int height(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(height(root.left), height(root.right));
    }

    /** Depth of the first node holding target (the root has depth 0), or -1 when the value is absent. O(n) time. */
    static int depthOf(TreeNode root, int target) {
        if (root == null) return -1;
        if (root.val == target) return 0;
        int d = depthOf(root.left, target);
        if (d == -1) d = depthOf(root.right, target);
        return d == -1 ? -1 : d + 1;
    }

    /** A leaf is a node with no children. O(n) time. */
    static int countLeaves(TreeNode root) {
        if (root == null) return 0;
        if (root.left == null && root.right == null) return 1;
        return countLeaves(root.left) + countLeaves(root.right);
    }

    /** Full: every node has either 0 or 2 children, never exactly 1. O(n) time. */
    static boolean isFull(TreeNode root) {
        if (root == null) return true;
        if ((root.left == null) != (root.right == null)) return false;    // exactly one child
        return isFull(root.left) && isFull(root.right);
    }

    /** Perfect: every level completely filled, which happens exactly when size == 2^height - 1. O(n) time. */
    static boolean isPerfect(TreeNode root) {
        int h = height(root);
        return size(root) == (1L << h) - 1;
    }

    /** Complete: reading level by level, left to right, no node appears after the first missing position. O(n) time, O(w) space. */
    static boolean isComplete(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();       // LinkedList, because ArrayDeque rejects null
        queue.add(root);
        boolean seenGap = false;
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                seenGap = true;                           // a missing position
                continue;
            }
            if (seenGap) return false;                    // a real node after a missing position
            queue.add(node.left);
            queue.add(node.right);
        }
        return true;
    }

    /** Balanced (height-balanced): at every node the two subtree heights differ by at most 1. O(n) time. */
    static boolean isBalanced(TreeNode root) {
        return balancedHeight(root) != -1;
    }

    private static int balancedHeight(TreeNode root) {   // the height, or -1 as soon as some node is unbalanced
        if (root == null) return 0;
        int l = balancedHeight(root.left);
        if (l == -1) return -1;
        int r = balancedHeight(root.right);
        if (r == -1) return -1;
        if (Math.abs(l - r) > 1) return -1;
        return 1 + Math.max(l, r);
    }

    /** Degenerate (skewed): every node has at most one child, so the tree is a linked list in disguise. O(n) time, O(1) space. */
    static boolean isDegenerate(TreeNode root) {
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left != null && cur.right != null) return false;
            cur = (cur.left != null) ? cur.left : cur.right;
        }
        return true;
    }

    /** Level i (the root is level 0) holds at most 2^i nodes, because each level can at most double the one above. */
    static long maxNodesAtLevel(int level) {
        return 1L << level;
    }

    /** A tree with h levels holds at most 1 + 2 + 4 + ... + 2^(h-1) = 2^h - 1 nodes. */
    static long maxNodesWithHeight(int h) {
        return (1L << h) - 1;
    }

    /** The fewest levels that can hold n nodes: the smallest h with 2^h - 1 >= n, which is ceil(log2(n + 1)). */
    static int minHeight(long n) {
        int h = 0;
        while (maxNodesWithHeight(h) < n) h++;
        return h;
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

    /** Check every measurement and every classification of one tree. */
    static void verify(String name, TreeNode root, int n, int h, int leaves,
                       boolean full, boolean perfect, boolean complete, boolean balanced, boolean degenerate) {
        check(size(root) == n, name + ": size " + size(root));
        check(countEdges(root) == Math.max(0, n - 1), name + ": edges " + countEdges(root));
        check(height(root) == h, name + ": height " + height(root));
        check(countLeaves(root) == leaves, name + ": leaves " + countLeaves(root));
        check(isFull(root) == full, name + ": isFull");
        check(isPerfect(root) == perfect, name + ": isPerfect");
        check(isComplete(root) == complete, name + ": isComplete");
        check(isBalanced(root) == balanced, name + ": isBalanced");
        check(isDegenerate(root) == degenerate, name + ": isDegenerate");
        check(n <= maxNodesWithHeight(h) && h >= minHeight(n), name + ": counting bounds");
        if (full && n > 0) check(leaves == (n - leaves) + 1, name + ": full tree has one more leaf than internal nodes");
    }

    public static void main(String[] args) {
        //          name        tree                                       n  h  leaves full   perfect complete balanced degenerate
        verify("example",   exampleTree(),                                 7, 4, 3, false, false, false, true,  false);
        verify("perfect",   build(1, 2, 3, 4, 5, 6, 7),                    7, 3, 4, true,  true,  true,  true,  false);
        verify("complete",  build(1, 2, 3, 4, 5, 6),                       6, 3, 3, false, false, true,  true,  false);
        verify("full",      build(1, 2, 3, null, null, 4, 5),              5, 3, 3, true,  false, false, true,  false);
        verify("zigzag",    build(1, null, 2, 3, null, null, 4),           4, 4, 1, false, false, false, false, true);
        verify("single",    build(9),                                      1, 1, 1, true,  true,  true,  true,  true);
        verify("empty",     build(),                                       0, 0, 0, true,  true,  true,  true,  true);

        TreeNode ex = exampleTree();
        check(depthOf(ex, 1) == 0 && depthOf(ex, 3) == 1 && depthOf(ex, 5) == 2 && depthOf(ex, 7) == 3, "depths in the example");
        check(depthOf(ex, 99) == -1 && depthOf(null, 1) == -1, "missing value has no depth");

        check(maxNodesAtLevel(0) == 1 && maxNodesAtLevel(3) == 8 && maxNodesAtLevel(40) == 1_099_511_627_776L, "nodes per level");
        check(maxNodesWithHeight(0) == 0 && maxNodesWithHeight(3) == 7 && maxNodesWithHeight(20) == 1_048_575, "nodes per height");
        check(minHeight(0) == 0 && minHeight(1) == 1 && minHeight(3) == 2 && minHeight(4) == 3, "min height small");
        check(minHeight(7) == 3 && minHeight(8) == 4 && minHeight(1_000_000) == 20, "min height large");
        System.out.println("OK P2865_IntroductionToTrees");
    }
}
