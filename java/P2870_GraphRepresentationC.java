import java.util.*;

/** TUF 2870 - Graph Representation (the C++ lecture, written in Java). Adjacency matrix vs adjacency list, 1-based vertices, directed and weighted variants. */
public class P2870_GraphRepresentationC {

    /** Adjacency matrix for an undirected graph on vertices 1..n: mat[u][v] = 1 when u and v are joined. O(n^2) space. */
    static int[][] adjacencyMatrix(int n, int[][] edges) {
        int[][] mat = new int[n + 1][n + 1];                 // row and column 0 unused: vertices are 1-based
        for (int[] e : edges) {
            mat[e[0]][e[1]] = 1;
            mat[e[1]][e[0]] = 1;                             // undirected: the matrix is symmetric
        }
        return mat;
    }

    /** Adjacency list for an undirected graph on vertices 1..n. Every edge is stored twice, 2E entries in total. */
    static List<List<Integer>> adjacencyList(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    /** Directed version: edge {u, v} means u -> v only, so it is stored once. E entries in total. */
    static List<List<Integer>> directedList(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    /** Weighted matrix: store the weight instead of 1. 0 still means "no edge", so this assumes non-zero weights. */
    static int[][] weightedMatrix(int n, int[][] wEdges) {
        int[][] mat = new int[n + 1][n + 1];
        for (int[] e : wEdges) {
            mat[e[0]][e[1]] = e[2];
            mat[e[1]][e[0]] = e[2];
        }
        return mat;
    }

    /** Weighted list: each entry is {neighbour, weight}, the Java stand-in for C++ pair<int, int>. */
    static List<List<int[]>> weightedList(int n, int[][] wEdges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] e : wEdges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        return adj;
    }

    /** Neighbours from a matrix: scan the whole row, O(n) however few neighbours there are. */
    static List<Integer> neighboursFromMatrix(int[][] mat, int u) {
        List<Integer> out = new ArrayList<>();
        for (int v = 1; v < mat.length; v++) {
            if (mat[u][v] != 0) out.add(v);
        }
        return out;
    }

    /** Edges counted from an undirected matrix: every edge sets two cells. */
    static int edgesInMatrix(int[][] mat) {
        int cells = 0;
        for (int[] row : mat) {
            for (int x : row) if (x != 0) cells++;
        }
        return cells / 2;
    }

    /** Entries stored in an adjacency list: 2E for undirected, E for directed. */
    static int storedEntries(List<? extends List<?>> adj) {
        int total = 0;
        for (List<?> list : adj) total += list.size();
        return total;
    }

    /** Rebuild the matrix from the list: both describe exactly the same graph. */
    static int[][] listToMatrix(List<List<Integer>> adj) {
        int size = adj.size();
        int[][] mat = new int[size][size];
        for (int u = 0; u < size; u++) {
            for (int v : adj.get(u)) mat[u][v] = 1;
        }
        return mat;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<Integer> sorted(List<Integer> list) {
        List<Integer> copy = new ArrayList<>(list);
        Collections.sort(copy);
        return copy;
    }

    public static void main(String[] args) {
        // 1. the example graph: n = 5, m = 6, 1-based
        int n = 5;
        int[][] edges = { {1, 2}, {1, 3}, {2, 4}, {3, 4}, {2, 5}, {4, 5} };
        int[][] mat = adjacencyMatrix(n, edges);
        List<List<Integer>> adj = adjacencyList(n, edges);
        check(Arrays.equals(mat[2], new int[]{0, 1, 0, 0, 1, 1}), "row 2 of the matrix");
        check(adj.get(2).equals(List.of(1, 4, 5)) && adj.get(4).equals(List.of(2, 3, 5)), "lists keep input order");
        check(adj.get(0).isEmpty(), "index 0 is an unused placeholder");
        check(storedEntries(adj) == 2 * edges.length && edgesInMatrix(mat) == edges.length, "2E entries, E edges");
        check(Arrays.deepEquals(listToMatrix(adj), mat), "list and matrix describe the same graph");
        for (int u = 1; u <= n; u++) {
            check(neighboursFromMatrix(mat, u).equals(sorted(adj.get(u))), "same neighbours for vertex " + u);
            for (int v = 1; v <= n; v++) check(mat[u][v] == mat[v][u], "undirected matrix is symmetric");
        }

        // 2. the same edges read as directed
        List<List<Integer>> dadj = directedList(n, edges);
        check(storedEntries(dadj) == edges.length, "directed stores each edge once");
        check(dadj.get(2).equals(List.of(4, 5)) && dadj.get(5).isEmpty(), "out-neighbours only");
        int[][] dmat = listToMatrix(dadj);
        check(dmat[1][2] == 1 && dmat[2][1] == 0, "directed matrix is not symmetric");

        // 3. weighted
        int[][] wEdges = { {1, 2, 2}, {1, 3, 3}, {2, 4, 1}, {3, 4, 6} };
        int[][] wmat = weightedMatrix(4, wEdges);
        List<List<int[]>> wadj = weightedList(4, wEdges);
        check(wmat[2][4] == 1 && wmat[4][2] == 1 && wmat[1][4] == 0, "weights in the matrix");
        check(wadj.get(4).size() == 2 && wadj.get(4).get(0)[0] == 2 && wadj.get(4).get(0)[1] == 1, "pair {2, 1} in vertex 4's list");
        check(wadj.get(4).get(1)[0] == 3 && wadj.get(4).get(1)[1] == 6, "pair {3, 6} in vertex 4's list");
        for (int u = 1; u <= 4; u++) {
            for (int[] p : wadj.get(u)) check(wmat[u][p[0]] == p[1], "list and matrix agree on weights");
        }

        // 4. edge cases: no edges, a single vertex
        List<List<Integer>> empty = adjacencyList(3, new int[0][]);
        check(empty.size() == 4 && storedEntries(empty) == 0, "3 isolated vertices");
        check(edgesInMatrix(adjacencyMatrix(3, new int[0][])) == 0, "all-zero matrix");
        check(adjacencyMatrix(1, new int[0][]).length == 2 && adjacencyList(1, new int[0][]).get(1).isEmpty(), "single vertex");

        // 5. a long path: the list stays small, a matrix would need (n + 1)^2 cells
        int big = 100_000;
        int[][] path = new int[big - 1][];
        for (int i = 1; i < big; i++) path[i - 1] = new int[]{i, i + 1};
        List<List<Integer>> padj = adjacencyList(big, path);
        check(storedEntries(padj) == 2 * (big - 1), "2E entries for the path");
        check(padj.get(1).equals(List.of(2)) && padj.get(big).equals(List.of(big - 1)) && padj.get(50).equals(List.of(49, 51)), "path neighbours");
        check((long) (big + 1) * (big + 1) > 10_000_000_000L, "the matrix would need over 10^10 cells");

        System.out.println("OK P2870_GraphRepresentationC");
    }
}
