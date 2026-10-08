import java.util.*;

/** TUF 535 - Number of provinces. isConnected[i][j] == 1 when cities i and j are directly linked; count the groups of cities linked directly or indirectly. */
public class P535_NumberOfProvinces {

    /** Approach 1: Warshall's transitive closure, then count every city that no smaller city can reach. O(n^3) time, O(n^2) space. */
    static int bruteForce(int[][] isConnected) {
        int n = isConnected.length;
        boolean[][] reach = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) reach[i][j] = i == j || isConnected[i][j] == 1;
        }
        for (int k = 0; k < n; k++) {                         // now allow k as a stop in between
            for (int i = 0; i < n; i++) {
                if (!reach[i][k]) continue;
                for (int j = 0; j < n; j++) {
                    if (reach[k][j]) reach[i][j] = true;
                }
            }
        }
        int provinces = 0;
        for (int j = 0; j < n; j++) {
            boolean smallest = true;                          // is j the smallest city of its province?
            for (int i = 0; i < j; i++) {
                if (reach[i][j]) {
                    smallest = false;
                    break;
                }
            }
            if (smallest) provinces++;
        }
        return provinces;
    }

    /** Approach 2: DFS straight on the matrix. Every DFS started from an unvisited city floods exactly one province. O(n^2). */
    static int optimalDfs(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int provinces = 0;
        for (int i = 0; i < n; i++) {
            if (visited[i]) continue;
            provinces++;                                      // i belongs to a province nobody has explored yet
            dfs(isConnected, i, visited);
        }
        return provinces;
    }

    static void dfs(int[][] isConnected, int u, boolean[] visited) {
        visited[u] = true;
        for (int v = 0; v < isConnected.length; v++) {
            if (isConnected[u][v] == 1 && !visited[v]) dfs(isConnected, v, visited);
        }
    }

    /** Approach 3: union-find over the upper triangle. Each union of two different sets merges two provinces. O(n^2 * alpha(n)). */
    static int optimalUnionFind(int[][] isConnected) {
        int n = isConnected.length;
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        int provinces = n;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {                 // the matrix is symmetric, so j > i is enough
                if (isConnected[i][j] == 0) continue;
                int a = find(parent, i), b = find(parent, j);
                if (a != b) {
                    parent[a] = b;
                    provinces--;
                }
            }
        }
        return provinces;
    }

    static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];                    // path halving
            x = parent[x];
        }
        return x;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] isConnected, int expected) {
        check(bruteForce(isConnected) == expected, "bruteForce expected " + expected);
        check(optimalDfs(isConnected) == expected, "optimalDfs expected " + expected);
        check(optimalUnionFind(isConnected) == expected, "optimalUnionFind expected " + expected);
    }

    /** Symmetric matrix with 1 on the diagonal and on every listed pair. */
    static int[][] fromPairs(int n, int[][] pairs) {
        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++) m[i][i] = 1;
        for (int[] p : pairs) {
            m[p[0]][p[1]] = 1;
            m[p[1]][p[0]] = 1;
        }
        return m;
    }

    public static void main(String[] args) {
        verify(new int[][]{ {1, 1, 0}, {1, 1, 0}, {0, 0, 1} }, 2);                       // {0,1} and {2}
        verify(new int[][]{ {1, 0, 0}, {0, 1, 0}, {0, 0, 1} }, 3);                       // nobody linked
        verify(new int[][]{ {1} }, 1);                                                   // one city
        verify(new int[][]{ {1, 0, 0, 1}, {0, 1, 1, 0}, {0, 1, 1, 1}, {1, 0, 1, 1} }, 1); // 0-3-2-1 indirectly
        verify(fromPairs(5, new int[][]{ {0, 4}, {1, 3} }), 3);                          // {0,4}, {1,3}, {2}
        verify(fromPairs(6, new int[][]{ {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5} }), 1);  // a chain
        verify(fromPairs(4, new int[][]{ {3, 0} }), 3);                                   // {0,3}, {1}, {2}

        // 200 cities, i linked to i + 3: three provinces by residue mod 3
        int n = 200;
        int[][] pairs = new int[n - 3][];
        for (int i = 0; i + 3 < n; i++) pairs[i] = new int[]{i, i + 3};
        verify(fromPairs(n, pairs), 3);

        System.out.println("OK P535_NumberOfProvinces");
    }
}
