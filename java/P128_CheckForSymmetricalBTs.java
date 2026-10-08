import java.util.*;

/** TUF 128 - Check for symmetrical BTs. A tree is symmetric when it is a mirror image of itself around the root. */
public class P128_CheckForSymmetricalBTs {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: build a mirrored copy of the tree and compare it with the original. O(n) time, O(n) extra space. */
    static boolean mirrorAndCompare(TreeNode root) {
        return identical(root, mirrorCopy(root));
    }

    static TreeNode mirrorCopy(TreeNode node) {
        if (node == null) return null;
        TreeNode m = new TreeNode(node.val);
        m.left = mirrorCopy(node.right);                     // children swap sides at every level
        m.right = mirrorCopy(node.left);
        return m;
    }

    static boolean identical(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && identical(a.left, b.left) && identical(a.right, b.right);
    }

    /** Approach 2: recursively compare the left subtree with the right subtree as mirror images. O(n) time, O(h) space. */
    static boolean recursive(TreeNode root) {
        return root == null || isMirror(root.left, root.right);
    }

    static boolean isMirror(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;           // both missing is fine, exactly one missing is not
        return a.val == b.val
                && isMirror(a.left, b.right)                 // outer pair
                && isMirror(a.right, b.left);                // inner pair
    }

    /** Approach 3: the same pairwise comparison driven by a queue of node pairs. O(n) time, O(w) space. */
    static boolean iterative(TreeNode root) {
        if (root == null) return true;
        Deque<TreeNode[]> queue = new ArrayDeque<>();
        queue.add(new TreeNode[]{root.left, root.right});
        while (!queue.isEmpty()) {
            TreeNode[] pair = queue.poll();
            TreeNode a = pair[0], b = pair[1];
            if (a == null && b == null) continue;
            if (a == null || b == null || a.val != b.val) return false;
            queue.add(new TreeNode[]{a.left, b.right});
            queue.add(new TreeNode[]{a.right, b.left});
        }
        return true;
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

    /** Perfect tree of the given depth where every node's value is its depth, which is symmetric. */
    static TreeNode perfectByDepth(int depth, int d) {
        if (d > depth) return null;
        TreeNode node = new TreeNode(d);
        node.left = perfectByDepth(depth, d + 1);
        node.right = perfectByDepth(depth, d + 1);
        return node;
    }

    static TreeNode rightmost(TreeNode node) {
        while (node.right != null) node = node.right;
        return node;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode tree, boolean expected, String label) {
        check(mirrorAndCompare(tree) == expected, "mirrorAndCompare " + label);
        check(recursive(tree) == expected, "recursive " + label);
        check(iterative(tree) == expected, "iterative " + label);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 2, 3, 4, 4, 3), true, "example 1");
        verify(build(1, 2, 2, null, 3, null, 3), false, "example 2: same values, wrong shape");
        verify(build(), true, "empty tree");                                                     // edge case
        verify(build(5), true, "single node");
        verify(build(1, 2, 3), false, "children differ");
        verify(build(1, 2, null), false, "only one child");
        verify(build(1, 2, 2, null, 3, 3, null), true, "inner children mirror each other");
        verify(build(1, 2, 2, 2, null, 2), false, "levels read as palindromes but shapes are not mirrored");
        verify(build(0, -7, -7, 4, null, null, 4), true, "negative values");

        TreeNode big = perfectByDepth(16, 0);                    // 131071 nodes, symmetric
        verify(big, true, "perfect tree of 131071 nodes");
        rightmost(big).val = 99;                                 // break symmetry at the deepest rightmost leaf
        verify(big, false, "one leaf changed");
        System.out.println("OK P128_CheckForSymmetricalBTs");
    }
}
