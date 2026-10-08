import java.util.*;

/** TUF 110 - Construct a BT from Preorder and Inorder. Values are distinct; return the root of the one tree with both traversals. */
public class P110_ConstructABTFromPreorderAndInorder {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force, split recursively and find the root in inorder by a linear scan. O(n^2) worst-case time, O(n) space. */
    static TreeNode bruteForce(int[] preorder, int[] inorder) {
        return buildScan(preorder, 0, preorder.length - 1, inorder, 0);
    }

    // builds the tree whose preorder is pre[preStart..preEnd] and whose inorder starts at in[inStart]
    static TreeNode buildScan(int[] pre, int preStart, int preEnd, int[] in, int inStart) {
        if (preStart > preEnd) return null;
        TreeNode root = new TreeNode(pre[preStart]);           // preorder always starts with the root
        int rootIndex = inStart;
        while (in[rootIndex] != root.val) rootIndex++;         // linear search: O(size of this subtree)
        int leftSize = rootIndex - inStart;
        root.left = buildScan(pre, preStart + 1, preStart + leftSize, in, inStart);
        root.right = buildScan(pre, preStart + leftSize + 1, preEnd, in, rootIndex + 1);
        return root;
    }

    /** Approach 2: optimal, a HashMap from value to inorder index finds every root in O(1). O(n) time, O(n) space. */
    static TreeNode optimal(int[] preorder, int[] inorder) {
        Map<Integer, Integer> inIndex = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) inIndex.put(inorder[i], i);
        return buildMap(preorder, 0, preorder.length - 1, 0, inIndex);
    }

    static TreeNode buildMap(int[] pre, int preStart, int preEnd, int inStart, Map<Integer, Integer> inIndex) {
        if (preStart > preEnd) return null;
        TreeNode root = new TreeNode(pre[preStart]);
        int rootIndex = inIndex.get(root.val);
        int leftSize = rootIndex - inStart;
        root.left = buildMap(pre, preStart + 1, preStart + leftSize, inStart, inIndex);
        root.right = buildMap(pre, preStart + leftSize + 1, preEnd, rootIndex + 1, inIndex);
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

    static void preorderOf(TreeNode node, List<Integer> out) {
        if (node == null) return;
        out.add(node.val);
        preorderOf(node.left, out);
        preorderOf(node.right, out);
    }

    static void inorderOf(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorderOf(node.left, out);
        out.add(node.val);
        inorderOf(node.right, out);
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

    static void verify(int[] preorder, int[] inorder, TreeNode expected, String label) {
        check(same(bruteForce(preorder, inorder), expected), "bruteForce " + label);
        check(same(optimal(preorder, inorder), expected), "optimal " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 9, 20, 15, 7}, new int[]{9, 3, 15, 20, 7}, build(3, 9, 20, null, null, 15, 7), "example 1");
        verify(new int[]{-1}, new int[]{-1}, build(-1), "single node");
        verify(new int[]{}, new int[]{}, build(), "empty tree");                                   // edge case
        verify(new int[]{1, 2, 3, 4}, new int[]{4, 3, 2, 1}, build(1, 2, null, 3, null, 4), "left chain");
        verify(new int[]{1, 2, 3}, new int[]{1, 2, 3}, build(1, null, 2, null, 3), "right chain");
        verify(new int[]{1, 2, 4, 5, 3, 6, 7}, new int[]{4, 2, 5, 1, 6, 3, 7}, build(1, 2, 3, 4, 5, 6, 7), "perfect tree");
        verify(new int[]{1, 2, 4, 5, 8, 9, 3, 6, 7}, new int[]{4, 2, 8, 5, 9, 1, 6, 3, 7},
                build(1, 2, 3, 4, 5, 6, 7, null, null, 8, 9), "uneven tree");
        verify(new int[]{5, 1, 4, 3, 2}, new int[]{1, 5, 2, 3, 4}, build(5, 1, 4, null, null, 3, null, 2), "zig-zag on the right");

        TreeNode big = randomTree(3000, 42);
        List<Integer> pre = new ArrayList<>(), in = new ArrayList<>();
        preorderOf(big, pre);
        inorderOf(big, in);
        verify(pre.stream().mapToInt(Integer::intValue).toArray(), in.stream().mapToInt(Integer::intValue).toArray(),
                big, "random tree of 3000 nodes");
        System.out.println("OK P110_ConstructABTFromPreorderAndInorder");
    }
}
