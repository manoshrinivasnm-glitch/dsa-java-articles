import java.util.*;

/** TUF 872 - Rat in a Maze. All paths from (0,0) to (n-1,n-1) through 1-cells, moving D/L/R/U, listed in lexicographic order. */
public class P872_RatInAMaze {

    /** Approach 1: DFS with a separate visited matrix and one explicit branch per direction, tried in the order D, L, R, U. */
    static List<String> bruteForce(int[][] maze) {
        List<String> paths = new ArrayList<>();
        int n = maze.length;
        if (n == 0 || maze[0][0] == 0 || maze[n - 1][n - 1] == 0) return paths;
        boolean[][] visited = new boolean[n][n];
        explore(0, 0, maze, n, new StringBuilder(), visited, paths);
        return paths;
    }

    private static void explore(int r, int c, int[][] maze, int n, StringBuilder path, boolean[][] visited, List<String> paths) {
        if (r == n - 1 && c == n - 1) {
            paths.add(path.toString());
            return;
        }
        visited[r][c] = true;
        if (r + 1 < n && !visited[r + 1][c] && maze[r + 1][c] == 1) {          // Down
            path.append('D');
            explore(r + 1, c, maze, n, path, visited, paths);
            path.deleteCharAt(path.length() - 1);
        }
        if (c - 1 >= 0 && !visited[r][c - 1] && maze[r][c - 1] == 1) {         // Left
            path.append('L');
            explore(r, c - 1, maze, n, path, visited, paths);
            path.deleteCharAt(path.length() - 1);
        }
        if (c + 1 < n && !visited[r][c + 1] && maze[r][c + 1] == 1) {          // Right
            path.append('R');
            explore(r, c + 1, maze, n, path, visited, paths);
            path.deleteCharAt(path.length() - 1);
        }
        if (r - 1 >= 0 && !visited[r - 1][c] && maze[r - 1][c] == 1) {         // Up
            path.append('U');
            explore(r - 1, c, maze, n, path, visited, paths);
            path.deleteCharAt(path.length() - 1);
        }
        visited[r][c] = false;
    }

    static final int[] DR = {1, 0, 0, -1};               // D, L, R, U: already in lexicographic order
    static final int[] DC = {0, -1, 1, 0};
    static final char[] MOVE = {'D', 'L', 'R', 'U'};

    /** Approach 2: direction arrays replace the four copies of the branch; the maze itself is marked (cell set to 0) while a cell is on the current path. */
    static List<String> optimal(int[][] maze) {
        List<String> paths = new ArrayList<>();
        int n = maze.length;
        if (n == 0 || maze[0][0] == 0 || maze[n - 1][n - 1] == 0) return paths;
        dfs(0, 0, maze, n, new char[n * n], 0, paths);  // a simple path visits at most n*n cells, so n*n - 1 moves
        return paths;
    }

    private static void dfs(int r, int c, int[][] maze, int n, char[] buf, int len, List<String> paths) {
        if (r == n - 1 && c == n - 1) {
            paths.add(new String(buf, 0, len));
            return;
        }
        maze[r][c] = 0;                                  // block the cell while it is on the current path
        for (int d = 0; d < 4; d++) {
            int nr = r + DR[d], nc = c + DC[d];
            if (nr >= 0 && nr < n && nc >= 0 && nc < n && maze[nr][nc] == 1) {
                buf[len] = MOVE[d];
                dfs(nr, nc, maze, n, buf, len + 1, paths);
            }
        }
        maze[r][c] = 1;                                  // restore it for the other branches
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static int[][] copyOf(int[][] maze) {
        int[][] c = new int[maze.length][];
        for (int i = 0; i < maze.length; i++) c[i] = maze[i].clone();
        return c;
    }

    static int[][] open(int n) {
        int[][] maze = new int[n][n];
        for (int[] row : maze) Arrays.fill(row, 1);
        return maze;
    }

    /** Runs both approaches, compares with the expected list, and checks that optimal() leaves the maze intact. */
    static List<String> verify(int[][] maze, List<String> expected) {
        int[][] copy = copyOf(maze);
        List<String> a = bruteForce(maze), b = optimal(copy);
        check(Arrays.deepEquals(maze, copy), "optimal must restore every cell");
        check(a.equals(b), "approaches disagree: " + a + " vs " + b);
        List<String> sorted = new ArrayList<>(a);
        Collections.sort(sorted);
        check(a.equals(sorted), "paths are not in lexicographic order: " + a);
        if (expected != null) check(a.equals(expected), "expected " + expected + " but got " + a);
        return a;
    }

    public static void main(String[] args) {
        int[][] maze1 = {{1, 0, 0, 0}, {1, 1, 0, 1}, {1, 1, 0, 0}, {0, 1, 1, 1}};
        verify(maze1, List.of("DDRDRR", "DRDDRR"));
        verify(new int[][]{{1, 0}, {1, 0}}, List.of());                       // destination blocked
        verify(new int[][]{{0, 1}, {1, 1}}, List.of());                       // source blocked
        verify(new int[][]{{1, 1}, {0, 1}}, List.of("RD"));
        verify(new int[][]{{1, 1}, {1, 1}}, List.of("DR", "RD"));
        verify(new int[][]{{1}}, List.of(""));                               // already at the destination: one empty path
        verify(open(3), List.of("DDRR", "DDRURD", "DDRUURDD", "DRDR", "DRRD", "DRURDD",
                "RDDR", "RDLDRR", "RDRD", "RRDD", "RRDLDR", "RRDLLDRR"));
        check(verify(open(4), null).size() == 184, "a fully open 4x4 maze has 184 simple corner-to-corner paths");
        int[][] ring = {{1, 1, 1, 1, 1, 1}, {1, 0, 0, 0, 0, 1}, {1, 0, 1, 1, 0, 1},
                        {1, 0, 1, 1, 0, 1}, {1, 0, 0, 0, 0, 1}, {1, 1, 1, 1, 1, 1}};
        verify(ring, List.of("DDDDDRRRRR", "RRRRRDDDDD"));                  // the open centre is unreachable
        int[][] detour = {{1, 0, 1, 1, 1}, {1, 1, 1, 0, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}};
        verify(detour, List.of("DRRURRDDDD"));                               // the only path needs a U move
        System.out.println("OK P872_RatInAMaze");
    }
}
