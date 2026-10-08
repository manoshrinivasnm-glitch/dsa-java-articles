import java.util.*;

/** TUF 327 - Matrix chain multiplication. Matrix i is arr[i-1] x arr[i]; minimise scalar multiplications. */
public class P327_MatrixChainMultiplication {

    /** Approach 1: try every last split of every sub-chain. Exponential time, O(n) stack. */
    static long recursive(int[] arr) {
        return solve(1, arr.length - 1, arr);
    }

    static long solve(int i, int j, int[] arr) {
        if (i == j) return 0;                                   // a single matrix costs nothing
        long best = Long.MAX_VALUE;
        for (int k = i; k < j; k++) {                           // (A_i .. A_k) times (A_k+1 .. A_j)
            long cost = solve(i, k, arr) + solve(k + 1, j, arr) + (long) arr[i - 1] * arr[k] * arr[j];
            best = Math.min(best, cost);
        }
        return best;
    }

    /** Approach 2: cache each (i, j) sub-chain. O(n^3) time, O(n^2) space. */
    static long memoization(int[] arr) {
        int n = arr.length;
        long[][] dp = new long[n][n];
        for (long[] row : dp) Arrays.fill(row, -1);
        return memo(1, n - 1, arr, dp);
    }

    static long memo(int i, int j, int[] arr, long[][] dp) {
        if (i == j) return 0;
        if (dp[i][j] != -1) return dp[i][j];
        long best = Long.MAX_VALUE;
        for (int k = i; k < j; k++) {
            long cost = memo(i, k, arr, dp) + memo(k + 1, j, arr, dp) + (long) arr[i - 1] * arr[k] * arr[j];
            best = Math.min(best, cost);
        }
        return dp[i][j] = best;
    }

    /** Approach 3: fill the table so every shorter sub-chain is ready first. O(n^3) time, O(n^2) space. */
    static long tabulation(int[] arr) {
        int n = arr.length;
        long[][] dp = new long[n][n];                           // dp[i][i] = 0 already
        for (int i = n - 1; i >= 1; i--) {
            for (int j = i + 1; j < n; j++) {
                long best = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    best = Math.min(best, dp[i][k] + dp[k + 1][j] + (long) arr[i - 1] * arr[k] * arr[j]);
                }
                dp[i][j] = best;
            }
        }
        return dp[1][n - 1];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, long expected) {
        String in = Arrays.toString(arr);
        check(recursive(arr) == expected, "recursive failed for " + in);
        check(memoization(arr) == expected, "memoization failed for " + in);
        check(tabulation(arr) == expected, "tabulation failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{10, 20, 30, 40, 50}, 38000);           // (((AB)C)D)
        verify(new int[]{40, 20, 30, 10, 30}, 26000);           // ((A(BC))D)
        verify(new int[]{1, 2, 3, 4, 3}, 30);
        verify(new int[]{10, 20, 30}, 6000);                    // two matrices: one way only
        verify(new int[]{7, 5, 7, 9}, 630);                     // A(BC); cheapest-pair-first does (AB)C = 686
        verify(new int[]{10, 30}, 0);                           // edge: one matrix, nothing to multiply
        verify(new int[]{500, 500, 500, 500}, 250_000_000L);    // two products of 500^3 each

        long chain = memoization(new int[]{500, 1, 500, 1, 500, 1, 500, 1, 500, 1, 500});
        check(chain == tabulation(new int[]{500, 1, 500, 1, 500, 1, 500, 1, 500, 1, 500}), "alternating dims");

        int[] wide = new int[101];                              // 100 matrices of 500 x 500: needs long
        Arrays.fill(wide, 500);
        check(memoization(wide) == 99L * 500 * 500 * 500 && tabulation(wide) == 99L * 500 * 500 * 500, "overflow case");

        Random rnd = new Random(327);
        for (int t = 0; t < 200; t++) {
            int[] arr = new int[2 + rnd.nextInt(8)];
            for (int i = 0; i < arr.length; i++) arr[i] = 1 + rnd.nextInt(30);
            verify(arr, recursive(arr));
        }
        System.out.println("OK P327_MatrixChainMultiplication");
    }
}
