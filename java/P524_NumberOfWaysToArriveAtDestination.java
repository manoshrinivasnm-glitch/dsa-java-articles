import java.util.*;

/** TUF 524 - Number of ways to arrive at destination. Undirected roads {u, v, time}; count shortest 0 -> n-1 routes modulo 1e9+7. */
public class P524_NumberOfWaysToArriveAtDestination {

    static final int MOD = 1_000_000_007;

    /** Approach 1: enumerate every simple path from 0 to n - 1, keeping the best time and how often it occurs. Exponential time. */
    static int bruteForce(int n, int[][] roads) {
        List<List<int[]>> adj = buildGraph(n, roads);    // adj.get(u) lists {v, time} for each road
        long[] best = {Long.MAX_VALUE, 0};               // {shortest time so far, routes with that time}
        boolean[] onPath = new boolean[n];               // nodes on the current path, never revisited
        onPath[0] = true;                                // every path starts at node 0
        explore(adj, 0, n - 1, 0, onPath, best);         // try every simple path to n - 1
        return (int) (best[1] % MOD);                    // number of fastest routes, mod 1e9+7
    }

    static void explore(List<List<int[]>> adj, int u, int target, long time, boolean[] onPath, long[] best) {
        if (time > best[0]) return;                      // already slower than a known route
        if (u == target) {                               // reached the destination
            if (time < best[0]) {                        // strictly faster: a new best time
                best[0] = time;
                best[1] = 1;                             // this route is the only one so far
            } else {
                best[1]++;                               // ties the best time: one more route
            }
            return;
        }
        for (int[] e : adj.get(u)) {                     // try every road out of u
            int v = e[0];
            if (onPath[v]) continue;                     // positive times: a shortest route never repeats a node
            onPath[v] = true;                            // put v on the path
            explore(adj, v, target, time + e[1], onPath, best);   // walk the road and keep exploring
            onPath[v] = false;                           // backtrack: free v for other paths
        }
    }

    /** Approach 2: Dijkstra for distances, then count paths on the shortest-path DAG in order of distance. O(E log E) time. */
    static int twoPass(int n, int[][] roads) {
        List<List<int[]>> adj = buildGraph(n, roads);    // adj.get(u) lists {v, time} for each road
        long[] dist = shortestTimes(adj, 0);             // dist[v] = shortest time from 0 to v
        Integer[] order = new Integer[n];                // node ids, to be visited by distance
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Long.compare(dist[a], dist[b]));   // nearest node first
        long[] ways = new long[n];                       // ways[v] = number of shortest routes 0 -> v
        ways[0] = 1;                                     // one way to stand at the start
        for (int u : order) {                            // u's predecessors are closer, so already counted
            if (dist[u] == Long.MAX_VALUE) break;        // the rest cannot be reached
            for (int[] e : adj.get(u)) {
                int v = e[0];
                // u -> v lies on a shortest route: extend u's routes
                if (dist[u] + e[1] == dist[v]) ways[v] = (ways[v] + ways[u]) % MOD;
            }
        }
        return (int) ways[n - 1];                        // shortest routes to the destination
    }

    static long[] shortestTimes(List<List<int[]>> adj, int src) {
        long[] dist = new long[adj.size()];              // dist[v] = shortest known time to v
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[src] = 0;                                   // the source costs nothing
        // min-heap of {time, node}, closest first
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.offer(new long[]{0, src});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();                      // earliest pending arrival
            long d = cur[0];
            int u = (int) cur[1];
            if (d > dist[u]) continue;                   // stale entry, u already settled
            for (int[] e : adj.get(u)) {                 // try every road out of u
                if (d + e[1] < dist[e[0]]) {             // found a faster way to e[0]
                    dist[e[0]] = d + e[1];
                    pq.offer(new long[]{dist[e[0]], e[0]});   // queue it with the new time
                }
            }
        }
        return dist;
    }

    /** Approach 3: one Dijkstra that carries a ways[] array next to dist[]. O(E log E) time, O(n + E) space. */
    static int optimal(int n, int[][] roads) {
        List<List<int[]>> adj = buildGraph(n, roads);    // adj.get(u) lists {v, time} for each road
        long[] dist = new long[n];                       // dist[v] = shortest known time to v
        long[] ways = new long[n];                       // ways[v] = number of routes achieving dist[v]
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[0] = 0;                                     // the start costs nothing
        ways[0] = 1;                                     // one way to stand at the start
        // min-heap: closest node comes out first
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.offer(new long[]{0, 0});                      // {time, node}
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();                      // earliest pending arrival
            long d = cur[0];
            int u = (int) cur[1];
            if (d > dist[u]) continue;                   // stale entry
            for (int[] e : adj.get(u)) {
                int v = e[0];
                long nd = d + e[1];                      // time to v through u
                if (nd < dist[v]) {                      // strictly faster: earlier routes to v are not shortest
                    dist[v] = nd;
                    ways[v] = ways[u];                   // v's shortest routes are exactly u's routes
                    pq.offer(new long[]{nd, v});         // queue v with its new time
                } else if (nd == dist[v]) {              // equally fast: every shortest route to u extends to v
                    ways[v] = (ways[v] + ways[u]) % MOD;   // add u's routes to v's count
                }
            }
        }
        return (int) ways[n - 1];                        // shortest routes to the destination
    }

    static List<List<int[]>> buildGraph(int n, int[][] roads) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] r : roads) {
            adj.get(r[0]).add(new int[]{r[1], r[2]});
            adj.get(r[1]).add(new int[]{r[0], r[2]});
        }
        return adj;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] roads, int expected) {
        int a = bruteForce(n, roads), b = twoPass(n, roads), c = optimal(n, roads);
        check(a == expected && b == expected && c == expected,
                "n " + n + ": expected " + expected + " got " + a + " " + b + " " + c);
    }

    public static void main(String[] args) {
        int[][] r1 = {{0, 6, 7}, {0, 1, 2}, {1, 2, 3}, {1, 3, 3}, {6, 3, 3}, {3, 5, 1}, {6, 5, 1}, {2, 5, 1}, {0, 4, 5}, {4, 6, 2}};
        verify(7, r1, 4);
        verify(2, new int[][]{{1, 0, 10}}, 1);
        verify(1, new int[0][], 1);                      // start is the destination: one empty route
        verify(4, new int[][]{{0, 1, 1}, {0, 2, 1}, {1, 3, 1}, {2, 3, 1}}, 2);    // diamond
        verify(4, new int[][]{{0, 1, 1}, {0, 2, 1}, {1, 3, 1}, {2, 3, 2}}, 1);    // only one branch is shortest
        int big = 1_000_000_000;                          // 3 * 10^9 overflows int; with long the direct road wins
        verify(4, new int[][]{{0, 1, big}, {1, 2, big}, {2, 3, big}, {0, 3, big}}, 1);

        // 40 diamonds in a row: 2^40 shortest routes, reduced modulo 1e9+7 (too many for brute force)
        int d = 40, n = 3 * d + 1;
        List<int[]> chain = new ArrayList<>();
        for (int i = 0; i < d; i++) {
            int a = 3 * i, top = a + 1, bottom = a + 2, b = a + 3;
            chain.add(new int[]{a, top, 1});
            chain.add(new int[]{a, bottom, 1});
            chain.add(new int[]{top, b, 1});
            chain.add(new int[]{bottom, b, 1});
        }
        long expected = 1;
        for (int i = 0; i < d; i++) expected = expected * 2 % MOD;
        int[][] chainRoads = chain.toArray(new int[0][]);
        check(twoPass(n, chainRoads) == expected && optimal(n, chainRoads) == expected, "40 diamonds");

        Random rnd = new Random(524);                    // seeded cross-check on small connected graphs
        for (int t = 0; t < 300; t++) {
            int m = 2 + rnd.nextInt(6);
            List<int[]> list = new ArrayList<>();
            boolean[][] used = new boolean[m][m];
            for (int v = 1; v < m; v++) {                // a random spanning tree keeps the graph connected
                int u = rnd.nextInt(v);
                used[u][v] = true;
                list.add(new int[]{u, v, 1 + rnd.nextInt(3)});
            }
            for (int u = 0; u < m; u++)
                for (int v = u + 1; v < m; v++)
                    if (!used[u][v] && rnd.nextInt(3) == 0) list.add(new int[]{u, v, 1 + rnd.nextInt(3)});
            int[][] roads = list.toArray(new int[0][]);
            verify(m, roads, bruteForce(m, roads));
        }
        System.out.println("OK P524_NumberOfWaysToArriveAtDestination");
    }
}
