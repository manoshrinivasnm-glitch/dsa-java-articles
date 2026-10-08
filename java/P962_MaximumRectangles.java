import java.util.*;

/** TUF 962 - Maximum Rectangles (Maximal Rectangle). Area of the largest all-'1' rectangle in a binary matrix. */
public class P962_MaximumRectangles {

    /** Approach 1: try every rectangle, count its 1s with a 2D prefix sum. O(n^2 * m^2) time, O(n * m) space. */
    static int bruteForce(char[][] matrix) {
        int n = matrix.length;
        if (n == 0) return 0;
        int m = matrix[0].length;
        int[][] pre = new int[n + 1][m + 1];           // pre[i][j] = number of 1s in rows < i and columns < j
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                pre[i + 1][j + 1] = pre[i][j + 1] + pre[i + 1][j] - pre[i][j] + (matrix[i][j] == '1' ? 1 : 0);
        int best = 0;
        for (int r1 = 0; r1 < n; r1++)
            for (int c1 = 0; c1 < m; c1++)
                for (int r2 = r1; r2 < n; r2++)
                    for (int c2 = c1; c2 < m; c2++) {
                        int area = (r2 - r1 + 1) * (c2 - c1 + 1);
                        int ones = pre[r2 + 1][c2 + 1] - pre[r1][c2 + 1] - pre[r2 + 1][c1] + pre[r1][c1];
                        if (ones == area) best = Math.max(best, area);
                    }
        return best;
    }

    /** Approach 2: run of 1s ending at each cell, then walk upward from every cell as a bottom-right corner. O(n^2 * m) time, O(n * m) space. */
    static int better(char[][] matrix) {
        int n = matrix.length;
        if (n == 0) return 0;
        int m = matrix[0].length;
        int[][] width = new int[n][m];                 // number of consecutive 1s ending at (i, j), going left
        int best = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (matrix[i][j] == '0') continue;
                width[i][j] = (j == 0 ? 0 : width[i][j - 1]) + 1;
                int minWidth = width[i][j];
                for (int k = i; k >= 0 && minWidth > 0; k--) {
                    minWidth = Math.min(minWidth, width[k][j]);
                    best = Math.max(best, minWidth * (i - k + 1));
                }
            }
        }
        return best;
    }

    /** Approach 3: each row is the base of a histogram; solve largest rectangle in histogram per row. O(n * m) time, O(m) space. */
    static int optimal(char[][] matrix) {
        if (matrix.length == 0) return 0;
        int m = matrix[0].length, best = 0;
        int[] heights = new int[m];
        for (char[] row : matrix) {
            for (int j = 0; j < m; j++) heights[j] = row[j] == '1' ? heights[j] + 1 : 0;
            best = Math.max(best, largestRectangle(heights));
        }
        return best;
    }

    static int largestRectangle(int[] heights) {
        int n = heights.length, best = 0;
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!st.isEmpty() && heights[st.peek()] >= h) {
                int height = heights[st.pop()];
                int leftBoundary = st.isEmpty() ? -1 : st.peek();
                best = Math.max(best, height * (i - leftBoundary - 1));
            }
            st.push(i);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static char[][] grid(String... rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) g[i] = rows[i].toCharArray();
        return g;
    }

    static void verify(int expected, String... rows) {
        int b = bruteForce(grid(rows)), m = better(grid(rows)), o = optimal(grid(rows));
        String name = Arrays.toString(rows);
        check(b == expected, "bruteForce " + name + " -> " + b);
        check(m == expected, "better " + name + " -> " + m);
        check(o == expected, "optimal " + name + " -> " + o);
    }

    public static void main(String[] args) {
        verify(6, "10100", "10111", "11111", "10010");
        verify(0, "0");                                  // no 1 at all
        verify(1, "1");                                  // single cell
        verify(6, "111", "111");                         // whole matrix
        verify(8, "0110", "1111", "1111", "0110");       // tie between a wide and a tall rectangle
        verify(6, "1101", "1101", "1111");
        verify(4, "1", "1", "0", "1", "1", "1", "1");    // single column, run broken by a 0
        verify(0);                                       // empty matrix
        System.out.println("OK P962_MaximumRectangles");
    }
}
