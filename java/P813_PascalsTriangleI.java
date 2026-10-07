import java.util.*;

/** TUF 813 - Pascal's Triangle. Three variants: one element (row r, column c, both 1-indexed), one full row, and the first n rows. */
public class P813_PascalsTriangleI {

    // ------------------------------------------------------------ Variant 1: a single element

    /** Build the rows one by one with additions and read the cell. O(r^2) time, O(r) space. */
    static long elementBrute(int r, int c) {
        long[] row = {1};
        for (int i = 2; i <= r; i++) {
            long[] next = new long[i];
            next[0] = 1;
            next[i - 1] = 1;
            for (int j = 1; j < i - 1; j++) next[j] = row[j - 1] + row[j];
            row = next;
        }
        return row[c - 1];
    }

    /** The cell is C(r-1, c-1); compute it directly. O(c) time, O(1) space. */
    static long elementOptimal(int r, int c) {
        return nCr(r - 1, c - 1);
    }

    /** C(n, k) as n/1 * (n-1)/2 * ... ; after i steps the product is exactly C(n, i), so every division is exact. */
    static long nCr(int n, int k) {
        if (k < 0 || k > n) return 0;
        k = Math.min(k, n - k);                     // C(n, k) == C(n, n-k): use the shorter loop
        long res = 1;
        for (int i = 0; i < k; i++) {
            res = res * (n - i) / (i + 1);
        }
        return res;
    }

    // ------------------------------------------------------------ Variant 2: one full row

    /** Compute every entry of row r independently with nCr. O(r^2) time, O(r) for the answer. */
    static long[] rowBrute(int r) {
        long[] row = new long[r];
        for (int c = 1; c <= r; c++) row[c - 1] = nCr(r - 1, c - 1);
        return row;
    }

    /** Each entry follows from the previous one: row[c] = row[c-1] * (r-c) / c. O(r) time, O(r) for the answer. */
    static long[] rowOptimal(int r) {
        long[] row = new long[r];
        row[0] = 1;
        for (int c = 1; c < r; c++) {
            row[c] = row[c - 1] * (r - c) / c;
        }
        return row;
    }

    // ------------------------------------------------------------ Variant 3: the first n rows

    /** Compute every cell with nCr. O(n^3) time, O(n^2) for the answer. */
    static long[][] triangleBrute(int n) {
        long[][] tri = new long[n][];
        for (int r = 1; r <= n; r++) {
            tri[r - 1] = new long[r];
            for (int c = 1; c <= r; c++) tri[r - 1][c - 1] = nCr(r - 1, c - 1);
        }
        return tri;
    }

    /** Build each row from the row above: every inner cell is the sum of the two cells over it. O(n^2) time, O(n^2) for the answer. */
    static long[][] triangleOptimal(int n) {
        long[][] tri = new long[n][];
        for (int r = 0; r < n; r++) {
            tri[r] = new long[r + 1];
            tri[r][0] = 1;
            tri[r][r] = 1;
            for (int c = 1; c < r; c++) tri[r][c] = tri[r - 1][c - 1] + tri[r - 1][c];
        }
        return tri;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyElement(int r, int c, long expected) {
        check(elementBrute(r, c) == expected, "elementBrute(" + r + "," + c + ") -> " + elementBrute(r, c));
        check(elementOptimal(r, c) == expected, "elementOptimal(" + r + "," + c + ") -> " + elementOptimal(r, c));
    }

    static void verifyRow(int r, long[] expected) {
        check(Arrays.equals(rowBrute(r), expected), "rowBrute(" + r + ") -> " + Arrays.toString(rowBrute(r)));
        check(Arrays.equals(rowOptimal(r), expected), "rowOptimal(" + r + ") -> " + Arrays.toString(rowOptimal(r)));
    }

    static void verifyTriangle(int n, long[][] expected) {
        check(Arrays.deepEquals(triangleBrute(n), expected), "triangleBrute(" + n + ") -> " + Arrays.deepToString(triangleBrute(n)));
        check(Arrays.deepEquals(triangleOptimal(n), expected), "triangleOptimal(" + n + ") -> " + Arrays.deepToString(triangleOptimal(n)));
    }

    public static void main(String[] args) {
        // Variant 1
        verifyElement(5, 3, 6);
        verifyElement(1, 1, 1);                                                  // the apex
        verifyElement(4, 4, 1);                                                  // right edge
        verifyElement(10, 5, 126);                                               // C(9, 4)
        verifyElement(30, 15, 77558760L);                                        // C(29, 14)
        for (int r = 1; r <= 40; r++) {                                          // both methods agree everywhere
            for (int c = 1; c <= r; c++) {
                check(elementBrute(r, c) == elementOptimal(r, c), "mismatch at " + r + "," + c);
            }
        }
        // Variant 2
        verifyRow(1, new long[]{1});                                             // smallest row
        verifyRow(2, new long[]{1, 1});
        verifyRow(5, new long[]{1, 4, 6, 4, 1});
        verifyRow(6, new long[]{1, 5, 10, 10, 5, 1});
        for (int r = 1; r <= 40; r++) {
            long[] row = rowOptimal(r);
            check(Arrays.equals(row, rowBrute(r)), "row mismatch at " + r);
            long sum = 0;
            for (long v : row) sum += v;
            check(sum == 1L << (r - 1), "row " + r + " should sum to 2^(r-1)");
        }
        // Variant 3
        verifyTriangle(0, new long[0][]);                                        // no rows
        verifyTriangle(1, new long[][]{{1}});
        verifyTriangle(5, new long[][]{{1}, {1, 1}, {1, 2, 1}, {1, 3, 3, 1}, {1, 4, 6, 4, 1}});
        verifyTriangle(6, new long[][]{{1}, {1, 1}, {1, 2, 1}, {1, 3, 3, 1}, {1, 4, 6, 4, 1}, {1, 5, 10, 10, 5, 1}});
        long[][] tri = triangleOptimal(40);
        for (int r = 0; r < 40; r++) {
            check(Arrays.equals(tri[r], rowOptimal(r + 1)), "triangle row " + r + " differs from rowOptimal");
        }
        System.out.println("OK P813_PascalsTriangleI");
    }
}
