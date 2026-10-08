import java.util.*;

/** TUF 2815 - Detect a cycle in a directed graph using BFS (Kahn's algorithm). adj.get(u) lists v for every edge u -> v. */
public class P2815_DetectACycleInADirectedGraphUsingBFS {

    /** Approach 1: BFS from every vertex s and check whether some edge leads back into s. O(V * (V + E)) time, O(V) space. */
    static boolean bruteForce(int V, List<List<Integer>> adj) {
        for (int s = 0; s < V; s++) {
            boolean[] seen = new boolean[V];
            Deque<Integer> queue = new ArrayDeque<>();
            seen[s] = true;
            queue.add(s);
            while (!queue.isEmpty()) {
                int x = queue.poll();
                for (int y : adj.get(x)) {
                    if (y == s) return true;              // walked from s back to s
                    if (!seen[y]) {
                        seen[y] = true;
                        queue.add(y);
                    }
                }
            }
        }
        return false;
    }

    /** Approach 2: Kahn's algorithm. Repeatedly remove vertices of in-degree 0; leftovers mean a cycle. O(V + E) time, O(V) space. */
    static boolean optimal(int V, List<List<Integer>> adj) {
        int[] indegree = new int[V];
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) indegree[v]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int u = 0; u < V; u++) {
            if (indegree[u] == 0) queue.add(u);
        }
        int removed = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            removed++;
            for (int v : adj.get(u)) {
                if (--indegree[v] == 0) queue.add(v);     // v has no remaining prerequisites
            }
        }
        return removed < V;                               // vertices on or behind a cycle never reach in-degree 0
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> graph(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    static void verify(int V, int[][] edges, boolean expected) {
        List<List<Integer>> adj = graph(V, edges);
        check(bruteForce(V, adj) == expected, "bruteForce " + Arrays.deepToString(edges));
        check(optimal(V, adj) == expected, "optimal " + Arrays.deepToString(edges));
    }

    public static void main(String[] args) {
        verify(6, new int[][]{{5, 0}, {4, 0}, {5, 2}, {4, 1}, {2, 3}, {3, 1}}, false); // DAG
        verify(5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 1}, {3, 4}}, true);          // 1 -> 2 -> 3 -> 1
        verify(1, new int[][]{}, false);                                               // single vertex
        verify(1, new int[][]{{0, 0}}, true);                                          // self-loop
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}}, false);                 // diamond
        verify(4, new int[][]{{0, 1}, {1, 0}, {2, 3}}, true);                          // cycle with no in-degree-0 entry
        verify(3, new int[][]{{0, 1}, {0, 1}, {1, 2}}, false);                         // repeated edge
        verify(4, new int[][]{}, false);                                               // no edges
        System.out.println("OK P2815_DetectACycleInADirectedGraphUsingBFS");
    }
}
