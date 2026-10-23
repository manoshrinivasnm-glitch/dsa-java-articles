import java.util.*;

/** TUF 498 - Kosaraju's algorithm. Find the strongly connected components of a directed graph (their count is the usual answer). */
public class P498_KosarajusAlgorithm {

    /** Approach 1: compute reachability from every vertex; u and v share an SCC iff each reaches the other. O(V * (V + E)) time, O(V^2) space. */
    static List<List<Integer>> bruteForce(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();             // adj.get(u) = targets of u's outgoing edges
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);           // directed: store each edge once
        boolean[][] reach = new boolean[n][];                    // reach[s][v] = s can reach v
        for (int s = 0; s < n; s++) reach[s] = reachableFrom(s, adj);   // one search from every vertex
        boolean[] assigned = new boolean[n];                     // vertices already placed in an SCC
        List<List<Integer>> sccs = new ArrayList<>();
        for (int i = 0; i < n; i++) {                            // the first unassigned vertex starts an SCC
            if (assigned[i]) continue;                           // already in an earlier SCC
            List<Integer> comp = new ArrayList<>();              // every vertex that shares i's SCC
            for (int j = i; j < n; j++) {
                if (reach[i][j] && reach[j][i]) {                // mutually reachable
                    assigned[j] = true;
                    comp.add(j);
                }
            }
            sccs.add(comp);                                      // one more SCC found
        }
        return sccs;
    }

    static boolean[] reachableFrom(int s, List<List<Integer>> adj) {
        boolean[] seen = new boolean[adj.size()];                // vertices reachable from s
        Deque<Integer> stack = new ArrayDeque<>();               // DFS stack of vertices to expand
        seen[s] = true;
        stack.push(s);
        while (!stack.isEmpty()) {
            int u = stack.pop();                                 // next vertex to expand
            for (int v : adj.get(u)) {                           // follow every outgoing edge
                if (!seen[v]) {                                  // first visit to v
                    seen[v] = true;
                    stack.push(v);
                }
            }
        }
        return seen;                                             // seen[v] is true exactly when s reaches v
    }

    /** Approach 2: Kosaraju - order by finish time, reverse every edge, then DFS in that order. O(V + E) time, O(V + E) space. */
    static List<List<Integer>> kosaraju(int n, int[][] edges) {
        // original graph and its transpose
        List<List<Integer>> adj = new ArrayList<>(), rev = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
            rev.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);                             // original edge u -> v
            rev.get(e[1]).add(e[0]);                             // transpose graph
        }
        boolean[] visited = new boolean[n];                      // vertices already explored in this pass
        Deque<Integer> finished = new ArrayDeque<>();            // stack of vertices, last finished on top
        for (int s = 0; s < n; s++) {                            // pass 1: finish order on the original graph
            if (!visited[s]) dfsOrder(s, adj, visited, finished);   // DFS from every unexplored vertex
        }
        Arrays.fill(visited, false);                             // reset for the second pass
        List<List<Integer>> sccs = new ArrayList<>();            // one list per strongly connected component
        while (!finished.isEmpty()) {                            // pass 2: latest finisher first, on the transpose
            int s = finished.pop();                              // latest finisher still left
            if (visited[s]) continue;                            // already inside an earlier SCC
            List<Integer> comp = new ArrayList<>();              // the SCC that s belongs to
            dfsCollect(s, rev, visited, comp);                   // collect s's SCC via the transpose
            sccs.add(comp);
        }
        return sccs;
    }

    static void dfsOrder(int u, List<List<Integer>> adj, boolean[] visited, Deque<Integer> finished) {
        visited[u] = true;                                       // u is now explored
        for (int v : adj.get(u)) {                               // follow every outgoing edge
            if (!visited[v]) dfsOrder(v, adj, visited, finished);   // finish all of u's descendants first
        }
        finished.push(u);                                        // pushed after all descendants finish
    }

    static void dfsCollect(int u, List<List<Integer>> rev, boolean[] visited, List<Integer> comp) {
        visited[u] = true;                                       // u is now explored
        comp.add(u);                                             // u belongs to this SCC
        for (int v : rev.get(u)) {                               // follow reversed edges only
            if (!visited[v]) dfsCollect(v, rev, visited, comp);  // unvisited ones here share u's SCC
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Components may come out in any order; sort inside and across components before comparing. */
    static List<String> normalize(List<List<Integer>> sccs) {
        List<String> out = new ArrayList<>();
        for (List<Integer> c : sccs) {
            List<Integer> s = new ArrayList<>(c);
            Collections.sort(s);
            out.add(s.toString());
        }
        Collections.sort(out);
        return out;
    }

    static void verify(int n, int[][] edges, int expectedCount, String expectedNormalized) {
        List<List<Integer>> a = bruteForce(n, edges), b = kosaraju(n, edges);
        check(a.size() == expectedCount, "bruteForce count " + a.size() + " expected " + expectedCount);
        check(b.size() == expectedCount, "kosaraju count " + b.size() + " expected " + expectedCount);
        check(normalize(a).toString().equals(expectedNormalized), "bruteForce comps " + normalize(a));
        check(normalize(b).toString().equals(expectedNormalized), "kosaraju comps " + normalize(b));
    }

    public static void main(String[] args) {
        verify(5, new int[][]{ {1, 0}, {0, 2}, {2, 1}, {0, 3}, {3, 4} }, 3, "[[0, 1, 2], [3], [4]]");
        verify(3, new int[][]{ {0, 1}, {1, 2}, {2, 0} }, 1, "[[0, 1, 2]]");                      // one big cycle
        verify(8, new int[][]{ {0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 4}, {6, 7} },
                4, "[[0, 1, 2], [3], [4, 5, 6], [7]]");
        verify(1, new int[][]{}, 1, "[[0]]");                                                      // single vertex
        verify(4, new int[][]{}, 4, "[[0], [1], [2], [3]]");                                       // no edges
        verify(4, new int[][]{ {0, 1}, {1, 2}, {2, 3} }, 4, "[[0], [1], [2], [3]]");               // DAG
        verify(2, new int[][]{ {0, 0}, {0, 1}, {0, 1} }, 2, "[[0], [1]]");                          // self-loop, duplicate edge
        verify(6, new int[][]{ {0, 1}, {1, 0}, {2, 3}, {3, 2}, {1, 2}, {4, 5}, {5, 4}, {5, 0} }, 3, "[[0, 1], [2, 3], [4, 5]]");
        // seeded cross-check on random directed graphs
        Random rnd = new Random(498);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(10), m = rnd.nextInt(20);
            int[][] es = new int[m][];
            for (int k = 0; k < m; k++) es[k] = new int[]{rnd.nextInt(n), rnd.nextInt(n)};
            check(normalize(bruteForce(n, es)).equals(normalize(kosaraju(n, es))), "random mismatch at trial " + t);
        }
        System.out.println("OK P498_KosarajusAlgorithm");
    }
}
