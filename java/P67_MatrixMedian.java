import java.util.*;

/** TUF 67 - Matrix Median. Every row of the n x m matrix is sorted and n * m is odd. Return the median of all n * m values. */
public class P67_MatrixMedian {

    /** Approach 1: copy every value into one array, sort it, read the middle. O(n*m log(n*m)) time, O(n*m) space. */
    static int bruteForce(int[][] mat) {
        int n = mat.length, m = mat[0].length;
        int[] all = new int[n * m];
        int k = 0;
        for (int[] row : mat) for (int x : row) all[k++] = x;
        Arrays.sort(all);
        return all[(n * m) / 2];
    }

    /** How many values are <= x, scanning every cell. O(n * m). */
    static int countLinear(int[][] mat, int x) {
        int count = 0;
        for (int[] row : mat) for (int v : row) if (v <= x) count++;
        return count;
    }

    /** Approach 2: binary search on the value. The median is the smallest x with more than half the cells <= x; counting scans the whole matrix. O(n*m log(range)) time, O(1) space. */
    static int better(int[][] mat) {
        int n = mat.length, m = mat[0].length;
        int lo = Integer.MAX_VALUE, hi = Integer.MIN_VALUE;
        for (int[] row : mat) { lo = Math.min(lo, row[0]); hi = Math.max(hi, row[m - 1]); }
        int half = (n * m) / 2;
        while (lo <= hi) {
            int mid = (int) Math.floorDiv((long) lo + hi, 2);   // long: lo + hi overflows int when the range is wide
            if (countLinear(mat, mid) <= half) lo = mid + 1;    // too few values <= mid: the median is larger
            else hi = mid - 1;                                  // mid already covers the middle position
        }
        return lo;
    }

    /** Number of values <= x in one sorted row: the index of the first value greater than x (upper bound). O(log m). */
    static int upperBound(int[] row, int x) {
        int lo = 0, hi = row.length - 1, ans = row.length;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (row[mid] > x) { ans = mid; hi = mid - 1; }
            else lo = mid + 1;
        }
        return ans;
    }

    /** Approach 3: the same binary search on the value, but each row is counted with an upper bound. O(n log m log(range)) time, O(1) space. */
    static int optimal(int[][] mat) {
        int n = mat.length, m = mat[0].length;
        int lo = Integer.MAX_VALUE, hi = Integer.MIN_VALUE;
        for (int[] row : mat) { lo = Math.min(lo, row[0]); hi = Math.max(hi, row[m - 1]); }
        int half = (n * m) / 2;
        while (lo <= hi) {
            int mid = (int) Math.floorDiv((long) lo + hi, 2);
            int count = 0;
            for (int[] row : mat) count += upperBound(row, mid);
            if (count <= half) lo = mid + 1;
            else hi = mid - 1;
        }
        return lo;
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
        verify(new int[][]{{1, 3, 5}, {2, 6, 9}, {3, 6, 9}}, 5);
        verify(new int[][]{{1, 3, 8}, {2, 3, 4}, {1, 2, 5}}, 3);
        verify(new int[][]{{1, 10, 20}, {2, 11, 21}, {3, 12, 22}, {4, 13, 23}, {5, 14, 24}}, 12);   // 5 x 3
        verify(new int[][]{{1, 2, 3, 4, 5}}, 3);                                   // single row
        verify(new int[][]{{1}, {2}, {3}}, 2);                                     // single column
        verify(new int[][]{{7}}, 7);                                               // single cell
        verify(new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, 1);                   // all equal
        verify(new int[][]{{-5, -3, 0}, {-4, -2, 7}, {-6, 1, 9}}, -2);             // negatives
        verify(new int[][]{{0, 2_000_000_000, 2_000_000_000}, {1, 1, 2_000_000_000}, {0, 0, 2_000_000_000}}, 1);   // wide value range
        verify(new int[][]{{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}}, 0);         // lo + hi overflows int
        System.out.println("OK P67_MatrixMedian");
    }
}
