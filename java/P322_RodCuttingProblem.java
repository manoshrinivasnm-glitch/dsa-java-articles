import java.util.*;

/** TUF 322 - Rod cutting problem. price[i] is the value of a piece of length i + 1; cut a rod of length n for maximum value. */
public class P322_RodCuttingProblem {

    /** Approach 1: try every length for the first piece, recurse on the rest. O(2^n) time, O(n) stack. */
    static int recursion(int[] price) {
        return best(price.length, price);
    }

    static int best(int len, int[] price) {
        if (len == 0) return 0;                                  // nothing left to sell
        int result = 0;
        for (int k = 1; k <= len; k++) {                         // first piece has length k
            result = Math.max(result, price[k - 1] + best(len - k, price));
        }
        return result;
    }

    /** Approach 2: memoise best(len). O(n^2) time, O(n) space. */
    static int memoization(int[] price) {
        int[] dp = new int[price.length + 1];
        Arrays.fill(dp, -1);
        return memo(price.length, price, dp);
    }

    static int memo(int len, int[] price, int[] dp) {
        if (len == 0) return 0;
        if (dp[len] != -1) return dp[len];
        int result = 0;
        for (int k = 1; k <= len; k++) {
            result = Math.max(result, price[k - 1] + memo(len - k, price, dp));
        }
        return dp[len] = result;
    }

    /** Approach 3: fill best[len] for len = 1..n bottom-up. O(n^2) time, O(n) space. */
    static int tabulation(int[] price) {
        int n = price.length;
        int[] best = new int[n + 1];                             // best[len]: maximum value for a rod of length len
        for (int len = 1; len <= n; len++) {
            for (int k = 1; k <= len; k++) {
                best[len] = Math.max(best[len], price[k - 1] + best[len - k]);
            }
        }
        return best[n];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] price, int expected) {
        check(recursion(price) == expected, "recursion " + Arrays.toString(price));
        verifyFast(price, expected);
    }

    static void verifyFast(int[] price, int expected) {
        check(memoization(price) == expected, "memoization n=" + price.length);
        check(tabulation(price) == expected, "tabulation n=" + price.length);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 5, 8, 9, 10, 17, 17, 20}, 22);       // 2 + 6 -> 5 + 17
        verify(new int[]{3, 5, 8, 9, 10, 17, 17, 20}, 24);       // eight pieces of length 1
        verify(new int[]{2, 5, 7, 8}, 10);                       // 2 + 2
        verify(new int[]{1, 5, 8, 9}, 10);                       // best ratio (length 3) leads greedy to 8 + 1 = 9
        verify(new int[]{2, 5, 7, 8, 10}, 12);                   // 2 + 2 + 1
        verify(new int[]{5, 1, 1, 1}, 20);
        verify(new int[]{1}, 1);                                 // rod of length 1: nothing to cut
        verify(new int[]{0, 0, 0}, 0);                           // worthless pieces
        verify(new int[]{}, 0);                                  // empty rod
        int[] p = new int[1000];
        p[0] = 3;
        for (int i = 1; i < p.length; i++) p[i] = 2 * (i + 1);   // length 1 sells at 3 per unit, everything else at 2
        verifyFast(p, 3000);
        System.out.println("OK P322_RodCuttingProblem");
    }
}
