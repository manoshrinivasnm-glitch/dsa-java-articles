import java.util.*;

/** TUF 1153 - Introduction to BST. The BST property, why inorder is sorted, O(h) search, and how insertion order decides the height. */
public class P1153_IntroductionToBST {

    /** A binary tree node. In a BST every key in left is smaller than val and every key in right is larger. */
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /** Insert one key by walking down from the root and attaching it where the walk falls off the tree. Duplicates are ignored. O(h) time. */
    static TreeNode insert(TreeNode root, int key) {
        if (root == null) return new TreeNode(key);       // fell off the tree: this is the key's spot
        if (key < root.val) root.left = insert(root.left, key);
        else if (key > root.val) root.right = insert(root.right, key);
        return root;                                      // key == root.val: already present, nothing to do
    }

    /** Build a BST by inserting the keys in the given order. The order decides the shape. */
    static TreeNode buildByInsertion(int[] keys) {
        TreeNode root = null;
        for (int k : keys) root = insert(root, k);
        return root;
    }

    /** Build a height-balanced BST from a sorted array: the middle element becomes the root, recursively. O(n) time. */
    static TreeNode buildBalanced(int[] sorted, int lo, int hi) {
        if (lo > hi) return null;
        int mid = (lo + hi) >>> 1;
        TreeNode node = new TreeNode(sorted[mid]);
        node.left = buildBalanced(sorted, lo, mid - 1);
        node.right = buildBalanced(sorted, mid + 1, hi);
        return node;
    }

    /** Inorder traversal (left, node, right). For a BST this lists the keys in increasing order. O(n) time. */
    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    /** WRONG check, shown on purpose: it only compares each node with its two children. */
    static boolean isBSTLocalOnly(TreeNode node) {
        if (node == null) return true;
        if (node.left != null && node.left.val >= node.val) return false;
        if (node.right != null && node.right.val <= node.val) return false;
        return isBSTLocalOnly(node.left) && isBSTLocalOnly(node.right);
    }

    /** Correct check: every key must lie strictly inside the (low, high) window inherited from its ancestors. O(n) time. */
    static boolean isBST(TreeNode root) {
        return isBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    static boolean isBST(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;
        return isBST(node.left, low, node.val) && isBST(node.right, node.val, high);
    }

    /** Search by discarding one subtree at every step. O(h) time, O(1) space. */
    static boolean contains(TreeNode root, int key) {
        TreeNode cur = root;
        while (cur != null && cur.val != key) {
            cur = key < cur.val ? cur.left : cur.right;   // the key can only be on one side
        }
        return cur != null;
    }

    /** Height counted in nodes: 0 for an empty tree, 1 for a single node. */
    static int height(TreeNode node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<Integer> inorderList(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    public static void main(String[] args) {
        // 1. the classic example, built by inserting keys one at a time
        TreeNode t = buildByInsertion(new int[]{8, 3, 10, 1, 6, 14, 4, 7, 13});
        check(inorderList(t).equals(List.of(1, 3, 4, 6, 7, 8, 10, 13, 14)), "inorder of a BST is sorted");
        check(isBST(t) && isBSTLocalOnly(t), "the built tree is a BST");
        check(t.val == 8 && t.left.val == 3 && t.right.val == 10 && t.left.right.left.val == 4, "shape of the insertion-built tree");
        check(height(t) == 4, "height of the classic tree");
        check(contains(t, 6) && contains(t, 13) && !contains(t, 5) && !contains(t, 15), "contains on the classic tree");

        // 2. the children-only check is fooled by a violation two levels down
        TreeNode bad = new TreeNode(5, new TreeNode(3, new TreeNode(1), new TreeNode(6)), new TreeNode(8));
        check(isBSTLocalOnly(bad), "the local check accepts the bad tree");
        check(!isBST(bad), "the window check rejects the bad tree");
        check(inorderList(bad).equals(List.of(1, 3, 6, 5, 8)), "inorder exposes the violation");

        // 3. empty tree
        check(inorderList(null).isEmpty() && isBST(null), "empty tree is a BST with no keys");
        check(height(null) == 0 && !contains(null, 1), "empty tree height and search");

        // 4. same keys, different insertion orders, different shapes
        TreeNode a = buildByInsertion(new int[]{2, 1, 3});
        TreeNode b = buildByInsertion(new int[]{1, 2, 3});
        check(inorderList(a).equals(inorderList(b)), "same keys give the same inorder");
        check(height(a) == 2 && height(b) == 3, "insertion order changes the height");

        // 5. sorted insertion degenerates into a chain; the balanced builder does not
        int n = 1000;
        int[] sorted = new int[n];
        for (int i = 0; i < n; i++) sorted[i] = i + 1;
        TreeNode chain = buildByInsertion(sorted);
        TreeNode balanced = buildBalanced(sorted, 0, n - 1);
        check(height(chain) == n, "sorted insertion gives a chain of height n");
        check(height(balanced) == 10, "balanced build has height floor(log2 n) + 1");
        check(inorderList(chain).equals(inorderList(balanced)), "both shapes hold the same keys");
        check(isBST(chain) && isBST(balanced), "both shapes are valid BSTs");
        check(contains(chain, 1000) && contains(balanced, 1000) && !contains(balanced, 0), "search in both shapes");

        // 6. duplicates are ignored by this insert
        check(inorderList(buildByInsertion(new int[]{5, 5, 5})).equals(List.of(5)), "duplicates are ignored");

        // 7. extreme values: long bounds keep the window check exact
        TreeNode ext = new TreeNode(0, new TreeNode(Integer.MIN_VALUE), new TreeNode(Integer.MAX_VALUE));
        check(isBST(ext), "extreme values form a valid BST");
        check(!isBST(new TreeNode(0, null, new TreeNode(0))), "an equal key on the right is not strictly greater");

        System.out.println("OK P1153_IntroductionToBST");
    }
}
