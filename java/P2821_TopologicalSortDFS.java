import java.util.*;

/** TUF 2821 - Topological sort using DFS. The input is a DAG; adj.get(u) lists v for every edge u -> v. */
public class P2821_TopologicalSortDFS {

    /** Approach 1: repeatedly find a vertex whose out-neighbours are all placed and put it at the back. O(V * (V + E)) time, O(V) space. */
    static int[] bruteForce(int V, List<List<Integer>> adj) {
        boolean[] placed = new boolean[V];                // vertices already written into order
        int[] order = new int[V];                         // filled from the back toward the front
        for (int back = V - 1; back >= 0; back--) {       // fill positions from the last one backwards
            int pick = -1;                                // -1 = no sink found yet
            for (int u = 0; u < V && pick == -1; u++) {
                if (placed[u]) continue;                  // already in the order
                boolean sink = true;                      // every edge out of u must point to a placed vertex
                // an unplaced target means u must wait
                for (int v : adj.get(u)) if (!placed[v]) { sink = false; break; }
                if (sink) pick = u;                       // u can go at position back
            }
            if (pick == -1) return new int[0];            // no sink left: the graph has a cycle
            placed[pick] = true;
            order[back] = pick;                           // everything after it is already placed
        }
        return order;
    }

    /** Approach 2: DFS and write each vertex just before the ones already finished (reverse post-order). O(V + E) time, O(V) space. */
    static int[] optimal(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];               // vertices whose DFS has started
        int[] order = new int[V];                         // the topological order being built
        int[] back = {V};                                 // next free slot, filled from the end
        for (int s = 0; s < V; s++) {                     // start a DFS from every unvisited vertex
            if (!visited[s]) dfs(adj, s, visited, order, back);
        }
        return order;
    }

    static void dfs(List<List<Integer>> adj, int u, boolean[] visited, int[] order, int[] back) {
        visited[u] = true;                                // mark u first so it is never entered twice
        for (int v : adj.get(u)) {                        // finish every vertex reachable from u first
            if (!visited[v]) dfs(adj, v, visited, order, back);
        }
        order[--back[0]] = u;                             // everything reachable from u is already placed after it
    }

    /** Approach 3: the same reverse post-order with an explicit stack instead of recursion. O(V + E) time, O(V) space. */
    static int[] optimalIterative(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];               // vertices already pushed at some point
        int[] next = new int[V];                          // next[u] = index of u's next edge to try
        int[] order = new int[V];                         // the topological order being built
        int back = V;                                     // next free slot, filled from the end
        Deque<Integer> stack = new ArrayDeque<>();        // the current DFS path, deepest vertex on top
        for (int s = 0; s < V; s++) {                     // start a DFS from every unvisited vertex
            if (visited[s]) continue;
            visited[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.peek();                     // work on the deepest vertex
                if (next[u] < adj.get(u).size()) {        // u still has an untried edge
                    int v = adj.get(u).get(next[u]++);    // follow it
                    if (!visited[v]) {                    // unvisited: go deeper into v
                        visited[v] = true;
                        stack.push(v);
                    }
                } else {                                  // all edges tried: u is finished
                    stack.pop();
                    order[--back] = u;                    // u finishes now
                }
            }
        }
        return order;
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

    /** A valid order is a permutation of 0..V-1 in which every edge u -> v has u before v. */
    static boolean isTopological(int V, int[][] edges, int[] order) {
        if (order.length != V) return false;
        int[] pos = new int[V];
        Arrays.fill(pos, -1);
        for (int i = 0; i < V; i++) {
            if (order[i] < 0 || order[i] >= V || pos[order[i]] != -1) return false;
            pos[order[i]] = i;
        }
        for (int[] e : edges) if (pos[e[0]] >= pos[e[1]]) return false;
        return true;
    }

    static void verify(int V, int[][] edges) {
        List<List<Integer>> adj = graph(V, edges);
        check(isTopological(V, edges, bruteForce(V, adj)), "bruteForce " + Arrays.deepToString(edges));
        check(isTopological(V, edges, optimal(V, adj)), "optimal " + Arrays.deepToString(edges));
        check(isTopological(V, edges, optimalIterative(V, adj)), "optimalIterative " + Arrays.deepToString(edges));
    }

    public static void main(String[] args) {
        verify(6, new int[][]{{5, 2}, {5, 0}, {4, 0}, {4, 1}, {2, 3}, {3, 1}});  // classic example
        verify(1, new int[][]{});                                                // single vertex
        verify(5, new int[][]{});                                                // no edges
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}, {0, 3}});          // diamond plus shortcut
        verify(5, new int[][]{{1, 0}, {2, 1}, {3, 2}, {4, 3}});                  // DFS from 0 first finds nothing to explore
        // a chain has exactly one valid order, so every method must return it exactly
        List<List<Integer>> chain = graph(4, new int[][]{{2, 0}, {0, 3}, {3, 1}});
        int[] expected = {2, 0, 3, 1};
        check(Arrays.equals(bruteForce(4, chain), expected), "bruteForce chain");
        check(Arrays.equals(optimal(4, chain), expected), "optimal chain");
        check(Arrays.equals(optimalIterative(4, chain), expected), "optimalIterative chain");
        // the two DFS versions visit vertices in the same order, so they agree on the classic example
        List<List<Integer>> g = graph(6, new int[][]{{5, 2}, {5, 0}, {4, 0}, {4, 1}, {2, 3}, {3, 1}});
        check(Arrays.equals(optimal(6, g), new int[]{5, 4, 2, 3, 1, 0}), "optimal classic order");
        check(Arrays.equals(optimalIterative(6, g), new int[]{5, 4, 2, 3, 1, 0}), "iterative classic order");
        System.out.println("OK P2821_TopologicalSortDFS");
    }
}
