import java.util.*;

/** TUF 518 - Bellman-Ford. Directed edges {u, v, w}, w may be negative. Distances from src (INF = 10^8 if unreachable), or {-1} if a negative cycle is reachable from src. */
public class P518_BellmanFordAlgorithm {

    static final int INF = 100_000_000;                        // "unreachable" marker used by the original problem

    /** Approach 1: textbook Bellman-Ford. V - 1 rounds that relax every edge, then one extra round to detect a negative cycle. O(V * E) time, O(V) space. */
    static int[] bellmanFord(int V, int[][] edges, int src) {
        int[] dist = new int[V];                               // dist[v] = shortest distance found so far
        Arrays.fill(dist, INF);                                // nothing reached yet
        dist[src] = 0;                                         // the source costs nothing
        for (int round = 1; round <= V - 1; round++) {         // a shortest path uses at most V - 1 edges
            for (int[] e : edges) {                            // relax every edge once per round
                int u = e[0], v = e[1], w = e[2];
                // u is reached and the edge u -> v gives a shorter route
                if (dist[u] != INF && dist[u] + w < dist[v]) dist[v] = dist[u] + w;
            }
        }
        for (int[] e : edges) {                                // extra round: any gain means a negative cycle
            if (dist[e[0]] != INF && dist[e[0]] + e[2] < dist[e[1]]) return new int[]{-1};   // still improvable: negative cycle
        }
        return dist;
    }

    /** Approach 2: same rounds, but stop as soon as a round changes nothing. O(V * E) worst case, often far fewer rounds. O(V) space. */
    static int[] earlyExit(int V, int[][] edges, int src) {
        int[] dist = new int[V];                               // dist[v] = shortest distance found so far
        Arrays.fill(dist, INF);                                // nothing reached yet
        dist[src] = 0;                                         // the source costs nothing
        for (int round = 1; round <= V; round++) {             // round V only detects a negative cycle
            boolean changed = false;                           // did this round improve any distance?
            for (int[] e : edges) {
                int u = e[0], v = e[1], w = e[2];
                if (dist[u] != INF && dist[u] + w < dist[v]) {   // reached u, and u -> v gives a shorter route
                    dist[v] = dist[u] + w;
                    changed = true;
                }
            }
            if (!changed) return dist;                         // stable: every distance is final
            if (round == V) return new int[]{-1};              // a V-th round still improved something
        }
        return dist;
    }

    /** Approach 3: queue-based Bellman-Ford (SPFA). Only vertices whose distance just dropped are re-examined. O(V * E) worst case, O(V + E) space. */
    static int[] spfa(int V, int[][] edges, int src) {
        List<List<int[]>> adj = new ArrayList<>();             // adj.get(u) = list of {v, weight}
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(new int[]{e[1], e[2]});   // directed edges only
        int[] dist = new int[V], len = new int[V];             // len[v]: number of edges on the walk that gave dist[v]
        Arrays.fill(dist, INF);
        dist[src] = 0;
        boolean[] inQueue = new boolean[V];                    // avoid queueing the same vertex twice
        Deque<Integer> queue = new ArrayDeque<>();             // vertices whose distance just dropped
        queue.add(src);
        inQueue[src] = true;
        while (!queue.isEmpty()) {                             // re-examine edges out of improved vertices
            int u = queue.poll();
            inQueue[u] = false;                                // u may be queued again later
            for (int[] e : adj.get(u)) {
                int v = e[0];
                if (dist[u] + e[1] < dist[v]) {                // a shorter route to v through u
                    dist[v] = dist[u] + e[1];
                    len[v] = len[u] + 1;                       // one more edge than the walk to u
                    if (len[v] >= V) return new int[]{-1};     // a shortest simple path has at most V - 1 edges
                    if (!inQueue[v]) {                         // v must re-relax its own edges
                        queue.add(v);
                        inQueue[v] = true;
                    }
                }
            }
        }
        return dist;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int V, int[][] edges, int src, int[] expected) {
        check(Arrays.equals(bellmanFord(V, edges, src), expected), "bellmanFord " + Arrays.toString(bellmanFord(V, edges, src)));
        check(Arrays.equals(earlyExit(V, edges, src), expected), "earlyExit " + Arrays.toString(earlyExit(V, edges, src)));
        check(Arrays.equals(spfa(V, edges, src), expected), "spfa " + Arrays.toString(spfa(V, edges, src)));
    }

    public static void main(String[] args) {
        verify(3, new int[][]{{0, 1, 5}, {1, 0, 3}, {1, 2, -1}, {2, 0, 1}}, 2, new int[]{1, 6, 0});
        verify(2, new int[][]{{0, 1, 9}}, 0, new int[]{0, 9});
        verify(5, new int[][]{{0, 1, 4}, {0, 2, 5}, {1, 2, -3}, {2, 3, 4}, {3, 4, -2}, {1, 4, 10}}, 0,
                new int[]{0, 4, 1, 5, 3});                                                    // negative edges, no cycle
        verify(3, new int[][]{{0, 1, 1}, {1, 2, -1}, {2, 0, -1}}, 0, new int[]{-1});          // negative cycle
        verify(4, new int[][]{{0, 1, 2}, {2, 3, -1}}, 0, new int[]{0, 2, INF, INF});          // unreachable vertices
        verify(4, new int[][]{{0, 1, 3}, {2, 3, -2}, {3, 2, 1}}, 0, new int[]{0, 3, INF, INF}); // negative cycle not reachable
        verify(1, new int[][]{}, 0, new int[]{0});                                            // single vertex
        verify(1, new int[][]{{0, 0, -1}}, 0, new int[]{-1});                                 // negative self loop
        verify(3, new int[][]{{0, 1, 2}, {0, 2, 5}, {2, 1, -4}}, 0, new int[]{0, 1, 5});      // Dijkstra would answer 2 for vertex 1
        verify(3, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 1, -1}}, 0, new int[]{0, 1, 2});      // zero-weight cycle is fine

        // seeded random graphs: all three must agree, including on negative-cycle verdicts
        Random rnd = new Random(518);
        for (int t = 0; t < 300; t++) {
            int V = 1 + rnd.nextInt(8), E = rnd.nextInt(15);
            int[][] edges = new int[E][];
            for (int i = 0; i < E; i++) edges[i] = new int[]{rnd.nextInt(V), rnd.nextInt(V), rnd.nextInt(21) - 5};
            int[] ref = bellmanFord(V, edges, 0);
            check(Arrays.equals(ref, earlyExit(V, edges, 0)), "earlyExit differs on random test " + t);
            check(Arrays.equals(ref, spfa(V, edges, 0)), "spfa differs on random test " + t);
        }
        System.out.println("OK P518_BellmanFordAlgorithm");
    }
}
