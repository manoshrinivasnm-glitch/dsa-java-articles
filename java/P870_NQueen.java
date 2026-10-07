import java.util.*;

/** TUF 870 - N Queen. Place n queens on an n x n board so that no two attack each other; return every arrangement. */
public class P870_NQueen {

    /** Approach 1: fill the board row by row; before placing at (row, col) scan the column and both upper diagonals. O(n) per check. */
    static List<List<String>> bruteForce(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');
        placeScan(0, n, board, result);
        return result;
    }

    private static void placeScan(int row, int n, char[][] board, List<List<String>> result) {
        if (row == n) {
            result.add(toStrings(board));
            return;
        }
        for (int col = 0; col < n; col++) {
            if (isSafe(board, row, col, n)) {
                board[row][col] = 'Q';
                placeScan(row + 1, n, board, result);
                board[row][col] = '.';                   // undo and try the next column
            }
        }
    }

    /** Only rows above `row` hold queens, so three upward directions need checking. */
    private static boolean isSafe(char[][] board, int row, int col, int n) {
        for (int r = row - 1; r >= 0; r--) if (board[r][col] == 'Q') return false;
        for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) if (board[r][c] == 'Q') return false;
        for (int r = row - 1, c = col + 1; r >= 0 && c < n; r--, c++) if (board[r][c] == 'Q') return false;
        return true;
    }

    private static List<String> toStrings(char[][] board) {
        List<String> rows = new ArrayList<>();
        for (char[] row : board) rows.add(new String(row));
        return rows;
    }

    /** Approach 2: O(1) safety check with three occupancy arrays: column, diagonal (row - col + n - 1) and anti-diagonal (row + col). */
    static List<List<String>> optimal(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queenCol = new int[n];                     // queenCol[r] = column of the queen placed in row r
        boolean[] cols = new boolean[n];
        boolean[] diag = new boolean[2 * n];             // indexed by row - col + n - 1, constant along "\" lines
        boolean[] anti = new boolean[2 * n];             // indexed by row + col, constant along "/" lines
        placeFast(0, n, queenCol, cols, diag, anti, result);
        return result;
    }

    private static void placeFast(int row, int n, int[] queenCol, boolean[] cols, boolean[] diag, boolean[] anti,
                                  List<List<String>> result) {
        if (row == n) {
            List<String> rows = new ArrayList<>();
            for (int r = 0; r < n; r++) {
                char[] line = new char[n];
                Arrays.fill(line, '.');
                line[queenCol[r]] = 'Q';
                rows.add(new String(line));
            }
            result.add(rows);
            return;
        }
        for (int col = 0; col < n; col++) {
            int d = row - col + n - 1, a = row + col;
            if (cols[col] || diag[d] || anti[a]) continue;
            cols[col] = diag[d] = anti[a] = true;
            queenCol[row] = col;
            placeFast(row + 1, n, queenCol, cols, diag, anti, result);
            cols[col] = diag[d] = anti[a] = false;       // undo
        }
    }

    /** Variant: count the solutions only, keeping the three occupancy sets as bitmasks (n <= 31). */
    static int countBitmask(int n) {
        return countRows(0, n, 0, 0, 0);
    }

    private static int countRows(int row, int n, int cols, int diag, int anti) {
        if (row == n) return 1;
        int free = ~(cols | diag | anti) & ((1 << n) - 1); // columns not attacked in this row
        int total = 0;
        while (free != 0) {
            int bit = free & -free;                      // lowest free column
            free -= bit;
            total += countRows(row + 1, n, cols | bit, (diag | bit) << 1, (anti | bit) >> 1);
        }
        return total;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Independent validity check: n rows of length n, exactly one Q per row, no shared column or diagonal. */
    static boolean isValid(List<String> sol, int n) {
        if (sol.size() != n) return false;
        int[] col = new int[n];
        for (int r = 0; r < n; r++) {
            String row = sol.get(r);
            if (row.length() != n) return false;
            int q = -1;
            for (int c = 0; c < n; c++) {
                if (row.charAt(c) == 'Q') {
                    if (q != -1) return false;
                    q = c;
                } else if (row.charAt(c) != '.') {
                    return false;
                }
            }
            if (q == -1) return false;
            col[r] = q;
        }
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                if (col[a] == col[b] || Math.abs(col[a] - col[b]) == b - a) return false;
            }
        }
        return true;
    }

    static void verify(int n, int expectedCount) {
        List<List<String>> brute = bruteForce(n), fast = optimal(n);
        check(brute.size() == expectedCount, "bruteForce count for n=" + n + " is " + brute.size());
        check(fast.size() == expectedCount, "optimal count for n=" + n + " is " + fast.size());
        check(countBitmask(n) == expectedCount, "countBitmask wrong for n=" + n);
        for (List<String> s : brute) check(isValid(s, n), "invalid board from bruteForce: " + s);
        for (List<String> s : fast) check(isValid(s, n), "invalid board from optimal: " + s);
        check(new HashSet<>(brute).size() == brute.size(), "bruteForce produced a duplicate board for n=" + n);
        check(new HashSet<>(brute).equals(new HashSet<>(fast)), "approaches disagree for n=" + n);
    }

    public static void main(String[] args) {
        int[] expected = {1, 0, 0, 2, 10, 4, 40, 92};    // n = 1 .. 8; n = 2 and n = 3 have no solution
        for (int n = 1; n <= 8; n++) verify(n, expected[n - 1]);
        List<List<String>> four = optimal(4);
        check(four.contains(List.of(".Q..", "...Q", "Q...", "..Q.")), "missing a known n=4 solution");
        check(four.contains(List.of("..Q.", "Q...", "...Q", ".Q..")), "missing the other n=4 solution");
        check(optimal(1).equals(List.of(List.of("Q"))), "n=1 must give the single-cell board");
        System.out.println("OK P870_NQueen");
    }
}
