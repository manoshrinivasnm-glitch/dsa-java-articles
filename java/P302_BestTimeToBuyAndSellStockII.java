import java.util.*;

/** TUF 302 - Best time to buy and sell stock II. Any number of transactions, but at most one share held at a time; return the maximum profit. */
public class P302_BestTimeToBuyAndSellStockII {

    /** Approach 1: recursion on (day, may I buy?). O(2^n) time, O(n) stack. */
    static int recursive(int[] prices) {
        return solve(0, 1, prices);
    }

    /** Best profit from day i onward; canBuy = 1 means no share is held right now. */
    static int solve(int i, int canBuy, int[] prices) {
        if (i == prices.length) return 0;
        if (canBuy == 1) {
            return Math.max(-prices[i] + solve(i + 1, 0, prices),     // buy today
                            solve(i + 1, 1, prices));                 // skip
        }
        return Math.max(prices[i] + solve(i + 1, 1, prices),          // sell today
                        solve(i + 1, 0, prices));                     // keep holding
    }

    /** Approach 2: memoization over the 2n states. O(n) time, O(n) space plus O(n) stack. */
    static int memoization(int[] prices) {
        int[][] dp = new int[prices.length][2];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(0, 1, prices, dp);
    }

    static int memo(int i, int canBuy, int[] prices, int[][] dp) {
        if (i == prices.length) return 0;
        if (dp[i][canBuy] != -1) return dp[i][canBuy];
        int best;
        if (canBuy == 1) best = Math.max(-prices[i] + memo(i + 1, 0, prices, dp), memo(i + 1, 1, prices, dp));
        else best = Math.max(prices[i] + memo(i + 1, 1, prices, dp), memo(i + 1, 0, prices, dp));
        return dp[i][canBuy] = best;
    }

    /** Approach 3: tabulation from the last day backwards. O(n) time, O(n) space. */
    static int tabulation(int[] prices) {
        int n = prices.length;
        int[][] dp = new int[n + 1][2];                     // dp[n][*] = 0: no days left
        for (int i = n - 1; i >= 0; i--) {
            dp[i][1] = Math.max(-prices[i] + dp[i + 1][0], dp[i + 1][1]);
            dp[i][0] = Math.max(prices[i] + dp[i + 1][1], dp[i + 1][0]);
        }
        return dp[0][1];
    }

    /** Approach 4: day i only reads day i + 1, so keep two numbers. O(n) time, O(1) space. */
    static int spaceOptimized(int[] prices) {
        int aheadBuy = 0, aheadHold = 0;                    // dp[i + 1][1], dp[i + 1][0]
        for (int i = prices.length - 1; i >= 0; i--) {
            int buy = Math.max(-prices[i] + aheadHold, aheadBuy);
            int hold = Math.max(prices[i] + aheadBuy, aheadHold);
            aheadBuy = buy;
            aheadHold = hold;
        }
        return aheadBuy;
    }

    /** Approach 5: greedy, collect every upward step. O(n) time, O(1) space. */
    static int greedy(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) profit += prices[i] - prices[i - 1];
        }
        return profit;
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
        check(greedy(prices) == expected, "greedy failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{7, 1, 5, 3, 6, 4}, 7);                 // (5 - 1) + (6 - 3)
        verify(new int[]{1, 2, 3, 4, 5}, 4);                    // one long rise
        verify(new int[]{7, 6, 4, 3, 1}, 0);                    // never trade
        verify(new int[]{3, 3, 5, 0, 0, 3, 1, 4}, 8);           // 2 + 3 + 3
        verify(new int[]{2, 1, 2, 0, 1}, 2);
        verify(new int[]{4}, 0);                                // edge: one day
        verify(new int[]{}, 0);                                 // edge: no days

        Random rnd = new Random(302);
        for (int t = 0; t < 300; t++) {
            int[] p = new int[rnd.nextInt(14)];
            for (int i = 0; i < p.length; i++) p[i] = rnd.nextInt(30);
            verify(p, recursive(p));
        }
        int[] big = new int[30000];
        for (int i = 0; i < big.length; i++) big[i] = (i * 37) % 1000;
        check(tabulation(big) == greedy(big) && spaceOptimized(big) == greedy(big), "large input mismatch");
        System.out.println("OK P302_BestTimeToBuyAndSellStockII");
    }
}
