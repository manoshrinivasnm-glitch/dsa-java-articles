import java.util.*;

/** TUF 2784 - Symmetric Binary Tree. Decide whether a binary tree is a mirror image of itself around its root. */
public class P2784_SymmetricBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: serialize the tree twice (left-first and right-first preorder, with null markers) and compare. O(n) time, O(n) space. */
    static boolean bruteForce(TreeNode root) {
        List<Integer> leftFirst = new ArrayList<>(), rightFirst = new ArrayList<>();
        serialize(root, true, leftFirst);
        serialize(root, false, rightFirst);
        return leftFirst.equals(rightFirst);
    }

    static void serialize(TreeNode node, boolean leftFirst, List<Integer> out) {
        if (node == null) { out.add(null); return; }              // the null marker keeps the shape in the list
        out.add(node.val);
        serialize(leftFirst ? node.left : node.right, leftFirst, out);
        serialize(leftFirst ? node.right : node.left, leftFirst, out);
    }

    /** Approach 2: recursion on mirrored pairs (outer with outer, inner with inner). O(n) time, O(h) space. */
    static boolean recursive(TreeNode root) {
        return root == null || isMirror(root.left, root.right);
    }

    static boolean isMirror(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;                // both missing is fine, exactly one missing is not
        return a.val == b.val && isMirror(a.left, b.right) && isMirror(a.right, b.left);
    }

    /** Approach 3: the same pairing done iteratively with a queue of node pairs. O(n) time, O(w) space. */
    static boolean iterative(TreeNode root) {
        if (root == null) return true;
        Queue<TreeNode> q = new LinkedList<>();                   // LinkedList accepts null entries; ArrayDeque does not
        q.add(root.left);
        q.add(root.right);
        while (!q.isEmpty()) {
            TreeNode a = q.poll(), b = q.poll();                  // entries always come in mirrored pairs
            if (a == null && b == null) continue;
            if (a == null || b == null || a.val != b.val) return false;
            q.add(a.left);
            q.add(b.right);
            q.add(a.right);
            q.add(b.left);
        }
        return true;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode build(Integer[] vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < vals.length) {
            TreeNode cur = q.poll();
            if (i < vals.length && vals[i] != null) { cur.left = new TreeNode(vals[i]); q.add(cur.left); }
            i++;
            if (i < vals.length && vals[i] != null) { cur.right = new TreeNode(vals[i]); q.add(cur.right); }
            i++;
        }
        return root;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] vals, boolean expected) {
        TreeNode root = build(vals);
        String in = Arrays.toString(vals);
        check(bruteForce(root) == expected, "bruteForce " + in);
        check(recursive(root) == expected, "recursive " + in);
        check(iterative(root) == expected, "iterative " + in);
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 2, 3, 4, 4, 3}, true);
        verify(new Integer[]{1, 2, 2, null, 3, null, 3}, false);      // same values, mirrored shape broken
        verify(new Integer[]{1, 2, 2, 3, null, null, 3}, true);
        verify(new Integer[]{1, 2, 2, null, 3, 3}, true);              // inner pair only
        verify(new Integer[]{1, 2, 2, 2, null, 2}, false);             // both children on the left side
        verify(new Integer[]{1, 2, 3}, false);                         // values differ
        verify(new Integer[]{}, true);                                 // edge: empty tree
        verify(new Integer[]{1}, true);                                // single node
        System.out.println("OK P2784_SymmetricBinaryTree");
    }
}
