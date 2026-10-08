import java.util.*;

/** TUF 486 - Flatten Binary Tree to Linked List. In place, in preorder, using right pointers as "next" and null left pointers. */
public class P486_FlattenBinaryTreeToLinkedList {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force, record the preorder sequence of nodes, then relink them. O(n) time, O(n) space. */
    static void bruteForce(TreeNode root) {
        List<TreeNode> order = new ArrayList<>();
        collectPreorder(root, order);
        for (int i = 0; i < order.size(); i++) {
            TreeNode node = order.get(i);
            node.left = null;
            node.right = i + 1 < order.size() ? order.get(i + 1) : null;
        }
    }

    static void collectPreorder(TreeNode node, List<TreeNode> order) {
        if (node == null) return;
        order.add(node);
        collectPreorder(node.left, order);
        collectPreorder(node.right, order);
    }

    /** Approach 2: better, build the list from its tail in reverse preorder (right, left, root). O(n) time, O(h) recursion space. */
    static void better(TreeNode root) {
        flattenOnto(root, null);
    }

    // flattens the subtree at node, appends tail after it, and returns the head of the result
    static TreeNode flattenOnto(TreeNode node, TreeNode tail) {
        if (node == null) return tail;
        tail = flattenOnto(node.right, tail);                   // right subtree comes last in preorder, so do it first
        tail = flattenOnto(node.left, tail);
        node.right = tail;
        node.left = null;
        return node;
    }

    /** Approach 3: optimal, Morris-style rewiring. O(n) time, O(1) extra space. */
    static void optimal(TreeNode root) {
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left != null) {
                TreeNode pred = cur.left;
                while (pred.right != null) pred = pred.right;   // last node of the left subtree in preorder
                pred.right = cur.right;                         // the right subtree follows it
                cur.right = cur.left;                           // the left subtree moves to the right
                cur.left = null;
            }
            cur = cur.right;
        }
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

    /** Balanced tree over lo..hi; nodes are created in preorder, so their values are recorded as the expected answer. */
    static TreeNode balanced(int lo, int hi, List<Integer> creationOrder) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;
        TreeNode node = new TreeNode(mid);
        creationOrder.add(mid);
        node.left = balanced(lo, mid - 1, creationOrder);
        node.right = balanced(mid + 1, hi, creationOrder);
        return node;
    }

    /** True when every left pointer is null and the right-pointer chain spells out exactly the expected values. */
    static boolean isFlatList(TreeNode root, int[] expected) {
        int i = 0;
        for (TreeNode t = root; t != null; t = t.right) {
            if (t.left != null || i >= expected.length || t.val != expected[i]) return false;
            i++;
        }
        return i == expected.length;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] expected, String label, Integer... level) {
        TreeNode a = build(level), b = build(level), c = build(level);
        bruteForce(a);
        better(b);
        optimal(c);
        check(isFlatList(a, expected), "bruteForce " + label);
        check(isFlatList(b, expected), "better " + label);
        check(isFlatList(c, expected), "optimal " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5, 6}, "example", 1, 2, 5, 3, 4, null, 6);
        verify(new int[]{1, 2, 4, 5, 8, 9, 3, 6, 7}, "two levels of nesting", 1, 2, 3, 4, 5, 6, 7, null, null, 8, 9);
        verify(new int[]{0}, "single node", 0);
        verify(new int[]{}, "empty tree");                                       // edge case
        verify(new int[]{1, 2, 3, 4}, "left chain", 1, 2, null, 3, null, 4);
        verify(new int[]{1, 2, 3}, "already flat", 1, null, 2, null, 3);
        verify(new int[]{1, 2, 3, 4, 5}, "zig-zag", 1, 2, null, null, 3, 4, null, null, 5);

        List<Integer> order = new ArrayList<>();
        balanced(1, 50_000, order);
        int[] expected = order.stream().mapToInt(Integer::intValue).toArray();
        TreeNode a = balanced(1, 50_000, new ArrayList<>()), b = balanced(1, 50_000, new ArrayList<>()), c = balanced(1, 50_000, new ArrayList<>());
        bruteForce(a);
        better(b);
        optimal(c);
        check(isFlatList(a, expected), "bruteForce balanced tree of 50000 nodes");
        check(isFlatList(b, expected), "better balanced tree of 50000 nodes");
        check(isFlatList(c, expected), "optimal balanced tree of 50000 nodes");
        System.out.println("OK P486_FlattenBinaryTreeToLinkedList");
    }
}
