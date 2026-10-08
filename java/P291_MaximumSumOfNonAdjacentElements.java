import java.util.*;

/** TUF 291 - Maximum sum of non adjacent elements (same as LeetCode 198 House Robber). Elements are non-negative. */
public class P291_MaximumSumOfNonAdjacentElements {

    /** Approach 1: pick / not-pick recursion from the last index. O(2^n) time, O(n) stack. */
    static long recursive(int[] nums) {
        return solve(nums.length - 1, nums);
    }

    /** Best sum using only nums[0..i]. */
    static long solve(int i, int[] nums) {
        if (i < 0) return 0;                                    // no elements left
        long pick = nums[i] + solve(i - 2, nums);               // take nums[i], so nums[i - 1] is off limits
        long skip = solve(i - 1, nums);                         // leave nums[i]
        return Math.max(pick, skip);
    }

    /** Approach 2: memoization. O(n) time, O(n) space for the table and the stack. */
    static long memoization(int[] nums) {
        long[] dp = new long[nums.length];
        Arrays.fill(dp, -1);                                    // -1 = not computed (real sums are >= 0)
        return memo(nums.length - 1, nums, dp);
    }

    static long memo(int i, int[] nums, long[] dp) {
        if (i < 0) return 0;
        if (dp[i] != -1) return dp[i];
        long pick = nums[i] + memo(i - 2, nums, dp);
        long skip = memo(i - 1, nums, dp);
        return dp[i] = Math.max(pick, skip);
    }

    /** Approach 3: tabulation, dp[i] = best sum using nums[0..i]. O(n) time, O(n) space. */
    static long tabulation(int[] nums) {
        int n = nums.length;
        if (n == 0) return 0;
        long[] dp = new long[n];
        for (int i = 0; i < n; i++) {
            long pick = nums[i] + (i >= 2 ? dp[i - 2] : 0);
            long skip = i >= 1 ? dp[i - 1] : 0;
            dp[i] = Math.max(pick, skip);
        }
        return dp[n - 1];
    }

    /** Approach 4: keep only dp[i - 1] and dp[i - 2]. O(n) time, O(1) space. */
    static long spaceOptimized(int[] nums) {
        long prev2 = 0, prev = 0;                               // dp[i - 2] and dp[i - 1]
        for (int x : nums) {
            long cur = Math.max(x + prev2, prev);
            prev2 = prev;
            prev = cur;
        }
        return prev;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, long expected) {
        String in = Arrays.toString(nums);
        check(recursive(nums) == expected, "recursive failed for " + in);
        check(memoization(nums) == expected, "memoization failed for " + in);
        check(tabulation(nums) == expected, "tabulation failed for " + in);
        check(spaceOptimized(nums) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 1, 4, 9}, 11);                      // 2 + 9
        verify(new int[]{1, 2, 4}, 5);                          // 1 + 4
        verify(new int[]{2, 7, 9, 3, 1}, 12);                   // 2 + 9 + 1
        verify(new int[]{1, 2, 3, 1}, 4);
        verify(new int[]{2, 1, 1, 2}, 4);                       // skip two in a row: 2 + 2
        verify(new int[]{5}, 5);                                // edge: one element
        verify(new int[]{}, 0);                                 // edge: empty array
        verify(new int[]{0, 0, 0}, 0);
        verify(new int[]{2_000_000_000, 1, 2_000_000_000}, 4_000_000_000L); // overflows int

        // Cross-check every approach on seeded random arrays (small enough for plain recursion).
        Random rnd = new Random(291);
        for (int t = 0; t < 200; t++) {
            int[] nums = new int[rnd.nextInt(18)];
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(50);
            verify(nums, spaceOptimized(nums));
        }
        System.out.println("OK P291_MaximumSumOfNonAdjacentElements");
    }
}
