import java.util.*;

/** TUF 124 - Top View of BT. Return the nodes visible when the tree is viewed from above, from the leftmost column to the rightmost. */
public class P124_TopViewOfBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursive DFS that remembers, for every column, the shallowest node seen so far. O(n log n) time, O(n) space. */
    static List<Integer> dfsWithDepth(TreeNode root) {
        TreeMap<Integer, int[]> best = new TreeMap<>();     // column -> {depth, value} of its shallowest node
        dfs(root, 0, 0, best);                              // fill best for every column
        List<Integer> ans = new ArrayList<>();
        for (int[] dv : best.values()) ans.add(dv[1]);      // TreeMap gives the columns left to right
        return ans;
    }

    static void dfs(TreeNode node, int col, int depth, TreeMap<Integer, int[]> best) {
        if (node == null) return;                           // empty subtree: nothing to check
        int[] cur = best.get(col);                          // shallowest node stored for this column so far
        // new column, or strictly shallower than the stored node: it is the new top
        if (cur == null || depth < cur[0]) best.put(col, new int[]{depth, node.val});
        dfs(node.left, col - 1, depth + 1, best);           // left child: one column left, one level deeper
        dfs(node.right, col + 1, depth + 1, best);
    }

    /** Approach 2: level-order BFS with a TreeMap; the first node to reach a column is the top one. O(n log n) time, O(n) space. */
    static List<Integer> bfsTreeMap(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        if (root == null) return ans;                       // empty tree: empty view
        TreeMap<Integer, Integer> top = new TreeMap<>();    // column -> value of the first node to reach it
        Queue<TreeNode> nodes = new ArrayDeque<>();         // BFS queue of nodes
        Queue<Integer> cols = new ArrayDeque<>();           // column of each queued node, kept in step
        nodes.add(root);
        cols.add(0);
        while (!nodes.isEmpty()) {                          // level by level, so higher nodes come first
            TreeNode node = nodes.poll();
            int col = cols.poll();
            top.putIfAbsent(col, node.val);                 // only the first arrival counts
            if (node.left != null) { nodes.add(node.left); cols.add(col - 1); }   // left child: column - 1
            if (node.right != null) { nodes.add(node.right); cols.add(col + 1); }
        }
        ans.addAll(top.values());                           // TreeMap values come out left to right
        return ans;
    }

    /** Approach 3: BFS with a HashMap and the column range, so no sorted map is needed. O(n) time, O(n) space. */
    static List<Integer> optimal(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        if (root == null) return ans;                       // empty tree: empty view
        Map<Integer, Integer> top = new HashMap<>();        // column -> value of its top node
        int minCol = 0, maxCol = 0;                         // column range seen so far
        Queue<TreeNode> nodes = new ArrayDeque<>();         // BFS queue of nodes
        Queue<Integer> cols = new ArrayDeque<>();           // column of each queued node, kept in step
        nodes.add(root);
        cols.add(0);
        while (!nodes.isEmpty()) {                          // level by level, so higher nodes come first
            TreeNode node = nodes.poll();
            int col = cols.poll();
            top.putIfAbsent(col, node.val);                 // first arrival in a column is its top node
            minCol = Math.min(minCol, col);                 // widen the known column range
            maxCol = Math.max(maxCol, col);
            if (node.left != null) { nodes.add(node.left); cols.add(col - 1); }   // left child: column - 1
            if (node.right != null) { nodes.add(node.right); cols.add(col + 1); }
        }
        // columns are contiguous, so none is missing
        for (int c = minCol; c <= maxCol; c++) ans.add(top.get(c));
        return ans;
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

    static void verify(Integer[] vals, List<Integer> expected) {
        TreeNode root = build(vals);
        String in = Arrays.toString(vals);
        check(dfsWithDepth(root).equals(expected), "dfsWithDepth " + in + " got " + dfsWithDepth(root));
        check(bfsTreeMap(root).equals(expected), "bfsTreeMap " + in + " got " + bfsTreeMap(root));
        check(optimal(root).equals(expected), "optimal " + in + " got " + optimal(root));
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7}, List.of(4, 2, 1, 3, 7));
        // 1's right column is first reached by 3, even though DFS meets 5 (same column, deeper) before it
        verify(new Integer[]{1, 2, 3, null, 4, null, null, null, 5, null, 6}, List.of(2, 1, 3, 6));
        // column 2 has two nodes at the same depth (6 and 9): the left one is visible
        verify(new Integer[]{1, 2, 3, null, 4, 7, null, null, 5, null, 8, null, 6, null, 9}, List.of(2, 1, 3, 6));
        verify(new Integer[]{20, 8, 22, 5, 3, null, 25, null, null, 10, 14}, List.of(5, 8, 20, 22, 25));
        verify(new Integer[]{}, List.of());                            // edge: empty tree
        verify(new Integer[]{1}, List.of(1));                           // single node
        verify(new Integer[]{1, 2, null, 3, null, 4, null, 5}, List.of(5, 4, 3, 2, 1));   // left-skewed chain
        System.out.println("OK P124_TopViewOfBT");
    }
}
