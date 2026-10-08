import java.util.*;

/** TUF 2391 - Construct BST from given keys. Build a height-balanced BST from keys given in strictly increasing order. */
public class P2391_ConstructBSTFromGivenKeys {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: middle key becomes the root; recurse on copied halves. O(n log n) time, O(n) extra space. */
    static TreeNode bruteForce(int[] keys) {
        if (keys.length == 0) return null;
        int mid = (keys.length - 1) / 2;                                  // left-middle for even lengths
        TreeNode root = new TreeNode(keys[mid]);
        root.left = bruteForce(Arrays.copyOfRange(keys, 0, mid));
        root.right = bruteForce(Arrays.copyOfRange(keys, mid + 1, keys.length));
        return root;
    }

    /** Approach 2: same idea on index bounds, no copying. O(n) time, O(log n) stack. */
    static TreeNode optimal(int[] keys) {
        return build(keys, 0, keys.length - 1);
    }

    static TreeNode build(int[] keys, int lo, int hi) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;                                     // same left-middle choice as above
        TreeNode root = new TreeNode(keys[mid]);
        root.left = build(keys, lo, mid - 1);                            // keys smaller than keys[mid]
        root.right = build(keys, mid + 1, hi);                           // keys larger than keys[mid]
        return root;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order; null means "no node here". */
    static TreeNode levelOrder(Integer... level) {
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

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    /** Height of the tree, or -1 if some node's subtrees differ in height by more than one. */
    static int balancedHeight(TreeNode node) {
        if (node == null) return 0;
        int l = balancedHeight(node.left), r = balancedHeight(node.right);
        if (l < 0 || r < 0 || Math.abs(l - r) > 1) return -1;
        return Math.max(l, r) + 1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyProperties(TreeNode tree, int[] keys, String label) {
        List<Integer> in = new ArrayList<>();
        inorder(tree, in);
        check(in.size() == keys.length, "size " + label);
        for (int i = 0; i < keys.length; i++) check(in.get(i) == keys[i], "inorder must equal the sorted keys " + label);
        int h = balancedHeight(tree);
        int minHeight = 32 - Integer.numberOfLeadingZeros(keys.length);   // floor(log2 n) + 1 levels
        check(h == minHeight, "height " + h + " should be minimal " + minHeight + " " + label);
    }

    static void verify(int[] keys, TreeNode expected, String label) {
        TreeNode a = bruteForce(keys), b = optimal(keys);
        check(same(a, expected), "bruteForce " + label);
        check(same(b, expected), "optimal " + label);
        verifyProperties(a, keys, label);
        verifyProperties(b, keys, label);
    }

    public static void main(String[] args) {
        verify(new int[]{-10, -3, 0, 5, 9}, levelOrder(0, -10, 5, null, -3, null, 9), "example 1");
        verify(new int[]{1, 3}, levelOrder(1, null, 3), "example 2");
        verify(new int[]{}, null, "no keys");                                                   // edge case
        verify(new int[]{7}, levelOrder(7), "single key");
        verify(new int[]{1, 2, 3, 4, 5, 6, 7}, levelOrder(4, 2, 6, 1, 3, 5, 7), "perfect tree");
        verify(new int[]{10, 20, 30, 40}, levelOrder(20, 10, 30, null, null, null, 40), "even count");
        verify(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}, levelOrder(0, Integer.MIN_VALUE, Integer.MAX_VALUE), "extreme values");

        int n = 100_000;
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) keys[i] = 2 * i - n;
        TreeNode a = bruteForce(keys), b = optimal(keys);
        check(same(a, b), "both approaches build the same tree for 100000 keys");
        verifyProperties(b, keys, "100000 keys");
        System.out.println("OK P2391_ConstructBSTFromGivenKeys");
    }
}
