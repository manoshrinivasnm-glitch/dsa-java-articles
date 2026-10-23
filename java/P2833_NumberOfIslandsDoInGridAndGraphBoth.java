import java.util.*;

/** TUF 2833 - Number of islands, solved directly on the grid and by converting the grid to a graph. */
public class P2833_NumberOfIslandsDoInGridAndGraphBoth {

    static final int[] DR = {-1, 1, 0, 0};                // row offsets: up, down, left, right
    static final int[] DC = {0, 0, -1, 1};                // matching column offsets

    /** Approach 1: grid DFS. Every unvisited land cell starts a new island; flood it recursively. O(m * n) time, O(m * n) space. */
    static int islandsDfs(char[][] grid) {
        int m = grid.length, n = grid[0].length, count = 0;   // count = islands found so far
        boolean[][] seen = new boolean[m][n];             // land cells already flooded
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == '1' && !seen[r][c]) {   // unvisited land: a new island starts here
                    count++;                              // first cell of an island we have not met before
                    flood(grid, r, c, seen);              // mark the whole island so it is not recounted
                }
            }
        }
        return count;
    }

    static void flood(char[][] grid, int r, int c, boolean[][] seen) {
        if (r < 0 || c < 0 || r >= grid.length || c >= grid[0].length) return;   // stepped off the grid
        if (grid[r][c] != '1' || seen[r][c]) return;      // water, or land this flood already took
        seen[r][c] = true;
        for (int d = 0; d < 4; d++) flood(grid, r + DR[d], c + DC[d], seen);   // flood all 4 neighbours
    }

    /** Approach 2: grid BFS. Same counting idea, but the flood uses a queue so depth is never a problem. O(m * n) time, O(m * n) space. */
    static int islandsBfs(char[][] grid) {
        int m = grid.length, n = grid[0].length, count = 0;   // count = islands found so far
        boolean[][] seen = new boolean[m][n];             // land cells already queued
        Deque<int[]> queue = new ArrayDeque<>();          // cells waiting to be expanded, reused per island
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] != '1' || seen[r][c]) continue;   // water, or an island already counted
                count++;                                  // a new island starts here
                seen[r][c] = true;
                queue.add(new int[]{r, c});
                while (!queue.isEmpty()) {                // flood this island breadth first
                    int[] cell = queue.poll();
                    for (int d = 0; d < 4; d++) {
                        int nr = cell[0] + DR[d], nc = cell[1] + DC[d];   // the neighbour in direction d
                        if (nr < 0 || nc < 0 || nr >= m || nc >= n) continue;   // off the grid
                        if (grid[nr][nc] == '1' && !seen[nr][nc]) {   // unvisited land joins this island
                            seen[nr][nc] = true;          // mark when enqueued, not when dequeued
                            queue.add(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
        return count;
    }

    /** Approach 3: build an explicit graph (one vertex per land cell) and count its connected components. O(m * n) time and space. */
    static int islandsGraph(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] id = new int[m][n];                       // vertex number of each land cell, -1 for water
        int V = 0;                                        // number of land cells = number of vertices
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) id[r][c] = grid[r][c] == '1' ? V++ : -1;   // number the land cells
        }
        List<List<Integer>> adj = new ArrayList<>();      // adjacency list over the land vertices
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int r = 0; r < m; r++) {                     // link each land cell to its land neighbours
            for (int c = 0; c < n; c++) {
                if (id[r][c] < 0) continue;               // water is not a vertex
                if (r + 1 < m && id[r + 1][c] >= 0) addEdge(adj, id[r][c], id[r + 1][c]);   // down
                if (c + 1 < n && id[r][c + 1] >= 0) addEdge(adj, id[r][c], id[r][c + 1]);   // right
            }
        }
        return countComponents(V, adj);                   // each connected component is one island
    }

    static void addEdge(List<List<Integer>> adj, int a, int b) {
        adj.get(a).add(b);                                // undirected: store the edge both ways
        adj.get(b).add(a);
    }

    /** The graph form of the problem: number of connected components of an undirected graph. O(V + E) time, O(V) space. */
    static int countComponents(int V, List<List<Integer>> adj) {
        boolean[] seen = new boolean[V];                  // vertices already reached
        int count = 0;                                    // components found so far
        Deque<Integer> stack = new ArrayDeque<>();        // iterative DFS stack
        for (int s = 0; s < V; s++) {                     // start a search from every unreached vertex
            if (seen[s]) continue;                        // already part of a counted component
            count++;                                      // s starts a new component
            seen[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {                    // reach everything connected to s
                int x = stack.pop();
                for (int y : adj.get(x)) {
                    if (!seen[y]) {                       // push each neighbour the first time we see it
                        seen[y] = true;
                        stack.push(y);
                    }
                }
            }
        }
        return count;
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

    static void verify(int expected, String... rows) {
        char[][] g = grid(rows);
        check(islandsDfs(g) == expected, "islandsDfs " + Arrays.toString(rows));
        check(islandsBfs(g) == expected, "islandsBfs " + Arrays.toString(rows));
        check(islandsGraph(g) == expected, "islandsGraph " + Arrays.toString(rows));
        check(Arrays.deepEquals(g, grid(rows)), "input grid was modified");
    }

    public static void main(String[] args) {
        verify(1, "11110", "11010", "11000", "00000");          // LeetCode example 1
        verify(3, "11000", "11000", "00100", "00011");          // LeetCode example 2
        verify(0, "000", "000");                                // all water
        verify(1, "1");                                         // single land cell
        verify(0, "0");                                         // single water cell
        verify(5, "101", "010", "101");                         // diagonal cells are NOT connected
        verify(1, "111", "101", "111");                         // ring around a lake is one island
        verify(2, "10001");                                     // single row
        // graph form directly: 5 vertices, edges 0-1 and 2-3, vertex 4 isolated
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 5; i++) adj.add(new ArrayList<>());
        addEdge(adj, 0, 1);
        addEdge(adj, 2, 3);
        check(countComponents(5, adj) == 3, "countComponents");
        System.out.println("OK P2833_NumberOfIslandsDoInGridAndGraphBoth");
    }
}
