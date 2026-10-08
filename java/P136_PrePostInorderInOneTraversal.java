import java.util.*;

/** TUF 136 - Pre, Post, Inorder in one traversal. Return [preorder, inorder, postorder] of a binary tree from a single walk. */
public class P136_PrePostInorderInOneTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** A stack frame for the iterative walk: the node and how many times we have arrived at it so far. */
    static class Frame {
        TreeNode node;
        int state = 1;                            // 1 = pre, 2 = in, 3 = post
        Frame(TreeNode node) { this.node = node; }
    }

    /** Approach 1: three independent recursive traversals, one per order. O(3n) time, O(h) call stack. */
    static List<List<Integer>> threeTraversals(TreeNode root) {
        List<Integer> pre = new ArrayList<>(), in = new ArrayList<>(), post = new ArrayList<>();
        preorder(root, pre);
        inorder(root, in);
        postorder(root, post);
        return List.of(pre, in, post);
    }

    private static void preorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        out.add(node.val);
        preorder(node.left, out);
        preorder(node.right, out);
    }

    private static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    private static void postorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        postorder(node.left, out);
        postorder(node.right, out);
        out.add(node.val);
    }

    /** Approach 2: one recursive walk that records the node at each of its three visits. O(n) time, O(h) call stack. */
    static List<List<Integer>> oneRecursion(TreeNode root) {
        List<Integer> pre = new ArrayList<>(), in = new ArrayList<>(), post = new ArrayList<>();
        walk(root, pre, in, post);
        return List.of(pre, in, post);
    }

    private static void walk(TreeNode node, List<Integer> pre, List<Integer> in, List<Integer> post) {
        if (node == null) return;
        pre.add(node.val);                        // 1st visit: arriving from the parent
        walk(node.left, pre, in, post);
        in.add(node.val);                         // 2nd visit: back from the left subtree
        walk(node.right, pre, in, post);
        post.add(node.val);                       // 3rd visit: back from the right subtree
    }

    /** Approach 3: one explicit stack of (node, state) frames; the state says which visit comes next. O(n) time, O(h) space. */
    static List<List<Integer>> oneStack(TreeNode root) {
        List<Integer> pre = new ArrayList<>(), in = new ArrayList<>(), post = new ArrayList<>();
        if (root == null) return List.of(pre, in, post);
        Deque<Frame> stack = new ArrayDeque<>();
        stack.push(new Frame(root));
        while (!stack.isEmpty()) {
            Frame top = stack.peek();
            if (top.state == 1) {                 // first visit: preorder, then descend left
                pre.add(top.node.val);
                top.state = 2;
                if (top.node.left != null) stack.push(new Frame(top.node.left));
            } else if (top.state == 2) {          // second visit: inorder, then descend right
                in.add(top.node.val);
                top.state = 3;
                if (top.node.right != null) stack.push(new Frame(top.node.right));
            } else {                              // third visit: postorder, the node is finished
                post.add(top.node.val);
                stack.pop();
            }
        }
        return List.of(pre, in, post);
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

    /** A chain of n nodes valued 1..n, each the right child of the previous one. Built without recursion. */
    static TreeNode rightChain(int n) {
        TreeNode root = new TreeNode(1), cur = root;
        for (int v = 2; v <= n; v++) {
            cur.right = new TreeNode(v);
            cur = cur.right;
        }
        return root;
    }

    static void verify(TreeNode root, List<Integer> pre, List<Integer> in, List<Integer> post) {
        List<List<Integer>> expected = List.of(pre, in, post);
        List<List<Integer>> r1 = threeTraversals(root), r2 = oneRecursion(root), r3 = oneStack(root);
        check(r1.equals(expected), "threeTraversals gave " + r1 + ", expected " + expected);
        check(r2.equals(expected), "oneRecursion gave " + r2 + ", expected " + expected);
        check(r3.equals(expected), "oneStack gave " + r3 + ", expected " + expected);
    }

    public static void main(String[] args) {
        verify(build(1, 2, 3, 4, 5, 6, 7),
                List.of(1, 2, 4, 5, 3, 6, 7), List.of(4, 2, 5, 1, 6, 3, 7), List.of(4, 5, 2, 6, 7, 3, 1));
        verify(build(1, 2, 3, null, 4, 5, null, null, null, 6),
                List.of(1, 2, 4, 3, 5, 6), List.of(2, 4, 1, 6, 5, 3), List.of(4, 2, 6, 5, 3, 1));
        verify(build(), List.of(), List.of(), List.of());                    // edge: empty tree
        verify(build(42), List.of(42), List.of(42), List.of(42));            // single node
        verify(build(1, 2, null, 3), List.of(1, 2, 3), List.of(3, 2, 1), List.of(3, 2, 1));                // left-skewed
        verify(build(1, null, 2, null, 3), List.of(1, 2, 3), List.of(1, 2, 3), List.of(3, 2, 1));          // right-skewed

        int n = 100_000;                                                     // deep tree: only the stack version survives it
        List<Integer> up = new ArrayList<>(), down = new ArrayList<>();
        for (int v = 1; v <= n; v++) up.add(v);
        for (int v = n; v >= 1; v--) down.add(v);
        check(oneStack(rightChain(n)).equals(List.of(up, up, down)), "oneStack on a deep right chain");
        System.out.println("OK P136_PrePostInorderInOneTraversal");
    }
}
