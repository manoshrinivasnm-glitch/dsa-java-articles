import java.util.*;

/** TUF 66 - Find row with maximum 1's. Every row of the binary matrix is sorted (all 0s before all 1s). Return the index of the row holding the most 1s, the smallest index on a tie, or -1 when the matrix has no 1 at all. */
public class P66_FindRowWithMaximum1s {

    /** Approach 1: count the 1s in every row. O(n * m) time, O(1) space. */
    static int bruteForce(int[][] mat) {
        int n = mat.length;
        int bestRow = -1, bestCount = 0;                 // best row so far and its count of 1s
        for (int r = 0; r < n; r++) {                    // check every row
            int count = 0;
            for (int x : mat[r]) if (x == 1) count++;    // count the 1s in row r
            // strictly more 1s: an equal count keeps the earlier row
            if (count > bestCount) { bestCount = count; bestRow = r; }
        }
        return bestRow;
    }

    /** Index of the first 1 in a sorted binary row (row.length when there is none): the lower bound of 1. O(log m). */
    static int firstOne(int[] row) {
        int lo = 0, hi = row.length - 1, ans = row.length;   // ans stays row.length if the row has no 1
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;                // middle index
            if (row[mid] == 1) { ans = mid; hi = mid - 1; }   // a 1: remember it and look further left
            else lo = mid + 1;                           // a 0: the first 1 is to the right
        }
        return ans;
    }

    /** Approach 2: binary search the first 1 of each row; the count of 1s is m minus that index. O(n log m) time, O(1) space. */
    static int better(int[][] mat) {
        int n = mat.length;
        int bestRow = -1, bestCount = 0;                 // best row so far and its count of 1s
        for (int r = 0; r < n; r++) {
            int count = mat[r].length - firstOne(mat[r]);   // 1s run from the first 1 to the row end
            // strictly more 1s: an equal count keeps the earlier row
            if (count > bestCount) { bestCount = count; bestRow = r; }
        }
        return bestRow;
    }

    /** Approach 3: start at the top-right corner; step left on a 1 (this row beats every row above it), step down on a 0. O(n + m) time, O(1) space. */
    static int optimal(int[][] mat) {
        int n = mat.length;
        if (n == 0) return -1;                           // empty matrix: no row at all
        int m = mat[0].length;
        int r = 0, c = m - 1, bestRow = -1;              // start at the top-right corner
        while (r < n && c >= 0) {                        // stop when we fall off the bottom or the left edge
            if (mat[r][c] == 1) { bestRow = r; c--; }   // row r has at least m - c ones: a new strict maximum
            else r++;                                   // row r has at most m - c - 1 ones: it cannot beat bestRow
        }
        return bestRow;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] mat, int expected) {
        String in = Arrays.deepToString(mat);
        check(bruteForce(mat) == expected, "bruteForce " + in);
        check(better(mat) == expected, "better " + in);
        check(optimal(mat) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 1, 1}, {0, 0, 1}, {0, 0, 0}}, 0);
        verify(new int[][]{{0, 0, 1}, {0, 1, 1}, {0, 0, 1}}, 1);
        verify(new int[][]{{0, 0, 0, 1}, {0, 0, 1, 1}, {0, 1, 1, 1}, {1, 1, 1, 1}}, 3);   // the staircase visits every row
        verify(new int[][]{{0, 1, 1}, {0, 1, 1}, {0, 0, 1}}, 0);        // tie: the smallest index wins
        verify(new int[][]{{0, 0}, {0, 0}}, -1);                          // no 1 anywhere
        verify(new int[][]{{0, 1}}, 0);                                   // single row
        verify(new int[][]{{0}, {1}, {1}}, 1);                            // single column, tie between rows 1 and 2
        verify(new int[][]{{1}, {1}}, 0);                                 // all ones
        verify(new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 1}}, 2);          // the only 1 sits in the last row
        verify(new int[0][0], -1);                                        // empty matrix
        System.out.println("OK P66_FindRowWithMaximum1s");
    }
}
