import java.util.*;

/** TUF 501 - Detect a cycle in an undirected graph. Return true if the (possibly disconnected) graph has a cycle. */
public class P501_DetectACycleInAnUndirectedGraph {

    /** Approach 1: for every edge u-v, check whether u still reaches v without that edge. O(E * (V + E)) time, O(V) space. */
    static boolean bruteForce(int V, List<List<Integer>> adj) {
        for (int u = 0; u < V; u++) {                    // try every vertex as one end of an edge
            for (int v : adj.get(u)) {
                if (u < v && reachableWithoutEdge(adj, u, v)) return true;   // u < v: test each edge once
            }
        }
        return false;
    }

    static boolean reachableWithoutEdge(List<List<Integer>> adj, int src, int dst) {
        boolean[] vis = new boolean[adj.size()];         // vertices this search has reached
        Deque<Integer> stack = new ArrayDeque<>();       // DFS stack, so no recursion depth issues
        stack.push(src);                                 // start the search from one end of the edge
        vis[src] = true;
        while (!stack.isEmpty()) {
            int x = stack.pop();
            for (int y : adj.get(x)) {
                if (x == src && y == dst) continue;      // the edge under test is not allowed
                if (y == dst) return true;               // dst reached another way: edge is on a cycle
                if (!vis[y]) { vis[y] = true; stack.push(y); }   // first visit: mark it and explore it later
            }
        }
        return false;
    }

    /** Approach 2: DFS that remembers the parent; a visited neighbour other than the parent closes a cycle. O(V + E) time, O(V) space. */
    static boolean dfs(int V, List<List<Integer>> adj) {
        boolean[] vis = new boolean[V];                  // vis[x] = x was reached by some earlier search
        for (int s = 0; s < V; s++) {                    // start a fresh search in each unvisited component
            if (!vis[s] && dfsFrom(s, -1, adj, vis)) return true;   // one call per component
        }
        return false;
    }

    static boolean dfsFrom(int node, int parent, List<List<Integer>> adj, boolean[] vis) {
        vis[node] = true;                                // mark on entry, before looking at neighbours
        for (int nb : adj.get(node)) {
            if (!vis[nb]) {                              // unvisited: go deeper with node as parent
                if (dfsFrom(nb, node, adj, vis)) return true;   // a cycle found below: pass it up
            } else if (nb != parent) {                   // visited and not where we came from...
                return true;                             // reached an already visited node by a second route
            }
        }
        return false;
    }

    /** Approach 3: the same parent check with BFS, carrying {node, parent} pairs in the queue. O(V + E) time, O(V) space. */
    static boolean bfs(int V, List<List<Integer>> adj) {
        boolean[] vis = new boolean[V];                  // vis[x] = x was already queued
        for (int s = 0; s < V; s++) {
            if (vis[s]) continue;                        // already covered by an earlier component's BFS
            Deque<int[]> q = new ArrayDeque<>();         // queue of {node, parent it was reached from}
            q.offer(new int[]{s, -1});                   // the start vertex has no parent
            vis[s] = true;
            while (!q.isEmpty()) {
                int[] cur = q.poll();
                int node = cur[0], parent = cur[1];      // node to expand and the vertex we came from
                for (int nb : adj.get(node)) {
                    if (!vis[nb]) {                      // first time seen: mark now so it is queued once
                        vis[nb] = true;
                        q.offer(new int[]{nb, node});    // remember node as nb's parent
                    } else if (nb != parent) {           // seen before and not our parent -> a second route
                        return true;                     // two BFS paths meet, so the graph has a cycle
                    }
                }
            }
        }
        return false;
    }

    /** Approach 4: union-find; an edge whose endpoints are already in one set closes a cycle. O(E * alpha(V)) time, O(V) space. */
    static boolean unionFind(int V, List<List<Integer>> adj) {
        int[] root = new int[V];                         // root[i] = parent link in the union-find forest
        for (int i = 0; i < V; i++) root[i] = i;         // every vertex starts as its own component
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {
                if (u > v) continue;                     // each undirected edge once
                int ru = find(root, u), rv = find(root, v);   // representatives of u's and v's components
                if (ru == rv) return true;               // u and v were already connected
                root[ru] = rv;                           // merge the two components
            }
        }
        return false;
    }

    static int find(int[] root, int x) {
        while (root[x] != x) {                           // climb until we hit the component's representative
            root[x] = root[root[x]];                     // path halving keeps the trees shallow
            x = root[x];
        }
        return x;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> build(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }
        return adj;
    }

    static void verify(int V, int[][] edges, boolean expected) {
        List<List<Integer>> adj = build(V, edges);
        String tag = "V=" + V + " edges=" + Arrays.deepToString(edges);
        check(bruteForce(V, adj) == expected, "bruteForce " + tag);
        check(dfs(V, adj) == expected, "dfs " + tag);
        check(bfs(V, adj) == expected, "bfs " + tag);
        check(unionFind(V, adj) == expected, "unionFind " + tag);
    }

    public static void main(String[] args) {
        verify(5, new int[][]{{0, 1}, {1, 2}, {1, 4}, {2, 3}, {3, 4}}, true);   // 1-2-3-4-1
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 3}}, false);                  // a path
        verify(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}, true);                   // triangle
        verify(5, new int[][]{{0, 1}, {0, 2}, {0, 3}, {0, 4}}, false);          // star tree
        verify(7, new int[][]{{0, 1}, {1, 2}, {3, 4}, {4, 5}, {5, 3}}, true);   // cycle only in the second component
        verify(6, new int[][]{{0, 1}, {0, 2}, {3, 4}}, false);                  // forest with an isolated vertex
        verify(1, new int[][]{}, false);                                         // single vertex
        verify(0, new int[][]{}, false);                                         // empty graph
        int n = 1500;                                                            // long ring, then the same ring with one edge removed
        int[][] ring = new int[n][];
        for (int i = 0; i < n; i++) ring[i] = new int[]{i, (i + 1) % n};
        verify(n, ring, true);
        verify(n, Arrays.copyOf(ring, n - 1), false);
        System.out.println("OK P501_DetectACycleInAnUndirectedGraph");
    }
}
