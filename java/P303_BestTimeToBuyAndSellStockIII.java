import java.util.*;

/** TUF 303 - Best time to buy and sell stock III. At most two transactions, never holding more than one share; return the maximum profit. */
public class P303_BestTimeToBuyAndSellStockIII {

    /** Approach 1: recursion on (day, may I buy?, transactions left). O(2^n) time, O(n) stack. */
    static int recursive(int[] prices) {
        return solve(0, 1, 2, prices);
    }

    /** Best profit from day i onward with cap transactions still allowed; a transaction is used up when we sell. */
    static int solve(int i, int canBuy, int cap, int[] prices) {
        if (i == prices.length || cap == 0) return 0;
        if (canBuy == 1) {
            return Math.max(-prices[i] + solve(i + 1, 0, cap, prices),       // buy today
                            solve(i + 1, 1, cap, prices));                   // skip
        }
        return Math.max(prices[i] + solve(i + 1, 1, cap - 1, prices),        // sell: one transaction done
                        solve(i + 1, 0, cap, prices));                       // keep holding
    }

    /** Approach 2: memoization over n * 2 * 3 states. O(n) time, O(n) space plus O(n) stack. */
    static int memoization(int[] prices) {
        int[][][] dp = new int[prices.length][2][3];
        for (int[][] a : dp) for (int[] b : a) Arrays.fill(b, -1);
        return memo(0, 1, 2, prices, dp);
    }

    static int memo(int i, int canBuy, int cap, int[] prices, int[][][] dp) {
        if (i == prices.length || cap == 0) return 0;
        if (dp[i][canBuy][cap] != -1) return dp[i][canBuy][cap];
        int best;
        if (canBuy == 1) {
            best = Math.max(-prices[i] + memo(i + 1, 0, cap, prices, dp), memo(i + 1, 1, cap, prices, dp));
        } else {
            best = Math.max(prices[i] + memo(i + 1, 1, cap - 1, prices, dp), memo(i + 1, 0, cap, prices, dp));
        }
        return dp[i][canBuy][cap] = best;
    }

    /** Approach 3: tabulation from the last day backwards. O(n) time, O(n) space. */
    static int tabulation(int[] prices) {
        int n = prices.length;
        int[][][] dp = new int[n + 1][2][3];                // dp[n][*][*] = 0 and dp[*][*][0] = 0
        for (int i = n - 1; i >= 0; i--) {
            for (int cap = 1; cap <= 2; cap++) {
                dp[i][1][cap] = Math.max(-prices[i] + dp[i + 1][0][cap], dp[i + 1][1][cap]);
                dp[i][0][cap] = Math.max(prices[i] + dp[i + 1][1][cap - 1], dp[i + 1][0][cap]);
            }
        }
        return dp[0][1][2];
    }

    /** Approach 4: day i only reads day i + 1, so keep two 2 x 3 tables. O(n) time, O(1) space. */
    static int spaceOptimized(int[] prices) {
        int[][] ahead = new int[2][3], cur = new int[2][3];
        for (int i = prices.length - 1; i >= 0; i--) {
            for (int cap = 1; cap <= 2; cap++) {
                cur[1][cap] = Math.max(-prices[i] + ahead[0][cap], ahead[1][cap]);
                cur[0][cap] = Math.max(prices[i] + ahead[1][cap - 1], ahead[0][cap]);
            }
            int[][] t = ahead; ahead = cur; cur = t;
        }
        return ahead[1][2];
    }

    /** Approach 5: forward state machine with four running values. O(n) time, O(1) space. */
    static int fourStates(int[] prices) {
        int buy1 = Integer.MIN_VALUE, sell1 = 0;            // best balance after the first buy / first sell
        int buy2 = Integer.MIN_VALUE, sell2 = 0;            // best balance after the second buy / second sell
        for (int p : prices) {
            buy1 = Math.max(buy1, -p);
            sell1 = Math.max(sell1, buy1 + p);
            buy2 = Math.max(buy2, sell1 - p);
            sell2 = Math.max(sell2, buy2 + p);
        }
        return sell2;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Independent reference: best single trade in prices[0..s] plus best single trade in prices[s..n-1]. */
    static int splitReference(int[] p) {
        int best = 0;
        for (int s = 0; s < p.length; s++) {
            int left = 0, right = 0;
            for (int a = 0; a <= s; a++) for (int b = a; b <= s; b++) left = Math.max(left, p[b] - p[a]);
            for (int a = s; a < p.length; a++) for (int b = a; b < p.length; b++) right = Math.max(right, p[b] - p[a]);
            best = Math.max(best, left + right);
        }
        return best;
    }

    static void verify(int[] prices, int expected) {
        String in = Arrays.toString(prices);
        check(recursive(prices) == expected, "recursive failed for " + in);
        check(memoization(prices) == expected, "memoization failed for " + in);
        check(tabulation(prices) == expected, "tabulation failed for " + in);
        check(spaceOptimized(prices) == expected, "spaceOptimized failed for " + in);
        check(fourStates(prices) == expected, "fourStates failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 3, 5, 0, 0, 3, 1, 4}, 6);           // (3 - 0) + (4 - 1)
        verify(new int[]{1, 2, 3, 4, 5}, 4);                    // one transaction is enough
        verify(new int[]{7, 6, 4, 3, 1}, 0);                    // never trade
        verify(new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, 13);    // (7 - 1) + (9 - 2); three rises but only two trades
        verify(new int[]{2, 1, 4, 5, 2, 9, 7}, 11);             // (5 - 1) + (9 - 2)
        verify(new int[]{6}, 0);                                // edge: one day
        verify(new int[]{}, 0);                                 // edge: no days

        Random rnd = new Random(303);
        for (int t = 0; t < 300; t++) {
            int[] p = new int[rnd.nextInt(13)];
            for (int i = 0; i < p.length; i++) p[i] = rnd.nextInt(30);
            verify(p, splitReference(p));
        }
        System.out.println("OK P303_BestTimeToBuyAndSellStockIII");
    }
}
