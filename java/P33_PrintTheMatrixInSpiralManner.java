import java.util.*;

/** TUF 33 - Spiral matrix. Return the elements of an n x m matrix in clockwise spiral order, starting at the top-left corner. */
public class P33_PrintTheMatrixInSpiralManner {

    /** Approach 1: walk one cell at a time and turn right whenever the next cell is outside the matrix or already visited. O(n*m) time, O(n*m) space. */
    static List<Integer> simulation(int[][] matrix) {
        List<Integer> ans = new ArrayList<>();
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length;
        if (n == 0 || m == 0) return ans;
        boolean[][] visited = new boolean[n][m];
        int[] dr = {0, 1, 0, -1};                     // right, down, left, up
        int[] dc = {1, 0, -1, 0};
        int r = 0, c = 0, dir = 0;
        for (int step = 0; step < n * m; step++) {
            ans.add(matrix[r][c]);
            visited[r][c] = true;
            int nr = r + dr[dir], nc = c + dc[dir];
            if (nr < 0 || nr >= n || nc < 0 || nc >= m || visited[nr][nc]) {
                dir = (dir + 1) % 4;                  // turn clockwise
                nr = r + dr[dir];
                nc = c + dc[dir];
            }
            r = nr;
            c = nc;
        }
        return ans;
    }

    /** Approach 2: four boundaries that shrink after each side is printed. O(n*m) time, O(1) extra space. */
    static List<Integer> optimal(int[][] matrix) {
        List<Integer> ans = new ArrayList<>();
        int n = matrix.length, m = n == 0 ? 0 : matrix[0].length;
        int top = 0, bottom = n - 1, left = 0, right = m - 1;
        while (top <= bottom && left <= right) {
            for (int j = left; j <= right; j++) ans.add(matrix[top][j]);        // top row, left to right
            top++;
            for (int i = top; i <= bottom; i++) ans.add(matrix[i][right]);      // right column, top to bottom
            right--;
            if (top <= bottom) {                                                 // bottom row, right to left
                for (int j = right; j >= left; j--) ans.add(matrix[bottom][j]);
                bottom--;
            }
            if (left <= right) {                                                 // left column, bottom to top
                for (int i = bottom; i >= top; i--) ans.add(matrix[i][left]);
                left++;
            }
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] matrix, List<Integer> expected) {
        check(simulation(matrix).equals(expected), "simulation " + Arrays.deepToString(matrix) + " -> " + simulation(matrix));
        check(optimal(matrix).equals(expected), "optimal " + Arrays.deepToString(matrix) + " -> " + optimal(matrix));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, List.of(1, 2, 3, 6, 9, 8, 7, 4, 5));
        verify(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}, List.of(1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7));
        verify(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}, List.of(1, 2, 3, 6, 9, 12, 11, 10, 7, 4, 5, 8)); // taller than wide
        verify(new int[][]{{7}}, List.of(7));                                                 // 1 x 1
        verify(new int[][]{{1, 2, 3, 4, 5}}, List.of(1, 2, 3, 4, 5));                         // single row
        verify(new int[][]{{1}, {2}, {3}, {4}}, List.of(1, 2, 3, 4));                         // single column
        verify(new int[][]{{1, 2}, {3, 4}}, List.of(1, 2, 4, 3));                             // 2 x 2
        verify(new int[][]{{1, 2}, {3, 4}, {5, 6}}, List.of(1, 2, 4, 6, 5, 3));               // 3 x 2: inner pass is a single column
        verify(new int[0][0], List.of());                                                     // empty
        System.out.println("OK P33_PrintTheMatrixInSpiralManner");
    }
}
