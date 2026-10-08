import java.util.*;

/** TUF 2871 - Graph Representation in Java. The usual Java containers for a graph, how to build each, and the traps specific to Java. */
public class P2871_GraphRepresentationJava {

    /** The everyday form: ArrayList<ArrayList<Integer>>, one inner list per vertex 0..n-1. */
    static ArrayList<ArrayList<Integer>> listOfLists(int n, int[][] edges, boolean directed) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());   // every inner list must exist before use
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            if (!directed) adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    /** An array of lists. new ArrayList<Integer>[n] does not compile, so create a raw array and suppress the unchecked warning. */
    @SuppressWarnings("unchecked")
    static List<Integer>[] arrayOfLists(int n, int[][] edges, boolean directed) {
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] e : edges) {
            adj[e[0]].add(e[1]);
            if (!directed) adj[e[1]].add(e[0]);
        }
        return adj;
    }

    /** Compact int[][]: count degrees, allocate exact-size rows, then fill. No boxing, but the graph is fixed once built. */
    static int[][] compact(int n, int[][] edges, boolean directed) {
        int[] deg = new int[n];
        for (int[] e : edges) {
            deg[e[0]]++;
            if (!directed) deg[e[1]]++;
        }
        int[][] adj = new int[n][];
        for (int i = 0; i < n; i++) adj[i] = new int[deg[i]];
        int[] fill = new int[n];                                  // next free slot in each row
        for (int[] e : edges) {
            adj[e[0]][fill[e[0]]++] = e[1];
            if (!directed) adj[e[1]][fill[e[1]]++] = e[0];
        }
        return adj;
    }

    /** HashMap of lists: for labels that are not 0..n-1, such as large ids. Only vertices that touch an edge appear. */
    static Map<Integer, List<Integer>> mapOfLists(int[][] edges, boolean directed) {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (int[] e : edges) {
            adj.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
            List<Integer> back = adj.computeIfAbsent(e[1], k -> new ArrayList<>());   // keeps sinks as keys too
            if (!directed) back.add(e[0]);
        }
        return adj;
    }

    /** boolean[][] matrix: O(1) edge test, O(n^2) memory. */
    static boolean[][] matrix(int n, int[][] edges, boolean directed) {
        boolean[][] mat = new boolean[n][n];
        for (int[] e : edges) {
            mat[e[0]][e[1]] = true;
            if (!directed) mat[e[1]][e[0]] = true;
        }
        return mat;
    }

    /** A weighted edge as a record instead of an int[] pair. A nested record is implicitly static. */
    record Edge(int to, int weight) {}

    static List<List<Edge>> weighted(int n, int[][] wEdges, boolean directed) {
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : wEdges) {
            adj.get(e[0]).add(new Edge(e[1], e[2]));
            if (!directed) adj.get(e[1]).add(new Edge(e[0], e[2]));
        }
        return adj;
    }

    /** Remove one undirected edge. remove(Integer.valueOf(v)) removes the value v; remove(v) would remove index v. */
    static void removeEdge(List<? extends List<Integer>> adj, int u, int v) {
        adj.get(u).remove(Integer.valueOf(v));
        adj.get(v).remove(Integer.valueOf(u));
    }

    /** WRONG on purpose: Arrays.fill stores the SAME list object in every slot, so all vertices share one neighbour list. */
    @SuppressWarnings("unchecked")
    static List<Integer>[] sharedListBug(int n, int[][] edges) {
        List<Integer>[] adj = new List[n];
        Arrays.fill(adj, new ArrayList<Integer>());
        for (int[] e : edges) {
            adj[e[0]].add(e[1]);
            adj[e[1]].add(e[0]);
        }
        return adj;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Canonical form for comparisons: one sorted neighbour list per vertex 0..n-1. */
    static List<List<Integer>> sortedRows(List<? extends List<Integer>> adj) {
        List<List<Integer>> out = new ArrayList<>();
        for (List<Integer> row : adj) {
            List<Integer> copy = new ArrayList<>(row);
            Collections.sort(copy);
            out.add(copy);
        }
        return out;
    }

    static List<List<Integer>> fromCompact(int[][] adj) {
        List<List<Integer>> out = new ArrayList<>();
        for (int[] row : adj) {
            List<Integer> list = new ArrayList<>();
            for (int v : row) list.add(v);
            out.add(list);
        }
        return sortedRows(out);
    }

    static List<List<Integer>> fromMatrix(boolean[][] mat) {
        List<List<Integer>> out = new ArrayList<>();
        for (boolean[] row : mat) {
            List<Integer> list = new ArrayList<>();
            for (int v = 0; v < row.length; v++) if (row[v]) list.add(v);
            out.add(list);
        }
        return out;
    }

    static List<List<Integer>> fromMap(Map<Integer, List<Integer>> map, int n) {
        List<List<Integer>> out = new ArrayList<>();
        for (int i = 0; i < n; i++) out.add(map.getOrDefault(i, new ArrayList<>()));
        return sortedRows(out);
    }

    static List<List<Integer>> fromWeighted(List<List<Edge>> adj) {
        List<List<Integer>> out = new ArrayList<>();
        for (List<Edge> row : adj) {
            List<Integer> list = new ArrayList<>();
            for (Edge e : row) list.add(e.to());
            out.add(list);
        }
        return sortedRows(out);
    }

    /** Every representation must describe the same graph. */
    static void verifyAll(int n, int[][] edges, boolean directed, List<List<Integer>> expected) {
        int[][] wEdges = new int[edges.length][];
        for (int i = 0; i < edges.length; i++) wEdges[i] = new int[]{edges[i][0], edges[i][1], 1};
        check(sortedRows(listOfLists(n, edges, directed)).equals(expected), "listOfLists");
        check(sortedRows(Arrays.asList(arrayOfLists(n, edges, directed))).equals(expected), "arrayOfLists");
        check(fromCompact(compact(n, edges, directed)).equals(expected), "compact");
        check(fromMap(mapOfLists(edges, directed), n).equals(expected), "mapOfLists");
        check(fromMatrix(matrix(n, edges, directed)).equals(expected), "matrix");
        check(fromWeighted(weighted(n, wEdges, directed)).equals(expected), "weighted");
    }

    public static void main(String[] args) {
        // 1. undirected example: 0-1, 0-2, 1-3, 2-3, 3-4
        int[][] edges = { {0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4} };
        verifyAll(5, edges, false, List.of(List.of(1, 2), List.of(0, 3), List.of(0, 3), List.of(1, 2, 4), List.of(3)));

        // 2. the same edges, directed
        verifyAll(5, edges, true, List.of(List.of(1, 2), List.of(3), List.of(3), List.of(4), List.of()));

        // 3. no edges, and a single vertex
        verifyAll(4, new int[0][], false, List.of(List.of(), List.of(), List.of(), List.of()));
        verifyAll(1, new int[0][], true, List.of(List.of()));

        // 4. insertion order is kept by the list-based forms (not sorted)
        int[][] star = { {0, 3}, {0, 1}, {0, 2} };
        check(listOfLists(4, star, false).get(0).equals(List.of(3, 1, 2)), "listOfLists keeps input order");
        check(Arrays.equals(compact(4, star, false)[0], new int[]{3, 1, 2}), "compact keeps input order");
        check(fromMatrix(matrix(4, star, false)).get(0).equals(List.of(1, 2, 3)), "a matrix row scan comes out sorted");

        // 5. map with labels far outside 0..n-1
        Map<Integer, List<Integer>> m = mapOfLists(new int[][]{ {1_000_000, 7}, {7, 42} }, false);
        check(m.size() == 3 && m.get(7).equals(List.of(1_000_000, 42)) && m.get(42).equals(List.of(7)), "sparse labels");
        Map<Integer, List<Integer>> dm = mapOfLists(new int[][]{ {5, 9} }, true);
        check(dm.get(5).equals(List.of(9)) && dm.get(9).isEmpty(), "directed sink is still a key");

        // 6. weighted record equality
        List<List<Edge>> w = weighted(3, new int[][]{ {0, 1, 5}, {1, 2, 8} }, false);
        check(w.get(1).equals(List.of(new Edge(0, 5), new Edge(2, 8))), "records compare by value");
        check(w.get(2).get(0).weight() == 8 && w.get(0).get(0).to() == 1, "record accessors");

        // 7. remove(Object) versus remove(int)
        ArrayList<ArrayList<Integer>> adj = listOfLists(5, edges, false);
        removeEdge(adj, 3, 1);
        check(adj.get(3).equals(List.of(2, 4)) && adj.get(1).equals(List.of(0)), "edge 1-3 removed by value");
        List<Integer> row = new ArrayList<>(List.of(4, 0, 2));
        row.remove(0);                                            // index 0, i.e. the value 4
        check(row.equals(List.of(0, 2)), "remove(int) removes by index");

        // 8. the shared-list bug
        List<Integer>[] bad = sharedListBug(3, new int[][]{ {0, 1} });
        check(bad[0] == bad[2] && bad[2].size() == 2, "vertex 2 sees neighbours it does not have");
        List<Integer>[] good = arrayOfLists(3, new int[][]{ {0, 1} }, false);
        check(good[0] != good[2] && good[2].isEmpty(), "separate lists per vertex");

        System.out.println("OK P2871_GraphRepresentationJava");
    }
}
