import java.util.*;

/** TUF 537 - Surrounded Regions. Flip every 'O' region that cannot reach the border into 'X', in place. */
public class P537_SurroundedRegions {

    static final int[] DR = {-1, 0, 1, 0}, DC = {0, 1, 0, -1};   // row/col offsets for up, right, down, left

    /** Approach 1: for every 'O', run a fresh BFS to see whether its region touches the border. O((n*m)^2) time, O(n*m) space. */
    static void bruteForce(char[][] board) {
        int n = board.length, m = board[0].length;
        boolean[][] capture = new boolean[n][m];         // O cells that end up flipped to X
        for (int i = 0; i < n; i++)                      // test every O on its own
            for (int j = 0; j < m; j++)
                // an O that cannot reach the border is surrounded
                if (board[i][j] == 'O' && !reachesBorder(board, i, j)) capture[i][j] = true;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                // flip afterwards so the searches see the original board
                if (capture[i][j]) board[i][j] = 'X';
    }

    static boolean reachesBorder(char[][] board, int sr, int sc) {
        int n = board.length, m = board[0].length;
        boolean[][] seen = new boolean[n][m];            // cells this BFS has already queued
        Deque<int[]> q = new ArrayDeque<>();             // BFS over this O's region
        q.offer(new int[]{sr, sc});
        seen[sr][sc] = true;                             // mark when queued so no cell is queued twice
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            int r = cell[0], c = cell[1];
            // border reached: the region survives
            if (r == 0 || c == 0 || r == n - 1 || c == m - 1) return true;
            for (int k = 0; k < 4; k++) {                // interior cell, so all 4 neighbours are inside the grid
                int nr = r + DR[k], nc = c + DC[k];      // neighbour cell
                // unseen O neighbour: same region, explore it
                if (board[nr][nc] == 'O' && !seen[nr][nc]) { seen[nr][nc] = true; q.offer(new int[]{nr, nc}); }
            }
        }
        return false;
    }

    /** Approach 2: DFS from every border 'O' marks the safe cells; every other 'O' is captured. O(n*m) time, O(n*m) space. */
    static void optimalDfs(char[][] board) {
        int n = board.length, m = board[0].length;
        boolean[][] safe = new boolean[n][m];            // safe = O connected to the border
        for (int i = 0; i < n; i++) {                    // walk the left and right columns
            markSafe(board, i, 0, safe);
            markSafe(board, i, m - 1, safe);
        }
        for (int j = 0; j < m; j++) {                    // walk the top and bottom rows
            markSafe(board, 0, j, safe);
            markSafe(board, n - 1, j, safe);
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                // every O not reached from the border is captured
                if (board[i][j] == 'O' && !safe[i][j]) board[i][j] = 'X';
    }

    static void markSafe(char[][] board, int r, int c, boolean[][] safe) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length) return;   // off the grid
        if (board[r][c] != 'O' || safe[r][c]) return;    // not an O, or already marked
        safe[r][c] = true;                               // this O is linked to the border
        // spread to all four neighbours
        for (int k = 0; k < 4; k++) markSafe(board, r + DR[k], c + DC[k], safe);
    }

    /** Approach 3: the same idea with a multi-source BFS, which needs no deep recursion. O(n*m) time, O(n*m) space. */
    static void optimalBfs(char[][] board) {
        int n = board.length, m = board[0].length;
        boolean[][] safe = new boolean[n][m];            // safe = O connected to the border
        Deque<int[]> q = new ArrayDeque<>();             // multi-source BFS queue
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                boolean border = i == 0 || j == 0 || i == n - 1 || j == m - 1;   // on the outer frame?
                // every border O is safe and starts the BFS
                if (border && board[i][j] == 'O') { safe[i][j] = true; q.offer(new int[]{i, j}); }
            }
        }
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            for (int k = 0; k < 4; k++) {
                int r = cell[0] + DR[k], c = cell[1] + DC[k];   // neighbour cell
                // in bounds, an O, and not marked yet
                if (r >= 0 && c >= 0 && r < n && c < m && board[r][c] == 'O' && !safe[r][c]) {
                    safe[r][c] = true;                   // reachable from a border O, so it survives
                    q.offer(new int[]{r, c});
                }
            }
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                // unreached O cells are surrounded: flip them
                if (board[i][j] == 'O' && !safe[i][j]) board[i][j] = 'X';
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static char[][] parse(String... rows) {
        char[][] b = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) b[i] = rows[i].toCharArray();
        return b;
    }

    static char[][] copy(char[][] b) {
        char[][] c = new char[b.length][];
        for (int i = 0; i < b.length; i++) c[i] = b[i].clone();
        return c;
    }

    static void verify(char[][] input, char[][] expected) {
        char[][] a = copy(input), b = copy(input), c = copy(input);
        bruteForce(a);
        optimalDfs(b);
        optimalBfs(c);
        String tag = Arrays.deepToString(input);
        check(Arrays.deepEquals(a, expected), "bruteForce " + tag);
        check(Arrays.deepEquals(b, expected), "optimalDfs " + tag);
        check(Arrays.deepEquals(c, expected), "optimalBfs " + tag);
    }

    public static void main(String[] args) {
        verify(parse("XXXX", "XOOX", "XXOX", "XOXX"), parse("XXXX", "XXXX", "XXXX", "XOXX"));
        verify(parse("XOXXX", "XOOXX", "XXXOX", "XOXXO"), parse("XOXXX", "XOOXX", "XXXXX", "XOXXO"));
        verify(parse("XXXXX", "XOOOX", "XOXOX", "XOOOX", "XXXXX"), parse("XXXXX", "XXXXX", "XXXXX", "XXXXX", "XXXXX")); // ring with no exit
        verify(parse("OOO", "OXO", "OOO"), parse("OOO", "OXO", "OOO"));       // everything on the border
        verify(parse("X"), parse("X"));                                      // single cell
        verify(parse("O"), parse("O"));
        verify(parse("OXO"), parse("OXO"));                                  // single row: all cells are border cells
        Random rnd = new Random(537);                                        // seeded random boards vs brute force
        for (int t = 0; t < 30; t++) {
            int n = 1 + rnd.nextInt(15), m = 1 + rnd.nextInt(15);
            char[][] g = new char[n][m];
            for (char[] row : g) for (int j = 0; j < m; j++) row[j] = rnd.nextInt(5) < 2 ? 'X' : 'O';
            char[][] exp = copy(g);
            bruteForce(exp);
            verify(g, exp);
        }
        System.out.println("OK P537_SurroundedRegions");
    }
}
