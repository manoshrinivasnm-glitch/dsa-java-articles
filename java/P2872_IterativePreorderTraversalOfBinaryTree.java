import java.util.*;

/** TUF 2872 - Iterative Preorder Traversal of Binary Tree. Return the preorder (Root, Left, Right) sequence without recursion. */
public class P2872_IterativePreorderTraversalOfBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion, the reference answer the iterative versions must reproduce. O(n) time, O(h) call stack. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();  // values in preorder
        preorder(root, out);                    // the helper appends every node
        return out;
    }

    private static void preorder(TreeNode node, List<Integer> out) {
        if (node == null) return;                 // empty subtree: nothing to visit
        out.add(node.val);                        // Root
        preorder(node.left, out);                 // Left
        preorder(node.right, out);                // Right
    }

    /** Approach 2: one explicit stack; pop a node, visit it, push right then left. O(n) time, O(h) space. */
    static List<Integer> iterativeStack(TreeNode root) {
        List<Integer> out = new ArrayList<>();                // values in preorder
        if (root == null) return out;                         // empty tree: nothing to visit
        Deque<TreeNode> stack = new ArrayDeque<>();           // nodes waiting to be visited, next one on top
        stack.push(root);                                     // start at the root
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();                      // the next node in preorder
            out.add(node.val);                                // visit as soon as the node is taken off
            if (node.right != null) stack.push(node.right);   // right waits underneath ...
            if (node.left != null) stack.push(node.left);     // ... left sits on top and is processed next
        }
        return out;
    }

    /** Approach 3: walk down left children directly and stack only the right children still to explore. O(n) time, O(h) space. */
    static List<Integer> iterativeRightStack(TreeNode root) {
        List<Integer> out = new ArrayList<>();                // values in preorder
        Deque<TreeNode> pendingRight = new ArrayDeque<>();    // right children saved for later, latest on top
        TreeNode cur = root;                                  // the node the walk is standing on
        while (cur != null || !pendingRight.isEmpty()) {      // until the left path ends and nothing is pending
            if (cur == null) cur = pendingRight.pop();        // left path ended: resume the latest unexplored right subtree
            out.add(cur.val);                                 // visit on arrival (Root)
            if (cur.right != null) pendingRight.push(cur.right);  // save the right child for after the left side
            cur = cur.left;                                   // keep going left
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Build a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int i = 1;
        while (!queue.isEmpty() && i < vals.length) {
            TreeNode node = queue.poll();
            if (i < vals.length && vals[i] != null) { node.left = new TreeNode(vals[i]); queue.offer(node.left); }
            i++;
            if (i < vals.length && vals[i] != null) { node.right = new TreeNode(vals[i]); queue.offer(node.right); }
            i++;
        }
        return root;
    }

    /** A chain of n nodes valued 1..n, each the left (or right) child of the previous one. Built without recursion. */
    static TreeNode chain(int n, boolean leftward) {
        TreeNode root = new TreeNode(1), cur = root;
        for (int v = 2; v <= n; v++) {
            TreeNode next = new TreeNode(v);
            if (leftward) cur.left = next; else cur.right = next;
            cur = next;
        }
        return root;
    }

    static List<Integer> range(int from, int to) {
        List<Integer> out = new ArrayList<>();
        for (int v = from; v <= to; v++) out.add(v);
        return out;
    }

    static void verify(TreeNode root, List<Integer> expected) {
        List<Integer> r1 = recursive(root), r2 = iterativeStack(root), r3 = iterativeRightStack(root);
        check(r1.equals(expected), "recursive gave " + r1 + ", expected " + expected);
        check(r2.equals(expected), "iterativeStack gave " + r2 + ", expected " + expected);
        check(r3.equals(expected), "iterativeRightStack gave " + r3 + ", expected " + expected);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, 6, 7), List.of(1, 2, 4, 5, 3, 6, 7));
        verify(build(1, 2, 3, null, 4, 5, null, null, null, 6), List.of(1, 2, 4, 3, 5, 6));
        verify(build(1, null, 2, 3), List.of(1, 2, 3));
        verify(build(), List.of());                                          // edge: empty tree
        verify(build(42), List.of(42));                                      // single node
        verify(build(1, 2, null, 3, null, 4), List.of(1, 2, 3, 4));          // left-skewed
        verify(build(1, null, 2, null, 3, null, 4), List.of(1, 2, 3, 4));    // right-skewed

        int n = 100_000;                                                     // deep trees: no call stack involved
        check(iterativeStack(chain(n, true)).equals(range(1, n)), "iterativeStack on a deep left chain");
        check(iterativeStack(chain(n, false)).equals(range(1, n)), "iterativeStack on a deep right chain");
        check(iterativeRightStack(chain(n, true)).equals(range(1, n)), "iterativeRightStack on a deep left chain");
        check(iterativeRightStack(chain(n, false)).equals(range(1, n)), "iterativeRightStack on a deep right chain");
        System.out.println("OK P2872_IterativePreorderTraversalOfBinaryTree");
    }
}
