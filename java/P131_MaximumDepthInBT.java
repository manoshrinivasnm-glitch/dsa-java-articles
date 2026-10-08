import java.util.*;

/** TUF 131 - Maximum Depth in BT. Return the number of nodes on the longest root-to-leaf path (0 for an empty tree). */
public class P131_MaximumDepthInBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: level-order traversal (BFS); the number of levels is the depth. O(n) time, O(w) space where w is the widest level. */
    static int levelOrder(TreeNode root) {
        if (root == null) return 0;
        Deque<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int depth = 0;
        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                if (node.left != null) q.add(node.left);
                if (node.right != null) q.add(node.right);
            }
            depth++;
        }
        return depth;
    }

    /** Approach 2: recursion. depth(null) = 0 and depth(node) = 1 + max(depth(left), depth(right)). O(n) time, O(h) stack. */
    static int recursive(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(recursive(root.left), recursive(root.right));
    }

    /** Approach 3: iterative DFS with a stack of (node, depth); same answer as recursion without risking a call-stack overflow on a skewed tree. O(n) time, O(h) space. */
    static int iterativeDfs(TreeNode root) {
        if (root == null) return 0;
        Deque<TreeNode> nodes = new ArrayDeque<>();
        Deque<Integer> depths = new ArrayDeque<>();
        nodes.push(root);
        depths.push(1);
        int best = 0;
        while (!nodes.isEmpty()) {
            TreeNode node = nodes.pop();
            int d = depths.pop();
            best = Math.max(best, d);
            if (node.left != null) { nodes.push(node.left); depths.push(d + 1); }
            if (node.right != null) { nodes.push(node.right); depths.push(d + 1); }
        }
        return best;
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

    static void verify(Integer[] level, int expected) {
        TreeNode root = build(level);
        String s = Arrays.toString(level);
        check(levelOrder(root) == expected, "levelOrder " + s);
        check(recursive(root) == expected, "recursive " + s);
        check(iterativeDfs(root) == expected, "iterativeDfs " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{3, 9, 20, null, null, 15, 7}, 3);           // LeetCode example
        verify(new Integer[]{1, null, 2}, 2);
        verify(new Integer[]{}, 0);                                      // edge: empty tree has depth 0
        verify(new Integer[]{1}, 1);                                     // single node
        verify(new Integer[]{1, 2, 3, 4, null, null, 5, 6}, 4);          // deepest leaf is 6 on path 1-2-4-6
        verify(new Integer[]{1, 2, null, 3, null, 4, null, 5}, 5);       // left-skewed: depth equals n

        // a 100000-node left chain: BFS and the explicit-stack DFS handle it without recursion
        TreeNode chain = null;
        for (int v = 100_000; v >= 1; v--) {
            TreeNode node = new TreeNode(v);
            node.left = chain;
            chain = node;
        }
        check(levelOrder(chain) == 100_000, "levelOrder deep chain");
        check(iterativeDfs(chain) == 100_000, "iterativeDfs deep chain");
        System.out.println("OK P131_MaximumDepthInBT");
    }
}
