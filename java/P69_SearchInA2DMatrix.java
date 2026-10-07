import java.util.*;

/** TUF 69 - Search in a 2D matrix. Every row is sorted and the first element of each row is larger than the last element of the row above it. Report whether target is present. */
public class P69_SearchInA2DMatrix {

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

    /** Approach 2: find the single row whose first and last element bracket target, then binary search inside it. O(n + log m) time, O(1) space. */
    static boolean better(int[][] matrix, int target) {
        for (int[] row : matrix) {
            if (row.length == 0) continue;
            if (row[0] <= target && target <= row[row.length - 1]) return inRow(row, target);
        }
        return false;
    }

    /** Approach 3: treat the matrix as one sorted array of n * m cells and binary search on the flat index. O(log(n * m)) time, O(1) space. */
    static boolean optimal(int[][] matrix, int target) {
        int n = matrix.length;
        if (n == 0 || matrix[0].length == 0) return false;
        int m = matrix[0].length;
        int lo = 0, hi = n * m - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int value = matrix[mid / m][mid % m];        // flat index -> (row, column)
            if (value == target) return true;
            if (value < target) lo = mid + 1; else hi = mid - 1;
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
        int[][] grid = {{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}};
        verify(grid, 3, true);
        verify(grid, 13, false);                                   // falls in the gap between two rows
        verify(grid, 1, true);                                     // first cell
        verify(grid, 60, true);                                    // last cell
        verify(grid, 0, false);                                    // smaller than everything
        verify(grid, 61, false);                                   // larger than everything
        verify(new int[][]{{1}}, 1, true);                         // 1 x 1
        verify(new int[][]{{1}}, 2, false);
        verify(new int[][]{{1, 2, 3, 4, 5}}, 4, true);             // single row
        verify(new int[][]{{1}, {3}, {5}}, 3, true);               // single column
        verify(new int[][]{{1}, {3}, {5}}, 4, false);
        verify(new int[][]{{-9, -7}, {-5, -3}, {-1, 0}}, -5, true); // negatives
        verify(new int[0][0], 5, false);                           // empty matrix
        System.out.println("OK P69_SearchInA2DMatrix");
    }
}
