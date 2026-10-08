import java.util.*;

/** TUF 2812 - Bipartite check using DFS. graph[u] lists the neighbours of u in an undirected graph. */
public class P2812_BipartiteCheckUsingDFS {

    /** Approach 1: backtracking. Give vertices 0, 1, 2, ... a side in turn and undo on conflict. O(2^V * V) worst case, O(V) space. */
    static boolean bruteForce(int[][] graph) {
        int[] color = new int[graph.length];
        Arrays.fill(color, -1);
        return assign(graph, 0, color);
    }

    static boolean assign(int[][] graph, int u, int[] color) {
        if (u == graph.length) return true;               // every vertex placed without a clash
        for (int side = 0; side < 2; side++) {
            boolean clash = false;
            for (int v : graph[u]) if (color[v] == side) { clash = true; break; }
            if (clash) continue;
            color[u] = side;
            if (assign(graph, u + 1, color)) return true;
            color[u] = -1;                                // undo and try the other side
        }
        return false;
    }

    /** Approach 2: recursive DFS 2-colouring; each child gets the opposite colour of its parent. O(V + E) time, O(V) space. */
    static boolean optimal(int[][] graph) {
        int[] color = new int[graph.length];
        Arrays.fill(color, -1);
        for (int s = 0; s < graph.length; s++) {
            if (color[s] == -1 && !dfs(graph, s, 0, color)) return false;
        }
        return true;
    }

    static boolean dfs(int[][] graph, int u, int c, int[] color) {
        color[u] = c;
        for (int v : graph[u]) {
            if (color[v] == -1) {
                if (!dfs(graph, v, 1 - c, color)) return false;
            } else if (color[v] == c) {
                return false;                             // both ends of edge u-v on the same side
            }
        }
        return true;
    }

    /** Approach 3: the same DFS colouring with an explicit stack, safe for very deep graphs. O(V + E) time, O(V) space. */
    static boolean optimalIterative(int[][] graph) {
        int V = graph.length;
        int[] color = new int[V];
        Arrays.fill(color, -1);
        Deque<Integer> stack = new ArrayDeque<>();
        for (int s = 0; s < V; s++) {
            if (color[s] != -1) continue;
            color[s] = 0;
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (int v : graph[u]) {
                    if (color[v] == -1) {
                        color[v] = 1 - color[u];          // colour on push, so no vertex is pushed twice
                        stack.push(v);
                    } else if (color[v] == color[u]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] graph, boolean expected) {
        check(bruteForce(graph) == expected, "bruteForce " + Arrays.deepToString(graph));
        check(optimal(graph) == expected, "optimal " + Arrays.deepToString(graph));
        check(optimalIterative(graph) == expected, "optimalIterative " + Arrays.deepToString(graph));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2, 3}, {0, 2}, {0, 1, 3}, {0, 2}}, false);       // LeetCode example 1
        verify(new int[][]{{1, 3}, {0, 2}, {1, 3}, {0, 2}}, true);              // LeetCode example 2
        verify(new int[][]{{}}, true);                                          // single vertex
        verify(new int[][]{}, true);                                            // empty graph
        verify(new int[][]{{}, {2, 4, 6}, {1, 4, 8, 9}, {7, 8}, {1, 2, 8, 9}, {6, 9}, {1, 5, 7, 8, 9}, {3, 6, 9}, {2, 3, 4, 6, 9}, {2, 4, 5, 6, 7, 8}}, false);
        verify(new int[][]{{1}, {0}, {3}, {2}}, true);                          // two separate edges
        verify(new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}}, true);   // 6-cycle (even)
        verify(new int[][]{{1, 2}, {0, 2}, {0, 1}, {4}, {3}}, false);           // triangle in first component
        // deep path: recursion would need 200000 frames; the iterative version does not care
        int n = 200_000;
        int[][] path = new int[n][];
        for (int i = 0; i < n; i++) {
            if (i == 0) path[i] = new int[]{1};
            else if (i == n - 1) path[i] = new int[]{n - 2};
            else path[i] = new int[]{i - 1, i + 1};
        }
        check(optimalIterative(path), "long path is bipartite");
        System.out.println("OK P2812_BipartiteCheckUsingDFS");
    }
}
