import java.util.*;

/** TUF 534 - Number of islands (A2Z: Number of Distinct Islands). Count islands, and count distinct island shapes up to translation. */
public class P534_NumberOfIslands {

    static final int[] DR = {-1, 0, 1, 0}, DC = {0, 1, 0, -1};

    /** Warm-up: plain island count. Every unvisited land cell starts a new island that DFS then floods. O(n*m) time and space. */
    static int countIslands(int[][] grid) {
        boolean[][] vis = new boolean[grid.length][grid[0].length];
        int islands = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1 && !vis[i][j]) {
                    islands++;
                    collect(grid, i, j, i, j, vis, new ArrayList<>());
                }
            }
        }
        return islands;
    }

    /** DFS that marks one island and appends each cell as (row - r0, col - c0), i.e. relative to the island's first cell. */
    static void collect(int[][] grid, int r, int c, int r0, int c0, boolean[][] vis, List<int[]> cells) {
        if (r < 0 || c < 0 || r >= grid.length || c >= grid[0].length) return;
        if (grid[r][c] == 0 || vis[r][c]) return;
        vis[r][c] = true;
        cells.add(new int[]{r - r0, c - c0});
        for (int k = 0; k < 4; k++) collect(grid, r + DR[k], c + DC[k], r0, c0, vis, cells);
    }

    /** Approach 1: normalise each island, then compare it cell by cell with every distinct shape found so far. O(n*m * D) time. */
    static int bruteForce(int[][] grid) {
        boolean[][] vis = new boolean[grid.length][grid[0].length];
        List<List<int[]>> shapes = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != 1 || vis[i][j]) continue;
                List<int[]> cells = new ArrayList<>();
                collect(grid, i, j, i, j, vis, cells);
                cells.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
                boolean seen = false;
                for (List<int[]> s : shapes) {
                    if (sameShape(s, cells)) { seen = true; break; }
                }
                if (!seen) shapes.add(cells);
            }
        }
        return shapes.size();
    }

    static boolean sameShape(List<int[]> a, List<int[]> b) {
        if (a.size() != b.size()) return false;
        for (int k = 0; k < a.size(); k++) {
            if (a.get(k)[0] != b.get(k)[0] || a.get(k)[1] != b.get(k)[1]) return false;
        }
        return true;
    }

    /** Approach 2: turn each island's relative cells (in DFS order) into a string key and drop it in a HashSet. O(n*m) time and space. */
    static int optimal(int[][] grid) {
        boolean[][] vis = new boolean[grid.length][grid[0].length];
        Set<String> shapes = new HashSet<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != 1 || vis[i][j]) continue;
                List<int[]> cells = new ArrayList<>();
                collect(grid, i, j, i, j, vis, cells);
                StringBuilder key = new StringBuilder();
                for (int[] cell : cells) key.append(cell[0]).append(',').append(cell[1]).append(';');
                shapes.add(key.toString());
            }
        }
        return shapes.size();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int islands, int distinct) {
        String tag = Arrays.deepToString(grid);
        check(countIslands(grid) == islands, "countIslands " + tag);
        check(bruteForce(grid) == distinct, "bruteForce " + tag);
        check(optimal(grid) == distinct, "optimal " + tag);
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 1, 0, 0, 0}, {1, 1, 0, 0, 0}, {0, 0, 0, 1, 1}, {0, 0, 0, 1, 1}}, 2, 1);   // two equal squares
        verify(new int[][]{{1, 1, 0, 1, 1}, {1, 0, 0, 0, 0}, {0, 0, 0, 0, 1}, {1, 1, 0, 1, 1}}, 4, 3);
        verify(new int[][]{{1, 1, 0, 1}, {0, 0, 0, 1}}, 2, 2);                 // a rotated domino is a different shape
        verify(new int[][]{{1, 0, 1}, {0, 1, 0}, {1, 0, 1}}, 5, 1);            // diagonals do not connect
        verify(new int[][]{{1, 0}, {1, 1}, {0, 0}, {0, 1}, {1, 1}}, 2, 2);     // mirror images differ
        verify(new int[][]{{1}}, 1, 1);
        verify(new int[][]{{0, 0}, {0, 0}}, 0, 0);                             // no land
        Random rnd = new Random(534);                                          // seeded random grids: both approaches must agree
        for (int t = 0; t < 40; t++) {
            int n = 1 + rnd.nextInt(12), m = 1 + rnd.nextInt(12);
            int[][] g = new int[n][m];
            for (int[] row : g) for (int j = 0; j < m; j++) row[j] = rnd.nextInt(3) == 0 ? 1 : 0;
            check(bruteForce(g) == optimal(g), "random grid " + Arrays.deepToString(g));
        }
        System.out.println("OK P534_NumberOfIslands");
    }
}
