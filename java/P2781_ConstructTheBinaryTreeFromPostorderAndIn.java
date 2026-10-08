import java.util.*;

/** TUF 2781 - Construct the Binary Tree from Postorder and Inorder Traversal. Values are distinct; return the root. */
public class P2781_ConstructTheBinaryTreeFromPostorderAndIn {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force, split recursively and find the root in inorder by a linear scan. O(n^2) worst-case time, O(n) space. */
    static TreeNode bruteForce(int[] inorder, int[] postorder) {
        return buildScan(postorder, 0, postorder.length - 1, inorder, 0);
    }

    // builds the tree whose postorder is post[postStart..postEnd] and whose inorder starts at in[inStart]
    static TreeNode buildScan(int[] post, int postStart, int postEnd, int[] in, int inStart) {
        if (postStart > postEnd) return null;
        TreeNode root = new TreeNode(post[postEnd]);           // postorder always ends with the root
        int rootIndex = inStart;
        while (in[rootIndex] != root.val) rootIndex++;         // linear search: O(size of this subtree)
        int leftSize = rootIndex - inStart;
        root.left = buildScan(post, postStart, postStart + leftSize - 1, in, inStart);
        root.right = buildScan(post, postStart + leftSize, postEnd - 1, in, rootIndex + 1);
        return root;
    }

    /** Approach 2: optimal, a HashMap from value to inorder index finds every root in O(1). O(n) time, O(n) space. */
    static TreeNode optimal(int[] inorder, int[] postorder) {
        Map<Integer, Integer> inIndex = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) inIndex.put(inorder[i], i);
        return buildMap(postorder, 0, postorder.length - 1, 0, inIndex);
    }

    static TreeNode buildMap(int[] post, int postStart, int postEnd, int inStart, Map<Integer, Integer> inIndex) {
        if (postStart > postEnd) return null;
        TreeNode root = new TreeNode(post[postEnd]);
        int rootIndex = inIndex.get(root.val);
        int leftSize = rootIndex - inStart;
        root.left = buildMap(post, postStart, postStart + leftSize - 1, inStart, inIndex);
        root.right = buildMap(post, postStart + leftSize, postEnd - 1, rootIndex + 1, inIndex);
        return root;
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

    static boolean same(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && same(a.left, b.left) && same(a.right, b.right);
    }

    static void inorderOf(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorderOf(node.left, out);
        out.add(node.val);
        inorderOf(node.right, out);
    }

    static void postorderOf(TreeNode node, List<Integer> out) {
        if (node == null) return;
        postorderOf(node.left, out);
        postorderOf(node.right, out);
        out.add(node.val);
    }

    /** Random BST over 0..n-1 (fixed seed), used as a larger test whose traversals come from the tree itself. */
    static TreeNode randomTree(int n, long seed) {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < n; i++) values.add(i);
        Collections.shuffle(values, new Random(seed));
        TreeNode root = null;
        for (int v : values) {
            TreeNode node = new TreeNode(v);
            if (root == null) {
                root = node;
                continue;
            }
            TreeNode cur = root;
            while (true) {
                if (v < cur.val) {
                    if (cur.left == null) { cur.left = node; break; }
                    cur = cur.left;
                } else {
                    if (cur.right == null) { cur.right = node; break; }
                    cur = cur.right;
                }
            }
        }
        return root;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] inorder, int[] postorder, TreeNode expected, String label) {
        check(same(bruteForce(inorder, postorder), expected), "bruteForce " + label);
        check(same(optimal(inorder, postorder), expected), "optimal " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{9, 3, 15, 20, 7}, new int[]{9, 15, 7, 20, 3}, build(3, 9, 20, null, null, 15, 7), "example 1");
        verify(new int[]{-1}, new int[]{-1}, build(-1), "single node");
        verify(new int[]{}, new int[]{}, build(), "empty tree");                                   // edge case
        verify(new int[]{4, 3, 2, 1}, new int[]{4, 3, 2, 1}, build(1, 2, null, 3, null, 4), "left chain");
        verify(new int[]{1, 2, 3}, new int[]{3, 2, 1}, build(1, null, 2, null, 3), "right chain");
        verify(new int[]{4, 2, 5, 1, 6, 3, 7}, new int[]{4, 5, 2, 6, 7, 3, 1}, build(1, 2, 3, 4, 5, 6, 7), "perfect tree");
        verify(new int[]{4, 2, 8, 5, 9, 1, 6, 3, 7}, new int[]{4, 8, 9, 5, 2, 6, 7, 3, 1},
                build(1, 2, 3, 4, 5, 6, 7, null, null, 8, 9), "uneven tree");
        verify(new int[]{1, 5, 2, 3, 4}, new int[]{1, 2, 3, 4, 5}, build(5, 1, 4, null, null, 3, null, 2), "zig-zag on the right");

        TreeNode big = randomTree(3000, 42);
        List<Integer> in = new ArrayList<>(), post = new ArrayList<>();
        inorderOf(big, in);
        postorderOf(big, post);
        verify(in.stream().mapToInt(Integer::intValue).toArray(), post.stream().mapToInt(Integer::intValue).toArray(),
                big, "random tree of 3000 nodes");
        System.out.println("OK P2781_ConstructTheBinaryTreeFromPostorderAndIn");
    }
}
