import java.util.*;

/** TUF 2819 - Detect a cycle in an undirected graph using DFS. adj.get(u) lists the neighbours of u. */
public class P2819_DetectACycleInUndirectedGraphUsingDFS {

    /** Approach 1: for every edge u-v, DFS from u without that edge and see whether v is still reachable. O(E * (V + E)) time, O(V) space. */
    static boolean bruteForce(int V, List<List<Integer>> adj) {
        for (int u = 0; u < V; u++) {                     // try every vertex as one end of an edge
            for (int v : adj.get(u)) {
                // u < v tests each undirected edge only once
                if (u < v && reach(adj, u, u, v, new boolean[V])) return true;
            }
        }
        return false;                                     // no edge lies on a cycle
    }

    /** Recursive DFS from x looking for v, never using the edge u-v. */
    static boolean reach(List<List<Integer>> adj, int x, int u, int v, boolean[] seen) {
        if (x == v) return true;                          // reached v by another route: u-v is on a cycle
        seen[x] = true;                                   // mark x so the search never loops
        for (int y : adj.get(x)) {                        // try every edge out of x except the removed one
            if ((x == u && y == v) || (x == v && y == u)) continue;   // the removed edge
            if (!seen[y] && reach(adj, y, u, v, seen)) return true;   // search on from each unseen neighbour
        }
        return false;                                     // v is not reachable from x without the edge
    }

    /** Approach 2: recursive DFS passing the parent; a visited neighbour other than the parent is a back edge. O(V + E) time, O(V) space. */
    static boolean optimal(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];               // vertices already reached by some DFS
        for (int s = 0; s < V; s++) {                     // start a DFS in every component
            if (!visited[s] && dfs(adj, s, -1, visited)) return true;   // -1: the root has no parent
        }
        return false;                                     // no component contains a cycle
    }

    static boolean dfs(List<List<Integer>> adj, int x, int parent, boolean[] visited) {
        visited[x] = true;                                // x is now part of the DFS tree
        for (int y : adj.get(x)) {
            if (!visited[y]) {                            // tree edge: go deeper with x as y's parent
                if (dfs(adj, y, x, visited)) return true;   // a cycle was found further down: pass it up
            } else if (y != parent) {                     // visited, and not the edge we arrived by
                return true;                              // back edge to an ancestor
            }
        }
        return false;                                     // no back edge below x
    }

    /** Approach 3: the same DFS with an explicit stack, so deep graphs cannot overflow the call stack. O(V + E) time, O(V) space. */
    static boolean optimalIterative(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];               // vertices already pushed at some point
        int[] parent = new int[V];                        // parent[y] = the vertex y was reached from
        int[] next = new int[V];                          // next[x] = index of the next neighbour of x to try
        Deque<Integer> stack = new ArrayDeque<>();        // the current DFS path, deepest vertex on top
        for (int s = 0; s < V; s++) {                     // start a DFS in every component
            if (visited[s]) continue;                     // already covered by an earlier DFS
            visited[s] = true;
            parent[s] = -1;                               // the root has no parent
            stack.push(s);
            while (!stack.isEmpty()) {
                int x = stack.peek();                     // keep working on the deepest vertex
                if (next[x] == adj.get(x).size()) {       // every neighbour handled: x is finished
                    stack.pop();
                    continue;
                }
                int y = adj.get(x).get(next[x]++);        // take x's next untried neighbour
                if (!visited[y]) {                        // tree edge: y is new
                    visited[y] = true;
                    parent[y] = x;
                    stack.push(y);                        // go deeper, exactly like the recursive call
                } else if (y != parent[x]) {              // visited, and not the edge back to x's parent
                    return true;                          // a back edge means a cycle
                }
            }
        }
        return false;                                     // no back edge anywhere
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> graph(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    static void verify(int V, int[][] edges, boolean expected) {
        List<List<Integer>> adj = graph(V, edges);
        check(bruteForce(V, adj) == expected, "bruteForce " + Arrays.deepToString(edges));
        check(optimal(V, adj) == expected, "optimal " + Arrays.deepToString(edges));
        check(optimalIterative(V, adj) == expected, "optimalIterative " + Arrays.deepToString(edges));
    }

    public static void main(String[] args) {
        verify(5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 1}}, true);   // 1-2-3-4-1
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 3}}, false);                  // path
        verify(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}, true);                   // triangle
        verify(1, new int[][]{}, false);                                        // single vertex
        verify(6, new int[][]{{0, 1}, {0, 2}, {2, 3}, {2, 4}, {4, 5}}, false);  // branching tree
        verify(8, new int[][]{{0, 1}, {2, 3}, {3, 4}, {5, 6}, {6, 7}, {7, 5}}, true); // cycle in third component
        // long path: 100000 vertices would overflow a small call stack; the iterative version handles any depth
        int n = 100_000;
        List<List<Integer>> path = new ArrayList<>();
        for (int i = 0; i < n; i++) path.add(new ArrayList<>());
        for (int i = 0; i + 1 < n; i++) { path.get(i).add(i + 1); path.get(i + 1).add(i); }
        check(!optimalIterative(n, path), "long path has no cycle");
        path.get(0).add(n - 1); path.get(n - 1).add(0);
        check(optimalIterative(n, path), "long ring has a cycle");
        System.out.println("OK P2819_DetectACycleInUndirectedGraphUsingDFS");
    }
}
