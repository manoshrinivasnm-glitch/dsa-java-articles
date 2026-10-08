import java.util.*;

/** TUF 2789 - Preorder, Inorder, and Postorder Traversal in one Traversal. Return the three traversals as [pre, in, post]. */
public class P2789_PreorderInorderAndPostorderTraversalIn {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** A stack entry: a node plus how many times we have already come back to it. */
    static class Pair {
        TreeNode node;
        int state;
        Pair(TreeNode node, int state) { this.node = node; this.state = state; }
    }

    /** Approach 1: three independent recursive traversals. O(n) time per walk (3 walks), O(h) stack. */
    static List<List<Integer>> threeTraversals(TreeNode root) {
        List<Integer> pre = new ArrayList<>(), in = new ArrayList<>(), post = new ArrayList<>();
        preorder(root, pre);
        inorder(root, in);
        postorder(root, post);
        return List.of(pre, in, post);
    }

    static void preorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        out.add(node.val);
        preorder(node.left, out);
        preorder(node.right, out);
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    static void postorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        postorder(node.left, out);
        postorder(node.right, out);
        out.add(node.val);
    }

    /** Approach 2: one recursive walk. Each node is met before its left subtree, between its subtrees and after its right subtree; record it in pre, in and post at those three moments. O(n) time, O(h) stack. */
    static List<List<Integer>> oneRecursiveWalk(TreeNode root) {
        List<Integer> pre = new ArrayList<>(), in = new ArrayList<>(), post = new ArrayList<>();
        walk(root, pre, in, post);
        return List.of(pre, in, post);
    }

    static void walk(TreeNode node, List<Integer> pre, List<Integer> in, List<Integer> post) {
        if (node == null) return;
        pre.add(node.val);
        walk(node.left, pre, in, post);
        in.add(node.val);
        walk(node.right, pre, in, post);
        post.add(node.val);
    }

    /** Approach 3: one iterative walk with a stack of (node, state). State 1 = first visit (preorder), 2 = back from the left subtree (inorder), 3 = back from the right subtree (postorder). O(n) time, O(h) space. */
    static List<List<Integer>> oneIterativeWalk(TreeNode root) {
        List<Integer> pre = new ArrayList<>(), in = new ArrayList<>(), post = new ArrayList<>();
        if (root == null) return List.of(pre, in, post);
        Deque<Pair> st = new ArrayDeque<>();
        st.push(new Pair(root, 1));
        while (!st.isEmpty()) {
            Pair top = st.pop();
            if (top.state == 1) {
                pre.add(top.node.val);
                top.state = 2;
                st.push(top);
                if (top.node.left != null) st.push(new Pair(top.node.left, 1));
            } else if (top.state == 2) {
                in.add(top.node.val);
                top.state = 3;
                st.push(top);
                if (top.node.right != null) st.push(new Pair(top.node.right, 1));
            } else {
                post.add(top.node.val);
            }
        }
        return List.of(pre, in, post);
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

    static TreeNode randomTree(Random rnd, int depth) {
        if (depth == 0 || rnd.nextInt(4) == 0) return null;
        TreeNode node = new TreeNode(rnd.nextInt(100));
        node.left = randomTree(rnd, depth - 1);
        node.right = randomTree(rnd, depth - 1);
        return node;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] level, List<Integer> pre, List<Integer> in, List<Integer> post) {
        TreeNode root = build(level);
        List<List<Integer>> expected = List.of(pre, in, post);
        String s = Arrays.toString(level);
        check(threeTraversals(root).equals(expected), "threeTraversals " + s);
        check(oneRecursiveWalk(root).equals(expected), "oneRecursiveWalk " + s);
        check(oneIterativeWalk(root).equals(expected), "oneIterativeWalk " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7},
                List.of(1, 2, 4, 5, 3, 6, 7), List.of(4, 2, 5, 1, 6, 3, 7), List.of(4, 5, 2, 6, 7, 3, 1));
        verify(new Integer[]{1, null, 2, 3},
                List.of(1, 2, 3), List.of(1, 3, 2), List.of(3, 2, 1));
        verify(new Integer[]{}, List.of(), List.of(), List.of());                            // edge: empty tree
        verify(new Integer[]{1}, List.of(1), List.of(1), List.of(1));                        // single node
        verify(new Integer[]{1, 2, 3, null, 4, 5, null, null, 6},
                List.of(1, 2, 4, 6, 3, 5), List.of(2, 4, 6, 1, 5, 3), List.of(6, 4, 2, 5, 3, 1));
        verify(new Integer[]{5, 3, 8, 1, 4, 7, 9},                                          // BST: inorder comes out sorted
                List.of(5, 3, 1, 4, 8, 7, 9), List.of(1, 3, 4, 5, 7, 8, 9), List.of(1, 4, 3, 7, 9, 8, 5));

        // seeded random trees: the single walks must match three separate walks
        Random rnd = new Random(2789);
        for (int t = 0; t < 300; t++) {
            TreeNode root = randomTree(rnd, 8);
            List<List<Integer>> expected = threeTraversals(root);
            check(oneRecursiveWalk(root).equals(expected), "oneRecursiveWalk random #" + t);
            check(oneIterativeWalk(root).equals(expected), "oneIterativeWalk random #" + t);
        }
        System.out.println("OK P2789_PreorderInorderAndPostorderTraversalIn");
    }
}
