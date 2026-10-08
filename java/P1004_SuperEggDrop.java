import java.util.*;

/** TUF 1004 - Super Egg Drop. Fewest drops that always find the critical floor with k eggs and n floors. */
public class P1004_SuperEggDrop {

    /** Approach 1: try every floor for the next drop, take the worse outcome. Exponential time, O(n) stack. */
    static int recursion(int k, int n) {
        if (n == 0 || n == 1) return n;                      // 0 floors: nothing to test; 1 floor: one drop
        if (k == 1) return n;                                // one egg: floor by floor from the bottom
        int best = Integer.MAX_VALUE;
        for (int x = 1; x <= n; x++) {
            int breaks = recursion(k - 1, x - 1);            // answer is below x, one egg fewer
            int survives = recursion(k, n - x);              // answer is above x, same eggs
            best = Math.min(best, 1 + Math.max(breaks, survives));
        }
        return best;
    }

    /** Approach 2: memoise (eggs, floors). O(k * n^2) time, O(k * n) space. */
    static int memoization(int k, int n) {
        int[][] dp = new int[k + 1][n + 1];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(k, n, dp);
    }

    static int memo(int k, int n, int[][] dp) {
        if (n == 0 || n == 1) return n;
        if (k == 1) return n;
        if (dp[k][n] != -1) return dp[k][n];
        int best = Integer.MAX_VALUE;
        for (int x = 1; x <= n; x++) {
            best = Math.min(best, 1 + Math.max(memo(k - 1, x - 1, dp), memo(k, n - x, dp)));
        }
        return dp[k][n] = best;
    }

    /** Approach 3: memoisation with a binary search for the best floor. O(k * n * log n) time, O(k * n) space. */
    static int memoBinarySearch(int k, int n) {
        int[][] dp = new int[k + 1][n + 1];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memoBs(k, n, dp);
    }

    static int memoBs(int k, int n, int[][] dp) {
        if (n == 0 || n == 1) return n;
        if (k == 1) return n;
        if (dp[k][n] != -1) return dp[k][n];
        // breaks(x) = memoBs(k-1, x-1) rises with x, survives(x) = memoBs(k, n-x) falls with x:
        // the best x sits where the two curves cross, so binary search for the crossing.
        int lo = 1, hi = n;
        while (lo + 1 < hi) {
            int mid = (lo + hi) >>> 1;
            int breaks = memoBs(k - 1, mid - 1, dp);
            int survives = memoBs(k, n - mid, dp);
            if (breaks < survives) lo = mid;
            else if (breaks > survives) hi = mid;
            else lo = hi = mid;
        }
        int best = Integer.MAX_VALUE;
        for (int x : new int[]{lo, hi}) {
            best = Math.min(best, 1 + Math.max(memoBs(k - 1, x - 1, dp), memoBs(k, n - x, dp)));
        }
        return dp[k][n] = best;
    }

    /** Approach 4: flip the question - how many floors can m moves and e eggs cover? O(k * answer) time, O(k) space. */
    static int optimal(int k, int n) {
        int[] floors = new int[k + 1];                       // floors[e]: floors fully resolved with e eggs and `moves` drops
        int moves = 0;
        while (floors[k] < n) {
            moves++;
            for (int e = k; e >= 1; e--) {                   // descending so floors[e - 1] is still the previous move's value
                floors[e] = floors[e - 1] + floors[e] + 1;   // floors below (egg broke) + floors above (survived) + this floor
            }
        }
        return moves;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int k, int n, int expected) {
        check(recursion(k, n) == expected, "recursion k=" + k + " n=" + n);
        verifyMemo(k, n, expected);
    }

    static void verifyMemo(int k, int n, int expected) {
        check(memoization(k, n) == expected, "memoization k=" + k + " n=" + n);
        verifyFast(k, n, expected);
    }

    static void verifyFast(int k, int n, int expected) {
        check(memoBinarySearch(k, n) == expected, "memoBinarySearch k=" + k + " n=" + n);
        check(optimal(k, n) == expected, "optimal k=" + k + " n=" + n);
    }

    public static void main(String[] args) {
        verify(1, 2, 2);
        verify(2, 6, 3);
        verify(3, 14, 4);
        verify(2, 10, 4);
        verify(1, 1, 1);                                     // single floor
        verify(1, 7, 7);                                     // one egg: linear scan
        verify(3, 0, 0);                                     // no floors at all
        verify(5, 15, 4);                                    // more eggs than needed: 2^4 - 1 = 15 floors
        verifyMemo(2, 100, 14);                              // the classic two-egg, hundred-floor puzzle
        verifyMemo(3, 1000, 19);
        verifyFast(4, 5000, 19);
        verifyFast(100, 10000, 14);                          // eggs to spare: plain binary search, ceil(log2(10001))
        System.out.println("OK P1004_SuperEggDrop");
    }
}
