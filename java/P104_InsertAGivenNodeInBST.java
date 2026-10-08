import java.util.*;

/** TUF 104 - Insert a given node in BST. Insert val (not already present) as a new leaf and return the root. */
public class P104_InsertAGivenNodeInBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursive descent; each call returns the (possibly new) root of its subtree. O(h) time, O(h) stack. */
    static TreeNode recursive(TreeNode root, int val) {
        if (root == null) return new TreeNode(val);      // fell off the tree: this empty spot is where val belongs
        if (val < root.val) root.left = recursive(root.left, val);
        else root.right = recursive(root.right, val);
        return root;
    }

    /** Approach 2: iterative descent; stop at the first missing child on the search path and hang the node there. O(h) time, O(1) space. */
    static TreeNode optimal(TreeNode root, int val) {
        TreeNode node = new TreeNode(val);
        if (root == null) return node;
        TreeNode cur = root;
        while (true) {
            if (val < cur.val) {
                if (cur.left == null) { cur.left = node; break; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = node; break; }
                cur = cur.right;
            }
        }
        return root;
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

    /** Serialize to LeetCode-style level order with trailing nulls removed, e.g. "[4, 2, 7, 1, 3, 5]". */
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

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    static void verify(Integer[] level, int val, String expected) {
        String a = toLevelOrder(recursive(fromLevelOrder(level), val));   // fresh tree per approach: insertion mutates it
        String b = toLevelOrder(optimal(fromLevelOrder(level), val));
        check(a.equals(expected), "recursive insert " + val + " into " + Arrays.toString(level) + " gave " + a);
        check(b.equals(expected), "optimal insert " + val + " into " + Arrays.toString(level) + " gave " + b);
    }

    public static void main(String[] args) {
        verify(new Integer[]{4, 2, 7, 1, 3}, 5, "[4, 2, 7, 1, 3, 5]");
        verify(new Integer[]{40, 20, 60, 10, 30, 50, 70}, 25, "[40, 20, 60, 10, 30, 50, 70, null, null, 25]");
        verify(new Integer[]{}, 5, "[5]");                                   // empty tree: the new node is the root
        verify(new Integer[]{5}, 3, "[5, 3]");
        verify(new Integer[]{5}, 8, "[5, null, 8]");
        verify(new Integer[]{1, null, 2, null, 3}, 4, "[1, null, 2, null, 3, null, 4]"); // right chain grows by one
        verify(new Integer[]{4, 2, 7, 1, 3}, 0, "[4, 2, 7, 1, 3, null, null, 0]");       // new minimum
        verify(new Integer[]{0}, Integer.MIN_VALUE, "[0, -2147483648]");

        // building a 100-key tree by repeated insertion: both approaches give the same shape and a sorted inorder
        TreeNode r1 = null, r2 = null;
        List<Integer> expectedKeys = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            int key = i * 37 % 101;                      // a fixed permutation of 1..100
            r1 = recursive(r1, key);
            r2 = optimal(r2, key);
            expectedKeys.add(i);
        }
        check(toLevelOrder(r1).equals(toLevelOrder(r2)), "both approaches build the same tree");
        List<Integer> keys = new ArrayList<>();
        inorder(r2, keys);
        check(keys.equals(expectedKeys), "inorder after 100 insertions is 1..100");
        System.out.println("OK P104_InsertAGivenNodeInBST");
    }
}
