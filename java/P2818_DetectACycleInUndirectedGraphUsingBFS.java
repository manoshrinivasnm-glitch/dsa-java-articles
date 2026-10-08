import java.util.*;

/** TUF 2818 - Detect a cycle in an undirected graph using BFS. adj.get(u) lists the neighbours of u. */
public class P2818_DetectACycleInUndirectedGraphUsingBFS {

    /** Approach 1: an edge u-v is on a cycle iff v is still reachable from u without it. O(E * (V + E)) time, O(V) space. */
    static boolean bruteForce(int V, List<List<Integer>> adj) {
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {
                if (u < v && reachableWithout(V, adj, u, v)) return true;
            }
        }
        return false;
    }

    /** BFS from u to v that is not allowed to walk along the edge u-v itself. */
    static boolean reachableWithout(int V, List<List<Integer>> adj, int u, int v) {
        boolean[] seen = new boolean[V];
        Deque<Integer> queue = new ArrayDeque<>();
        seen[u] = true;
        queue.add(u);
        while (!queue.isEmpty()) {
            int x = queue.poll();
            for (int y : adj.get(x)) {
                if ((x == u && y == v) || (x == v && y == u)) continue;   // the removed edge
                if (y == v) return true;
                if (!seen[y]) {
                    seen[y] = true;
                    queue.add(y);
                }
            }
        }
        return false;
    }

    /** Approach 2: BFS remembering each vertex's parent; a visited neighbour that is not the parent closes a cycle. O(V + E) time, O(V) space. */
    static boolean optimal(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];
        int[] parent = new int[V];
        for (int start = 0; start < V; start++) {
            if (visited[start]) continue;                 // new component
            visited[start] = true;
            parent[start] = -1;
            Deque<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                int x = queue.poll();
                for (int y : adj.get(x)) {
                    if (!visited[y]) {
                        visited[y] = true;
                        parent[y] = x;
                        queue.add(y);
                    } else if (y != parent[x]) {
                        return true;                      // reached y a second way
                    }
                }
            }
        }
        return false;
    }

    /** Approach 3: BFS each component and count it; a tree on k vertices has exactly k - 1 edges. O(V + E) time, O(V) space. */
    static boolean edgeCount(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];
        for (int start = 0; start < V; start++) {
            if (visited[start]) continue;
            long vertices = 0, degreeSum = 0;
            visited[start] = true;
            Deque<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                int x = queue.poll();
                vertices++;
                degreeSum += adj.get(x).size();
                for (int y : adj.get(x)) {
                    if (!visited[y]) {
                        visited[y] = true;
                        queue.add(y);
                    }
                }
            }
            if (degreeSum / 2 > vertices - 1) return true; // every edge is counted from both ends
        }
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> graph(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    static void verify(int V, int[][] edges, boolean expected) {
        List<List<Integer>> adj = graph(V, edges);
        check(bruteForce(V, adj) == expected, "bruteForce " + Arrays.deepToString(edges));
        check(optimal(V, adj) == expected, "optimal " + Arrays.deepToString(edges));
        check(edgeCount(V, adj) == expected, "edgeCount " + Arrays.deepToString(edges));
    }

    public static void main(String[] args) {
        verify(5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 1}}, true);   // 1-2-3-4-1
        verify(5, new int[][]{{0, 1}, {1, 2}, {1, 3}, {3, 4}}, false);          // a tree
        verify(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}, true);                   // triangle
        verify(1, new int[][]{}, false);                                        // single vertex
        verify(4, new int[][]{}, false);                                        // no edges at all
        verify(7, new int[][]{{0, 1}, {2, 3}, {4, 5}, {5, 6}, {6, 4}}, true);   // cycle only in the last component
        verify(6, new int[][]{{0, 1}, {1, 2}, {3, 4}, {4, 5}}, false);          // forest of two paths
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}}, true);           // even cycle 0-1-3-2-0
        System.out.println("OK P2818_DetectACycleInUndirectedGraphUsingBFS");
    }
}
