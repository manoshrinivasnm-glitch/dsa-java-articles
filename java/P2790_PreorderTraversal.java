import java.util.*;

/** TUF 2790 - Preorder Traversal. Return the values of a binary tree in preorder: node, left subtree, right subtree. */
public class P2790_PreorderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion. O(n) time, O(h) call-stack space. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        preorder(root, out);
        return out;
    }

    static void preorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        out.add(node.val);              // 1. the node itself, before anything below it
        preorder(node.left, out);       // 2. its whole left subtree
        preorder(node.right, out);      // 3. then its whole right subtree
    }

    /** Approach 2: explicit stack; push right before left so left is popped first. O(n) time, O(h) space. */
    static List<Integer> iterative(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            out.add(node.val);
            if (node.right != null) stack.push(node.right);   // LIFO: pushed first, processed last
            if (node.left != null) stack.push(node.left);
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

    /** Chain where every node only has a right child: 1 -> 2 -> ... -> n. Preorder is 1..n. */
    static TreeNode rightChain(int n) {
        TreeNode root = null;
        for (int v = n; v >= 1; v--) {
            TreeNode node = new TreeNode(v);
            node.right = root;
            root = node;
        }
        return root;
    }

    /** Tree whose preorder is lo, lo + 1, ..., hi: root lo, left half and right half of the rest, recursively. */
    static TreeNode preorderNumbered(int lo, int hi) {
        if (lo > hi) return null;
        TreeNode node = new TreeNode(lo);
        int rest = hi - lo, leftSize = rest / 2;
        node.left = preorderNumbered(lo + 1, lo + leftSize);
        node.right = preorderNumbered(lo + leftSize + 1, hi);
        return node;
    }

    static List<Integer> list(int... values) {
        List<Integer> out = new ArrayList<>();
        for (int v : values) out.add(v);
        return out;
    }

    static List<Integer> range(int lo, int hi) {
        List<Integer> out = new ArrayList<>();
        for (int v = lo; v <= hi; v++) out.add(v);
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode tree, List<Integer> expected, String label) {
        check(recursive(tree).equals(expected), "recursive " + label);
        check(iterative(tree).equals(expected), "iterative " + label);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, null, 6), list(1, 2, 4, 5, 3, 6), "example 1");
        verify(build(1, null, 2, 3), list(1, 2, 3), "example 2");
        verify(build(), list(), "empty tree");                                     // edge case
        verify(build(7), list(7), "single node");
        verify(build(1, 2, null, 3, null, 4), list(1, 2, 3, 4), "left chain");
        verify(build(1, 2, 3, 4, 5, 6, 7), list(1, 2, 4, 5, 3, 6, 7), "perfect tree");
        verify(build(0, -5, 5, -5, 0), list(0, -5, -5, 0, 5), "duplicates and negatives");
        verify(rightChain(5_000), range(1, 5_000), "deep right chain of 5000 nodes");
        verify(preorderNumbered(1, 100_000), range(1, 100_000), "balanced tree of 100000 nodes");
        System.out.println("OK P2790_PreorderTraversal");
    }
}
