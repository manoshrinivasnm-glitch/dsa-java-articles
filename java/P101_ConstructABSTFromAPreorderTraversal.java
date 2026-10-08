import java.util.*;

/** TUF 101 - Construct a BST from a preorder traversal. The keys are distinct; return the root of the unique BST with that preorder. */
public class P101_ConstructABSTFromAPreorderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. Insert the keys one by one in preorder order; every insertion walks down from the root. O(n^2) time worst case, O(1) extra space. */
    static TreeNode bruteForce(int[] preorder) {
        TreeNode root = null;
        for (int v : preorder) root = insert(root, v);
        return root;
    }

    static TreeNode insert(TreeNode root, int v) {
        TreeNode node = new TreeNode(v);
        if (root == null) return node;
        TreeNode cur = root;
        while (true) {
            if (v < cur.val) {
                if (cur.left == null) { cur.left = node; break; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = node; break; }
                cur = cur.right;
            }
        }
        return root;
    }

    /** Approach 2: better. Sorting the preorder gives the inorder; rebuild from preorder + inorder with an index map. O(n log n) time, O(n) space. */
    static TreeNode better(int[] preorder) {
        int[] inorder = preorder.clone();
        Arrays.sort(inorder);
        Map<Integer, Integer> pos = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) pos.put(inorder[i], i);
        int[] next = {0};                                    // index of the next preorder key to place
        return buildRange(preorder, pos, next, 0, inorder.length - 1);
    }

    static TreeNode buildRange(int[] preorder, Map<Integer, Integer> pos, int[] next, int lo, int hi) {
        if (lo > hi) return null;                            // no keys belong to this subtree
        int v = preorder[next[0]++];
        TreeNode node = new TreeNode(v);
        int mid = pos.get(v);                                // keys left of mid go left, right of mid go right
        node.left = buildRange(preorder, pos, next, lo, mid - 1);
        node.right = buildRange(preorder, pos, next, mid + 1, hi);
        return node;
    }

    /** Approach 3: optimal. Consume the preorder left to right; a key joins the current subtree only while it is below the subtree's upper bound. O(n) time, O(h) space. */
    static TreeNode optimal(int[] preorder) {
        int[] next = {0};
        return buildWithBound(preorder, next, Long.MAX_VALUE);
    }

    static TreeNode buildWithBound(int[] preorder, int[] next, long bound) {
        if (next[0] == preorder.length || preorder[next[0]] > bound) return null;
        TreeNode node = new TreeNode(preorder[next[0]++]);
        node.left = buildWithBound(preorder, next, node.val);    // left keys must stay below node.val
        node.right = buildWithBound(preorder, next, bound);      // right keys inherit the parent's bound
        return node;
    }

    // ---------------------------------------------------------------- helpers
    /** LeetCode-style level order with trailing nulls removed, used to compare tree shapes. */
    static List<Integer> levelOrder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();             // LinkedList accepts null entries
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode cur = queue.poll();
            if (cur == null) { out.add(null); continue; }
            out.add(cur.val);
            queue.add(cur.left);
            queue.add(cur.right);
        }
        while (!out.isEmpty() && out.get(out.size() - 1) == null) out.remove(out.size() - 1);
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] preorder, Integer... expectedLevel) {
        List<Integer> expected = Arrays.asList(expectedLevel);
        String in = Arrays.toString(preorder);
        check(levelOrder(bruteForce(preorder)).equals(expected), "bruteForce " + in);
        check(levelOrder(better(preorder)).equals(expected), "better " + in);
        check(levelOrder(optimal(preorder)).equals(expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{8, 5, 1, 7, 10, 12}, 8, 5, 10, 1, 7, null, 12);
        verify(new int[]{10, 5, 1, 7, 40, 50}, 10, 5, 40, 1, 7, null, 50);
        verify(new int[]{1, 3}, 1, null, 3);
        verify(new int[]{5}, 5);                                                   // single key
        verify(new int[]{});                                                       // empty input, empty tree
        verify(new int[]{5, 4, 3, 2, 1}, 5, 4, null, 3, null, 2, null, 1);         // left chain
        verify(new int[]{1, 2, 3, 4, 5}, 1, null, 2, null, 3, null, 4, null, 5);   // right chain
        verify(new int[]{0, Integer.MIN_VALUE, Integer.MAX_VALUE}, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("OK P101_ConstructABSTFromAPreorderTraversal");
    }
}
