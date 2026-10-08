import java.util.*;

/** TUF 135 - Postorder Traversal. Return the values in postorder: Left subtree, then Right subtree, then Root. */
public class P135_PostorderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion, the definition written as code. O(n) time, O(h) call stack. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        postorder(root, out);
        return out;
    }

    private static void postorder(TreeNode node, List<Integer> out) {
        if (node == null) return;                 // empty subtree: nothing to visit
        postorder(node.left, out);                // Left
        postorder(node.right, out);               // Right
        out.add(node.val);                        // Root, only after both subtrees are finished
    }

    /** Approach 2: produce Root-Right-Left with a preorder-style stack, then reverse it. O(n) time, O(h) stack besides the output. */
    static List<Integer> reversedPreorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            out.add(node.val);                                // builds Root, Right, Left
            if (node.left != null) stack.push(node.left);     // left pushed first so right is popped first
            if (node.right != null) stack.push(node.right);
        }
        Collections.reverse(out);                             // reverse of Root-Right-Left is Left-Right-Root
        return out;
    }

    /** Approach 3: one stack and a lastVisited pointer; a node is emitted only when its right subtree is finished. O(n) time, O(h) space. */
    static List<Integer> oneStack(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root, lastVisited = null;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {                             // go as far left as possible
                stack.push(cur);
                cur = cur.left;
            }
            TreeNode top = stack.peek();                      // its left subtree is finished
            if (top.right != null && top.right != lastVisited) {
                cur = top.right;                              // right subtree not done yet: go there first
            } else {
                out.add(top.val);                             // both subtrees done: emit and retire it
                lastVisited = stack.pop();
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
        List<Integer> r1 = recursive(root), r2 = reversedPreorder(root), r3 = oneStack(root);
        check(r1.equals(expected), "recursive gave " + r1 + ", expected " + expected);
        check(r2.equals(expected), "reversedPreorder gave " + r2 + ", expected " + expected);
        check(r3.equals(expected), "oneStack gave " + r3 + ", expected " + expected);
    }

    public static void main(String[] args) {
        verify(build(1, null, 2, 3), List.of(3, 2, 1));
        verify(build(1, 2, 3, 4, 5, null, 8, null, null, 6, 7, 9), List.of(4, 6, 7, 5, 2, 9, 8, 3, 1));
        verify(build(1, 2, 3, 4, 5, 6, 7), List.of(4, 5, 2, 6, 7, 3, 1));
        verify(build(), List.of());                                          // edge: empty tree
        verify(build(42), List.of(42));                                      // single node
        verify(build(1, 2, null, 3, null, 4), List.of(4, 3, 2, 1));          // left-skewed
        verify(build(1, null, 2, null, 3, null, 4), List.of(4, 3, 2, 1));    // right-skewed

        int n = 100_000;                                                     // deep trees: no call stack involved
        check(reversedPreorder(chain(n, true)).equals(range(n, 1)), "reversedPreorder on a deep left chain");
        check(reversedPreorder(chain(n, false)).equals(range(n, 1)), "reversedPreorder on a deep right chain");
        check(oneStack(chain(n, true)).equals(range(n, 1)), "oneStack on a deep left chain");
        check(oneStack(chain(n, false)).equals(range(n, 1)), "oneStack on a deep right chain");
        System.out.println("OK P135_PostorderTraversal");
    }
}
