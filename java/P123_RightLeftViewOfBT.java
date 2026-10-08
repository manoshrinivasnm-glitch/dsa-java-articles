import java.util.*;

/** TUF 123 - Right/Left View of BT. Return the first (left view) or last (right view) node of every level, top to bottom. */
public class P123_RightLeftViewOfBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: level order traversal; keep the last (right view) or first (left view) node of each level. O(n) time, O(w) space. */
    static List<Integer> rightViewBfs(TreeNode root) {
        return viewBfs(root, true);
    }

    static List<Integer> leftViewBfs(TreeNode root) {
        return viewBfs(root, false);
    }

    static List<Integer> viewBfs(TreeNode root, boolean rightSide) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();                         // exactly the nodes of the current level
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (rightSide ? i == size - 1 : i == 0) out.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
        }
        return out;
    }

    /** Approach 2: DFS visiting the viewed side first; the first node met at each depth is visible. O(n) time, O(h) space. */
    static List<Integer> rightViewDfs(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        viewDfs(root, 0, out, true);
        return out;
    }

    static List<Integer> leftViewDfs(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        viewDfs(root, 0, out, false);
        return out;
    }

    static void viewDfs(TreeNode node, int depth, List<Integer> out, boolean rightSide) {
        if (node == null) return;
        if (depth == out.size()) out.add(node.val);          // first node ever reached at this depth
        TreeNode near = rightSide ? node.right : node.left;   // the side we look from goes first
        TreeNode far = rightSide ? node.left : node.right;
        viewDfs(near, depth + 1, out, rightSide);
        viewDfs(far, depth + 1, out, rightSide);
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

    /** Complete tree with nodes 1..n in level order (node k has children 2k and 2k + 1). */
    static TreeNode complete(int k, int n) {
        if (k > n) return null;
        TreeNode node = new TreeNode(k);
        node.left = complete(2 * k, n);
        node.right = complete(2 * k + 1, n);
        return node;
    }

    static List<Integer> list(int... values) {
        List<Integer> out = new ArrayList<>();
        for (int v : values) out.add(v);
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode tree, List<Integer> right, List<Integer> left, String label) {
        check(rightViewBfs(tree).equals(right), "rightViewBfs " + label);
        check(rightViewDfs(tree).equals(right), "rightViewDfs " + label);
        check(leftViewBfs(tree).equals(left), "leftViewBfs " + label);
        check(leftViewDfs(tree).equals(left), "leftViewDfs " + label);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, null, 5, null, 4), list(1, 3, 4), list(1, 2, 5), "example 1");
        verify(build(1, 2, 3, 4), list(1, 3, 4), list(1, 2, 4), "deep node hidden on the left");
        verify(build(), list(), list(), "empty tree");                                         // edge case
        verify(build(42), list(42), list(42), "single node");
        verify(build(1, 2, 3, null, null, 4, 5, 6), list(1, 3, 5, 6), list(1, 2, 4, 6), "deepest level reached through the right subtree");
        verify(build(1, null, 2, null, 3), list(1, 2, 3), list(1, 2, 3), "right chain");
        verify(build(1, 2, null, 3), list(1, 2, 3), list(1, 2, 3), "left chain");
        verify(build(1, -1, -1, 0, null, null, 0), list(1, -1, 0), list(1, -1, 0), "duplicates and negatives");
        // complete tree of 100000 nodes: the levels hold 1, 2-3, 4-7, ...; the last level ends at 100000
        List<Integer> right = new ArrayList<>(), left = new ArrayList<>();
        for (int start = 1; start <= 100_000; start *= 2) {
            left.add(start);
            right.add(Math.min(2 * start - 1, 100_000));
        }
        verify(complete(1, 100_000), right, left, "complete tree of 100000 nodes");
        System.out.println("OK P123_RightLeftViewOfBT");
    }
}
