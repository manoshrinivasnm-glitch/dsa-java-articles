import java.util.*;

/** TUF 497 - Bridges in graph (Critical Connections in a Network). Return every edge whose removal disconnects its endpoints. */
public class P497_BridgesInGraph {

    /** Approach 1: delete each edge in turn and test whether its endpoints are still connected. O(E * (V + E)) time, O(V + E) space. */
    static List<List<Integer>> bruteForce(int n, int[][] connections) {
        List<List<Integer>> bridges = new ArrayList<>();
        for (int skip = 0; skip < connections.length; skip++) {
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
            for (int e = 0; e < connections.length; e++) {
                if (e == skip) continue;                         // graph without edge 'skip'
                adj.get(connections[e][0]).add(connections[e][1]);
                adj.get(connections[e][1]).add(connections[e][0]);
            }
            int u = connections[skip][0], v = connections[skip][1];
            if (!reachable(adj, u, v)) bridges.add(List.of(u, v));
        }
        return bridges;
    }

    static boolean reachable(List<List<Integer>> adj, int src, int dst) {
        boolean[] seen = new boolean[adj.size()];
        Deque<Integer> stack = new ArrayDeque<>();
        seen[src] = true;
        stack.push(src);
        while (!stack.isEmpty()) {
            int x = stack.pop();
            if (x == dst) return true;
            for (int y : adj.get(x)) {
                if (!seen[y]) {
                    seen[y] = true;
                    stack.push(y);
                }
            }
        }
        return false;
    }

    /** Approach 2: Tarjan's bridge-finding DFS with discovery time and low-link. O(V + E) time, O(V + E) space. */
    static List<List<Integer>> tarjan(int n, int[][] connections) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : connections) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        int[] tin = new int[n], low = new int[n], timer = new int[1];
        boolean[] visited = new boolean[n];
        List<List<Integer>> bridges = new ArrayList<>();
        for (int s = 0; s < n; s++) {
            if (!visited[s]) dfs(s, -1, adj, visited, tin, low, timer, bridges);   // every component
        }
        return bridges;
    }

    static void dfs(int u, int parent, List<List<Integer>> adj, boolean[] visited,
                    int[] tin, int[] low, int[] timer, List<List<Integer>> bridges) {
        visited[u] = true;
        tin[u] = low[u] = timer[0]++;
        for (int v : adj.get(u)) {
            if (v == parent) continue;                           // do not walk back along the tree edge
            if (!visited[v]) {
                dfs(v, u, adj, visited, tin, low, timer, bridges);
                low[u] = Math.min(low[u], low[v]);
                if (low[v] > tin[u]) bridges.add(List.of(u, v)); // v's subtree cannot reach u or above
            } else {
                low[u] = Math.min(low[u], tin[v]);               // back edge to an ancestor
            }
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Bridges may be listed in any order and either direction; compare as sorted "a-b" strings with a < b. */
    static List<String> normalize(List<List<Integer>> edges) {
        List<String> out = new ArrayList<>();
        for (List<Integer> e : edges) out.add(Math.min(e.get(0), e.get(1)) + "-" + Math.max(e.get(0), e.get(1)));
        Collections.sort(out);
        return out;
    }

    static void verify(int n, int[][] connections, String... expected) {
        List<String> want = new ArrayList<>(Arrays.asList(expected));
        Collections.sort(want);
        List<String> a = normalize(bruteForce(n, connections)), b = normalize(tarjan(n, connections));
        check(a.equals(want), "bruteForce got " + a + " expected " + want);
        check(b.equals(want), "tarjan got " + b + " expected " + want);
    }

    public static void main(String[] args) {
        verify(4, new int[][]{ {0, 1}, {1, 2}, {2, 0}, {1, 3} }, "1-3");
        verify(2, new int[][]{ {0, 1} }, "0-1");
        verify(6, new int[][]{ {0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}, {4, 5}, {5, 3} }, "2-3");   // two triangles joined by one edge
        verify(4, new int[][]{ {0, 1}, {1, 2}, {2, 3} }, "0-1", "1-2", "2-3");                    // a path: every edge
        verify(4, new int[][]{ {0, 1}, {1, 2}, {2, 3}, {3, 0} });                                     // a cycle: none
        verify(1, new int[][]{});                                                                  // single vertex
        verify(5, new int[][]{ {0, 1}, {2, 3}, {3, 4}, {4, 2} }, "0-1");                           // disconnected graph
        verify(12, new int[][]{ {0, 1}, {1, 2}, {2, 0}, {1, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 3}, {6, 7},
                {7, 8}, {8, 9}, {9, 10}, {10, 7}, {8, 11} }, "1-3", "6-7", "8-11");
        // seeded cross-check on random simple graphs
        Random rnd = new Random(497);
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
            check(normalize(bruteForce(n, arr)).equals(normalize(tarjan(n, arr))), "random mismatch at trial " + t);
        }
        System.out.println("OK P497_BridgesInGraph");
    }
}
