import java.util.*;

/** TUF 507 - Shortest path in DAG. edges[i] = {u, v, w} is a directed edge u -> v of weight w. Return the shortest distance from src to every node, -1 if unreachable. */
public class P507_ShortestPathInDAG {

    /** Approach 1: Bellman-Ford style; relax every edge up to n - 1 times. O(V * E) time, O(V) space. */
    static int[] bruteForce(int n, int[][] edges, int src) {
        final int INF = Integer.MAX_VALUE;
        int[] dist = new int[n];                              // dist[v] = shortest distance found so far
        Arrays.fill(dist, INF);
        dist[src] = 0;                                        // the source costs 0
        for (int round = 1; round < n; round++) {             // Bellman-Ford: n - 1 rounds are always enough
            boolean changed = false;
            for (int[] e : edges) {                           // relax every edge
                // going through e[0] gives a shorter route to e[1]
                if (dist[e[0]] != INF && dist[e[0]] + e[2] < dist[e[1]]) {
                    dist[e[1]] = dist[e[0]] + e[2];
                    changed = true;
                }
            }
            if (!changed) break;                              // no change this round: distances are final
        }
        for (int i = 0; i < n; i++) if (dist[i] == INF) dist[i] = -1;   // unreachable -> -1
        return dist;
    }

    /** Approach 2: topological order (Kahn's algorithm), then relax each node's edges once in that order. O(V + E) time, O(V + E) space. */
    static int[] optimal(int n, int[][] edges, int src) {
        List<List<int[]>> adj = new ArrayList<>();            // adj.get(u) = {v, weight} per edge u -> v
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        int[] indegree = new int[n];                          // indegree[v] = edges into v not yet released
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});         // directed edge u -> v with its weight
            indegree[e[1]]++;
        }
        int[] topo = new int[n];                              // used as the queue of Kahn's algorithm
        int head = 0, tail = 0;
        // start Kahn with every node that has no incoming edge
        for (int i = 0; i < n; i++) if (indegree[i] == 0) topo[tail++] = i;
        while (head < tail) {
            int u = topo[head++];                             // next node in topological order
            for (int[] e : adj.get(u)) {
                if (--indegree[e[0]] == 0) topo[tail++] = e[0];   // last incoming edge gone: v can follow
            }
        }
        final int INF = Integer.MAX_VALUE;
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int u : topo) {                                  // all edges into u were relaxed before we get here
            if (dist[u] == INF) continue;                     // u is not reachable from src
            for (int[] e : adj.get(u)) {
                dist[e[0]] = Math.min(dist[e[0]], dist[u] + e[1]);   // relax u -> e[0] with weight e[1]
            }
        }
        for (int i = 0; i < n; i++) if (dist[i] == INF) dist[i] = -1;   // unreachable -> -1
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
        int[][] g = {{0, 1, 2}, {0, 4, 1}, {4, 5, 4}, {4, 2, 2}, {1, 2, 3}, {2, 3, 6}, {5, 3, 1}};
        verify(6, g, 0, new int[]{0, 2, 3, 6, 1, 5});
        verify(4, new int[][]{{0, 1, 2}, {0, 2, 1}}, 0, new int[]{0, 2, 1, -1});          // node 3 unreachable
        verify(1, new int[][]{}, 0, new int[]{0});                                         // single node
        verify(6, g, 4, new int[]{-1, -1, 2, 5, 0, 4});                                    // source in the middle
        verify(3, new int[][]{{0, 1, 5}, {0, 2, 2}, {2, 1, -4}}, 0, new int[]{0, -2, 2});  // negative edge is fine in a DAG
        verify(5, new int[][]{{3, 0, 1}, {0, 1, 1}, {0, 2, 4}, {1, 2, 1}, {2, 4, 1}, {1, 4, 7}}, 0,
               new int[]{0, 1, 2, -1, 3});                                                 // node 3 precedes src in topo order
        System.out.println("OK P507_ShortestPathInDAG");
    }
}
