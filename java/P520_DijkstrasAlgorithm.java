import java.util.*;

/** TUF 520 - Dijkstra's algorithm. Undirected graph, edges {u, v, w} with w >= 0. Shortest distance from src to every vertex, -1 if unreachable. */
public class P520_DijkstrasAlgorithm {

    /** Adjacency list of {neighbour, weight}; each undirected edge is stored in both directions. */
    static List<List<int[]>> buildAdj(int V, int[][] edges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        return adj;
    }

    /** Turns the "infinity" marker into -1 for vertices that were never reached. */
    static int[] finish(int[] dist) {
        for (int i = 0; i < dist.length; i++) if (dist[i] == Integer.MAX_VALUE) dist[i] = -1;
        return dist;
    }

    /** Approach 1: walk every simple path out of src with DFS and keep the cheapest arrival at each vertex. Exponential time, O(V) extra space. */
    static int[] bruteForce(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] best = new int[V];
        Arrays.fill(best, Integer.MAX_VALUE);
        boolean[] onPath = new boolean[V];
        explore(src, 0, adj, onPath, best);
        return finish(best);
    }

    static void explore(int u, int d, List<List<int[]>> adj, boolean[] onPath, int[] best) {
        best[u] = Math.min(best[u], d);
        onPath[u] = true;
        for (int[] e : adj.get(u)) {
            if (!onPath[e[0]]) explore(e[0], d + e[1], adj, onPath, best);
        }
        onPath[u] = false;                                     // backtrack so other paths may pass through u
    }

    /** Approach 2: Dijkstra with a linear scan for the closest unsettled vertex. O(V^2 + E) time, O(V + E) space. */
    static int[] better(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        boolean[] settled = new boolean[V];
        for (int iter = 0; iter < V; iter++) {
            int u = -1;
            for (int i = 0; i < V; i++) {
                if (!settled[i] && dist[i] != Integer.MAX_VALUE && (u == -1 || dist[i] < dist[u])) u = i;
            }
            if (u == -1) break;                                // everything left is unreachable
            settled[u] = true;                                 // dist[u] can no longer improve
            for (int[] e : adj.get(u)) {
                int v = e[0];
                if (!settled[v] && dist[u] + e[1] < dist[v]) dist[v] = dist[u] + e[1];
            }
        }
        return finish(dist);
    }

    /** Approach 3: Dijkstra with a min-heap of {distance, vertex} and lazy deletion of stale entries. O((V + E) log V) time, O(V + E) space. */
    static int[] optimal(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));   // {distance, vertex}
        pq.add(new int[]{0, src});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0], u = top[1];
            if (d > dist[u]) continue;                         // stale entry: u was settled with a smaller distance
            for (int[] e : adj.get(u)) {
                int v = e[0], nd = d + e[1];
                if (nd < dist[v]) {
                    dist[v] = nd;
                    pq.add(new int[]{nd, v});
                }
            }
        }
        return finish(dist);
    }

    /** Approach 4: Dijkstra with a TreeSet, erasing the old entry when a distance improves. O((V + E) log V) time, O(V + E) space. */
    static int[] optimalSet(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        TreeSet<int[]> set = new TreeSet<>((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        set.add(new int[]{0, src});
        while (!set.isEmpty()) {
            int[] top = set.pollFirst();
            int d = top[0], u = top[1];
            for (int[] e : adj.get(u)) {
                int v = e[0], nd = d + e[1];
                if (nd < dist[v]) {
                    if (dist[v] != Integer.MAX_VALUE) set.remove(new int[]{dist[v], v});   // drop the outdated entry
                    dist[v] = nd;
                    set.add(new int[]{nd, v});
                }
            }
        }
        return finish(dist);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int V, int[][] edges, int src, int[] expected) {
        check(Arrays.equals(bruteForce(V, edges, src), expected), "bruteForce " + Arrays.toString(bruteForce(V, edges, src)));
        check(Arrays.equals(better(V, edges, src), expected), "better " + Arrays.toString(better(V, edges, src)));
        check(Arrays.equals(optimal(V, edges, src), expected), "optimal " + Arrays.toString(optimal(V, edges, src)));
        check(Arrays.equals(optimalSet(V, edges, src), expected), "optimalSet " + Arrays.toString(optimalSet(V, edges, src)));
    }

    public static void main(String[] args) {
        verify(3, new int[][]{{0, 1, 1}, {0, 2, 6}, {1, 2, 3}}, 2, new int[]{4, 3, 0});      // detour beats the direct edge
        verify(5, new int[][]{{0, 1, 2}, {0, 2, 4}, {1, 2, 1}, {1, 3, 7}, {2, 4, 3}, {3, 4, 1}}, 0,
                new int[]{0, 2, 3, 7, 6});
        verify(1, new int[][]{}, 0, new int[]{0});                                           // single vertex
        verify(4, new int[][]{{0, 1, 5}, {2, 3, 1}}, 0, new int[]{0, 5, -1, -1});            // unreachable vertices
        verify(3, new int[][]{{0, 1, 0}, {1, 2, 0}}, 2, new int[]{0, 0, 0});                 // zero-weight edges
        verify(2, new int[][]{{0, 1, 10}, {0, 1, 3}}, 1, new int[]{3, 0});                   // parallel edges

        // larger seeded graph: the three Dijkstra variants must agree with each other
        Random rnd = new Random(520);
        int V = 400;
        int[][] edges = new int[2000][];
        for (int i = 0; i < edges.length; i++) edges[i] = new int[]{rnd.nextInt(V), rnd.nextInt(V), rnd.nextInt(1000)};
        int[] ref = better(V, edges, 7);
        check(Arrays.equals(ref, optimal(V, edges, 7)), "optimal differs on random graph");
        check(Arrays.equals(ref, optimalSet(V, edges, 7)), "optimalSet differs on random graph");
        System.out.println("OK P520_DijkstrasAlgorithm");
    }
}
