import java.util.*;

/** TUF 65 - Find Peak Element - II. In a grid of non-negative values where no two adjacent cells are equal, return {row, col} of any cell strictly greater than its four neighbours (cells outside the grid count as -1). */
public class P65_FindPeakElementII {

    /** Value at (r, c), or -1 outside the grid. */
    static int at(int[][] mat, int r, int c) {
        // outside the grid: -1 is below every real value
        if (r < 0 || r >= mat.length || c < 0 || c >= mat[0].length) return -1;
        return mat[r][c];
    }

    /** Is (r, c) strictly greater than its four neighbours? */
    static boolean isPeak(int[][] mat, int r, int c) {
        int v = mat[r][c];
        // strictly larger than the cells above, below, left and right
        return v > at(mat, r - 1, c) && v > at(mat, r + 1, c) && v > at(mat, r, c - 1) && v > at(mat, r, c + 1);
    }

    /** Approach 1: test every cell against its neighbours. O(n * m) time, O(1) space. */
    static int[] bruteForce(int[][] mat) {
        for (int r = 0; r < mat.length; r++) {           // try every row ...
            for (int c = 0; c < mat[0].length; c++) {    // ... and every column
                if (isPeak(mat, r, c)) return new int[]{r, c};   // the first peak found is a valid answer
            }
        }
        return new int[]{-1, -1};                        // unreachable: every grid has a peak
    }

    /** Approach 2: hill climbing. From (0, 0) keep stepping onto a strictly larger neighbour; values rise along the path, so no cell repeats and the walk ends on a peak. O(n * m) worst case, O(1) space. */
    static int[] hillClimb(int[][] mat) {
        int r = 0, c = 0;                                // start the climb at the top-left cell
        int[] dr = {-1, 1, 0, 0}, dc = {0, 0, -1, 1};    // row/column offsets for up, down, left, right
        while (true) {                                   // climb until no neighbour is larger
            int bestR = r, bestC = c;                    // largest cell around (r, c), itself included
            for (int d = 0; d < 4; d++) {                // look at the four neighbours
                int nr = r + dr[d], nc = c + dc[d];
                if (at(mat, nr, nc) > at(mat, bestR, bestC)) { bestR = nr; bestC = nc; }   // larger: new best
            }
            if (bestR == r && bestC == c) return new int[]{r, c};   // no neighbour is larger: a peak
            r = bestR; c = bestC;                        // step onto the largest neighbour
        }
    }

    /** Row index of the largest value in column c. O(n). */
    static int rowOfColumnMax(int[][] mat, int c) {
        int best = 0;                                    // assume row 0 holds the maximum
        // keep the row with the larger value in column c
        for (int r = 1; r < mat.length; r++) if (mat[r][c] > mat[best][c]) best = r;
        return best;
    }

    /** Approach 3: binary search on columns. Take the maximum of the middle column; if a horizontal neighbour beats it, a peak is guaranteed on that side. O(n log m) time, O(1) space. */
    static int[] optimal(int[][] mat) {
        int m = mat[0].length;
        int lo = 0, hi = m - 1;                          // binary search over column indices
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;                // middle column
            int r = rowOfColumnMax(mat, mid);            // row of the largest value in column mid
            int left = at(mat, r, mid - 1), right = at(mat, r, mid + 1);   // horizontal neighbours, -1 off-grid
            // beats left and right; above/below lose to the column max
            if (mat[r][mid] > left && mat[r][mid] > right) return new int[]{r, mid};
            if (left > mat[r][mid]) hi = mid - 1;        // left is larger: a peak is guaranteed on the left
            else lo = mid + 1;                           // otherwise the right is larger: search right
        }
        return new int[]{-1, -1};                        // unreachable: every grid has a peak
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Every approach must return a valid peak; when the grid has exactly one peak, it must be that one. */
    static void verify(int[][] mat, int[] unique) {
        String in = Arrays.deepToString(mat);
        for (int[] p : new int[][]{bruteForce(mat), hillClimb(mat), optimal(mat)}) {
            check(p[0] >= 0 && p[0] < mat.length && p[1] >= 0 && p[1] < mat[0].length, "out of range " + Arrays.toString(p) + " for " + in);
            check(isPeak(mat, p[0], p[1]), "not a peak " + Arrays.toString(p) + " for " + in);
            if (unique != null) check(Arrays.equals(p, unique), "expected " + Arrays.toString(unique) + " got " + Arrays.toString(p) + " for " + in);
        }
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 4}, {3, 2}}, null);                                   // two peaks: 4 and 3
        verify(new int[][]{{10, 20, 15}, {21, 30, 14}, {7, 16, 32}}, null);          // two peaks: 30 and 32
        verify(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, new int[]{2, 2});       // the only peak is a corner
        verify(new int[][]{{9, 8, 7}, {6, 5, 4}, {3, 2, 1}}, new int[]{0, 0});
        verify(new int[][]{{0, 1, 0}, {1, 2, 1}, {0, 1, 0}}, new int[]{1, 1});       // the only peak is the centre
        verify(new int[][]{{1, 2, 3, 4, 5}}, new int[]{0, 4});                       // single row
        verify(new int[][]{{5}, {4}, {3}, {2}, {1}}, new int[]{0, 0});               // single column
        verify(new int[][]{{7}}, new int[]{0, 0});                                   // single cell
        verify(new int[][]{{4, 2, 5, 1, 4, 5}, {2, 1, 8, 9, 0, 1}, {3, 5, 2, 1, 3, 2}}, null);   // five peaks
        System.out.println("OK P65_FindPeakElementII");
    }
}
