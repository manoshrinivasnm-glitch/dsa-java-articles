import java.util.*;

/** TUF 65 - Find Peak Element - II. In a grid of non-negative values where no two adjacent cells are equal, return {row, col} of any cell strictly greater than its four neighbours (cells outside the grid count as -1). */
public class P65_FindPeakElementII {

    /** Value at (r, c), or -1 outside the grid. */
    static int at(int[][] mat, int r, int c) {
        if (r < 0 || r >= mat.length || c < 0 || c >= mat[0].length) return -1;
        return mat[r][c];
    }

    /** Is (r, c) strictly greater than its four neighbours? */
    static boolean isPeak(int[][] mat, int r, int c) {
        int v = mat[r][c];
        return v > at(mat, r - 1, c) && v > at(mat, r + 1, c) && v > at(mat, r, c - 1) && v > at(mat, r, c + 1);
    }

    /** Approach 1: test every cell against its neighbours. O(n * m) time, O(1) space. */
    static int[] bruteForce(int[][] mat) {
        for (int r = 0; r < mat.length; r++) {
            for (int c = 0; c < mat[0].length; c++) {
                if (isPeak(mat, r, c)) return new int[]{r, c};
            }
        }
        return new int[]{-1, -1};
    }

    /** Approach 2: hill climbing. From (0, 0) keep stepping onto a strictly larger neighbour; values rise along the path, so no cell repeats and the walk ends on a peak. O(n * m) worst case, O(1) space. */
    static int[] hillClimb(int[][] mat) {
        int r = 0, c = 0;
        int[] dr = {-1, 1, 0, 0}, dc = {0, 0, -1, 1};
        while (true) {
            int bestR = r, bestC = c;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (at(mat, nr, nc) > at(mat, bestR, bestC)) { bestR = nr; bestC = nc; }
            }
            if (bestR == r && bestC == c) return new int[]{r, c};   // no neighbour is larger: a peak
            r = bestR; c = bestC;
        }
    }

    /** Row index of the largest value in column c. O(n). */
    static int rowOfColumnMax(int[][] mat, int c) {
        int best = 0;
        for (int r = 1; r < mat.length; r++) if (mat[r][c] > mat[best][c]) best = r;
        return best;
    }

    /** Approach 3: binary search on columns. Take the maximum of the middle column; if a horizontal neighbour beats it, a peak is guaranteed on that side. O(n log m) time, O(1) space. */
    static int[] optimal(int[][] mat) {
        int m = mat[0].length;
        int lo = 0, hi = m - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int r = rowOfColumnMax(mat, mid);
            int left = at(mat, r, mid - 1), right = at(mat, r, mid + 1);
            if (mat[r][mid] > left && mat[r][mid] > right) return new int[]{r, mid};
            if (left > mat[r][mid]) hi = mid - 1;      // climb towards the larger side
            else lo = mid + 1;
        }
        return new int[]{-1, -1};
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
