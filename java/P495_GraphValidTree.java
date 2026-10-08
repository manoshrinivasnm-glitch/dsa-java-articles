import java.util.*;

/** TUF 495 - Graph Valid Tree. Do the n vertices and undirected edges form a single tree? */
public class P495_GraphValidTree {

    /** Approach 1: a tree is connected and every edge is a bridge. Test connectivity, then remove each edge in turn. O(E * (n + E)) time, O(n + E) space. */
    static boolean bruteForce(int n, int[][] edges) {
        if (countReachable(n, edges, -1, 0) != n) return false;   // must be connected
        for (int skip = 0; skip < edges.length; skip++) {
            int u = edges[skip][0], v = edges[skip][1];
            if (u == v) return false;                     // a self-loop is a cycle
            if (reachableWithout(n, edges, skip, u, v)) return false;   // another route: the edge sits on a cycle
        }
        return true;
    }

    /** Number of vertices reachable from start when edge number skip is ignored. */
    static int countReachable(int n, int[][] edges, int skip, int start) {
        boolean[] seen = bfs(n, edges, skip, start);
        int count = 0;
        for (boolean b : seen) if (b) count++;
        return count;
    }

    static boolean reachableWithout(int n, int[][] edges, int skip, int from, int to) {
        return bfs(n, edges, skip, from)[to];
    }

    static boolean[] bfs(int n, int[][] edges, int skip, int start) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int i = 0; i < edges.length; i++) {
            if (i == skip) continue;
            adj.get(edges[i][0]).add(edges[i][1]);
            adj.get(edges[i][1]).add(edges[i][0]);
        }
        boolean[] seen = new boolean[n];
        Deque<Integer> queue = new ArrayDeque<>();
        seen[start] = true;
        queue.add(start);
        while (!queue.isEmpty()) {
            int x = queue.poll();
            for (int y : adj.get(x)) {
                if (!seen[y]) {
                    seen[y] = true;
                    queue.add(y);
                }
            }
        }
        return seen;
    }

    /** Approach 2: exactly n - 1 edges plus connectivity, checked with one BFS. O(n + E) time, O(n + E) space. */
    static boolean optimalBfs(int n, int[][] edges) {
        if (edges.length != n - 1) return false;          // a tree on n vertices has exactly n - 1 edges
        return countReachable(n, edges, -1, 0) == n;
    }

    /** Approach 3: union-find. Exactly n - 1 edges, and no edge may join two vertices already connected. O(n + E * α(n)) time, O(n) space. */
    static boolean optimalUnionFind(int n, int[][] edges) {
        if (edges.length != n - 1) return false;
        int[] parent = new int[n], size = new int[n];
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        for (int[] e : edges) {
            int a = find(parent, e[0]), b = find(parent, e[1]);
            if (a == b) return false;                     // u and v already connected: this edge closes a cycle
            if (size[a] < size[b]) { int t = a; a = b; b = t; }
            parent[b] = a;                                // union by size
            size[a] += size[b];
        }
        return true;                                      // n - 1 merges leave exactly one set
    }

    static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];                // path halving
            x = parent[x];
        }
        return x;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] edges, boolean expected) {
        check(bruteForce(n, edges) == expected, "bruteForce n=" + n + " " + Arrays.deepToString(edges));
        check(optimalBfs(n, edges) == expected, "optimalBfs n=" + n + " " + Arrays.deepToString(edges));
        check(optimalUnionFind(n, edges) == expected, "optimalUnionFind n=" + n + " " + Arrays.deepToString(edges));
    }

    public static void main(String[] args) {
        verify(5, new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 4}}, true);           // LeetCode example 1
        verify(5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {1, 3}, {1, 4}}, false);  // LeetCode example 2: cycle 1-2-3
        verify(1, new int[][]{}, true);                                         // single vertex is a tree
        verify(2, new int[][]{}, false);                                        // two isolated vertices
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 0}}, false);                  // n - 1 edges, but a cycle and an isolated vertex
        verify(4, new int[][]{{0, 1}, {2, 3}}, false);                          // forest of two trees
        verify(4, new int[][]{{3, 2}, {2, 1}, {1, 0}}, true);                   // path given in reverse
        verify(2, new int[][]{{0, 0}}, false);                                  // self-loop
        System.out.println("OK P495_GraphValidTree");
    }
}
