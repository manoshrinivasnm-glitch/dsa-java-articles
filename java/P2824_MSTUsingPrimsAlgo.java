import java.util.*;

/** TUF 2824 - MST using Prim's algorithm. Undirected edges {u, v, w}. Return the total weight of a minimum spanning tree, or -1 if the graph is disconnected. */
public class P2824_MSTUsingPrimsAlgo {

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

    /** Approach 1: try every subset of V - 1 edges; keep the cheapest one that has no cycle (hence is a spanning tree). O(2^E * E * V) time, O(V) space. */
    static int bruteForce(int V, int[][] edges) {
        int E = edges.length, best = Integer.MAX_VALUE;
        for (int mask = 0; mask < (1 << E); mask++) {
            if (Integer.bitCount(mask) != V - 1) continue;
            int[] comp = new int[V];                           // comp[x]: label of the piece containing x
            for (int i = 0; i < V; i++) comp[i] = i;
            int weight = 0, merges = 0;
            for (int i = 0; i < E; i++) {
                if ((mask >> i & 1) == 0) continue;
                int a = comp[edges[i][0]], b = comp[edges[i][1]];
                if (a == b) break;                             // this subset contains a cycle
                for (int x = 0; x < V; x++) if (comp[x] == b) comp[x] = a;
                weight += edges[i][2];
                merges++;
            }
            if (merges == V - 1) best = Math.min(best, weight);    // V - 1 edges and no cycle: a spanning tree
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    /** Approach 2: Prim with a key array and a linear scan for the cheapest vertex to attach. O(V^2 + E) time, O(V + E) space. */
    static int better(int V, int[][] edges) {
        List<List<int[]>> adj = buildAdj(V, edges);
        int[] key = new int[V];                                // key[v]: cheapest known edge joining v to the tree
        Arrays.fill(key, Integer.MAX_VALUE);
        boolean[] inTree = new boolean[V];
        key[0] = 0;
        int total = 0;
        for (int iter = 0; iter < V; iter++) {
            int u = -1;
            for (int v = 0; v < V; v++) {
                if (!inTree[v] && key[v] != Integer.MAX_VALUE && (u == -1 || key[v] < key[u])) u = v;
            }
            if (u == -1) return -1;                            // some vertex can never be attached
            inTree[u] = true;
            total += key[u];
            for (int[] e : adj.get(u)) {
                if (!inTree[e[0]] && e[1] < key[e[0]]) key[e[0]] = e[1];
            }
        }
        return total;
    }

    /** Approach 3: Prim with a min-heap of {edgeWeight, vertex}; stale entries are skipped when popped. O(E log E) time, O(V + E) space. */
    static int optimal(int V, int[][] edges) {
        List<List<int[]>> adj = buildAdj(V, edges);
        boolean[] inTree = new boolean[V];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));   // {edgeWeight, vertex}
        pq.add(new int[]{0, 0});
        int total = 0, added = 0;
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int w = top[0], u = top[1];
            if (inTree[u]) continue;                           // u already joined through a cheaper edge
            inTree[u] = true;
            total += w;
            added++;
            for (int[] e : adj.get(u)) {
                if (!inTree[e[0]]) pq.add(new int[]{e[1], e[0]});
            }
        }
        return added == V ? total : -1;
    }

    /** Approach 3, returning the tree itself: heap entries also carry the parent, giving edges {parent, child, weight}. */
    static List<int[]> optimalEdges(int V, int[][] edges) {
        List<List<int[]>> adj = buildAdj(V, edges);
        boolean[] inTree = new boolean[V];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));   // {edgeWeight, vertex, parent}
        pq.add(new int[]{0, 0, -1});
        List<int[]> tree = new ArrayList<>();
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int u = top[1];
            if (inTree[u]) continue;
            inTree[u] = true;
            if (top[2] != -1) tree.add(new int[]{top[2], u, top[0]});
            for (int[] e : adj.get(u)) {
                if (!inTree[e[0]]) pq.add(new int[]{e[1], e[0], u});
            }
        }
        return tree;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** True when the given edges connect all V vertices (used to validate optimalEdges). */
    static boolean spans(int V, List<int[]> tree) {
        int[][] asArray = tree.toArray(new int[0][]);
        List<List<int[]>> adj = buildAdj(V, asArray);
        boolean[] seen = new boolean[V];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        seen[0] = true;
        int count = 1;
        while (!stack.isEmpty()) {
            for (int[] e : adj.get(stack.pop())) {
                if (!seen[e[0]]) {
                    seen[e[0]] = true;
                    count++;
                    stack.push(e[0]);
                }
            }
        }
        return count == V;
    }

    static void verify(int V, int[][] edges, int expected) {
        check(bruteForce(V, edges) == expected, "bruteForce got " + bruteForce(V, edges) + " expected " + expected);
        check(better(V, edges) == expected, "better got " + better(V, edges) + " expected " + expected);
        check(optimal(V, edges) == expected, "optimal got " + optimal(V, edges) + " expected " + expected);
        if (expected != -1) {
            List<int[]> tree = optimalEdges(V, edges);
            int sum = 0;
            for (int[] e : tree) sum += e[2];
            check(tree.size() == V - 1 && sum == expected && spans(V, tree), "optimalEdges is not a minimum spanning tree");
        }
    }

    public static void main(String[] args) {
        verify(3, new int[][]{{0, 1, 5}, {1, 2, 3}, {0, 2, 1}}, 4);
        verify(5, new int[][]{{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}}, 16);
        verify(1, new int[][]{}, 0);                                                         // single vertex
        verify(2, new int[][]{{0, 1, 10}, {0, 1, 4}}, 4);                                    // parallel edges
        verify(4, new int[][]{{0, 1, 1}, {2, 3, 1}}, -1);                                    // disconnected
        verify(4, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 0, 1}, {0, 2, 1}}, 3);    // ties everywhere
        verify(3, new int[][]{{0, 1, -2}, {1, 2, 3}, {0, 2, 2}}, 0);                         // negative weights are fine for MST
        verify(3, new int[][]{{0, 0, 1}, {0, 1, 2}, {1, 2, 2}}, 4);                          // self loop is ignored

        // larger seeded graph (connected through a random path): heap and array versions must agree
        Random rnd = new Random(2824);
        int V = 300;
        List<int[]> list = new ArrayList<>();
        for (int i = 1; i < V; i++) list.add(new int[]{rnd.nextInt(i), i, rnd.nextInt(1000)});
        for (int i = 0; i < 3000; i++) list.add(new int[]{rnd.nextInt(V), rnd.nextInt(V), rnd.nextInt(1000)});
        int[][] edges = list.toArray(new int[0][]);
        check(better(V, edges) == optimal(V, edges), "array and heap Prim differ on random graph");
        System.out.println("OK P2824_MSTUsingPrimsAlgo");
    }
}
