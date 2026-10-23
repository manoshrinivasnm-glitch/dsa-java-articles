import java.util.*;

/** TUF 121 - Print all nodes at a distance of K in BT. Return (sorted) the values of all nodes exactly k edges away from a target node. */
public class P121_PrintAllNodesAtADistanceOfKInBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. Store every node's root path; the distance between two nodes follows from their shared prefix. O(n * h) time and space. */
    static List<Integer> bruteForce(TreeNode root, TreeNode target, int k) {
        Map<TreeNode, List<TreeNode>> pathOf = new HashMap<>();   // node -> its root-to-node path
        collectPaths(root, new ArrayList<>(), pathOf);   // fill pathOf for every node
        List<Integer> ans = new ArrayList<>();
        List<TreeNode> pt = pathOf.get(target);          // root path of the target
        if (pt == null) return ans;                      // target is not in the tree
        for (Map.Entry<TreeNode, List<TreeNode>> e : pathOf.entrySet()) {   // try every node as an answer
            List<TreeNode> pv = e.getValue();            // root path of this candidate
            int common = 0;                              // nodes shared by both root paths
            // count the shared prefix of the two paths
            while (common < pt.size() && common < pv.size() && pt.get(common) == pv.get(common)) common++;
            // edges from target up to the shared part, plus down to this node
            if (pt.size() + pv.size() - 2 * common == k) ans.add(e.getKey().val);
        }
        Collections.sort(ans);                           // sorted, so the order is fixed
        return ans;
    }

    static void collectPaths(TreeNode node, List<TreeNode> path, Map<TreeNode, List<TreeNode>> pathOf) {
        if (node == null) return;                        // empty subtree: nothing to record
        path.add(node);                                  // extend the current root path
        pathOf.put(node, new ArrayList<>(path));         // store a snapshot for this node
        collectPaths(node.left, path, pathOf);
        collectPaths(node.right, path, pathOf);
        path.remove(path.size() - 1);                    // undo before returning to the parent
    }

    /** Approach 2: record parent links, then BFS outward from the target for k rounds, as in an undirected graph. O(n) time, O(n) space. */
    static List<Integer> parentMapBfs(TreeNode root, TreeNode target, int k) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();   // child -> parent, so BFS can also walk up
        fillParents(root, null, parent);                 // record every node's parent
        List<Integer> ans = new ArrayList<>();
        if (!parent.containsKey(target)) return ans;     // target is not in the tree
        Set<TreeNode> visited = new HashSet<>();         // reached nodes, so none is counted twice
        Queue<TreeNode> q = new ArrayDeque<>();          // current BFS ring around the target
        q.add(target);
        visited.add(target);
        int dist = 0;                                    // distance of the ring held in q
        while (!q.isEmpty() && dist < k) {               // afterwards q holds exactly the ring at distance k
            int size = q.size();                         // number of nodes in the current ring
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                // neighbours of a node: both children and its parent
                for (TreeNode next : new TreeNode[]{node.left, node.right, parent.get(node)}) {
                    // add() is false for an already visited node
                    if (next != null && visited.add(next)) q.add(next);
                }
            }
            dist++;                                      // the ring moved one step outward
        }
        for (TreeNode node : q) ans.add(node.val);       // collect the ring at distance k
        Collections.sort(ans);
        return ans;
    }

    static void fillParents(TreeNode node, TreeNode par, Map<TreeNode, TreeNode> parent) {
        if (node == null) return;                        // empty subtree: nothing to record
        parent.put(node, par);                           // record this node's parent
        fillParents(node.left, node, parent);            // this node is its children's parent
        fillParents(node.right, node, parent);
    }

    /** Approach 3: one DFS returning each node's distance to the target; ancestors collect answers from their other subtree. O(n) time, O(h) space. */
    static List<Integer> optimal(TreeNode root, TreeNode target, int k) {
        List<Integer> ans = new ArrayList<>();
        distanceToTarget(root, target, k, ans);          // one DFS fills ans
        Collections.sort(ans);                           // sorted, so the order is fixed
        return ans;
    }

    /** Returns the distance from node down to target, or -1 when target is not in node's subtree. */
    static int distanceToTarget(TreeNode node, TreeNode target, int k, List<Integer> ans) {
        if (node == null) return -1;                     // empty subtree: target not here
        if (node == target) {                            // found the target
            collectDown(node, k, ans);                   // nodes k levels below the target
            return 0;                                    // target is 0 edges from itself
        }
        // distance from the left child to target, or -1
        int left = distanceToTarget(node.left, target, k, ans);
        if (left >= 0) {                                 // target is in the left subtree
            int d = left + 1;                            // distance from node to target
            if (d == k) ans.add(node.val);               // this ancestor is exactly k away
            else if (d < k) collectDown(node.right, k - d - 1, ans);   // look down the other (right) side
            return d;                                    // tell the parent how far target is
        }
        // not on the left: search the right subtree
        int right = distanceToTarget(node.right, target, k, ans);
        if (right >= 0) {                                // target is in the right subtree
            int d = right + 1;                           // distance from node to target
            if (d == k) ans.add(node.val);
            else if (d < k) collectDown(node.left, k - d - 1, ans);   // look down the other (left) side
            return d;
        }
        return -1;                                       // target is not below this node
    }

    static void collectDown(TreeNode node, int depth, List<Integer> ans) {
        if (node == null || depth < 0) return;           // nothing here, or past the wanted depth
        if (depth == 0) { ans.add(node.val); return; }   // reached the wanted depth: record it
        collectDown(node.left, depth - 1, ans);          // one level further down on both sides
        collectDown(node.right, depth - 1, ans);
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

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode root, int targetVal, int k, List<Integer> expected) {
        TreeNode target = find(root, targetVal);
        String in = "target " + targetVal + " k " + k;
        check(target != null, "test setup " + in);
        check(bruteForce(root, target, k).equals(expected), "bruteForce " + in + " got " + bruteForce(root, target, k));
        check(parentMapBfs(root, target, k).equals(expected), "parentMapBfs " + in + " got " + parentMapBfs(root, target, k));
        check(optimal(root, target, k).equals(expected), "optimal " + in + " got " + optimal(root, target, k));
    }

    public static void main(String[] args) {
        TreeNode t = build(new Integer[]{3, 5, 1, 6, 2, 0, 8, null, null, 7, 4});
        verify(t, 5, 2, List.of(1, 4, 7));        // two below the target, one through the root
        verify(t, 5, 0, List.of(5));              // k = 0 is the target itself
        verify(t, 5, 1, List.of(2, 3, 6));        // children and parent
        verify(t, 5, 3, List.of(0, 8));           // up to the root, then down the other side
        verify(t, 6, 3, List.of(1, 4, 7));        // leaf target: everything is reached by going up first
        verify(t, 3, 2, List.of(0, 2, 6, 8));     // target is the root
        verify(t, 7, 4, List.of(1));
        verify(t, 4, 3, List.of(3, 6));
        verify(t, 5, 10, List.of());              // k larger than any distance
        TreeNode single = build(new Integer[]{1});
        verify(single, 1, 0, List.of(1));         // edge: single node
        verify(single, 1, 1, List.of());
        System.out.println("OK P121_PrintAllNodesAtADistanceOfKInBT");
    }
}
