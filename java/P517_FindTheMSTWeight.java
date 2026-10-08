import java.util.*;

/** TUF 517 - Find the MST weight (Kruskal's algorithm). Undirected weighted graph on 0..n-1; MST weight, or -1 if disconnected. */
public class P517_FindTheMSTWeight {

    /** Approach 1: Kruskal, checking "would this edge close a cycle?" with a DFS over the edges taken so far. O(E log E + E * V) time. */
    static int kruskalWithDfs(int n, int[][] edges) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2]));
        List<List<Integer>> forest = new ArrayList<>();
        for (int i = 0; i < n; i++) forest.add(new ArrayList<>());
        int total = 0, used = 0;
        for (int[] e : sorted) {
            if (connected(forest, e[0], e[1])) continue; // already joined: this edge would close a cycle
            forest.get(e[0]).add(e[1]);
            forest.get(e[1]).add(e[0]);
            total += e[2];
            used++;
        }
        return used == n - 1 ? total : -1;
    }

    static boolean connected(List<List<Integer>> forest, int s, int t) {
        boolean[] seen = new boolean[forest.size()];
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

    /** Approach 2: Kruskal with a disjoint set (union by size + path compression). O(E log E) time, O(V) extra space. */
    static int kruskalWithDsu(int n, int[][] edges) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2]));
        DSU dsu = new DSU(n);
        int total = 0, used = 0;
        for (int[] e : sorted) {
            if (!dsu.union(e[0], e[1])) continue;        // same component: skip
            total += e[2];
            if (++used == n - 1) break;                  // a spanning tree is complete
        }
        return used == n - 1 ? total : -1;
    }

    static class DSU {
        final int[] parent, size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] == x) return x;
            return parent[x] = find(parent[x]);
        }

        /** Merge the sets of a and b; false if they were already the same set. */
        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;
            if (size[ra] < size[rb]) {
                int tmp = ra;
                ra = rb;
                rb = tmp;
            }
            parent[rb] = ra;
            size[ra] += size[rb];
            return true;
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
