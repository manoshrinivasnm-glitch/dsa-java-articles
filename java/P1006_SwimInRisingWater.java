import java.util.*;

/** TUF 1006 - Swim in Rising Water. Least time t such that a 4-directional path from (0,0) to (n-1,n-1) uses only cells with elevation <= t. */
public class P1006_SwimInRisingWater {

    static final int[] DR = {-1, 1, 0, 0}, DC = {0, 0, -1, 1};

    /** Approach 1: binary search on the answer t; BFS checks whether t is enough. O(n^2 log(maxElevation)) time, O(n^2) space. */
    static int binarySearchBfs(int[][] grid) {
        int lo = grid[0][0], hi = 0;
        for (int[] row : grid) for (int v : row) hi = Math.max(hi, v);
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canReach(grid, mid)) hi = mid;               // mid works, so the answer is mid or smaller
            else lo = mid + 1;
        }
        return lo;
    }

    static boolean canReach(int[][] grid, int t) {
        int n = grid.length;
        if (grid[0][0] > t) return false;
        boolean[][] seen = new boolean[n][n];
        Deque<int[]> queue = new ArrayDeque<>();
        seen[0][0] = true;
        queue.add(new int[]{0, 0});
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            if (cell[0] == n - 1 && cell[1] == n - 1) return true;
            for (int k = 0; k < 4; k++) {
                int nr = cell[0] + DR[k], nc = cell[1] + DC[k];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc] && grid[nr][nc] <= t) {
                    seen[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return false;
    }

    /** Approach 2: Dijkstra where a path costs its highest cell (minimax path). O(n^2 log n) time, O(n^2) space. */
    static int dijkstra(int[][] grid) {
        int n = grid.length;
        boolean[][] done = new boolean[n][n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));   // {time, r, c}
        pq.add(new int[]{grid[0][0], 0, 0});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int t = top[0], r = top[1], c = top[2];
            if (done[r][c]) continue;                        // stale entry
            done[r][c] = true;
            if (r == n - 1 && c == n - 1) return t;
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && !done[nr][nc]) {
                    pq.add(new int[]{Math.max(t, grid[nr][nc]), nr, nc});
                }
            }
        }
        return -1;                                           // unreachable for a non-empty grid
    }

    /** Disjoint Set Union with union by size and path compression. */
    static final class DisjointSet {
        final int[] parent, size;

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
            return parent[x];
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;                         // already in the same set
            if (size[ra] < size[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }
            parent[rb] = ra;                                    // hang the smaller tree under the larger
            size[ra] += size[rb];
            return true;
        }
    }

    /** Approach 3: Disjoint Set; flood cells in increasing elevation until both corners share a set. O(n^2 log n) time, O(n^2) space. */
    static int unionFind(int[][] grid) {
        int n = grid.length, cells = n * n;
        Integer[] order = new Integer[cells];
        for (int i = 0; i < cells; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(grid[a / n][a % n], grid[b / n][b % n]));
        DisjointSet ds = new DisjointSet(cells);
        boolean[][] open = new boolean[n][n];
        for (int id : order) {
            int r = id / n, c = id % n;
            open[r][c] = true;                               // water level has reached this cell
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && open[nr][nc]) ds.union(id, nr * n + nc);
            }
            if (ds.find(0) == ds.find(cells - 1)) return grid[r][c];
        }
        return -1;
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
