import java.util.*;

/** TUF 316 - Coin change II. Count the combinations of coins (unlimited copies of each) that sum to amount. */
public class P316_CoinChangeII {

    /** Approach 1: recursion on (coin index, remaining amount). Exponential time, O(amount + n) stack. */
    static long recursion(int[] coins, int amount) {
        return count(0, amount, coins);
    }

    static long count(int i, int rem, int[] coins) {
        if (rem == 0) return 1;                                  // nothing left to pay: one finished combination
        if (i == coins.length) return 0;                         // money left but no denominations left
        long skip = count(i + 1, rem, coins);                    // never use coins[i] again
        long take = coins[i] <= rem ? count(i, rem - coins[i], coins) : 0;   // one more coins[i], stay on i
        return skip + take;
    }

    /** Approach 2: memoised recursion. O(n * amount) time, O(n * amount) space plus stack. */
    static long memoization(int[] coins, int amount) {
        long[][] dp = new long[coins.length][amount + 1];
        for (long[] row : dp) Arrays.fill(row, -1);
        return memo(0, amount, coins, dp);
    }

    static long memo(int i, int rem, int[] coins, long[][] dp) {
        if (rem == 0) return 1;
        if (i == coins.length) return 0;
        if (dp[i][rem] != -1) return dp[i][rem];
        long skip = memo(i + 1, rem, coins, dp);
        long take = coins[i] <= rem ? memo(i, rem - coins[i], coins, dp) : 0;
        return dp[i][rem] = skip + take;
    }

    /** Approach 3: bottom-up table, last coin first. O(n * amount) time, O(n * amount) space. */
    static long tabulation(int[] coins, int amount) {
        int n = coins.length;
        long[][] dp = new long[n + 1][amount + 1];               // dp[i][r]: combinations for r using coins[i..n-1]
        dp[n][0] = 1;                                            // no coins: only r = 0 is reachable
        for (int i = n - 1; i >= 0; i--) {
            for (int r = 0; r <= amount; r++) {
                dp[i][r] = dp[i + 1][r];
                if (coins[i] <= r) dp[i][r] += dp[i][r - coins[i]];
            }
        }
        return dp[0][amount];
    }

    /** Approach 4: one array, coins in the outer loop. O(n * amount) time, O(amount) space. */
    static long spaceOptimised(int[] coins, int amount) {
        long[] ways = new long[amount + 1];                      // ways[r]: combinations for r with the coins seen so far
        ways[0] = 1;
        for (int c : coins) {
            for (int r = c; r <= amount; r++) ways[r] += ways[r - c];   // ascending r lets c be reused
        }
        return ways[amount];
    }

    /** Blind 75 variant (LeetCode 322): fewest coins that make amount, or -1. O(n * amount) time, O(amount) space. */
    static int fewestCoins(int[] coins, int amount) {
        int inf = Integer.MAX_VALUE;
        int[] best = new int[amount + 1];
        Arrays.fill(best, inf);
        best[0] = 0;
        for (int r = 1; r <= amount; r++) {
            for (int c : coins) {
                if (c <= r && best[r - c] != inf) best[r] = Math.min(best[r], best[r - c] + 1);
            }
        }
        return best[amount] == inf ? -1 : best[amount];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] coins, int amount, long expected) {
        check(recursion(coins, amount) == expected, "recursion " + Arrays.toString(coins) + " " + amount);
        verifyFast(coins, amount, expected);
    }

    static void verifyFast(int[] coins, int amount, long expected) {
        String tag = Arrays.toString(coins) + " " + amount;
        check(memoization(coins, amount) == expected, "memoization " + tag);
        check(tabulation(coins, amount) == expected, "tabulation " + tag);
        check(spaceOptimised(coins, amount) == expected, "spaceOptimised " + tag);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 5}, 5, 4);
        verify(new int[]{2}, 3, 0);                              // unreachable amount
        verify(new int[]{10}, 10, 1);
        verify(new int[]{7}, 0, 1);                              // amount 0: the empty combination
        verify(new int[]{1, 2, 3}, 4, 4);
        verify(new int[]{2, 5, 3, 6}, 10, 5);
        verify(new int[]{5, 2, 1}, 5, 4);                        // order of coins does not matter
        verifyFast(new int[]{3, 5, 7, 8, 9, 10, 11}, 500, 35502874L);
        verifyFast(new int[]{1, 2, 5, 10, 20, 50, 100, 200}, 1000, 321335886L);

        check(fewestCoins(new int[]{1, 2, 5}, 11) == 3, "fewest 11");
        check(fewestCoins(new int[]{2}, 3) == -1, "fewest unreachable");
        check(fewestCoins(new int[]{1}, 0) == 0, "fewest zero");
        check(fewestCoins(new int[]{1, 3, 4}, 6) == 2, "fewest beats greedy");   // 3 + 3, greedy takes 4 + 1 + 1
        check(fewestCoins(new int[]{186, 419, 83, 408}, 6249) == 20, "fewest large");
        System.out.println("OK P316_CoinChangeII");
    }
}
