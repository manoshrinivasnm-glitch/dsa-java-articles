import java.util.*;

/** TUF 1006 - Swim in Rising Water. Least time t such that a 4-directional path from (0,0) to (n-1,n-1) uses only cells with elevation <= t. */
public class P1006_SwimInRisingWater {

    static final int[] DR = {-1, 1, 0, 0}, DC = {0, 0, -1, 1};

    /** Approach 1: binary search on the answer t; BFS checks whether t is enough. O(n^2 log(maxElevation)) time, O(n^2) space. */
    static int binarySearchBfs(int[][] grid) {
        int lo = grid[0][0], hi = 0;                         // the answer lies in [start cell, highest cell]
        for (int[] row : grid) for (int v : row) hi = Math.max(hi, v);   // highest elevation in the grid
        while (lo < hi) {                                    // shrink [lo, hi] until it pins down the answer
            int mid = lo + (hi - lo) / 2;                    // middle time to test
            if (canReach(grid, mid)) hi = mid;               // mid works, so the answer is mid or smaller
            else lo = mid + 1;                               // mid is too early: wait longer
        }
        return lo;                                           // smallest time that works
    }

    static boolean canReach(int[][] grid, int t) {
        int n = grid.length;
        if (grid[0][0] > t) return false;                    // cannot even stand on the start cell
        boolean[][] seen = new boolean[n][n];                // cells already reached
        Deque<int[]> queue = new ArrayDeque<>();             // BFS queue of reachable cells
        seen[0][0] = true;
        queue.add(new int[]{0, 0});
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();                       // next reachable cell
            if (cell[0] == n - 1 && cell[1] == n - 1) return true;   // reached the bottom-right corner
            for (int k = 0; k < 4; k++) {                    // look at the 4 neighbours
                int nr = cell[0] + DR[k], nc = cell[1] + DC[k];   // neighbour position
                // inside the grid, new, and already under water at time t
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc] && grid[nr][nc] <= t) {
                    seen[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return false;                                        // the corner is cut off at time t
    }

    /** Approach 2: Dijkstra where a path costs its highest cell (minimax path). O(n^2 log n) time, O(n^2) space. */
    static int dijkstra(int[][] grid) {
        int n = grid.length;
        boolean[][] done = new boolean[n][n];                // cells whose earliest time is final
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));   // {time, r, c}
        pq.add(new int[]{grid[0][0], 0, 0});                 // we must wait until the start cell is under water
        while (!pq.isEmpty()) {
            int[] top = pq.poll();                           // cell with the earliest possible arrival time
            int t = top[0], r = top[1], c = top[2];
            if (done[r][c]) continue;                        // stale entry
            done[r][c] = true;                               // its time is now final
            if (r == n - 1 && c == n - 1) return t;          // the corner is settled: earliest time to finish
            for (int k = 0; k < 4; k++) {                    // look at the 4 neighbours
                int nr = r + DR[k], nc = c + DC[k];          // neighbour position
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && !done[nr][nc]) {   // in the grid, not settled
                    pq.add(new int[]{Math.max(t, grid[nr][nc]), nr, nc});   // arrive at max(t, cell height)
                }
            }
        }
        return -1;                                           // unreachable for a non-empty grid
    }

    /** Disjoint Set Union with union by size and path compression. */
    static final class DisjointSet {
        final int[] parent, size;                              // parent links; size[r] = size of root r's set

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {                      // every element starts as its own set
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
            return parent[x];                                  // parent[x] is now the root itself
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);                    // roots of the two sets
            if (ra == rb) return false;                        // already in the same set
            if (size[ra] < size[rb]) {                         // keep ra as the root of the larger set
                int t = ra;
                ra = rb;
                rb = t;
            }
            parent[rb] = ra;                                   // hang the smaller tree under the larger
            size[ra] += size[rb];                              // ra's set absorbs rb's elements
            return true;                                       // two different sets were merged
        }
    }

    /** Approach 3: Disjoint Set; flood cells in increasing elevation until both corners share a set. O(n^2 log n) time, O(n^2) space. */
    static int unionFind(int[][] grid) {
        int n = grid.length, cells = n * n;                  // number of cells, ids r * n + c
        Integer[] order = new Integer[cells];                // cell ids, to be visited by elevation
        for (int i = 0; i < cells; i++) order[i] = i;
        // visit cells from the lowest elevation to the highest
        Arrays.sort(order, (a, b) -> Integer.compare(grid[a / n][a % n], grid[b / n][b % n]));
        DisjointSet ds = new DisjointSet(cells);             // one set per cell
        boolean[][] open = new boolean[n][n];                // cells already under water
        for (int id : order) {                               // raise the water one cell at a time
            int r = id / n, c = id % n;                      // row and column of this cell
            open[r][c] = true;                               // water level has reached this cell
            for (int k = 0; k < 4; k++) {                    // look at the 4 neighbours
                int nr = r + DR[k], nc = c + DC[k];          // neighbour position
                // link with every neighbour that is already under water
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && open[nr][nc]) ds.union(id, nr * n + nc);
            }
            if (ds.find(0) == ds.find(cells - 1)) return grid[r][c];   // corners linked: answer found
        }
        return -1;                                           // unreachable for a non-empty grid
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int expected) {
        int a = binarySearchBfs(grid), b = dijkstra(grid), c = unionFind(grid);
        String g = Arrays.deepToString(grid);
        check(a == expected, "binarySearchBfs got " + a + " expected " + expected + " for " + g);
        check(b == expected, "dijkstra got " + b + " expected " + expected + " for " + g);
        check(c == expected, "unionFind got " + c + " expected " + expected + " for " + g);
    }

    public static void main(String[] args) {
        verify(new int[][]{ {0, 2}, {1, 3} }, 3);
        verify(new int[][]{ {0, 1, 2, 3, 4}, {24, 23, 22, 21, 5}, {12, 13, 14, 15, 16}, {11, 17, 18, 19, 20}, {10, 9, 8, 7, 6} }, 16);
        verify(new int[][]{ {0} }, 0);                                         // single cell
        verify(new int[][]{ {3, 2}, {0, 1} }, 3);                              // the start cell is the bottleneck
        verify(new int[][]{ {0, 6, 1}, {7, 8, 2}, {3, 4, 5} }, 6);             // bottleneck in the middle of the path
        verify(new int[][]{ {0, 1, 2}, {5, 4, 3}, {6, 7, 8} }, 8);             // the end cell is the bottleneck
        // seeded cross-check on random permutation grids
        Random rnd = new Random(1006);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(7);
            List<Integer> vals = new ArrayList<>();
            for (int i = 0; i < n * n; i++) vals.add(i);
            Collections.shuffle(vals, rnd);
            int[][] g = new int[n][n];
            for (int i = 0; i < n * n; i++) g[i / n][i % n] = vals.get(i);
            int a = binarySearchBfs(g);
            check(a == dijkstra(g) && a == unionFind(g), "random mismatch at trial " + t);
        }
        System.out.println("OK P1006_SwimInRisingWater");
    }
}
