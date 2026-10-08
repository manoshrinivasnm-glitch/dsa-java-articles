import java.util.*;

/** TUF 533 - Number of enclaves. Count land cells (1) from which you cannot walk off the grid through 4-directional land moves. */
public class P533_NumberOfEnclaves {

    static final int[] DR = {-1, 0, 1, 0}, DC = {0, 1, 0, -1};

    /** Approach 1: for every land cell, run a fresh BFS and ask whether it reaches the border. O((n*m)^2) time, O(n*m) space. */
    static int bruteForce(int[][] grid) {
        int n = grid.length, m = grid[0].length, count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 1 && !canEscape(grid, i, j)) count++;
            }
        }
        return count;
    }

    static boolean canEscape(int[][] grid, int sr, int sc) {
        int n = grid.length, m = grid[0].length;
        boolean[][] seen = new boolean[n][m];
        Deque<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{sr, sc});
        seen[sr][sc] = true;
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            int r = cell[0], c = cell[1];
            if (r == 0 || c == 0 || r == n - 1 || c == m - 1) return true;   // one more step walks off the grid
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (grid[nr][nc] == 1 && !seen[nr][nc]) { seen[nr][nc] = true; q.offer(new int[]{nr, nc}); }
            }
        }
        return false;
    }

    /** Approach 2: multi-source BFS from all border land; whatever land stays unvisited is an enclave. O(n*m) time, O(n*m) space. */
    static int optimalBfs(int[][] grid) {
        int n = grid.length, m = grid[0].length;
        boolean[][] vis = new boolean[n][m];
        Deque<int[]> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                boolean border = i == 0 || j == 0 || i == n - 1 || j == m - 1;
                if (border && grid[i][j] == 1) { vis[i][j] = true; q.offer(new int[]{i, j}); }
            }
        }
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            for (int k = 0; k < 4; k++) {
                int r = cell[0] + DR[k], c = cell[1] + DC[k];
                if (r >= 0 && c >= 0 && r < n && c < m && grid[r][c] == 1 && !vis[r][c]) {
                    vis[r][c] = true;
                    q.offer(new int[]{r, c});
                }
            }
        }
        int count = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (grid[i][j] == 1 && !vis[i][j]) count++;
        return count;
    }

    /** Approach 3: count each island with DFS and keep it only if no cell of it lies on the border. O(n*m) time, O(n*m) space. */
    static int optimalDfs(int[][] grid) {
        int n = grid.length, m = grid[0].length, count = 0;
        boolean[][] vis = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 1 && !vis[i][j]) {
                    int[] info = new int[2];             // {cells in this island, 1 if it touches the border}
                    explore(grid, i, j, vis, info);
                    if (info[1] == 0) count += info[0];
                }
            }
        }
        return count;
    }

    static void explore(int[][] grid, int r, int c, boolean[][] vis, int[] info) {
        int n = grid.length, m = grid[0].length;
        if (r < 0 || c < 0 || r >= n || c >= m || grid[r][c] == 0 || vis[r][c]) return;
        vis[r][c] = true;
        info[0]++;
        if (r == 0 || c == 0 || r == n - 1 || c == m - 1) info[1] = 1;
        for (int k = 0; k < 4; k++) explore(grid, r + DR[k], c + DC[k], vis, info);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int expected) {
        String tag = Arrays.deepToString(grid);
        check(bruteForce(grid) == expected, "bruteForce " + tag);
        check(optimalBfs(grid) == expected, "optimalBfs " + tag);
        check(optimalDfs(grid) == expected, "optimalDfs " + tag);
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 0, 0, 0}, {1, 0, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}}, 3);
        verify(new int[][]{{0, 1, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 0, 0}}, 0);
        verify(new int[][]{{0, 0, 0, 0, 0}, {0, 1, 1, 1, 0}, {0, 1, 1, 1, 0}, {0, 1, 1, 1, 0}, {0, 0, 0, 0, 0}}, 9);
        verify(new int[][]{{0, 0, 0, 0, 0}, {0, 1, 1, 1, 0}, {0, 1, 0, 1, 1}, {0, 1, 1, 1, 0}, {0, 0, 0, 0, 0}}, 0); // one cell leaks out
        verify(new int[][]{{1}}, 0);                                           // single land cell is on the border
        verify(new int[][]{{0}}, 0);
        verify(new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, 0);               // centre connects to the border
        Random rnd = new Random(533);                                          // seeded random grids vs brute force
        for (int t = 0; t < 30; t++) {
            int n = 1 + rnd.nextInt(15), m = 1 + rnd.nextInt(15);
            int[][] g = new int[n][m];
            for (int[] row : g) for (int j = 0; j < m; j++) row[j] = rnd.nextInt(2);
            verify(g, bruteForce(g));
        }
        System.out.println("OK P533_NumberOfEnclaves");
    }
}
