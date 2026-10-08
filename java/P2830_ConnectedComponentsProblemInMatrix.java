import java.util.*;

/** TUF 2830 - Connected Components Problem in Matrix (number of islands). Count groups of '1' cells; with diagonals = true, cells touching at a corner also connect. */
public class P2830_ConnectedComponentsProblemInMatrix {

    static final int[][] DIRS4 = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
    static final int[][] DIRS8 = { {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1} };

    /** Approach 1: BFS flood from every land cell not yet visited; each flood is one island. O(n * m) time and space. */
    static int bfs(char[][] grid, boolean diagonals) {
        if (grid.length == 0) return 0;
        int n = grid.length, m = grid[0].length;
        int[][] dirs = diagonals ? DIRS8 : DIRS4;
        boolean[][] visited = new boolean[n][m];
        Deque<int[]> queue = new ArrayDeque<>();
        int islands = 0;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (grid[r][c] != '1' || visited[r][c]) continue;
                islands++;                                    // a land cell no earlier flood reached
                visited[r][c] = true;
                queue.add(new int[]{r, c});
                while (!queue.isEmpty()) {
                    int[] cell = queue.poll();
                    for (int[] d : dirs) {
                        int nr = cell[0] + d[0], nc = cell[1] + d[1];
                        if (nr < 0 || nr >= n || nc < 0 || nc >= m) continue;
                        if (grid[nr][nc] == '1' && !visited[nr][nc]) {
                            visited[nr][nc] = true;
                            queue.add(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
        return islands;
    }

    /** Approach 2: the same counting loop with a recursive DFS flood. O(n * m) time, recursion depth up to n * m. */
    static int dfs(char[][] grid, boolean diagonals) {
        if (grid.length == 0) return 0;
        int n = grid.length, m = grid[0].length;
        boolean[][] visited = new boolean[n][m];
        int islands = 0;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (grid[r][c] == '1' && !visited[r][c]) {
                    islands++;
                    flood(grid, r, c, visited, diagonals ? DIRS8 : DIRS4);
                }
            }
        }
        return islands;
    }

    static void flood(char[][] grid, int r, int c, boolean[][] visited, int[][] dirs) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) return;
        if (grid[r][c] != '1' || visited[r][c]) return;
        visited[r][c] = true;
        for (int[] d : dirs) flood(grid, r + d[0], c + d[1], visited, dirs);
    }

    /** Approach 3: union-find on cell ids r * m + c. Count every land cell, subtract one per successful merge. O(n * m * alpha). */
    static int unionFind(char[][] grid, boolean diagonals) {
        if (grid.length == 0) return 0;
        int n = grid.length, m = grid[0].length;
        int[][] dirs = diagonals ? DIRS8 : DIRS4;
        int[] parent = new int[n * m];
        for (int i = 0; i < n * m; i++) parent[i] = i;
        int islands = 0;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (grid[r][c] != '1') continue;
                islands++;                                    // a new island of one cell, until merged
                for (int[] d : dirs) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr < 0 || nr >= n || nc < 0 || nc >= m || grid[nr][nc] != '1') continue;
                    int a = find(parent, r * m + c), b = find(parent, nr * m + nc);
                    if (a != b) {
                        parent[a] = b;
                        islands--;
                    }
                }
            }
        }
        return islands;
    }

    static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
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

    static void verify(char[][] g, int expected8, int expected4) {
        check(bfs(g, true) == expected8 && dfs(g, true) == expected8 && unionFind(g, true) == expected8,
                "8-directional expected " + expected8);
        check(bfs(g, false) == expected4 && dfs(g, false) == expected4 && unionFind(g, false) == expected4,
                "4-directional expected " + expected4);
    }

    public static void main(String[] args) {
        verify(grid("01", "10", "11", "10"), 1, 2);                         // (0,1) touches (1,0) only at a corner
        verify(grid("0111000", "0011010"), 2, 2);                           // a blob and a lone cell
        verify(grid("101", "010", "101"), 1, 5);                            // checkerboard
        verify(grid("11000", "11000", "00100", "00011"), 1, 3);             // diagonal chain of blocks
        verify(grid("000", "000"), 0, 0);                                   // all water
        verify(grid("1"), 1, 1);                                            // single land cell
        verify(grid("0"), 0, 0);                                            // single water cell
        verify(new char[0][], 0, 0);                                        // no rows
        verify(grid("10101"), 3, 3);                                        // one row
        verify(grid("1", "0", "1", "1"), 2, 2);                             // one column

        // 40 x 40 all land, and a grid of isolated land cells at every other row and column
        String full = "1".repeat(40), sparse = "10".repeat(20), water = "0".repeat(40);
        String[] fullRows = new String[40], sparseRows = new String[40];
        for (int i = 0; i < 40; i++) {
            fullRows[i] = full;
            sparseRows[i] = i % 2 == 0 ? sparse : water;
        }
        verify(grid(fullRows), 1, 1);
        verify(grid(sparseRows), 400, 400);                                  // 20 x 20 cells, none touching

        System.out.println("OK P2830_ConnectedComponentsProblemInMatrix");
    }
}
