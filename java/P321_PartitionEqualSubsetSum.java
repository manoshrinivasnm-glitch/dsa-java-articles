import java.util.*;

/** TUF 321 - Partition equal subset sum (LeetCode 416). Can nums be split into two subsets with equal sums? */
public class P321_PartitionEqualSubsetSum {

    /** Approach 1: reduce to "is there a subset with sum total / 2" and solve it by pick / not-pick recursion. O(2^n) time. */
    static boolean recursive(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;                 // sum of every element
        if (total % 2 != 0) return false;              // an odd total cannot be split into two equal integers
        return canReach(nums.length - 1, total / 2, nums);   // one side reaching half fixes the other side too
    }

    /** Can some subset of nums[0..i] sum to exactly t? */
    static boolean canReach(int i, int t, int[] nums) {
        if (t == 0) return true;                       // the empty subset already works
        if (i == 0) return nums[0] == t;               // one element left: it must equal t
        boolean notTake = canReach(i - 1, t, nums);    // leave nums[i] out
        boolean take = nums[i] <= t && canReach(i - 1, t - nums[i], nums);   // use nums[i] if it fits
        return notTake || take;
    }

    /** Approach 2: memoization on (i, t). O(n * sum) time, O(n * sum) space plus the stack. */
    static boolean memoization(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;                 // sum of every element
        if (total % 2 != 0) return false;              // an odd total cannot be split evenly
        int half = total / 2;                          // the sum one subset must hit
        int[][] dp = new int[nums.length][half + 1];   // -1 = unknown, 0 = false, 1 = true
        for (int[] row : dp) Arrays.fill(row, -1);     // nothing computed yet
        return memo(nums.length - 1, half, nums, dp);
    }

    static boolean memo(int i, int t, int[] nums, int[][] dp) {
        if (t == 0) return true;                       // the empty subset already works
        if (i == 0) return nums[0] == t;               // one element left: it must equal t
        if (dp[i][t] != -1) return dp[i][t] == 1;      // state solved before: reuse it
        // skip nums[i], or take it when it fits
        boolean res = memo(i - 1, t, nums, dp) || (nums[i] <= t && memo(i - 1, t - nums[i], nums, dp));
        dp[i][t] = res ? 1 : 0;                        // remember the answer for (i, t)
        return res;
    }

    /** Approach 3: tabulation over prefixes and targets 0..half. O(n * sum) time and space. */
    static boolean tabulation(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;                 // sum of every element
        if (total % 2 != 0) return false;              // an odd total cannot be split evenly
        int n = nums.length, half = total / 2;
        boolean[][] dp = new boolean[n][half + 1];     // dp[i][t]: some subset of nums[0..i] sums to t
        for (int i = 0; i < n; i++) dp[i][0] = true;   // target 0: take nothing
        if (nums[0] <= half) dp[0][nums[0]] = true;    // row 0: only nums[0] itself is reachable
        for (int i = 1; i < n; i++) {
            for (int t = 1; t <= half; t++) {
                dp[i][t] = dp[i - 1][t] || (nums[i] <= t && dp[i - 1][t - nums[i]]);   // skip or take nums[i]
            }
        }
        return dp[n - 1][half];                        // can all of nums reach half?
    }

    /** Approach 4: one boolean array updated from right to left. O(n * sum) time, O(sum) space. */
    static boolean spaceOptimized(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;                 // sum of every element
        if (total % 2 != 0) return false;              // an odd total cannot be split evenly
        int half = total / 2;                          // the sum one subset must hit
        boolean[] reach = new boolean[half + 1];       // reach[t]: some subset of the elements seen so far sums to t
        reach[0] = true;                               // the empty subset sums to 0
        for (int x : nums) {                           // add one element at a time
            for (int t = half; t >= x; t--) {          // right to left: reach[t - x] is still the old value
                if (reach[t - x]) reach[t] = true;     // t - x was reachable, so adding x reaches t
            }
            if (reach[half]) return true;              // early exit once the half is reachable
        }
        return reach[half];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, boolean expected) {
        String in = Arrays.toString(nums);
        check(recursive(nums) == expected, "recursive failed for " + in);
        check(memoization(nums) == expected, "memoization failed for " + in);
        check(tabulation(nums) == expected, "tabulation failed for " + in);
        check(spaceOptimized(nums) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 5, 11, 5}, true);             // {1, 5, 5} and {11}
        verify(new int[]{1, 2, 3, 5}, false);             // total 11 is odd
        verify(new int[]{2, 3, 3, 3, 4, 5}, true);        // total 20: {2, 3, 5} and {3, 3, 4}
        verify(new int[]{1, 2, 5}, false);                // total 8 is even but no subset makes 4
        verify(new int[]{7}, false);                      // edge: a single element can never be split
        verify(new int[]{4, 4}, true);                    // edge: two equal elements
        verify(new int[]{100, 1, 1, 1, 1}, false);        // one element exceeds half: hopeless
        verify(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 100}, false);   // even total 118, but 100 > 59

        // Cross-check on seeded random arrays against a bitmask enumeration.
        Random rnd = new Random(321);
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(12);
            int[] nums = new int[n];
            int total = 0;
            for (int i = 0; i < n; i++) { nums[i] = 1 + rnd.nextInt(12); total += nums[i]; }
            boolean brute = false;
            for (int mask = 0; mask < (1 << n) && !brute; mask++) {
                int s = 0;
                for (int i = 0; i < n; i++) if ((mask >> i & 1) == 1) s += nums[i];
                brute = 2 * s == total;
            }
            verify(nums, brute);
        }
        System.out.println("OK P321_PartitionEqualSubsetSum");
    }
}
