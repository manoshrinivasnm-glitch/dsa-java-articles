import java.util.*;

/** TUF 874 - Word Search. Does `word` appear in the grid along a path of side-adjacent cells, each cell used at most once? */
public class P874_WordSearch {

    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = {0, 0, -1, 1};

    /** Approach 1: enumerate every simple path of word.length() cells and compare the spelled string only at the end. Exponential. */
    static boolean bruteForce(char[][] board, String word) {
        int m = board.length, n = m == 0 ? 0 : board[0].length;
        if (word.isEmpty()) return true;
        boolean[][] used = new boolean[m][n];
        StringBuilder path = new StringBuilder();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (enumerate(board, r, c, used, path, word)) return true;
            }
        }
        return false;
    }

    private static boolean enumerate(char[][] board, int r, int c, boolean[][] used, StringBuilder path, String word) {
        used[r][c] = true;
        path.append(board[r][c]);
        boolean found = false;
        if (path.length() == word.length()) {
            found = path.toString().equals(word);
        } else {
            for (int d = 0; d < 4 && !found; d++) {
                int nr = r + DR[d], nc = c + DC[d];
                if (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length && !used[nr][nc]) {
                    found = enumerate(board, nr, nc, used, path, word);
                }
            }
        }
        path.deleteCharAt(path.length() - 1);
        used[r][c] = false;
        return found;
    }

    /** Approach 2: DFS from every cell, matching one character per step and backtracking as soon as a character differs. Separate visited matrix. */
    static boolean better(char[][] board, String word) {
        int m = board.length, n = m == 0 ? 0 : board[0].length;
        if (word.isEmpty()) return true;
        boolean[][] used = new boolean[m][n];
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (board[r][c] == word.charAt(0) && dfsVisited(board, r, c, 0, word, used)) return true;
            }
        }
        return false;
    }

    private static boolean dfsVisited(char[][] board, int r, int c, int i, String word, boolean[][] used) {
        if (board[r][c] != word.charAt(i)) return false;
        if (i == word.length() - 1) return true;
        used[r][c] = true;
        for (int d = 0; d < 4; d++) {
            int nr = r + DR[d], nc = c + DC[d];
            if (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length && !used[nr][nc]
                    && dfsVisited(board, nr, nc, i + 1, word, used)) {
                used[r][c] = false;
                return true;
            }
        }
        used[r][c] = false;
        return false;
    }

    /** Approach 3: same DFS, but marks cells in place (no visited matrix) and prunes with a letter-count check and by searching from the rarer end of the word. */
    static boolean optimal(char[][] board, String word) {
        int m = board.length, n = m == 0 ? 0 : board[0].length;
        if (word.isEmpty()) return true;
        if (word.length() > m * n) return false;
        int[] boardCount = new int[128], wordCount = new int[128];
        for (char[] row : board) for (char ch : row) boardCount[ch]++;
        for (int i = 0; i < word.length(); i++) wordCount[word.charAt(i)]++;
        for (int ch = 0; ch < 128; ch++) if (wordCount[ch] > boardCount[ch]) return false;
        // Start from the end whose letter is rarer on the board: fewer starting cells and earlier dead ends.
        if (boardCount[word.charAt(0)] > boardCount[word.charAt(word.length() - 1)]) {
            word = new StringBuilder(word).reverse().toString();
        }
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (dfsInPlace(board, r, c, 0, word)) return true;
            }
        }
        return false;
    }

    private static boolean dfsInPlace(char[][] board, int r, int c, int i, String word) {
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return false;
        if (board[r][c] != word.charAt(i)) return false;
        if (i == word.length() - 1) return true;
        char saved = board[r][c];
        board[r][c] = '#';                               // mark as used; '#' never equals a letter of the word
        boolean found = dfsInPlace(board, r - 1, c, i + 1, word) || dfsInPlace(board, r + 1, c, i + 1, word)
                || dfsInPlace(board, r, c - 1, i + 1, word) || dfsInPlace(board, r, c + 1, i + 1, word);
        board[r][c] = saved;                             // undo the mark on the way back
        return found;
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

    static void verify(char[][] board, String word, boolean expected) {
        check(bruteForce(board, word) == expected, "bruteForce wrong for " + word);
        check(better(board, word) == expected, "better wrong for " + word);
        check(optimal(board, word) == expected, "optimal wrong for " + word);
    }

    public static void main(String[] args) {
        char[][] board = grid("ABCE", "SFCS", "ADEE");
        verify(board, "ABCCED", true);
        verify(board, "SEE", true);
        verify(board, "ABCB", false);                                // would have to reuse the B at (0,1)
        verify(board, "ASADB", false);
        verify(grid("ABCE", "SFES", "ADEE"), "ABCESEEEFS", true);    // snakes through 10 of the 12 cells
        verify(grid("a"), "a", true);                                // single cell
        verify(grid("a"), "ab", false);                              // longer than the board
        verify(grid("ab", "cd"), "abdc", true);                      // uses every cell
        verify(grid("aaaa", "aaaa", "aaab"), "aaaaaaaaaaab", true);  // a Hamiltonian path that must end on the b
        verify(grid("aaaa", "aaaa", "aaaa"), "aaaab", false);        // the letter-count check rejects this instantly
        char[][] copy = grid("ABCE", "SFCS", "ADEE");
        optimal(copy, "ABCCED");
        check(Arrays.deepEquals(copy, board), "optimal must leave the board unchanged");
        System.out.println("OK P874_WordSearch");
    }
}
