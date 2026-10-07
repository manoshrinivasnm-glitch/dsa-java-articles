import java.util.*;

/** TUF 35 - Rotate an n x n matrix by 90 degrees clockwise. */
public class P35_RotateMatrixBy90Degrees {

    /** Approach 1: copy every cell into a fresh matrix at its rotated position. O(n^2) time, O(n^2) space. */
    static int[][] bruteForce(int[][] matrix) {
        int n = matrix.length;
        int[][] rotated = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                rotated[j][n - 1 - i] = matrix[i][j];   // row i of the input becomes column n-1-i of the output
            }
        }
        return rotated;
    }

    /** Approach 2: transpose in place, then reverse every row. O(n^2) time, O(1) space. Returns the same array for convenience. */
    static int[][] optimal(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {                   // transpose: swap across the main diagonal
            for (int j = i + 1; j < n; j++) {
                int t = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = t;
            }
        }
        for (int[] row : matrix) {                      // reverse each row
            for (int lo = 0, hi = n - 1; lo < hi; lo++, hi--) {
                int t = row[lo];
                row[lo] = row[hi];
                row[hi] = t;
            }
        }
        return matrix;
    }

    /** Approach 3: rotate four cells at a time, ring by ring, from the outside in. O(n^2) time, O(1) space. */
    static int[][] optimalCycles(int[][] matrix) {
        int n = matrix.length;
        for (int layer = 0; layer < n / 2; layer++) {
            int first = layer, last = n - 1 - layer;
            for (int k = first; k < last; k++) {
                int offset = k - first;
                int top = matrix[first][k];                                 // save top
                matrix[first][k] = matrix[last - offset][first];            // left   -> top
                matrix[last - offset][first] = matrix[last][last - offset]; // bottom -> left
                matrix[last][last - offset] = matrix[k][last];              // right  -> bottom
                matrix[k][last] = top;                                      // top    -> right
            }
        }
        return matrix;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static int[][] copy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = a[i].clone();
        return c;
    }

    static void verify(int[][] input, int[][] expected) {
        int[][] a = bruteForce(copy(input));
        int[][] b = optimal(copy(input));
        int[][] c = optimalCycles(copy(input));
        check(Arrays.deepEquals(a, expected), "bruteForce " + Arrays.deepToString(input) + " -> " + Arrays.deepToString(a));
        check(Arrays.deepEquals(b, expected), "optimal " + Arrays.deepToString(input) + " -> " + Arrays.deepToString(b));
        check(Arrays.deepEquals(c, expected), "optimalCycles " + Arrays.deepToString(input) + " -> " + Arrays.deepToString(c));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, new int[][]{{7, 4, 1}, {8, 5, 2}, {9, 6, 3}});
        verify(new int[][]{{1, 2}, {3, 4}}, new int[][]{{3, 1}, {4, 2}});
        verify(new int[][]{{5}}, new int[][]{{5}});                                              // 1 x 1
        verify(new int[0][0], new int[0][0]);                                                    // empty
        verify(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}},
               new int[][]{{13, 9, 5, 1}, {14, 10, 6, 2}, {15, 11, 7, 3}, {16, 12, 8, 4}});     // even n: no fixed centre
        verify(new int[][]{{-1, 0, 2}, {0, 0, 0}, {3, -4, 5}}, new int[][]{{3, 0, -1}, {-4, 0, 0}, {5, 0, 2}}); // negatives and zeros
        // rotating four times must give back the original
        int[][] m = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] four = optimal(optimal(optimal(optimal(copy(m)))));
        check(Arrays.deepEquals(four, m), "four rotations should be the identity");
        System.out.println("OK P35_RotateMatrixBy90Degrees");
    }
}
