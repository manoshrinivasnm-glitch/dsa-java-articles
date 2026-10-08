import java.util.*;

/** TUF 2820 - Topological sort using BFS (Kahn's algorithm). The input is a DAG; adj.get(u) lists v for every edge u -> v. */
public class P2820_TopologicalSortBFS {

    /** Approach 1: repeatedly scan for an unplaced vertex whose in-degree has dropped to 0. O(V^2 + E) time, O(V) space. */
    static int[] bruteForce(int V, List<List<Integer>> adj) {
        int[] indegree = new int[V];
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) indegree[v]++;
        }
        boolean[] placed = new boolean[V];
        int[] order = new int[V];
        for (int k = 0; k < V; k++) {
            int pick = -1;
            for (int u = 0; u < V && pick == -1; u++) {   // full scan for a free vertex
                if (!placed[u] && indegree[u] == 0) pick = u;
            }
            if (pick == -1) return new int[0];            // only possible if the graph has a cycle
            placed[pick] = true;
            order[k] = pick;
            for (int v : adj.get(pick)) indegree[v]--;
        }
        return order;
    }

    /** Approach 2: Kahn's algorithm. Keep the free vertices in a queue instead of rescanning. O(V + E) time, O(V) space. */
    static int[] optimal(int V, List<List<Integer>> adj) {
        int[] indegree = new int[V];
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) indegree[v]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int u = 0; u < V; u++) {
            if (indegree[u] == 0) queue.add(u);
        }
        int[] order = new int[V];
        int k = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order[k++] = u;
            for (int v : adj.get(u)) {
                if (--indegree[v] == 0) queue.add(v);     // last incoming edge removed: v is free
            }
        }
        return k == V ? order : new int[0];               // fewer than V placed means a cycle
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
    }

    public static void main(String[] args) {
        verify(6, new int[][]{{5, 2}, {5, 0}, {4, 0}, {4, 1}, {2, 3}, {3, 1}});  // classic example
        verify(4, new int[][]{{3, 0}, {1, 0}, {2, 0}});                          // many sources, one sink
        verify(1, new int[][]{});                                                // single vertex
        verify(5, new int[][]{});                                                // no edges: any order works
        verify(4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}, {0, 3}});          // diamond plus shortcut
        // a chain has exactly one valid order, so both methods must return it exactly
        List<List<Integer>> chain = graph(4, new int[][]{{2, 0}, {0, 3}, {3, 1}});
        check(Arrays.equals(bruteForce(4, chain), new int[]{2, 0, 3, 1}), "bruteForce chain");
        check(Arrays.equals(optimal(4, chain), new int[]{2, 0, 3, 1}), "optimal chain");
        // the methods report a cycle by returning an empty array
        List<List<Integer>> cyc = graph(3, new int[][]{{0, 1}, {1, 2}, {2, 1}});
        check(bruteForce(3, cyc).length == 0 && optimal(3, cyc).length == 0, "cycle must give empty order");
        System.out.println("OK P2820_TopologicalSortBFS");
    }
}
