import java.util.*;

/** TUF 2828 - Why priority Queue is used in Dijkstra's Algorithm. Compares FIFO queue, priority queue and TreeSet frontiers on directed graphs with edges {u, v, w}. */
public class P2828_WhyPriorityQueueIsUsedInDjisktrasAlgorit {

    static final int INF = Integer.MAX_VALUE;

    /** Final distances plus how many times a node's edges were scanned ("expansions"). */
    record Run(int[] dist, int expansions) {}

    /** Directed adjacency list of {neighbour, weight} pairs. */
    static List<List<int[]>> buildAdj(int V, int[][] edges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(new int[]{e[1], e[2]});
        return adj;
    }

    /** Same loop as Dijkstra but the frontier is a FIFO queue. Correct, yet a node can be expanded many times. */
    static Run fifoQueue(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, src});
        int expansions = 0;
        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            int d = top[0], u = top[1];
            if (d > dist[u]) continue;                        // outdated entry
            expansions++;
            for (int[] e : adj.get(u)) {
                if (d + e[1] < dist[e[0]]) {
                    dist[e[0]] = d + e[1];
                    queue.add(new int[]{dist[e[0]], e[0]});
                }
            }
        }
        return new Run(dist, expansions);
    }

    /** Dijkstra with a min-heap and lazy deletion: each reachable node is expanded exactly once. O((V + E) log V). */
    static Run priorityQueue(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.add(new int[]{0, src});
        int expansions = 0;
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0], u = top[1];
            if (d > dist[u]) continue;                        // stale copy left behind by a later improvement
            expansions++;
            for (int[] e : adj.get(u)) {
                if (d + e[1] < dist[e[0]]) {
                    dist[e[0]] = d + e[1];
                    pq.add(new int[]{dist[e[0]], e[0]});
                }
            }
        }
        return new Run(dist, expansions);
    }

    /** Dijkstra with an ordered set: the old entry is removed before the improved one is added (a real decrease-key). */
    static Run treeSet(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        TreeSet<int[]> set = new TreeSet<>((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        set.add(new int[]{0, src});
        int expansions = 0;
        while (!set.isEmpty()) {
            int[] top = set.pollFirst();
            int d = top[0], u = top[1];
            expansions++;                                     // never stale: the set holds one entry per node
            for (int[] e : adj.get(u)) {
                int v = e[0], nd = d + e[1];
                if (nd < dist[v]) {
                    if (dist[v] != INF) set.remove(new int[]{dist[v], v});
                    dist[v] = nd;
                    set.add(new int[]{nd, v});
                }
            }
        }
        return new Run(dist, expansions);
    }

    /** Textbook variant that freezes a node once popped. Right for w >= 0, wrong once a negative edge exists. */
    static int[] settleOnPop(int V, int[][] edges, int src) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        boolean[] settled = new boolean[V];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.add(new int[]{0, src});
        while (!pq.isEmpty()) {
            int u = pq.poll()[1];
            if (settled[u]) continue;
            settled[u] = true;                                // dist[u] is declared final here
            for (int[] e : adj.get(u)) {
                int v = e[0];
                if (!settled[v] && dist[u] + e[1] < dist[v]) {
                    dist[v] = dist[u] + e[1];
                    pq.add(new int[]{dist[v], v});
                }
            }
        }
        return dist;
    }

    /** Reference answer that tolerates negative edges (no negative cycles): relax all edges V - 1 times. */
    static int[] bellmanFord(int V, int[][] edges, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 1; round < V; round++) {
            for (int[] e : edges) {
                if (dist[e[0]] != INF && dist[e[0]] + e[2] < dist[e[1]]) dist[e[1]] = dist[e[0]] + e[2];
            }
        }
        return dist;
    }

    /** k diamonds in a row: s_i -> s_(i+1) costs 2 directly, or 1 through the extra node m_i. Nodes: s_i = 2i, m_i = 2i + 1. */
    static int[][] diamondChain(int k) {
        List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            int s = 2 * i, m = 2 * i + 1, next = 2 * i + 2;
            edges.add(new int[]{s, next, 2});                 // short in hops, long in cost
            edges.add(new int[]{s, m, 1});
            edges.add(new int[]{m, next, 0});                 // long in hops, short in cost
        }
        return edges.toArray(new int[0][]);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int V, int[][] edges, int src, int[] expected) {
        String name = "V=" + V + " src=" + src;
        Run fifo = fifoQueue(V, edges, src), pq = priorityQueue(V, edges, src), ts = treeSet(V, edges, src);
        check(Arrays.equals(fifo.dist(), expected), "fifoQueue " + name + " got " + Arrays.toString(fifo.dist()));
        check(Arrays.equals(pq.dist(), expected), "priorityQueue " + name + " got " + Arrays.toString(pq.dist()));
        check(Arrays.equals(ts.dist(), expected), "treeSet " + name + " got " + Arrays.toString(ts.dist()));
        check(Arrays.equals(settleOnPop(V, edges, src), expected), "settleOnPop " + name);
        check(Arrays.equals(bellmanFord(V, edges, src), expected), "bellmanFord " + name);
        int reachable = 0;
        for (int d : expected) if (d != INF) reachable++;
        check(pq.expansions() == reachable, "heap Dijkstra must expand each reachable node once: " + name);
        check(ts.expansions() == reachable, "set Dijkstra must expand each reachable node once: " + name);
        check(fifo.expansions() >= reachable, "a FIFO queue can only expand more often: " + name);
    }

    public static void main(String[] args) {
        // 1. Every frontier gives the same distances on non-negative graphs.
        verify(5, new int[][]{{0, 1, 4}, {0, 2, 1}, {2, 1, 2}, {1, 3, 1}, {2, 3, 5}, {3, 4, 3}}, 0, new int[]{0, 3, 1, 4, 7});
        verify(1, new int[][]{}, 0, new int[]{0});                                              // single node
        verify(3, new int[][]{{0, 1, 0}, {1, 2, 0}}, 0, new int[]{0, 0, 0});                    // zero weights
        verify(4, new int[][]{{0, 1, 1}, {2, 3, 1}}, 0, new int[]{0, 1, INF, INF});              // unreachable part

        // 2. The diamond chain: the FIFO queue re-expands nodes, the heap does not.
        int k = 40, V = 2 * k + 1;
        int[][] chain = diamondChain(k);
        int[] expected = new int[V];
        for (int i = 0; i <= k; i++) expected[2 * i] = i;                       // s_i is i diamonds away, cost 1 each
        for (int i = 0; i < k; i++) expected[2 * i + 1] = i + 1;
        verify(V, chain, 0, expected);
        Run fifo = fifoQueue(V, chain, 0), pq = priorityQueue(V, chain, 0);
        check(pq.expansions() == V, "heap expands each of the 81 nodes once");
        check(fifo.expansions() == (k + 1) * (k + 1), "FIFO expands (k + 1)^2 times, got " + fifo.expansions());
        System.out.println("diamond chain k=" + k + ": FIFO expansions = " + fifo.expansions() + ", heap expansions = " + pq.expansions());

        // 3. One negative edge breaks "final once popped"; Bellman-Ford still gets it right.
        int[][] neg = {{0, 1, 2}, {0, 2, 5}, {2, 1, -4}};
        check(Arrays.equals(bellmanFord(3, neg, 0), new int[]{0, 1, 5}), "Bellman-Ford with a negative edge");
        check(Arrays.equals(settleOnPop(3, neg, 0), new int[]{0, 2, 5}), "settle-on-pop freezes node 1 at 2 too early");
        Run lazy = priorityQueue(3, neg, 0);
        check(Arrays.equals(lazy.dist(), new int[]{0, 1, 5}), "lazy heap happens to recover here");
        check(lazy.expansions() == 4, "but node 1 is expanded twice, so the once-per-node bound is gone");
        System.out.println("OK P2828_WhyPriorityQueueIsUsedInDjisktrasAlgorit");
    }
}
