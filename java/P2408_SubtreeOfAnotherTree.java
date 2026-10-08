import java.util.*;

/** TUF 2408 - Subtree of Another Tree. Is there a node of root whose whole subtree is identical to subRoot? */
public class P2408_SubtreeOfAnotherTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: try every node of root as the top of a match and compare from there. O(n * m) time, O(h) stack. */
    static boolean bruteForce(TreeNode root, TreeNode subRoot) {
        if (subRoot == null) return true;           // the empty tree is a subtree of every tree
        if (root == null) return false;
        return isSame(root, subRoot) || bruteForce(root.left, subRoot) || bruteForce(root.right, subRoot);
    }

    static boolean isSame(TreeNode p, TreeNode q) {
        if (p == null || q == null) return p == q;
        return p.val == q.val && isSame(p.left, q.left) && isSame(p.right, q.right);
    }

    /** Approach 2: preorder with a marker for every missing child, then find the pattern with KMP. O(n + m) time and space. */
    static final long NULL = Long.MAX_VALUE;        // no int value can equal this marker

    static boolean optimal(TreeNode root, TreeNode subRoot) {
        List<Long> text = new ArrayList<>(), pattern = new ArrayList<>();
        serialize(root, text);
        serialize(subRoot, pattern);
        return kmpContains(toArray(text), toArray(pattern));
    }

    static void serialize(TreeNode node, List<Long> out) {
        if (node == null) {
            out.add(NULL);
            return;
        }
        out.add((long) node.val);
        serialize(node.left, out);
        serialize(node.right, out);
    }

    static long[] toArray(List<Long> list) {
        long[] a = new long[list.size()];
        for (int i = 0; i < a.length; i++) a[i] = list.get(i);
        return a;
    }

    static boolean kmpContains(long[] text, long[] pattern) {
        int m = pattern.length;
        int[] lps = new int[m];        // lps[i]: longest proper prefix of pattern[0..i] that is also a suffix of it
        int len = 0;
        for (int i = 1; i < m; i++) {
            while (len > 0 && pattern[i] != pattern[len]) len = lps[len - 1];
            if (pattern[i] == pattern[len]) len++;
            lps[i] = len;
        }
        int j = 0;                     // how many pattern tokens the current window matches
        for (long token : text) {
            while (j > 0 && token != pattern[j]) j = lps[j - 1];
            if (token == pattern[j]) j++;
            if (j == m) return true;
        }
        return false;
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

    static TreeNode randomTree(Random rnd, int size) {
        if (size == 0) return null;
        TreeNode node = new TreeNode(rnd.nextInt(2));
        int leftSize = rnd.nextInt(size);
        node.left = randomTree(rnd, leftSize);
        node.right = randomTree(rnd, size - 1 - leftSize);
        return node;
    }

    static TreeNode copy(TreeNode node) {
        if (node == null) return null;
        TreeNode c = new TreeNode(node.val);
        c.left = copy(node.left);
        c.right = copy(node.right);
        return c;
    }

    static void collect(TreeNode node, List<TreeNode> out) {
        if (node == null) return;
        out.add(node);
        collect(node.left, out);
        collect(node.right, out);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] r, Integer[] s, boolean expected) {
        TreeNode root = build(r), sub = build(s);
        String msg = Arrays.toString(r) + " contains " + Arrays.toString(s);
        check(bruteForce(root, sub) == expected, "bruteForce " + msg);
        check(optimal(root, sub) == expected, "optimal " + msg);
    }

    public static void main(String[] args) {
        verify(new Integer[]{3, 4, 5, 1, 2}, new Integer[]{4, 1, 2}, true);
        verify(new Integer[]{3, 4, 5, 1, 2, null, null, null, null, 0}, new Integer[]{4, 1, 2}, false); // extra node below
        verify(new Integer[]{12}, new Integer[]{2}, false);             // "12" contains "2" only as text
        verify(new Integer[]{1, 1}, new Integer[]{1}, true);            // the leaf matches, the root does not
        verify(new Integer[]{1, 2, 3}, new Integer[]{1, 2, 3}, true);   // the whole tree counts
        verify(new Integer[]{1, 2, 3}, new Integer[]{1, 2}, false);     // a top part is not a subtree
        verify(new Integer[]{2, 2, null, 2}, new Integer[]{2, 2}, true);
        verify(new Integer[]{2, 2, null, 2}, new Integer[]{2, null, 2}, false); // same values, mirrored shape
        verify(new Integer[]{1, null, 2, null, 3}, new Integer[]{2, null, 3}, true);
        verify(new Integer[]{-5, 7, -5}, new Integer[]{-5}, true);      // negative values
        verify(new Integer[]{}, new Integer[]{1}, false);               // edge: empty root
        verify(new Integer[]{1}, new Integer[]{}, true);                // edge: empty subRoot
        verify(new Integer[]{}, new Integer[]{}, true);

        Random rnd = new Random(2408);
        for (int k = 0; k < 3000; k++) {
            TreeNode root = randomTree(rnd, 1 + rnd.nextInt(12));
            List<TreeNode> nodes = new ArrayList<>();
            collect(root, nodes);
            TreeNode sub = copy(nodes.get(rnd.nextInt(nodes.size())));
            check(bruteForce(root, sub) && optimal(root, sub), "a copy of a real subtree must be found");
            TreeNode other = randomTree(rnd, 1 + rnd.nextInt(4));
            check(bruteForce(root, other) == optimal(root, other), "random pair disagrees");
        }
        System.out.println("OK P2408_SubtreeOfAnotherTree");
    }
}
