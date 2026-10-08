import java.util.*;

/** TUF 496 - Articulation point in graph. Return, sorted, every vertex whose removal increases the number of connected components, or [-1]. */
public class P496_ArticulationPointInGraph {

    static List<List<Integer>> buildGraph(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    /** Approach 1: remove each vertex in turn and recount components. O(V * (V + E)) time, O(V + E) space. */
    static List<Integer> bruteForce(int n, int[][] edges) {
        List<List<Integer>> adj = buildGraph(n, edges);
        int original = countComponents(n, adj, -1);
        List<Integer> points = new ArrayList<>();
        for (int x = 0; x < n; x++) {
            if (countComponents(n, adj, x) > original) points.add(x);
        }
        if (points.isEmpty()) points.add(-1);
        return points;
    }

    static int countComponents(int n, List<List<Integer>> adj, int removed) {
        boolean[] seen = new boolean[n];
        if (removed >= 0) seen[removed] = true;                  // pretend the vertex is gone
        Deque<Integer> stack = new ArrayDeque<>();
        int count = 0;
        for (int s = 0; s < n; s++) {
            if (seen[s]) continue;
            count++;
            seen[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (int v : adj.get(u)) {
                    if (!seen[v]) {
                        seen[v] = true;
                        stack.push(v);
                    }
                }
            }
        }
        return count;
    }

    /** Approach 2: Tarjan's DFS with discovery time and low-link. O(V + E) time, O(V + E) space. */
    static List<Integer> tarjan(int n, int[][] edges) {
        List<List<Integer>> adj = buildGraph(n, edges);
        int[] tin = new int[n], low = new int[n], timer = new int[1];
        boolean[] visited = new boolean[n], isCut = new boolean[n];
        for (int s = 0; s < n; s++) {
            if (!visited[s]) dfs(s, -1, adj, visited, tin, low, timer, isCut);   // every component
        }
        List<Integer> points = new ArrayList<>();
        for (int x = 0; x < n; x++) {
            if (isCut[x]) points.add(x);                         // a vertex can qualify via several children
        }
        if (points.isEmpty()) points.add(-1);
        return points;
    }

    static void dfs(int u, int parent, List<List<Integer>> adj, boolean[] visited,
                    int[] tin, int[] low, int[] timer, boolean[] isCut) {
        visited[u] = true;
        tin[u] = low[u] = timer[0]++;
        int children = 0;
        for (int v : adj.get(u)) {
            if (v == parent) continue;
            if (!visited[v]) {
                dfs(v, u, adj, visited, tin, low, timer, isCut);
                low[u] = Math.min(low[u], low[v]);
                if (low[v] >= tin[u] && parent != -1) isCut[u] = true;   // v's subtree cannot climb above u
                children++;
            } else {
                low[u] = Math.min(low[u], tin[v]);               // tin, not low: see the article
            }
        }
        if (parent == -1 && children > 1) isCut[u] = true;       // DFS root: needs two separate subtrees
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] edges, List<Integer> expected) {
        List<Integer> a = bruteForce(n, edges), b = tarjan(n, edges);
        check(a.equals(expected), "bruteForce got " + a + " expected " + expected);
        check(b.equals(expected), "tarjan got " + b + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(5, new int[][]{ {0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4} }, List.of(1, 4));
        verify(4, new int[][]{ {0, 1}, {1, 2}, {2, 3}, {3, 0} }, List.of(-1));                    // a cycle has none
        verify(5, new int[][]{ {0, 1}, {1, 2}, {2, 0}, {1, 3}, {3, 4}, {4, 1} }, List.of(1));     // bow-tie: fails if low[v] is used on back edges
        verify(4, new int[][]{ {0, 1}, {1, 2}, {2, 3} }, List.of(1, 2));                          // path
        verify(4, new int[][]{ {0, 1}, {0, 2}, {0, 3} }, List.of(0));                             // star: root rule
        verify(1, new int[][]{}, List.of(-1));                                                     // single vertex
        verify(2, new int[][]{ {0, 1} }, List.of(-1));                                             // single edge
        verify(6, new int[][]{ {0, 1}, {1, 2}, {3, 4}, {4, 5}, {5, 3} }, List.of(1));             // disconnected
        // seeded cross-check on random simple graphs
        Random rnd = new Random(496);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(9), m = rnd.nextInt(14);
            Set<Integer> used = new HashSet<>();
            List<int[]> es = new ArrayList<>();
            for (int k = 0; k < m && n > 1; k++) {
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                if (u == v || !used.add(Math.min(u, v) * 100 + Math.max(u, v))) continue;
                es.add(new int[]{u, v});
            }
            int[][] arr = es.toArray(new int[0][]);
            check(bruteForce(n, arr).equals(tarjan(n, arr)), "random mismatch at trial " + t);
        }
        System.out.println("OK P496_ArticulationPointInGraph");
    }
}
