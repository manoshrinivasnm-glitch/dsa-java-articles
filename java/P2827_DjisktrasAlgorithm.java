import java.util.*;

/** TUF 2827 - Dijkstra's Algorithm. Undirected graph with edges {u, v, w}, w >= 0. Return the shortest distance from src to every node, -1 if unreachable. */
public class P2827_DjisktrasAlgorithm {

    /** Adjacency list of {neighbour, weight} pairs; each undirected edge is stored in both directions. */
    static List<List<int[]>> buildAdj(int V, int[][] edges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        return adj;
    }

    /** Replaces the "infinity" marker with -1 for unreachable nodes. */
    static int[] finish(int[] dist) {
        for (int i = 0; i < dist.length; i++) if (dist[i] == Integer.MAX_VALUE) dist[i] = -1;
        return dist;
    }

    /** Approach 1: relax every edge (both directions) until a full round changes nothing. O(V * E) time, O(V) space. */
    static int[] bruteForce(int V, int[][] edges, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int[] e : edges) {
                for (int d = 0; d < 2; d++) {
                    int u = e[d], v = e[1 - d];
                    if (dist[u] != Integer.MAX_VALUE && dist[u] + e[2] < dist[v]) {
                        dist[v] = dist[u] + e[2];
                        changed = true;
                    }
                }
            }
        }
        return finish(dist);
    }

    /** Approach 2: classic Dijkstra with a linear scan for the closest unfinished node. O(V^2 + E) time, O(V + E) space. */
    static int[] dijkstraArray(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        boolean[] done = new boolean[V];
        for (int iter = 0; iter < V; iter++) {
            int u = -1;
            for (int i = 0; i < V; i++) {
                if (!done[i] && dist[i] != Integer.MAX_VALUE && (u == -1 || dist[i] < dist[u])) u = i;
            }
            if (u == -1) break;                               // every remaining node is unreachable
            done[u] = true;                                   // dist[u] is final from here on
            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (!done[v] && dist[u] + w < dist[v]) dist[v] = dist[u] + w;
            }
        }
        return finish(dist);
    }

    /** Approach 3: Dijkstra with a min-heap of {distance, node} and lazy deletion. O((V + E) log V) time, O(V + E) space. */
    static int[] optimal(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.add(new int[]{0, src});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0], u = top[1];
            if (d > dist[u]) continue;                        // stale entry: u was settled with a smaller distance
            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (d + w < dist[v]) {
                    dist[v] = d + w;
                    pq.add(new int[]{dist[v], v});
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
        String name = "V=" + V + " src=" + src + " edges=" + Arrays.deepToString(edges);
        check(Arrays.equals(bruteForce(V, edges, src), expected), "bruteForce " + name + " got " + Arrays.toString(bruteForce(V, edges, src)));
        check(Arrays.equals(dijkstraArray(V, edges, src), expected), "dijkstraArray " + name + " got " + Arrays.toString(dijkstraArray(V, edges, src)));
        check(Arrays.equals(optimal(V, edges, src), expected), "optimal " + name + " got " + Arrays.toString(optimal(V, edges, src)));
    }

    public static void main(String[] args) {
        verify(2, new int[][]{{0, 1, 9}}, 0, new int[]{0, 9});
        verify(3, new int[][]{{0, 1, 1}, {0, 2, 6}, {1, 2, 3}}, 2, new int[]{4, 3, 0});           // two hops beat one heavy edge
        int[][] g = {{0, 1, 4}, {0, 2, 4}, {1, 2, 2}, {2, 3, 3}, {2, 4, 1}, {2, 5, 6}, {3, 5, 2}, {4, 5, 3}};
        verify(6, g, 0, new int[]{0, 4, 4, 7, 5, 8});
        verify(1, new int[][]{}, 0, new int[]{0});                                               // single node
        verify(4, new int[][]{{0, 1, 5}, {2, 3, 1}}, 0, new int[]{0, 5, -1, -1});                 // disconnected
        verify(3, new int[][]{{0, 1, 0}, {1, 2, 0}, {0, 2, 1}}, 0, new int[]{0, 0, 0});           // zero-weight edges
        verify(2, new int[][]{{0, 1, 7}, {0, 1, 3}, {1, 0, 5}}, 1, new int[]{3, 0});              // parallel edges
        System.out.println("OK P2827_DjisktrasAlgorithm");
    }
}
