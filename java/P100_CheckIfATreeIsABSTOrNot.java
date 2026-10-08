import java.util.*;

/** TUF 100 - Check if a tree is a BST or not. Every key must be strictly greater than all keys in its left subtree and strictly smaller than all keys in its right subtree. */
public class P100_CheckIfATreeIsABSTOrNot {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. At every node compare with the largest key on the left and the smallest key on the right. O(n^2) time worst case, O(h) space. */
    static boolean bruteForce(TreeNode root) {
        if (root == null) return true;
        if (root.left != null && maxOf(root.left) >= root.val) return false;
        if (root.right != null && minOf(root.right) <= root.val) return false;
        return bruteForce(root.left) && bruteForce(root.right);
    }

    static int maxOf(TreeNode node) {
        if (node == null) return Integer.MIN_VALUE;
        return Math.max(node.val, Math.max(maxOf(node.left), maxOf(node.right)));
    }

    static int minOf(TreeNode node) {
        if (node == null) return Integer.MAX_VALUE;
        return Math.min(node.val, Math.min(minOf(node.left), minOf(node.right)));
    }

    /** Approach 2: better. The in-order sequence of a BST is strictly increasing, so collect it and scan once. O(n) time, O(n) space. */
    static boolean better(TreeNode root) {
        List<Integer> inorder = new ArrayList<>();
        collectInorder(root, inorder);
        for (int i = 1; i < inorder.size(); i++) {
            if (inorder.get(i - 1) >= inorder.get(i)) return false;   // equal keys are not allowed either
        }
        return true;
    }

    static void collectInorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        collectInorder(node.left, out);
        out.add(node.val);
        collectInorder(node.right, out);
    }

    /** Approach 3: optimal. Pass down the open interval (low, high) that every key of the subtree must lie in. O(n) time, O(h) space. */
    static boolean optimal(TreeNode root) {
        return inRange(root, Long.MIN_VALUE, Long.MAX_VALUE);   // long bounds: keys may be Integer.MIN_VALUE / MAX_VALUE
    }

    static boolean inRange(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;
        return inRange(node.left, low, node.val) && inRange(node.right, node.val, high);
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

    static void verify(boolean expected, Integer... level) {
        TreeNode root = build(level);
        String in = Arrays.toString(level);
        check(bruteForce(root) == expected, "bruteForce " + in);
        check(better(root) == expected, "better " + in);
        check(optimal(root) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(true, 2, 1, 3);
        verify(false, 5, 1, 4, null, null, 3, 6);                  // right child 4 is smaller than the root
        verify(false, 5, 4, 6, null, null, 3, 7);                  // every parent-child pair is fine, but 3 sits right of 5
        verify(false, 10, 5, 15, null, null, 6, 20);               // same trap one level deeper
        verify(false, 2, 2, 2);                                    // duplicates break strictness
        verify(true);                                              // empty tree
        verify(true, Integer.MAX_VALUE);                           // single node
        verify(true, Integer.MIN_VALUE, null, Integer.MAX_VALUE);  // int sentinels would wrongly reject this
        verify(false, Integer.MAX_VALUE, Integer.MAX_VALUE);
        verify(true, 8, 4, 12, 2, 6, 10, 14, 1, 3, 5, 7, 9, 11, 13, 15);
        System.out.println("OK P100_CheckIfATreeIsABSTOrNot");
    }
}
