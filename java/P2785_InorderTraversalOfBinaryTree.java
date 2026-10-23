import java.util.*;

/** TUF 2785 - Inorder Traversal of Binary Tree. Return the values in inorder: Left subtree, then Root, then Right subtree. */
public class P2785_InorderTraversalOfBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion, the definition written as code. O(n) time, O(h) call stack. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();  // values in inorder
        inorder(root, out);                     // the helper appends every node
        return out;
    }

    private static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;                 // empty subtree: nothing to visit
        inorder(node.left, out);                  // Left
        out.add(node.val);                        // Root
        inorder(node.right, out);                 // Right
    }

    /** Approach 2: iterative; stack the left spine, pop the leftmost unvisited node, then move into its right subtree. O(n) time, O(h) space. */
    static List<Integer> iterative(TreeNode root) {
        List<Integer> out = new ArrayList<>();       // values in inorder
        Deque<TreeNode> stack = new ArrayDeque<>();  // nodes whose left subtree is still being walked
        TreeNode cur = root;                         // the node the walk is standing on
        while (cur != null || !stack.isEmpty()) {    // until there is nothing left to walk or pop
            while (cur != null) {                    // walk down the left spine, remembering every node
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();                       // leftmost node whose left subtree is finished
            out.add(cur.val);                        // Root comes after its whole left subtree
            cur = cur.right;                         // its right subtree is handled the same way
        }
        return out;
    }

    /** Approach 3: Morris traversal; a temporary thread from the predecessor replaces the stack. O(n) time, O(1) extra space, tree restored. */
    static List<Integer> morris(TreeNode root) {
        List<Integer> out = new ArrayList<>();                // values in inorder
        TreeNode cur = root;                                  // the node the walk is standing on
        while (cur != null) {                                 // until the walk falls off the tree
            if (cur.left == null) {
                out.add(cur.val);                             // nothing on the left: visit and go right
                cur = cur.right;
            } else {
                TreeNode pred = cur.left;                     // inorder predecessor = rightmost node of the left subtree
                // go to that rightmost node, stopping early at a thread back to cur
                while (pred.right != null && pred.right != cur) pred = pred.right;
                if (pred.right == null) {                     // first arrival: leave a thread back to cur, go left
                    pred.right = cur;                         // the thread: how the walk gets back to cur
                    cur = cur.left;                           // now walk the left subtree
                } else {                                      // second arrival: left subtree done, unthread and visit
                    pred.right = null;                        // undo the thread, restoring the tree
                    out.add(cur.val);                         // Root after its left subtree: inorder
                    cur = cur.right;                          // continue with the right subtree
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
        verify(build(1, null, 2, 3), List.of(1, 3, 2));
        verify(build(1, 2, 3, 4, 5, null, 8, null, null, 6, 7, 9), List.of(4, 2, 6, 5, 7, 1, 3, 9, 8));
        verify(build(1, 2, 3, 4, 5, 6, 7), List.of(4, 2, 5, 1, 6, 3, 7));
        verify(build(4, 2, 6, 1, 3, 5, 7), List.of(1, 2, 3, 4, 5, 6, 7));     // a BST comes out sorted
        verify(build(), List.of());                                          // edge: empty tree
        verify(build(42), List.of(42));                                      // single node
        verify(build(1, 2, null, 3, null, 4), List.of(4, 3, 2, 1));          // left-skewed

        int n = 100_000;                                                     // deep trees: no call stack involved
        check(iterative(chain(n, true)).equals(range(n, 1)), "iterative on a deep left chain");
        check(iterative(chain(n, false)).equals(range(1, n)), "iterative on a deep right chain");
        check(morris(chain(n, true)).equals(range(n, 1)), "morris on a deep left chain");
        check(morris(chain(n, false)).equals(range(1, n)), "morris on a deep right chain");
        System.out.println("OK P2785_InorderTraversalOfBinaryTree");
    }
}
