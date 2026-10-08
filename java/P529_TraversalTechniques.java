import java.util.*;

/** TUF 529 - Traversal Techniques (BFS). Breadth-first order of the vertices reachable from a start vertex, neighbours taken in adjacency-list order. */
public class P529_TraversalTechniques {

    /** Approach 1: mark a vertex visited when it comes OUT of the queue. Correct, but a vertex can sit in the queue many times. O(V + E) time, O(E) queue. */
    static List<Integer> markOnPoll(List<List<Integer>> adj, int start) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[adj.size()];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (visited[u]) continue;                     // a stale duplicate: u was already processed
            visited[u] = true;
            order.add(u);
            for (int v : adj.get(u)) {
                if (!visited[v]) queue.add(v);
            }
        }
        return order;
    }

    /** Approach 2: mark a vertex visited when it goes INTO the queue. Every vertex is enqueued exactly once. O(V + E) time, O(V) space. */
    static List<Integer> bfs(List<List<Integer>> adj, int start) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[adj.size()];
        Deque<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.add(start);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;                    // mark now so nobody else enqueues v again
                    queue.add(v);
                }
            }
        }
        return order;
    }

    /** Approach 3: the same BFS processed one level at a time. levels.get(d) holds every vertex at distance d from start. */
    static List<List<Integer>> bfsLevels(List<List<Integer>> adj, int start) {
        List<List<Integer>> levels = new ArrayList<>();
        boolean[] visited = new boolean[adj.size()];
        Deque<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.add(start);
        while (!queue.isEmpty()) {
            int size = queue.size();                      // exactly the vertices of the current level
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int u = queue.poll();
                level.add(u);
                for (int v : adj.get(u)) {
                    if (!visited[v]) {
                        visited[v] = true;
                        queue.add(v);
                    }
                }
            }
            levels.add(level);
        }
        return levels;
    }

    /** Disconnected graphs: restart the BFS from every vertex still unvisited, in increasing order. */
    static List<Integer> bfsAll(List<List<Integer>> adj) {
        int n = adj.size();
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[n];
        Deque<Integer> queue = new ArrayDeque<>();
        for (int s = 0; s < n; s++) {
            if (visited[s]) continue;
            visited[s] = true;
            queue.add(s);
            while (!queue.isEmpty()) {
                int u = queue.poll();
                order.add(u);
                for (int v : adj.get(u)) {
                    if (!visited[v]) {
                        visited[v] = true;
                        queue.add(v);
                    }
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

    static void verify(List<List<Integer>> adj, int start, List<Integer> expected, List<List<Integer>> expectedLevels) {
        check(markOnPoll(adj, start).equals(expected), "markOnPoll got " + markOnPoll(adj, start));
        check(bfs(adj, start).equals(expected), "bfs got " + bfs(adj, start));
        List<List<Integer>> levels = bfsLevels(adj, start);
        check(levels.equals(expectedLevels), "bfsLevels got " + levels);
        List<Integer> flat = new ArrayList<>();
        for (List<Integer> level : levels) flat.addAll(level);
        check(flat.equals(expected), "levels concatenated give the BFS order");
    }

    public static void main(String[] args) {
        // 1. a small tree: 0-1, 0-2, 0-3, 2-4
        List<List<Integer>> g1 = build(5, new int[][]{ {0, 1}, {0, 2}, {0, 3}, {2, 4} }, false);
        verify(g1, 0, List.of(0, 1, 2, 3, 4), List.of(List.of(0), List.of(1, 2, 3), List.of(4)));
        verify(g1, 4, List.of(4, 2, 0, 1, 3), List.of(List.of(4), List.of(2), List.of(0), List.of(1, 3)));

        // 2. a graph with a cycle: 0-1, 0-2, 1-3, 2-3, 3-4, 4-5
        List<List<Integer>> g2 = build(6, new int[][]{ {0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {4, 5} }, false);
        verify(g2, 0, List.of(0, 1, 2, 3, 4, 5), List.of(List.of(0), List.of(1, 2), List.of(3), List.of(4), List.of(5)));

        // 3. single vertex, no edges
        verify(build(1, new int[0][], false), 0, List.of(0), List.of(List.of(0)));

        // 4. disconnected: only the start's component is reached; bfsAll covers the rest
        List<List<Integer>> g4 = build(5, new int[][]{ {0, 1}, {2, 3}, {3, 4} }, false);
        verify(g4, 0, List.of(0, 1), List.of(List.of(0), List.of(1)));
        check(bfsAll(g4).equals(List.of(0, 1, 2, 3, 4)), "bfsAll visits every component");
        check(bfsAll(g1).equals(bfs(g1, 0)), "connected graph: bfsAll equals bfs from 0");

        // 5. directed: 0->1, 0->2, 1->3, 3->0, 2->3, 4->0 (4 is not reachable from 0)
        List<List<Integer>> g5 = build(5, new int[][]{ {0, 1}, {0, 2}, {1, 3}, {3, 0}, {2, 3}, {4, 0} }, true);
        verify(g5, 0, List.of(0, 1, 2, 3), List.of(List.of(0), List.of(1, 2), List.of(3)));
        verify(g5, 4, List.of(4, 0, 1, 2, 3), List.of(List.of(4), List.of(0), List.of(1, 2), List.of(3)));
        check(bfsAll(g5).equals(List.of(0, 1, 2, 3, 4)), "bfsAll on the directed graph");

        // 6. neighbour order decides the output
        verify(build(4, new int[][]{ {0, 3}, {0, 1}, {0, 2} }, false), 0, List.of(0, 3, 1, 2), List.of(List.of(0), List.of(3, 1, 2)));

        // 7. complete graph on 60 vertices: markOnPoll enqueues duplicates but still agrees
        int k = 60;
        List<int[]> ke = new ArrayList<>();
        for (int i = 0; i < k; i++) for (int j = i + 1; j < k; j++) ke.add(new int[]{i, j});
        List<List<Integer>> complete = build(k, ke.toArray(new int[0][]), false);
        List<Integer> all = new ArrayList<>();
        List<Integer> rest = new ArrayList<>();
        for (int i = 0; i < k; i++) all.add(i);
        for (int i = 1; i < k; i++) rest.add(i);
        verify(complete, 0, all, List.of(List.of(0), rest));

        System.out.println("OK P529_TraversalTechniques");
    }
}
