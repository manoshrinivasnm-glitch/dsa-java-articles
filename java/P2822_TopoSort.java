import java.util.*;

/** TUF 2822 - Topo Sort. Return any topological order of a DAG: for every edge u -> v, u appears before v. */
public class P2822_TopoSort {

    /** Approach 1: repeatedly find a vertex with no incoming edge from the unplaced vertices, place it, repeat. O(V * (V + E)) time. */
    static int[] bruteForce(int V, List<List<Integer>> adj) {
        boolean[] placed = new boolean[V];
        int[] order = new int[V];
        for (int k = 0; k < V; k++) {
            boolean[] hasIncoming = new boolean[V];     // incoming edge from a vertex that is not placed yet
            for (int u = 0; u < V; u++) {
                if (!placed[u]) for (int v : adj.get(u)) hasIncoming[v] = true;
            }
            int pick = -1;
            for (int v = 0; v < V && pick == -1; v++) {
                if (!placed[v] && !hasIncoming[v]) pick = v;
            }
            if (pick == -1) return new int[0];           // impossible in a DAG: every remaining vertex is on or after a cycle
            placed[pick] = true;
            order[k] = pick;
        }
        return order;
    }

    /** Approach 2: DFS; push a vertex after all its descendants are finished, then pop the stack. O(V + E) time, O(V) space. */
    static int[] optimal(int V, List<List<Integer>> adj) {
        boolean[] vis = new boolean[V];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int s = 0; s < V; s++) {
            if (!vis[s]) dfs(s, adj, vis, stack);
        }
        int[] order = new int[V];
        for (int k = 0; k < V; k++) order[k] = stack.pop();
        return order;
    }

    static void dfs(int u, List<List<Integer>> adj, boolean[] vis, Deque<Integer> stack) {
        vis[u] = true;
        for (int v : adj.get(u)) {
            if (!vis[v]) dfs(v, adj, vis, stack);
        }
        stack.push(u);                                   // everything reachable from u is already on the stack
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

    /** A valid answer is a permutation of 0..V-1 in which every edge points forward. */
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

    static void verify(int V, int[][] edges) {
        List<List<Integer>> adj = build(V, edges);
        String tag = "V=" + V + " edges=" + Arrays.deepToString(edges);
        check(isTopoOrder(V, edges, bruteForce(V, adj)), "bruteForce " + tag);
        check(isTopoOrder(V, edges, optimal(V, adj)), "optimal " + tag);
    }

    public static void main(String[] args) {
        int[][] striver = {{5, 0}, {5, 2}, {4, 0}, {4, 1}, {2, 3}, {3, 1}};
        verify(6, striver);
        check(Arrays.equals(optimal(6, build(6, striver)), new int[]{5, 4, 2, 3, 1, 0}), "dfs order on example");
        check(Arrays.equals(bruteForce(6, build(6, striver)), new int[]{4, 5, 0, 2, 3, 1}), "brute order on example");
        int[][] chain = {{0, 1}, {1, 2}, {2, 3}};                       // only one valid order exists
        verify(4, chain);
        check(Arrays.equals(optimal(4, build(4, chain)), new int[]{0, 1, 2, 3}), "chain dfs");
        check(Arrays.equals(bruteForce(4, build(4, chain)), new int[]{0, 1, 2, 3}), "chain brute");
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}});          // diamond
        verify(5, new int[][]{{3, 1}, {1, 0}});                          // isolated vertices 2 and 4
        verify(3, new int[][]{});                                        // no edges: any permutation works
        verify(1, new int[][]{});                                        // single vertex
        verify(0, new int[][]{});                                        // empty graph
        Random rnd = new Random(2822);                                   // seeded random DAG: edges follow a hidden permutation
        int n = 300;
        List<Integer> perm = new ArrayList<>();
        for (int i = 0; i < n; i++) perm.add(i);
        Collections.shuffle(perm, rnd);
        List<int[]> es = new ArrayList<>();
        for (int t = 0; t < 1500; t++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            if (a != b) es.add(new int[]{perm.get(Math.min(a, b)), perm.get(Math.max(a, b))});
        }
        verify(n, es.toArray(new int[0][]));
        System.out.println("OK P2822_TopoSort");
    }
}
