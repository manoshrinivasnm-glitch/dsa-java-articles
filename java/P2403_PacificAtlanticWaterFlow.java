import java.util.*;

/** TUF 2403 - Pacific Atlantic Water Flow. Return the cells from which rain can reach both oceans, in row-major order. */
public class P2403_PacificAtlanticWaterFlow {

    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = {0, 0, -1, 1};

    /** Approach 1: from every cell, flood downhill and see which ocean edges the water touches. O((m * n)^2) time, O(m * n) space. */
    static List<List<Integer>> bruteForce(int[][] heights) {
        int m = heights.length, n = heights[0].length;
        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                boolean pacific = false, atlantic = false;
                boolean[][] seen = new boolean[m][n];
                Deque<int[]> queue = new ArrayDeque<>();
                seen[r][c] = true;
                queue.add(new int[]{r, c});
                while (!queue.isEmpty() && !(pacific && atlantic)) {
                    int[] cell = queue.poll();
                    int x = cell[0], y = cell[1];
                    if (x == 0 || y == 0) pacific = true;           // top or left edge
                    if (x == m - 1 || y == n - 1) atlantic = true;  // bottom or right edge
                    for (int d = 0; d < 4; d++) {
                        int nx = x + DR[d], ny = y + DC[d];
                        if (nx < 0 || ny < 0 || nx >= m || ny >= n || seen[nx][ny]) continue;
                        if (heights[nx][ny] <= heights[x][y]) {     // water moves to an equal or lower cell
                            seen[nx][ny] = true;
                            queue.add(new int[]{nx, ny});
                        }
                    }
                }
                if (pacific && atlantic) result.add(List.of(r, c));
            }
        }
        return result;
    }

    /** Approach 2: reverse the flow. Climb uphill from each ocean's border once, then intersect. O(m * n) time, O(m * n) space. */
    static List<List<Integer>> optimal(int[][] heights) {
        int m = heights.length, n = heights[0].length;
        boolean[][] pac = new boolean[m][n], atl = new boolean[m][n];
        Deque<int[]> pq = new ArrayDeque<>(), aq = new ArrayDeque<>();
        for (int r = 0; r < m; r++) {
            mark(pac, pq, r, 0);                          // left edge touches the Pacific
            mark(atl, aq, r, n - 1);                      // right edge touches the Atlantic
        }
        for (int c = 0; c < n; c++) {
            mark(pac, pq, 0, c);                          // top edge
            mark(atl, aq, m - 1, c);                      // bottom edge
        }
        climb(heights, pac, pq);
        climb(heights, atl, aq);
        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (pac[r][c] && atl[r][c]) result.add(List.of(r, c));
            }
        }
        return result;
    }

    static void mark(boolean[][] reach, Deque<int[]> queue, int r, int c) {
        if (reach[r][c]) return;
        reach[r][c] = true;
        queue.add(new int[]{r, c});
    }

    /** Multi-source BFS that only steps to neighbours at least as high: those are cells whose water could flow down to us. */
    static void climb(int[][] heights, boolean[][] reach, Deque<int[]> queue) {
        int m = heights.length, n = heights[0].length;
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int d = 0; d < 4; d++) {
                int nr = cell[0] + DR[d], nc = cell[1] + DC[d];
                if (nr < 0 || nc < 0 || nr >= m || nc >= n || reach[nr][nc]) continue;
                if (heights[nr][nc] >= heights[cell[0]][cell[1]]) mark(reach, queue, nr, nc);
            }
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<List<Integer>> cells(int[][] pairs) {
        List<List<Integer>> out = new ArrayList<>();
        for (int[] p : pairs) out.add(List.of(p[0], p[1]));
        return out;
    }

    static void verify(int[][] heights, int[][] expected) {
        List<List<Integer>> want = cells(expected);
        check(bruteForce(heights).equals(want), "bruteForce " + Arrays.deepToString(heights) + " got " + bruteForce(heights));
        check(optimal(heights).equals(want), "optimal " + Arrays.deepToString(heights) + " got " + optimal(heights));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2, 2, 3, 5}, {3, 2, 3, 4, 4}, {2, 4, 5, 3, 1}, {6, 7, 1, 4, 5}, {5, 1, 1, 2, 4}},
               new int[][]{{0, 4}, {1, 3}, {1, 4}, {2, 2}, {3, 0}, {3, 1}, {4, 0}});   // LeetCode example 1
        verify(new int[][]{{1}}, new int[][]{{0, 0}});                                 // single cell touches both
        verify(new int[][]{{3, 3}, {3, 3}}, new int[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}}); // flat: equal heights flow
        verify(new int[][]{{1, 2, 3}}, new int[][]{{0, 0}, {0, 1}, {0, 2}});           // one row: every cell borders both
        verify(new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}}, new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 2}, {2, 0}, {2, 1}, {2, 2}}); // pit in the middle
        verify(new int[][]{{1, 2}, {4, 3}}, new int[][]{{0, 1}, {1, 0}, {1, 1}});      // (0,0) can only go to the Pacific
        System.out.println("OK P2403_PacificAtlanticWaterFlow");
    }
}
