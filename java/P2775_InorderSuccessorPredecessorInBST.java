import java.util.*;

/** TUF 2775 - Inorder Successor/Predecessor in BST. Return {predecessor, successor} of key: the largest key below it and the smallest key above it, or -1 when missing. */
public class P2775_InorderSuccessorPredecessorInBST {

    static final int NONE = -1;

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. The in-order sequence is sorted, so collect it and scan for the neighbours of key. O(n) time, O(n) space. */
    static int[] bruteForce(TreeNode root, int key) {
        List<Integer> sorted = new ArrayList<>();
        collectInorder(root, sorted);
        int pred = NONE, succ = NONE;
        for (int v : sorted) {
            if (v < key) pred = v;                       // keeps overwriting: the last one below key wins
            else if (v > key) { succ = v; break; }       // the first one above key is the answer
        }
        return new int[]{pred, succ};
    }

    static void collectInorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        collectInorder(node.left, out);
        out.add(node.val);
        collectInorder(node.right, out);
    }

    /** Approach 2: optimal. Use the BST order to walk one root-to-leaf path for each answer. O(h) time, O(1) space. */
    static int[] optimal(TreeNode root, int key) {
        return new int[]{predecessor(root, key), successor(root, key)};
    }

    static int successor(TreeNode root, int key) {
        int succ = NONE;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.val > key) {
                succ = cur.val;          // a candidate; anything smaller that still beats key is on the left
                cur = cur.left;
            } else {
                cur = cur.right;         // cur and its whole left subtree are <= key
            }
        }
        return succ;
    }

    static int predecessor(TreeNode root, int key) {
        int pred = NONE;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.val < key) {
                pred = cur.val;          // a candidate; anything larger that is still below key is on the right
                cur = cur.right;
            } else {
                cur = cur.left;          // cur and its whole right subtree are >= key
            }
        }
        return pred;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, where null means "no child here". */
    static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < vals.length) {
            TreeNode cur = queue.poll();
            if (i < vals.length && vals[i] != null) { cur.left = new TreeNode(vals[i]); queue.add(cur.left); }
            i++;
            if (i < vals.length && vals[i] != null) { cur.right = new TreeNode(vals[i]); queue.add(cur.right); }
            i++;
        }
        return root;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode root, int key, int expectedPred, int expectedSucc) {
        int[] expected = {expectedPred, expectedSucc};
        check(Arrays.equals(bruteForce(root, key), expected), "bruteForce key=" + key);
        check(Arrays.equals(optimal(root, key), expected), "optimal key=" + key);
    }

    public static void main(String[] args) {
        TreeNode t = build(5, 2, 10, 1, 4, 7, 12);
        verify(t, 10, 7, 12);
        verify(t, 5, 4, 7);           // root: predecessor is in the left subtree, successor in the right
        verify(t, 4, 2, 5);           // successor is an ancestor, not a descendant
        verify(t, 1, NONE, 2);        // smallest key has no predecessor
        verify(t, 12, 10, NONE);      // largest key has no successor
        verify(t, 6, 5, 7);           // key not present in the tree
        verify(t, 0, NONE, 1);
        verify(t, 100, 12, NONE);
        TreeNode g = build(20, 8, 22, 4, 12, null, null, null, null, 10, 14);
        verify(g, 10, 8, 12);
        verify(g, 14, 12, 20);        // successor is two levels up
        verify(build(8), 8, NONE, NONE);
        verify(build(), 3, NONE, NONE);   // empty tree
        System.out.println("OK P2775_InorderSuccessorPredecessorInBST");
    }
}
