import java.util.*;

/** TUF 2398 - Find Min/Max in BST. Return {min, max} of the keys in a BST, or {-1, -1} for an empty tree. */
public class P2398_FindMinMaxInBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: visit every node with an explicit stack and keep a running min and max. O(n) time, O(h) space. */
    static int[] bruteForce(TreeNode root) {
        if (root == null) return new int[]{-1, -1};
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            min = Math.min(min, node.val);
            max = Math.max(max, node.val);
            if (node.right != null) stack.push(node.right);
            if (node.left != null) stack.push(node.left);
        }
        return new int[]{min, max};
    }

    /** Approach 2: recurse down the left edge for the min and down the right edge for the max. O(h) time, O(h) stack. */
    static int[] recursive(TreeNode root) {
        if (root == null) return new int[]{-1, -1};
        return new int[]{leftmost(root), rightmost(root)};
    }

    static int leftmost(TreeNode node) {
        return node.left == null ? node.val : leftmost(node.left);
    }

    static int rightmost(TreeNode node) {
        return node.right == null ? node.val : rightmost(node.right);
    }

    /** Approach 3: the same two walks as loops. O(h) time, O(1) space. */
    static int[] optimal(TreeNode root) {
        if (root == null) return new int[]{-1, -1};
        TreeNode lo = root, hi = root;
        while (lo.left != null) lo = lo.left;            // smaller keys are always to the left
        while (hi.right != null) hi = hi.right;          // larger keys are always to the right
        return new int[]{lo.val, hi.val};
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Build a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode fromLevelOrder(Integer... a) {
        if (a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < a.length) {
            TreeNode cur = q.poll();
            if (a[i] != null) { cur.left = new TreeNode(a[i]); q.add(cur.left); }
            i++;
            if (i < a.length && a[i] != null) { cur.right = new TreeNode(a[i]); q.add(cur.right); }
            i++;
        }
        return root;
    }

    static void verify(Integer[] level, int min, int max) {
        TreeNode root = fromLevelOrder(level);
        int[] expected = {min, max};
        int[][] results = {bruteForce(root), recursive(root), optimal(root)};
        for (int[] r : results) {
            check(Arrays.equals(r, expected), "tree " + Arrays.toString(level) + " gave " + Arrays.toString(r)
                    + ", expected " + Arrays.toString(expected));
        }
    }

    public static void main(String[] args) {
        verify(new Integer[]{5, 3, 8, 1, 4, 7, 9}, 1, 9);
        verify(new Integer[]{10}, 10, 10);                          // one node is both min and max
        verify(new Integer[]{}, -1, -1);                            // empty tree
        verify(new Integer[]{1, null, 2, null, 3}, 1, 3);           // right chain: the root is the min
        verify(new Integer[]{3, 2, null, 1}, 1, 3);                 // left chain: the root is the max
        verify(new Integer[]{5, 3, 8, null, 4}, 3, 8);              // the min has a right child and is not a leaf
        verify(new Integer[]{5, 3, 8, null, null, 6}, 3, 8);        // the max has a left child and is not a leaf
        verify(new Integer[]{-2, -10, 7, null, -5}, -10, 7);        // negative keys
        verify(new Integer[]{0, Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("OK P2398_FindMinMaxInBST");
    }
}
