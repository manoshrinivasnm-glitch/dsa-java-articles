import java.util.*;

/** TUF 2780 - Search in a Binary Search Tree. Return the node whose value equals val (the subtree rooted there), or null. */
public class P2780_SearchInABinarySearchTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: ignore the ordering and search the whole tree with DFS. O(n) time, O(h) stack. */
    static TreeNode bruteForce(TreeNode root, int val) {
        if (root == null) return null;
        if (root.val == val) return root;
        TreeNode inLeft = bruteForce(root.left, val);
        if (inLeft != null) return inLeft;
        return bruteForce(root.right, val);
    }

    /** Approach 2: recursive descent that discards one subtree per call. O(h) time, O(h) stack. */
    static TreeNode recursive(TreeNode root, int val) {
        if (root == null || root.val == val) return root;
        return val < root.val ? recursive(root.left, val) : recursive(root.right, val);
    }

    /** Approach 3: the same descent as a loop. O(h) time, O(1) space. */
    static TreeNode optimal(TreeNode root, int val) {
        TreeNode cur = root;
        while (cur != null && cur.val != val) {
            cur = val < cur.val ? cur.left : cur.right;   // val can only live on one side
        }
        return cur;                                       // the matching node, or null if we fell off
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

    /** Serialize to LeetCode-style level order with trailing nulls removed, e.g. "[2, 1, 3]". */
    static String toLevelOrder(TreeNode root) {
        List<String> out = new ArrayList<>();
        Queue<TreeNode> q = new LinkedList<>();          // LinkedList accepts null elements
        q.add(root);
        while (!q.isEmpty()) {
            TreeNode cur = q.poll();
            if (cur == null) { out.add("null"); continue; }
            out.add(String.valueOf(cur.val));
            q.add(cur.left);
            q.add(cur.right);
        }
        while (!out.isEmpty() && out.get(out.size() - 1).equals("null")) out.remove(out.size() - 1);
        return out.toString();
    }

    static void verify(Integer[] level, int val, String expected) {
        TreeNode root = fromLevelOrder(level);
        TreeNode a = bruteForce(root, val), b = recursive(root, val), c = optimal(root, val);
        check(a == b && b == c, "approaches returned different nodes for " + val + " in " + Arrays.toString(level));
        check(toLevelOrder(c).equals(expected), "search " + val + " in " + Arrays.toString(level) + " gave " + toLevelOrder(c));
    }

    public static void main(String[] args) {
        verify(new Integer[]{4, 2, 7, 1, 3}, 2, "[2, 1, 3]");
        verify(new Integer[]{4, 2, 7, 1, 3}, 5, "[]");                         // absent: null
        verify(new Integer[]{4, 2, 7, 1, 3}, 4, "[4, 2, 7, 1, 3]");            // the root itself
        verify(new Integer[]{4, 2, 7, 1, 3}, 7, "[7]");                        // a leaf
        verify(new Integer[]{}, 1, "[]");                                      // empty tree
        verify(new Integer[]{5}, 5, "[5]");                                    // single node
        verify(new Integer[]{1, null, 2, null, 3, null, 4}, 3, "[3, null, 4]"); // right-skewed chain
        verify(new Integer[]{0, -5, 5, -8, -2}, -2, "[-2]");                   // negative keys
        verify(new Integer[]{8, 3, 10, 1, 6, null, 14, null, null, 4, 7, 13}, 6, "[6, 4, 7]");
        verify(new Integer[]{8, 3, 10, 1, 6, null, 14, null, null, 4, 7, 13}, 9, "[]"); // falls off between 8 and 10

        // a deep left chain 2000 .. 1: the recursive version goes 2000 calls deep, which is still fine
        TreeNode chain = null;
        for (int v = 1; v <= 2000; v++) {
            TreeNode t = new TreeNode(v);
            t.left = chain;
            chain = t;
        }
        check(optimal(chain, 1) == recursive(chain, 1) && optimal(chain, 1).val == 1, "deep chain search");
        check(bruteForce(chain, 0) == null && optimal(chain, 0) == null, "deep chain miss");
        System.out.println("OK P2780_SearchInABinarySearchTree");
    }
}
