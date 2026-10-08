import java.util.*;

/** TUF 129 - Check if two trees are identical or not. Identical means the same shape and the same value at every position. */
public class P129_CheckIfTwoTreesAreIdenticalOrNot {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: serialize both trees in preorder with a null marker for every missing child, then compare the sequences. O(n + m) time and space. */
    static boolean bySerialization(TreeNode p, TreeNode q) {
        List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
        serialize(p, a);
        serialize(q, b);
        return a.equals(b);
    }

    static void serialize(TreeNode node, List<Integer> out) {
        if (node == null) {
            out.add(null);                 // the marker that pins down the shape
            return;
        }
        out.add(node.val);
        serialize(node.left, out);
        serialize(node.right, out);
    }

    /** Approach 2: recursion. Both empty, or both present with equal values and identical left and right subtrees. O(min(n, m)) time, O(h) stack. */
    static boolean recursive(TreeNode p, TreeNode q) {
        if (p == null || q == null) return p == q;
        return p.val == q.val && recursive(p.left, q.left) && recursive(p.right, q.right);
    }

    /** Approach 3: iterative BFS over pairs of nodes that sit at the same position in both trees. O(min(n, m)) time, O(w) space. */
    static boolean iterative(TreeNode p, TreeNode q) {
        Deque<TreeNode[]> queue = new ArrayDeque<>();
        queue.add(new TreeNode[]{p, q});
        while (!queue.isEmpty()) {
            TreeNode[] pair = queue.poll();
            TreeNode a = pair[0], b = pair[1];
            if (a == null && b == null) continue;
            if (a == null || b == null || a.val != b.val) return false;
            queue.add(new TreeNode[]{a.left, b.left});
            queue.add(new TreeNode[]{a.right, b.right});
        }
        return true;
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

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Every method is run in both argument orders; "identical" is symmetric. */
    static void verify(Integer[] x, Integer[] y, boolean expected) {
        TreeNode p = build(x), q = build(y);
        String s = Arrays.toString(x) + " vs " + Arrays.toString(y);
        check(bySerialization(p, q) == expected && bySerialization(q, p) == expected, "bySerialization " + s);
        check(recursive(p, q) == expected && recursive(q, p) == expected, "recursive " + s);
        check(iterative(p, q) == expected && iterative(q, p) == expected, "iterative " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 3}, new Integer[]{1, 2, 3}, true);
        verify(new Integer[]{1, 2}, new Integer[]{1, null, 2}, false);                    // same values, different shape
        verify(new Integer[]{1, 2, 1}, new Integer[]{1, 1, 2}, false);                    // same shape, values swapped
        verify(new Integer[]{}, new Integer[]{}, true);                                   // edge: two empty trees
        verify(new Integer[]{}, new Integer[]{1}, false);                                 // edge: one empty tree
        verify(new Integer[]{1}, new Integer[]{1}, true);
        verify(new Integer[]{1, 2, 3, 4, 5, null, 7}, new Integer[]{1, 2, 3, 4, 5, null, 7}, true);
        verify(new Integer[]{1, 2, 3, 4}, new Integer[]{1, 2, 3, null, 4}, false);        // differ only deep down
        verify(new Integer[]{2, 2}, new Integer[]{2, null, 2}, false);                    // plain preorder [2, 2] would match

        TreeNode t = build(new Integer[]{4, 2, 6, 1, 3, 5, 7});
        check(bySerialization(t, t) && recursive(t, t) && iterative(t, t), "a tree is identical to itself");
        System.out.println("OK P129_CheckIfTwoTreesAreIdenticalOrNot");
    }
}
