import java.util.*;

/** TUF 2782 - Right/Left View of Binary Tree. Return the first node seen on every level when the tree is viewed from the right (or from the left), top to bottom. */
public class P2782_RightLeftViewOfBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1 (right view): level-order BFS; the last node of each level is the one visible from the right. O(n) time, O(w) space. */
    static List<Integer> rightViewBfs(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        if (root == null) return ans;
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            int size = q.size();                                  // number of nodes on the current level
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                if (i == size - 1) ans.add(node.val);             // last node of this level
                if (node.left != null) q.add(node.left);
                if (node.right != null) q.add(node.right);
            }
        }
        return ans;
    }

    /** Approach 1 (left view): the same BFS, keeping the first node of each level instead. O(n) time, O(w) space. */
    static List<Integer> leftViewBfs(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        if (root == null) return ans;
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                if (i == 0) ans.add(node.val);                    // first node of this level
                if (node.left != null) q.add(node.left);
                if (node.right != null) q.add(node.right);
            }
        }
        return ans;
    }

    /** Approach 2 (right view): DFS visiting right before left; the first node reached at each depth is the answer. O(n) time, O(h) space. */
    static List<Integer> rightViewDfs(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        rightDfs(root, 0, ans);
        return ans;
    }

    static void rightDfs(TreeNode node, int depth, List<Integer> ans) {
        if (node == null) return;
        if (depth == ans.size()) ans.add(node.val);               // first time we reach this depth
        rightDfs(node.right, depth + 1, ans);
        rightDfs(node.left, depth + 1, ans);
    }

    /** Approach 2 (left view): DFS visiting left before right. O(n) time, O(h) space. */
    static List<Integer> leftViewDfs(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        leftDfs(root, 0, ans);
        return ans;
    }

    static void leftDfs(TreeNode node, int depth, List<Integer> ans) {
        if (node == null) return;
        if (depth == ans.size()) ans.add(node.val);
        leftDfs(node.left, depth + 1, ans);
        leftDfs(node.right, depth + 1, ans);
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode build(Integer[] vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < vals.length) {
            TreeNode cur = q.poll();
            if (i < vals.length && vals[i] != null) { cur.left = new TreeNode(vals[i]); q.add(cur.left); }
            i++;
            if (i < vals.length && vals[i] != null) { cur.right = new TreeNode(vals[i]); q.add(cur.right); }
            i++;
        }
        return root;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] vals, List<Integer> right, List<Integer> left) {
        TreeNode root = build(vals);
        String in = Arrays.toString(vals);
        check(rightViewBfs(root).equals(right), "rightViewBfs " + in + " got " + rightViewBfs(root));
        check(rightViewDfs(root).equals(right), "rightViewDfs " + in + " got " + rightViewDfs(root));
        check(leftViewBfs(root).equals(left), "leftViewBfs " + in + " got " + leftViewBfs(root));
        check(leftViewDfs(root).equals(left), "leftViewDfs " + in + " got " + leftViewDfs(root));
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 3, null, 5, null, 4}, List.of(1, 3, 4), List.of(1, 2, 5));
        verify(new Integer[]{1, 2, 3, 4}, List.of(1, 3, 4), List.of(1, 2, 4));          // a left node shows from the right
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7, null, null, 8}, List.of(1, 3, 7, 8), List.of(1, 2, 4, 8));
        verify(new Integer[]{1, null, 3}, List.of(1, 3), List.of(1, 3));
        verify(new Integer[]{1, 2, null, 3, null, 4}, List.of(1, 2, 3, 4), List.of(1, 2, 3, 4));   // left-skewed chain
        verify(new Integer[]{}, List.of(), List.of());                 // edge: empty tree
        verify(new Integer[]{7}, List.of(7), List.of(7));                // single node
        System.out.println("OK P2782_RightLeftViewOfBinaryTree");
    }
}
