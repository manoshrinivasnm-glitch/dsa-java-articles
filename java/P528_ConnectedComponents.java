import java.util.*;

/** TUF 528 - Connected Components. Count the connected components of an undirected graph with vertices 0..n-1 and an edge list. */
public class P528_ConnectedComponents {

    /** Approach 1: every vertex starts with its own label; push the smaller label across every edge until nothing changes. O(V * E). */
    static int bruteForce(int n, int[][] edges) {
        int[] label = new int[n];                   // label[v] = smallest vertex known to share v's component
        for (int i = 0; i < n; i++) label[i] = i;   // start: every vertex is alone
        boolean changed = true;                     // did the last sweep lower any label?
        while (changed) {                           // repeat until the labels settle
            changed = false;
            for (int[] e : edges) {                 // an edge's two ends must end up with the same label
                int low = Math.min(label[e[0]], label[e[1]]);     // the smaller label wins
                if (label[e[0]] != low || label[e[1]] != low) {   // the ends disagree: copy low to both
                    label[e[0]] = low;
                    label[e[1]] = low;
                    changed = true;
                }
            }
        }
        int components = 0;
        for (int i = 0; i < n; i++) {
            if (label[i] == i) components++;        // each component ends up labelled by its smallest vertex
        }
        return components;
    }

    /** Approach 2: build the adjacency list, then start a BFS from every vertex not yet visited. Each start is a new component. O(V + E). */
    static int optimalTraversal(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();   // adj.get(u) = neighbours of u
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);                   // undirected: store the edge both ways
            adj.get(e[1]).add(e[0]);
        }
        boolean[] visited = new boolean[n];            // vertices already placed in some component
        int components = 0;
        Deque<Integer> queue = new ArrayDeque<>();
        for (int start = 0; start < n; start++) {      // try every vertex as the seed of a new component
            if (visited[start]) continue;              // already swallowed by an earlier component
            components++;                              // unvisited vertex: it starts a new component
            visited[start] = true;
            queue.add(start);
            while (!queue.isEmpty()) {                 // BFS floods everything reachable from start
                int u = queue.poll();
                for (int v : adj.get(u)) {
                    if (!visited[v]) {                 // first time we reach v: mark it and explore later
                        visited[v] = true;
                        queue.add(v);
                    }
                }
            }
        }
        return components;
    }

    /** Approach 3: union-find. Start with n singletons; every edge that joins two different sets removes one component. O(E * alpha(n)). */
    static int optimalUnionFind(int n, int[][] edges) {
        int[] parent = new int[n], size = new int[n];   // union-find forest: parent links and tree sizes
        for (int i = 0; i < n; i++) {
            parent[i] = i;                              // every vertex starts as its own root
            size[i] = 1;
        }
        int components = n;                             // n singletons before any edge is applied
        for (int[] e : edges) {
            int a = find(parent, e[0]), b = find(parent, e[1]);   // roots of the two ends
            if (a == b) continue;                       // already in the same component: nothing merges
            if (size[a] < size[b]) {                    // make a the root of the larger tree
                int t = a;
                a = b;
                b = t;
            }
            parent[b] = a;                              // hang the smaller tree under the larger
            size[a] += size[b];
            components--;                               // two components merged into one
        }
        return components;
    }

    static int find(int[] parent, int x) {
        while (parent[x] != x) {             // climb until we reach the root
            parent[x] = parent[parent[x]];   // path halving keeps the trees shallow
            x = parent[x];
        }
        return x;                            // the root names the component
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] edges, int expected) {
        check(bruteForce(n, edges) == expected, "bruteForce n=" + n + " expected " + expected);
        check(optimalTraversal(n, edges) == expected, "optimalTraversal n=" + n + " expected " + expected);
        check(optimalUnionFind(n, edges) == expected, "optimalUnionFind n=" + n + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(5, new int[][]{ {0, 1}, {1, 2}, {3, 4} }, 2);                 // {0,1,2} and {3,4}
        verify(5, new int[][]{ {0, 1}, {1, 2}, {2, 3}, {3, 4} }, 1);         // one path
        verify(6, new int[][]{ {0, 1}, {1, 2}, {2, 0}, {3, 4} }, 3);         // triangle, pair, isolated 5
        verify(4, new int[0][], 4);                                          // no edges: every vertex alone
        verify(1, new int[0][], 1);                                          // single vertex
        verify(3, new int[][]{ {0, 1}, {1, 0}, {0, 1} }, 2);                 // repeated edges merge once
        verify(3, new int[][]{ {2, 2} }, 3);                                 // a self-loop joins nothing
        verify(4, new int[][]{ {3, 2}, {1, 0}, {2, 1} }, 1);                 // edges in "bad" order for labels

        // 1000 disjoint pairs
        int pairs = 1000;
        int[][] pe = new int[pairs][];
        for (int i = 0; i < pairs; i++) pe[i] = new int[]{2 * i, 2 * i + 1};
        verify(2 * pairs, pe, pairs);

        // a 2000-vertex path listed from the far end: the brute force needs about n sweeps here
        int n = 2000;
        int[][] chain = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) chain[i] = new int[]{n - 2 - i, n - 1 - i};
        verify(n, chain, 1);

        System.out.println("OK P528_ConnectedComponents");
    }
}
