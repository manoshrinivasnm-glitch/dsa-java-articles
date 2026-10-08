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
        if (root == null) return out;
        if (!isLeaf(root)) out.add(root.val);      // a lone root is added by the leaf walk instead
        addLeftBoundary(root, out);
        addLeaves(root, out);
        addRightBoundary(root, out);
        return out;
    }

    static void addLeftBoundary(TreeNode root, List<Integer> out) {
        TreeNode cur = root.left;
        while (cur != null) {
            if (!isLeaf(cur)) out.add(cur.val);
            cur = cur.left != null ? cur.left : cur.right;
        }
    }

    static void addLeaves(TreeNode node, List<Integer> out) {
        if (node == null) return;
        if (isLeaf(node)) {
            out.add(node.val);
            return;
        }
        addLeaves(node.left, out);
        addLeaves(node.right, out);
    }

    static void addRightBoundary(TreeNode root, List<Integer> out) {
        List<Integer> tmp = new ArrayList<>();
        TreeNode cur = root.right;
        while (cur != null) {
            if (!isLeaf(cur)) tmp.add(cur.val);
            cur = cur.right != null ? cur.right : cur.left;
        }
        for (int i = tmp.size() - 1; i >= 0; i--) out.add(tmp.get(i));   // bottom-up
    }

    /** Approach 2: one DFS carrying two flags. Left-boundary nodes are emitted on the way down, right-boundary nodes on the way back up, every other leaf when it is reached. O(n) time, O(h) stack. */
    static List<Integer> singleDfs(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        out.add(root.val);
        dfs(root.left, true, false, out);
        dfs(root.right, false, true, out);
        return out;
    }

    static void dfs(TreeNode node, boolean onLeft, boolean onRight, List<Integer> out) {
        if (node == null) return;
        if (onLeft || (!onRight && isLeaf(node))) out.add(node.val);    // pre-order moment
        dfs(node.left, onLeft, onRight && node.right == null, out);
        dfs(node.right, onLeft && node.left == null, onRight, out);
        if (onRight) out.add(node.val);                                 // post-order moment
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
