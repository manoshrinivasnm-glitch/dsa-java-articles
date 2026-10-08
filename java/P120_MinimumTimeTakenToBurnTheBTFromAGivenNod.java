import java.util.*;

/**
 * TUF 120 - Minimum time taken to burn the BT from a given Node. Each second the fire spreads from every burning
 * node to its parent and children; return the seconds until the whole tree burns (the farthest node's distance).
 */
public class P120_MinimumTimeTakenToBurnTheBTFromAGivenNod {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. Store every node's root path and take the largest distance from start. O(n * h) time and space. */
    static int bruteForce(TreeNode root, TreeNode start) {
        Map<TreeNode, List<TreeNode>> pathOf = new HashMap<>();
        collectPaths(root, new ArrayList<>(), pathOf);
        List<TreeNode> ps = pathOf.get(start);
        if (ps == null) return -1;                                // start is not in the tree
        int worst = 0;
        for (List<TreeNode> pv : pathOf.values()) {
            int common = 0;                                       // nodes shared by both root paths
            while (common < ps.size() && common < pv.size() && ps.get(common) == pv.get(common)) common++;
            worst = Math.max(worst, ps.size() + pv.size() - 2 * common);
        }
        return worst;
    }

    static void collectPaths(TreeNode node, List<TreeNode> path, Map<TreeNode, List<TreeNode>> pathOf) {
        if (node == null) return;
        path.add(node);
        pathOf.put(node, new ArrayList<>(path));
        collectPaths(node.left, path, pathOf);
        collectPaths(node.right, path, pathOf);
        path.remove(path.size() - 1);
    }

    /** Approach 2: record parent links, then simulate the fire with a level-by-level BFS from start. O(n) time, O(n) space. */
    static int parentMapBfs(TreeNode root, TreeNode start) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        fillParents(root, null, parent);
        if (!parent.containsKey(start)) return -1;
        Set<TreeNode> burnt = new HashSet<>();
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(start);
        burnt.add(start);
        int time = 0;
        while (!q.isEmpty()) {
            int size = q.size();
            boolean spread = false;                               // did this second set any new node on fire?
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                for (TreeNode next : new TreeNode[]{node.left, node.right, parent.get(node)}) {
                    if (next != null && burnt.add(next)) {
                        q.add(next);
                        spread = true;
                    }
                }
            }
            if (spread) time++;
        }
        return time;
    }

    static void fillParents(TreeNode node, TreeNode par, Map<TreeNode, TreeNode> parent) {
        if (node == null) return;
        parent.put(node, par);
        fillParents(node.left, node, parent);
        fillParents(node.right, node, parent);
    }

    /** Approach 3: one post-order DFS combining subtree heights with the distance to start. O(n) time, O(h) space. */
    static int optimal(TreeNode root, TreeNode start) {
        int[] best = new int[1];
        int[] top = burnDfs(root, start, best);
        return top[1] >= 0 ? best[0] : -1;                        // -1 when start was never found
    }

    /** Returns {height, dist}: the subtree height in nodes, and the distance from node down to start (-1 if start is not below). */
    static int[] burnDfs(TreeNode node, TreeNode start, int[] best) {
        if (node == null) return new int[]{0, -1};
        int[] l = burnDfs(node.left, start, best);
        int[] r = burnDfs(node.right, start, best);
        int height = 1 + Math.max(l[0], r[0]);
        if (node == start) {
            best[0] = Math.max(best[0], height - 1);              // fire spreading straight down from start
            return new int[]{height, 0};
        }
        if (l[1] >= 0) {                                          // start is in the left subtree
            int d = l[1] + 1;
            best[0] = Math.max(best[0], d + r[0]);                // up to node, then down the right side
            return new int[]{height, d};
        }
        if (r[1] >= 0) {                                          // start is in the right subtree
            int d = r[1] + 1;
            best[0] = Math.max(best[0], d + l[0]);
            return new int[]{height, d};
        }
        return new int[]{height, -1};
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

    /** Returns the node holding val (values are unique in the tests), or null. */
    static TreeNode find(TreeNode node, int val) {
        if (node == null || node.val == val) return node;
        TreeNode inLeft = find(node.left, val);
        return inLeft != null ? inLeft : find(node.right, val);
    }

    /** A left-skewed chain 1 -> 2 -> ... -> n. */
    static TreeNode chain(int n) {
        TreeNode root = new TreeNode(1), cur = root;
        for (int v = 2; v <= n; v++) { cur.left = new TreeNode(v); cur = cur.left; }
        return root;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode root, int startVal, int expected) {
        TreeNode start = find(root, startVal);
        String in = "start " + startVal;
        check(start != null, "test setup " + in);
        check(bruteForce(root, start) == expected, "bruteForce " + in + " got " + bruteForce(root, start));
        check(parentMapBfs(root, start) == expected, "parentMapBfs " + in + " got " + parentMapBfs(root, start));
        check(optimal(root, start) == expected, "optimal " + in + " got " + optimal(root, start));
    }

    public static void main(String[] args) {
        TreeNode t = build(new Integer[]{1, 2, 3, 4, 5, null, 6, null, null, 7, 8, null, 10});
        verify(t, 8, 6);                  // 8 -> 5 -> 2 -> 1 -> 3 -> 6 -> 10
        verify(t, 1, 3);                  // from the root the answer is height - 1
        verify(t, 10, 6);
        verify(t, 4, 5);                  // the far side wins over the near subtree
        verify(t, 7, 6);
        TreeNode u = build(new Integer[]{1, 5, 3, null, 4, 10, 6, 9, 2});
        verify(u, 3, 4);
        verify(u, 9, 5);
        verify(build(new Integer[]{1}), 1, 0);          // edge: single node burns at time 0
        verify(build(new Integer[]{1, 2}), 2, 1);
        TreeNode c = chain(1000);
        verify(c, 1000, 999);             // bottom of a deep chain
        verify(c, 500, 500);              // middle: 499 edges up, 500 edges down
        verify(c, 1, 999);
        System.out.println("OK P120_MinimumTimeTakenToBurnTheBTFromAGivenNod");
    }
}
