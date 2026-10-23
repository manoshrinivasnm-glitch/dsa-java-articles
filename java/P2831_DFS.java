import java.util.*;

/** TUF 2831 - DFS. Depth-first order of the vertices reachable from a start vertex, neighbours taken in adjacency-list order. */
public class P2831_DFS {

    /** Approach 1: recursion. Visit u, then fully explore each unvisited neighbour in order before trying the next one. O(V + E). */
    static List<Integer> recursive(List<List<Integer>> adj, int start) {
        List<Integer> order = new ArrayList<>();
        dfs(adj, start, new boolean[adj.size()], order);   // one recursive call explores everything reachable
        return order;
    }

    static void dfs(List<List<Integer>> adj, int u, boolean[] visited, List<Integer> order) {
        visited[u] = true;                                  // mark on entry so no cycle leads back into u
        order.add(u);                                       // preorder: record u before its neighbours
        for (int v : adj.get(u)) {                          // neighbours in adjacency-list order
            if (!visited[v]) dfs(adj, v, visited, order);   // go as deep as possible through v first
        }
    }

    /** Approach 2: explicit stack of vertices. Push neighbours in reverse so the first is popped first; skip stale entries. O(V + E) time, O(E) stack. */
    static List<Integer> iterativeStack(List<List<Integer>> adj, int start) {
        List<Integer> order = new ArrayList<>();           // vertices in the order DFS visits them
        boolean[] visited = new boolean[adj.size()];       // set when a vertex is popped and explored
        Deque<Integer> stack = new ArrayDeque<>();         // LIFO: the newest vertex is explored next
        stack.push(start);
        while (!stack.isEmpty()) {
            int u = stack.pop();                           // the most recently pushed vertex
            if (visited[u]) continue;                      // pushed more than once; already explored
            visited[u] = true;
            order.add(u);
            List<Integer> nbrs = adj.get(u);
            for (int i = nbrs.size() - 1; i >= 0; i--) {   // reverse, so the first neighbour pops first
                int v = nbrs.get(i);
                if (!visited[v]) stack.push(v);
            }
        }
        return order;
    }

    /** Approach 3: simulate the call stack. Each frame is {vertex, index of the next neighbour to try}; at most V frames. O(V + E). */
    static List<Integer> iterativeFrames(List<List<Integer>> adj, int start) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[adj.size()];
        Deque<int[]> stack = new ArrayDeque<>();   // frames {vertex, index of the next neighbour to try}
        visited[start] = true;                     // enter start: mark and record it
        order.add(start);
        stack.push(new int[]{start, 0});           // like calling dfs(start)
        while (!stack.isEmpty()) {
            int[] top = stack.peek();              // the "call" currently running
            List<Integer> nbrs = adj.get(top[0]);
            if (top[1] == nbrs.size()) {           // every neighbour tried: "return" from this call
                stack.pop();
                continue;
            }
            int v = nbrs.get(top[1]++);            // next neighbour; the frame remembers how far it got
            if (!visited[v]) {                     // "call" dfs(v)
                visited[v] = true;
                order.add(v);
                stack.push(new int[]{v, 0});
            }
        }
        return order;
    }

    /** WRONG on purpose: marking at push time is fine for BFS but changes the DFS order. */
    static List<Integer> markOnPushWrong(List<List<Integer>> adj, int start) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[adj.size()];
        Deque<Integer> stack = new ArrayDeque<>();
        visited[start] = true;           // marked when pushed, like BFS does
        stack.push(start);
        while (!stack.isEmpty()) {
            int u = stack.pop();
            order.add(u);                // visit order is the pop order
            List<Integer> nbrs = adj.get(u);
            for (int i = nbrs.size() - 1; i >= 0; i--) {
                int v = nbrs.get(i);
                if (!visited[v]) {       // a vertex already pushed is never pushed again
                    visited[v] = true;   // BUG: claimed now, so a deeper vertex cannot reach v first
                    stack.push(v);
                }
            }
        }
        return order;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> build(int n, int[][] edges, boolean directed) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            if (!directed) adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    static void verify(List<List<Integer>> adj, int start, List<Integer> expected) {
        check(recursive(adj, start).equals(expected), "recursive got " + recursive(adj, start));
        check(iterativeStack(adj, start).equals(expected), "iterativeStack got " + iterativeStack(adj, start));
        check(iterativeFrames(adj, start).equals(expected), "iterativeFrames got " + iterativeFrames(adj, start));
    }

    public static void main(String[] args) {
        // 1. 0-1, 0-2, 0-3, 2-4: DFS goes deep into 2 before reaching 3 (BFS would give 0 1 2 3 4)
        List<List<Integer>> g1 = build(5, new int[][]{ {0, 1}, {0, 2}, {0, 3}, {2, 4} }, false);
        verify(g1, 0, List.of(0, 1, 2, 4, 3));
        verify(g1, 4, List.of(4, 2, 0, 1, 3));

        // 2. with a cycle: 0-1, 0-2, 1-3, 2-3, 3-4, 4-5
        List<List<Integer>> g2 = build(6, new int[][]{ {0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {4, 5} }, false);
        verify(g2, 0, List.of(0, 1, 3, 2, 4, 5));

        // 3. single vertex
        verify(build(1, new int[0][], false), 0, List.of(0));

        // 4. disconnected: only the start's component
        List<List<Integer>> g4 = build(5, new int[][]{ {0, 1}, {2, 3}, {3, 4} }, false);
        verify(g4, 0, List.of(0, 1));
        verify(g4, 3, List.of(3, 2, 4));

        // 5. directed: 0->1, 0->2, 1->3, 3->0, 2->3, 4->0
        List<List<Integer>> g5 = build(5, new int[][]{ {0, 1}, {0, 2}, {1, 3}, {3, 0}, {2, 3}, {4, 0} }, true);
        verify(g5, 0, List.of(0, 1, 3, 2));
        verify(g5, 4, List.of(4, 0, 1, 3, 2));

        // 6. directed 0->1, 0->2, 1->2, 1->4, 2->3: marking at push time gets this one wrong
        List<List<Integer>> g6 = build(5, new int[][]{ {0, 1}, {0, 2}, {1, 2}, {1, 4}, {2, 3} }, true);
        verify(g6, 0, List.of(0, 1, 2, 3, 4));
        check(markOnPushWrong(g6, 0).equals(List.of(0, 1, 4, 2, 3)), "mark-on-push visits 4 before 2");

        // 7. a 3000-vertex path: depth 3000 recursion is still fine
        int n = 3000;
        int[][] path = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) path[i] = new int[]{i, i + 1};
        List<List<Integer>> chain = build(n, path, false);
        List<Integer> inOrder = new ArrayList<>();
        for (int i = 0; i < n; i++) inOrder.add(i);
        verify(chain, 0, inOrder);

        // 8. a 200000-vertex path: only the iterative versions are safe at this depth
        int big = 200_000;
        int[][] bigPath = new int[big - 1][];
        for (int i = 0; i < big - 1; i++) bigPath[i] = new int[]{i, i + 1};
        List<List<Integer>> bigChain = build(big, bigPath, false);
        List<Integer> s = iterativeStack(bigChain, 0), f = iterativeFrames(bigChain, 0);
        check(s.size() == big && f.size() == big && s.equals(f), "iterative versions agree on a deep path");
        check(s.get(big - 1) == big - 1, "the far end is visited last");

        System.out.println("OK P2831_DFS");
    }
}
