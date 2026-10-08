import java.util.*;
import java.util.function.*;

/** TUF 188 - Clone Graph. Return a deep copy of a connected undirected graph given one of its nodes. */
public class P188_CloneGraph {

    static class Node {
        int val;
        List<Node> neighbors = new ArrayList<>();
        Node(int val) { this.val = val; }
    }

    /** Approach 1: collect every node first, create all copies, then wire the edges. O(V + E) time, O(V) space. */
    static Node twoPass(Node node) {
        if (node == null) return null;
        List<Node> all = new ArrayList<>();
        Set<Node> seen = new HashSet<>();
        Deque<Node> queue = new ArrayDeque<>();
        seen.add(node);
        queue.add(node);
        while (!queue.isEmpty()) {                       // pass 1: find every original node
            Node cur = queue.poll();
            all.add(cur);
            for (Node nb : cur.neighbors) {
                if (seen.add(nb)) queue.add(nb);
            }
        }
        Map<Node, Node> copy = new HashMap<>();
        for (Node orig : all) copy.put(orig, new Node(orig.val));
        for (Node orig : all) {                          // pass 2: copy each adjacency list
            Node c = copy.get(orig);
            for (Node nb : orig.neighbors) c.neighbors.add(copy.get(nb));
        }
        return copy.get(node);
    }

    /** Approach 2: recursive DFS that creates a copy the first time it meets a node. O(V + E) time, O(V) space. */
    static Node cloneDfs(Node node) {
        return dfs(node, new HashMap<>());
    }

    static Node dfs(Node node, Map<Node, Node> copy) {
        if (node == null) return null;
        Node done = copy.get(node);
        if (done != null) return done;                   // already copied: reuse it, this closes cycles
        Node c = new Node(node.val);
        copy.put(node, c);                               // register BEFORE recursing into neighbours
        for (Node nb : node.neighbors) c.neighbors.add(dfs(nb, copy));
        return c;
    }

    /** Approach 3: iterative BFS with the same original-to-copy map. O(V + E) time, O(V) space. */
    static Node cloneBfs(Node node) {
        if (node == null) return null;
        Map<Node, Node> copy = new HashMap<>();
        copy.put(node, new Node(node.val));
        Deque<Node> queue = new ArrayDeque<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            Node c = copy.get(cur);
            for (Node nb : cur.neighbors) {
                Node nbCopy = copy.get(nb);
                if (nbCopy == null) {                    // first sighting: create it and schedule a visit
                    nbCopy = new Node(nb.val);
                    copy.put(nb, nbCopy);
                    queue.add(nb);
                }
                c.neighbors.add(nbCopy);
            }
        }
        return copy.get(node);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Builds the LeetCode format: adj[i] lists the neighbours of the node with value i + 1. Returns node 1. */
    static Node build(int[][] adj) {
        if (adj.length == 0) return null;
        Node[] nodes = new Node[adj.length];
        for (int i = 0; i < adj.length; i++) nodes[i] = new Node(i + 1);
        for (int i = 0; i < adj.length; i++) {
            for (int v : adj[i]) nodes[i].neighbors.add(nodes[v - 1]);
        }
        return nodes[0];
    }

    /** Collects every node reachable from start (by identity). */
    static List<Node> collect(Node start) {
        List<Node> out = new ArrayList<>();
        if (start == null) return out;
        Set<Node> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Node> st = new ArrayDeque<>();
        seen.add(start);
        st.push(start);
        while (!st.isEmpty()) {
            Node cur = st.pop();
            out.add(cur);
            for (Node nb : cur.neighbors) if (seen.add(nb)) st.push(nb);
        }
        return out;
    }

    static int[][] toAdj(Node start) {
        List<Node> all = collect(start);
        int[][] adj = new int[all.size()][];
        for (Node x : all) {
            adj[x.val - 1] = new int[x.neighbors.size()];
            for (int k = 0; k < x.neighbors.size(); k++) adj[x.val - 1][k] = x.neighbors.get(k).val;
        }
        return adj;
    }

    static void verify(int[][] adj) {
        Node original = build(adj);
        List<Function<Node, Node>> methods = List.of(P188_CloneGraph::twoPass, P188_CloneGraph::cloneDfs, P188_CloneGraph::cloneBfs);
        for (Function<Node, Node> m : methods) {
            Node clone = m.apply(original);
            if (original == null) {
                check(clone == null, "empty graph must clone to null");
                continue;
            }
            check(Arrays.deepEquals(toAdj(clone), adj), "structure differs for " + Arrays.deepToString(adj));
            Set<Node> orig = Collections.newSetFromMap(new IdentityHashMap<>());
            orig.addAll(collect(original));
            for (Node x : collect(clone)) check(!orig.contains(x), "clone shares a node with the original");
            check(Arrays.deepEquals(toAdj(original), adj), "original graph was modified");
        }
    }

    public static void main(String[] args) {
        verify(new int[][]{{2, 4}, {1, 3}, {2, 4}, {1, 3}});            // 4-cycle (LeetCode example 1)
        verify(new int[][]{{}});                                         // single node, no edges
        verify(new int[][]{});                                           // empty graph
        verify(new int[][]{{2}, {1}});                                   // one edge
        verify(new int[][]{{2, 3, 4}, {1, 3, 4}, {1, 2, 4}, {1, 2, 3}}); // complete graph K4
        verify(new int[][]{{2}, {1, 3}, {2, 4}, {3, 5}, {4}});           // path
        System.out.println("OK P188_CloneGraph");
    }
}
