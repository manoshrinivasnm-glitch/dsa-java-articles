import java.util.*;

/** TUF 2777 - Ceil in a BST. Return the smallest key >= x, or -1 if every key is smaller (keys are non-negative). */
public class P2777_CeilInABST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: the in-order sequence is sorted, so the ceil is the first key in it that is >= x. O(n) time, O(n) space. */
    static int bruteForce(TreeNode root, int x) {
        List<Integer> sorted = new ArrayList<>();
        inorder(root, sorted);
        for (int key : sorted) {
            if (key >= x) return key;            // first key that is large enough
        }
        return -1;
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    /** Approach 2: recursive descent into the single subtree that can still hold the answer. O(h) time, O(h) stack. */
    static int recursive(TreeNode root, int x) {
        if (root == null) return -1;
        if (root.val == x) return x;
        if (root.val < x) return recursive(root.right, x);  // root and its whole left subtree are too small
        int closer = recursive(root.left, x);               // root fits; anything closer must be on its left
        return closer != -1 ? closer : root.val;
    }

    /** Approach 3: iterative descent that remembers the last key where we went left. O(h) time, O(1) space. */
    static int optimal(TreeNode root, int x) {
        int ceil = -1;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.val == x) return x;          // cannot do better than an exact match
            if (cur.val > x) {
                ceil = cur.val;                  // a candidate, and smaller than every earlier candidate
                cur = cur.left;                  // look for a smaller key that is still large enough
            } else {
                cur = cur.right;                 // too small: the ceil can only be on the right
            }
        }
        return ceil;
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

    static TreeNode insert(TreeNode root, int key) {
        if (root == null) return new TreeNode(key);
        if (key < root.val) root.left = insert(root.left, key);
        else if (key > root.val) root.right = insert(root.right, key);
        return root;
    }

    static void verify(TreeNode root, int x, int expected, String label) {
        int a = bruteForce(root, x), b = recursive(root, x), c = optimal(root, x);
        check(a == expected && b == expected && c == expected,
                "ceil of " + x + " in " + label + ": got " + a + ", " + b + ", " + c + ", expected " + expected);
    }

    static void verify(Integer[] level, int x, int expected) {
        verify(fromLevelOrder(level), x, expected, Arrays.toString(level));
    }

    public static void main(String[] args) {
        Integer[] t = {10, 5, 15, 2, 8, 12, 20};
        verify(t, 13, 15);
        verify(t, 6, 8);
        verify(t, 10, 10);                               // exact match at the root
        verify(t, 9, 10);                                // the answer is the root even though we keep descending
        verify(t, 21, -1);                               // larger than every key
        verify(t, 0, 2);                                 // smaller than every key: the minimum
        verify(t, 20, 20);                               // exact match on the rightmost leaf
        verify(new Integer[]{}, 7, -1);                  // empty tree
        verify(new Integer[]{0}, 0, 0);                  // 0 is a genuine ceil, not "missing"
        verify(new Integer[]{6}, 7, -1);
        verify(new Integer[]{1, null, 2, null, 3, null, 4}, 5, -1);    // right-skewed chain
        verify(new Integer[]{4, 3, null, 2, null, 1}, 0, 1);           // left-skewed chain

        // cross-check against a sorted set on seeded random BSTs
        Random rnd = new Random(2777);
        for (int trial = 0; trial < 200; trial++) {
            TreeNode root = null;
            TreeSet<Integer> keys = new TreeSet<>();
            int n = rnd.nextInt(30);
            for (int i = 0; i < n; i++) {
                int key = rnd.nextInt(100);
                keys.add(key);
                root = insert(root, key);
            }
            for (int x = 0; x <= 101; x += 3) {
                Integer c = keys.ceiling(x);
                verify(root, x, c == null ? -1 : c, "random tree " + keys);
            }
        }
        System.out.println("OK P2777_CeilInABST");
    }
}
