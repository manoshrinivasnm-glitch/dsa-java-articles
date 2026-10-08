import java.util.*;

/** TUF 106 - LCA in BST. Given the keys p and q of two nodes in a BST, return their lowest common ancestor (a node counts as its own ancestor). */
public class P106_LCAInBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. Ignore the ordering and run the general binary-tree LCA recursion. O(n) time, O(h) space. */
    static TreeNode bruteForce(TreeNode root, int p, int q) {
        if (root == null || root.val == p || root.val == q) return root;
        TreeNode left = bruteForce(root.left, p, q);
        TreeNode right = bruteForce(root.right, p, q);
        if (left != null && right != null) return root;   // p and q were found on different sides
        return left != null ? left : right;
    }

    /** Approach 2: better. Record both root-to-node search paths and return the last node they share. O(h) time, O(h) space. */
    static TreeNode better(TreeNode root, int p, int q) {
        List<TreeNode> pathP = searchPath(root, p);
        List<TreeNode> pathQ = searchPath(root, q);
        TreeNode lca = null;
        for (int i = 0; i < pathP.size() && i < pathQ.size(); i++) {
            if (pathP.get(i) != pathQ.get(i)) break;          // same node object, not just same key
            lca = pathP.get(i);
        }
        return lca;
    }

    static List<TreeNode> searchPath(TreeNode root, int key) {
        List<TreeNode> path = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            path.add(cur);
            if (key == cur.val) break;
            cur = key < cur.val ? cur.left : cur.right;
        }
        return path;
    }

    /** Approach 3: optimal. Walk down from the root; the first node where p and q stop being on the same side is the LCA. O(h) time, O(1) space. */
    static TreeNode optimal(TreeNode root, int p, int q) {
        TreeNode cur = root;
        while (cur != null) {
            if (p < cur.val && q < cur.val) cur = cur.left;          // both keys live in the left subtree
            else if (p > cur.val && q > cur.val) cur = cur.right;    // both keys live in the right subtree
            else return cur;                                          // they split here, or one of them is cur
        }
        return null;
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

    static void verify(TreeNode root, int p, int q, int expected) {
        String in = "p=" + p + " q=" + q;
        TreeNode a = bruteForce(root, p, q), b = better(root, p, q), c = optimal(root, p, q);
        check(a != null && a.val == expected, "bruteForce " + in);
        check(b != null && b.val == expected, "better " + in);
        check(c != null && c.val == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        TreeNode t = build(6, 2, 8, 0, 4, 7, 9, null, null, 3, 5);
        verify(t, 2, 8, 6);     // split at the root
        verify(t, 2, 4, 2);     // p is an ancestor of q
        verify(t, 3, 5, 4);
        verify(t, 0, 5, 2);
        verify(t, 5, 0, 2);     // order of p and q does not matter
        verify(t, 7, 9, 8);
        verify(t, 3, 9, 6);
        verify(t, 0, 3, 2);
        verify(t, 6, 6, 6);     // p == q
        verify(build(2, 1), 2, 1, 2);
        verify(build(1), 1, 1, 1);                                  // single node
        TreeNode chain = build(1, null, 2, null, 3, null, 4);       // right-skewed
        verify(chain, 3, 4, 3);
        verify(chain, 4, 1, 1);
        System.out.println("OK P106_LCAInBST");
    }
}
