import java.util.*;

/** TUF 102 - Delete a node in BST. Remove the node whose value is key (if any) and return the root of a valid BST. */
public class P102_DeleteANodeInBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: read the keys in sorted order, drop key, rebuild a balanced BST. O(n) time, O(n) space. */
    static TreeNode bruteForce(TreeNode root, int key) {
        List<Integer> keys = new ArrayList<>();
        inorder(root, keys);
        keys.remove(Integer.valueOf(key));               // remove the value, not the element at index key
        return buildBalanced(keys, 0, keys.size() - 1);
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    static TreeNode buildBalanced(List<Integer> keys, int lo, int hi) {
        if (lo > hi) return null;
        int mid = (lo + hi) >>> 1;
        TreeNode node = new TreeNode(keys.get(mid));
        node.left = buildBalanced(keys, lo, mid - 1);
        node.right = buildBalanced(keys, mid + 1, hi);
        return node;
    }

    /** Approach 2: recursive; a node with two children takes its inorder successor's value. O(h) time, O(h) stack. */
    static TreeNode successorDelete(TreeNode root, int key) {
        if (root == null) return null;                   // key not found
        if (key < root.val) {
            root.left = successorDelete(root.left, key);
        } else if (key > root.val) {
            root.right = successorDelete(root.right, key);
        } else {
            if (root.left == null) return root.right;    // zero or one child: splice the node out
            if (root.right == null) return root.left;
            TreeNode succ = root.right;
            while (succ.left != null) succ = succ.left;  // smallest key in the right subtree
            root.val = succ.val;                         // copy it up ...
            root.right = successorDelete(root.right, succ.val); // ... and delete it below (it has no left child)
        }
        return root;
    }

    /** Approach 3: iterative; find the parent, then replace the node by its left subtree with the right subtree hung under the left subtree's maximum. O(h) time, O(1) space. */
    static TreeNode optimal(TreeNode root, int key) {
        if (root == null) return null;
        if (root.val == key) return joinChildren(root);
        TreeNode cur = root;
        while (cur != null) {
            if (key < cur.val) {
                if (cur.left != null && cur.left.val == key) { cur.left = joinChildren(cur.left); break; }
                cur = cur.left;
            } else {
                if (cur.right != null && cur.right.val == key) { cur.right = joinChildren(cur.right); break; }
                cur = cur.right;
            }
        }
        return root;
    }

    /** The subtree that replaces a deleted node. */
    static TreeNode joinChildren(TreeNode node) {
        if (node.left == null) return node.right;
        if (node.right == null) return node.left;
        TreeNode rightmost = node.left;
        while (rightmost.right != null) rightmost = rightmost.right; // largest key of the left subtree
        rightmost.right = node.right;                    // every key on the right is larger than it
        return node.left;
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

    /** Serialize to LeetCode-style level order with trailing nulls removed. */
    static String toLevelOrder(TreeNode root) {
        List<String> out = new ArrayList<>();
        Queue<TreeNode> q = new LinkedList<>();          // LinkedList accepts null elements
        q.add(root);
        while (!q.isEmpty()) {
            TreeNode cur = q.poll();
            if (cur == null) { out.add("null"); continue; }
            out.add(String.valueOf(cur.val));
            q.add(cur.left);
            q.add(cur.right);
        }
        while (!out.isEmpty() && out.get(out.size() - 1).equals("null")) out.remove(out.size() - 1);
        return out.toString();
    }

    static boolean isBST(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;
        return isBST(node.left, low, node.val) && isBST(node.right, node.val, high);
    }

    /** Every approach must leave a valid BST holding exactly the expected keys (shapes may differ). */
    static void verify(Integer[] level, int key, List<Integer> expectedKeys) {
        TreeNode[] results = {
            bruteForce(fromLevelOrder(level), key),      // fresh tree per approach: deletion mutates it
            successorDelete(fromLevelOrder(level), key),
            optimal(fromLevelOrder(level), key)
        };
        for (TreeNode r : results) {
            List<Integer> keys = new ArrayList<>();
            inorder(r, keys);
            check(keys.equals(expectedKeys), "delete " + key + " from " + Arrays.toString(level) + " left keys " + keys);
            check(isBST(r, Long.MIN_VALUE, Long.MAX_VALUE), "delete " + key + " from " + Arrays.toString(level) + " broke the BST");
        }
    }

    public static void main(String[] args) {
        Integer[] t = {5, 3, 6, 2, 4, null, 7};
        verify(t, 3, List.of(2, 4, 5, 6, 7));            // node with two children
        verify(t, 0, List.of(2, 3, 4, 5, 6, 7));         // key absent: tree unchanged
        verify(t, 5, List.of(2, 3, 4, 6, 7));            // the root, which has two children
        verify(t, 7, List.of(2, 3, 4, 5, 6));            // a leaf
        verify(t, 6, List.of(2, 3, 4, 5, 7));            // a node with only a right child
        verify(new Integer[]{}, 0, List.of());           // empty tree
        verify(new Integer[]{5}, 5, List.of());          // the only node
        verify(new Integer[]{5, 3}, 5, List.of(3));      // root with only a left child

        // exact shapes for the two O(h) methods on the examples above
        check(toLevelOrder(successorDelete(fromLevelOrder(t), 3)).equals("[5, 4, 6, 2, null, null, 7]"), "successor shape, key 3");
        check(toLevelOrder(optimal(fromLevelOrder(t), 3)).equals("[5, 2, 6, null, 4, null, 7]"), "optimal shape, key 3");
        check(toLevelOrder(successorDelete(fromLevelOrder(t), 5)).equals("[6, 3, 7, 2, 4]"), "successor shape, key 5");
        check(toLevelOrder(optimal(fromLevelOrder(t), 5)).equals("[3, 2, 4, null, null, null, 6, null, 7]"), "optimal shape, key 5");
        check(toLevelOrder(optimal(fromLevelOrder(t), 0)).equals("[5, 3, 6, 2, 4, null, 7]"), "absent key leaves the shape alone");

        // delete every key of a 50-node tree one at a time with each approach
        Integer[] big = new Integer[50];
        TreeNode[] roots = new TreeNode[3];
        for (int i = 0; i < 50; i++) big[i] = i + 1;
        for (int a = 0; a < 3; a++) roots[a] = buildBalanced(Arrays.asList(big), 0, 49);
        List<Integer> remaining = new ArrayList<>(Arrays.asList(big));
        for (int i = 0; i < 50; i++) {
            int key = (i * 17) % 50 + 1;                 // a fixed permutation of 1..50
            roots[0] = bruteForce(roots[0], key);
            roots[1] = successorDelete(roots[1], key);
            roots[2] = optimal(roots[2], key);
            remaining.remove(Integer.valueOf(key));
            for (TreeNode r : roots) {
                List<Integer> keys = new ArrayList<>();
                inorder(r, keys);
                check(keys.equals(remaining) && isBST(r, Long.MIN_VALUE, Long.MAX_VALUE), "after deleting " + key);
            }
        }
        check(roots[0] == null && roots[1] == null && roots[2] == null, "all keys deleted");
        System.out.println("OK P102_DeleteANodeInBST");
    }
}
