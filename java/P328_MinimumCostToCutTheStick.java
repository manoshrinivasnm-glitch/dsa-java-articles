import java.util.*;

/** TUF 328 - Minimum cost to cut the stick. Each cut costs the length of the piece being cut; minimise the total. */
public class P328_MinimumCostToCutTheStick {

    /** Sorted cut positions with the two stick ends added: c[0] = 0, c[last] = n. */
    static int[] withEnds(int n, int[] cuts) {
        int m = cuts.length;
        int[] c = new int[m + 2];
        for (int i = 0; i < m; i++) c[i + 1] = cuts[i];
        c[m + 1] = n;                                           // c[0] = 0 already
        Arrays.sort(c, 1, m + 1);
        return c;
    }

    /** Approach 1: try every cut as the first one on each piece. Exponential time, O(m) stack. */
    static int recursive(int n, int[] cuts) {
        int[] c = withEnds(n, cuts);
        return solve(0, c.length - 1, c);
    }

    static int solve(int i, int j, int[] c) {
        if (j - i < 2) return 0;                                // no cut strictly between c[i] and c[j]
        int best = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) {                       // cut at c[k] first, then solve both halves
            best = Math.min(best, solve(i, k, c) + solve(k, j, c));
        }
        return best + c[j] - c[i];                              // the first cut pays for the whole piece
    }

    /** Approach 2: cache each (i, j) piece. O(m^3) time, O(m^2) space. */
    static int memoization(int n, int[] cuts) {
        int[] c = withEnds(n, cuts);
        int[][] dp = new int[c.length][c.length];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(0, c.length - 1, c, dp);
    }

    static int memo(int i, int j, int[] c, int[][] dp) {
        if (j - i < 2) return 0;
        if (dp[i][j] != -1) return dp[i][j];
        int best = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) best = Math.min(best, memo(i, k, c, dp) + memo(k, j, c, dp));
        return dp[i][j] = best + c[j] - c[i];
    }

    /** Approach 3: bottom-up over pieces, shorter index ranges first. O(m^3) time, O(m^2) space. */
    static int tabulation(int n, int[] cuts) {
        int[] c = withEnds(n, cuts);
        int m = c.length;
        int[][] dp = new int[m][m];                             // dp[i][i + 1] = 0: nothing to cut
        for (int i = m - 3; i >= 0; i--) {
            for (int j = i + 2; j < m; j++) {
                int best = Integer.MAX_VALUE;
                for (int k = i + 1; k < j; k++) best = Math.min(best, dp[i][k] + dp[k][j]);
                dp[i][j] = best + c[j] - c[i];
            }
        }
        return dp[0][m - 1];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[] cuts, int expected) {
        String in = "n=" + n + " cuts=" + Arrays.toString(cuts);
        check(recursive(n, cuts.clone()) == expected, "recursive failed for " + in);
        check(memoization(n, cuts.clone()) == expected, "memoization failed for " + in);
        check(tabulation(n, cuts.clone()) == expected, "tabulation failed for " + in);
    }

    public static void main(String[] args) {
        verify(7, new int[]{1, 3, 4, 5}, 16);                   // cut 3, then 5, then 1, then 4
        verify(9, new int[]{5, 6, 1, 4, 2}, 22);                // unsorted input
        verify(10, new int[]{2, 4, 7}, 20);                     // cut 4 (10), then 2 (4), then 7 (6)
        verify(10, new int[]{5}, 10);                           // one cut costs the whole stick
        verify(26, new int[]{1, 10, 14, 18}, 60);               // cutting nearest the middle first costs 62
        verify(10, new int[]{}, 0);                             // edge: no cuts
        verify(1_000_000, new int[]{1, 999_999}, 1_999_999);    // long stick

        Random rnd = new Random(328);
        for (int t = 0; t < 200; t++) {
            int n = 2 + rnd.nextInt(40);
            Set<Integer> set = new TreeSet<>();
            int want = rnd.nextInt(Math.min(8, n - 1) + 1);
            while (set.size() < want) set.add(1 + rnd.nextInt(n - 1));
            int[] cuts = set.stream().mapToInt(Integer::intValue).toArray();
            verify(n, cuts, recursive(n, cuts.clone()));
        }
        System.out.println("OK P328_MinimumCostToCutTheStick");
    }
}
