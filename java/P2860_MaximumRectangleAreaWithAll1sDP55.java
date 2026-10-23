import java.util.*;

/** TUF 2860 - Maximum Rectangle Area with all 1's. Largest all-ones axis-aligned rectangle in a 0/1 matrix. */
public class P2860_MaximumRectangleAreaWithAll1sDP55 {

    /** Approach 1: every pair of corners, checked in O(1) with 2D prefix sums. O(n^2 m^2) time, O(nm) space. */
    static int bruteForce(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length, best = 0;
        int[][] pre = new int[n + 1][m + 1];                    // pre[i][j] = ones in rows < i, cols < j
        for (int i = 0; i < n; i++) {
            // ones in the rectangle (0,0)..(i,j), by inclusion-exclusion
            for (int j = 0; j < m; j++) pre[i + 1][j + 1] = matrix[i][j] + pre[i][j + 1] + pre[i + 1][j] - pre[i][j];
        }
        for (int r1 = 0; r1 < n; r1++)                          // top-left corner (r1, c1)
            for (int c1 = 0; c1 < m; c1++)
                for (int r2 = r1; r2 < n; r2++)                 // bottom-right corner (r2, c2)
                    for (int c2 = c1; c2 < m; c2++) {
                        int area = (r2 - r1 + 1) * (c2 - c1 + 1);
                        // ones inside (r1,c1)..(r2,c2), again by inclusion-exclusion
                        int ones = pre[r2 + 1][c2 + 1] - pre[r1][c2 + 1] - pre[r2 + 1][c1] + pre[r1][c1];
                        if (ones == area) best = Math.max(best, area);   // every cell is 1: a valid rectangle
                    }
        return best;
    }

    /** Approach 2: column heights per row, then extend each bar to the left. O(n m^2) time, O(m) space. */
    static int better(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length, best = 0;
        int[] h = new int[m];                                   // h[j] = run of ones ending here in column j
        for (int[] row : matrix) {
            for (int j = 0; j < m; j++) h[j] = row[j] == 1 ? h[j] + 1 : 0;   // ones ending at this row
            for (int j = 0; j < m; j++) {                       // rectangle whose right edge is column j
                int minH = Integer.MAX_VALUE;                   // shortest bar between k and j
                for (int k = j; k >= 0 && h[k] > 0; k--) {      // walk left until a zero-height column
                    minH = Math.min(minH, h[k]);
                    best = Math.max(best, minH * (j - k + 1));   // columns k..j, as tall as the shortest bar
                }
            }
        }
        return best;
    }

    /** Approach 3: each row is a histogram; solve it with a monotonic stack. O(n m) time, O(m) space. */
    static int optimal(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length, best = 0;
        int[] h = new int[m];                                   // h[j] = run of ones ending here in column j
        for (int[] row : matrix) {
            for (int j = 0; j < m; j++) h[j] = row[j] == 1 ? h[j] + 1 : 0;   // grow or reset each column's bar
            best = Math.max(best, largestInHistogram(h));       // best rectangle whose bottom is this row
        }
        return best;
    }

    static int largestInHistogram(int[] h) {
        int m = h.length, best = 0;
        Deque<Integer> stack = new ArrayDeque<>();              // indices of bars with increasing heights
        for (int i = 0; i <= m; i++) {                          // i == m is a sentinel step
            int cur = i == m ? 0 : h[i];                        // a final bar of height 0 empties the stack
            while (!stack.isEmpty() && h[stack.peek()] >= cur) {   // top bar cannot extend to i: settle it
                int height = h[stack.pop()];                    // the popped bar's height limits this rectangle
                int left = stack.isEmpty() ? -1 : stack.peek(); // nearest smaller bar on the left
                best = Math.max(best, height * (i - left - 1)); // i is the nearest smaller-or-equal on the right
            }
            stack.push(i);
        }
        return best;
    }

    /** Approach 4: carry height, left and right limits row to row (no stack). O(n m) time, O(m) space. */
    static int optimalDp(int[][] matrix) {
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length, best = 0;
        int[] height = new int[m], left = new int[m], right = new int[m];   // bar height and its [left, right) span
        Arrays.fill(right, m);                                  // no limit on the right yet
        for (int[] row : matrix) {
            int curLeft = 0, curRight = m;                      // boundaries of the current run of ones in this row
            for (int j = 0; j < m; j++) height[j] = row[j] == 1 ? height[j] + 1 : 0;   // grow or reset each bar
            for (int j = 0; j < m; j++) {                       // scan left to right for the left limits
                if (row[j] == 1) left[j] = Math.max(left[j], curLeft);   // in a run: tighten to this run's start
                else { left[j] = 0; curLeft = j + 1; }          // zero: reset; next run starts at j + 1
            }
            for (int j = m - 1; j >= 0; j--) {                  // scan right to left for the right limits
                if (row[j] == 1) right[j] = Math.min(right[j], curRight);   // in a run: tighten to this run's end
                else { right[j] = m; curRight = j; }            // zero: reset; runs to the left end before j
            }
            // rectangle of height[j] spanning columns left[j] .. right[j] - 1
            for (int j = 0; j < m; j++) best = Math.max(best, height[j] * (right[j] - left[j]));
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] matrix, int expected) {
        String in = Arrays.deepToString(matrix);
        check(bruteForce(matrix) == expected, "bruteForce failed for " + in);
        check(better(matrix) == expected, "better failed for " + in);
        check(optimal(matrix) == expected, "optimal failed for " + in);
        check(optimalDp(matrix) == expected, "optimalDp failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 0, 1, 0, 0}, {1, 0, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 0, 0, 1, 0}}, 6);
        verify(new int[][]{{1, 1, 1}, {1, 1, 1}}, 6);           // the whole matrix
        verify(new int[][]{{0, 1}, {1, 0}}, 1);                 // only single cells
        verify(new int[][]{{1, 1, 0, 1}, {1, 1, 0, 1}, {0, 1, 1, 1}}, 4);   // a 2x2 block beats the 3x1 column
        verify(new int[][]{{0, 1, 1, 0}, {1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 0, 0}}, 8);
        verify(new int[][]{{0}}, 0);                            // edge: no ones
        verify(new int[][]{{1}}, 1);                            // edge: single one
        verify(new int[][]{}, 0);                               // edge: empty matrix

        Random rnd = new Random(2860);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(6), m = 1 + rnd.nextInt(6);
            int[][] g = new int[n][m];
            for (int[] row : g) for (int j = 0; j < m; j++) row[j] = rnd.nextInt(4) == 0 ? 0 : 1;
            verify(g, bruteForce(g));
        }
        System.out.println("OK P2860_MaximumRectangleAreaWithAll1sDP55");
    }
}
