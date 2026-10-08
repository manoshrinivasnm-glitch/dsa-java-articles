import java.util.*;

/** TUF 2779 - Floor in a BST. Return the largest key <= x, or -1 if every key is larger (keys are non-negative). */
public class P2779_FloorInABST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: the in-order sequence is sorted, so the floor is the last key in it that is <= x. O(n) time, O(n) space. */
    static int bruteForce(TreeNode root, int x) {
        List<Integer> sorted = new ArrayList<>();
        inorder(root, sorted);
        int floor = -1;
        for (int key : sorted) {
            if (key > x) break;                  // sorted: every later key is too big as well
            floor = key;
        }
        return floor;
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
        if (root.val > x) return recursive(root.left, x);   // root and its whole right subtree are too big
        int closer = recursive(root.right, x);              // root fits; anything closer must be on its right
        return closer != -1 ? closer : root.val;
    }

    /** Approach 3: iterative descent that remembers the last key where we went right. O(h) time, O(1) space. */
    static int optimal(TreeNode root, int x) {
        int floor = -1;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.val == x) return x;          // cannot do better than an exact match
            if (cur.val < x) {
                floor = cur.val;                 // a candidate, and larger than every earlier candidate
                cur = cur.right;                 // look for a bigger key that still fits
            } else {
                cur = cur.left;                  // too big: the floor can only be on the left
            }
        }
        return floor;
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
                "floor of " + x + " in " + label + ": got " + a + ", " + b + ", " + c + ", expected " + expected);
    }

    static void verify(Integer[] level, int x, int expected) {
        verify(fromLevelOrder(level), x, expected, Arrays.toString(level));
    }

    public static void main(String[] args) {
        Integer[] t = {10, 5, 15, 2, 8, 12, 20};
        verify(t, 13, 12);
        verify(t, 9, 8);
        verify(t, 10, 10);                               // exact match at the root
        verify(t, 11, 10);                               // the answer is the root even though we keep descending
        verify(t, 1, -1);                                // smaller than every key
        verify(t, 50, 20);                               // larger than every key: the maximum
        verify(t, 2, 2);                                 // exact match on the leftmost leaf
        verify(new Integer[]{}, 7, -1);                  // empty tree
        verify(new Integer[]{0}, 0, 0);                  // 0 is a genuine floor, not "missing"
        verify(new Integer[]{6}, 5, -1);
        verify(new Integer[]{1, null, 2, null, 3, null, 4}, 100, 4);   // right-skewed chain
        verify(new Integer[]{4, 3, null, 2, null, 1}, 0, -1);          // left-skewed chain

        // cross-check against a sorted array on seeded random BSTs
        Random rnd = new Random(2779);
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
                Integer f = keys.floor(x);
                verify(root, x, f == null ? -1 : f, "random tree " + keys);
            }
        }
        System.out.println("OK P2779_FloorInABST");
    }
}
