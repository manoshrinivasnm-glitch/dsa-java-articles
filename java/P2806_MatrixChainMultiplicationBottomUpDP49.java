import java.util.*;

/** TUF 2806 - Matrix Chain Multiplication, bottom-up. Matrix i is arr[i-1] x arr[i]; minimise scalar multiplications. */
public class P2806_MatrixChainMultiplicationBottomUpDP49 {

    /** Reference: the top-down memoized recursion that the tables below replace. O(n^3) time, O(n^2) space. */
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
            best = Math.min(best, memo(i, k, arr, dp) + memo(k + 1, j, arr, dp) + (long) arr[i - 1] * arr[k] * arr[j]);
        }
        return dp[i][j] = best;
    }

    /** Approach 1: rows bottom to top, columns left to right. O(n^3) time, O(n^2) space. */
    static long tabulation(int[] arr) {
        int n = arr.length;
        long[][] dp = new long[n][n];                           // base case dp[i][i] = 0 is the default
        for (int i = n - 1; i >= 1; i--) {
            for (int j = i + 1; j < n; j++) {
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {                   // dp[i][k] is left in row i, dp[k + 1][j] is below
                    dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k + 1][j] + (long) arr[i - 1] * arr[k] * arr[j]);
                }
            }
        }
        return dp[1][n - 1];
    }

    /** Approach 2: the same table filled diagonal by diagonal, by chain length. O(n^3) time, O(n^2) space. */
    static long tabulationByLength(int[] arr) {
        int n = arr.length;
        long[][] dp = new long[n][n];
        for (int len = 2; len < n; len++) {                     // number of matrices in the sub-chain
            for (int i = 1; i + len - 1 < n; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k + 1][j] + (long) arr[i - 1] * arr[k] * arr[j]);
                }
            }
        }
        return dp[1][n - 1];
    }

    /** Approach 3: also remember the best split and rebuild the bracketing, matrices named A, B, C, ... */
    static String parenthesization(int[] arr) {
        int n = arr.length;
        long[][] dp = new long[n][n];
        int[][] split = new int[n][n];
        for (int i = n - 1; i >= 1; i--) {
            for (int j = i + 1; j < n; j++) {
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j] + (long) arr[i - 1] * arr[k] * arr[j];
                    if (cost < dp[i][j]) { dp[i][j] = cost; split[i][j] = k; }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        build(1, n - 1, split, sb);
        return sb.toString();
    }

    static void build(int i, int j, int[][] split, StringBuilder sb) {
        if (i == j) { sb.append((char) ('A' + i - 1)); return; }
        sb.append('(');
        build(i, split[i][j], split, sb);
        build(split[i][j] + 1, j, split, sb);
        sb.append(')');
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Evaluates a bracketing string: returns {rows, cols, cost}; pos[0] is the read cursor. */
    static long[] costOf(String s, int[] pos, int[] arr) {
        char c = s.charAt(pos[0]++);
        if (c != '(') { int m = c - 'A' + 1; return new long[]{arr[m - 1], arr[m], 0}; }
        long[] left = costOf(s, pos, arr), right = costOf(s, pos, arr);
        pos[0]++;                                               // skip ')'
        check(left[1] == right[0], "dimension mismatch in " + s);
        return new long[]{left[0], right[1], left[2] + right[2] + left[0] * left[1] * right[1]};
    }

    static void verify(int[] arr, long expected) {
        String in = Arrays.toString(arr);
        check(memoization(arr) == expected, "memoization failed for " + in);
        check(tabulation(arr) == expected, "tabulation failed for " + in);
        check(tabulationByLength(arr) == expected, "tabulationByLength failed for " + in);
        String p = parenthesization(arr);
        check(p.chars().filter(Character::isLetter).count() == arr.length - 1, "wrong matrix count in " + p);
        check(costOf(p, new int[]{0}, arr)[2] == expected, "bracketing " + p + " is not optimal for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{10, 20, 30, 40, 50}, 38000);
        verify(new int[]{40, 20, 30, 10, 30}, 26000);
        verify(new int[]{1, 2, 3, 4, 3}, 30);
        verify(new int[]{10, 20, 30}, 6000);
        verify(new int[]{10, 30}, 0);                           // edge: one matrix
        check(parenthesization(new int[]{10, 20, 30, 40, 50}).equals("(((AB)C)D)"), "bracketing 1");
        check(parenthesization(new int[]{40, 20, 30, 10, 30}).equals("((A(BC))D)"), "bracketing 2");
        check(parenthesization(new int[]{10, 30}).equals("A"), "bracketing of one matrix");

        int[] wide = new int[101];                              // 100 matrices of 500 x 500: needs long
        Arrays.fill(wide, 500);
        check(tabulation(wide) == 99L * 500 * 500 * 500 && tabulationByLength(wide) == tabulation(wide), "overflow case");

        Random rnd = new Random(2806);
        for (int t = 0; t < 300; t++) {
            int[] arr = new int[2 + rnd.nextInt(12)];
            for (int i = 0; i < arr.length; i++) arr[i] = 1 + rnd.nextInt(40);
            verify(arr, memoization(arr));
        }
        System.out.println("OK P2806_MatrixChainMultiplicationBottomUpDP49");
    }
}
