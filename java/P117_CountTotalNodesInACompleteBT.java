import java.util.*;

/** TUF 117 - Count total nodes in a complete BT. Every level is full except possibly the last, which is filled from the left. */
public class P117_CountTotalNodesInACompleteBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force, visit every node. O(n) time, O(log n) recursion depth because a complete tree is balanced. */
    static int bruteForce(TreeNode root) {
        if (root == null) return 0;
        return 1 + bruteForce(root.left) + bruteForce(root.right);
    }

    /** Approach 2: optimal, a subtree whose leftmost and rightmost depths agree is perfect. O(log^2 n) time, O(log n) space. */
    static int optimal(TreeNode root) {
        if (root == null) return 0;
        int lh = leftHeight(root), rh = rightHeight(root);
        if (lh == rh) return (1 << lh) - 1;              // perfect subtree: 2^h - 1 nodes, no need to look inside
        return 1 + optimal(root.left) + optimal(root.right);
    }

    static int leftHeight(TreeNode node) {
        int h = 0;
        while (node != null) {
            h++;
            node = node.left;
        }
        return h;
    }

    static int rightHeight(TreeNode node) {
        int h = 0;
        while (node != null) {
            h++;
            node = node.right;
        }
        return h;
    }

    /** Approach 3: binary search for the last node on the bottom level. O(log^2 n) time, O(1) space. */
    static int optimalBinarySearch(TreeNode root) {
        if (root == null) return 0;
        int d = 0;                                        // depth of the bottom level, root at depth 0
        for (TreeNode t = root.left; t != null; t = t.left) d++;
        int lo = 0, hi = (1 << d) - 1;                    // bottom-level positions; position 0 always exists
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;             // round up so that lo = mid always makes progress
            if (exists(root, d, mid)) lo = mid; else hi = mid - 1;
        }
        return (1 << d) - 1 + lo + 1;                     // every level above the bottom, plus positions 0..lo
    }

    // walks from the root to bottom-level position pos, halving the range [0, 2^d - 1] at every step
    static boolean exists(TreeNode root, int d, int pos) {
        int lo = 0, hi = (1 << d) - 1;
        TreeNode node = root;
        for (int level = 0; level < d; level++) {
            int mid = lo + (hi - lo) / 2;
            if (pos <= mid) {
                node = node.left;
                hi = mid;
            } else {
                node = node.right;
                lo = mid + 1;
            }
        }
        return node != null;
    }

    // ---------------------------------------------------------------- helpers
    /** Complete tree with n nodes laid out like a heap: node i has children 2i + 1 and 2i + 2. */
    static TreeNode buildComplete(int n) {
        if (n == 0) return null;
        TreeNode[] nodes = new TreeNode[n];
        for (int i = 0; i < n; i++) nodes[i] = new TreeNode(i + 1);
        for (int i = 0; i < n; i++) {
            if (2 * i + 1 < n) nodes[i].left = nodes[2 * i + 1];
            if (2 * i + 2 < n) nodes[i].right = nodes[2 * i + 2];
        }
        return nodes[0];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode root, int expected, String label) {
        check(bruteForce(root) == expected, "bruteForce " + label);
        check(optimal(root) == expected, "optimal " + label);
        check(optimalBinarySearch(root) == expected, "optimalBinarySearch " + label);
    }

    public static void main(String[] args) {
        verify(buildComplete(6), 6, "[1,2,3,4,5,6]: bottom level 3 of 4 full");
        verify(buildComplete(7), 7, "perfect tree of 7");
        verify(buildComplete(4), 4, "one node on the bottom level");
        verify(buildComplete(1), 1, "single node");
        verify(null, 0, "empty tree");                                  // edge case
        for (int n = 0; n <= 300; n++) verify(buildComplete(n), n, "n = " + n);
        verify(buildComplete(100_000), 100_000, "n = 100000");
        verify(buildComplete((1 << 17) - 1), (1 << 17) - 1, "perfect tree with 17 levels");
        System.out.println("OK P117_CountTotalNodesInACompleteBT");
    }
}
