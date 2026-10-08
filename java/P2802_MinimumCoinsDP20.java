import java.util.*;

/** TUF 2802 - Minimum Coins (DP-20), LeetCode 322 Coin Change. Fewest coins (unlimited supply) summing to amount, or -1. */
public class P2802_MinimumCoinsDP20 {

    static final int INF = 1_000_000_000;          // "impossible"; 1 + INF still fits in an int

    /** Approach 1: recursion over (coin index, remaining amount); a taken coin stays available. Exponential time. */
    static int recursive(int[] coins, int amount) {
        int res = solve(coins.length - 1, amount, coins);
        return res >= INF ? -1 : res;
    }

    /** Fewest coins from coins[0..i] that sum to exactly t, or INF. */
    static int solve(int i, int t, int[] coins) {
        if (i == 0) return t % coins[0] == 0 ? t / coins[0] : INF;    // only coins[0] left: it must divide t
        int notTake = solve(i - 1, t, coins);
        int take = coins[i] <= t ? 1 + solve(i, t - coins[i], coins) : INF;   // stay on i: unlimited supply
        return Math.min(notTake, take);
    }

    /** Approach 2: memoization on (i, t). O(n * amount) time, O(n * amount) space plus the stack. */
    static int memoization(int[] coins, int amount) {
        int[][] dp = new int[coins.length][amount + 1];
        for (int[] row : dp) Arrays.fill(row, -1);
        int res = memo(coins.length - 1, amount, coins, dp);
        return res >= INF ? -1 : res;
    }

    static int memo(int i, int t, int[] coins, int[][] dp) {
        if (i == 0) return t % coins[0] == 0 ? t / coins[0] : INF;
        if (dp[i][t] != -1) return dp[i][t];
        int notTake = memo(i - 1, t, coins, dp);
        int take = coins[i] <= t ? 1 + memo(i, t - coins[i], coins, dp) : INF;
        return dp[i][t] = Math.min(notTake, take);
    }

    /** Approach 3: tabulation, dp[i][t] = fewest coins from coins[0..i] for t. O(n * amount) time and space. */
    static int tabulation(int[] coins, int amount) {
        int n = coins.length;
        int[][] dp = new int[n][amount + 1];
        for (int t = 0; t <= amount; t++) dp[0][t] = t % coins[0] == 0 ? t / coins[0] : INF;
        for (int i = 1; i < n; i++) {
            for (int t = 0; t <= amount; t++) {
                int notTake = dp[i - 1][t];
                int take = coins[i] <= t ? 1 + dp[i][t - coins[i]] : INF;  // same row: coin i may be used again
                dp[i][t] = Math.min(notTake, take);
            }
        }
        return dp[n - 1][amount] >= INF ? -1 : dp[n - 1][amount];
    }

    /** Approach 4: one array updated from left to right. O(n * amount) time, O(amount) space. */
    static int spaceOptimized(int[] coins, int amount) {
        int[] best = new int[amount + 1];
        Arrays.fill(best, INF);
        best[0] = 0;                                                    // zero coins make amount 0
        for (int c : coins) {
            for (int t = c; t <= amount; t++) {                          // left to right: best[t - c] may already use c
                best[t] = Math.min(best[t], 1 + best[t - c]);
            }
        }
        return best[amount] >= INF ? -1 : best[amount];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] coins, int amount, int expected) {
        String in = Arrays.toString(coins) + " amount=" + amount;
        check(recursive(coins, amount) == expected, "recursive failed for " + in);
        check(memoization(coins, amount) == expected, "memoization failed for " + in);
        check(tabulation(coins, amount) == expected, "tabulation failed for " + in);
        check(spaceOptimized(coins, amount) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 5}, 11, 3);              // 5 + 5 + 1
        verify(new int[]{2}, 3, -1);                    // odd amount from 2s only
        verify(new int[]{1}, 0, 0);                     // edge: amount 0 needs no coins
        verify(new int[]{1, 3, 4}, 6, 2);               // 3 + 3; largest-first greedy would use 4 + 1 + 1
        verify(new int[]{9, 6, 5, 1}, 11, 2);           // 6 + 5
        verify(new int[]{2, 5, 10, 1}, 27, 4);          // 10 + 10 + 5 + 2
        verify(new int[]{5, 7}, 3, -1);                 // every coin is larger than the amount
        verify(new int[]{2147483647}, 2, -1);           // a huge coin never fits; no overflow in the guards

        // Cross-check on seeded random inputs.
        Random rnd = new Random(2802);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(4), amount = rnd.nextInt(20);
            int[] coins = new int[n];
            for (int i = 0; i < n; i++) coins[i] = 1 + rnd.nextInt(9);
            verify(coins, amount, spaceOptimized(coins, amount));
        }
        System.out.println("OK P2802_MinimumCoinsDP20");
    }
}
