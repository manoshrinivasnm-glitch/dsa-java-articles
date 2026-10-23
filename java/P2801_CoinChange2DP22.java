import java.util.*;

/** TUF 2801 - Coin Change 2 (DP-22), LeetCode 518. Number of combinations (unordered) of coins, unlimited supply, summing to amount. */
public class P2801_CoinChange2DP22 {

    /** Approach 1: recursion over (coin index, remaining amount); a taken coin stays available. Exponential time. */
    static long recursive(int[] coins, int amount) {
        return ways(coins.length - 1, amount, coins);                // all coin types allowed
    }

    /** Combinations of coins[0..i] that sum to exactly t. */
    static long ways(int i, int t, int[] coins) {
        if (i == 0) return t % coins[0] == 0 ? 1 : 0;             // only coins[0] left: one way if it divides t
        long notTake = ways(i - 1, t, coins);                      // combinations with no more coin i
        long take = coins[i] <= t ? ways(i, t - coins[i], coins) : 0;   // stay on i: it may be used again
        return notTake + take;                                     // the two groups never overlap
    }

    /** Approach 2: memoization on (i, t). O(n * amount) time, O(n * amount) space plus the stack. */
    static long memoization(int[] coins, int amount) {
        long[][] dp = new long[coins.length][amount + 1];            // dp[i][t]: ways with coins[0..i] for t
        for (long[] row : dp) Arrays.fill(row, -1);                  // counts are >= 0, so -1 = unknown
        return memo(coins.length - 1, amount, coins, dp);
    }

    static long memo(int i, int t, int[] coins, long[][] dp) {
        if (i == 0) return t % coins[0] == 0 ? 1 : 0;             // only coins[0] left: one way if it divides t
        if (dp[i][t] != -1) return dp[i][t];                         // state solved before: reuse it
        long notTake = memo(i - 1, t, coins, dp);                    // stop using coin i
        long take = coins[i] <= t ? memo(i, t - coins[i], coins, dp) : 0;   // use coin i, stay on i
        return dp[i][t] = notTake + take;                            // store the combined count
    }

    /** Approach 3: tabulation, dp[i][t] = combinations of coins[0..i] for t. O(n * amount) time and space. */
    static long tabulation(int[] coins, int amount) {
        int n = coins.length;
        long[][] dp = new long[n][amount + 1];                       // dp[i][t]: ways with coins[0..i] for t
        for (int t = 0; t <= amount; t++) dp[0][t] = t % coins[0] == 0 ? 1 : 0;   // row 0: coins[0] must divide t
        for (int i = 1; i < n; i++) {
            for (int t = 0; t <= amount; t++) {                      // smaller amounts are filled first
                long notTake = dp[i - 1][t];                         // combinations without coin i
                long take = coins[i] <= t ? dp[i][t - coins[i]] : 0;      // same row: unlimited copies of coin i
                dp[i][t] = notTake + take;
            }
        }
        return dp[n - 1][amount];                                    // all coin types, full amount
    }

    /** Approach 4: one array, coins in the outer loop, amounts left to right. O(n * amount) time, O(amount) space. */
    static long spaceOptimized(int[] coins, int amount) {
        long[] ways = new long[amount + 1];                          // ways[t] = combinations so far for t
        ways[0] = 1;                                                 // one way to make 0: use no coins
        for (int c : coins) {                                        // coin loop outside: no order duplicates
            for (int t = c; t <= amount; t++) ways[t] += ways[t - c];   // left to right: c may repeat
        }
        return ways[amount];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] coins, int amount, long expected) {
        String in = Arrays.toString(coins) + " amount=" + amount;
        check(recursive(coins, amount) == expected, "recursive failed for " + in);
        check(memoization(coins, amount) == expected, "memoization failed for " + in);
        check(tabulation(coins, amount) == expected, "tabulation failed for " + in);
        check(spaceOptimized(coins, amount) == expected, "spaceOptimized failed for " + in);
    }

    /** For inputs too large for plain recursion. */
    static void verifyFast(int[] coins, int amount, long expected) {
        String in = Arrays.toString(coins) + " amount=" + amount;
        check(memoization(coins, amount) == expected, "memoization failed for " + in);
        check(tabulation(coins, amount) == expected, "tabulation failed for " + in);
        check(spaceOptimized(coins, amount) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 5}, 5, 4);               // 5, 2+2+1, 2+1+1+1, 1+1+1+1+1
        verify(new int[]{2}, 3, 0);                     // odd amount from 2s only
        verify(new int[]{10}, 10, 1);
        verify(new int[]{1, 2, 3}, 4, 4);               // 1111, 112, 22, 13 (order does not matter)
        verify(new int[]{3, 7}, 0, 1);                  // edge: amount 0 has exactly one combination, the empty one
        verify(new int[]{5, 7}, 3, 0);                  // every coin is larger than the amount
        verify(new int[]{2, 5, 3, 6}, 10, 5);           // 2x5, 2x2+3x2, 2x2+6, 2+3+5, 5x2
        verifyFast(new int[]{3, 5, 7, 8, 9, 10, 11}, 500, 35502874L);   // larger case from LeetCode

        // Cross-check on seeded random inputs.
        Random rnd = new Random(2801);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(4), amount = rnd.nextInt(20);
            List<Integer> pool = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
            Collections.shuffle(pool, rnd);                               // distinct denominations
            int[] coins = new int[n];
            for (int i = 0; i < n; i++) coins[i] = pool.get(i);
            verify(coins, amount, spaceOptimized(coins, amount));
        }
        System.out.println("OK P2801_CoinChange2DP22");
    }
}
