import java.util.*;

/** TUF 292 - Ninja's Training. points[day][task] for 3 tasks; the same task cannot be done on two consecutive days. Maximise the total. */
public class P292_NinjasTraining {

    static final int NONE = 3;                                  // "no task is forbidden" (used for the last day)

    /** Approach 1: recursion over (day, task done on the following day). O(2^n) time, O(n) stack. */
    static int recursive(int[][] points) {
        return solve(points.length - 1, NONE, points);
    }

    /** Best total for days 0..day, given that task `last` is forbidden on `day`. */
    static int solve(int day, int last, int[][] points) {
        if (day < 0) return 0;                                  // no days left
        int best = 0;
        for (int task = 0; task < 3; task++) {
            if (task == last) continue;                         // same task as the day after: not allowed
            best = Math.max(best, points[day][task] + solve(day - 1, task, points));
        }
        return best;
    }

    /** Approach 2: memoization on the n x 4 states. O(n * 4 * 3) time, O(n * 4) space plus the stack. */
    static int memoization(int[][] points) {
        int n = points.length;
        int[][] dp = new int[n][4];
        for (int[] row : dp) Arrays.fill(row, -1);              // -1 = not computed (totals are >= 0)
        return memo(n - 1, NONE, points, dp);
    }

    static int memo(int day, int last, int[][] points, int[][] dp) {
        if (day < 0) return 0;
        if (dp[day][last] != -1) return dp[day][last];
        int best = 0;
        for (int task = 0; task < 3; task++) {
            if (task == last) continue;
            best = Math.max(best, points[day][task] + memo(day - 1, task, points, dp));
        }
        return dp[day][last] = best;
    }

    /** Approach 3: tabulation, dp[day][last] filled from day 0 forward. O(n * 4 * 3) time, O(n * 4) space. */
    static int tabulation(int[][] points) {
        int n = points.length;
        if (n == 0) return 0;
        int[][] dp = new int[n][4];
        for (int last = 0; last < 4; last++) {                  // day 0: best single task other than `last`
            for (int task = 0; task < 3; task++) {
                if (task != last) dp[0][last] = Math.max(dp[0][last], points[0][task]);
            }
        }
        for (int day = 1; day < n; day++) {
            for (int last = 0; last < 4; last++) {
                for (int task = 0; task < 3; task++) {
                    if (task != last) dp[day][last] = Math.max(dp[day][last], points[day][task] + dp[day - 1][task]);
                }
            }
        }
        return dp[n - 1][NONE];
    }

    /** Approach 4: day `day` reads only day `day - 1`, so keep one row of 4 values. O(n * 4 * 3) time, O(1) space. */
    static int spaceOptimized(int[][] points) {
        int[] prev = new int[4];                                // prev[last] = best total for the days so far (0 before day 0)
        for (int[] today : points) {
            int[] cur = new int[4];
            for (int last = 0; last < 4; last++) {
                for (int task = 0; task < 3; task++) {
                    if (task != last) cur[last] = Math.max(cur[last], today[task] + prev[task]);
                }
            }
            prev = cur;
        }
        return prev[NONE];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] points, int expected) {
        String in = Arrays.deepToString(points);
        check(recursive(points) == expected, "recursive failed for " + in);
        check(memoization(points) == expected, "memoization failed for " + in);
        check(tabulation(points) == expected, "tabulation failed for " + in);
        check(spaceOptimized(points) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2, 5}, {3, 1, 1}, {3, 3, 3}}, 11);           // 5 + 3 + 3
        verify(new int[][]{{10, 40, 70}, {20, 50, 80}, {30, 60, 90}}, 210); // 70 + 50 + 90
        verify(new int[][]{{18, 11, 19}, {4, 13, 7}, {1, 8, 13}}, 45);      // 19 + 13 + 13
        verify(new int[][]{{10, 1, 1}, {10, 1, 1}, {10, 1, 1}}, 21);        // greedy 10 every day is illegal
        verify(new int[][]{{10, 50, 1}, {5, 100, 11}}, 110);                // greedy takes 50 first and ends at 61
        verify(new int[][]{{5, 9, 2}}, 9);                                  // edge: one day
        verify(new int[][]{}, 0);                                           // edge: no days

        // Cross-check every approach on seeded random schedules (small enough for plain recursion).
        Random rnd = new Random(292);
        for (int t = 0; t < 200; t++) {
            int[][] points = new int[1 + rnd.nextInt(10)][3];
            for (int[] row : points) for (int j = 0; j < 3; j++) row[j] = rnd.nextInt(100);
            verify(points, spaceOptimized(points));
        }
        System.out.println("OK P292_NinjasTraining");
    }
}
