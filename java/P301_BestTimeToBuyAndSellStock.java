import java.util.*;

/** TUF 301 - Best time to buy and sell stock. Buy on one day, sell on a later day; return the maximum profit, or 0 if no profit is possible. */
public class P301_BestTimeToBuyAndSellStock {

    /** Approach 1: try every (buy day, sell day) pair. O(n^2) time, O(1) space. */
    static int bruteForce(int[] prices) {
        int best = 0;
        for (int buy = 0; buy < prices.length; buy++) {
            for (int sell = buy + 1; sell < prices.length; sell++) {
                best = Math.max(best, prices[sell] - prices[buy]);
            }
        }
        return best;
    }

    /** Approach 2: precompute the highest price from every day to the end. O(n) time, O(n) space. */
    static int better(int[] prices) {
        int n = prices.length;
        if (n == 0) return 0;
        int[] maxFrom = new int[n];                         // maxFrom[i] = max(prices[i..n-1])
        maxFrom[n - 1] = prices[n - 1];
        for (int i = n - 2; i >= 0; i--) maxFrom[i] = Math.max(prices[i], maxFrom[i + 1]);
        int best = 0;
        for (int i = 0; i < n; i++) best = Math.max(best, maxFrom[i] - prices[i]);
        return best;
    }

    /** Approach 3: one pass, remembering the cheapest price seen so far. O(n) time, O(1) space. */
    static int optimal(int[] prices) {
        int minSoFar = Integer.MAX_VALUE, best = 0;
        for (int price : prices) {
            minSoFar = Math.min(minSoFar, price);           // cheapest buy on or before today
            best = Math.max(best, price - minSoFar);        // sell today
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] prices, int expected) {
        String in = Arrays.toString(prices);
        check(bruteForce(prices) == expected, "bruteForce failed for " + in);
        check(better(prices) == expected, "better failed for " + in);
        check(optimal(prices) == expected, "optimal failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{7, 1, 5, 3, 6, 4}, 5);                 // buy at 1, sell at 6
        verify(new int[]{7, 6, 4, 3, 1}, 0);                    // falling prices: do not trade
        verify(new int[]{2, 4, 1}, 2);                          // the later minimum 1 has nothing after it
        verify(new int[]{3, 8, 1, 6}, 5);                       // two pairs tie at 5
        verify(new int[]{1, 2, 3, 4, 5}, 4);                    // one transaction only: 5 - 1
        verify(new int[]{5, 5, 5}, 0);                          // flat prices
        verify(new int[]{9}, 0);                                // edge: one day, cannot sell later
        verify(new int[]{}, 0);                                 // edge: no days

        Random rnd = new Random(301);
        for (int t = 0; t < 300; t++) {
            int[] p = new int[rnd.nextInt(15)];
            for (int i = 0; i < p.length; i++) p[i] = rnd.nextInt(50);
            verify(p, bruteForce(p));
        }
        System.out.println("OK P301_BestTimeToBuyAndSellStock");
    }
}
