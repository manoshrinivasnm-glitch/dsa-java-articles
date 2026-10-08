import java.util.*;

/** TUF 527 - Shortest Distance in a Binary Maze. Cells with 1 are open, 0 are walls; moves go up/down/left/right. Return the fewest moves from src to dst, or -1. */
public class P527_ShortestDistanceInABinaryMaze {

    static final int[] DR = {-1, 0, 1, 0}, DC = {0, 1, 0, -1};

    /** Approach 1: backtracking DFS over every simple path, keeping the shortest. Exponential time, O(n * m) space. */
    static int bruteForce(int[][] grid, int[] src, int[] dst) {
        if (grid[src[0]][src[1]] == 0 || grid[dst[0]][dst[1]] == 0) return -1;
        int[] best = {Integer.MAX_VALUE};
        explore(grid, src[0], src[1], dst, 0, new boolean[grid.length][grid[0].length], best);
        return best[0] == Integer.MAX_VALUE ? -1 : best[0];
    }

    static void explore(int[][] grid, int r, int c, int[] dst, int steps, boolean[][] onPath, int[] best) {
        if (steps >= best[0]) return;                         // cannot beat the best path found so far
        if (r == dst[0] && c == dst[1]) {
            best[0] = steps;
            return;
        }
        onPath[r][c] = true;
        for (int k = 0; k < 4; k++) {
            int nr = r + DR[k], nc = c + DC[k];
            if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length
                    && grid[nr][nc] == 1 && !onPath[nr][nc]) {
                explore(grid, nr, nc, dst, steps + 1, onPath, best);
            }
        }
        onPath[r][c] = false;                                 // free the cell for other paths
    }

    /** Approach 2: Dijkstra on the grid with a min-heap of {dist, row, col}. O(n * m * log(n * m)) time, O(n * m) space. */
    static int dijkstra(int[][] grid, int[] src, int[] dst) {
        int n = grid.length, m = grid[0].length;
        if (grid[src[0]][src[1]] == 0 || grid[dst[0]][dst[1]] == 0) return -1;
        int[][] dist = new int[n][m];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[src[0]][src[1]] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.add(new int[]{0, src[0], src[1]});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0], r = top[1], c = top[2];
            if (d > dist[r][c]) continue;                     // stale heap entry
            if (r == dst[0] && c == dst[1]) return d;
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (nr >= 0 && nr < n && nc >= 0 && nc < m && grid[nr][nc] == 1 && d + 1 < dist[nr][nc]) {
                    dist[nr][nc] = d + 1;
                    pq.add(new int[]{d + 1, nr, nc});
                }
            }
        }
        return -1;
    }

    /** Approach 3: plain BFS; with every move costing 1 the queue is already in distance order. O(n * m) time, O(n * m) space. */
    static int optimal(int[][] grid, int[] src, int[] dst) {
        int n = grid.length, m = grid[0].length;
        if (grid[src[0]][src[1]] == 0 || grid[dst[0]][dst[1]] == 0) return -1;
        int[][] dist = new int[n][m];
        for (int[] row : dist) Arrays.fill(row, -1);          // -1 = not reached yet
        dist[src[0]][src[1]] = 0;
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{src[0], src[1]});
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int r = cell[0], c = cell[1];
            if (r == dst[0] && c == dst[1]) return dist[r][c];
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (nr >= 0 && nr < n && nc >= 0 && nc < m && grid[nr][nc] == 1 && dist[nr][nc] == -1) {
                    dist[nr][nc] = dist[r][c] + 1;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int[] src, int[] dst, int expected) {
        String name = Arrays.deepToString(grid) + " " + Arrays.toString(src) + " -> " + Arrays.toString(dst);
        check(bruteForce(grid, src, dst) == expected, "bruteForce " + name + " got " + bruteForce(grid, src, dst));
        check(dijkstra(grid, src, dst) == expected, "dijkstra " + name + " got " + dijkstra(grid, src, dst));
        check(optimal(grid, src, dst) == expected, "optimal " + name + " got " + optimal(grid, src, dst));
    }

    public static void main(String[] args) {
        int[][] g1 = {{1, 1, 1, 1}, {1, 1, 0, 1}, {1, 1, 1, 1}, {1, 1, 0, 0}, {1, 0, 0, 1}};
        verify(g1, new int[]{0, 1}, new int[]{2, 2}, 3);
        int[][] g2 = {{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 0}, {1, 0, 1, 0, 1}};
        verify(g2, new int[]{0, 0}, new int[]{3, 4}, -1);                                 // destination walled in
        verify(g1, new int[]{2, 3}, new int[]{2, 3}, 0);                                  // source equals destination
        verify(new int[][]{{1, 0}, {1, 1}}, new int[]{0, 0}, new int[]{0, 1}, -1);        // destination is a wall
        int[][] snake = {{1, 1, 1, 1}, {0, 0, 0, 1}, {1, 1, 1, 1}, {1, 0, 0, 0}, {1, 1, 1, 1}};
        verify(snake, new int[]{0, 0}, new int[]{4, 3}, 13);                              // forced to wind through the maze
        verify(new int[][]{{1}}, new int[]{0, 0}, new int[]{0, 0}, 0);                    // 1 x 1 grid
        int[][] open = new int[5][5];
        for (int[] row : open) Arrays.fill(row, 1);
        verify(open, new int[]{0, 0}, new int[]{4, 4}, 8);                                // open field: Manhattan distance
        System.out.println("OK P527_ShortestDistanceInABinaryMaze");
    }
}
