import java.util.*;

/** TUF 521 - Find the city with the smallest number of neighbors at a threshold distance. Ties go to the largest city number. */
public class P521_FindTheCityWithTheSmallestNumberOfNeighb {

    static final int INF = Integer.MAX_VALUE / 2;        // INF + INF still fits in an int

    /** Approach 1: Floyd-Warshall for all pairs, then count neighbours within the threshold. O(n^3) time, O(n^2) space. */
    static int floydWarshall(int n, int[][] edges, int distanceThreshold) {
        int[][] dist = new int[n][n];
        for (int[] row : dist) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) dist[i][i] = 0;
        for (int[] e : edges) {                          // roads are two-way
            dist[e[0]][e[1]] = Math.min(dist[e[0]][e[1]], e[2]);
            dist[e[1]][e[0]] = Math.min(dist[e[1]][e[0]], e[2]);
        }
        for (int via = 0; via < n; via++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][via] + dist[via][j] < dist[i][j]) dist[i][j] = dist[i][via] + dist[via][j];
                }
            }
        }
        int answer = -1, fewest = Integer.MAX_VALUE;
        for (int city = 0; city < n; city++) {
            int reachable = 0;
            for (int j = 0; j < n; j++) if (j != city && dist[city][j] <= distanceThreshold) reachable++;
            if (reachable <= fewest) {                   // "<=" lets a later (larger) city win ties
                fewest = reachable;
                answer = city;
            }
        }
        return answer;
    }

    /** Approach 2: Dijkstra from every city, stopping once distances pass the threshold. O(n * E log E) time, O(n + E) space. */
    static int dijkstraFromEach(int n, int[][] edges, int distanceThreshold) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        int answer = -1, fewest = Integer.MAX_VALUE;
        for (int city = 0; city < n; city++) {
            int reachable = countWithin(adj, city, distanceThreshold);
            if (reachable <= fewest) {
                fewest = reachable;
                answer = city;
            }
        }
        return answer;
    }

    static int countWithin(List<List<int[]>> adj, int src, int limit) {
        int[] dist = new int[adj.size()];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, src});                     // {distance, city}
        int count = 0;
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], u = cur[1];
            if (d > dist[u]) continue;                   // stale entry
            if (d > limit) break;                        // everything left in the heap is even farther
            if (u != src) count++;
            for (int[] e : adj.get(u)) {
                if (d + e[1] < dist[e[0]]) {
                    dist[e[0]] = d + e[1];
                    pq.offer(new int[]{dist[e[0]], e[0]});
                }
            }
        }
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] edges, int threshold, int expected) {
        int a = floydWarshall(n, edges, threshold), b = dijkstraFromEach(n, edges, threshold);
        check(a == expected && b == expected, "n " + n + " threshold " + threshold + ": expected " + expected + " got " + a + " " + b);
    }

    public static void main(String[] args) {
        verify(4, new int[][]{{0, 1, 3}, {1, 2, 1}, {1, 3, 4}, {2, 3, 1}}, 4, 3);
        verify(5, new int[][]{{0, 1, 2}, {0, 4, 8}, {1, 2, 3}, {1, 4, 2}, {2, 3, 1}, {3, 4, 1}}, 2, 0);
        verify(3, new int[0][], 5, 2);                   // no roads: everyone reaches 0 cities, largest index wins
        verify(2, new int[][]{{0, 1, 3}}, 2, 1);         // threshold below the only road
        verify(3, new int[][]{{0, 1, 5}, {1, 2, 1}, {0, 2, 1}}, 2, 2);    // 0 -> 2 -> 1 (2) beats the direct road (5)
        verify(4, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}}, 1, 3);    // path graph: the two ends reach 1 city each

        Random rnd = new Random(521);                    // seeded cross-check
        for (int t = 0; t < 300; t++) {
            int n = 2 + rnd.nextInt(7);
            List<int[]> list = new ArrayList<>();
            for (int u = 0; u < n; u++)
                for (int v = u + 1; v < n; v++)
                    if (rnd.nextInt(3) == 0) list.add(new int[]{u, v, 1 + rnd.nextInt(10)});
            int[][] edges = list.toArray(new int[0][]);
            int threshold = 1 + rnd.nextInt(15);
            verify(n, edges, threshold, floydWarshall(n, edges, threshold));
        }
        System.out.println("OK P521_FindTheCityWithTheSmallestNumberOfNeighb");
    }
}
