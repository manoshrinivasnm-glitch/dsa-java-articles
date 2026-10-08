import java.util.*;

/** TUF 508 - Shortest path in undirected graph with unit weights. Return the fewest edges from src to every node, or -1 if a node is unreachable. */
public class P508_ShortestPathInUndirectedGraphWithUnitWei {

    /** Approach 1: relax every edge in both directions, round after round, until nothing improves. O(V * E) time, O(V) space. */
    static int[] bruteForce(int n, int[][] edges, int src) {
        final int INF = Integer.MAX_VALUE;
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 1; round < n; round++) {             // a shortest path uses at most n - 1 edges
            boolean changed = false;
            for (int[] e : edges) {
                for (int d = 0; d < 2; d++) {                 // undirected: try u -> v and v -> u
                    int u = e[d], v = e[1 - d];
                    if (dist[u] != INF && dist[u] + 1 < dist[v]) {
                        dist[v] = dist[u] + 1;
                        changed = true;
                    }
                }
            }
            if (!changed) break;
        }
        for (int i = 0; i < n; i++) if (dist[i] == INF) dist[i] = -1;
        return dist;
    }

    /** Approach 2: breadth-first search; the first time a node is reached is along a shortest path. O(V + E) time, O(V + E) space. */
    static int[] optimal(int n, int[][] edges, int src) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        int[] dist = new int[n];
        Arrays.fill(dist, -1);                                // -1 also means "not discovered yet"
        dist[src] = 0;
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(src);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adj.get(u)) {
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    queue.add(v);
                }
            }
        }
        return dist;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] edges, int src, int[] expected) {
        String name = "n=" + n + " src=" + src + " edges=" + Arrays.deepToString(edges);
        check(Arrays.equals(bruteForce(n, edges, src), expected), "bruteForce " + name + " got " + Arrays.toString(bruteForce(n, edges, src)));
        check(Arrays.equals(optimal(n, edges, src), expected), "optimal " + name + " got " + Arrays.toString(optimal(n, edges, src)));
    }

    public static void main(String[] args) {
        int[][] tuf = {{0, 1}, {0, 3}, {3, 4}, {4, 5}, {5, 6}, {1, 2}, {2, 6}, {6, 7}, {7, 8}, {6, 8}};
        verify(9, tuf, 0, new int[]{0, 1, 2, 1, 2, 3, 3, 4, 4});
        verify(9, tuf, 6, new int[]{3, 2, 1, 3, 2, 1, 0, 1, 1});                          // same graph, different source
        verify(5, new int[][]{{0, 1}, {1, 2}, {3, 4}}, 0, new int[]{0, 1, 2, -1, -1});      // nodes 3, 4 unreachable
        verify(1, new int[][]{}, 0, new int[]{0});                                         // single node
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}}, 0, new int[]{0, 1, 2, 1});   // a cycle: go either way round
        verify(3, new int[][]{{0, 1}, {0, 1}, {1, 1}, {1, 2}}, 2, new int[]{2, 1, 0});      // parallel edge and self-loop
        System.out.println("OK P508_ShortestPathInUndirectedGraphWithUnitWei");
    }
}
