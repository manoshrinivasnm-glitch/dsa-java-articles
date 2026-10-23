import java.util.*;

/** TUF 764 - Network Delay Time. Nodes 1..n, directed times {u, v, w}; time for a signal from k to reach every node, or -1. */
public class P764_NetworkDelayTime {

    /** Approach 1: Bellman-Ford, up to n - 1 rounds of relaxing every edge. O(n * E) time, O(n) space. */
    static int bellmanFord(int[][] times, int n, int k) {
        int[] dist = new int[n + 1];                     // dist[v] = fastest known arrival time at v
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;                                     // the signal starts at k at time 0
        for (int round = 1; round < n; round++) {        // n - 1 rounds are enough for any shortest path
            boolean changed = false;                     // did this round improve anything?
            for (int[] t : times) {                      // try every edge u -> v
                int u = t[0], v = t[1], w = t[2];
                // u is reached and going through u is faster
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;               // shorter arrival time at v
                    changed = true;
                }
            }
            if (!changed) break;                         // nothing moved, so nothing will move later
        }
        return slowest(dist, n);                         // time the last node hears the signal
    }

    /** Approach 2: Dijkstra with a min-heap of {time, node}. O(E log E) time, O(n + E) space. */
    static int dijkstra(int[][] times, int n, int k) {
        List<List<int[]>> adj = new ArrayList<>();       // adj.get(u) lists {v, w} for each edge out of u
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] t : times) adj.get(t[0]).add(new int[]{t[1], t[2]});
        int[] dist = new int[n + 1];                     // dist[v] = fastest known arrival time at v
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;                                     // the signal starts at k at time 0
        // min-heap: the earliest arrival comes out first
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, k});                       // {time, node}
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();                       // earliest pending arrival
            int d = cur[0], u = cur[1];
            if (d > dist[u]) continue;                   // stale entry, u was settled with a smaller time
            for (int[] e : adj.get(u)) {                 // try every edge out of u
                int v = e[0], w = e[1];
                if (d + w < dist[v]) {                   // reaching v through u is faster
                    dist[v] = d + w;
                    pq.offer(new int[]{dist[v], v});     // push v with its new, smaller time
                }
            }
        }
        return slowest(dist, n);                         // time the last node hears the signal
    }

    /** The answer is the largest shortest distance, or -1 when some node never hears the signal. */
    static int slowest(int[] dist, int n) {
        int worst = 0;                                   // largest arrival time seen so far
        for (int v = 1; v <= n; v++) {                   // check every node 1..n
            if (dist[v] == Integer.MAX_VALUE) return -1;   // some node is never reached
            worst = Math.max(worst, dist[v]);            // the network waits for the slowest node
        }
        return worst;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] times, int n, int k, int expected) {
        int a = bellmanFord(times, n, k), b = dijkstra(times, n, k);
        check(a == expected && b == expected, "n " + n + " k " + k + ": expected " + expected + " got " + a + " " + b);
    }

    public static void main(String[] args) {
        verify(new int[][]{{2, 1, 1}, {2, 3, 1}, {3, 4, 1}}, 4, 2, 2);
        verify(new int[][]{{1, 2, 1}}, 2, 1, 1);
        verify(new int[][]{{1, 2, 1}}, 2, 2, -1);        // edges are directed: node 1 never hears from 2
        verify(new int[0][], 1, 1, 0);                   // single node: already informed at time 0
        verify(new int[][]{{1, 2, 10}, {1, 3, 1}, {3, 2, 2}}, 3, 1, 3);   // detour beats the direct edge
        verify(new int[][]{{1, 2, 0}, {2, 3, 0}}, 3, 1, 0);               // zero-weight edges
        verify(new int[][]{{1, 2, 5}, {1, 2, 2}, {2, 1, 1}}, 2, 1, 2);    // parallel edges: the cheaper one counts

        Random rnd = new Random(764);                    // seeded cross-check
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(7);
            List<int[]> list = new ArrayList<>();
            for (int u = 1; u <= n; u++)
                for (int v = 1; v <= n; v++)
                    if (u != v && rnd.nextInt(3) == 0) list.add(new int[]{u, v, rnd.nextInt(101)});
            int[][] times = list.toArray(new int[0][]);
            int k = 1 + rnd.nextInt(n);
            verify(times, n, k, bellmanFord(times, n, k));
        }
        System.out.println("OK P764_NetworkDelayTime");
    }
}
