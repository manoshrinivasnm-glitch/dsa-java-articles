import java.util.*;

/** TUF 499 - Bipartite graph (BFS check). graph[u] lists the neighbours of u in an undirected graph. */
public class P499_BipartiteGraph {

    /** Approach 1: try all 2^V ways to split the vertices into two sides. O(2^V * (V + E)) time, O(1) extra space. Only for tiny V. */
    static boolean bruteForce(int[][] graph) {
        int V = graph.length;
        for (long mask = 0; mask < (1L << V); mask++) {  // bit u of mask = side of vertex u
            boolean ok = true;
            for (int u = 0; u < V && ok; u++) {
                for (int v : graph[u]) {
                    if (((mask >> u) & 1) == ((mask >> v) & 1)) { ok = false; break; }
                }
            }
            if (ok) return true;
        }
        return false;
    }

    /** Approach 2: BFS 2-colouring. Colour a start vertex 0, its neighbours 1, theirs 0, and look for a clash. O(V + E) time, O(V) space. */
    static boolean optimal(int[][] graph) {
        int V = graph.length;
        int[] color = new int[V];
        Arrays.fill(color, -1);                           // -1 = not coloured yet
        Deque<Integer> queue = new ArrayDeque<>();
        for (int s = 0; s < V; s++) {
            if (color[s] != -1) continue;                 // already handled as part of an earlier component
            color[s] = 0;
            queue.add(s);
            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (int v : graph[u]) {
                    if (color[v] == -1) {
                        color[v] = 1 - color[u];          // forced: the opposite side of u
                        queue.add(v);
                    } else if (color[v] == color[u]) {
                        return false;                     // an edge inside one side
                    }
                }
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] graph, boolean expected) {
        check(bruteForce(graph) == expected, "bruteForce " + Arrays.deepToString(graph));
        check(optimal(graph) == expected, "optimal " + Arrays.deepToString(graph));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2, 3}, {0, 2}, {0, 1, 3}, {0, 2}}, false);       // LeetCode example 1: triangle 0-1-2
        verify(new int[][]{{1, 3}, {0, 2}, {1, 3}, {0, 2}}, true);              // LeetCode example 2: square
        verify(new int[][]{{}}, true);                                          // single vertex
        verify(new int[][]{}, true);                                            // empty graph
        verify(new int[][]{{1}, {0}, {3}, {2, 4}, {3, 5}, {4, 6}, {5, 7}, {6, 3}}, false); // odd cycle 3..7 in second component
        verify(new int[][]{{1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4}}, true);    // path of 6 vertices
        verify(new int[][]{{1, 4}, {0, 2}, {1, 3}, {2, 4}, {3, 0}}, false);     // 5-cycle
        verify(new int[][]{{3, 4}, {3, 4}, {3, 4}, {0, 1, 2}, {0, 1, 2}}, true); // complete bipartite K(3,2)
        System.out.println("OK P499_BipartiteGraph");
    }
}
