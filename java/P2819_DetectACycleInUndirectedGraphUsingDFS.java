import java.util.*;

/** TUF 2819 - Detect a cycle in an undirected graph using DFS. adj.get(u) lists the neighbours of u. */
public class P2819_DetectACycleInUndirectedGraphUsingDFS {

    /** Approach 1: for every edge u-v, DFS from u without that edge and see whether v is still reachable. O(E * (V + E)) time, O(V) space. */
    static boolean bruteForce(int V, List<List<Integer>> adj) {
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {
                if (u < v && reach(adj, u, u, v, new boolean[V])) return true;
            }
        }
        return false;
    }

    /** Recursive DFS from x looking for v, never using the edge u-v. */
    static boolean reach(List<List<Integer>> adj, int x, int u, int v, boolean[] seen) {
        if (x == v) return true;
        seen[x] = true;
        for (int y : adj.get(x)) {
            if ((x == u && y == v) || (x == v && y == u)) continue;   // the removed edge
            if (!seen[y] && reach(adj, y, u, v, seen)) return true;
        }
        return false;
    }

    /** Approach 2: recursive DFS passing the parent; a visited neighbour other than the parent is a back edge. O(V + E) time, O(V) space. */
    static boolean optimal(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];
        for (int s = 0; s < V; s++) {
            if (!visited[s] && dfs(adj, s, -1, visited)) return true;
        }
        return false;
    }

    static boolean dfs(List<List<Integer>> adj, int x, int parent, boolean[] visited) {
        visited[x] = true;
        for (int y : adj.get(x)) {
            if (!visited[y]) {
                if (dfs(adj, y, x, visited)) return true;
            } else if (y != parent) {
                return true;                              // back edge to an ancestor
            }
        }
        return false;
    }

    /** Approach 3: the same DFS with an explicit stack, so deep graphs cannot overflow the call stack. O(V + E) time, O(V) space. */
    static boolean optimalIterative(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];
        int[] parent = new int[V];
        int[] next = new int[V];                          // next[x] = index of the next neighbour of x to try
        Deque<Integer> stack = new ArrayDeque<>();
        for (int s = 0; s < V; s++) {
            if (visited[s]) continue;
            visited[s] = true;
            parent[s] = -1;
            stack.push(s);
            while (!stack.isEmpty()) {
                int x = stack.peek();
                if (next[x] == adj.get(x).size()) {       // every neighbour handled: x is finished
                    stack.pop();
                    continue;
                }
                int y = adj.get(x).get(next[x]++);
                if (!visited[y]) {
                    visited[y] = true;
                    parent[y] = x;
                    stack.push(y);                        // go deeper, exactly like the recursive call
                } else if (y != parent[x]) {
                    return true;
                }
            }
        }
        return false;
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
