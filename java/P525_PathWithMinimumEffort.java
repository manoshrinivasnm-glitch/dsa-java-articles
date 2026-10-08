import java.util.*;

/** TUF 525 - Path with minimum effort. The effort of a path is its largest absolute height difference between consecutive cells. Minimise it from top-left to bottom-right. */
public class P525_PathWithMinimumEffort {

    static final int[] DR = {-1, 0, 1, 0}, DC = {0, 1, 0, -1};

    /** Approach 1: backtracking over every simple path with pruning. Exponential time, O(n * m) space. */
    static int bruteForce(int[][] heights) {
        int[] best = {Integer.MAX_VALUE};
        walk(heights, 0, 0, 0, new boolean[heights.length][heights[0].length], best);
        return best[0];
    }

    static void walk(int[][] h, int r, int c, int effort, boolean[][] onPath, int[] best) {
        if (effort >= best[0]) return;                        // this path can no longer beat the best one
        if (r == h.length - 1 && c == h[0].length - 1) {
            best[0] = effort;
            return;
        }
        onPath[r][c] = true;
        for (int k = 0; k < 4; k++) {
            int nr = r + DR[k], nc = c + DC[k];
            if (nr >= 0 && nr < h.length && nc >= 0 && nc < h[0].length && !onPath[nr][nc]) {
                walk(h, nr, nc, Math.max(effort, Math.abs(h[nr][nc] - h[r][c])), onPath, best);
            }
        }
        onPath[r][c] = false;
    }

    /** Approach 2: binary search the answer; for a limit, BFS using only steps whose difference is within it. O(n * m * log H) time, O(n * m) space. */
    static int binarySearchBfs(int[][] heights) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int[] row : heights) {
            for (int x : row) {
                min = Math.min(min, x);
                max = Math.max(max, x);
            }
        }
        int lo = 0, hi = max - min;                           // no single step can differ by more than max - min
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canReach(heights, mid)) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    static boolean canReach(int[][] h, int limit) {
        int n = h.length, m = h[0].length;
        boolean[][] seen = new boolean[n][m];
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0});
        seen[0][0] = true;
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int r = cell[0], c = cell[1];
            if (r == n - 1 && c == m - 1) return true;
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (nr >= 0 && nr < n && nc >= 0 && nc < m && !seen[nr][nc]
                        && Math.abs(h[nr][nc] - h[r][c]) <= limit) {
                    seen[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return false;
    }

    /** Approach 3: Dijkstra where a path's cost is the max step instead of the sum. O(n * m * log(n * m)) time, O(n * m) space. */
    static int optimal(int[][] heights) {
        int n = heights.length, m = heights[0].length;
        int[][] effort = new int[n][m];
        for (int[] row : effort) Arrays.fill(row, Integer.MAX_VALUE);
        effort[0][0] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));   // {effort, row, col}
        pq.add(new int[]{0, 0, 0});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int e = top[0], r = top[1], c = top[2];
            if (e > effort[r][c]) continue;                   // stale entry
            if (r == n - 1 && c == m - 1) return e;           // first pop of the target is final
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (nr < 0 || nr >= n || nc < 0 || nc >= m) continue;
                int next = Math.max(e, Math.abs(heights[nr][nc] - heights[r][c]));
                if (next < effort[nr][nc]) {
                    effort[nr][nc] = next;
                    pq.add(new int[]{next, nr, nc});
                }
            }
        }
        return effort[n - 1][m - 1];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] heights, int expected) {
        String name = Arrays.deepToString(heights);
        check(bruteForce(heights) == expected, "bruteForce " + name + " got " + bruteForce(heights));
        check(binarySearchBfs(heights) == expected, "binarySearchBfs " + name + " got " + binarySearchBfs(heights));
        check(optimal(heights) == expected, "optimal " + name + " got " + optimal(heights));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2, 2}, {3, 8, 2}, {5, 3, 5}}, 2);
        verify(new int[][]{{1, 2, 3}, {3, 8, 4}, {5, 3, 5}}, 1);
        verify(new int[][]{{1, 2, 1, 1, 1}, {1, 2, 1, 2, 1}, {1, 2, 1, 2, 1}, {1, 2, 1, 2, 1}, {1, 1, 1, 2, 1}}, 0);
        verify(new int[][]{{7}}, 0);                                                  // single cell: no steps at all
        verify(new int[][]{{1, 10, 6, 7, 9, 10, 4, 9}}, 9);                           // one row: every step is forced
        verify(new int[][]{{1, 100}, {2, 3}}, 1);                                     // detour avoids the cliff
        verify(new int[][]{{3}, {1}, {4}, {1}, {5}}, 4);                              // one column
        System.out.println("OK P525_PathWithMinimumEffort");
    }
}
