import java.util.*;

/**
 * TUF 769 - Ninja and his Friends (Cherry Pickup II, LeetCode 1463).
 * Two friends start at (0, 0) and (0, c - 1); each step both move one row down to column j - 1, j or j + 1.
 * Maximise the chocolates collected; a cell visited by both at the same time is counted once.
 */
public class P769_NinjaAndHisFriends {

    static final int NEG = -1_000_000_000;      // "this move leaves the grid"; smaller than any real total

    /** Approach 1: recursion on (row, col1, col2), trying all 9 pairs of moves. O(9^r) time, O(r) stack. */
    static int recursive(int[][] grid) {
        return solve(0, 0, grid[0].length - 1, grid);
    }

    /** Best total from row i down to the last row when the friends stand at columns j1 and j2. */
    static int solve(int i, int j1, int j2, int[][] grid) {
        int c = grid[0].length;
        if (j1 < 0 || j1 >= c || j2 < 0 || j2 >= c) return NEG;    // walked off the grid
        int here = (j1 == j2) ? grid[i][j1] : grid[i][j1] + grid[i][j2];
        if (i == grid.length - 1) return here;                       // last row: nothing below
        int best = NEG;
        for (int d1 = -1; d1 <= 1; d1++) {
            for (int d2 = -1; d2 <= 1; d2++) {
                best = Math.max(best, solve(i + 1, j1 + d1, j2 + d2, grid));
            }
        }
        return here + best;
    }

    /** Approach 2: memoization on the r * c * c states. O(9 * r * c^2) time, O(r * c^2) space plus the stack. */
    static int memoization(int[][] grid) {
        int r = grid.length, c = grid[0].length;
        int[][][] dp = new int[r][c][c];
        for (int[][] plane : dp) for (int[] row : plane) Arrays.fill(row, -1);   // totals are >= 0, so -1 = unknown
        return memo(0, 0, c - 1, grid, dp);
    }

    static int memo(int i, int j1, int j2, int[][] grid, int[][][] dp) {
        int c = grid[0].length;
        if (j1 < 0 || j1 >= c || j2 < 0 || j2 >= c) return NEG;
        if (dp[i][j1][j2] != -1) return dp[i][j1][j2];
        int here = (j1 == j2) ? grid[i][j1] : grid[i][j1] + grid[i][j2];
        if (i == grid.length - 1) return dp[i][j1][j2] = here;
        int best = NEG;
        for (int d1 = -1; d1 <= 1; d1++) {
            for (int d2 = -1; d2 <= 1; d2++) {
                best = Math.max(best, memo(i + 1, j1 + d1, j2 + d2, grid, dp));
            }
        }
        return dp[i][j1][j2] = here + best;
    }

    /** Approach 3: tabulation from the last row up. O(9 * r * c^2) time, O(r * c^2) space. */
    static int tabulation(int[][] grid) {
        int r = grid.length, c = grid[0].length;
        int[][][] dp = new int[r][c][c];
        for (int j1 = 0; j1 < c; j1++) {
            for (int j2 = 0; j2 < c; j2++) {
                dp[r - 1][j1][j2] = (j1 == j2) ? grid[r - 1][j1] : grid[r - 1][j1] + grid[r - 1][j2];
            }
        }
        for (int i = r - 2; i >= 0; i--) {
            for (int j1 = 0; j1 < c; j1++) {
                for (int j2 = 0; j2 < c; j2++) {
                    int best = NEG;
                    for (int d1 = -1; d1 <= 1; d1++) {
                        for (int d2 = -1; d2 <= 1; d2++) {
                            int n1 = j1 + d1, n2 = j2 + d2;
                            if (n1 >= 0 && n1 < c && n2 >= 0 && n2 < c) best = Math.max(best, dp[i + 1][n1][n2]);
                        }
                    }
                    int here = (j1 == j2) ? grid[i][j1] : grid[i][j1] + grid[i][j2];
                    dp[i][j1][j2] = here + best;
                }
            }
        }
        return dp[0][0][c - 1];
    }

    /** Approach 4: keep only the c x c layer of the row below. O(9 * r * c^2) time, O(c^2) space. */
    static int spaceOptimized(int[][] grid) {
        int r = grid.length, c = grid[0].length;
        int[][] below = new int[c][c];
        for (int j1 = 0; j1 < c; j1++) {
            for (int j2 = 0; j2 < c; j2++) {
                below[j1][j2] = (j1 == j2) ? grid[r - 1][j1] : grid[r - 1][j1] + grid[r - 1][j2];
            }
        }
        for (int i = r - 2; i >= 0; i--) {
            int[][] cur = new int[c][c];
            for (int j1 = 0; j1 < c; j1++) {
                for (int j2 = 0; j2 < c; j2++) {
                    int best = NEG;
                    for (int d1 = -1; d1 <= 1; d1++) {
                        for (int d2 = -1; d2 <= 1; d2++) {
                            int n1 = j1 + d1, n2 = j2 + d2;
                            if (n1 >= 0 && n1 < c && n2 >= 0 && n2 < c) best = Math.max(best, below[n1][n2]);
                        }
                    }
                    int here = (j1 == j2) ? grid[i][j1] : grid[i][j1] + grid[i][j2];
                    cur[j1][j2] = here + best;
                }
            }
            below = cur;
        }
        return below[0][c - 1];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int expected) {
        String in = Arrays.deepToString(grid);
        check(recursive(grid) == expected, "recursive failed for " + in);
        check(memoization(grid) == expected, "memoization failed for " + in);
        check(tabulation(grid) == expected, "tabulation failed for " + in);
        check(spaceOptimized(grid) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{2, 3, 1, 2}, {3, 4, 2, 2}, {5, 6, 3, 5}}, 21);            // (2 + 4 + 6) + (2 + 2 + 5)
        verify(new int[][]{{3, 1, 1}, {2, 5, 1}, {1, 5, 5}, {2, 1, 1}}, 24);           // LeetCode example 1
        verify(new int[][]{{1, 0, 0, 0, 0, 0, 1}, {2, 0, 0, 0, 0, 3, 0}, {2, 0, 9, 0, 0, 0, 0},
                           {0, 3, 0, 5, 4, 0, 0}, {1, 0, 2, 3, 0, 0, 6}}, 28);          // LeetCode example 2
        verify(new int[][]{{7}}, 7);                                                     // edge: one cell, both stand on it
        verify(new int[][]{{1}, {2}, {3}}, 6);                                           // edge: one column, always shared
        verify(new int[][]{{0, 0}, {0, 0}}, 0);                                          // all zeros
        verify(new int[][]{{0, 1, 4}, {2, 1, 0}, {1, 1, 3}}, 11);                        // planning Alice alone first gives only 9
        verify(new int[][]{{1, 1}, {9, 1}, {1, 1}}, 14);                                 // only one friend should take the 9: it counts once

        // Cross-check on seeded random grids (small enough for plain recursion).
        Random rnd = new Random(769);
        for (int t = 0; t < 150; t++) {
            int r = 1 + rnd.nextInt(5), c = 1 + rnd.nextInt(5);
            int[][] grid = new int[r][c];
            for (int[] row : grid) for (int j = 0; j < c; j++) row[j] = rnd.nextInt(10);
            verify(grid, tabulation(grid));
        }
        System.out.println("OK P769_NinjaAndHisFriends");
    }
}
