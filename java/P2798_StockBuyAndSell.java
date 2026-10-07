import java.util.*;

/** TUF 2798 - Stock Buy and Sell. Maximum profit from one buy followed by one later sell; 0 if no profit is possible. */
public class P2798_StockBuyAndSell {

    /** Approach 1: try every buy day with every later sell day. O(n^2) time, O(1) space. */
    static int bruteForce(int[] prices) {
        int best = 0;
        for (int i = 0; i < prices.length; i++) {
            for (int j = i + 1; j < prices.length; j++) {
                best = Math.max(best, prices[j] - prices[i]);
            }
        }
        return best;
    }

    /** Approach 2: precompute the highest price from each day onwards, then pair it with each buy day. O(n) time, O(n) space. */
    static int better(int[] prices) {
        int n = prices.length;
        if (n == 0) return 0;
        int[] maxRight = new int[n];          // maxRight[i] = highest price on day i or later
        maxRight[n - 1] = prices[n - 1];
        for (int i = n - 2; i >= 0; i--) maxRight[i] = Math.max(prices[i], maxRight[i + 1]);
        int best = 0;
        for (int i = 0; i < n; i++) best = Math.max(best, maxRight[i] - prices[i]);
        return best;
    }

    /** Approach 3: one pass, tracking the cheapest price seen so far. O(n) time, O(1) space. */
    static int optimal(int[] prices) {
        int minSoFar = Integer.MAX_VALUE, best = 0;
        for (int p : prices) {
            if (p < minSoFar) minSoFar = p;
            else best = Math.max(best, p - minSoFar);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] prices, int expected) {
        String in = Arrays.toString(prices.length > 20 ? Arrays.copyOf(prices, 20) : prices);
        check(bruteForce(prices) == expected, "bruteForce failed on " + in + " expected " + expected);
        check(better(prices) == expected, "better failed on " + in + " expected " + expected);
        check(optimal(prices) == expected, "optimal failed on " + in + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(new int[]{7, 1, 5, 3, 6, 4}, 5);                             // buy at 1, sell at 6
        verify(new int[]{7, 6, 4, 3, 1}, 0);                                // prices only fall
        verify(new int[]{1, 2}, 1);
        verify(new int[]{2, 4, 1}, 2);                                      // the global minimum comes too late to use
        verify(new int[]{5}, 0);                                            // single day: cannot sell
        verify(new int[]{}, 0);                                             // empty
        verify(new int[]{3, 3, 3}, 0);                                      // flat prices
        verify(new int[]{1, 2, 3, 4, 5}, 4);                                // strictly rising
        verify(new int[]{2, 1, 2, 1, 0, 1, 2}, 2);
        verify(new int[]{10_000, 0, 10_000}, 10_000);
        Random rnd = new Random(42);
        int[] big = new int[2000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(10_001);
        verify(big, bruteForce(big));                                       // 2000 random prices, fixed seed
        System.out.println("OK P2798_StockBuyAndSell");
    }
}
