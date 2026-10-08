import java.util.*;

/** TUF 2826 - Bellman Ford Algorithm. Directed edges {u, v, w} (w may be negative). Distances from src, 10^8 if unreachable, {-1} on a negative cycle. */
public class P2826_BellmanFordAlgorithm {

    static final int INF = 100_000_000;                  // the judge's "unreachable" value (10^8)

    /** Approach 1: classic Bellman-Ford, V - 1 full rounds and one extra round to detect a negative cycle. O(V * E) time, O(V) space. */
    static int[] bellmanFord(int V, int[][] edges, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 1; round <= V - 1; round++) {
            for (int[] e : edges) {
                int u = e[0], v = e[1], w = e[2];
                if (dist[u] != INF && dist[u] + w < dist[v]) dist[v] = dist[u] + w;
            }
        }
        for (int[] e : edges) {                          // round V: any improvement means a negative cycle
            int u = e[0], v = e[1], w = e[2];
            if (dist[u] != INF && dist[u] + w < dist[v]) return new int[]{-1};
        }
        return dist;
    }

    /** Approach 2: stop as soon as a round changes nothing; still changing in round V means a negative cycle. Same worst case, often far faster. */
    static int[] bellmanFordEarlyExit(int V, int[][] edges, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 1; round <= V; round++) {
            boolean changed = false;
            for (int[] e : edges) {
                int u = e[0], v = e[1], w = e[2];
                if (dist[u] != INF && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    changed = true;
                }
            }
            if (!changed) return dist;                   // a quiet round: every distance is final
        }
        return new int[]{-1};                            // V rounds and still improving
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int V, int[][] edges, int src, int[] expected) {
        int[] a = bellmanFord(V, edges, src), b = bellmanFordEarlyExit(V, edges, src);
        check(Arrays.equals(a, expected) && Arrays.equals(b, expected),
                "expected " + Arrays.toString(expected) + " got " + Arrays.toString(a) + " " + Arrays.toString(b));
    }

    public static void main(String[] args) {
        verify(2, new int[][]{{0, 1, 9}}, 0, new int[]{0, 9});
        verify(3, new int[][]{{0, 1, 5}, {1, 0, 3}, {1, 2, -1}, {2, 0, 1}}, 2, new int[]{1, 6, 0});
        verify(4, new int[][]{{0, 1, 4}, {0, 2, 5}, {2, 1, -3}, {1, 3, 2}}, 0, new int[]{0, 2, 5, 4});   // negative edge, no cycle
        verify(3, new int[][]{{0, 1, 1}, {1, 2, -1}, {2, 0, -1}}, 0, new int[]{-1});                      // cycle of weight -1
        verify(4, new int[][]{{0, 1, 4}, {2, 3, 1}}, 0, new int[]{0, 4, INF, INF});                        // unreachable vertices
        verify(4, new int[][]{{0, 1, 2}, {2, 3, -5}, {3, 2, 1}}, 0, new int[]{0, 2, INF, INF});            // negative cycle not reachable from src
        verify(1, new int[0][], 0, new int[]{0});                                                          // single vertex
        verify(1, new int[][]{{0, 0, -2}}, 0, new int[]{-1});                                              // negative self-loop
        verify(5, new int[][]{{3, 4, 1}, {2, 3, 1}, {1, 2, 1}, {0, 1, 1}}, 0, new int[]{0, 1, 2, 3, 4});   // worst order: needs V - 1 rounds

        Random rnd = new Random(2826);                   // seeded cross-check, including graphs with negative cycles
        for (int t = 0; t < 300; t++) {
            int V = 1 + rnd.nextInt(6);
            List<int[]> list = new ArrayList<>();
            for (int u = 0; u < V; u++)
                for (int v = 0; v < V; v++)
                    if (u != v && rnd.nextInt(3) == 0) list.add(new int[]{u, v, rnd.nextInt(12) - 3});
            int[][] edges = list.toArray(new int[0][]);
            int src = rnd.nextInt(V);
            verify(V, edges, src, bellmanFord(V, edges, src));
        }
        System.out.println("OK P2826_BellmanFordAlgorithm");
    }
}
