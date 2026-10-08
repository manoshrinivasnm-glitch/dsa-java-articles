import java.util.*;

/** TUF 522 - Floyd warshall algorithm. matrix[i][j] = edge weight i -> j or -1 for no edge; overwrite it with all-pairs shortest distances (-1 if unreachable). */
public class P522_FloydWarshallAlgorithm {

    static final int INF = 1_000_000_000;

    /** Approach 1: run Bellman-Ford from every source on the adjacency matrix. O(V^4) time on a matrix, O(V^2) space. */
    static void bruteForce(int[][] matrix) {
        int n = matrix.length;
        int[][] result = new int[n][];
        for (int src = 0; src < n; src++) {
            int[] dist = new int[n];
            Arrays.fill(dist, INF);
            dist[src] = 0;
            for (int round = 1; round < n; round++) {
                for (int u = 0; u < n; u++) {
                    if (dist[u] == INF) continue;
                    for (int v = 0; v < n; v++) {
                        if (matrix[u][v] != -1 && dist[u] + matrix[u][v] < dist[v]) dist[v] = dist[u] + matrix[u][v];
                    }
                }
            }
            result[src] = dist;                          // keep the input intact until every source is done
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) matrix[i][j] = result[i][j] == INF ? -1 : result[i][j];
        }
    }

    /** Approach 2: Floyd-Warshall, allow intermediate vertices 0, 1, ..., via one at a time. O(V^3) time, O(1) extra space. */
    static void floydWarshall(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) if (matrix[i][j] == -1) matrix[i][j] = INF;
        }
        for (int via = 0; via < n; via++) {
            for (int i = 0; i < n; i++) {
                if (matrix[i][via] == INF) continue;     // i cannot reach via, nothing to improve
                for (int j = 0; j < n; j++) {
                    if (matrix[via][j] != INF && matrix[i][via] + matrix[via][j] < matrix[i][j]) {
                        matrix[i][j] = matrix[i][via] + matrix[via][j];
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) if (matrix[i][j] == INF) matrix[i][j] = -1;
        }
    }

    /** Extension: with negative weights allowed, a negative cycle exists exactly when some dist[i][i] ends below 0. */
    static boolean hasNegativeCycle(int n, int[][] edges) {
        int[][] dist = new int[n][n];
        for (int[] row : dist) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) dist[i][i] = 0;
        for (int[] e : edges) dist[e[0]][e[1]] = Math.min(dist[e[0]][e[1]], e[2]);
        for (int via = 0; via < n; via++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][via] != INF && dist[via][j] != INF && dist[i][via] + dist[via][j] < dist[i][j]) {
                        dist[i][j] = dist[i][via] + dist[via][j];
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) if (dist[i][i] < 0) return true;
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static int[][] copy(int[][] m) {
        int[][] c = new int[m.length][];
        for (int i = 0; i < m.length; i++) c[i] = m[i].clone();
        return c;
    }

    static void verify(int[][] matrix, int[][] expected) {
        int[][] a = copy(matrix), b = copy(matrix);
        bruteForce(a);
        floydWarshall(b);
        check(Arrays.deepEquals(a, expected) && Arrays.deepEquals(b, expected),
                "expected " + Arrays.deepToString(expected) + " got " + Arrays.deepToString(a) + " " + Arrays.deepToString(b));
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 25}, {-1, 0}}, new int[][]{{0, 25}, {-1, 0}});
        verify(new int[][]{{0, 1, 43}, {1, 0, 6}, {-1, -1, 0}}, new int[][]{{0, 1, 7}, {1, 0, 6}, {-1, -1, 0}});
        verify(new int[][]{{0}}, new int[][]{{0}});                                           // single vertex
        verify(new int[][]{{0, 2, -1, -1}, {1, 0, 3, -1}, {-1, -1, 0, -1}, {3, 5, 4, 0}},
               new int[][]{{0, 2, 5, -1}, {1, 0, 3, -1}, {-1, -1, 0, -1}, {3, 5, 4, 0}});
        verify(new int[][]{{0, 1, -1, -1}, {-1, 0, 1, -1}, {-1, -1, 0, 1}, {1, -1, -1, 0}},  // directed 4-cycle of weight-1 edges
               new int[][]{{0, 1, 2, 3}, {3, 0, 1, 2}, {2, 3, 0, 1}, {1, 2, 3, 0}});
        verify(new int[][]{{0, 0, -1}, {-1, 0, 0}, {-1, -1, 0}}, new int[][]{{0, 0, 0}, {-1, 0, 0}, {-1, -1, 0}});   // zero weights

        check(hasNegativeCycle(3, new int[][]{{0, 1, 1}, {1, 2, -1}, {2, 0, -1}}), "cycle of weight -1");
        check(!hasNegativeCycle(3, new int[][]{{0, 1, 1}, {1, 2, -1}, {2, 0, 0}}), "cycle of weight 0 is fine");
        check(!hasNegativeCycle(3, new int[][]{{0, 1, -5}, {1, 2, -5}}), "negative edges without a cycle");
        check(hasNegativeCycle(1, new int[][]{{0, 0, -1}}), "negative self-loop");

        Random rnd = new Random(522);                    // seeded cross-check
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(6);
            int[][] m = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) m[i][j] = i == j ? 0 : (rnd.nextInt(3) == 0 ? rnd.nextInt(20) : -1);
            int[][] expected = copy(m);
            bruteForce(expected);
            verify(m, expected);
        }
        System.out.println("OK P522_FloydWarshallAlgorithm");
    }
}
