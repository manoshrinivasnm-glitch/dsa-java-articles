import java.util.*;

/** TUF 531 - Flood fill algorithm. Repaint the 4-connected region of equal colour that contains (sr, sc). Returns a new image; the input is left untouched. */
public class P531_FloodFillAlgorithm {

    static final int[][] DIRS = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };

    /** Approach 1: grow the region by repeated full sweeps until a sweep adds nothing, then paint it. O((n * m)^2) time. */
    static int[][] bruteForce(int[][] image, int sr, int sc, int color) {
        int n = image.length, m = image[0].length;
        int original = image[sr][sc];
        boolean[][] inRegion = new boolean[n][m];
        inRegion[sr][sc] = true;
        boolean grew = true;
        while (grew) {
            grew = false;
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < m; c++) {
                    if (inRegion[r][c] || image[r][c] != original) continue;
                    for (int[] d : DIRS) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr >= 0 && nr < n && nc >= 0 && nc < m && inRegion[nr][nc]) {
                            inRegion[r][c] = true;        // same colour and touching the region: it belongs
                            grew = true;
                            break;
                        }
                    }
                }
            }
        }
        int[][] result = copy(image);
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) if (inRegion[r][c]) result[r][c] = color;
        }
        return result;
    }

    /** Approach 2: recursive DFS. Repainting a cell doubles as marking it visited. O(n * m) time, recursion up to n * m deep. */
    static int[][] dfs(int[][] image, int sr, int sc, int color) {
        int[][] img = copy(image);
        int original = img[sr][sc];
        if (original != color) paint(img, sr, sc, original, color);   // equal colours: nothing to do, and no way to tell visited cells apart
        return img;
    }

    static void paint(int[][] img, int r, int c, int original, int color) {
        if (r < 0 || r >= img.length || c < 0 || c >= img[0].length || img[r][c] != original) return;
        img[r][c] = color;
        paint(img, r + 1, c, original, color);
        paint(img, r - 1, c, original, color);
        paint(img, r, c + 1, original, color);
        paint(img, r, c - 1, original, color);
    }

    /** Approach 3: BFS with an explicit queue. Same O(n * m) time, no recursion depth limit. */
    static int[][] bfs(int[][] image, int sr, int sc, int color) {
        int[][] img = copy(image);
        int original = img[sr][sc];
        if (original == color) return img;
        int n = img.length, m = img[0].length;
        Deque<int[]> queue = new ArrayDeque<>();
        img[sr][sc] = color;
        queue.add(new int[]{sr, sc});
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] d : DIRS) {
                int nr = cell[0] + d[0], nc = cell[1] + d[1];
                if (nr < 0 || nr >= n || nc < 0 || nc >= m || img[nr][nc] != original) continue;
                img[nr][nc] = color;                      // paint on enqueue so the cell is never queued twice
                queue.add(new int[]{nr, nc});
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
        check(Arrays.deepEquals(bruteForce(image, sr, sc, color), expected), "bruteForce " + Arrays.deepToString(bruteForce(image, sr, sc, color)));
        check(Arrays.deepEquals(dfs(image, sr, sc, color), expected), "dfs " + Arrays.deepToString(dfs(image, sr, sc, color)));
        check(Arrays.deepEquals(bfs(image, sr, sc, color), expected), "bfs " + Arrays.deepToString(bfs(image, sr, sc, color)));
        check(Arrays.deepEquals(image, before), "the input image must not be modified");
    }

    public static void main(String[] args) {
        // LeetCode example 1: the bottom-right 1 is only diagonally connected, so it keeps its colour
        verify(new int[][]{ {1, 1, 1}, {1, 1, 0}, {1, 0, 1} }, 1, 1, 2,
                new int[][]{ {2, 2, 2}, {2, 2, 0}, {2, 0, 1} });
        // new colour equals the old one: unchanged
        verify(new int[][]{ {0, 0, 0}, {0, 0, 0} }, 0, 0, 0,
                new int[][]{ {0, 0, 0}, {0, 0, 0} });
        verify(new int[][]{ {0, 0, 0}, {0, 1, 1} }, 1, 1, 1,
                new int[][]{ {0, 0, 0}, {0, 1, 1} });
        // single pixel
        verify(new int[][]{ {5} }, 0, 0, 7, new int[][]{ {7} });
        // diagonal neighbours are not connected
        verify(new int[][]{ {1, 0}, {0, 1} }, 0, 0, 3, new int[][]{ {3, 0}, {0, 1} });
        // the new colour already exists next to the region: those cells are not part of it
        verify(new int[][]{ {1, 2}, {1, 1} }, 0, 0, 2, new int[][]{ {2, 2}, {2, 2} });
        verify(new int[][]{ {1, 2, 1}, {2, 2, 1}, {1, 1, 1} }, 0, 0, 9,
                new int[][]{ {9, 2, 1}, {2, 2, 1}, {1, 1, 1} });
        // start in the middle of a ring: only the centre changes
        verify(new int[][]{ {3, 3, 3}, {3, 4, 3}, {3, 3, 3} }, 1, 1, 0,
                new int[][]{ {3, 3, 3}, {3, 0, 3}, {3, 3, 3} });

        // 40 x 40 all zeros, filled from the centre
        int[][] blank = new int[40][40];
        int[][] filled = new int[40][40];
        for (int[] row : filled) Arrays.fill(row, 1);
        verify(blank, 20, 20, 1, filled);

        System.out.println("OK P531_FloodFillAlgorithm");
    }
}
