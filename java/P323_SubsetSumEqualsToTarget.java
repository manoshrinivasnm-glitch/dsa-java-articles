import java.util.*;

/** TUF 323 - Subset sum equals to target. Decide whether some subset of the non-negative array sums to target. */
public class P323_SubsetSumEqualsToTarget {

    /** Approach 1: take / skip recursion from the last element. O(2^n) time, O(n) stack. */
    static boolean recursion(int[] arr, int target) {
        return canMake(arr.length - 1, target, arr);
    }

    static boolean canMake(int i, int t, int[] arr) {
        if (t == 0) return true;                                   // the subset chosen so far already works
        if (i < 0) return false;                                   // no elements left, t still positive
        if (canMake(i - 1, t, arr)) return true;                   // skip arr[i]
        return arr[i] <= t && canMake(i - 1, t - arr[i], arr);     // take arr[i]
    }

    /** Approach 2: memoised recursion over (i, t). O(n * target) time, O(n * target) space plus O(n) stack. */
    static boolean memoization(int[] arr, int target) {
        int[][] dp = new int[arr.length][target + 1];              // 0 = unknown, 1 = false, 2 = true
        return memo(arr.length - 1, target, arr, dp);
    }

    static boolean memo(int i, int t, int[] arr, int[][] dp) {
        if (t == 0) return true;
        if (i < 0) return false;
        if (dp[i][t] != 0) return dp[i][t] == 2;
        boolean ok = memo(i - 1, t, arr, dp) || (arr[i] <= t && memo(i - 1, t - arr[i], arr, dp));
        dp[i][t] = ok ? 2 : 1;
        return ok;
    }

    /** Approach 3: bottom-up boolean table. O(n * target) time, O(n * target) space. */
    static boolean tabulation(int[] arr, int target) {
        int n = arr.length;
        boolean[][] dp = new boolean[n + 1][target + 1];           // dp[i][t]: some subset of the first i elements sums to t
        for (int i = 0; i <= n; i++) dp[i][0] = true;              // the empty subset makes 0
        for (int i = 1; i <= n; i++) {
            int x = arr[i - 1];
            for (int t = 1; t <= target; t++) {
                dp[i][t] = dp[i - 1][t] || (x <= t && dp[i - 1][t - x]);
            }
        }
        return dp[n][target];
    }

    /** Approach 4: one boolean row updated from high t to low t. O(n * target) time, O(target) space. */
    static boolean spaceOptimised(int[] arr, int target) {
        boolean[] can = new boolean[target + 1];                   // can[t]: t is a sum of a subset of the elements seen so far
        can[0] = true;
        for (int x : arr) {
            for (int t = target; t >= x; t--) {                    // descending: x is used at most once per subset
                if (can[t - x]) can[t] = true;
            }
        }
        return can[target];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int target, boolean expected) {
        check(recursion(arr, target) == expected, "recursion " + Arrays.toString(arr) + " " + target);
        verifyFast(arr, target, expected);
    }

    static void verifyFast(int[] arr, int target, boolean expected) {
        String tag = arr.length + " elements, target " + target;
        check(memoization(arr, target) == expected, "memoization " + tag);
        check(tabulation(arr, target) == expected, "tabulation " + tag);
        check(spaceOptimised(arr, target) == expected, "spaceOptimised " + tag);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4}, 4, true);
        verify(new int[]{4, 3, 2, 1}, 5, true);
        verify(new int[]{2, 5, 1, 6, 7}, 4, false);
        verify(new int[]{6, 1, 2, 1}, 4, true);                    // 1 + 2 + 1, duplicates are separate elements
        verify(new int[]{3, 34, 4, 12, 5, 2}, 9, true);
        verify(new int[]{3, 34, 4, 12, 5, 2}, 30, false);
        verify(new int[]{5}, 5, true);                             // single element equal to target
        verify(new int[]{5}, 3, false);
        verify(new int[]{}, 0, true);                              // empty array, target 0: the empty subset
        verify(new int[]{}, 1, false);
        verify(new int[]{0, 0, 7}, 7, true);                       // zeros never hurt
        verify(new int[]{100, 200}, 50, false);                    // every element larger than target
        int[] evens = new int[60];
        for (int i = 0; i < evens.length; i++) evens[i] = 2 * (i + 1);
        verifyFast(evens, 999, false);                             // all even, odd target: 2^60 subsets, none work
        verifyFast(evens, 1000, true);
        System.out.println("OK P323_SubsetSumEqualsToTarget");
    }
}
