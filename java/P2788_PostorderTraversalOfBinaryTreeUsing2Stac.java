import java.util.*;

/** TUF 2788 - Post-order Traversal of Binary Tree using 2 stack. Return the postorder (Left, Right, Root) sequence without recursion. */
public class P2788_PostorderTraversalOfBinaryTreeUsing2Stac {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion, the reference answer. O(n) time, O(h) call stack. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        postorder(root, out);
        return out;
    }

    private static void postorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        postorder(node.left, out);                // Left
        postorder(node.right, out);               // Right
        out.add(node.val);                        // Root
    }

    /** Approach 2: two stacks. Stack 1 generates Root-Right-Left, stack 2 reverses it into Left-Right-Root. O(n) time, O(n) space. */
    static List<Integer> twoStacks(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> s1 = new ArrayDeque<>(), s2 = new ArrayDeque<>();
        s1.push(root);
        while (!s1.isEmpty()) {
            TreeNode node = s1.pop();
            s2.push(node);                                    // s2 receives Root, Right, Left (bottom to top)
            if (node.left != null) s1.push(node.left);        // left pushed first ...
            if (node.right != null) s1.push(node.right);      // ... so right is popped first
        }
        while (!s2.isEmpty()) out.add(s2.pop().val);          // popping s2 reverses it: Left, Right, Root
        return out;
    }

    /** Approach 3: one stack, with the second stack replaced by prepending to the output list. O(n) time, O(h) stack besides the output. */
    static List<Integer> oneStackPrepend(TreeNode root) {
        LinkedList<Integer> out = new LinkedList<>();
        if (root == null) return out;
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            out.addFirst(node.val);                           // prepending reverses the order as it is built
            if (node.left != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
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
        List<Integer> r1 = recursive(root), r2 = twoStacks(root), r3 = oneStackPrepend(root);
        check(r1.equals(expected), "recursive gave " + r1 + ", expected " + expected);
        check(r2.equals(expected), "twoStacks gave " + r2 + ", expected " + expected);
        check(r3.equals(expected), "oneStackPrepend gave " + r3 + ", expected " + expected);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, 6, 7), List.of(4, 5, 2, 6, 7, 3, 1));
        verify(build(1, 2, 3, 4, 5, null, 8, null, null, 6, 7, 9), List.of(4, 6, 7, 5, 2, 9, 8, 3, 1));
        verify(build(1, null, 2, 3), List.of(3, 2, 1));
        verify(build(), List.of());                                          // edge: empty tree
        verify(build(42), List.of(42));                                      // single node
        verify(build(1, 2, null, 3, null, 4), List.of(4, 3, 2, 1));          // left-skewed
        verify(build(1, null, 2, null, 3, null, 4), List.of(4, 3, 2, 1));    // right-skewed
        verify(build(7, 7, 7, null, 7), List.of(7, 7, 7, 7));                // equal values

        int n = 100_000;                                                     // deep trees: no call stack involved
        check(twoStacks(chain(n, true)).equals(range(n, 1)), "twoStacks on a deep left chain");
        check(twoStacks(chain(n, false)).equals(range(n, 1)), "twoStacks on a deep right chain");
        check(oneStackPrepend(chain(n, true)).equals(range(n, 1)), "oneStackPrepend on a deep left chain");
        check(oneStackPrepend(chain(n, false)).equals(range(n, 1)), "oneStackPrepend on a deep right chain");
        System.out.println("OK P2788_PostorderTraversalOfBinaryTreeUsing2Stac");
    }
}
