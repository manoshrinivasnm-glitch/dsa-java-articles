import java.util.*;

/** TUF 68 - Search in 2D matrix - II. Every row and every column is sorted in increasing order (rows are not chained). Report whether target is present. */
public class P68_SearchIn2DMatrixII {

    /** Approach 1: look at every cell. O(n * m) time, O(1) space. */
    static boolean bruteForce(int[][] matrix, int target) {
        for (int[] row : matrix) {
            for (int x : row) if (x == target) return true;
        }
        return false;
    }

    /** Plain binary search for target inside one sorted row. O(log m). */
    static boolean inRow(int[] row, int target) {
        int lo = 0, hi = row.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (row[mid] == target) return true;
            if (row[mid] < target) lo = mid + 1; else hi = mid - 1;
        }
        return false;
    }

    /** Approach 2: binary search every row whose first and last element bracket target. O(n log m) time, O(1) space. */
    static boolean better(int[][] matrix, int target) {
        for (int[] row : matrix) {
            if (row.length == 0) continue;
            if (row[0] <= target && target <= row[row.length - 1] && inRow(row, target)) return true;
        }
        return false;
    }

    /** Approach 3: start at the top-right corner; every step throws away a whole row or a whole column. O(n + m) time, O(1) space. */
    static boolean optimal(int[][] matrix, int target) {
        int n = matrix.length;
        if (n == 0 || matrix[0].length == 0) return false;
        int m = matrix[0].length;
        int r = 0, c = m - 1;
        while (r < n && c >= 0) {
            int value = matrix[r][c];
            if (value == target) return true;
            if (value > target) c--;                 // everything below in this column is even larger
            else r++;                                // everything to the left in this row is even smaller
        }
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] matrix, int target, boolean expected) {
        String in = Arrays.deepToString(matrix) + " target=" + target;
        check(bruteForce(matrix, target) == expected, "bruteForce " + in);
        check(better(matrix, target) == expected, "better " + in);
        check(optimal(matrix, target) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        int[][] grid = {
            {1, 4, 7, 11, 15},
            {2, 5, 8, 12, 19},
            {3, 6, 9, 16, 22},
            {10, 13, 14, 17, 24},
            {18, 21, 23, 26, 30}};
        verify(grid, 5, true);
        verify(grid, 20, false);                                   // bracketed by several rows, present in none
        verify(grid, 1, true);                                     // top-left
        verify(grid, 30, true);                                    // bottom-right
        verify(grid, 15, true);                                    // top-right: found on the first step of the staircase
        verify(grid, 18, true);                                    // bottom-left: the longest staircase walk
        verify(grid, 0, false);                                    // smaller than everything
        verify(grid, 31, false);                                   // larger than everything
        verify(new int[][]{{-5}}, -5, true);                       // 1 x 1
        verify(new int[][]{{-5}}, 5, false);
        verify(new int[][]{{1, 2, 3}}, 2, true);                   // single row
        verify(new int[][]{{1, 2, 3}}, 4, false);
        verify(new int[][]{{1}, {2}, {3}}, 3, true);               // single column
        verify(new int[0][0], 7, false);                           // empty matrix
        System.out.println("OK P68_SearchIn2DMatrixII");
    }
}
