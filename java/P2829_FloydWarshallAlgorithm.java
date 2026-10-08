import java.util.*;

/** TUF 2829 - Floyd Warshall. matrix[i][j] = weight of edge i -> j (>= 0) or -1 if there is no edge. Return all-pairs shortest distances, -1 if unreachable. */
public class P2829_FloydWarshallAlgorithm {

    static final int INF = Integer.MAX_VALUE;                  // internal "no path" marker

    /** Copies the input, turning -1 (no edge) into INF and forcing d[i][i] = 0. */
    static int[][] toDist(int[][] matrix) {
        int n = matrix.length;
        int[][] d = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) d[i][j] = i == j ? 0 : (matrix[i][j] == -1 ? INF : matrix[i][j]);
        }
        return d;
    }

    /** Turns INF back into -1, the output convention. */
    static int[][] toAnswer(int[][] d) {
        for (int[] row : d) {
            for (int j = 0; j < row.length; j++) if (row[j] == INF) row[j] = -1;
        }
        return d;
    }

    /** One min-plus product: out[i][j] = min over k of a[i][k] + b[k][j]. O(n^3). */
    static int[][] minPlus(int[][] a, int[][] b) {
        int n = a.length;
        int[][] out = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int best = INF;
                for (int k = 0; k < n; k++) {
                    if (a[i][k] != INF && b[k][j] != INF) best = Math.min(best, a[i][k] + b[k][j]);
                }
                out[i][j] = best;
            }
        }
        return out;
    }

    /** Approach 1: extend shortest paths by one edge at a time until they may use n - 1 edges. O(n^4) time, O(n^2) space. */
    static int[][] bruteForce(int[][] matrix) {
        int[][] w = toDist(matrix);
        int[][] d = w;                                         // d = best paths with at most 1 edge
        for (int m = 2; m <= w.length - 1; m++) d = minPlus(d, w);   // now at most m edges
        return toAnswer(d);
    }

    /** Approach 2: repeated squaring; each product doubles the number of edges a path may use. O(n^3 log n) time, O(n^2) space. */
    static int[][] better(int[][] matrix) {
        int[][] d = toDist(matrix);
        for (int len = 1; len < d.length - 1; len *= 2) d = minPlus(d, d);
        return toAnswer(d);
    }

    /** Approach 3: Floyd-Warshall. After round k, d[i][j] is the shortest path whose intermediate vertices are all in 0..k. O(n^3) time, O(n^2) space. */
    static int[][] floydWarshall(int[][] matrix) {
        int[][] d = toDist(matrix);
        int n = d.length;
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (d[i][k] == INF) continue;                  // i cannot reach k, so k cannot help i
                for (int j = 0; j < n; j++) {
                    if (d[k][j] != INF && d[i][k] + d[k][j] < d[i][j]) d[i][j] = d[i][k] + d[k][j];
                }
            }
        }
        return toAnswer(d);
    }

    /** Negative-cycle check for directed edges {u, v, w} with possibly negative w: run Floyd-Warshall and look for d[i][i] < 0. O(n^3) time. */
    static boolean hasNegativeCycle(int n, int[][] edges) {
        long[][] d = new long[n][n];
        for (long[] row : d) Arrays.fill(row, Long.MAX_VALUE);
        for (int i = 0; i < n; i++) d[i][i] = 0;
        for (int[] e : edges) d[e[0]][e[1]] = Math.min(d[e[0]][e[1]], e[2]);
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (d[i][k] == Long.MAX_VALUE) continue;
                for (int j = 0; j < n; j++) {
                    if (d[k][j] != Long.MAX_VALUE) d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]);
                }
            }
        }
        for (int i = 0; i < n; i++) if (d[i][i] < 0) return true;   // a walk from i back to i with negative cost
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] matrix, int[][] expected) {
        check(Arrays.deepEquals(bruteForce(matrix), expected), "bruteForce " + Arrays.deepToString(bruteForce(matrix)));
        check(Arrays.deepEquals(better(matrix), expected), "better " + Arrays.deepToString(better(matrix)));
        check(Arrays.deepEquals(floydWarshall(matrix), expected), "floydWarshall " + Arrays.deepToString(floydWarshall(matrix)));
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 25}, {-1, 0}}, new int[][]{{0, 25}, {-1, 0}});
        verify(new int[][]{{0, 1, 43}, {1, 0, 6}, {-1, -1, 0}}, new int[][]{{0, 1, 7}, {1, 0, 6}, {-1, -1, 0}});
        verify(new int[][]{{0}}, new int[][]{{0}});                                          // single vertex
        verify(new int[][]{{0, 3, -1, 7}, {8, 0, 2, -1}, {5, -1, 0, 1}, {2, -1, -1, 0}},
                new int[][]{{0, 3, 5, 6}, {5, 0, 2, 3}, {3, 6, 0, 1}, {2, 5, 7, 0}});
        verify(new int[][]{{0, 4, -1}, {-1, 0, -1}, {-1, -1, 0}},                             // mostly unreachable
                new int[][]{{0, 4, -1}, {-1, 0, -1}, {-1, -1, 0}});
        verify(new int[][]{{0, 0, -1}, {-1, 0, 0}, {0, -1, 0}}, new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}});   // zero weights

        // seeded random matrices: the three methods must agree
        Random rnd = new Random(2829);
        for (int t = 0; t < 40; t++) {
            int n = 1 + rnd.nextInt(25);
            int[][] m = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) m[i][j] = i == j ? 0 : (rnd.nextInt(3) == 0 ? -1 : rnd.nextInt(100));
            }
            int[][] ref = floydWarshall(m);
            check(Arrays.deepEquals(ref, bruteForce(m)), "bruteForce differs on random test " + t);
            check(Arrays.deepEquals(ref, better(m)), "better differs on random test " + t);
        }

        check(hasNegativeCycle(3, new int[][]{{0, 1, 1}, {1, 2, -1}, {2, 0, -1}}), "cycle of weight -1");
        check(!hasNegativeCycle(3, new int[][]{{0, 1, 1}, {1, 2, -1}, {2, 0, 1}}), "cycle of weight +1");
        check(hasNegativeCycle(2, new int[][]{{1, 1, -3}}), "negative self loop");
        check(!hasNegativeCycle(4, new int[][]{{0, 1, -5}, {1, 2, -5}, {2, 3, -5}}), "negative edges without a cycle");
        System.out.println("OK P2829_FloydWarshallAlgorithm");
    }
}
