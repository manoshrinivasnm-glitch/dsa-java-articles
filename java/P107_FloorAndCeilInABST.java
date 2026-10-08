import java.util.*;

/** TUF 107 - Floor and Ceil in a BST. Return {floor, ceil} of key: the largest key <= key and the smallest key >= key, -1 when absent. */
public class P107_FloorAndCeilInABST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: inorder gives the keys sorted; scan them once. O(n) time, O(n) space. Keys are non-negative, so -1 means "none". */
    static int[] bruteForce(TreeNode root, int key) {
        List<Integer> sorted = new ArrayList<>();
        inorder(root, sorted);
        int floor = -1, ceil = -1;
        for (int v : sorted) {
            if (v <= key) floor = v;                     // the last value <= key wins
            if (v >= key && ceil == -1) ceil = v;        // the first value >= key wins
        }
        return new int[]{floor, ceil};
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    /** Approach 2: two root-to-leaf walks that each remember the best candidate seen. O(h) time, O(1) space. */
    static int[] optimal(TreeNode root, int key) {
        return new int[]{findFloor(root, key), findCeil(root, key)};
    }

    static int findFloor(TreeNode root, int key) {
        int floor = -1;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.val == key) return key;
            if (cur.val < key) {
                floor = cur.val;                         // a candidate; anything closer is to its right
                cur = cur.right;
            } else {
                cur = cur.left;                          // too big, and so is its whole right subtree
            }
        }
        return floor;
    }

    static int findCeil(TreeNode root, int key) {
        int ceil = -1;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.val == key) return key;
            if (cur.val > key) {
                ceil = cur.val;                          // a candidate; anything closer is to its left
                cur = cur.left;
            } else {
                cur = cur.right;                         // too small, and so is its whole left subtree
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
        Queue<TreeNode> q = new LinkedList<>();
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

    static void verify(Integer[] level, int key, int floor, int ceil) {
        TreeNode root = fromLevelOrder(level);
        int[] expected = {floor, ceil};
        int[] a = bruteForce(root, key), b = optimal(root, key);
        check(Arrays.equals(a, expected), "bruteForce key " + key + " in " + Arrays.toString(level) + " gave " + Arrays.toString(a));
        check(Arrays.equals(b, expected), "optimal key " + key + " in " + Arrays.toString(level) + " gave " + Arrays.toString(b));
    }

    public static void main(String[] args) {
        Integer[] t = {8, 4, 12, 2, 6, 10, 14};
        verify(t, 11, 10, 12);
        verify(t, 8, 8, 8);                              // key present: floor = ceil = key
        verify(t, 1, -1, 2);                             // below every key: no floor
        verify(t, 15, 14, -1);                           // above every key: no ceil
        verify(t, 5, 4, 6);
        verify(t, 9, 8, 10);                             // the answers are an ancestor and a descendant
        verify(t, 7, 6, 8);
        verify(t, 13, 12, 14);
        verify(new Integer[]{10, 5, 13, 3, 6, 11, 14, 2, 4, null, 9}, 8, 6, 9);
        verify(new Integer[]{}, 3, -1, -1);              // empty tree
        verify(new Integer[]{5}, 5, 5, 5);
        verify(new Integer[]{5}, 3, -1, 5);
        verify(new Integer[]{5}, 7, 5, -1);
        verify(new Integer[]{0}, 0, 0, 0);               // key 0 is a real answer, not "none"
        verify(new Integer[]{1, null, 2, null, 3, null, 4}, 10, 4, -1); // right chain
        System.out.println("OK P107_FloorAndCeilInABST");
    }
}
