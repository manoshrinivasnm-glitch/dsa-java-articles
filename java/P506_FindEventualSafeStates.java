import java.util.*;

/** TUF 506 - Find eventual safe states. graph[u] lists the targets of u's outgoing edges. A node is safe if every path from it ends at a terminal node. Return safe nodes in increasing order. */
public class P506_FindEventualSafeStates {

    /** Approach 1: run a fresh cycle-search DFS from every node. O(V * (V + E)) time, O(V) space. */
    static List<Integer> bruteForce(int[][] graph) {
        int n = graph.length;
        List<Integer> safe = new ArrayList<>();
        for (int s = 0; s < n; s++) {
            if (!reachesCycle(s, graph, new int[n])) safe.add(s);
        }
        return safe;
    }

    /** DFS with states 0 = new, 1 = on current path, 2 = finished. Returns true if a cycle is reachable from u. */
    static boolean reachesCycle(int u, int[][] graph, int[] state) {
        state[u] = 1;
        for (int v : graph[u]) {
            if (state[v] == 1) return true;
            if (state[v] == 0 && reachesCycle(v, graph, state)) return true;
        }
        state[u] = 2;
        return false;
    }

    /** Approach 2: one shared DFS state for the whole graph; nodes left "on path" after an aborted search are unsafe. O(V + E) time, O(V) space. */
    static List<Integer> dfsShared(int[][] graph) {
        int n = graph.length;
        int[] state = new int[n];
        List<Integer> safe = new ArrayList<>();
        for (int s = 0; s < n; s++) {
            if (state[s] == 0) reachesCycle(s, graph, state);
            if (state[s] == 2) safe.add(s);
        }
        return safe;
    }

    /** Approach 3: reverse every edge and run Kahn's algorithm from the terminal nodes. O(V + E) time, O(V + E) space. */
    static List<Integer> optimal(int[][] graph) {
        int n = graph.length;
        List<List<Integer>> reversed = new ArrayList<>();
        for (int i = 0; i < n; i++) reversed.add(new ArrayList<>());
        int[] outdegree = new int[n];
        for (int u = 0; u < n; u++) {
            outdegree[u] = graph[u].length;
            for (int v : graph[u]) reversed.get(v).add(u);
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (outdegree[i] == 0) queue.add(i);   // terminal nodes are safe
        boolean[] safe = new boolean[n];
        while (!queue.isEmpty()) {
            int v = queue.poll();
            safe[v] = true;
            for (int u : reversed.get(v)) {
                if (--outdegree[u] == 0) queue.add(u);       // every edge of u now leads to a safe node
            }
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) if (safe[i]) result.add(i);
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] graph, List<Integer> expected) {
        String name = Arrays.deepToString(graph);
        check(bruteForce(graph).equals(expected), "bruteForce " + name + " got " + bruteForce(graph));
        check(dfsShared(graph).equals(expected), "dfsShared " + name + " got " + dfsShared(graph));
        check(optimal(graph).equals(expected), "optimal " + name + " got " + optimal(graph));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2}, {2, 3}, {5}, {0}, {5}, {}, {}}, List.of(2, 4, 5, 6));
        verify(new int[][]{{1, 2, 3, 4}, {1, 2}, {3, 4}, {0, 4}, {}}, List.of(4));
        verify(new int[][]{{}}, List.of(0));                                  // single terminal node
        verify(new int[][]{{0}}, List.of());                                  // single node with a self-loop
        verify(new int[][]{{1}, {2}, {0}}, List.of());                        // everything on one cycle
        verify(new int[][]{{1}, {2}, {3}, {}}, List.of(0, 1, 2, 3));          // a chain into a terminal
        verify(new int[][]{{1, 3}, {2}, {1}, {}}, List.of(3));                // 0 can escape to 3 but can also fall into 1 <-> 2
        verify(new int[][]{{1}, {}, {0, 3}, {3}}, List.of(0, 1));             // 2 points at a safe node and at a self-loop
        System.out.println("OK P506_FindEventualSafeStates");
    }
}
