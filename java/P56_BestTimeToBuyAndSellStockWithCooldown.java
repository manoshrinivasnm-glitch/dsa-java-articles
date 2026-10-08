import java.util.*;

/** TUF 56 - Best Time to Buy and Sell Stock with Cooldown. Unlimited transactions, one share at a time, and no buying on the day right after a sell. */
public class P56_BestTimeToBuyAndSellStockWithCooldown {

    /** Approach 1: recursion on (day, may I buy?); a sell jumps two days ahead. O(2^n) time, O(n) stack. */
    static int recursive(int[] prices) {
        return solve(0, 1, prices);
    }

    static int solve(int i, int canBuy, int[] prices) {
        if (i >= prices.length) return 0;
        if (canBuy == 1) {
            return Math.max(-prices[i] + solve(i + 1, 0, prices),     // buy today
                            solve(i + 1, 1, prices));                 // skip
        }
        return Math.max(prices[i] + solve(i + 2, 1, prices),          // sell today, tomorrow is the cooldown
                        solve(i + 1, 0, prices));                     // keep holding
    }

    /** Approach 2: memoization over the 2n states. O(n) time, O(n) space plus O(n) stack. */
    static int memoization(int[] prices) {
        int[][] dp = new int[prices.length][2];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(0, 1, prices, dp);
    }

    static int memo(int i, int canBuy, int[] prices, int[][] dp) {
        if (i >= prices.length) return 0;
        if (dp[i][canBuy] != -1) return dp[i][canBuy];
        int best;
        if (canBuy == 1) best = Math.max(-prices[i] + memo(i + 1, 0, prices, dp), memo(i + 1, 1, prices, dp));
        else best = Math.max(prices[i] + memo(i + 2, 1, prices, dp), memo(i + 1, 0, prices, dp));
        return dp[i][canBuy] = best;
    }

    /** Approach 3: tabulation from the last day backwards; two extra rows absorb the i + 2 jump. O(n) time, O(n) space. */
    static int tabulation(int[] prices) {
        int n = prices.length;
        int[][] dp = new int[n + 2][2];                     // dp[n][*] = dp[n + 1][*] = 0
        for (int i = n - 1; i >= 0; i--) {
            dp[i][1] = Math.max(-prices[i] + dp[i + 1][0], dp[i + 1][1]);
            dp[i][0] = Math.max(prices[i] + dp[i + 2][1], dp[i + 1][0]);
        }
        return dp[0][1];
    }

    /** Approach 4: day i reads days i + 1 and i + 2 only, so keep three numbers. O(n) time, O(1) space. */
    static int spaceOptimized(int[] prices) {
        int buy1 = 0, hold1 = 0;                            // dp[i + 1][1], dp[i + 1][0]
        int buy2 = 0;                                       // dp[i + 2][1]
        for (int i = prices.length - 1; i >= 0; i--) {
            int buy = Math.max(-prices[i] + hold1, buy1);
            int hold = Math.max(prices[i] + buy2, hold1);
            buy2 = buy1;
            buy1 = buy;
            hold1 = hold;
        }
        return buy1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] prices, int expected) {
        String in = Arrays.toString(prices);
        check(recursive(prices) == expected, "recursive failed for " + in);
        check(memoization(prices) == expected, "memoization failed for " + in);
        check(tabulation(prices) == expected, "tabulation failed for " + in);
        check(spaceOptimized(prices) == expected, "spaceOptimized failed for " + in);
    }

    /** Independent reference: enumerate the action of every day (rest, buy, sell) and simulate the rules. */
    static int reference(int[] p) {
        int n = p.length, best = 0;
        int total = 1;
        for (int i = 0; i < n; i++) total *= 3;
        for (int code = 0; code < total; code++) {
            int c = code, profit = 0, lastSell = -2;
            boolean holding = false, ok = true;
            for (int i = 0; i < n && ok; i++, c /= 3) {
                int act = c % 3;
                if (act == 1) {                             // buy
                    if (holding || lastSell == i - 1) ok = false;
                    else { holding = true; profit -= p[i]; }
                } else if (act == 2) {                      // sell
                    if (!holding) ok = false;
                    else { holding = false; profit += p[i]; lastSell = i; }
                }
            }
            if (ok && !holding) best = Math.max(best, profit);
        }
        return best;
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 0, 2}, 3);                    // buy, sell, cooldown, buy, sell
        verify(new int[]{1, 2, 4}, 3);                          // one trade: 4 - 1
        verify(new int[]{1, 3, 1, 3}, 2);                       // without cooldown it would be 4
        verify(new int[]{6, 1, 6, 4, 3, 0, 2}, 7);              // (6 - 1) + (2 - 0)
        verify(new int[]{5, 4, 3}, 0);                          // falling prices
        verify(new int[]{1}, 0);                                // edge: one day
        verify(new int[]{}, 0);                                 // edge: no days

        Random rnd = new Random(56);
        for (int t = 0; t < 200; t++) {
            int[] p = new int[rnd.nextInt(9)];
            for (int i = 0; i < p.length; i++) p[i] = rnd.nextInt(20);
            verify(p, reference(p));
        }
        System.out.println("OK P56_BestTimeToBuyAndSellStockWithCooldown");
    }
}
