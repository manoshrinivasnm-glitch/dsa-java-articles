import java.util.*;

/** TUF 519 - Cheapest flight within K stops. Directed flights {from, to, price}; cheapest src -> dst using at most k intermediate stops, or -1. */
public class P519_CheapestFlightWithinKStops {

    /** Approach 1: DFS over every route that uses at most k + 1 flights. Exponential time, O(n + E) space. */
    static int bruteForce(int n, int[][] flights, int src, int dst, int k) {
        List<List<int[]>> adj = buildGraph(n, flights);   // flights out of each city as {to, price}
        long best = cheapestFrom(adj, src, dst, k + 1);   // k stops = at most k + 1 flights
        return best == Long.MAX_VALUE ? -1 : (int) best;   // MAX_VALUE means no route fits the limit
    }

    static long cheapestFrom(List<List<int[]>> adj, int node, int dst, int flightsLeft) {
        if (node == dst) return 0;                       // prices are positive, so going on never helps
        if (flightsLeft == 0) return Long.MAX_VALUE;     // no flights left and dst not reached
        long best = Long.MAX_VALUE;                      // cheapest cost from node to dst found so far
        for (int[] e : adj.get(node)) {                  // try every flight leaving this city
            long rest = cheapestFrom(adj, e[0], dst, flightsLeft - 1);   // cheapest finish after this flight
            if (rest != Long.MAX_VALUE) best = Math.min(best, e[1] + rest);   // ignore dead ends
        }
        return best;
    }

    /** Approach 2: BFS whose levels are the number of stops; relax when the price improves. Roughly O(k * E) time, O(n + E) space. */
    static int bfsByStops(int n, int[][] flights, int src, int dst, int k) {
        List<List<int[]>> adj = buildGraph(n, flights);   // flights out of each city as {to, price}
        int[] dist = new int[n];                         // dist[v] = cheapest price found so far to reach v
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;                                   // the start city costs nothing
        Deque<int[]> queue = new ArrayDeque<>();         // each BFS level uses one more flight
        queue.offer(new int[]{0, src, 0});               // {stops, node, cost}
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int stops = cur[0], node = cur[1], cost = cur[2];
            if (stops > k) continue;                     // one more flight would exceed k stops
            for (int[] e : adj.get(node)) {              // try every flight out of this city
                int next = e[0], price = e[1];
                if (cost + price < dist[next]) {         // only continue when this route is cheaper
                    dist[next] = cost + price;           // remember the cheaper price
                    queue.offer(new int[]{stops + 1, next, cost + price});   // fly on, one more flight used
                }
            }
        }
        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];   // -1 if dst was never reached
    }

    /** Approach 3: Bellman-Ford limited to k + 1 rounds, each round reading only the previous round. O(k * E) time, O(n) space. */
    static int bellmanFordKRounds(int n, int[][] flights, int src, int dst, int k) {
        int[] dist = new int[n];                         // dist[v] = cheapest price to v so far
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;                                   // the start city costs nothing
        for (int round = 0; round <= k; round++) {       // after round r, dist uses at most r + 1 flights
            int[] next = dist.clone();                   // copy, so a round only extends last round's routes
            for (int[] f : flights) {                    // try to extend every route by one flight
                int u = f[0], v = f[1], price = f[2];
                // relax u -> v using only prices from the previous round
                if (dist[u] != Integer.MAX_VALUE && dist[u] + price < next[v]) next[v] = dist[u] + price;
            }
            dist = next;                                 // commit this round's prices
        }
        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];   // -1 when dst needs more than k stops
    }

    static List<List<int[]>> buildGraph(int n, int[][] flights) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] f : flights) adj.get(f[0]).add(new int[]{f[1], f[2]});   // {to, price}
        return adj;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] flights, int src, int dst, int k, int expected) {
        int a = bruteForce(n, flights, src, dst, k);
        int b = bfsByStops(n, flights, src, dst, k);
        int c = bellmanFordKRounds(n, flights, src, dst, k);
        check(a == expected && b == expected && c == expected,
                "src " + src + " dst " + dst + " k " + k + ": expected " + expected + " got " + a + " " + b + " " + c);
    }

    public static void main(String[] args) {
        int[][] f1 = {{0, 1, 100}, {1, 2, 100}, {2, 0, 100}, {1, 3, 600}, {2, 3, 200}};
        verify(4, f1, 0, 3, 1, 700);                     // 0 -> 1 -> 3; the cheaper 0 -> 1 -> 2 -> 3 needs 2 stops
        verify(4, f1, 0, 3, 2, 400);                     // now 0 -> 1 -> 2 -> 3 is allowed
        int[][] f2 = {{0, 1, 100}, {1, 2, 100}, {0, 2, 500}};
        verify(3, f2, 0, 2, 1, 200);
        verify(3, f2, 0, 2, 0, 500);                     // k = 0 means direct flights only
        verify(3, new int[][]{{0, 1, 5}}, 0, 2, 1, -1);  // dst unreachable
        int[][] trap = {{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {0, 2, 5}};
        verify(4, trap, 0, 3, 1, 6);                     // plain Dijkstra on price would keep only cost 2 at node 2 and fail
        verify(1, new int[0][], 0, 0, 0, 0);             // src == dst
        verify(2, new int[][]{{1, 0, 3}}, 0, 1, 5, -1);  // flights are one-way

        Random rnd = new Random(519);                    // seeded cross-check on small random graphs
        for (int t = 0; t < 300; t++) {
            int n = 2 + rnd.nextInt(5);
            List<int[]> list = new ArrayList<>();
            for (int u = 0; u < n; u++)
                for (int v = 0; v < n; v++)
                    if (u != v && rnd.nextInt(3) == 0) list.add(new int[]{u, v, 1 + rnd.nextInt(20)});
            int[][] fl = list.toArray(new int[0][]);
            int src = rnd.nextInt(n), dst = rnd.nextInt(n), k = rnd.nextInt(n);
            int expected = bruteForce(n, fl, src, dst, k);
            verify(n, fl, src, dst, k, expected);
        }
        System.out.println("OK P519_CheapestFlightWithinKStops");
    }
}
