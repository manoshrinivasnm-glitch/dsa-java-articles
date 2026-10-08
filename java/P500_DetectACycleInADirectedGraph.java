import java.util.*;

/** TUF 500 - Detect a cycle in a directed graph. Return true if the graph with V nodes and directed edges {u, v} (u -> v) has a cycle. */
public class P500_DetectACycleInADirectedGraph {

    /** Builds an adjacency list from directed edges {u, v} meaning u -> v. */
    static List<List<Integer>> buildAdj(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    /** Approach 1: from every node s, search for a walk that comes back to s. O(V * (V + E)) time, O(V + E) space. */
    static boolean bruteForce(int V, int[][] edges) {
        List<List<Integer>> adj = buildAdj(V, edges);
        for (int s = 0; s < V; s++) {
            boolean[] seen = new boolean[V];
            Deque<Integer> stack = new ArrayDeque<>(adj.get(s));
            while (!stack.isEmpty()) {
                int u = stack.pop();
                if (u == s) return true;                      // walked back to the start
                if (seen[u]) continue;
                seen[u] = true;
                for (int v : adj.get(u)) stack.push(v);
            }
        }
        return false;
    }

    /** Approach 2: DFS that remembers which nodes are on the current recursion path. O(V + E) time, O(V) space. */
    static boolean dfsPathVisited(int V, int[][] edges) {
        List<List<Integer>> adj = buildAdj(V, edges);
        boolean[] visited = new boolean[V], onPath = new boolean[V];
        for (int i = 0; i < V; i++) {
            if (!visited[i] && dfs(i, adj, visited, onPath)) return true;
        }
        return false;
    }

    static boolean dfs(int u, List<List<Integer>> adj, boolean[] visited, boolean[] onPath) {
        visited[u] = true;
        onPath[u] = true;
        for (int v : adj.get(u)) {
            if (onPath[v]) return true;                       // back edge: v is an ancestor on the current path
            if (!visited[v] && dfs(v, adj, visited, onPath)) return true;
        }
        onPath[u] = false;                                    // leaving u: it is no longer on the path
        return false;
    }

    /** Approach 3: Kahn's algorithm. If topological sort cannot remove every node, a cycle exists. O(V + E) time, O(V) space. */
    static boolean kahn(int V, int[][] edges) {
        List<List<Integer>> adj = buildAdj(V, edges);
        int[] indegree = new int[V];
        for (int[] e : edges) indegree[e[1]]++;
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < V; i++) if (indegree[i] == 0) queue.add(i);
        int removed = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            removed++;
            for (int v : adj.get(u)) {
                if (--indegree[v] == 0) queue.add(v);
            }
        }
        return removed < V;                                   // nodes never freed sit on or behind a cycle
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int V, int[][] edges, boolean expected) {
        String name = "V=" + V + " edges=" + Arrays.deepToString(edges);
        check(bruteForce(V, edges) == expected, "bruteForce " + name);
        check(dfsPathVisited(V, edges) == expected, "dfsPathVisited " + name);
        check(kahn(V, edges) == expected, "kahn " + name);
    }

    public static void main(String[] args) {
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 3}}, false);                    // simple chain
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 0}, {2, 3}}, true);             // 0 -> 1 -> 2 -> 0
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}}, false);            // diamond: 3 reached twice, still no cycle
        verify(1, new int[][]{{0, 0}}, true);                                     // self-loop
        verify(3, new int[][]{}, false);                                          // no edges
        verify(0, new int[][]{}, false);                                          // empty graph
        verify(6, new int[][]{{0, 1}, {1, 2}, {3, 4}, {4, 5}, {5, 3}}, true);     // cycle only in the second component
        int[][] tuf = {{1, 2}, {2, 3}, {3, 4}, {3, 7}, {4, 5}, {5, 6}, {7, 5}, {8, 9}, {9, 10}, {10, 8}};
        verify(11, tuf, true);                                                    // 8 -> 9 -> 10 -> 8
        verify(11, Arrays.copyOf(tuf, tuf.length - 1), false);                    // same graph without 10 -> 8
        int n = 3000;
        int[][] chain = new int[n - 1][];
        for (int i = 0; i + 1 < n; i++) chain[i] = new int[]{i, i + 1};
        verify(n, chain, false);                                                  // long path, deep recursion
        int[][] loop = Arrays.copyOf(chain, n);
        loop[n - 1] = new int[]{n - 1, 0};
        verify(n, loop, true);                                                    // one big cycle
        System.out.println("OK P500_DetectACycleInADirectedGraph");
    }
}
