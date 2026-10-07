import java.util.*;

/** TUF 873 - Sudoku Solver. Fill the '.' cells of a 9x9 board so that every row, column and 3x3 box holds the digits 1-9 exactly once. */
public class P873_SudokuSolver {

    /** Approach 1: find the first empty cell, try the digits 1-9 and validate each by scanning its row, column and box. Returns false when the board cannot be completed. */
    static boolean bruteForce(char[][] board) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != '.') continue;
                for (char d = '1'; d <= '9'; d++) {
                    if (canPlace(board, r, c, d)) {
                        board[r][c] = d;
                        if (bruteForce(board)) return true;
                        board[r][c] = '.';               // undo and try the next digit
                    }
                }
                return false;                            // no digit fits this cell: backtrack
            }
        }
        return true;                                     // no empty cell is left
    }

    private static boolean canPlace(char[][] board, int r, int c, char d) {
        int br = r - r % 3, bc = c - c % 3;              // top-left corner of the 3x3 box
        for (int i = 0; i < 9; i++) {
            if (board[r][i] == d) return false;
            if (board[i][c] == d) return false;
            if (board[br + i / 3][bc + i % 3] == d) return false;
        }
        return true;
    }

    private static int boxOf(int r, int c) {
        return (r / 3) * 3 + c / 3;
    }

    /** Approach 2: keep a digit bitmask per row, column and box so each placement test is O(1); cells are still filled in row-major order. */
    static boolean better(char[][] board) {
        int[] rows = new int[9], cols = new int[9], boxes = new int[9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != '.') {
                    int bit = 1 << (board[r][c] - '0');
                    rows[r] |= bit;
                    cols[c] |= bit;
                    boxes[boxOf(r, c)] |= bit;
                }
            }
        }
        return fillInOrder(board, 0, rows, cols, boxes);
    }

    private static boolean fillInOrder(char[][] board, int pos, int[] rows, int[] cols, int[] boxes) {
        while (pos < 81 && board[pos / 9][pos % 9] != '.') pos++;
        if (pos == 81) return true;
        int r = pos / 9, c = pos % 9, b = boxOf(r, c);
        int used = rows[r] | cols[c] | boxes[b];
        for (int d = 1; d <= 9; d++) {
            int bit = 1 << d;
            if ((used & bit) != 0) continue;
            board[r][c] = (char) ('0' + d);
            rows[r] |= bit; cols[c] |= bit; boxes[b] |= bit;
            if (fillInOrder(board, pos + 1, rows, cols, boxes)) return true;
            rows[r] ^= bit; cols[c] ^= bit; boxes[b] ^= bit;
            board[r][c] = '.';
        }
        return false;
    }

    private static final int ALL = 0b1111111110;         // bits 1..9 set

    /** Approach 3: O(1) masks plus the most-constrained-cell heuristic: always branch on the empty cell with the fewest candidates. */
    static boolean optimal(char[][] board) {
        int[] rows = new int[9], cols = new int[9], boxes = new int[9];
        int empty = 0;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == '.') {
                    empty++;
                } else {
                    int bit = 1 << (board[r][c] - '0');
                    rows[r] |= bit;
                    cols[c] |= bit;
                    boxes[boxOf(r, c)] |= bit;
                }
            }
        }
        return fillMostConstrained(board, empty, rows, cols, boxes);
    }

    private static boolean fillMostConstrained(char[][] board, int empty, int[] rows, int[] cols, int[] boxes) {
        if (empty == 0) return true;
        int bestR = -1, bestC = -1, bestCand = 0, bestCount = 10;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != '.') continue;
                int cand = ALL & ~(rows[r] | cols[c] | boxes[boxOf(r, c)]);
                int count = Integer.bitCount(cand);
                if (count < bestCount) {
                    bestCount = count;
                    bestCand = cand;
                    bestR = r;
                    bestC = c;
                }
            }
        }
        if (bestCount == 0) return false;                // an empty cell has no legal digit: dead end
        int b = boxOf(bestR, bestC);
        for (int cand = bestCand; cand != 0; cand &= cand - 1) {
            int bit = cand & -cand;                      // lowest remaining candidate
            int d = Integer.numberOfTrailingZeros(bit);
            board[bestR][bestC] = (char) ('0' + d);
            rows[bestR] |= bit; cols[bestC] |= bit; boxes[b] |= bit;
            if (fillMostConstrained(board, empty - 1, rows, cols, boxes)) return true;
            rows[bestR] ^= bit; cols[bestC] ^= bit; boxes[b] ^= bit;
            board[bestR][bestC] = '.';
        }
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static char[][] parse(String... rows) {
        char[][] b = new char[9][];
        for (int i = 0; i < 9; i++) b[i] = rows[i].toCharArray();
        return b;
    }

    static char[][] copyOf(char[][] b) {
        char[][] c = new char[9][];
        for (int i = 0; i < 9; i++) c[i] = b[i].clone();
        return c;
    }

    static int digitBit(char ch) {
        return ch >= '1' && ch <= '9' ? 1 << (ch - '0') : 0;
    }

    /** Independent check: every given digit kept, and each row, column and box contains exactly the digits 1-9. */
    static boolean isSolution(char[][] board, char[][] puzzle) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (puzzle[r][c] != '.' && board[r][c] != puzzle[r][c]) return false;
            }
        }
        for (int i = 0; i < 9; i++) {
            int rowMask = 0, colMask = 0, boxMask = 0;
            for (int j = 0; j < 9; j++) {
                rowMask |= digitBit(board[i][j]);
                colMask |= digitBit(board[j][i]);
                boxMask |= digitBit(board[(i / 3) * 3 + j / 3][(i % 3) * 3 + j % 3]);
            }
            if (rowMask != ALL || colMask != ALL || boxMask != ALL) return false;
        }
        return true;
    }

    static void verifySolvable(char[][] puzzle, char[][] expected) {
        char[][] a = copyOf(puzzle), b = copyOf(puzzle), c = copyOf(puzzle);
        check(bruteForce(a), "bruteForce reported no solution");
        check(better(b), "better reported no solution");
        check(optimal(c), "optimal reported no solution");
        check(isSolution(a, puzzle), "bruteForce produced an invalid grid");
        check(isSolution(b, puzzle), "better produced an invalid grid");
        check(isSolution(c, puzzle), "optimal produced an invalid grid");
        if (expected != null) {
            check(Arrays.deepEquals(a, expected) && Arrays.deepEquals(b, expected) && Arrays.deepEquals(c, expected),
                    "solution differs from the known unique answer");
        }
    }

    static void verifyUnsolvable(char[][] puzzle) {
        char[][] a = copyOf(puzzle), b = copyOf(puzzle), c = copyOf(puzzle);
        check(!bruteForce(a) && !better(b) && !optimal(c), "an unsolvable board was reported as solved");
        check(Arrays.deepEquals(a, puzzle), "bruteForce must restore the board after failing");
    }

    public static void main(String[] args) {
        char[][] leetcode = parse("53..7....", "6..195...", ".98....6.", "8...6...3", "4..8.3..1",
                                  "7...2...6", ".6....28.", "...419..5", "....8..79");
        char[][] leetcodeSolved = parse("534678912", "672195348", "198342567", "859761423", "426853791",
                                        "713924856", "961537284", "287419635", "345286179");
        verifySolvable(leetcode, leetcodeSolved);

        char[][] euler = parse("..3.2.6..", "9..3.5..1", "..18.64..", "..81.29..", "7.......8",
                               "..67.82..", "..26.95..", "8..2.3..9", "..5.1.3..");
        char[][] eulerSolved = parse("483921657", "967345821", "251876493", "548132976", "729564138",
                                     "136798245", "372689514", "814253769", "695417382");
        verifySolvable(euler, eulerSolved);

        // Arto Inkala's "world's hardest sudoku": few givens, deep backtracking for the row-major solvers.
        char[][] inkala = parse("8........", "..36.....", ".7..9.2..", ".5...7...", "....457..",
                                "...1...3.", "..1....68", "..85...1.", ".9....4..");
        char[][] inkalaSolved = parse("812753649", "943682175", "675491283", "154237896", "369845721",
                                      "287169534", "521974368", "438526917", "796318452");
        verifySolvable(inkala, inkalaSolved);

        verifySolvable(leetcodeSolved, leetcodeSolved);  // already complete: nothing to do

        char[][] oneHole = copyOf(leetcodeSolved);
        oneHole[4][4] = '.';
        verifySolvable(oneHole, leetcodeSolved);        // a single empty cell

        // Row 0 is missing only a 9, but column 8 already holds a 9: no completion exists.
        char[][] impossible = parse("12345678.", "........9", ".........", ".........", ".........",
                                    ".........", ".........", ".........", ".........");
        verifyUnsolvable(impossible);
        System.out.println("OK P873_SudokuSolver");
    }
}
