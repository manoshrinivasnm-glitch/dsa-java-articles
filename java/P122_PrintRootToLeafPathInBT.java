import java.util.*;

/** TUF 122 - Print root to leaf path in BT. Return every root-to-leaf path, left to right; also find the root-to-node path for a given value. */
public class P122_PrintRootToLeafPathInBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: pass a fresh copy of the path into every recursive call. O(n * h) time and space. */
    static List<List<Integer>> bruteForce(TreeNode root) {
        List<List<Integer>> paths = new ArrayList<>();
        copyDfs(root, new ArrayList<>(), paths);
        return paths;
    }

    static void copyDfs(TreeNode node, List<Integer> pathSoFar, List<List<Integer>> paths) {
        if (node == null) return;
        List<Integer> path = new ArrayList<>(pathSoFar);          // every call owns its own copy
        path.add(node.val);
        if (node.left == null && node.right == null) { paths.add(path); return; }
        copyDfs(node.left, path, paths);
        copyDfs(node.right, path, paths);
    }

    /** Approach 2: backtracking with one shared path: add on the way down, remove on the way back up. O(n + L * h) time, O(h) extra space. */
    static List<List<Integer>> optimal(TreeNode root) {
        List<List<Integer>> paths = new ArrayList<>();
        backtrack(root, new ArrayList<>(), paths);
        return paths;
    }

    static void backtrack(TreeNode node, List<Integer> path, List<List<Integer>> paths) {
        if (node == null) return;
        path.add(node.val);
        if (node.left == null && node.right == null) {
            paths.add(new ArrayList<>(path));                     // snapshot: path keeps changing after this
        } else {
            backtrack(node.left, path, paths);
            backtrack(node.right, path, paths);
        }
        path.remove(path.size() - 1);                             // undo before returning to the parent
    }

    /** Variant: path from the root to the first node holding target (empty if absent). O(n) time, O(h) space. */
    static List<Integer> pathToNode(TreeNode root, int target) {
        List<Integer> path = new ArrayList<>();
        findPath(root, target, path);
        return path;
    }

    static boolean findPath(TreeNode node, int target, List<Integer> path) {
        if (node == null) return false;
        path.add(node.val);
        if (node.val == target) return true;
        if (findPath(node.left, target, path) || findPath(node.right, target, path)) return true;
        path.remove(path.size() - 1);                             // target is not below this node
        return false;
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

    static void verify(Integer[] vals, List<List<Integer>> expected) {
        TreeNode root = build(vals);
        String in = Arrays.toString(vals);
        check(bruteForce(root).equals(expected), "bruteForce " + in + " got " + bruteForce(root));
        check(optimal(root).equals(expected), "optimal " + in + " got " + optimal(root));
    }

    static void verifyPath(Integer[] vals, int target, List<Integer> expected) {
        List<Integer> got = pathToNode(build(vals), target);
        check(got.equals(expected), "pathToNode " + Arrays.toString(vals) + " target " + target + " got " + got);
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 3, null, 5}, List.of(List.of(1, 2, 5), List.of(1, 3)));
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7},
               List.of(List.of(1, 2, 4), List.of(1, 2, 5), List.of(1, 3, 6), List.of(1, 3, 7)));
        verify(new Integer[]{1, 2, null, 3, null, 4}, List.of(List.of(1, 2, 3, 4)));        // chain: one path
        verify(new Integer[]{1, 1, 1}, List.of(List.of(1, 1), List.of(1, 1)));              // duplicate values
        verify(new Integer[]{-1, -2, 3}, List.of(List.of(-1, -2), List.of(-1, 3)));          // negative values
        verify(new Integer[]{1}, List.of(List.of(1)));                                       // single node is a leaf
        verify(new Integer[]{}, List.of());                                                  // edge: empty tree

        Integer[] t = {1, 2, 3, 4, 5, 6, 7, null, null, 8, 9};
        verifyPath(t, 9, List.of(1, 2, 5, 9));
        verifyPath(t, 6, List.of(1, 3, 6));
        verifyPath(t, 4, List.of(1, 2, 4));
        verifyPath(t, 1, List.of(1));                                                        // target is the root
        verifyPath(t, 42, List.of());                                                        // target absent
        verifyPath(new Integer[]{}, 1, List.of());                                           // empty tree
        System.out.println("OK P122_PrintRootToLeafPathInBT");
    }
}
