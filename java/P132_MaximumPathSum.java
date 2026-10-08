import java.util.*;

/** TUF 132 - Maximum path sum. Return the largest sum of node values over all non-empty paths (a path may start and end anywhere but never revisits a node). */
public class P132_MaximumPathSum {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Best sum of a downward path that starts at node (node included, may stop anywhere); 0 for an empty tree. O(size of subtree). */
    static int maxDown(TreeNode node) {
        if (node == null) return 0;
        int bestChild = Math.max(maxDown(node.left), maxDown(node.right));
        return node.val + Math.max(bestChild, 0);
    }

    /** Approach 1: brute force. Try every node as the highest point of the path: its value plus the best non-negative downward gain on each side. O(n^2) worst case, O(h) stack. Requires a non-empty tree. */
    static int bruteForce(TreeNode root) {
        int best = root.val + Math.max(0, maxDown(root.left)) + Math.max(0, maxDown(root.right));
        if (root.left != null) best = Math.max(best, bruteForce(root.left));
        if (root.right != null) best = Math.max(best, bruteForce(root.right));
        return best;
    }

    /** Approach 2: one post-order pass. Each call returns the gain its node can offer the parent and updates the best path that bends at the node. O(n) time, O(h) stack. Requires a non-empty tree. */
    static int optimal(TreeNode root) {
        int[] best = {Integer.MIN_VALUE};
        gain(root, best);
        return best[0];
    }

    static int gain(TreeNode node, int[] best) {
        if (node == null) return 0;
        int left = Math.max(0, gain(node.left, best));     // a negative branch is better dropped
        int right = Math.max(0, gain(node.right, best));
        best[0] = Math.max(best[0], node.val + left + right);  // path bending here uses both sides
        return node.val + Math.max(left, right);               // the parent can extend only one side
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

    /** A random non-empty tree with values in [-50, 49]. */
    static TreeNode randomTree(Random rnd, int depth) {
        TreeNode node = new TreeNode(rnd.nextInt(100) - 50);
        if (depth > 1 && rnd.nextInt(3) != 0) node.left = randomTree(rnd, depth - 1);
        if (depth > 1 && rnd.nextInt(3) != 0) node.right = randomTree(rnd, depth - 1);
        return node;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] level, int expected) {
        TreeNode root = build(level);
        String s = Arrays.toString(level);
        check(bruteForce(root) == expected, "bruteForce " + s);
        check(optimal(root) == expected, "optimal " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 3}, 6);                                            // 2-1-3
        verify(new Integer[]{-10, 9, 20, null, null, 15, 7}, 42);                     // 15-20-7, root left out
        verify(new Integer[]{-3}, -3);                                                // edge: single negative node
        verify(new Integer[]{-2, -1}, -1);                                            // all negative: best single node
        verify(new Integer[]{2, -1}, 2);                                              // drop the negative child
        verify(new Integer[]{1, -2, 3}, 4);                                           // 1-3
        verify(new Integer[]{5, 4, 8, 11, null, 13, 4, 7, 2, null, null, null, 1}, 48); // 7-11-4-5-8-13
        verify(new Integer[]{-1, 5, null, 4, null, null, 2, -4}, 11);                 // 5-4-2, deep inside the tree
        verify(new Integer[]{10, 2, 10, 20, 1, null, -25, null, null, null, null, 3, 4}, 42); // 20-2-10-10

        // seeded random trees with mixed signs: both methods must agree
        Random rnd = new Random(132);
        for (int t = 0; t < 300; t++) {
            TreeNode root = randomTree(rnd, 8);
            check(optimal(root) == bruteForce(root), "random tree #" + t);
        }
        System.out.println("OK P132_MaximumPathSum");
    }
}
