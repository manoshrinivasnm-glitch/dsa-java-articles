import java.util.*;

/** TUF 134 - Level Order Traversal. Return the values level by level, each level left to right, as a list of lists. */
public class P134_LevelOrderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. For every depth d, walk the whole tree and keep the nodes at depth d. O(n * h) time, O(h) call stack. */
    static List<List<Integer>> bruteForce(TreeNode root) {
        List<List<Integer>> levels = new ArrayList<>();  // levels.get(d) holds the values at depth d
        int h = height(root);                            // number of levels to fill
        for (int d = 0; d < h; d++) {                    // one full tree walk per depth
            List<Integer> level = new ArrayList<>();     // values at depth d, left to right
            collect(root, d, level);                     // gathers every node at depth d
            levels.add(level);
        }
        return levels;
    }

    private static void collect(TreeNode node, int d, List<Integer> level) {
        if (node == null) return;                 // empty subtree: nothing to collect
        if (d == 0) {                             // this node is exactly at the wanted depth
            level.add(node.val);
            return;                               // no need to look deeper
        }
        collect(node.left, d - 1, level);         // left first keeps the level in left-to-right order
        collect(node.right, d - 1, level);
    }

    private static int height(TreeNode node) {
        // number of levels: 0 for an empty tree
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }

    /** Approach 2: one DFS that carries the depth and appends each node to the list of its depth. O(n) time, O(h) call stack. */
    static List<List<Integer>> dfsWithDepth(TreeNode root) {
        List<List<Integer>> levels = new ArrayList<>();  // levels.get(d) holds the values at depth d
        dfs(root, 0, levels);                            // start at the root, depth 0
        return levels;
    }

    private static void dfs(TreeNode node, int depth, List<List<Integer>> levels) {
        if (node == null) return;                                     // empty subtree: nothing to add
        if (depth == levels.size()) levels.add(new ArrayList<>());    // first node ever seen at this depth
        levels.get(depth).add(node.val);                              // add the node to the list of its depth
        dfs(node.left, depth + 1, levels);                            // left before right keeps each level ordered
        dfs(node.right, depth + 1, levels);
    }

    /** Approach 3: optimal BFS with a queue; the queue size at the start of a round is the size of the level. O(n) time, O(w) space. */
    static List<List<Integer>> bfs(TreeNode root) {
        List<List<Integer>> levels = new ArrayList<>();             // levels.get(d) holds the values at depth d
        if (root == null) return levels;                            // empty tree: no levels
        Queue<TreeNode> queue = new ArrayDeque<>();                 // nodes waiting to be visited, in level order
        queue.offer(root);                                          // level 0 is just the root
        while (!queue.isEmpty()) {                                  // each round handles one whole level
            int size = queue.size();                                // exactly the nodes of the current level
            List<Integer> level = new ArrayList<>(size);            // values of this level, left to right
            for (int i = 0; i < size; i++) {                        // take exactly this level's nodes
                TreeNode node = queue.poll();
                level.add(node.val);                                // record it in this level
                if (node.left != null) queue.offer(node.left);      // children join the back of the queue,
                if (node.right != null) queue.offer(node.right);    // behind the rest of this level
            }
            levels.add(level);                                      // this level is complete
        }
        return levels;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Build a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int i = 1;
        while (!queue.isEmpty() && i < vals.length) {
            TreeNode node = queue.poll();
            if (i < vals.length && vals[i] != null) { node.left = new TreeNode(vals[i]); queue.offer(node.left); }
            i++;
            if (i < vals.length && vals[i] != null) { node.right = new TreeNode(vals[i]); queue.offer(node.right); }
            i++;
        }
        return root;
    }

    static void verify(TreeNode root, List<List<Integer>> expected) {
        List<List<Integer>> r1 = bruteForce(root), r2 = dfsWithDepth(root), r3 = bfs(root);
        check(r1.equals(expected), "bruteForce gave " + r1 + ", expected " + expected);
        check(r2.equals(expected), "dfsWithDepth gave " + r2 + ", expected " + expected);
        check(r3.equals(expected), "bfs gave " + r3 + ", expected " + expected);
    }

    public static void main(String[] args) {
        verify(build(3, 9, 20, null, null, 15, 7), List.of(List.of(3), List.of(9, 20), List.of(15, 7)));
        verify(build(1, 2, 3, 4, 5, 6, 7), List.of(List.of(1), List.of(2, 3), List.of(4, 5, 6, 7)));
        verify(build(1, 2, 3, null, 4, null, 5), List.of(List.of(1), List.of(2, 3), List.of(4, 5)));   // gaps inside a level
        verify(build(1), List.of(List.of(1)));                                                         // single node
        verify(build(), List.of());                                                                    // edge: empty tree
        verify(build(1, 2, null, 3, null, 4), List.of(List.of(1), List.of(2), List.of(3), List.of(4))); // left-skewed

        int n = 100_000;                                     // deep right chain: only BFS avoids deep recursion
        TreeNode root = new TreeNode(1), cur = root;
        for (int v = 2; v <= n; v++) {
            cur.right = new TreeNode(v);
            cur = cur.right;
        }
        List<List<Integer>> deep = bfs(root);
        check(deep.size() == n, "deep chain has n levels");
        check(deep.get(0).equals(List.of(1)) && deep.get(n - 1).equals(List.of(n)), "deep chain levels hold one node each");
        System.out.println("OK P134_LevelOrderTraversal");
    }
}
