import java.util.*;

/** TUF 911 - Set Matrix Zeroes. If a cell is 0, set its entire row and entire column to 0, in place. */
public class P911_SetMatrixZeroes {

    /** Approach 1: read the zero positions from an untouched copy and clear rows/columns in the original. O(n*m*(n+m)) time, O(n*m) space. */
    static void bruteForce(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length;
        int[][] original = new int[n][];
        for (int i = 0; i < n; i++) original[i] = matrix[i].clone();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (original[i][j] == 0) {
                    for (int c = 0; c < m; c++) matrix[i][c] = 0;   // whole row i
                    for (int r = 0; r < n; r++) matrix[r][j] = 0;   // whole column j
                }
            }
        }
    }

    /** Approach 2: remember which rows and which columns contain a zero, then clear them in a second pass. O(n*m) time, O(n+m) space. */
    static void better(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length;
        boolean[] zeroRow = new boolean[n], zeroCol = new boolean[m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (matrix[i][j] == 0) {
                    zeroRow[i] = true;
                    zeroCol[j] = true;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (zeroRow[i] || zeroCol[j]) matrix[i][j] = 0;
            }
        }
    }

    /** Approach 3: use row 0 and column 0 as the marker arrays, plus one flag for column 0 itself. O(n*m) time, O(1) space. */
    static void optimal(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length;
        if (n == 0 || m == 0) return;
        boolean col0 = false;                        // does column 0 need to become zero?
        for (int i = 0; i < n; i++) {
            if (matrix[i][0] == 0) col0 = true;
            for (int j = 1; j < m; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;                // row marker for row i
                    matrix[0][j] = 0;                // column marker for column j
                }
            }
        }
        for (int i = 1; i < n; i++) {                // inner cells: read the markers
            for (int j = 1; j < m; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;
            }
        }
        if (matrix[0][0] == 0) {                     // matrix[0][0] is the marker for row 0
            for (int j = 0; j < m; j++) matrix[0][j] = 0;
        }
        if (col0) {                                  // finally column 0, from the separate flag
            for (int i = 0; i < n; i++) matrix[i][0] = 0;
        }
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
        int[][] a = copy(input), b = copy(input), c = copy(input);
        bruteForce(a);
        better(b);
        optimal(c);
        check(Arrays.deepEquals(a, expected), "bruteForce " + Arrays.deepToString(input) + " -> " + Arrays.deepToString(a));
        check(Arrays.deepEquals(b, expected), "better " + Arrays.deepToString(input) + " -> " + Arrays.deepToString(b));
        check(Arrays.deepEquals(c, expected), "optimal " + Arrays.deepToString(input) + " -> " + Arrays.deepToString(c));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}}, new int[][]{{1, 0, 1}, {0, 0, 0}, {1, 0, 1}});
        verify(new int[][]{{0, 1, 2, 0}, {3, 4, 5, 2}, {1, 3, 1, 5}}, new int[][]{{0, 0, 0, 0}, {0, 4, 5, 0}, {0, 3, 1, 0}});
        verify(new int[][]{{1, 2, 3}, {4, 5, 6}}, new int[][]{{1, 2, 3}, {4, 5, 6}});           // no zero: unchanged
        verify(new int[][]{{0}}, new int[][]{{0}});                                              // 1 x 1
        verify(new int[][]{{1, 0}}, new int[][]{{0, 0}});                                        // single row
        verify(new int[][]{{1}, {0}, {2}}, new int[][]{{0}, {0}, {0}});                          // single column
        verify(new int[][]{{-1, 2}, {3, 0}}, new int[][]{{-1, 0}, {0, 0}});                      // negatives must survive
        verify(new int[][]{{1, 2}, {3, 4}, {0, 6}}, new int[][]{{0, 2}, {0, 4}, {0, 0}});        // zero in column 0 only
        verify(new int[][]{{1, 0, 3}, {4, 5, 6}}, new int[][]{{0, 0, 0}, {4, 0, 6}});            // zero in row 0 only
        verify(new int[][]{{0, 0}, {0, 0}}, new int[][]{{0, 0}, {0, 0}});                        // all zeros
        verify(new int[0][0], new int[0][0]);                                                    // empty
        System.out.println("OK P911_SetMatrixZeroes");
    }
}
