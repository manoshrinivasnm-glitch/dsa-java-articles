import java.util.*;

/** TUF 98 - Largest BST in Binary Tree. Return the number of nodes in the largest subtree (a node plus all its descendants) that is a BST. */
public class P98_LargestBSTInBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. Validate the subtree at every node; the first valid one from the top is the biggest on that branch. O(n^2) time, O(h) space. */
    static int bruteForce(TreeNode root) {
        if (root == null) return 0;
        if (isBST(root, Long.MIN_VALUE, Long.MAX_VALUE)) return count(root);   // nothing below can be bigger
        return Math.max(bruteForce(root.left), bruteForce(root.right));
    }

    static boolean isBST(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;
        return isBST(node.left, low, node.val) && isBST(node.right, node.val, high);
    }

    static int count(TreeNode node) {
        return node == null ? 0 : 1 + count(node.left) + count(node.right);
    }

    /** What a subtree reports to its parent. A subtree that is not a BST reports min = -inf and max = +inf so no ancestor can accept it. */
    static class Info {
        final long min, max;   // smallest and largest key in the subtree
        final int size;        // size of the largest BST inside the subtree
        Info(long min, long max, int size) { this.min = min; this.max = max; this.size = size; }
    }

    /** Approach 2: optimal. One post-order pass; each node combines its children's (min, max, size) summaries. O(n) time, O(h) space. */
    static int optimal(TreeNode root) {
        return summarize(root).size;
    }

    static Info summarize(TreeNode node) {
        if (node == null) return new Info(Long.MAX_VALUE, Long.MIN_VALUE, 0);   // empty: fits under any parent
        Info left = summarize(node.left);
        Info right = summarize(node.right);
        if (left.max < node.val && node.val < right.min) {                      // node joins two BSTs into one
            return new Info(Math.min(left.min, node.val), Math.max(right.max, node.val), left.size + right.size + 1);
        }
        return new Info(Long.MIN_VALUE, Long.MAX_VALUE, Math.max(left.size, right.size));
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, where null means "no child here". */
    static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < vals.length) {
            TreeNode cur = queue.poll();
            if (i < vals.length && vals[i] != null) { cur.left = new TreeNode(vals[i]); queue.add(cur.left); }
            i++;
            if (i < vals.length && vals[i] != null) { cur.right = new TreeNode(vals[i]); queue.add(cur.right); }
            i++;
        }
        return root;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int expected, Integer... level) {
        TreeNode root = build(level);
        String in = Arrays.toString(level);
        check(bruteForce(root) == expected, "bruteForce " + in);
        check(optimal(root) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(3, 10, 5, 15, 1, 8, null, 7);                 // subtree {5, 1, 8}
        verify(3, 5, 2, 4, 1, 3);                            // subtree {2, 1, 3}
        verify(2, 3, 2, 4, null, null, 1);                   // root passes local checks, but 1 sits right of 3
        verify(2, 4, 2, 7, 2, 3, 5, null, 2, null, null, null, null, null, 1);
        verify(5, 50, 30, 60, 5, 20, 45, 70, null, null, null, null, null, null, 65, 80);  // subtree rooted at 60
        verify(3, 2, 1, 3);                                  // the whole tree is a BST
        verify(1, 1, 1);                                     // duplicate keys are not a BST
        verify(2, Integer.MIN_VALUE, null, Integer.MAX_VALUE);
        verify(1, 1);                                        // single node
        verify(0);                                           // empty tree
        System.out.println("OK P98_LargestBSTInBinaryTree");
    }
}
