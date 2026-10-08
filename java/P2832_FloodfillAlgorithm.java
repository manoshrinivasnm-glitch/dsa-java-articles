import java.util.*;

/** TUF 2832 - Flood-fill Algorithm. Recolour the 4-directionally connected region of image[sr][sc]; the input is not modified. */
public class P2832_FloodfillAlgorithm {

    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = {0, 0, -1, 1};

    /** Approach 1: brute force, grow the region by repeated full-grid sweeps until nothing changes. O((m*n)^2) time, O(m*n) space. */
    static int[][] bruteForce(int[][] image, int sr, int sc, int color) {
        int[][] img = copy(image);
        int m = img.length, n = img[0].length, original = img[sr][sc];
        boolean[][] inRegion = new boolean[m][n];
        inRegion[sr][sc] = true;
        boolean changed = true;
        while (changed) {                                       // each sweep adds at least one cell or stops
            changed = false;
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < n; c++) {
                    if (inRegion[r][c] || img[r][c] != original) continue;
                    for (int d = 0; d < 4; d++) {
                        int nr = r + DR[d], nc = c + DC[d];
                        if (nr >= 0 && nr < m && nc >= 0 && nc < n && inRegion[nr][nc]) {
                            inRegion[r][c] = true;
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                if (inRegion[r][c]) img[r][c] = color;
        return img;
    }

    /** Approach 2: recursive DFS from the start cell. O(m*n) time, O(m*n) recursion depth in the worst case. */
    static int[][] dfs(int[][] image, int sr, int sc, int color) {
        int[][] img = copy(image);
        int original = img[sr][sc];
        if (original != color) paint(img, sr, sc, original, color);   // same colour: nothing to do, and no infinite loop
        return img;
    }

    static void paint(int[][] img, int r, int c, int original, int color) {
        if (r < 0 || r >= img.length || c < 0 || c >= img[0].length || img[r][c] != original) return;
        img[r][c] = color;                                      // recolouring doubles as the "visited" mark
        for (int d = 0; d < 4; d++) paint(img, r + DR[d], c + DC[d], original, color);
    }

    /** Approach 3: iterative BFS with a queue, no recursion. O(m*n) time, O(m*n) queue in the worst case. */
    static int[][] bfs(int[][] image, int sr, int sc, int color) {
        int[][] img = copy(image);
        int original = img[sr][sc];
        if (original == color) return img;
        int m = img.length, n = img[0].length;
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        img[sr][sc] = color;                                    // colour when enqueued, so no cell enters twice
        queue.offer(new int[]{sr, sc});
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int d = 0; d < 4; d++) {
                int r = cell[0] + DR[d], c = cell[1] + DC[d];
                if (r >= 0 && r < m && c >= 0 && c < n && img[r][c] == original) {
                    img[r][c] = color;
                    queue.offer(new int[]{r, c});
                }
            }
        }
        return img;
    }

    static int[][] copy(int[][] image) {
        int[][] out = new int[image.length][];
        for (int i = 0; i < image.length; i++) out[i] = image[i].clone();
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] image, int sr, int sc, int color, int[][] expected) {
        int[][] before = copy(image);
        int[][][] results = {bruteForce(image, sr, sc, color), dfs(image, sr, sc, color), bfs(image, sr, sc, color)};
        String[] names = {"bruteForce", "dfs", "bfs"};
        for (int i = 0; i < results.length; i++) {
            check(Arrays.deepEquals(results[i], expected), names[i] + " gave " + Arrays.deepToString(results[i]));
        }
        check(Arrays.deepEquals(before, image), "input image was modified");
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 1, 1}, {1, 1, 0}, {1, 0, 1}}, 1, 1, 2,
               new int[][]{{2, 2, 2}, {2, 2, 0}, {2, 0, 1}});              // bottom-right 1 touches only diagonally
        verify(new int[][]{{0, 0, 0}, {0, 0, 0}}, 0, 0, 0,
               new int[][]{{0, 0, 0}, {0, 0, 0}});                         // new colour == old colour
        verify(new int[][]{{5}}, 0, 0, 9, new int[][]{{9}});                // single pixel
        verify(new int[][]{{1, 0}, {0, 1}}, 0, 0, 3, new int[][]{{3, 0}, {0, 1}});   // diagonal is not connected
        verify(new int[][]{{1, 1, 2}, {2, 2, 2}, {1, 2, 1}}, 1, 1, 1,
               new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}});              // new colour already present elsewhere
        verify(new int[][]{{0, 1, 0}, {1, 1, 1}, {0, 1, 0}}, 0, 0, 7,
               new int[][]{{7, 1, 0}, {1, 1, 1}, {0, 1, 0}});              // start in a one-cell region

        // 50 x 50 serpentine corridor: one long region, worst case for the sweeping brute force
        int size = 50;
        int[][] snake = new int[size][size];
        for (int r = 0; r < size; r++) {
            if (r % 2 == 0) Arrays.fill(snake[r], 1);
            else snake[r][(r / 2) % 2 == 0 ? size - 1 : 0] = 1;
        }
        int[][] painted = copy(snake);
        for (int[] row : painted) for (int c = 0; c < size; c++) if (row[c] == 1) row[c] = 4;
        verify(snake, 0, 0, 4, painted);
        System.out.println("OK P2832_FloodfillAlgorithm");
    }
}
