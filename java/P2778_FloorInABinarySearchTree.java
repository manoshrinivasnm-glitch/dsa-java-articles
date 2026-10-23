import java.util.*;

/** TUF 2778 - Floor in a Binary Search Tree. Return the largest key <= x, or -1 if every key is larger (keys are non-negative). */
public class P2778_FloorInABinarySearchTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: look at every node and keep the best key <= x. O(n) time, O(h) stack. */
    static int bruteForce(TreeNode root, int x) {
        if (root == null) return -1;                     // empty subtree: no floor
        int best = root.val <= x ? root.val : -1;        // this node counts only if it is <= x
        best = Math.max(best, bruteForce(root.left, x));   // best from the left subtree
        best = Math.max(best, bruteForce(root.right, x));   // and from the right subtree
        return best;
    }

    /** Approach 2: recursive descent that only enters the one subtree that can hold a better answer. O(h) time, O(h) stack. */
    static int recursive(TreeNode root, int x) {
        if (root == null) return -1;                          // fell off the tree: no floor here
        if (root.val == x) return x;                          // an exact match is the floor
        if (root.val > x) return recursive(root.left, x);     // root and its right subtree are all too big
        int closer = recursive(root.right, x);               // root fits; something closer may be on its right
        return closer != -1 ? closer : root.val;              // fall back to root if the right has none
    }

    /** Approach 3: iterative descent that remembers the last node where we turned right. O(h) time, O(1) space. */
    static int optimal(TreeNode root, int x) {
        int floor = -1;                                  // best key <= x so far; -1 = none
        TreeNode cur = root;
        while (cur != null) {                            // one root-to-leaf walk
            if (cur.val == x) return x;                  // an exact match is the floor
            if (cur.val < x) {
                floor = cur.val;                         // candidate; every later candidate is larger
                cur = cur.right;
            } else {
                cur = cur.left;                          // too big: look left for something smaller
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

    static void verify(Integer[] level, int x, int expected) {
        TreeNode root = fromLevelOrder(level);
        int a = bruteForce(root, x), b = recursive(root, x), c = optimal(root, x);
        check(a == expected && b == expected && c == expected,
                "floor of " + x + " in " + Arrays.toString(level) + ": got " + a + ", " + b + ", " + c + ", expected " + expected);
    }

    public static void main(String[] args) {
        Integer[] t = {8, 4, 12, 2, 6, 10, 14};
        verify(t, 11, 10);
        verify(t, 1, -1);                                // smaller than every key
        verify(t, 14, 14);                               // exact match on a leaf
        verify(t, 100, 14);                              // larger than every key: the maximum
        verify(t, 7, 6);
        verify(t, 9, 8);                                 // the answer is the root, found before descending further
        verify(t, 2, 2);
        verify(new Integer[]{10, 5, 15, 2, 6}, 7, 6);
        verify(new Integer[]{10, 5, 15, 2, 6}, 13, 10);
        verify(new Integer[]{}, 5, -1);                  // empty tree
        verify(new Integer[]{5}, 4, -1);
        verify(new Integer[]{5}, 5, 5);
        verify(new Integer[]{0}, 0, 0);                  // 0 is a real floor, not "none"
        verify(new Integer[]{5, 4, null, 3, null, 2}, 1, -1); // left chain, nothing small enough
        verify(new Integer[]{5, 4, null, 3, null, 2}, 6, 5);
        System.out.println("OK P2778_FloorInABinarySearchTree");
    }
}
