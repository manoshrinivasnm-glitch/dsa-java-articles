import java.util.*;

/** TUF 324 - Target sum (LeetCode 494). Count the ways to put + or - before every element of nums so the result equals target. */
public class P324_TargetSum {

    /** Approach 1: recursion that tries both signs for every element. O(2^n) time, O(n) stack. */
    static int recursive(int[] nums, int target) {
        return signs(0, 0, nums, target);
    }

    /** Ways to sign nums[i..] so that the final total is target, given the running total cur of nums[0..i-1]. */
    static int signs(int i, int cur, int[] nums, int target) {
        if (i == nums.length) return cur == target ? 1 : 0;
        return signs(i + 1, cur + nums[i], nums, target)            // +nums[i]
             + signs(i + 1, cur - nums[i], nums, target);           // -nums[i]
    }

    /** Approach 2: memoization on (i, running total), shifted by total so indices are non-negative. O(n * total) time and space. */
    static int memoization(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        int[][] dp = new int[nums.length][2 * total + 1];           // running total ranges over [-total, total]
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(0, 0, nums, target, total, dp);
    }

    static int memo(int i, int cur, int[] nums, int target, int total, int[][] dp) {
        if (i == nums.length) return cur == target ? 1 : 0;
        if (dp[i][cur + total] != -1) return dp[i][cur + total];
        int ways = memo(i + 1, cur + nums[i], nums, target, total, dp)
                 + memo(i + 1, cur - nums[i], nums, target, total, dp);
        return dp[i][cur + total] = ways;
    }

    /** Approach 3: reduce to counting subsets with sum (total - |target|) / 2, then tabulate. O(n * total) time and space. */
    static int tabulation(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        int t = Math.abs(target);                                    // flipping every sign maps target to -target
        if (t > total || (total - t) % 2 != 0) return 0;
        int neg = (total - t) / 2;                                   // the elements given a minus sign must sum to this
        int n = nums.length;
        int[][] dp = new int[n + 1][neg + 1];                        // dp[i][s]: subsets of the first i elements with sum s
        dp[0][0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int s = 0; s <= neg; s++) {
                dp[i][s] = dp[i - 1][s] + (nums[i - 1] <= s ? dp[i - 1][s - nums[i - 1]] : 0);
            }
        }
        return dp[n][neg];
    }

    /** Approach 4: the same count with one array updated from right to left. O(n * total) time, O(total) space. */
    static int spaceOptimized(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        int t = Math.abs(target);
        if (t > total || (total - t) % 2 != 0) return 0;
        int neg = (total - t) / 2;
        int[] ways = new int[neg + 1];
        ways[0] = 1;
        for (int x : nums) {
            for (int s = neg; s >= x; s--) ways[s] += ways[s - x];   // right to left: each element used at most once
        }
        return ways[neg];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int target, int expected) {
        String in = Arrays.toString(nums) + " target=" + target;
        check(recursive(nums, target) == expected, "recursive failed for " + in);
        check(memoization(nums, target) == expected, "memoization failed for " + in);
        check(tabulation(nums, target) == expected, "tabulation failed for " + in);
        check(spaceOptimized(nums, target) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 1, 1, 1}, 3, 5);                  // choose which one of the five 1s is negative
        verify(new int[]{1}, 1, 1);                              // edge: single element
        verify(new int[]{1}, 2, 0);                              // edge: target beyond the total
        verify(new int[]{1, 2, 3}, -2, 1);                       // negative target: -1 + 2 - 3
        verify(new int[]{1, 2, 7, 1}, 9, 2);                     // +1 +2 +7 -1 and -1 +2 +7 +1
        verify(new int[]{1, 2}, 2, 0);                           // parity: total 3 and target 2 differ in parity
        verify(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, 1, 256);    // each zero can take either sign
        verify(new int[]{0}, 0, 2);                              // edge: +0 and -0 are different expressions
        verify(new int[]{1000}, -1000, 1);                       // largest magnitudes

        // Cross-check on seeded random inputs, zeros and negative targets included.
        Random rnd = new Random(324);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(12);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(7);
            int target = rnd.nextInt(21) - 10;
            verify(nums, target, recursive(nums, target));
        }
        System.out.println("OK P324_TargetSum");
    }
}
