import java.util.*;

/** TUF 2817 - Cycle Detection in Undirected Graph (BFS). Vertices 0..n-1, undirected edge list, possibly disconnected, no parallel edges. Is there a cycle? */
public class P2817_CycleDetectionInUndirectedGraphBfs {

    /** Approach 1: an edge (u, v) lies on a cycle exactly when v is still reachable from u without it. Test every edge. O(E * (V + E)). */
    static boolean bruteForce(int n, int[][] edges) {
        for (int skip = 0; skip < edges.length; skip++) {
            int u = edges[skip][0], v = edges[skip][1];
            if (u == v) return true;                          // a self-loop is a cycle on its own
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
            for (int i = 0; i < edges.length; i++) {
                if (i == skip) continue;
                adj.get(edges[i][0]).add(edges[i][1]);
                adj.get(edges[i][1]).add(edges[i][0]);
            }
            if (reachable(adj, u, v)) return true;            // another route plus this edge closes a loop
        }
        return false;
    }

    static boolean reachable(List<List<Integer>> adj, int from, int to) {
        boolean[] seen = new boolean[adj.size()];
        Deque<Integer> queue = new ArrayDeque<>();
        seen[from] = true;
        queue.add(from);
        while (!queue.isEmpty()) {
            int x = queue.poll();
            if (x == to) return true;
            for (int y : adj.get(x)) {
                if (!seen[y]) {
                    seen[y] = true;
                    queue.add(y);
                }
            }
        }
        return false;
    }

    /** Approach 2: BFS remembering each vertex's parent. A visited neighbour that is not the parent means two routes met: a cycle. O(V + E). */
    static boolean optimalBfs(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        boolean[] visited = new boolean[n];
        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;                     // every component needs its own BFS
            if (bfsFindsCycle(adj, start, visited)) return true;
        }
        return false;
    }

    static boolean bfsFindsCycle(List<List<Integer>> adj, int start, boolean[] visited) {
        Deque<int[]> queue = new ArrayDeque<>();              // entries are {vertex, parent}
        visited[start] = true;
        queue.add(new int[]{start, -1});
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int node = cur[0], parent = cur[1];
            for (int next : adj.get(node)) {
                if (!visited[next]) {
                    visited[next] = true;
                    queue.add(new int[]{next, node});
                } else if (next != parent) {
                    return true;                              // reached already by a different route
                }
            }
        }
        return false;
    }

    /** Approach 3: union-find. An edge whose ends are already in one set closes a cycle. O(E * alpha(n)). */
    static boolean unionFind(int n, int[][] edges) {
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        for (int[] e : edges) {
            int a = find(parent, e[0]), b = find(parent, e[1]);
            if (a == b) return true;                          // u and v were already connected
            parent[a] = b;
        }
        return false;
    }

    static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] edges, boolean expected) {
        check(bruteForce(n, edges) == expected, "bruteForce expected " + expected + " for " + Arrays.deepToString(edges));
        check(optimalBfs(n, edges) == expected, "optimalBfs expected " + expected + " for " + Arrays.deepToString(edges));
        check(unionFind(n, edges) == expected, "unionFind expected " + expected + " for " + Arrays.deepToString(edges));
    }

    public static void main(String[] args) {
        verify(5, new int[][]{ {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 1} }, true);      // 1-2-3-4-1
        verify(5, new int[][]{ {0, 1}, {1, 2}, {1, 3}, {3, 4} }, false);             // a tree
        verify(4, new int[][]{ {0, 1}, {1, 2}, {2, 3}, {3, 0} }, true);              // a square: BFS meets at 2
        verify(3, new int[][]{ {0, 1}, {1, 2}, {2, 0} }, true);                      // triangle: siblings 1 and 2 see each other
        verify(1, new int[0][], false);                                              // single vertex
        verify(0, new int[0][], false);                                              // empty graph
        verify(4, new int[0][], false);                                              // no edges
        verify(6, new int[][]{ {0, 1}, {1, 2}, {3, 4}, {4, 5}, {5, 3} }, true);      // the cycle is not in vertex 0's component
        verify(7, new int[][]{ {0, 1}, {2, 3}, {3, 4}, {5, 6} }, false);             // a forest
        verify(3, new int[][]{ {0, 1}, {2, 2} }, true);                              // self-loop

        // a 500-vertex path is acyclic; one extra edge between its ends closes a cycle
        int n = 500;
        int[][] path = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) path[i] = new int[]{i, i + 1};
        verify(n, path, false);
        int[][] ring = Arrays.copyOf(path, n);
        ring[n - 1] = new int[]{n - 1, 0};
        verify(n, ring, true);

        System.out.println("OK P2817_CycleDetectionInUndirectedGraphBfs");
    }
}
