import java.util.*;

/** TUF 116 - Boundary Traversal. Return the boundary anticlockwise from the root: root, left boundary top-down, all leaves left to right, right boundary bottom-up, each node once. */
public class P116_BoundaryTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    static boolean isLeaf(TreeNode node) {
        return node.left == null && node.right == null;
    }

    /** Approach 1: three separate walks (left boundary, leaves, right boundary). The boundary walks skip leaves so the leaf walk owns them. O(n) time, O(h) space. */
    static List<Integer> threeParts(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;                    // empty tree: no boundary
        if (!isLeaf(root)) out.add(root.val);            // a lone root is added by the leaf walk instead
        addLeftBoundary(root, out);                      // left edge top-down, without leaves
        addLeaves(root, out);                            // every leaf, left to right
        addRightBoundary(root, out);                     // right edge bottom-up, without leaves
        return out;
    }

    static void addLeftBoundary(TreeNode root, List<Integer> out) {
        TreeNode cur = root.left;                        // the root is already added
        while (cur != null) {                            // walk down the left edge
            if (!isLeaf(cur)) out.add(cur.val);          // leaves are left to addLeaves
            cur = cur.left != null ? cur.left : cur.right;   // stay on the edge: prefer left, else right
        }
    }

    static void addLeaves(TreeNode node, List<Integer> out) {
        if (node == null) return;                        // empty subtree: no leaves
        if (isLeaf(node)) {                              // a leaf: record it and stop
            out.add(node.val);
            return;
        }
        addLeaves(node.left, out);                       // left subtree's leaves come before the right's
        addLeaves(node.right, out);
    }

    static void addRightBoundary(TreeNode root, List<Integer> out) {
        List<Integer> tmp = new ArrayList<>();           // right edge top-down, reversed at the end
        TreeNode cur = root.right;                       // the root is already added
        while (cur != null) {                            // walk down the right edge
            if (!isLeaf(cur)) tmp.add(cur.val);          // leaves are left to addLeaves
            cur = cur.right != null ? cur.right : cur.left;   // stay on the edge: prefer right, else left
        }
        for (int i = tmp.size() - 1; i >= 0; i--) out.add(tmp.get(i));   // add them bottom-up
    }

    /** Approach 2: one DFS carrying two flags. Left-boundary nodes are emitted on the way down, right-boundary nodes on the way back up, every other leaf when it is reached. O(n) time, O(h) stack. */
    static List<Integer> singleDfs(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;                    // empty tree: no boundary
        out.add(root.val);                               // the root is always on the boundary
        dfs(root.left, true, false, out);                // left subtree starts on the left edge
        dfs(root.right, false, true, out);               // right subtree starts on the right edge
        return out;
    }

    static void dfs(TreeNode node, boolean onLeft, boolean onRight, List<Integer> out) {
        if (node == null) return;                        // empty subtree: nothing to add
        if (onLeft || (!onRight && isLeaf(node))) out.add(node.val);  // left edge, or leaf off the right edge
        // the left child is on the right edge only if there is no right child
        dfs(node.left, onLeft, onRight && node.right == null, out);
        // the right child is on the left edge only if there is no left child
        dfs(node.right, onLeft && node.left == null, onRight, out);
        if (onRight) out.add(node.val);                               // right edge: emit on the way back up
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

    static TreeNode randomTree(Random rnd, int depth) {
        if (depth == 0 || rnd.nextInt(4) == 0) return null;
        TreeNode node = new TreeNode(rnd.nextInt(100));
        node.left = randomTree(rnd, depth - 1);
        node.right = randomTree(rnd, depth - 1);
        return node;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] level, List<Integer> expected) {
        TreeNode root = build(level);
        String s = Arrays.toString(level);
        check(threeParts(root).equals(expected), "threeParts " + s);
        check(singleDfs(root).equals(expected), "singleDfs " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7, null, null, 8, 9}, List.of(1, 2, 4, 8, 9, 6, 7, 3));
        verify(new Integer[]{20, 8, 22, 4, 12, null, 25, null, null, 10, 14}, List.of(20, 8, 4, 10, 14, 25, 22));
        verify(new Integer[]{1}, List.of(1));                                       // edge: root is also a leaf, printed once
        verify(new Integer[]{}, List.of());                                         // edge: empty tree
        verify(new Integer[]{1, 2, null, 3, null, 4}, List.of(1, 2, 3, 4));         // left-skewed: no right boundary
        verify(new Integer[]{1, null, 2, null, 3}, List.of(1, 3, 2));               // right-skewed: no left boundary
        verify(new Integer[]{1, 2, 3, 4, 5, 6, null, null, null, 7, 8, 9, 10}, List.of(1, 2, 4, 7, 8, 9, 10, 6, 3));
        verify(new Integer[]{1, 2, 3, null, 4, null, null, 5, 6}, List.of(1, 2, 4, 5, 6, 3)); // left boundary turns right at 2
        verify(new Integer[]{1, null, 2, 3}, List.of(1, 3, 2));                     // right boundary turns left at 2

        // seeded random trees: both methods must agree
        Random rnd = new Random(116);
        for (int t = 0; t < 300; t++) {
            TreeNode root = randomTree(rnd, 8);
            check(singleDfs(root).equals(threeParts(root)), "random tree #" + t);
        }
        System.out.println("OK P116_BoundaryTraversal");
    }
}
