import java.util.*;

/** TUF 517 - Find the MST weight (Kruskal's algorithm). Undirected weighted graph on 0..n-1; MST weight, or -1 if disconnected. */
public class P517_FindTheMSTWeight {

    /** Approach 1: Kruskal, checking "would this edge close a cycle?" with a DFS over the edges taken so far. O(E log E + E * V) time. */
    static int kruskalWithDfs(int n, int[][] edges) {
        int[][] sorted = edges.clone();                  // copy, so the caller's array keeps its order
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2]));   // lightest edges first
        List<List<Integer>> forest = new ArrayList<>();   // adjacency lists of the edges taken so far
        for (int i = 0; i < n; i++) forest.add(new ArrayList<>());
        int total = 0, used = 0;                         // MST weight and number of edges taken
        for (int[] e : sorted) {                         // try every edge from light to heavy
            if (connected(forest, e[0], e[1])) continue; // already joined: this edge would close a cycle
            forest.get(e[0]).add(e[1]);                  // take the edge: store both directions
            forest.get(e[1]).add(e[0]);
            total += e[2];                               // pay for it
            used++;
        }
        return used == n - 1 ? total : -1;               // n - 1 edges means every vertex is connected
    }

    static boolean connected(List<List<Integer>> forest, int s, int t) {
        boolean[] seen = new boolean[forest.size()];     // vertices already visited by this DFS
        Deque<Integer> stack = new ArrayDeque<>();       // DFS stack starting at s
        stack.push(s);
        seen[s] = true;
        while (!stack.isEmpty()) {
            int u = stack.pop();                         // next vertex to expand
            if (u == t) return true;                     // found a path s -> t
            for (int v : forest.get(u)) {                // walk every taken edge out of u
                if (!seen[v]) {                          // first visit to v
                    seen[v] = true;
                    stack.push(v);
                }
            }
        }
        return false;                                    // t cannot be reached from s
    }

    /** Approach 2: Kruskal with a disjoint set (union by size + path compression). O(E log E) time, O(V) extra space. */
    static int kruskalWithDsu(int n, int[][] edges) {
        int[][] sorted = edges.clone();                  // copy, so the caller's array keeps its order
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2]));   // lightest edges first
        DSU dsu = new DSU(n);                            // one set per connected component
        int total = 0, used = 0;                         // MST weight and number of edges taken
        for (int[] e : sorted) {                         // try every edge from light to heavy
            if (!dsu.union(e[0], e[1])) continue;        // same component: skip
            total += e[2];                               // the edge joins two components: take it
            if (++used == n - 1) break;                  // a spanning tree is complete
        }
        return used == n - 1 ? total : -1;               // fewer than n - 1 edges: graph is disconnected
    }

    static class DSU {
        final int[] parent, size;                        // parent links; size[r] = elements in root r's set

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {                // every vertex starts as its own component
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] == x) return x;                // x is the root
            return parent[x] = find(parent[x]);          // path compression: point straight at the root
        }

        /** Merge the sets of a and b; false if they were already the same set. */
        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);              // roots of the two sets
            if (ra == rb) return false;                  // already in the same set
            if (size[ra] < size[rb]) {                   // keep ra as the root of the larger set
                int tmp = ra;
                ra = rb;
                rb = tmp;
            }
            parent[rb] = ra;                             // hang the smaller tree under the larger one
            size[ra] += size[rb];                        // ra's set absorbs rb's elements
            return true;                                 // two sets merged into one
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] edges, int expected) {
        int a = kruskalWithDfs(n, edges), b = kruskalWithDsu(n, edges);
        check(a == expected && b == expected, "n " + n + ": expected " + expected + " got " + a + " " + b);
    }

    public static void main(String[] args) {
        verify(3, new int[][]{{0, 1, 5}, {1, 2, 3}, {0, 2, 1}}, 4);
        verify(6, new int[][]{{5, 4, 9}, {5, 1, 4}, {4, 1, 1}, {4, 3, 5}, {1, 2, 2}, {2, 3, 3}, {2, 0, 7}, {1, 0, 4}}, 14);   // 4-1, 1-2, 2-3, 1-0, 5-1
        verify(5, new int[][]{{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}}, 16);
        verify(1, new int[0][], 0);                      // single vertex
        verify(2, new int[][]{{0, 1, 7}, {0, 1, 3}, {1, 1, 0}}, 3);   // parallel edges and a self-loop
        verify(4, new int[][]{{0, 1, 1}, {2, 3, 1}}, -1);// disconnected
        verify(4, new int[][]{{0, 1, -2}, {1, 2, 5}, {2, 3, -1}, {3, 0, 4}}, 1);   // negative weights are fine for MST

        Random rnd = new Random(517);                    // seeded cross-check
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(8);
            List<int[]> list = new ArrayList<>();
            for (int u = 0; u < n; u++)
                for (int v = u + 1; v < n; v++)
                    if (rnd.nextInt(2) == 0) list.add(new int[]{u, v, 1 + rnd.nextInt(9)});
            int[][] edges = list.toArray(new int[0][]);
            verify(n, edges, kruskalWithDfs(n, edges));
        }
        System.out.println("OK P517_FindTheMSTWeight");
    }
}
