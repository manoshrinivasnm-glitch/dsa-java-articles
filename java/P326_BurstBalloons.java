import java.util.*;

/** TUF 326 - Burst balloons. Bursting i earns left * nums[i] * right with its current neighbours; maximise coins. */
public class P326_BurstBalloons {

    /** Approach 1: try every bursting order. O(n!) time, O(n) space. */
    static int bruteForce(int[] nums) {
        List<Integer> list = new ArrayList<>();                 // the balloons still in the row
        for (int x : nums) list.add(x);
        return tryAll(list);
    }

    static int tryAll(List<Integer> list) {
        int best = 0;                                           // most coins from the balloons left in list
        for (int i = 0; i < list.size(); i++) {                 // try bursting each remaining balloon next
            int left = i > 0 ? list.get(i - 1) : 1;             // missing neighbour counts as 1
            int right = i < list.size() - 1 ? list.get(i + 1) : 1;   // missing neighbour counts as 1
            int x = list.remove(i);                             // burst it: its neighbours now touch
            best = Math.max(best, left * x * right + tryAll(list));   // coins now, plus the best for what is left
            list.add(i, x);                                     // undo before trying the next balloon
        }
        return best;
    }

    /** Pads the array with a 1 on each side: a[0] = a[n + 1] = 1. */
    static int[] padded(int[] nums) {
        int[] a = new int[nums.length + 2];
        a[0] = 1;                                               // left wall
        a[a.length - 1] = 1;                                    // right wall
        System.arraycopy(nums, 0, a, 1, nums.length);           // real balloons sit at 1 .. n
        return a;
    }

    /** Approach 2: choose the balloon that bursts LAST between two fixed walls. Exponential time, O(n) stack. */
    static int recursive(int[] nums) {
        int[] a = padded(nums);
        return solve(0, a.length - 1, a);                       // walls 0 and n + 1 are never burst
    }

    static int solve(int i, int j, int[] a) {
        if (j - i < 2) return 0;                                // no balloon strictly between i and j
        int best = 0;                                           // most coins from bursting everything inside
        for (int k = i + 1; k < j; k++) {                       // k is the last balloon left between i and j
            // k bursts after both sides are cleared, so its neighbours are the walls a[i] and a[j]
            best = Math.max(best, solve(i, k, a) + solve(k, j, a) + a[i] * a[k] * a[j]);
        }
        return best;
    }

    /** Approach 3: cache each (i, j) window. O(n^3) time, O(n^2) space. */
    static int memoization(int[] nums) {
        int[] a = padded(nums);
        int[][] dp = new int[a.length][a.length];               // dp[i][j] = best coins between walls i and j
        for (int[] row : dp) Arrays.fill(row, -1);              // -1 marks a window not solved yet
        return memo(0, a.length - 1, a, dp);                    // everything between the two padded walls
    }

    static int memo(int i, int j, int[] a, int[][] dp) {
        if (j - i < 2) return 0;                                // no balloon strictly between i and j
        if (dp[i][j] != -1) return dp[i][j];                    // already solved: reuse it
        int best = 0;
        for (int k = i + 1; k < j; k++) {                       // k is the last balloon left between i and j
            // both sides first, then k with walls a[i] and a[j] as neighbours
            best = Math.max(best, memo(i, k, a, dp) + memo(k, j, a, dp) + a[i] * a[k] * a[j]);
        }
        return dp[i][j] = best;                                 // store the answer before returning it
    }

    /** Approach 4: bottom-up, left wall moving from right to left. O(n^3) time, O(n^2) space. */
    static int tabulation(int[] nums) {
        int[] a = padded(nums);
        int m = a.length;
        int[][] dp = new int[m][m];                             // dp[i][i + 1] = 0: empty window
        for (int i = m - 3; i >= 0; i--) {                      // i goes down, so later windows are ready
            for (int j = i + 2; j < m; j++) {                   // at least one balloon between i and j
                int best = 0;
                // k bursts last; both side windows are shorter, so already filled
                for (int k = i + 1; k < j; k++) best = Math.max(best, dp[i][k] + dp[k][j] + a[i] * a[k] * a[j]);
                dp[i][j] = best;                                // best coins strictly between walls i and j
            }
        }
        return dp[0][m - 1];                                    // everything between the two padded walls
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected, boolean small) {
        String in = Arrays.toString(nums);
        if (small) {
            check(bruteForce(nums) == expected, "bruteForce failed for " + in);
            check(recursive(nums) == expected, "recursive failed for " + in);
        }
        check(memoization(nums) == expected, "memoization failed for " + in);
        check(tabulation(nums) == expected, "tabulation failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 1, 5, 8}, 167, true);               // burst 1, 5, 3, 8: 15 + 120 + 24 + 8
        verify(new int[]{1, 5}, 10, true);                      // burst 1 (5), then 5 (5)
        verify(new int[]{7}, 7, true);                          // edge: a single balloon
        verify(new int[]{}, 0, true);                           // edge: no balloons
        verify(new int[]{0, 0, 0}, 0, true);                    // zeros earn nothing
        verify(new int[]{2, 0, 3}, 9, true);                    // pop 0 (0), then 2 (1 * 2 * 3), then 3 (3)
        int[] many = new int[300];                              // large input: only the polynomial methods
        Arrays.fill(many, 100);
        check(memoization(many) == tabulation(many), "300 balloons");

        Random rnd = new Random(326);
        for (int t = 0; t < 150; t++) {
            int[] nums = new int[rnd.nextInt(8)];
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(10);
            verify(nums, bruteForce(nums), true);
        }
        System.out.println("OK P326_BurstBalloons");
    }
}
