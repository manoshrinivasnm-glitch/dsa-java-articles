import java.util.*;

/** TUF 2811 - Strongly Connected Components (Kosaraju). Directed graph with V vertices and edges {u, v} meaning u -> v. Return every SCC. */
public class P2811_StronglyConnectedComponentUsingKosaRajus {

    /** Adjacency list for a directed graph: edge {u, v} means u -> v. */
    static List<List<Integer>> buildAdj(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    /** Sorts each component, then orders components by their smallest vertex, so answers can be compared directly. */
    static List<List<Integer>> normalise(List<List<Integer>> comps) {
        for (List<Integer> c : comps) Collections.sort(c);
        comps.sort((a, b) -> Integer.compare(a.get(0), b.get(0)));
        return comps;
    }

    /** Approach 1: reachability from every vertex; u and v share an SCC iff each reaches the other. O(V * (V + E)) time, O(V^2) space. */
    static List<List<Integer>> bruteForce(int V, int[][] edges) {
        List<List<Integer>> adj = buildAdj(V, edges);
        boolean[][] reach = new boolean[V][V];                 // reach[s][v]: there is a path s -> v
        for (int s = 0; s < V; s++) {
            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(s);
            reach[s][s] = true;
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (int v : adj.get(u)) {
                    if (!reach[s][v]) {
                        reach[s][v] = true;
                        stack.push(v);
                    }
                }
            }
        }
        boolean[] assigned = new boolean[V];
        List<List<Integer>> comps = new ArrayList<>();
        for (int u = 0; u < V; u++) {
            if (assigned[u]) continue;
            List<Integer> comp = new ArrayList<>();
            for (int v = u; v < V; v++) {
                if (!assigned[v] && reach[u][v] && reach[v][u]) {
                    assigned[v] = true;
                    comp.add(v);
                }
            }
            comps.add(comp);
        }
        return normalise(comps);
    }

    /** Approach 2: Kosaraju. DFS for finish order, reverse every edge, then DFS the reversed graph in decreasing finish time. O(V + E) time and space. */
    static List<List<Integer>> kosaraju(int V, int[][] edges) {
        List<List<Integer>> adj = buildAdj(V, edges);
        boolean[] visited = new boolean[V];
        Deque<Integer> finished = new ArrayDeque<>();          // top of the stack = vertex that finished last
        for (int u = 0; u < V; u++) {
            if (!visited[u]) dfsOrder(u, adj, visited, finished);
        }
        List<List<Integer>> rev = new ArrayList<>();
        for (int i = 0; i < V; i++) rev.add(new ArrayList<>());
        for (int[] e : edges) rev.get(e[1]).add(e[0]);         // transpose: u -> v becomes v -> u
        Arrays.fill(visited, false);
        List<List<Integer>> comps = new ArrayList<>();
        while (!finished.isEmpty()) {
            int u = finished.pop();
            if (visited[u]) continue;
            List<Integer> comp = new ArrayList<>();
            dfsCollect(u, rev, visited, comp);                 // everything reached here is exactly u's SCC
            comps.add(comp);
        }
        return normalise(comps);
    }

    static void dfsOrder(int u, List<List<Integer>> adj, boolean[] visited, Deque<Integer> finished) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) dfsOrder(v, adj, visited, finished);
        }
        finished.push(u);                                      // pushed only after all its descendants are done
    }

    static void dfsCollect(int u, List<List<Integer>> rev, boolean[] visited, List<Integer> comp) {
        visited[u] = true;
        comp.add(u);
        for (int v : rev.get(u)) {
            if (!visited[v]) dfsCollect(v, rev, visited, comp);
        }
    }

    /** Approach 3: Kosaraju with explicit stacks, safe for paths of 10^5 vertices. O(V + E) time and space. */
    static List<List<Integer>> kosarajuIterative(int V, int[][] edges) {
        List<List<Integer>> adj = buildAdj(V, edges);
        boolean[] visited = new boolean[V];
        int[] order = new int[V];                              // vertices in increasing finish time
        int[] next = new int[V];                               // next[u]: index of the next neighbour of u to try
        int finishedCount = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        for (int s = 0; s < V; s++) {
            if (visited[s]) continue;
            visited[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.peek();
                if (next[u] < adj.get(u).size()) {
                    int v = adj.get(u).get(next[u]++);
                    if (!visited[v]) {
                        visited[v] = true;
                        stack.push(v);
                    }
                } else {
                    stack.pop();
                    order[finishedCount++] = u;                // u finishes after all its descendants
                }
            }
        }
        List<List<Integer>> rev = new ArrayList<>();
        for (int i = 0; i < V; i++) rev.add(new ArrayList<>());
        for (int[] e : edges) rev.get(e[1]).add(e[0]);
        Arrays.fill(visited, false);
        List<List<Integer>> comps = new ArrayList<>();
        for (int i = V - 1; i >= 0; i--) {
            int s = order[i];
            if (visited[s]) continue;
            List<Integer> comp = new ArrayList<>();
            visited[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.pop();
                comp.add(u);
                for (int v : rev.get(u)) {
                    if (!visited[v]) {
                        visited[v] = true;
                        stack.push(v);
                    }
                }
            }
            comps.add(comp);
        }
        return normalise(comps);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> groups(int[]... gs) {
        List<List<Integer>> out = new ArrayList<>();
        for (int[] g : gs) {
            List<Integer> l = new ArrayList<>();
            for (int x : g) l.add(x);
            out.add(l);
        }
        return out;
    }

    static void verify(int V, int[][] edges, List<List<Integer>> expected) {
        check(bruteForce(V, edges).equals(expected), "bruteForce V=" + V + " got " + bruteForce(V, edges));
        check(kosaraju(V, edges).equals(expected), "kosaraju V=" + V + " got " + kosaraju(V, edges));
        check(kosarajuIterative(V, edges).equals(expected), "kosarajuIterative V=" + V + " got " + kosarajuIterative(V, edges));
    }

    public static void main(String[] args) {
        verify(5, new int[][]{{1, 0}, {0, 2}, {2, 1}, {0, 3}, {3, 4}},
                groups(new int[]{0, 1, 2}, new int[]{3}, new int[]{4}));
        verify(8, new int[][]{{0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 4}, {6, 7}},
                groups(new int[]{0, 1, 2}, new int[]{3}, new int[]{4, 5, 6}, new int[]{7}));
        verify(1, new int[][]{}, groups(new int[]{0}));                                     // single vertex
        verify(4, new int[][]{}, groups(new int[]{0}, new int[]{1}, new int[]{2}, new int[]{3}));   // no edges
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}}, groups(new int[]{0, 1, 2, 3}));      // one big cycle
        verify(3, new int[][]{{0, 0}, {0, 1}, {0, 1}, {1, 2}},                                // self loop, parallel edges, DAG
                groups(new int[]{0}, new int[]{1}, new int[]{2}));
        verify(6, new int[][]{{0, 1}, {1, 0}, {2, 3}, {3, 2}, {4, 5}},                        // several pieces
                groups(new int[]{0, 1}, new int[]{2, 3}, new int[]{4}, new int[]{5}));
        verify(6, new int[][]{{5, 4}, {4, 3}, {3, 5}, {2, 1}, {1, 0}, {0, 2}, {3, 0}},         // finish order matters here
                groups(new int[]{0, 1, 2}, new int[]{3, 4, 5}));

        // deep chain closed into one cycle: only the iterative version is safe at this depth
        int big = 100_000;
        int[][] ring = new int[big][];
        for (int i = 0; i < big; i++) ring[i] = new int[]{i, (i + 1) % big};
        List<List<Integer>> r = kosarajuIterative(big, ring);
        check(r.size() == 1 && r.get(0).size() == big, "big ring should be a single SCC");
        System.out.println("OK P2811_StronglyConnectedComponentUsingKosaRajus");
    }
}
