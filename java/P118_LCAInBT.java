import java.util.*;

/** TUF 118 - LCA in BT. Return the lowest common ancestor of two nodes p and q that are both present in the tree. */
public class P118_LCAInBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: find the root-to-node path of p and of q; the last node the two paths share is the LCA. O(n) time, O(n) space. */
    static TreeNode bruteForce(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> pathP = new ArrayList<>(), pathQ = new ArrayList<>();
        if (!pathTo(root, p, pathP) || !pathTo(root, q, pathQ)) return null;
        TreeNode lca = null;
        for (int i = 0; i < pathP.size() && i < pathQ.size() && pathP.get(i) == pathQ.get(i); i++) {
            lca = pathP.get(i);                                   // still on the shared prefix
        }
        return lca;
    }

    static boolean pathTo(TreeNode node, TreeNode target, List<TreeNode> path) {
        if (node == null) return false;
        path.add(node);
        if (node == target) return true;
        if (pathTo(node.left, target, path) || pathTo(node.right, target, path)) return true;
        path.remove(path.size() - 1);                             // target is not below this node
        return false;
    }

    /** Approach 2: record parents with a BFS, put p and all its ancestors in a set, then climb from q until we hit one. O(n) time, O(n) space. */
    static TreeNode parentPointers(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        parent.put(root, null);
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node.left != null) { parent.put(node.left, node); queue.add(node.left); }
            if (node.right != null) { parent.put(node.right, node); queue.add(node.right); }
        }
        if (!parent.containsKey(p) || !parent.containsKey(q)) return null;
        Set<TreeNode> ancestorsOfP = new HashSet<>();
        for (TreeNode a = p; a != null; a = parent.get(a)) ancestorsOfP.add(a);   // p counts as its own ancestor
        for (TreeNode b = q; b != null; b = parent.get(b)) {
            if (ancestorsOfP.contains(b)) return b;               // first shared ancestor on the way up is the lowest
        }
        return null;
    }

    /** Approach 3: one post-order recursion that reports p, q, or their LCA from each subtree. O(n) time, O(h) space. */
    static TreeNode optimal(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = optimal(root.left, p, q);
        TreeNode right = optimal(root.right, p, q);
        if (left == null) return right;
        if (right == null) return left;
        return root;                                              // p and q were found on different sides
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

    /** Returns the node holding val (values are unique in the tests), or null. */
    static TreeNode find(TreeNode node, int val) {
        if (node == null || node.val == val) return node;
        TreeNode inLeft = find(node.left, val);
        return inLeft != null ? inLeft : find(node.right, val);
    }

    /** A left-skewed chain 1 -> 2 -> ... -> n. */
    static TreeNode chain(int n) {
        TreeNode root = new TreeNode(1), cur = root;
        for (int v = 2; v <= n; v++) { cur.left = new TreeNode(v); cur = cur.left; }
        return root;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode root, int pv, int qv, int expected) {
        TreeNode p = find(root, pv), q = find(root, qv), want = find(root, expected);
        String in = "p=" + pv + " q=" + qv;
        check(p != null && q != null && want != null, "test setup " + in);
        check(bruteForce(root, p, q) == want, "bruteForce " + in);
        check(parentPointers(root, p, q) == want, "parentPointers " + in);
        check(optimal(root, p, q) == want, "optimal " + in);
    }

    public static void main(String[] args) {
        TreeNode t = build(new Integer[]{3, 5, 1, 6, 2, 0, 8, null, null, 7, 4});
        verify(t, 5, 1, 3);               // on opposite sides of the root
        verify(t, 5, 4, 5);               // one node is an ancestor of the other
        verify(t, 4, 5, 5);               // same pair, order swapped
        verify(t, 7, 8, 3);
        verify(t, 7, 4, 2);
        verify(t, 6, 4, 5);
        verify(t, 0, 8, 1);
        verify(t, 6, 6, 6);               // p == q
        verify(build(new Integer[]{1}), 1, 1, 1);                 // edge: single node
        verify(build(new Integer[]{1, 2}), 1, 2, 1);
        TreeNode c = chain(1000);
        verify(c, 500, 1000, 500);        // deep chain: the shallower node is the answer
        verify(c, 1000, 1, 1);
        System.out.println("OK P118_LCAInBT");
    }
}
