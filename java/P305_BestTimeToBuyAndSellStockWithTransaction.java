import java.util.*;

/** TUF 305 - Best time to buy and sell stock with transaction fees. Unlimited transactions, one share at a time, each completed transaction costs fee. */
public class P305_BestTimeToBuyAndSellStockWithTransaction {

    /** Approach 1: recursion on (day, may I buy?); the fee is paid when selling. O(2^n) time, O(n) stack. */
    static int recursive(int[] prices, int fee) {
        return solve(0, 1, prices, fee);  // day 0, free to buy
    }

    static int solve(int i, int canBuy, int[] prices, int fee) {
        if (i == prices.length) return 0;                                   // no days left: no more profit
        if (canBuy == 1) {                                                  // no share held: we may buy
            return Math.max(-prices[i] + solve(i + 1, 0, prices, fee),      // buy today
                            solve(i + 1, 1, prices, fee));                  // skip
        }
        return Math.max(prices[i] - fee + solve(i + 1, 1, prices, fee),     // sell today and pay the fee
                        solve(i + 1, 0, prices, fee));                      // keep holding
    }

    /** Approach 2: memoization over the 2n states. O(n) time, O(n) space plus O(n) stack. */
    static int memoization(int[] prices, int fee) {
        int[][] dp = new int[prices.length][2];     // dp[i][canBuy] caches solve(i, canBuy)
        for (int[] row : dp) Arrays.fill(row, -1);  // -1 marks a state not solved yet
        return memo(0, 1, prices, fee, dp);         // day 0, free to buy
    }

    static int memo(int i, int canBuy, int[] prices, int fee, int[][] dp) {
        if (i == prices.length) return 0;               // no days left: no more profit
        if (dp[i][canBuy] != -1) return dp[i][canBuy];  // already solved: reuse the stored answer
        int best;
        // free to buy: buy today or skip
        if (canBuy == 1) best = Math.max(-prices[i] + memo(i + 1, 0, prices, fee, dp), memo(i + 1, 1, prices, fee, dp));
        // holding: sell and pay the fee, or keep holding
        else best = Math.max(prices[i] - fee + memo(i + 1, 1, prices, fee, dp), memo(i + 1, 0, prices, fee, dp));
        return dp[i][canBuy] = best;
    }

    /** Approach 3: tabulation from the last day backwards. O(n) time, O(n) space. */
    static int tabulation(int[] prices, int fee) {
        int n = prices.length;
        int[][] dp = new int[n + 1][2];                     // dp[n][*] = 0
        for (int i = n - 1; i >= 0; i--) {                  // walk the days from last to first
            // free to buy: buy today or skip
            dp[i][1] = Math.max(-prices[i] + dp[i + 1][0], dp[i + 1][1]);
            // holding: sell and pay the fee, or keep holding
            dp[i][0] = Math.max(prices[i] - fee + dp[i + 1][1], dp[i + 1][0]);
        }
        return dp[0][1];                                    // day 0, free to buy
    }

    /** Approach 4: day i only reads day i + 1, so keep two numbers. O(n) time, O(1) space. */
    static int spaceOptimized(int[] prices, int fee) {
        int aheadBuy = 0, aheadHold = 0;                           // dp[i + 1][1], dp[i + 1][0]
        for (int i = prices.length - 1; i >= 0; i--) {             // walk the days from last to first
            int buy = Math.max(-prices[i] + aheadHold, aheadBuy);  // dp[i][1]: buy today or skip
            // dp[i][0]: sell (minus the fee) or keep holding
            int hold = Math.max(prices[i] - fee + aheadBuy, aheadHold);
            aheadBuy = buy;                                        // today becomes "ahead" for the day before
            aheadHold = hold;
        }
        return aheadBuy;                                           // day 0, free to buy
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] prices, int fee, int expected) {
        String in = Arrays.toString(prices) + ", fee = " + fee;
        check(recursive(prices, fee) == expected, "recursive failed for " + in);
        check(memoization(prices, fee) == expected, "memoization failed for " + in);
        check(tabulation(prices, fee) == expected, "tabulation failed for " + in);
        check(spaceOptimized(prices, fee) == expected, "spaceOptimized failed for " + in);
    }

    /** Independent reference: choose every set of non-overlapping (buy, sell) pairs, each paying the fee. */
    static int reference(int[] p, int fee, int from) {
        int best = 0;
        for (int b = from; b < p.length; b++) {
            for (int s = b + 1; s < p.length; s++) {
                best = Math.max(best, p[s] - p[b] - fee + reference(p, fee, s + 1));
            }
        }
        return best;
    }

    public static void main(String[] args) {
        verify(new int[]{1, 3, 2, 8, 4, 9}, 2, 8);              // (8 - 1 - 2) + (9 - 4 - 2)
        verify(new int[]{1, 3, 7, 5, 10, 3}, 3, 6);             // one long trade beats two short ones
        verify(new int[]{1, 3, 2, 8, 4, 9}, 0, 13);             // fee 0 is the unlimited problem
        verify(new int[]{1, 4, 2, 6}, 10, 0);                   // the fee eats every trade
        verify(new int[]{4, 1, 7}, 6, 0);                       // a profit equal to the fee gains nothing
        verify(new int[]{5}, 1, 0);                             // edge: one day
        verify(new int[]{}, 2, 0);                              // edge: no days

        Random rnd = new Random(305);
        for (int t = 0; t < 300; t++) {
            int[] p = new int[rnd.nextInt(10)];
            for (int i = 0; i < p.length; i++) p[i] = rnd.nextInt(30);
            int fee = rnd.nextInt(6);
            verify(p, fee, reference(p, fee, 0));
        }
        System.out.println("OK P305_BestTimeToBuyAndSellStockWithTransaction");
    }
}
