import java.util.*;

/** TUF 2803 - Rod Cutting Problem (DP 24). price[i] is the value of a piece of length i + 1; cut a rod of length price.length to maximise the total value. */
public class P2803_RodCuttingProblemDP24 {

    /** Approach 1: recursion over piece lengths with take / not take. Exponential time, O(n) stack. */
    static int recursive(int[] price) {
        int n = price.length;
        return solve(n - 1, n, price);
    }

    /** Best value for a rod of length len using only piece lengths 1 .. ind + 1. */
    static int solve(int ind, int len, int[] price) {
        if (ind == 0) return len * price[0];                 // only length-1 pieces remain: cut len of them
        int notTake = solve(ind - 1, len, price);
        int take = Integer.MIN_VALUE;
        int pieceLen = ind + 1;
        if (pieceLen <= len) take = price[ind] + solve(ind, len - pieceLen, price);  // stay at ind: unlimited supply
        return Math.max(take, notTake);
    }

    /** Approach 2: memoization on (ind, len). O(n^2) time, O(n^2) space plus the stack. */
    static int memoization(int[] price) {
        int n = price.length;
        int[][] dp = new int[n][n + 1];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(n - 1, n, price, dp);
    }

    static int memo(int ind, int len, int[] price, int[][] dp) {
        if (ind == 0) return len * price[0];
        if (dp[ind][len] != -1) return dp[ind][len];
        int notTake = memo(ind - 1, len, price, dp);
        int take = Integer.MIN_VALUE;
        int pieceLen = ind + 1;
        if (pieceLen <= len) take = price[ind] + memo(ind, len - pieceLen, price, dp);
        return dp[ind][len] = Math.max(take, notTake);
    }

    /** Approach 3: tabulation, piece lengths in the outer loop. O(n^2) time, O(n^2) space. */
    static int tabulation(int[] price) {
        int n = price.length;
        int[][] dp = new int[n][n + 1];
        for (int len = 0; len <= n; len++) dp[0][len] = len * price[0];   // base case: only length-1 pieces
        for (int ind = 1; ind < n; ind++) {
            int pieceLen = ind + 1;
            for (int len = 0; len <= n; len++) {
                int notTake = dp[ind - 1][len];
                int take = Integer.MIN_VALUE;
                if (pieceLen <= len) take = price[ind] + dp[ind][len - pieceLen];
                dp[ind][len] = Math.max(take, notTake);
            }
        }
        return dp[n - 1][n];
    }

    /** Approach 4: a single array updated left to right in place. O(n^2) time, O(n) space. */
    static int spaceOptimized(int[] price) {
        int n = price.length;
        int[] best = new int[n + 1];
        for (int len = 0; len <= n; len++) best[len] = len * price[0];
        for (int ind = 1; ind < n; ind++) {
            int pieceLen = ind + 1;
            for (int len = pieceLen; len <= n; len++) {
                best[len] = Math.max(best[len], price[ind] + best[len - pieceLen]);  // best[len - pieceLen] is already this row
            }
        }
        return best[n];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] price, int expected) {
        String in = Arrays.toString(price);
        check(recursive(price) == expected, "recursive failed for " + in);
        check(memoization(price) == expected, "memoization failed for " + in);
        check(tabulation(price) == expected, "tabulation failed for " + in);
        check(spaceOptimized(price) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 5, 7, 8, 10}, 12);                    // 2 + 2 + 1 -> 5 + 5 + 2
        verify(new int[]{1, 5, 8, 9, 10, 17, 17, 20}, 22);        // 2 + 6 -> 5 + 17
        verify(new int[]{3, 5, 8, 9, 10, 17, 17, 20}, 24);        // eight pieces of length 1
        verify(new int[]{1, 1, 1, 10}, 10);                       // keep the rod whole
        verify(new int[]{1, 10, 1, 1, 1}, 21);                    // 2 + 2 + 1 -> 10 + 10 + 1
        verify(new int[]{0, 5, 8, 0}, 10);                        // best price per unit (length 3) gives only 8 + 0
        verify(new int[]{7}, 7);                                  // edge: rod of length 1
        verify(new int[]{0, 0, 0}, 0);                            // all prices zero

        // Cross-check every approach on seeded random price lists (small enough for plain recursion).
        Random rnd = new Random(2803);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(12);
            int[] price = new int[n];
            for (int i = 0; i < n; i++) price[i] = rnd.nextInt(30);
            verify(price, tabulation(price));
        }
        System.out.println("OK P2803_RodCuttingProblemDP24");
    }
}
