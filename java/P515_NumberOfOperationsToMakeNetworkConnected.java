import java.util.*;

/** TUF 515 - Number of operations to make network connected. Minimum cable moves to connect all n computers, or -1. */
public class P515_NumberOfOperationsToMakeNetworkConnected {

    /** Approach 1: count connected components with an iterative DFS. O(n + m) time, O(n + m) space. */
    static int dfsComponents(int n, int[][] connections) {
        if (connections.length < n - 1) return -1;          // fewer than n - 1 cables can never connect n machines
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] c : connections) {
            adj.get(c[0]).add(c[1]);
            adj.get(c[1]).add(c[0]);
        }
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        int components = 0;
        for (int s = 0; s < n; s++) {
            if (visited[s]) continue;
            components++;
            visited[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (int v : adj.get(u)) {
                    if (!visited[v]) {
                        visited[v] = true;
                        stack.push(v);
                    }
                }
            }
        }
        return components - 1;
    }

    /** Disjoint Set Union with union by size and path compression. */
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
            if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
            return parent[x];
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;                         // already in the same set
            if (size[ra] < size[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }
            parent[rb] = ra;                                    // hang the smaller tree under the larger
            size[ra] += size[rb];
            return true;
        }
    }

    /** Approach 2: Disjoint Set; count components and redundant cables in one pass. O(m * alpha(n)) time, O(n) space. */
    static int disjointSet(int n, int[][] connections) {
        DisjointSet ds = new DisjointSet(n);
        int components = n, extra = 0;
        for (int[] c : connections) {
            if (ds.union(c[0], c[1])) components--;            // cable joined two groups
            else extra++;                                      // cable inside one group: free to move
        }
        int needed = components - 1;
        return extra >= needed ? needed : -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] connections, int expected) {
        int a = dfsComponents(n, connections), b = disjointSet(n, connections);
        check(a == expected, "dfsComponents n=" + n + " got " + a + " expected " + expected);
        check(b == expected, "disjointSet n=" + n + " got " + b + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(4, new int[][]{ {0, 1}, {0, 2}, {1, 2} }, 1);
        verify(6, new int[][]{ {0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3} }, 2);
        verify(6, new int[][]{ {0, 1}, {0, 2}, {0, 3}, {1, 2} }, -1);            // only 4 cables for 6 machines
        verify(1, new int[][]{}, 0);                                               // single machine
        verify(3, new int[][]{}, -1);                                              // no cables at all
        verify(5, new int[][]{ {0, 1}, {1, 2}, {2, 3}, {3, 4} }, 0);              // already one tree
        // complete graph on 0..4 (10 cables, 6 redundant) plus isolated 5..9: 6 components, 5 moves
        List<int[]> k5 = new ArrayList<>();
        for (int i = 0; i < 5; i++) for (int j = i + 1; j < 5; j++) k5.add(new int[]{i, j});
        verify(10, k5.toArray(new int[0][]), 5);
        // seeded cross-check: the two methods must agree, and match the m >= n - 1 rule
        Random rnd = new Random(515);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(12), m = rnd.nextInt(16);
            Set<Long> used = new HashSet<>();
            List<int[]> es = new ArrayList<>();
            for (int k = 0; k < m && n > 1; k++) {
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                if (u == v || !used.add((long) Math.min(u, v) * 100 + Math.max(u, v))) continue;
                es.add(new int[]{u, v});
            }
            int[][] arr = es.toArray(new int[0][]);
            check(dfsComponents(n, arr) == disjointSet(n, arr), "random mismatch at trial " + t);
        }
        System.out.println("OK P515_NumberOfOperationsToMakeNetworkConnected");
    }
}
