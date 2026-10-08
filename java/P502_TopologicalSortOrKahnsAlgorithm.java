import java.util.*;

/** TUF 502 - Topological sort or Kahn's algorithm. BFS topological order by in-degrees; empty array if the graph has a cycle. */
public class P502_TopologicalSortOrKahnsAlgorithm {

    /** Approach 1: keep in-degrees, but scan all vertices for a zero each round. O(V^2 + E) time, O(V) space. */
    static int[] bruteForce(int V, List<List<Integer>> adj) {
        int[] indeg = new int[V];
        for (int u = 0; u < V; u++) for (int v : adj.get(u)) indeg[v]++;
        boolean[] placed = new boolean[V];
        int[] order = new int[V];
        for (int k = 0; k < V; k++) {
            int pick = -1;
            for (int v = 0; v < V && pick == -1; v++) {
                if (!placed[v] && indeg[v] == 0) pick = v;
            }
            if (pick == -1) return new int[0];           // every remaining vertex still has an incoming edge: cycle
            placed[pick] = true;
            order[k] = pick;
            for (int v : adj.get(pick)) indeg[v]--;
        }
        return order;
    }

    /** Approach 2: Kahn's algorithm; a queue holds exactly the vertices whose in-degree has dropped to zero. O(V + E) time. */
    static int[] kahn(int V, List<List<Integer>> adj) {
        int[] indeg = new int[V];
        for (int u = 0; u < V; u++) for (int v : adj.get(u)) indeg[v]++;
        Deque<Integer> q = new ArrayDeque<>();
        for (int v = 0; v < V; v++) if (indeg[v] == 0) q.offer(v);
        int[] order = new int[V];
        int k = 0;
        while (!q.isEmpty()) {
            int u = q.poll();
            order[k++] = u;
            for (int v : adj.get(u)) {
                if (--indeg[v] == 0) q.offer(v);         // u was v's last unplaced prerequisite
            }
        }
        return k == V ? order : new int[0];              // vertices never freed sit on or behind a cycle
    }

    /** Approach 3: Kahn's with a min-heap, giving the lexicographically smallest order. O((V + E) log V) time. */
    static int[] kahnSmallestFirst(int V, List<List<Integer>> adj) {
        int[] indeg = new int[V];
        for (int u = 0; u < V; u++) for (int v : adj.get(u)) indeg[v]++;
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int v = 0; v < V; v++) if (indeg[v] == 0) pq.offer(v);
        int[] order = new int[V];
        int k = 0;
        while (!pq.isEmpty()) {
            int u = pq.poll();
            order[k++] = u;
            for (int v : adj.get(u)) {
                if (--indeg[v] == 0) pq.offer(v);
            }
        }
        return k == V ? order : new int[0];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> build(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    static boolean isTopoOrder(int V, int[][] edges, int[] order) {
        if (order.length != V) return false;
        int[] pos = new int[V];
        Arrays.fill(pos, -1);
        for (int k = 0; k < V; k++) {
            if (order[k] < 0 || order[k] >= V || pos[order[k]] != -1) return false;
            pos[order[k]] = k;
        }
        for (int[] e : edges) if (pos[e[0]] > pos[e[1]]) return false;
        return true;
    }

    /** acyclic: every approach returns a valid order; cyclic: every approach returns an empty array. */
    static void verify(int V, int[][] edges, boolean acyclic) {
        List<List<Integer>> adj = build(V, edges);
        String tag = "V=" + V + " edges=" + Arrays.deepToString(edges);
        int[][] results = {bruteForce(V, adj), kahn(V, adj), kahnSmallestFirst(V, adj)};
        for (int[] r : results) {
            if (acyclic) check(isTopoOrder(V, edges, r), "not a topological order " + Arrays.toString(r) + " " + tag);
            else check(r.length == 0, "cycle not reported " + tag);
        }
        check(Arrays.equals(results[0], results[2]), "smallest-first orders differ " + tag);   // both always take the smallest free vertex
    }

    public static void main(String[] args) {
        int[][] striver = {{5, 0}, {5, 2}, {4, 0}, {4, 1}, {2, 3}, {3, 1}};
        verify(6, striver, true);
        check(Arrays.equals(kahn(6, build(6, striver)), new int[]{4, 5, 0, 2, 3, 1}), "kahn order on example");
        int[][] twoChains = {{0, 3}, {1, 2}};
        verify(4, twoChains, true);
        check(Arrays.equals(kahn(4, build(4, twoChains)), new int[]{0, 1, 3, 2}), "FIFO order");
        check(Arrays.equals(kahnSmallestFirst(4, build(4, twoChains)), new int[]{0, 1, 2, 3}), "smallest order");
        verify(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}, false);           // pure cycle
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 1}, {2, 3}}, false);   // cycle in the middle blocks 3 as well
        verify(2, new int[][]{{1, 1}}, false);                           // self-loop
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}}, true);    // diamond
        verify(1, new int[][]{}, true);                                  // single vertex
        verify(0, new int[][]{}, true);                                  // empty graph
        Random rnd = new Random(502);                                    // seeded random DAG
        int n = 400;
        List<Integer> perm = new ArrayList<>();
        for (int i = 0; i < n; i++) perm.add(i);
        Collections.shuffle(perm, rnd);
        List<int[]> es = new ArrayList<>();
        for (int t = 0; t < 2000; t++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            if (a != b) es.add(new int[]{perm.get(Math.min(a, b)), perm.get(Math.max(a, b))});
        }
        verify(n, es.toArray(new int[0][]), true);
        System.out.println("OK P502_TopologicalSortOrKahnsAlgorithm");
    }
}
