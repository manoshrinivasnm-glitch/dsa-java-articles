import java.util.*;

/** TUF 2803 - Rod Cutting Problem (DP 24). price[i] is the value of a piece of length i + 1; cut a rod of length price.length to maximise the total value. */
public class P2803_RodCuttingProblemDP24 {

    /** Approach 1: recursion over piece lengths with take / not take. Exponential time, O(n) stack. */
    static int recursive(int[] price) {
        int n = price.length;
        return solve(n - 1, n, price);  // last index = every piece length allowed, full rod length n
    }

    /** Best value for a rod of length len using only piece lengths 1 .. ind + 1. */
    static int solve(int ind, int len, int[] price) {
        if (ind == 0) return len * price[0];       // only length-1 pieces remain: cut len of them
        int notTake = solve(ind - 1, len, price);  // skip pieces of length ind + 1
        int take = Integer.MIN_VALUE;              // stays -inf if the piece does not fit
        int pieceLen = ind + 1;                    // length of the piece at index ind
        if (pieceLen <= len) take = price[ind] + solve(ind, len - pieceLen, price);  // stay at ind: unlimited supply
        return Math.max(take, notTake);            // best of cutting this piece or skipping it
    }

    /** Approach 2: memoization on (ind, len). O(n^2) time, O(n^2) space plus the stack. */
    static int memoization(int[] price) {
        int n = price.length;
        int[][] dp = new int[n][n + 1];             // dp[ind][len] caches solve(ind, len)
        for (int[] row : dp) Arrays.fill(row, -1);  // -1 marks a state not solved yet
        return memo(n - 1, n, price, dp);           // start with every piece length and the full rod
    }

    static int memo(int ind, int len, int[] price, int[][] dp) {
        if (ind == 0) return len * price[0];            // base case: fill the rod with length-1 pieces
        if (dp[ind][len] != -1) return dp[ind][len];    // already solved: reuse the stored answer
        int notTake = memo(ind - 1, len, price, dp);    // skip this piece length
        int take = Integer.MIN_VALUE;                   // stays -inf if the piece does not fit
        int pieceLen = ind + 1;
        // cut one piece and stay at ind: unlimited supply
        if (pieceLen <= len) take = price[ind] + memo(ind, len - pieceLen, price, dp);
        return dp[ind][len] = Math.max(take, notTake);  // store the answer before returning it
    }

    /** Approach 3: tabulation, piece lengths in the outer loop. O(n^2) time, O(n^2) space. */
    static int tabulation(int[] price) {
        int n = price.length;
        int[][] dp = new int[n][n + 1];           // dp[ind][len] = best using lengths 1 .. ind + 1
        for (int len = 0; len <= n; len++) dp[0][len] = len * price[0];  // base case: only length-1 pieces
        for (int ind = 1; ind < n; ind++) {       // allow one more piece length per row
            int pieceLen = ind + 1;
            for (int len = 0; len <= n; len++) {  // every rod length 0 .. n
                int notTake = dp[ind - 1][len];   // best value without this piece length
                int take = Integer.MIN_VALUE;
                // same row: the piece can be cut again
                if (pieceLen <= len) take = price[ind] + dp[ind][len - pieceLen];
                dp[ind][len] = Math.max(take, notTake);
            }
        }
        return dp[n - 1][n];                      // full rod, every piece length allowed
    }

    /** Approach 4: a single array updated left to right in place. O(n^2) time, O(n) space. */
    static int spaceOptimized(int[] price) {
        int n = price.length;
        int[] best = new int[n + 1];                     // best[len] = best value for rod length len
        for (int len = 0; len <= n; len++) best[len] = len * price[0];  // row 0: only length-1 pieces
        for (int ind = 1; ind < n; ind++) {              // allow one more piece length per pass
            int pieceLen = ind + 1;
            for (int len = pieceLen; len <= n; len++) {  // only rods long enough to hold this piece
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
