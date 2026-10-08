import java.util.*;

/** TUF 2813 - Bipartite Graph (DFS). Can the vertices be 2-coloured so that every edge joins different colours? */
public class P2813_BipartiteGraphDFS {

    /** Approach 1: try all 2^V colourings and test every edge. O(2^V * (V + E)) time, O(1) extra space. Tiny V only. */
    static boolean bruteForce(int V, List<List<Integer>> adj) {
        for (int mask = 0; mask < (1 << V); mask++) {     // bit u of mask = colour of vertex u
            boolean ok = true;
            for (int u = 0; u < V && ok; u++) {
                for (int v : adj.get(u)) {
                    if (((mask >> u) & 1) == ((mask >> v) & 1)) { ok = false; break; }
                }
            }
            if (ok) return true;
        }
        return false;
    }

    /** Approach 2: BFS colouring, layer by layer. O(V + E) time, O(V) space. */
    static boolean bfs(int V, List<List<Integer>> adj) {
        int[] color = new int[V];
        Arrays.fill(color, -1);
        for (int s = 0; s < V; s++) {
            if (color[s] != -1) continue;
            color[s] = 0;
            Deque<Integer> q = new ArrayDeque<>();
            q.offer(s);
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : adj.get(u)) {
                    if (color[v] == -1) { color[v] = 1 - color[u]; q.offer(v); }
                    else if (color[v] == color[u]) return false;
                }
            }
        }
        return true;
    }

    /** Approach 3: DFS colouring; each neighbour gets the opposite colour, and a same-coloured neighbour is a conflict. O(V + E). */
    static boolean dfs(int V, List<List<Integer>> adj) {
        int[] color = new int[V];
        Arrays.fill(color, -1);                          // -1 = not coloured yet
        for (int s = 0; s < V; s++) {
            if (color[s] == -1 && !paint(s, 0, adj, color)) return false;   // each component starts fresh
        }
        return true;
    }

    static boolean paint(int u, int c, List<List<Integer>> adj, int[] color) {
        color[u] = c;
        for (int v : adj.get(u)) {
            if (color[v] == -1) {
                if (!paint(v, 1 - c, adj, color)) return false;
            } else if (color[v] == c) {
                return false;                            // both ends of edge u-v have the same colour
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> build(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }
        return adj;
    }

    static void verify(int V, int[][] edges, boolean expected) {
        List<List<Integer>> adj = build(V, edges);
        String tag = "V=" + V + " edges=" + Arrays.deepToString(edges);
        check(bruteForce(V, adj) == expected, "bruteForce " + tag);
        check(bfs(V, adj) == expected, "bfs " + tag);
        check(dfs(V, adj) == expected, "dfs " + tag);
    }

    static int[][] cycle(int n) {
        int[][] e = new int[n][];
        for (int i = 0; i < n; i++) e[i] = new int[]{i, (i + 1) % n};
        return e;
    }

    public static void main(String[] args) {
        verify(4, new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 2}, {2, 3}}, false);   // LeetCode 785 example 1: triangle 0-1-2
        verify(4, new int[][]{{0, 1}, {0, 3}, {1, 2}, {2, 3}}, true);            // square
        verify(6, cycle(6), true);                                                // even cycle
        verify(5, cycle(5), false);                                               // odd cycle
        verify(7, new int[][]{{0, 1}, {1, 2}, {3, 4}, {4, 5}, {5, 3}}, false);    // second component is a triangle
        verify(8, new int[][]{{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {6, 7}}, true); // a forest is always bipartite
        verify(4, new int[][]{}, true);                                           // no edges
        verify(1, new int[][]{}, true);                                           // single vertex
        verify(0, new int[][]{}, true);                                           // empty graph
        System.out.println("OK P2813_BipartiteGraphDFS");
    }
}
