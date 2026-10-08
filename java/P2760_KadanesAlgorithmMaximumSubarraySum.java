import java.util.*;

/** TUF 2760 - Kadane's Algorithm, maximum subarray sum. Largest sum of a non-empty contiguous subarray. */
public class P2760_KadanesAlgorithmMaximumSubarraySum {

    /** Approach 1: every (i, j) pair, summing the subarray from scratch. O(n^3) time, O(1) space. */
    static long bruteForce(int[] nums) {
        long best = Long.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                long sum = 0;
                for (int k = i; k <= j; k++) sum += nums[k];
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    /** Approach 2: fix the start, extend the end while keeping a running sum. O(n^2) time, O(1) space. */
    static long better(int[] nums) {
        long best = Long.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            long sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];                              // sum of nums[i..j] reuses nums[i..j-1]
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    /** Approach 3: Kadane - drop the running sum the moment it goes negative. O(n) time, O(1) space. */
    static long optimal(int[] nums) {
        long best = Long.MIN_VALUE;
        long cur = 0;                                        // best sum of a subarray ending at the current index
        for (int x : nums) {
            cur += x;
            best = Math.max(best, cur);                      // record before resetting, so all-negative arrays work
            if (cur < 0) cur = 0;                            // a negative prefix can only hurt what comes next
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, long expected) {
        check(bruteForce(nums) == expected, "bruteForce " + Arrays.toString(nums));
        check(better(nums) == expected, "better " + Arrays.toString(nums));
        check(optimal(nums) == expected, "optimal " + Arrays.toString(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, 6);           // [4, -1, 2, 1]
        verify(new int[]{5, 4, -1, 7, 8}, 23);                          // the whole array
        verify(new int[]{1}, 1);                                        // single element
        verify(new int[]{-3, -1, -2}, -1);                              // all negative: best single element
        verify(new int[]{0, 0, 0}, 0);
        verify(new int[]{2, -1, 2, -1, 2}, 4);                          // dipping but never below zero: keep going
        verify(new int[]{-1, 3, -5, 4, 6, -1, 2, -7, 13, -3}, 17);
        verify(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}, 2L * Integer.MAX_VALUE);   // overflows int
        System.out.println("OK P2760_KadanesAlgorithmMaximumSubarraySum");
    }
}
