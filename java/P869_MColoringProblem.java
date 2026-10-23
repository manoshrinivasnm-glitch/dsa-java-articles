import java.util.*;

/** TUF 869 - M Coloring Problem. Can the vertices of an undirected graph be coloured with at most m colours so that no edge joins two vertices of the same colour? */
public class P869_MColoringProblem {

    private static boolean[][] adjacency(int v, int[][] edges) {
        boolean[][] adj = new boolean[v][v];
        for (int[] e : edges) {
            adj[e[0]][e[1]] = true;
            adj[e[1]][e[0]] = true;
        }
        return adj;
    }

    /** Approach 1: generate all m^v colourings and validate each one only when it is complete. */
    static boolean bruteForce(int v, int[][] edges, int m) {
        boolean[][] adj = adjacency(v, edges);           // adj[a][b] is true when a and b share an edge
        return generate(0, v, m, new int[v], adj);       // color[i] will hold vertex i's colour, 1..m
    }

    private static boolean generate(int node, int v, int m, int[] color, boolean[][] adj) {
        if (node == v) return valid(color, adj, v);      // full colouring built: only now test it
        for (int c = 1; c <= m; c++) {                   // give this vertex every colour in turn
            color[node] = c;
            if (generate(node + 1, v, m, color, adj)) return true;   // some completion is valid: done
        }
        return false;
    }

    private static boolean valid(int[] color, boolean[][] adj, int v) {
        for (int a = 0; a < v; a++) {                    // check every pair of vertices once
            for (int b = a + 1; b < v; b++) {
                if (adj[a][b] && color[a] == color[b]) return false;   // an edge joins two equal colours
            }
        }
        return true;
    }

    /** Approach 2: backtracking. Colour vertices in index order and try a colour only if no already-coloured neighbour has it. */
    static boolean better(int v, int[][] edges, int m) {
        boolean[][] adj = adjacency(v, edges);           // adj[a][b] is true when a and b share an edge
        return backtrack(0, v, m, new int[v], adj);      // 0 in color[] means "not coloured yet"
    }

    private static boolean backtrack(int node, int v, int m, int[] color, boolean[][] adj) {
        if (node == v) return true;                      // every vertex coloured without conflict
        for (int c = 1; c <= m; c++) {                   // try each colour for this vertex
            if (isSafe(node, c, color, adj, v)) {        // no neighbour already has colour c
                color[node] = c;
                if (backtrack(node + 1, v, m, color, adj)) return true;   // the rest could be coloured too
                color[node] = 0;                         // undo
            }
        }
        return false;                                    // no colour fits: the caller must try another
    }

    private static boolean isSafe(int node, int c, int[] color, boolean[][] adj, int v) {
        for (int other = 0; other < v; other++) {
            if (adj[node][other] && color[other] == c) return false;   // a neighbour already uses c
        }
        return true;
    }

    /** Approach 3: backtracking over adjacency lists, with the blocked colours of a vertex gathered once per call and symmetry breaking: a vertex never takes a colour larger than (highest colour used so far) + 1. */
    static boolean optimal(int v, int[][] edges, int m) {
        if (v == 0) return true;                         // no vertices: trivially coloured
        if (m <= 0) return false;                        // at least one vertex but no colours
        if (m >= v) return true;                         // every vertex can simply get its own colour
        // count neighbours first so each list is sized exactly
        int[] degree = new int[v];
        for (int[] e : edges) {
            degree[e[0]]++;
            degree[e[1]]++;
        }
        int[][] adj = new int[v][];                      // adj[i] lists the neighbours of vertex i
        for (int i = 0; i < v; i++) adj[i] = new int[degree[i]];
        int[] fill = new int[v];                         // next free slot in each adjacency list
        for (int[] e : edges) {                          // store each undirected edge in both lists
            adj[e[0]][fill[e[0]]++] = e[1];
            adj[e[1]][fill[e[1]]++] = e[0];
        }
        return colourFrom(0, v, m, new int[v], adj, 0);  // no colour used yet, so maxUsed starts at 0
    }

    private static boolean colourFrom(int node, int v, int m, int[] color, int[][] adj, int maxUsed) {
        if (node == v) return true;                      // every vertex coloured without conflict
        boolean[] blocked = new boolean[m + 1];          // blocked[c]: some neighbour already has colour c
        // mark the colours of already-coloured neighbours (0 means uncoloured)
        for (int nb : adj[node]) if (color[nb] != 0) blocked[color[nb]] = true;
        int limit = Math.min(m, maxUsed + 1);            // colours above maxUsed + 1 are interchangeable with maxUsed + 1
        for (int c = 1; c <= limit; c++) {
            if (blocked[c]) continue;                    // clashes with a neighbour
            color[node] = c;
            // recurse; maxUsed grows only when c is a brand-new colour
            if (colourFrom(node + 1, v, m, color, adj, Math.max(maxUsed, c))) return true;
            color[node] = 0;                             // undo before trying the next colour
        }
        return false;                                    // every allowed colour failed: backtrack
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int v, int[][] edges, int m, boolean expected) {
        check(bruteForce(v, edges, m) == expected, "bruteForce wrong for v=" + v + ", m=" + m);
        check(better(v, edges, m) == expected, "better wrong for v=" + v + ", m=" + m);
        check(optimal(v, edges, m) == expected, "optimal wrong for v=" + v + ", m=" + m);
    }

    static int[][] cycle(int n) {
        int[][] edges = new int[n][];
        for (int i = 0; i < n; i++) edges[i] = new int[]{i, (i + 1) % n};
        return edges;
    }

    static int[][] complete(int n) {
        List<int[]> edges = new ArrayList<>();
        for (int a = 0; a < n; a++) for (int b = a + 1; b < n; b++) edges.add(new int[]{a, b});
        return edges.toArray(new int[0][]);
    }

    public static void main(String[] args) {
        int[][] square = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {0, 2}};
        verify(4, square, 3, true);                      // a square with one diagonal needs exactly 3 colours
        verify(4, square, 2, false);
        verify(3, cycle(3), 2, false);                   // a triangle needs 3
        verify(3, cycle(3), 3, true);
        verify(4, complete(4), 3, false);                // K4 needs 4
        verify(4, complete(4), 4, true);
        verify(6, cycle(6), 2, true);                    // even cycle is bipartite
        verify(5, cycle(5), 2, false);                   // odd cycle is not
        verify(5, cycle(5), 3, true);
        int[][] petersen = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 0}, {0, 5}, {1, 6}, {2, 7}, {3, 8}, {4, 9},
                            {5, 7}, {7, 9}, {9, 6}, {6, 8}, {8, 5}};
        verify(10, petersen, 2, false);                  // chromatic number of the Petersen graph is 3
        verify(10, petersen, 3, true);
        verify(1, new int[0][], 1, true);                // single vertex
        verify(2, new int[][]{{0, 1}}, 1, false);        // one edge, one colour
        verify(3, new int[0][], 1, true);                // no edges: one colour is enough
        verify(2, new int[][]{{0, 1}}, 0, false);        // no colours at all
        System.out.println("OK P869_MColoringProblem");
    }
}
