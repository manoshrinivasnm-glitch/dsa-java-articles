import java.util.*;

/** TUF 130 - Diameter of Binary Tree. Return the number of edges on the longest path between any two nodes. */
public class P130_DiameterOfBinaryTree {

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

    /** Approach 1: brute force. The longest path that bends at a node has height(left) + height(right) edges; try every node as the bend. O(n^2) worst case, O(h) stack. */
    static int bruteForce(TreeNode root) {
        if (root == null) return 0;
        int through = height(root.left) + height(root.right);
        return Math.max(through, Math.max(bruteForce(root.left), bruteForce(root.right)));
    }

    /** Approach 2: one post-order pass. Compute every height once, bottom-up, and update the best diameter at each node on the way. O(n) time, O(h) stack. */
    static int optimal(TreeNode root) {
        int[] best = new int[1];
        heightAndDiameter(root, best);
        return best[0];
    }

    static int heightAndDiameter(TreeNode node, int[] best) {
        if (node == null) return 0;
        int lh = heightAndDiameter(node.left, best);
        int rh = heightAndDiameter(node.right, best);
        best[0] = Math.max(best[0], lh + rh);      // longest path that bends at this node
        return 1 + Math.max(lh, rh);               // what this node offers to its parent
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
        if (depth == 0 || rnd.nextInt(4) == 0) return null;
        TreeNode node = new TreeNode(rnd.nextInt(100));
        node.left = randomTree(rnd, depth - 1);
        node.right = randomTree(rnd, depth - 1);
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
        verify(new Integer[]{1, 2, 3, 4, 5}, 3);                                      // path 4-2-1-3
        verify(new Integer[]{1, 2}, 1);
        verify(new Integer[]{}, 0);                                                   // edge: empty tree
        verify(new Integer[]{1}, 0);                                                  // single node: no edges
        verify(new Integer[]{1, 2, null, 3, 4, 5, null, null, 6, 7, null, null, 8}, 6); // best path 7-5-3-2-4-6-8 avoids the root
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7}, 4);                                // leaf to leaf through the root

        // seeded random trees: both methods must agree
        Random rnd = new Random(130);
        for (int t = 0; t < 300; t++) {
            TreeNode root = randomTree(rnd, 8);
            check(optimal(root) == bruteForce(root), "random tree #" + t);
        }
        System.out.println("OK P130_DiameterOfBinaryTree");
    }
}
