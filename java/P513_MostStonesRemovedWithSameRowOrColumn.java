import java.util.*;

/** TUF 513 - Most stones removed with same row or column. Max stones removable when a stone can go only if another shares its row or column. */
public class P513_MostStonesRemovedWithSameRowOrColumn {

    /** Approach 1: DFS over the stone graph (edge = shared row or column). O(n^2) time, O(n) space. */
    static int dfsStones(int[][] stones) {
        int n = stones.length, components = 0;
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int s = 0; s < n; s++) {
            if (visited[s]) continue;
            components++;
            visited[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (int v = 0; v < n; v++) {
                    boolean linked = stones[u][0] == stones[v][0] || stones[u][1] == stones[v][1];
                    if (!visited[v] && linked) {
                        visited[v] = true;
                        stack.push(v);
                    }
                }
            }
        }
        return n - components;                       // each component keeps exactly one stone
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

    /** Approach 2: Disjoint Set over row nodes and column nodes; each stone unions its row with its column. O(n * alpha) time, O(R + C) space. */
    static int disjointSet(int[][] stones) {
        int maxRow = 0, maxCol = 0;
        for (int[] s : stones) {
            maxRow = Math.max(maxRow, s[0]);
            maxCol = Math.max(maxCol, s[1]);
        }
        DisjointSet ds = new DisjointSet(maxRow + maxCol + 2);   // rows 0..maxRow, then columns
        Set<Integer> usedNodes = new HashSet<>();
        for (int[] s : stones) {
            int rowNode = s[0], colNode = maxRow + 1 + s[1];
            ds.union(rowNode, colNode);
            usedNodes.add(rowNode);
            usedNodes.add(colNode);
        }
        int components = 0;
        for (int node : usedNodes) {
            if (ds.find(node) == node) components++;
        }
        return stones.length - components;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] stones, int expected) {
        int a = dfsStones(stones), b = disjointSet(stones);
        check(a == expected, "dfsStones got " + a + " expected " + expected + " for " + Arrays.deepToString(stones));
        check(b == expected, "disjointSet got " + b + " expected " + expected + " for " + Arrays.deepToString(stones));
    }

    public static void main(String[] args) {
        verify(new int[][]{ {0, 0}, {0, 1}, {1, 0}, {1, 2}, {2, 1}, {2, 2} }, 5);
        verify(new int[][]{ {0, 0}, {0, 2}, {1, 1}, {2, 0}, {2, 2} }, 3);
        verify(new int[][]{ {0, 0} }, 0);                                   // single stone
        verify(new int[][]{}, 0);                                           // no stones
        verify(new int[][]{ {0, 0}, {1, 1}, {2, 2} }, 0);                   // diagonal: nothing shares a line
        verify(new int[][]{ {0, 1}, {1, 0}, {1, 1} }, 2);
        verify(new int[][]{ {10000, 10000}, {0, 10000} }, 1);               // large coordinates
        // row 5 with columns 0..9 (10 stones) and column 20 with rows 100..104 (5 stones): 2 components
        List<int[]> two = new ArrayList<>();
        for (int c = 0; c < 10; c++) two.add(new int[]{5, c});
        for (int r = 100; r < 105; r++) two.add(new int[]{r, 20});
        verify(two.toArray(new int[0][]), 13);
        // seeded cross-check on random distinct stones
        Random rnd = new Random(513);
        for (int t = 0; t < 300; t++) {
            int k = rnd.nextInt(15);
            Set<Integer> seen = new HashSet<>();
            List<int[]> st = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                int r = rnd.nextInt(6), c = rnd.nextInt(6);
                if (seen.add(r * 10 + c)) st.add(new int[]{r, c});
            }
            int[][] arr = st.toArray(new int[0][]);
            check(dfsStones(arr) == disjointSet(arr), "random mismatch at trial " + t);
        }
        System.out.println("OK P513_MostStonesRemovedWithSameRowOrColumn");
    }
}
