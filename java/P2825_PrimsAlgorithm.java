import java.util.*;

/** TUF 2825 - Prim's Algorithm. Undirected weighted graph on vertices 0..n-1; return the MST weight (and its edges), or -1 if disconnected. */
public class P2825_PrimsAlgorithm {

    static final int INF = Integer.MAX_VALUE;

    /** Approach 1: array-based Prim, scan for the cheapest outside vertex each step. O(V^2) time, O(V^2) space for the matrix. */
    static int primArray(int n, int[][] edges, List<int[]> mstEdges) {
        int[][] w = new int[n][n];                       // w[u][v] = weight of edge u - v, INF if none
        for (int[] row : w) Arrays.fill(row, INF);
        for (int[] e : edges) {                          // keep the cheapest of parallel edges
            w[e[0]][e[1]] = Math.min(w[e[0]][e[1]], e[2]);
            w[e[1]][e[0]] = Math.min(w[e[1]][e[0]], e[2]);
        }
        int[] key = new int[n], parent = new int[n];     // key[v] = cheapest edge from the tree to v
        boolean[] inTree = new boolean[n];               // vertices already in the MST
        Arrays.fill(key, INF);
        Arrays.fill(parent, -1);
        key[0] = 0;                                      // start the tree from vertex 0
        int total = 0;                                   // weight of the edges taken so far
        for (int step = 0; step < n; step++) {           // each step adds one vertex to the tree
            int u = -1;
            // pick the outside vertex with the cheapest link to the tree
            for (int v = 0; v < n; v++) if (!inTree[v] && (u == -1 || key[v] < key[u])) u = v;
            if (key[u] == INF) return -1;                // nothing outside the tree is reachable
            inTree[u] = true;                            // u joins the tree
            total += key[u];                             // pay for the edge that brought u in
            // record the edge (the start vertex has none)
            if (parent[u] != -1) mstEdges.add(new int[]{parent[u], u, key[u]});
            for (int v = 0; v < n; v++) {                // u's edges may give cheaper links to others
                if (!inTree[v] && w[u][v] < key[v]) {    // v is outside and u -> v is cheaper
                    key[v] = w[u][v];                    // remember the cheaper link
                    parent[v] = u;                       // ... and that it comes from u
                }
            }
        }
        return total;
    }

    /** Approach 2: lazy Prim with a min-heap of {weight, node, parent}. O(E log E) time, O(V + E) space. */
    static int primHeap(int n, int[][] edges, List<int[]> mstEdges) {
        List<List<int[]>> adj = new ArrayList<>();       // adj.get(u) lists {v, w} for each edge at u
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});    // undirected: store both directions
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        boolean[] inTree = new boolean[n];               // vertices already in the MST
        // min-heap of {weight, node, parent}, lightest first
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, 0, -1});                   // start anywhere: vertex 0 with no parent
        int total = 0, added = 0;                        // MST weight and vertices added so far
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();                       // lightest edge leaving the tree (maybe stale)
            int wt = cur[0], u = cur[1], parent = cur[2];
            if (inTree[u]) continue;                     // a cheaper edge already pulled u in
            inTree[u] = true;
            total += wt;                                 // take that edge into the MST
            added++;                                     // one more vertex in the tree
            // record the edge (the start vertex has none)
            if (parent != -1) mstEdges.add(new int[]{parent, u, wt});
            for (int[] e : adj.get(u)) {                 // offer every edge from u to the outside
                if (!inTree[e[0]]) pq.offer(new int[]{e[1], e[0], u});
            }
        }
        return added == n ? total : -1;                  // fewer than n vertices: graph is disconnected
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** The returned edges must be n - 1 real edges that connect everything and add up to the weight. */
    static void checkTree(int n, int[][] edges, List<int[]> tree, int weight) {
        check(tree.size() == n - 1, "tree has " + tree.size() + " edges");
        int[] comp = new int[n];
        for (int i = 0; i < n; i++) comp[i] = i;
        int sum = 0;
        for (int[] t : tree) {
            boolean exists = false;
            for (int[] e : edges) {
                if (e[2] == t[2] && ((e[0] == t[0] && e[1] == t[1]) || (e[0] == t[1] && e[1] == t[0]))) exists = true;
            }
            check(exists, "edge " + Arrays.toString(t) + " not in graph");
            int a = comp[t[0]], b = comp[t[1]];
            check(a != b, "tree edge closes a cycle");
            for (int i = 0; i < n; i++) if (comp[i] == b) comp[i] = a;
            sum += t[2];
        }
        check(sum == weight, "edge weights do not add up");
    }

    static void verify(int n, int[][] edges, int expected) {
        List<int[]> t1 = new ArrayList<>(), t2 = new ArrayList<>();
        int a = primArray(n, edges, t1), b = primHeap(n, edges, t2);
        check(a == expected && b == expected, "n " + n + ": expected " + expected + " got " + a + " " + b);
        if (expected != -1) {
            checkTree(n, edges, t1, a);
            checkTree(n, edges, t2, b);
        }
    }

    public static void main(String[] args) {
        verify(3, new int[][]{{0, 1, 5}, {1, 2, 3}, {0, 2, 1}}, 4);
        verify(5, new int[][]{{0, 1, 2}, {0, 2, 1}, {1, 2, 1}, {2, 3, 2}, {3, 4, 1}, {4, 2, 2}}, 5);
        verify(5, new int[][]{{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}}, 16);
        verify(1, new int[0][], 0);                      // single vertex: empty tree
        verify(2, new int[][]{{0, 1, 7}, {0, 1, 3}}, 3); // parallel edges
        verify(4, new int[][]{{0, 1, 1}, {2, 3, 1}}, -1);// disconnected: no spanning tree
        verify(3, new int[][]{{0, 0, 1}, {0, 1, 4}, {1, 2, 4}, {2, 0, 4}}, 8);   // self-loop and equal weights

        Random rnd = new Random(2825);                   // seeded cross-check of the two versions
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(8);
            List<int[]> list = new ArrayList<>();
            for (int u = 0; u < n; u++)
                for (int v = u + 1; v < n; v++)
                    if (rnd.nextInt(2) == 0) list.add(new int[]{u, v, 1 + rnd.nextInt(9)});
            int[][] edges = list.toArray(new int[0][]);
            verify(n, edges, primArray(n, edges, new ArrayList<>()));
        }
        System.out.println("OK P2825_PrimsAlgorithm");
    }
}
