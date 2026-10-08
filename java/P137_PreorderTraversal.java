import java.util.*;

/** TUF 137 - Preorder Traversal. Return the values of a binary tree in preorder: Root, then Left subtree, then Right subtree. */
public class P137_PreorderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion, the definition written as code. O(n) time, O(h) call stack. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        preorder(root, out);
        return out;
    }

    private static void preorder(TreeNode node, List<Integer> out) {
        if (node == null) return;                 // empty subtree: nothing to visit
        out.add(node.val);                        // Root
        preorder(node.left, out);                 // Left
        preorder(node.right, out);                // Right
    }

    /** Approach 2: iterative with an explicit stack; push right before left so left is popped first. O(n) time, O(h) space. */
    static List<Integer> iterative(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            out.add(node.val);
            if (node.right != null) stack.push(node.right);   // pushed first, popped last
            if (node.left != null) stack.push(node.left);     // pushed last, popped next
        }
        return out;
    }

    /** Approach 3: Morris traversal; temporary threads replace the stack. O(n) time, O(1) extra space, tree restored. */
    static List<Integer> morris(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) {
                out.add(cur.val);                             // no left subtree: visit and go right
                cur = cur.right;
            } else {
                TreeNode pred = cur.left;                     // rightmost node of the left subtree
                while (pred.right != null && pred.right != cur) pred = pred.right;
                if (pred.right == null) {                     // first arrival: visit, leave a thread back, go left
                    out.add(cur.val);
                    pred.right = cur;
                    cur = cur.left;
                } else {                                      // second arrival: left subtree done, remove the thread
                    pred.right = null;
                    cur = cur.right;
                }
            }
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
        if (from <= to) {
            for (int v = from; v <= to; v++) out.add(v);
        } else {
            for (int v = from; v >= to; v--) out.add(v);
        }
        return out;
    }

    static void verify(TreeNode root, List<Integer> expected) {
        List<Integer> r1 = recursive(root), r2 = iterative(root), r3 = morris(root);
        check(r1.equals(expected), "recursive gave " + r1 + ", expected " + expected);
        check(r2.equals(expected), "iterative gave " + r2 + ", expected " + expected);
        check(r3.equals(expected), "morris gave " + r3 + ", expected " + expected);
        List<Integer> again = recursive(root);
        check(again.equals(expected), "morris did not restore the tree: " + again);
    }

    public static void main(String[] args) {
        verify(build(1, null, 2, 3), List.of(1, 2, 3));
        verify(build(1, 2, 3, 4, 5, null, 8, null, null, 6, 7, 9), List.of(1, 2, 4, 5, 6, 7, 3, 8, 9));
        verify(build(1, 2, 3, 4, 5, 6, 7), List.of(1, 2, 4, 5, 3, 6, 7));
        verify(build(), List.of());                                        // edge: empty tree
        verify(build(42), List.of(42));                                    // single node
        verify(build(1, 2, null, 3, null, 4), List.of(1, 2, 3, 4));        // left-skewed
        verify(build(5, -3, 8, -3, 0), List.of(5, -3, -3, 0, 8));          // duplicates and negatives

        int n = 100_000;                                                   // deep trees: no call stack involved
        check(iterative(chain(n, true)).equals(range(1, n)), "iterative on a deep left chain");
        check(iterative(chain(n, false)).equals(range(1, n)), "iterative on a deep right chain");
        check(morris(chain(n, true)).equals(range(1, n)), "morris on a deep left chain");
        check(morris(chain(n, false)).equals(range(1, n)), "morris on a deep right chain");
        System.out.println("OK P137_PreorderTraversal");
    }
}
