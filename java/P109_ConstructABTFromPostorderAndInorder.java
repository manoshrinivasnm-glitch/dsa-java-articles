import java.util.*;

/** TUF 109 - Construct a BT from Postorder and Inorder. Rebuild the unique tree (distinct values) from its two traversals. */
public class P109_ConstructABTFromPostorderAndInorder {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion, root located in inorder by a linear scan. O(n^2) worst-case time, O(h) extra space. */
    static TreeNode bruteForce(int[] inorder, int[] postorder) {
        return buildScan(inorder, 0, inorder.length - 1, postorder, 0, postorder.length - 1);
    }

    static TreeNode buildScan(int[] in, int inLo, int inHi, int[] post, int postLo, int postHi) {
        if (inLo > inHi) return null;
        TreeNode root = new TreeNode(post[postHi]);          // postorder ends with the root
        int k = inLo;
        while (in[k] != root.val) k++;                       // root splits inorder into left | root | right
        int leftSize = k - inLo;
        root.left = buildScan(in, inLo, k - 1, post, postLo, postLo + leftSize - 1);
        root.right = buildScan(in, k + 1, inHi, post, postLo + leftSize, postHi - 1);
        return root;
    }

    /** Approach 2: same recursion, root positions taken from a HashMap. O(n) time, O(n) space. */
    static TreeNode optimal(int[] inorder, int[] postorder) {
        Map<Integer, Integer> pos = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) pos.put(inorder[i], i);
        return buildMap(0, inorder.length - 1, postorder, 0, postorder.length - 1, pos);
    }

    static TreeNode buildMap(int inLo, int inHi, int[] post, int postLo, int postHi, Map<Integer, Integer> pos) {
        if (inLo > inHi) return null;
        TreeNode root = new TreeNode(post[postHi]);
        int k = pos.get(root.val);                           // O(1) instead of a scan
        int leftSize = k - inLo;
        root.left = buildMap(inLo, k - 1, post, postLo, postLo + leftSize - 1, pos);
        root.right = buildMap(k + 1, inHi, post, postLo + leftSize, postHi - 1, pos);
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

    static int[] toArray(List<Integer> values) {
        int[] a = new int[values.size()];
        for (int i = 0; i < a.length; i++) a[i] = values.get(i);
        return a;
    }

    /** Random-shaped tree with distinct values: values 0..n-1 in a shuffled order, inserted under random free slots. */
    static TreeNode randomTree(int n, long seed) {
        if (n == 0) return null;
        Random rnd = new Random(seed);
        List<Integer> values = new ArrayList<>();
        for (int v = 0; v < n; v++) values.add(v * 3 - n);              // distinct, includes negatives
        Collections.shuffle(values, rnd);
        TreeNode root = new TreeNode(values.get(0));
        List<TreeNode> nodes = new ArrayList<>(List.of(root));
        for (int i = 1; i < n; i++) {
            TreeNode child = new TreeNode(values.get(i));
            while (true) {
                TreeNode parent = nodes.get(rnd.nextInt(nodes.size()));
                if (rnd.nextBoolean()) {
                    if (parent.left == null) { parent.left = child; break; }
                } else if (parent.right == null) { parent.right = child; break; }
            }
            nodes.add(child);
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

    static void verifyTree(TreeNode tree, String label) {
        List<Integer> in = new ArrayList<>(), post = new ArrayList<>();
        inorderOf(tree, in);
        postorderOf(tree, post);
        verify(toArray(in), toArray(post), tree, label);
    }

    public static void main(String[] args) {
        verify(new int[]{9, 3, 15, 20, 7}, new int[]{9, 15, 7, 20, 3}, build(3, 9, 20, null, null, 15, 7), "example 1");
        verify(new int[]{-1}, new int[]{-1}, build(-1), "single node");
        verify(new int[]{}, new int[]{}, null, "empty tree");                                         // edge case
        verify(new int[]{4, 2, 5, 1, 6, 3, 7}, new int[]{4, 5, 2, 6, 7, 3, 1}, build(1, 2, 3, 4, 5, 6, 7), "perfect tree");
        verify(new int[]{1, 2, 3, 4}, new int[]{1, 2, 3, 4}, build(4, 3, null, 2, null, 1), "left chain");
        verify(new int[]{1, 2, 3}, new int[]{3, 2, 1}, build(1, null, 2, null, 3), "right chain");
        verify(new int[]{2, 1}, new int[]{2, 1}, build(1, 2), "root with only a left child");
        verify(new int[]{1, 2}, new int[]{2, 1}, build(1, null, 2), "root with only a right child");
        for (int seed = 1; seed <= 20; seed++) verifyTree(randomTree(60, seed), "random tree seed " + seed);
        verifyTree(randomTree(3_000, 99), "random tree of 3000 nodes");
        System.out.println("OK P109_ConstructABTFromPostorderAndInorder");
    }
}
