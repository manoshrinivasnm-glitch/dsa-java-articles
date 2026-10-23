import java.util.*;

/** TUF 533 - Number of enclaves. Count land cells (1) from which you cannot walk off the grid through 4-directional land moves. */
public class P533_NumberOfEnclaves {

    static final int[] DR = {-1, 0, 1, 0}, DC = {0, 1, 0, -1};   // row/col offsets for up, right, down, left

    /** Approach 1: for every land cell, run a fresh BFS and ask whether it reaches the border. O((n*m)^2) time, O(n*m) space. */
    static int bruteForce(int[][] grid) {
        int n = grid.length, m = grid[0].length, count = 0;   // count = land cells that can never leave
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // land that cannot escape is an enclave cell
                if (grid[i][j] == 1 && !canEscape(grid, i, j)) count++;
            }
        }
        return count;
    }

    static boolean canEscape(int[][] grid, int sr, int sc) {
        int n = grid.length, m = grid[0].length;
        boolean[][] seen = new boolean[n][m];            // cells this BFS has already queued
        Deque<int[]> q = new ArrayDeque<>();             // BFS over this land region
        q.offer(new int[]{sr, sc});
        seen[sr][sc] = true;                             // mark when queued so no cell is queued twice
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            int r = cell[0], c = cell[1];
            if (r == 0 || c == 0 || r == n - 1 || c == m - 1) return true;   // one more step walks off the grid
            for (int k = 0; k < 4; k++) {                // interior cell, so all 4 neighbours are in the grid
                int nr = r + DR[k], nc = c + DC[k];      // neighbour cell
                // unseen land neighbour: explore it
                if (grid[nr][nc] == 1 && !seen[nr][nc]) { seen[nr][nc] = true; q.offer(new int[]{nr, nc}); }
            }
        }
        return false;
    }

    /** Approach 2: multi-source BFS from all border land; whatever land stays unvisited is an enclave. O(n*m) time, O(n*m) space. */
    static int optimalBfs(int[][] grid) {
        int n = grid.length, m = grid[0].length;
        boolean[][] vis = new boolean[n][m];             // vis = land reachable from the border
        Deque<int[]> q = new ArrayDeque<>();             // multi-source BFS queue
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                boolean border = i == 0 || j == 0 || i == n - 1 || j == m - 1;   // on the outer frame?
                // border land escapes and seeds the BFS
                if (border && grid[i][j] == 1) { vis[i][j] = true; q.offer(new int[]{i, j}); }
            }
        }
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            for (int k = 0; k < 4; k++) {
                int r = cell[0] + DR[k], c = cell[1] + DC[k];   // neighbour cell
                // in bounds, unvisited land
                if (r >= 0 && c >= 0 && r < n && c < m && grid[r][c] == 1 && !vis[r][c]) {
                    vis[r][c] = true;                    // connected to the border, so it can escape
                    q.offer(new int[]{r, c});
                }
            }
        }
        int count = 0;                                   // land cells the BFS never reached
        for (int i = 0; i < n; i++)                      // what the border BFS never reached is enclosed
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
                if (grid[i][j] == 1 && !vis[i][j]) {     // a new island: measure it in one DFS
                    int[] info = new int[2];             // {cells in this island, 1 if it touches the border}
                    explore(grid, i, j, vis, info);
                    if (info[1] == 0) count += info[0];   // never touched the border: all its cells count
                }
            }
        }
        return count;
    }

    static void explore(int[][] grid, int r, int c, boolean[][] vis, int[] info) {
        int n = grid.length, m = grid[0].length;
        // off the grid, water, or already visited
        if (r < 0 || c < 0 || r >= n || c >= m || grid[r][c] == 0 || vis[r][c]) return;
        vis[r][c] = true;
        info[0]++;                                       // one more cell in this island
        // on the border: the whole island can escape
        if (r == 0 || c == 0 || r == n - 1 || c == m - 1) info[1] = 1;
        // flood into the four neighbours
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
