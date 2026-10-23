import java.util.*;

/** TUF 2816 - Detect a cycle in a directed graph using DFS. adj.get(u) lists the vertices v with an edge u -> v. */
public class P2816_DetectACycleInADirectedGraphUsingDFS {

    /** Approach 1: from every vertex s, search for a walk that comes back to s. O(V * (V + E)) time, O(V) space. */
    static boolean bruteForce(int V, List<List<Integer>> adj) {
        for (int s = 0; s < V; s++) {                     // try every vertex as the start of a cycle
            boolean[] seen = new boolean[V];              // fresh visited set for each search
            if (returnsTo(adj, s, s, seen)) return true;  // some walk from s leads back to s
        }
        return false;                                     // no vertex can reach itself again
    }

    /** DFS from x: is there an edge into target from anything reachable? */
    static boolean returnsTo(List<List<Integer>> adj, int x, int target, boolean[] seen) {
        seen[x] = true;                                   // never expand a vertex twice
        for (int y : adj.get(x)) {
            if (y == target) return true;                 // an edge back into target closes the cycle
            if (!seen[y] && returnsTo(adj, y, target, seen)) return true;   // keep searching from unseen y
        }
        return false;                                     // target cannot be reached again from x
    }

    /** Approach 2: one DFS with visited[] and onPath[]; an edge into a vertex still on the path is a cycle. O(V + E) time, O(V) space. */
    static boolean optimal(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];               // vertices whose DFS has already started
        boolean[] onPath = new boolean[V];                // vertices on the current recursion path
        for (int s = 0; s < V; s++) {                     // start a DFS from every unvisited vertex
            if (!visited[s] && dfs(adj, s, visited, onPath)) return true;
        }
        return false;                                     // no back edge in any DFS
    }

    static boolean dfs(List<List<Integer>> adj, int x, boolean[] visited, boolean[] onPath) {
        visited[x] = true;
        onPath[x] = true;                                 // x joins the current path
        for (int y : adj.get(x)) {
            if (onPath[y]) return true;                   // back edge: y is an ancestor of x
            if (!visited[y] && dfs(adj, y, visited, onPath)) return true;   // go deeper into unvisited y
        }
        onPath[x] = false;                                // leaving x: it is no longer on the current path
        return false;                                     // no cycle is reachable from x
    }

    /** Approach 3: the same three-state DFS with an explicit stack. 0 = new, 1 = on path, 2 = finished. O(V + E) time, O(V) space. */
    static boolean optimalIterative(int V, List<List<Integer>> adj) {
        int[] state = new int[V];                         // 0 = new, 1 = on the current path, 2 = finished
        int[] next = new int[V];                          // next[x] = index of x's next edge to try
        Deque<Integer> stack = new ArrayDeque<>();        // the current DFS path, deepest vertex on top
        for (int s = 0; s < V; s++) {                     // start a DFS from every new vertex
            if (state[s] != 0) continue;                  // already explored by an earlier DFS
            state[s] = 1;                                 // s starts the path
            stack.push(s);
            while (!stack.isEmpty()) {
                int x = stack.peek();                     // work on the deepest vertex of the path
                if (next[x] == adj.get(x).size()) {       // no edges left to try: x is done
                    state[x] = 2;                         // all edges out of x explored
                    stack.pop();
                    continue;
                }
                int y = adj.get(x).get(next[x]++);        // follow x's next untried edge
                if (state[y] == 1) return true;           // edge back into the current path: a cycle
                if (state[y] == 0) {                      // new vertex: extend the path to y
                    state[y] = 1;
                    stack.push(y);
                }
            }
        }
        return false;                                     // no back edge found anywhere
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> graph(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    static void verify(int V, int[][] edges, boolean expected) {
        List<List<Integer>> adj = graph(V, edges);
        check(bruteForce(V, adj) == expected, "bruteForce " + Arrays.deepToString(edges));
        check(optimal(V, adj) == expected, "optimal " + Arrays.deepToString(edges));
        check(optimalIterative(V, adj) == expected, "optimalIterative " + Arrays.deepToString(edges));
    }

    public static void main(String[] args) {
        verify(4, new int[][]{{0, 1}, {1, 2}, {2, 0}, {2, 3}}, true);           // 0 -> 1 -> 2 -> 0
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}}, false);          // diamond: 3 reached twice, no cycle
        verify(1, new int[][]{}, false);                                        // single vertex
        verify(1, new int[][]{{0, 0}}, true);                                   // self-loop
        verify(2, new int[][]{{0, 1}, {1, 0}}, true);                           // two-vertex cycle
        verify(6, new int[][]{{0, 1}, {1, 2}, {3, 4}, {4, 5}, {5, 3}}, true);   // cycle in second component
        verify(5, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}, {4, 3}}, false);  // DAG explored in an awkward order
        verify(3, new int[][]{{0, 1}, {0, 1}, {1, 2}}, false);                  // repeated edge
        System.out.println("OK P2816_DetectACycleInADirectedGraphUsingDFS");
    }
}
