import java.util.*;

/** TUF 2823 - MST using Kruskal's algorithm. Undirected edges {u, v, w}. Return the total weight of a minimum spanning tree, or -1 if the graph is disconnected. */
public class P2823_MSTUsingKruskalsAlgo {

    /** Returns a copy of the edge list sorted by weight (the input is left untouched). */
    static int[][] sortedByWeight(int[][] edges) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2]));
        return sorted;
    }

    /** Approach 1: Kruskal with a DFS cycle check: an edge is taken only if its ends are not yet connected in the forest. O(E log E + E * V) time, O(V + E) space. */
    static int bruteForce(int V, int[][] edges) {
        List<List<Integer>> forest = new ArrayList<>();
        for (int i = 0; i < V; i++) forest.add(new ArrayList<>());
        int total = 0, used = 0;
        for (int[] e : sortedByWeight(edges)) {
            if (connected(forest, e[0], e[1], V)) continue;    // adding e would close a cycle
            forest.get(e[0]).add(e[1]);
            forest.get(e[1]).add(e[0]);
            total += e[2];
            used++;
        }
        return used == V - 1 ? total : -1;
    }

    static boolean connected(List<List<Integer>> forest, int s, int t, int V) {
        boolean[] seen = new boolean[V];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(s);
        seen[s] = true;
        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (u == t) return true;
            for (int v : forest.get(u)) {
                if (!seen[v]) {
                    seen[v] = true;
                    stack.push(v);
                }
            }
        }
        return false;
    }

    /** Approach 2: Kruskal with component labels; a merge relabels one whole component. O(E log E + V^2) time, O(V) extra space. */
    static int better(int V, int[][] edges) {
        int[] label = new int[V];                              // label[x]: id of the component containing x
        for (int i = 0; i < V; i++) label[i] = i;
        int total = 0, used = 0;
        for (int[] e : sortedByWeight(edges)) {
            int a = label[e[0]], b = label[e[1]];
            if (a == b) continue;                              // same component already
            for (int i = 0; i < V; i++) if (label[i] == b) label[i] = a;
            total += e[2];
            used++;
        }
        return used == V - 1 ? total : -1;
    }

    /** Disjoint Set Union with union by size and path halving: near-constant time per operation. */
    static final class DisjointSet {
        final int[] parent, size;

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];                 // path halving: skip a level on the way up
                x = parent[x];
            }
            return x;
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;                        // already in the same set
            if (size[ra] < size[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }
            parent[rb] = ra;                                   // hang the smaller tree under the larger
            size[ra] += size[rb];
            return true;
        }
    }

    /** Approach 3: Kruskal with a Disjoint Set Union. O(E log E) time for the sort plus O(E * alpha(V)) for the unions, O(V + E) space. */
    static int optimal(int V, int[][] edges) {
        DisjointSet ds = new DisjointSet(V);
        int total = 0, used = 0;
        for (int[] e : sortedByWeight(edges)) {
            if (used == V - 1) break;                          // tree complete, the rest cannot be used
            if (ds.union(e[0], e[1])) {
                total += e[2];
                used++;
            }
        }
        return used == V - 1 ? total : -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int V, int[][] edges, int expected) {
        check(bruteForce(V, edges) == expected, "bruteForce got " + bruteForce(V, edges) + " expected " + expected);
        check(better(V, edges) == expected, "better got " + better(V, edges) + " expected " + expected);
        check(optimal(V, edges) == expected, "optimal got " + optimal(V, edges) + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(3, new int[][]{{0, 1, 5}, {1, 2, 3}, {0, 2, 1}}, 4);
        verify(5, new int[][]{{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}}, 16);
        verify(1, new int[][]{}, 0);                                                         // single vertex
        verify(2, new int[][]{{0, 1, 10}, {0, 1, 4}}, 4);                                    // parallel edges
        verify(4, new int[][]{{0, 1, 1}, {2, 3, 1}}, -1);                                    // disconnected
        verify(4, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 0, 1}, {0, 2, 1}}, 3);    // ties everywhere
        verify(3, new int[][]{{0, 1, -2}, {1, 2, 3}, {0, 2, 2}}, 0);                         // negative weights are fine for MST
        verify(3, new int[][]{{0, 0, 1}, {0, 1, 2}, {1, 2, 2}}, 4);                          // self loop is ignored

        // larger seeded graph (connected through a random path): all three must agree
        Random rnd = new Random(2823);
        int V = 300;
        List<int[]> list = new ArrayList<>();
        for (int i = 1; i < V; i++) list.add(new int[]{rnd.nextInt(i), i, rnd.nextInt(1000)});
        for (int i = 0; i < 3000; i++) list.add(new int[]{rnd.nextInt(V), rnd.nextInt(V), rnd.nextInt(1000)});
        int[][] edges = list.toArray(new int[0][]);
        int ref = optimal(V, edges);
        check(bruteForce(V, edges) == ref, "bruteForce differs on random graph");
        check(better(V, edges) == ref, "better differs on random graph");
        System.out.println("OK P2823_MSTUsingKruskalsAlgo");
    }
}
