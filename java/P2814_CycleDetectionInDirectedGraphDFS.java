import java.util.*;

/** TUF 2814 - Cycle Detection in Directed Graph (DFS). Return true if the directed graph contains a cycle. */
public class P2814_CycleDetectionInDirectedGraphDFS {

    /** Approach 1: from every vertex s, search whether some path leads back to s. O(V * (V + E)) time, O(V) space. */
    static boolean bruteForce(int V, List<List<Integer>> adj) {
        for (int s = 0; s < V; s++) {
            boolean[] seen = new boolean[V];
            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (int v : adj.get(u)) {
                    if (v == s) return true;             // walked back to the start
                    if (!seen[v]) { seen[v] = true; stack.push(v); }
                }
            }
        }
        return false;
    }

    /** Approach 2: one DFS with vis[] and pathVis[]; an edge into a vertex on the current path is a back edge. O(V + E). */
    static boolean optimal(int V, List<List<Integer>> adj) {
        boolean[] vis = new boolean[V], pathVis = new boolean[V];
        for (int s = 0; s < V; s++) {
            if (!vis[s] && dfsFindsCycle(s, adj, vis, pathVis)) return true;
        }
        return false;
    }

    static boolean dfsFindsCycle(int u, List<List<Integer>> adj, boolean[] vis, boolean[] pathVis) {
        vis[u] = true;
        pathVis[u] = true;
        for (int v : adj.get(u)) {
            if (!vis[v]) {
                if (dfsFindsCycle(v, adj, vis, pathVis)) return true;
            } else if (pathVis[v]) {
                return true;                             // v is an ancestor of u: the edge u -> v closes a cycle
            }
        }
        pathVis[u] = false;                              // u leaves the current path
        return false;
    }

    /** Approach 3: the same DFS with one state array and an explicit stack, so deep graphs cannot overflow the call stack. */
    static boolean iterative(int V, List<List<Integer>> adj) {
        int[] state = new int[V];                        // 0 = unvisited, 1 = on current path, 2 = finished
        int[] nextEdge = new int[V];                     // index of the next neighbour to try
        Deque<Integer> stack = new ArrayDeque<>();
        for (int s = 0; s < V; s++) {
            if (state[s] != 0) continue;
            stack.push(s);
            state[s] = 1;
            while (!stack.isEmpty()) {
                int u = stack.peek();
                if (nextEdge[u] < adj.get(u).size()) {
                    int v = adj.get(u).get(nextEdge[u]++);
                    if (state[v] == 1) return true;
                    if (state[v] == 0) { state[v] = 1; stack.push(v); }
                } else {
                    state[u] = 2;                        // all edges of u explored
                    stack.pop();
                }
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> build(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    static void verify(int V, int[][] edges, boolean expected) {
        List<List<Integer>> adj = build(V, edges);
        String tag = "V=" + V + " edges=" + Arrays.deepToString(edges);
        check(bruteForce(V, adj) == expected, "bruteForce " + tag);
        check(optimal(V, adj) == expected, "optimal " + tag);
        check(iterative(V, adj) == expected, "iterative " + tag);
    }

    public static void main(String[] args) {
        int[][] base = {{1, 2}, {2, 3}, {3, 4}, {3, 7}, {4, 5}, {5, 6}, {7, 5}, {8, 9}, {9, 10}};
        verify(11, base, false);                                    // 5 is reached twice, but that is not a cycle
        int[][] withCycle = Arrays.copyOf(base, base.length + 1);
        withCycle[base.length] = new int[]{10, 8};
        verify(11, withCycle, true);                                // 8 -> 9 -> 10 -> 8
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 3}}, true);       // self-loop
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}}, false);      // diamond DAG
        verify(2, new int[][]{{0, 1}, {1, 0}}, true);                       // two-vertex cycle
        verify(3, new int[][]{{1, 0}, {2, 0}}, false);                      // edges point into 0 only
        verify(1, new int[][]{}, false);                                    // single vertex
        verify(0, new int[][]{}, false);                                    // empty graph
        int n = 200_000;                                                    // deep chain: only the iterative version is safe here
        List<List<Integer>> chain = new ArrayList<>();
        for (int i = 0; i < n; i++) chain.add(new ArrayList<>());
        for (int i = 0; i + 1 < n; i++) chain.get(i).add(i + 1);
        check(!iterative(n, chain), "long chain has no cycle");
        chain.get(n - 1).add(0);
        check(iterative(n, chain), "long ring has a cycle");
        System.out.println("OK P2814_CycleDetectionInDirectedGraphDFS");
    }
}
