import java.util.*;

/** TUF 108 - Search in BST. Return the node whose value equals val (the subtree rooted there), or null. */
public class P108_SearchInBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: search the whole tree, ignoring the ordering. O(n) time, O(h) stack. */
    static TreeNode bruteForce(TreeNode root, int val) {
        if (root == null || root.val == val) return root;
        TreeNode found = bruteForce(root.left, val);
        return found != null ? found : bruteForce(root.right, val);
    }

    /** Approach 2: recursion that follows only the side that can contain val. O(h) time, O(h) stack. */
    static TreeNode recursive(TreeNode root, int val) {
        if (root == null || root.val == val) return root;
        return val < root.val ? recursive(root.left, val) : recursive(root.right, val);
    }

    /** Approach 3: the same single downward path as a loop. O(h) time, O(1) space. */
    static TreeNode iterative(TreeNode root, int val) {
        TreeNode cur = root;
        while (cur != null && cur.val != val) {
            cur = val < cur.val ? cur.left : cur.right;      // discard the half that cannot hold val
        }
        return cur;
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

    /** Balanced BST holding lo..hi. */
    static TreeNode balanced(int lo, int hi) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;
        TreeNode node = new TreeNode(mid);
        node.left = balanced(lo, mid - 1);
        node.right = balanced(mid + 1, hi);
        return node;
    }

    /** Reference answer: walk every node and remember the one with this value (values are distinct). */
    static TreeNode locate(TreeNode root, int val) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        if (root != null) stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (node.val == val) return node;
            if (node.left != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
        }
        return null;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** expectFound: whether val is in the tree. The returned node must be the very node in the tree, not a copy. */
    static void verify(TreeNode root, int val, boolean expectFound) {
        TreeNode expected = locate(root, val);
        check((expected != null) == expectFound, "test setup for val " + val);
        check(bruteForce(root, val) == expected, "bruteForce val " + val);
        check(recursive(root, val) == expected, "recursive val " + val);
        check(iterative(root, val) == expected, "iterative val " + val);
    }

    public static void main(String[] args) {
        TreeNode t = build(4, 2, 7, 1, 3);
        verify(t, 2, true);                                          // example 1: subtree [2, 1, 3]
        check(t.left == iterative(t, 2) && iterative(t, 2).left.val == 1, "subtree of 2");
        verify(t, 5, false);                                         // example 2: absent, falls off below 7
        verify(t, 4, true);                                          // root itself
        verify(t, 3, true);                                          // leaf
        verify(t, 0, false);                                         // smaller than everything
        verify(t, 100, false);                                       // larger than everything
        verify(null, 1, false);                                      // edge case: empty tree
        verify(build(-10), -10, true);                               // single node, negative value
        verify(build(-10), 10, false);
        verify(build(Integer.MIN_VALUE, null, Integer.MAX_VALUE), Integer.MAX_VALUE, true);   // extreme values

        TreeNode big = balanced(1, 100_000);
        for (int v : new int[]{1, 50_000, 77_777, 100_000}) verify(big, v, true);
        verify(big, 0, false);
        verify(big, 100_001, false);
        System.out.println("OK P108_SearchInBST");
    }
}
