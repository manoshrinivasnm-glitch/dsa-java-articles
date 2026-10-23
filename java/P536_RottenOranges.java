import java.util.*;

/** TUF 536 - Rotten Oranges. 0 = empty, 1 = fresh, 2 = rotten. Each minute rot spreads to 4-neighbours; return minutes until no fresh orange is left, or -1. */
public class P536_RottenOranges {

    static final int[][] DIRS = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };   // down, up, right, left

    /** Approach 1: simulate minute by minute, rescanning the whole grid each time. O((n * m)^2) time, O(n * m) space. */
    static int bruteForce(int[][] grid) {
        int n = grid.length, m = grid[0].length;
        int[][] g = new int[n][];                      // work on a copy so the input stays intact
        for (int i = 0; i < n; i++) g[i] = grid[i].clone();
        int minutes = 0;                               // minutes simulated so far
        while (true) {                                 // simulate one minute per round
            List<int[]> toRot = new ArrayList<>();     // fresh oranges that rot during this minute
            for (int r = 0; r < n; r++) {              // scan the whole grid every minute
                for (int c = 0; c < m; c++) {
                    if (g[r][c] != 1) continue;        // only fresh oranges can change
                    for (int[] d : DIRS) {
                        int nr = r + d[0], nc = c + d[1];
                        // neighbour inside the grid and already rotten
                        if (nr >= 0 && nr < n && nc >= 0 && nc < m && g[nr][nc] == 2) {
                            // touches an orange that was rotten at the start of this minute
                            toRot.add(new int[]{r, c});
                            break;
                        }
                    }
                }
            }
            if (toRot.isEmpty()) break;                // nothing changed: the spread has stopped
            for (int[] p : toRot) g[p[0]][p[1]] = 2;   // apply after the scan, so rot moves one step per minute
            minutes++;
        }
        for (int[] row : g) {
            for (int v : row) if (v == 1) return -1;   // some fresh orange can never be reached
        }
        return minutes;
    }

    /** Approach 2: multi-source BFS. All rotten oranges start in the queue; one BFS level = one minute. O(n * m) time and space. */
    static int optimal(int[][] grid) {
        int n = grid.length, m = grid[0].length;
        int[][] g = new int[n][];                  // work on a copy so the input stays intact
        for (int i = 0; i < n; i++) g[i] = grid[i].clone();
        Deque<int[]> queue = new ArrayDeque<>();   // rotten cells whose neighbours are still to be infected
        int fresh = 0;                             // fresh oranges not yet reached
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (g[r][c] == 2) queue.add(new int[]{r, c});   // rotten oranges are the BFS sources
                else if (g[r][c] == 1) fresh++;
            }
        }
        int minutes = 0;
        while (!queue.isEmpty() && fresh > 0) {    // one round of this loop = one minute
            int size = queue.size();               // everything in the queue rotted at the same minute
            for (int k = 0; k < size; k++) {       // spread from this minute's cells only
                int[] cell = queue.poll();
                for (int[] d : DIRS) {
                    int nr = cell[0] + d[0], nc = cell[1] + d[1];
                    // skip cells off the grid or not fresh
                    if (nr < 0 || nr >= n || nc < 0 || nc >= m || g[nr][nc] != 1) continue;
                    g[nr][nc] = 2;                 // rot it now so no other cell enqueues it again
                    fresh--;
                    queue.add(new int[]{nr, nc});
                }
            }
            minutes++;                             // this minute's spread is done
        }
        return fresh == 0 ? minutes : -1;          // -1 when some orange was never reached
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int expected) {
        int[][] before = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) before[i] = grid[i].clone();
        check(bruteForce(grid) == expected, "bruteForce expected " + expected + " got " + bruteForce(grid));
        check(optimal(grid) == expected, "optimal expected " + expected + " got " + optimal(grid));
        check(Arrays.deepEquals(grid, before), "the input grid must not be modified");
    }

    public static void main(String[] args) {
        verify(new int[][]{ {2, 1, 1}, {1, 1, 0}, {0, 1, 1} }, 4);   // LeetCode example 1
        verify(new int[][]{ {2, 1, 1}, {0, 1, 1}, {1, 0, 1} }, -1);  // bottom-left orange is cut off
        verify(new int[][]{ {0, 2} }, 0);                            // nothing fresh to begin with
        verify(new int[][]{ {0} }, 0);                               // no oranges at all
        verify(new int[][]{ {1} }, -1);                              // fresh with no rotten source
        verify(new int[][]{ {2, 1, 1, 1, 1} }, 4);                   // one source, a row of five
        verify(new int[][]{ {2, 1, 1, 1, 2} }, 2);                   // two sources meet in the middle
        verify(new int[][]{ {2, 2}, {1, 1}, {0, 0}, {2, 0} }, 1);    // rot does not jump over empty cells
        verify(new int[][]{ {1, 0, 2} }, -1);                        // an empty cell blocks the spread

        // 30 x 30 all fresh with one rotten corner: the far corner rots after 29 + 29 minutes
        int[][] big = new int[30][30];
        for (int[] row : big) Arrays.fill(row, 1);
        big[0][0] = 2;
        verify(big, 58);

        System.out.println("OK P536_RottenOranges");
    }
}
