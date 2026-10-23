import java.util.*;

/** TUF 1222 - Introduction to Graph. Vertices, edges, direction, degree, paths, cycles, weights, connectivity and trees, all on plain edge lists. */
public class P1222_IntroductionToGraph {

    /** Degree of every vertex of an undirected graph: edge {u, v} adds one to u and one to v. O(V + E). */
    static int[] degrees(int n, int[][] edges) {
        int[] deg = new int[n];   // deg[v] = number of edges touching v
        for (int[] e : edges) {   // each undirected edge adds 1 to both ends
            deg[e[0]]++;
            deg[e[1]]++;
        }
        return deg;
    }

    /** In-degree and out-degree of every vertex of a directed graph: edge {u, v} leaves u and enters v. O(V + E). */
    static int[][] inOutDegrees(int n, int[][] edges) {
        int[] in = new int[n], out = new int[n];   // edges arriving at / leaving each vertex
        for (int[] e : edges) {
            out[e[0]]++;                           // the edge leaves e[0]
            in[e[1]]++;                            // and arrives at e[1]
        }
        return new int[][]{in, out};
    }

    /** True when the edge list contains u -> v, or for an undirected graph either orientation. O(E). */
    static boolean hasEdge(int[][] edges, int u, int v, boolean directed) {
        for (int[] e : edges) {                                     // look through the whole edge list
            if (e[0] == u && e[1] == v) return true;                // stored exactly as u -> v
            if (!directed && e[0] == v && e[1] == u) return true;   // undirected: v - u is the same edge
        }
        return false;                                               // no edge joins u and v
    }

    /** A path: consecutive vertices are joined by an edge and no vertex appears twice. */
    static boolean isPath(int n, int[][] edges, int[] seq, boolean directed) {
        if (seq.length == 0) return false;       // a path needs at least one vertex
        boolean[] used = new boolean[n];         // vertices already on the path
        for (int i = 0; i < seq.length; i++) {   // check every vertex of the sequence in order
            if (used[seq[i]]) return false;      // a path never revisits a vertex
            used[seq[i]] = true;
            // each step must follow a real edge
            if (i > 0 && !hasEdge(edges, seq[i - 1], seq[i], directed)) return false;
        }
        return true;                             // distinct vertices, each joined to the next
    }

    /** A cycle: a path whose last vertex has an edge back to the first. Undirected cycles need at least 3 vertices. */
    static boolean isCycle(int n, int[][] edges, int[] seq, boolean directed) {
        int k = seq.length;
        if (k < (directed ? 1 : 3)) return false;              // undirected u-v-u would reuse one edge
        if (!isPath(n, edges, seq, directed)) return false;    // vertices must be distinct and linked
        return hasEdge(edges, seq[k - 1], seq[0], directed);   // the closing edge
    }

    /** Total weight along seq for weighted edges {u, v, w}. Uses the cheapest edge between each pair; -1 if a pair is not adjacent. */
    static long pathWeight(int[][] wEdges, int[] seq, boolean directed) {
        long total = 0;                              // sum of the chosen edge weights
        for (int i = 1; i < seq.length; i++) {       // one step per consecutive pair
            long best = Long.MAX_VALUE;              // lightest edge for this step so far
            for (int[] e : wEdges) {                 // check every edge for this step
                boolean forward = e[0] == seq[i - 1] && e[1] == seq[i];
                boolean backward = !directed && e[0] == seq[i] && e[1] == seq[i - 1];
                if (forward || backward) best = Math.min(best, e[2]);   // parallel edges: keep the cheapest
            }
            if (best == Long.MAX_VALUE) return -1;   // consecutive vertices are not adjacent
            total += best;
        }
        return total;
    }

    /** Most edges a simple graph on n vertices can have: n(n-1) ordered pairs, halved when direction does not matter. */
    static long maxEdges(int n, boolean directed) {
        long pairs = (long) n * (n - 1);       // long: n = 10^5 already overflows int
        return directed ? pairs : pairs / 2;   // undirected: u-v and v-u are one edge
    }

    /** Connected: every vertex can be reached from vertex 0. Spread a "reached" flag across edges until nothing changes. O(V * E). */
    static boolean isConnected(int n, int[][] edges) {
        if (n == 0) return true;                          // an empty graph counts as connected
        boolean[] reached = new boolean[n];               // vertices known to connect to vertex 0
        reached[0] = true;                                // spread outward from vertex 0
        boolean changed = true;                           // did the last sweep reach anything new?
        while (changed) {                                 // repeat sweeps until nothing new is reached
            changed = false;
            for (int[] e : edges) {                       // sweep every edge
                if (reached[e[0]] != reached[e[1]]) {     // one end reached, the other not yet
                    reached[e[0]] = true;
                    reached[e[1]] = true;
                    changed = true;
                }
            }
        }
        for (boolean r : reached) if (!r) return false;   // some vertex was never reached
        return true;                                      // every vertex reached from 0
    }

    /** A tree is a connected graph with no cycle; equivalently, connected with exactly n - 1 edges. */
    static boolean isTree(int n, int[][] edges) {
        return n >= 1 && edges.length == n - 1 && isConnected(n, edges);   // connected and only n - 1 edges
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // 1. the undirected example graph: 0-1, 0-2, 1-2, 1-3, 3-4
        int n = 5;
        int[][] und = { {0, 1}, {0, 2}, {1, 2}, {1, 3}, {3, 4} };
        int[] deg = degrees(n, und);
        check(Arrays.equals(deg, new int[]{2, 3, 2, 2, 1}), "degrees of the example");
        check(Arrays.stream(deg).sum() == 2 * und.length, "handshake lemma: the degree sum is 2E");
        int odd = 0;
        for (int d : deg) if (d % 2 == 1) odd++;
        check(odd == 2, "odd-degree vertices come in pairs");
        check(isPath(n, und, new int[]{0, 1, 3, 4}, false), "0-1-3-4 is a path");
        check(isPath(n, und, new int[]{4, 3, 1, 2, 0}, false), "a path may use every vertex");
        check(!isPath(n, und, new int[]{0, 3}, false), "0 and 3 are not adjacent");
        check(!isPath(n, und, new int[]{0, 1, 2, 0}, false), "a path cannot repeat a vertex");
        check(isCycle(n, und, new int[]{0, 1, 2}, false), "0-1-2 is a triangle");
        check(isCycle(n, und, new int[]{2, 1, 0}, false), "the same triangle walked the other way");
        check(!isCycle(n, und, new int[]{1, 3, 4}, false), "4 has no edge back to 1");
        check(!isCycle(n, und, new int[]{0, 1}, false), "0-1-0 reuses a single edge");
        check(isConnected(n, und) && !isTree(n, und), "connected, but 5 edges on 5 vertices means a cycle");
        int[][] tree = { {0, 1}, {0, 2}, {1, 3}, {3, 4} };
        check(isTree(n, tree), "dropping edge 1-2 leaves a tree");
        check(!isCycle(n, tree, new int[]{0, 1, 2}, false), "and the triangle is gone");

        // 2. a directed graph: 0->1, 1->2, 2->0, 2->3
        int[][] dir = { {0, 1}, {1, 2}, {2, 0}, {2, 3} };
        int[][] io = inOutDegrees(4, dir);
        check(Arrays.equals(io[0], new int[]{1, 1, 1, 1}), "in-degrees");
        check(Arrays.equals(io[1], new int[]{1, 1, 2, 0}), "out-degrees");
        check(Arrays.stream(io[0]).sum() == dir.length && Arrays.stream(io[1]).sum() == dir.length, "in and out sums both equal E");
        check(isPath(4, dir, new int[]{0, 1, 2, 3}, true), "directed path 0->1->2->3");
        check(!isPath(4, dir, new int[]{3, 2}, true), "edge 2->3 cannot be walked backwards");
        check(isPath(4, dir, new int[]{3, 2}, false), "ignoring direction, 3-2 is fine");
        check(isCycle(4, dir, new int[]{0, 1, 2}, true), "0->1->2->0 is a directed cycle");
        check(!isCycle(4, dir, new int[]{0, 2, 1}, true), "the reverse orientation is not");
        check(isCycle(4, dir, new int[]{0, 2, 1}, false), "but it is a cycle once direction is ignored");
        check(isCycle(2, new int[][]{ {0, 1}, {1, 0} }, new int[]{0, 1}, true), "two opposite arcs form a directed 2-cycle");

        // 3. weighted edges {u, v, w}
        int[][] w = { {0, 1, 4}, {1, 2, 1}, {0, 2, 7}, {2, 3, 3} };
        check(pathWeight(w, new int[]{0, 1, 2, 3}, false) == 8, "4 + 1 + 3");
        check(pathWeight(w, new int[]{0, 2, 3}, false) == 10, "fewer edges is not always cheaper");
        check(pathWeight(w, new int[]{3, 2}, false) == 3, "undirected: either orientation");
        check(pathWeight(w, new int[]{3, 2}, true) == -1, "directed: no edge 3->2");
        check(pathWeight(new int[][]{ {0, 1, 4}, {0, 1, 2} }, new int[]{0, 1}, false) == 2, "cheapest of two parallel edges");
        check(pathWeight(w, new int[]{2}, false) == 0, "a single vertex weighs nothing");

        // 4. edge cases: counts, isolated vertices, self-loops
        check(maxEdges(5, false) == 10 && maxEdges(5, true) == 20, "complete graph on 5 vertices");
        check(maxEdges(1, false) == 0 && maxEdges(0, true) == 0, "tiny graphs have no edges");
        check(maxEdges(100_000, false) == 4_999_950_000L, "needs long");
        int[][] none = new int[0][];
        check(Arrays.equals(degrees(3, none), new int[]{0, 0, 0}), "isolated vertices have degree 0");
        check(!isConnected(3, none) && !isTree(3, none), "three isolated vertices are not connected");
        check(isTree(1, none) && isConnected(1, none), "a single vertex is a tree");
        check(!isConnected(4, new int[][]{ {0, 1}, {2, 3} }), "two separate pieces");
        check(!isTree(4, new int[][]{ {0, 1}, {1, 0}, {2, 3} }), "n - 1 edges is not enough if they are not connected");
        check(Arrays.equals(degrees(2, new int[][]{ {1, 1} }), new int[]{0, 2}), "a self-loop adds 2 to its vertex");
        check(isCycle(1, new int[][]{ {0, 0} }, new int[]{0}, true), "a directed self-loop is a cycle of length 1");

        System.out.println("OK P1222_IntroductionToGraph");
    }
}
