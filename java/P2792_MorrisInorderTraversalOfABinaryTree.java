import java.util.*;

/** TUF 2792 - Morris Inorder Traversal of a Binary Tree. Return the inorder sequence using O(1) extra space. */
public class P2792_MorrisInorderTraversalOfABinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion. O(n) time, O(h) call-stack space. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();           // values in inorder
        inorder(root, out);                              // fill the list recursively
        return out;
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;                        // past a leaf: nothing to add
        inorder(node.left, out);                         // left subtree first
        out.add(node.val);                               // then the root
        inorder(node.right, out);                        // then the right subtree
    }

    /** Approach 2: explicit stack. O(n) time, O(h) space. */
    static List<Integer> iterativeStack(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();              // passed going down, not yet visited
        TreeNode cur = root;                                     // root of the next subtree to explore
        while (cur != null || !stack.isEmpty()) {                // runs until nothing is left to visit
            while (cur != null) {                                // walk as far left as possible
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();                                   // leftmost node not yet visited
            out.add(cur.val);                                    // visit it after its left subtree
            cur = cur.right;                                     // then explore its right subtree
        }
        return out;
    }

    /** Approach 3: Morris traversal, temporary threads replace the stack. O(n) time, O(1) extra space. */
    static List<Integer> morris(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        TreeNode cur = root;                                     // walker; threads replace the stack
        while (cur != null) {
            if (cur.left == null) {                              // no left subtree: visit and move right
                out.add(cur.val);
                cur = cur.right;                                 // may follow a thread back up
            } else {
                TreeNode pred = cur.left;                        // inorder predecessor: rightmost node of the left subtree
                // walk right to the end, or stop at a thread that already points to cur
                while (pred.right != null && pred.right != cur) pred = pred.right;
                if (pred.right == null) {                        // first arrival: leave a thread back, go left
                    pred.right = cur;                            // thread pred -> cur, to come back later
                    cur = cur.left;
                } else {                                         // second arrival via the thread: visit, remove it
                    pred.right = null;                           // remove the thread, restoring the tree
                    out.add(cur.val);
                    cur = cur.right;                             // left side done: continue right
                }
            }
        }
        return out;
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

    /** Balanced search tree over lo..hi, so its inorder sequence is lo, lo + 1, ..., hi. */
    static TreeNode balanced(int lo, int hi) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;
        TreeNode node = new TreeNode(mid);
        node.left = balanced(lo, mid - 1);
        node.right = balanced(mid + 1, hi);
        return node;
    }

    static boolean same(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && same(a.left, b.left) && same(a.right, b.right);
    }

    static TreeNode copy(TreeNode node) {
        if (node == null) return null;
        TreeNode c = new TreeNode(node.val);
        c.left = copy(node.left);
        c.right = copy(node.right);
        return c;
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

    static void verify(TreeNode tree, List<Integer> expected, String label) {
        TreeNode original = copy(tree);
        check(recursive(tree).equals(expected), "recursive " + label);
        check(iterativeStack(tree).equals(expected), "iterativeStack " + label);
        check(morris(tree).equals(expected), "morris " + label);
        check(same(tree, original), "morris must restore the tree " + label);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, null, 6), list(4, 2, 5, 1, 3, 6), "example");
        verify(build(1, null, 2, 3), list(1, 3, 2), "LeetCode example");
        verify(build(1, 2, 3, 4, 5, 6, 7, null, null, 8, 9), list(4, 2, 8, 5, 9, 1, 6, 3, 7), "nested threads");
        verify(build(7), list(7), "single node");
        verify(build(), list(), "empty tree");                                   // edge case
        verify(build(1, 2, null, 3, null, 4), list(4, 3, 2, 1), "left chain");
        verify(build(1, null, 2, null, 3), list(1, 2, 3), "right chain");
        verify(build(5, 5, 5), list(5, 5, 5), "duplicate values");

        int n = 100_000;
        List<Integer> expected = new ArrayList<>();
        for (int v = 1; v <= n; v++) expected.add(v);
        verify(balanced(1, n), expected, "balanced search tree of 100000 nodes");
        System.out.println("OK P2792_MorrisInorderTraversalOfABinaryTree");
    }
}
