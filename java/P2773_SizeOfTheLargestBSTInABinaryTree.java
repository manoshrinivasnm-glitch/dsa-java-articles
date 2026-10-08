import java.util.*;

/** TUF 2773 - Size of the largest BST in a Binary Tree. Return the node count of the largest subtree (a node plus all its descendants) that is a strict BST. */
public class P2773_SizeOfTheLargestBSTInABinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: at each node, validate the whole subtree; if it is a BST it beats anything inside it. O(n * h) time, O(h) stack. */
    static int bruteForce(TreeNode root) {
        if (root == null) return 0;
        if (isBST(root, Long.MIN_VALUE, Long.MAX_VALUE)) return size(root);   // nothing strictly inside can be larger
        return Math.max(bruteForce(root.left), bruteForce(root.right));
    }

    static boolean isBST(TreeNode node, long lo, long hi) {          // every key must lie strictly between lo and hi
        if (node == null) return true;
        if (node.val <= lo || node.val >= hi) return false;
        return isBST(node.left, lo, node.val) && isBST(node.right, node.val, hi);
    }

    static int size(TreeNode node) {
        return node == null ? 0 : 1 + size(node.left) + size(node.right);
    }

    /** What a parent needs to know about a child subtree. min and max are meaningful only when isBST is true. */
    static final class Info {
        final boolean isBST;
        final int size;
        final long min, max;

        Info(boolean isBST, int size, long min, long max) {
            this.isBST = isBST;
            this.size = size;
            this.min = min;
            this.max = max;
        }
    }

    /** Approach 2: optimal. One post-order pass; each node combines its children's summaries in O(1). O(n) time, O(h) stack. */
    static int optimal(TreeNode root) {
        int[] best = {0};
        summarize(root, best);
        return best[0];
    }

    static Info summarize(TreeNode node, int[] best) {
        if (node == null) return new Info(true, 0, Long.MAX_VALUE, Long.MIN_VALUE);   // empty: fits under any parent
        Info l = summarize(node.left, best);
        Info r = summarize(node.right, best);
        if (l.isBST && r.isBST && l.max < node.val && node.val < r.min) {
            int size = l.size + r.size + 1;
            best[0] = Math.max(best[0], size);
            return new Info(true, size, Math.min(l.min, node.val), Math.max(r.max, node.val));
        }
        return new Info(false, 0, 0, 0);                 // no ancestor can be a BST either
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Build a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode fromLevelOrder(Integer... a) {
        if (a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < a.length) {
            TreeNode cur = q.poll();
            if (a[i] != null) { cur.left = new TreeNode(a[i]); q.add(cur.left); }
            i++;
            if (i < a.length && a[i] != null) { cur.right = new TreeNode(a[i]); q.add(cur.right); }
            i++;
        }
        return root;
    }

    static void verify(TreeNode root, int expected, String label) {
        int a = bruteForce(root), b = optimal(root);
        check(a == expected && b == expected, label + ": got " + a + ", " + b + ", expected " + expected);
    }

    static void verify(Integer[] level, int expected) {
        verify(fromLevelOrder(level), expected, Arrays.toString(level));
    }

    /** A random binary tree (not necessarily a BST) with keys from a small range, so duplicates and violations are common. */
    static TreeNode randomTree(Random rnd, int n, int keyRange) {
        if (n == 0) return null;
        TreeNode node = new TreeNode(rnd.nextInt(keyRange));
        int leftSize = rnd.nextInt(n);
        node.left = randomTree(rnd, leftSize, keyRange);
        node.right = randomTree(rnd, n - 1 - leftSize, keyRange);
        return node;
    }

    /** Reference answer: every node, every subtree, checked by collecting its in-order keys. */
    static int reference(TreeNode node) {
        if (node == null) return 0;
        List<Integer> keys = new ArrayList<>();
        collect(node, keys);
        boolean increasing = true;
        for (int i = 1; i < keys.size(); i++) if (keys.get(i - 1) >= keys.get(i)) increasing = false;
        int here = increasing ? keys.size() : 0;
        return Math.max(here, Math.max(reference(node.left), reference(node.right)));
    }

    static void collect(TreeNode node, List<Integer> out) {
        if (node == null) return;
        collect(node.left, out);
        out.add(node.val);
        collect(node.right, out);
    }

    public static void main(String[] args) {
        verify(new Integer[]{10, 5, 15, 1, 8, null, 7}, 3);           // {5, 1, 8}; 15 -> 7 breaks the right side
        verify(new Integer[]{8, 4, 12, 2, 6, 10, 14}, 7);              // the whole tree is a BST
        verify(new Integer[]{10, 5, 15, 1, 12}, 3);                    // 12 is fine under 5 locally but not under 10
        verify(new Integer[]{1, 4, 4, 6, 8}, 1);                       // no two nodes form a BST: every leaf counts as 1
        verify(new Integer[]{5, 2, 4, 1, 3}, 3);                       // {2, 1, 3}
        verify(new Integer[]{2, 2, 2}, 1);                             // duplicates are not allowed in a strict BST
        verify(new Integer[]{7}, 1);                                   // single node
        verify(new Integer[]{}, 0);                                    // empty tree
        verify(new Integer[]{Integer.MIN_VALUE, null, Integer.MAX_VALUE}, 2);   // extreme keys must not collide with sentinels
        verify(new Integer[]{Integer.MAX_VALUE, Integer.MAX_VALUE}, 1);
        verify(new Integer[]{50, 30, 60, 5, 20, 45, 70, null, null, null, null, null, null, 65, 80}, 5);   // right subtree {60, 45, 70, 65, 80}

        // cross-check against the slow reference on seeded random trees
        Random rnd = new Random(2773);
        for (int trial = 0; trial < 500; trial++) {
            TreeNode root = randomTree(rnd, rnd.nextInt(30), 1 + rnd.nextInt(40));
            verify(root, reference(root), "random tree #" + trial);
        }
        System.out.println("OK P2773_SizeOfTheLargestBSTInABinaryTree");
    }
}
