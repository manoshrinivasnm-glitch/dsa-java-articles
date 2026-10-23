import java.util.*;

/** TUF 512 - Making a large island. Flip at most one 0 to 1 in an n x n binary grid; return the largest possible island. */
public class P512_MakingALargeIsland {

    static final int[] DR = {-1, 1, 0, 0}, DC = {0, 0, -1, 1};

    /** Approach 1: try flipping every 0 and measure the island that contains it with BFS. O(n^4) time, O(n^2) space. */
    static int bruteForce(int[][] grid) {
        int n = grid.length, best = 0;                       // best = largest island seen after a flip
        boolean hasZero = false;                             // does the grid contain any 0 at all?
        for (int r = 0; r < n; r++) {                        // try every cell as the one to flip
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) continue;               // already land: nothing to flip
                hasZero = true;
                grid[r][c] = 1;                              // flip, measure, restore
                best = Math.max(best, componentSize(grid, r, c));   // size of the island the flipped cell joins
                grid[r][c] = 0;
            }
        }
        return hasZero ? best : n * n;                       // no 0 to flip: the whole grid is one island
    }

    static int componentSize(int[][] grid, int sr, int sc) {
        int n = grid.length, size = 0;                       // size = cells counted in this island
        boolean[][] seen = new boolean[n][n];                // land cells already reached
        Deque<int[]> queue = new ArrayDeque<>();             // BFS queue of cells to expand
        seen[sr][sc] = true;                                 // start the flood from the given cell
        queue.add(new int[]{sr, sc});
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();                       // next cell of the island
            size++;                                          // count it
            for (int k = 0; k < 4; k++) {                    // look at the 4 neighbours
                int nr = cell[0] + DR[k], nc = cell[1] + DC[k];   // neighbour position
                // inside the grid, land, and not reached yet
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 1 && !seen[nr][nc]) {
                    seen[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return size;
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

    /** Approach 2: Disjoint Set; group islands once, then for each 0 add the sizes of its distinct neighbouring islands. O(n^2 * alpha) time, O(n^2) space. */
    static int optimal(int[][] grid) {
        int n = grid.length;
        DisjointSet ds = new DisjointSet(n * n);                // one set per cell, id = r * n + c
        for (int r = 0; r < n; r++) {                           // step 1: union every pair of adjacent 1s
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 0) continue;                  // water cells join nothing yet
                // link with the land cell below
                if (r + 1 < n && grid[r + 1][c] == 1) ds.union(r * n + c, (r + 1) * n + c);
                // link with the land cell to the right
                if (c + 1 < n && grid[r][c + 1] == 1) ds.union(r * n + c, r * n + c + 1);
            }
        }
        int best = 0;                                           // largest island found so far
        for (int r = 0; r < n; r++) {                           // step 2: evaluate every cell
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) {                          // a land cell: its island already counts
                    best = Math.max(best, ds.size[ds.find(r * n + c)]);   // covers the all-ones grid
                    continue;
                }
                Set<Integer> roots = new HashSet<>();           // distinct islands around this 0
                for (int k = 0; k < 4; k++) {
                    int nr = r + DR[k], nc = c + DC[k];         // neighbour position
                    // a land neighbour: remember the root of its island
                    if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 1) roots.add(ds.find(nr * n + nc));
                }
                int total = 1;                                  // the flipped cell itself
                for (int root : roots) total += ds.size[root];  // add each neighbouring island once
                best = Math.max(best, total);                   // keep the largest result
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] grid, int expected) {
        String g = Arrays.deepToString(grid);
        int a = bruteForce(grid);
        check(Arrays.deepToString(grid).equals(g), "bruteForce must leave the grid unchanged");
        int b = optimal(grid);
        check(a == expected, "bruteForce got " + a + " expected " + expected + " for " + g);
        check(b == expected, "optimal got " + b + " expected " + expected + " for " + g);
    }

    public static void main(String[] args) {
        verify(new int[][]{ {1, 0}, {0, 1} }, 3);
        verify(new int[][]{ {1, 1}, {1, 0} }, 4);
        verify(new int[][]{ {1, 1}, {1, 1} }, 4);                                // nothing to flip
        verify(new int[][]{ {0} }, 1);                                           // single water cell
        verify(new int[][]{ {1} }, 1);
        verify(new int[][]{ {0, 0}, {0, 0} }, 1);                                // all water
        verify(new int[][]{ {1, 0, 1}, {0, 0, 0}, {1, 0, 1} }, 3);
        verify(new int[][]{ {1, 1, 1}, {1, 0, 1}, {1, 1, 1} }, 9);               // same island on all four sides
        verify(new int[][]{ {1, 1, 0, 0}, {1, 0, 0, 1}, {0, 0, 1, 1}, {0, 1, 1, 0} }, 6);   // islands of 3 and 5 never share a 0
        verify(new int[][]{ {1, 1, 0}, {0, 0, 0}, {0, 1, 1} }, 5);               // centre bridges two islands of 2
        // seeded cross-check
        Random rnd = new Random(512);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(7);
            int[][] g = new int[n][n];
            for (int[] row : g) for (int c = 0; c < n; c++) row[c] = rnd.nextInt(10) < 6 ? 1 : 0;
            check(bruteForce(g) == optimal(g), "random mismatch at trial " + t);
        }
        System.out.println("OK P512_MakingALargeIsland");
    }
}
