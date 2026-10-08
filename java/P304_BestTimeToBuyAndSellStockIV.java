import java.util.*;

/** TUF 304 - Best time to buy and sell stock IV. At most k transactions, never holding more than one share; return the maximum profit. */
public class P304_BestTimeToBuyAndSellStockIV {

    /** Approach 1: recursion on (day, operation number). Even t = next move is a buy, odd t = a sell. O(2^n) time, O(n) stack. */
    static int recursive(int k, int[] prices) {
        return solve(0, 0, k, prices);
    }

    static int solve(int i, int t, int k, int[] prices) {
        if (i == prices.length || t == 2 * k) return 0;
        if (t % 2 == 0) {
            return Math.max(-prices[i] + solve(i + 1, t + 1, k, prices),     // buy today
                            solve(i + 1, t, k, prices));                     // skip
        }
        return Math.max(prices[i] + solve(i + 1, t + 1, k, prices),          // sell today
                        solve(i + 1, t, k, prices));                         // keep holding
    }

    /** Approach 2: memoization over n * 2k states. O(n * k) time, O(n * k) space plus O(n) stack. */
    static int memoization(int k, int[] prices) {
        int[][] dp = new int[prices.length][2 * k];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(0, 0, k, prices, dp);
    }

    static int memo(int i, int t, int k, int[] prices, int[][] dp) {
        if (i == prices.length || t == 2 * k) return 0;
        if (dp[i][t] != -1) return dp[i][t];
        int sign = (t % 2 == 0) ? -1 : 1;                   // buying pays the price, selling receives it
        int best = Math.max(sign * prices[i] + memo(i + 1, t + 1, k, prices, dp), memo(i + 1, t, k, prices, dp));
        return dp[i][t] = best;
    }

    /** Approach 3: tabulation from the last day backwards. O(n * k) time, O(n * k) space. */
    static int tabulation(int k, int[] prices) {
        int n = prices.length;
        int[][] dp = new int[n + 1][2 * k + 1];             // dp[n][*] = 0 and dp[*][2k] = 0
        for (int i = n - 1; i >= 0; i--) {
            for (int t = 2 * k - 1; t >= 0; t--) {
                int sign = (t % 2 == 0) ? -1 : 1;
                dp[i][t] = Math.max(sign * prices[i] + dp[i + 1][t + 1], dp[i + 1][t]);
            }
        }
        return dp[0][0];
    }

    /** Approach 4: day i only reads day i + 1, so keep two rows of length 2k + 1. O(n * k) time, O(k) space. */
    static int spaceOptimized(int k, int[] prices) {
        int[] ahead = new int[2 * k + 1], cur = new int[2 * k + 1];
        for (int i = prices.length - 1; i >= 0; i--) {
            for (int t = 2 * k - 1; t >= 0; t--) {
                int sign = (t % 2 == 0) ? -1 : 1;
                cur[t] = Math.max(sign * prices[i] + ahead[t + 1], ahead[t]);
            }
            int[] tmp = ahead; ahead = cur; cur = tmp;
        }
        return ahead[0];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int k, int[] prices, int expected) {
        String in = "k = " + k + ", " + Arrays.toString(prices);
        check(recursive(k, prices) == expected, "recursive failed for " + in);
        check(memoization(k, prices) == expected, "memoization failed for " + in);
        check(tabulation(k, prices) == expected, "tabulation failed for " + in);
        check(spaceOptimized(k, prices) == expected, "spaceOptimized failed for " + in);
    }

    /** Independent reference for small inputs: try every way to pick k non-overlapping (buy, sell) pairs. */
    static int reference(int k, int[] p, int from) {
        if (k == 0) return 0;
        int best = 0;
        for (int b = from; b < p.length; b++) {
            for (int s = b + 1; s < p.length; s++) {
                if (p[s] > p[b]) best = Math.max(best, p[s] - p[b] + reference(k - 1, p, s + 1));
            }
        }
        return best;
    }

    public static void main(String[] args) {
        verify(2, new int[]{2, 4, 1}, 2);                       // buy 2, sell 4
        verify(2, new int[]{3, 2, 6, 5, 0, 3}, 7);              // (6 - 2) + (3 - 0)
        verify(1, new int[]{7, 1, 5, 3, 6, 4}, 5);              // k = 1 is the single-trade problem
        verify(2, new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, 13); // k = 2 is Stock III
        verify(3, new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, 15); // three rises, all taken
        verify(5, new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, 15); // more trades than rises: unlimited answer
        verify(0, new int[]{1, 5, 2, 8}, 0);                    // edge: no transactions allowed
        verify(3, new int[]{}, 0);                              // edge: no days
        verify(4, new int[]{9, 8, 7}, 0);                       // falling prices

        Random rnd = new Random(304);
        for (int t = 0; t < 300; t++) {
            int[] p = new int[rnd.nextInt(11)];
            for (int i = 0; i < p.length; i++) p[i] = rnd.nextInt(30);
            int k = rnd.nextInt(4);
            verify(k, p, reference(k, p, 0));
        }
        System.out.println("OK P304_BestTimeToBuyAndSellStockIV");
    }
}
