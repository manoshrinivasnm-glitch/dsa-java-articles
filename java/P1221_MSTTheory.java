import java.util.*;

/** TUF 1221 - MST theory. Spanning trees, minimum spanning trees, the cut and cycle properties, checked by brute force on small graphs. */
public class P1221_MSTTheory {

    /** A set of edges (bit i of mask = edge i) is a spanning tree when it has n - 1 edges and connects all n vertices. */
    static boolean isSpanningTree(int n, int[][] edges, int mask) {
        if (Integer.bitCount(mask) != n - 1) return false;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int i = 0; i < edges.length; i++) {
            if ((mask >> i & 1) == 0) continue;
            adj.get(edges[i][0]).add(edges[i][1]);
            adj.get(edges[i][1]).add(edges[i][0]);
        }
        boolean[] seen = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        seen[0] = true;
        int reached = 1;
        while (!stack.isEmpty()) {
            int u = stack.pop();
            for (int v : adj.get(u)) {
                if (!seen[v]) {
                    seen[v] = true;
                    reached++;
                    stack.push(v);
                }
            }
        }
        return reached == n;                             // n - 1 edges + connected => no cycle
    }

    static int weight(int[][] edges, int mask) {
        int total = 0;
        for (int i = 0; i < edges.length; i++) if ((mask >> i & 1) == 1) total += edges[i][2];
        return total;
    }

    /** How many spanning trees exist (try every subset of edges). O(2^E * (V + E)). */
    static int countSpanningTrees(int n, int[][] edges) {
        int count = 0;
        for (int mask = 0; mask < (1 << edges.length); mask++) if (isSpanningTree(n, edges, mask)) count++;
        return count;
    }

    /** Brute-force MST: the lightest spanning subset (first one found on ties), or -1 if the graph is disconnected. */
    static int bruteForceMst(int n, int[][] edges) {
        int best = -1;
        for (int mask = 0; mask < (1 << edges.length); mask++) {
            if (isSpanningTree(n, edges, mask) && (best == -1 || weight(edges, mask) < weight(edges, best))) best = mask;
        }
        return best;
    }

    /** How many different spanning trees reach the minimum weight. */
    static int countMinimumSpanningTrees(int n, int[][] edges) {
        int best = bruteForceMst(n, edges);
        if (best == -1) return 0;
        int count = 0;
        for (int mask = 0; mask < (1 << edges.length); mask++) {
            if (isSpanningTree(n, edges, mask) && weight(edges, mask) == weight(edges, best)) count++;
        }
        return count;
    }

    /** Cut property: index of the lightest edge with exactly one endpoint inside the set S. */
    static int lightestCrossingEdge(int[][] edges, boolean[] inS) {
        int best = -1;
        for (int i = 0; i < edges.length; i++) {
            boolean crosses = inS[edges[i][0]] != inS[edges[i][1]];
            if (crosses && (best == -1 || edges[i][2] < edges[best][2])) best = i;
        }
        return best;
    }

    /** Cycle property: index of the heaviest edge among the given edge indices (which form a cycle). */
    static int heaviestOnCycle(int[][] edges, int[] cycle) {
        int best = cycle[0];
        for (int i : cycle) if (edges[i][2] > edges[best][2]) best = i;
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // Worked example: 5 vertices, 7 edges with distinct weights.
        int[][] g = {{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}};
        int mst = bruteForceMst(5, g);
        check(weight(g, mst) == 16, "MST weight 16");
        check(mst == (1 << 0 | 1 << 1 | 1 << 2 | 1 << 4), "MST = {0-1, 0-3, 1-2, 1-4}");
        check(countSpanningTrees(5, g) == 21, "the example graph has 21 spanning trees");
        check(countMinimumSpanningTrees(5, g) == 1, "distinct weights => unique MST");
        check(isSpanningTree(5, g, 1 << 0 | 1 << 2 | 1 << 5 | 1 << 6), "0-1, 1-2, 2-4, 3-4 is a spanning tree");
        check(weight(g, 1 << 0 | 1 << 2 | 1 << 5 | 1 << 6) == 21, "... of weight 21");
        check(!isSpanningTree(5, g, 1 << 0 | 1 << 1 | 1 << 3 | 1 << 4), "0-1, 0-3, 1-3 closes a cycle, so vertex 2 is left out");
        check(!isSpanningTree(5, g, 1 << 0 | 1 << 2 | 1 << 4), "3 edges cannot span 5 vertices");

        boolean[] s = {true, true, false, false, false};  // cut S = {0, 1}
        int light = lightestCrossingEdge(g, s);
        check(light == 2 && (mst >> light & 1) == 1, "lightest edge across {0,1} is 1-2 and it is in the MST");
        int heavy = heaviestOnCycle(g, new int[]{0, 1, 3});
        check(heavy == 3 && (mst >> heavy & 1) == 0, "heaviest edge on cycle 0-1-3 is 1-3 and it is not in the MST");

        // Cayley: the complete graph K_n has n^(n-2) spanning trees.
        int[][] k4 = {{0, 1, 1}, {0, 2, 1}, {0, 3, 1}, {1, 2, 1}, {1, 3, 1}, {2, 3, 1}};
        check(countSpanningTrees(4, k4) == 16, "K4 has 4^2 = 16 spanning trees");
        List<int[]> k5 = new ArrayList<>();
        for (int u = 0; u < 5; u++) for (int v = u + 1; v < 5; v++) k5.add(new int[]{u, v, 1});
        check(countSpanningTrees(5, k5.toArray(new int[0][])) == 125, "K5 has 5^3 = 125 spanning trees");

        // Equal weights: a square of weight-1 edges has 4 MSTs (drop any one side).
        int[][] square = {{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 0, 1}};
        check(countMinimumSpanningTrees(4, square) == 4 && weight(square, bruteForceMst(4, square)) == 3, "square: 4 MSTs of weight 3");

        // MST is not the shortest-path tree: triangle a-b 2, b-c 2, a-c 3.
        int[][] tri = {{0, 1, 2}, {1, 2, 2}, {0, 2, 3}};
        check(bruteForceMst(3, tri) == (1 << 0 | 1 << 1) && weight(tri, bruteForceMst(3, tri)) == 4, "triangle MST = {a-b, b-c}");

        // Edge cases: single vertex (empty tree), disconnected graph (no spanning tree).
        check(bruteForceMst(1, new int[0][]) == 0 && countSpanningTrees(1, new int[0][]) == 1, "single vertex");
        check(bruteForceMst(4, new int[][]{{0, 1, 1}, {2, 3, 1}}) == -1, "disconnected graph has no spanning tree");
        System.out.println("OK P1221_MSTTheory");
    }
}
