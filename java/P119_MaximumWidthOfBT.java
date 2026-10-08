import java.util.*;

/** TUF 119 - Maximum Width of BT. Width of a level = positions from its leftmost to its rightmost node, counting the gaps; return the largest. */
public class P119_MaximumWidthOfBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. Level order that keeps explicit null placeholders between the two ends of each level. Exponential in the worst case. */
    static int bruteForce(TreeNode root) {
        if (root == null) return 0;
        List<TreeNode> level = new ArrayList<>();
        level.add(root);
        int best = 0;
        while (!level.isEmpty()) {
            best = Math.max(best, level.size());                  // the level is trimmed, so its size is its width
            List<TreeNode> next = new ArrayList<>();
            for (TreeNode node : level) {
                next.add(node == null ? null : node.left);        // a gap has two gap children
                next.add(node == null ? null : node.right);
            }
            int lo = 0, hi = next.size() - 1;                     // trim placeholders at both ends
            while (lo <= hi && next.get(lo) == null) lo++;
            while (hi >= lo && next.get(hi) == null) hi--;
            level = new ArrayList<>(next.subList(lo, hi + 1));
        }
        return best;
    }

    /** Approach 2: DFS numbering nodes like a heap, re-based on the leftmost index of each depth. O(n) time, O(h) space. */
    static int dfs(TreeNode root) {
        return (int) dfsWidth(root, 0, 0L, new ArrayList<>());
    }

    static long dfsWidth(TreeNode node, int depth, long index, List<Long> firstAtDepth) {
        if (node == null) return 0;
        if (depth == firstAtDepth.size()) firstAtDepth.add(index);   // preorder meets the leftmost node of a depth first
        long first = firstAtDepth.get(depth);
        long width = index - first + 1;
        long childBase = 2 * (index - first);                     // children numbered relative to the leftmost node
        width = Math.max(width, dfsWidth(node.left, depth + 1, childBase, firstAtDepth));
        width = Math.max(width, dfsWidth(node.right, depth + 1, childBase + 1, firstAtDepth));
        return width;
    }

    /** Approach 3: level-order BFS carrying heap indices, re-based to start at 0 on every level. O(n) time, O(w) space. */
    static int optimal(TreeNode root) {
        if (root == null) return 0;
        Queue<TreeNode> nodes = new ArrayDeque<>();
        Queue<Long> indices = new ArrayDeque<>();
        nodes.add(root);
        indices.add(0L);
        long best = 0;
        while (!nodes.isEmpty()) {
            int size = nodes.size();
            long first = indices.peek();                          // index of the leftmost node on this level
            long last = 0;
            for (int i = 0; i < size; i++) {
                TreeNode node = nodes.poll();
                long idx = indices.poll() - first;                // re-base so this level starts at 0
                last = idx;
                if (node.left != null) { nodes.add(node.left); indices.add(2 * idx); }
                if (node.right != null) { nodes.add(node.right); indices.add(2 * idx + 1); }
            }
            best = Math.max(best, last + 1);                      // last is already relative to the leftmost node
        }
        return (int) best;
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

    /** A chain of n nodes that alternates left, right, left, ... (one node per level). */
    static TreeNode zigzag(int n) {
        TreeNode root = new TreeNode(1), cur = root;
        for (int v = 2; v <= n; v++) {
            TreeNode next = new TreeNode(v);
            if (v % 2 == 0) cur.left = next; else cur.right = next;
            cur = next;
        }
        return root;
    }

    /** A root with an all-left arm and an all-right arm, each d nodes long: the bottom level has width 2^d. */
    static TreeNode arms(int d) {
        TreeNode root = new TreeNode(0), l = root, r = root;
        for (int i = 1; i <= d; i++) {
            l.left = new TreeNode(-i);
            l = l.left;
            r.right = new TreeNode(i);
            r = r.right;
        }
        return root;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode root, int expected, String label) {
        check(bruteForce(root) == expected, "bruteForce " + label + " got " + bruteForce(root));
        check(dfs(root) == expected, "dfs " + label + " got " + dfs(root));
        check(optimal(root) == expected, "optimal " + label + " got " + optimal(root));
    }

    static void verify(Integer[] vals, int expected) {
        verify(build(vals), expected, Arrays.toString(vals));
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 3, 2, 5, 3, null, 9}, 4);                 // gap in the middle counts
        verify(new Integer[]{1, 3, 2, 5, null, null, 9, 6, null, 7}, 7);  // deepest level: 6 ... 7 spans 7 slots
        verify(new Integer[]{1, 3, 2, 5}, 2);                             // widest level is not the last one
        verify(new Integer[]{1}, 1);
        verify(new Integer[]{}, 0);                                       // edge: empty tree
        verify(zigzag(2000), 1, "zigzag chain of 2000");                  // heap indices would need 2000 bits without re-basing
        verify(arms(16), 65536, "two arms of 16");
        System.out.println("OK P119_MaximumWidthOfBT");
    }
}
