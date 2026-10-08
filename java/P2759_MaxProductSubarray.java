import java.util.*;

/** TUF 2759 - Max Product Subarray. Return the largest product of a non-empty contiguous subarray (every subarray product fits in an int). */
public class P2759_MaxProductSubarray {

    /** Approach 1: fix the start, extend the end one step at a time keeping a running product. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int prod = 1;
            for (int j = i; j < nums.length; j++) {
                prod *= nums[j];                               // product of nums[i..j]
                best = Math.max(best, prod);
            }
        }
        return best;
    }

    /** Approach 2: the answer is always a prefix or a suffix of a zero-free block, so scan prefix and suffix products. O(n) time, O(1) space. */
    static int prefixSuffix(int[] nums) {
        int n = nums.length, best = Integer.MIN_VALUE, prefix = 1, suffix = 1;
        for (int i = 0; i < n; i++) {
            if (prefix == 0) prefix = 1;                       // a zero ends the block; start a new one
            if (suffix == 0) suffix = 1;
            prefix *= nums[i];
            suffix *= nums[n - 1 - i];
            best = Math.max(best, Math.max(prefix, suffix));
        }
        return best;
    }

    /** Approach 3: Kadane-style DP that tracks both the largest and the smallest product ending at each index. O(n) time, O(1) space. */
    static int minMaxDp(int[] nums) {
        int maxEnd = nums[0], minEnd = nums[0], best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            if (x < 0) {                                       // multiplying by a negative swaps largest and smallest
                int t = maxEnd;
                maxEnd = minEnd;
                minEnd = t;
            }
            maxEnd = Math.max(x, maxEnd * x);                  // either start fresh at x or extend
            minEnd = Math.min(x, minEnd * x);
            best = Math.max(best, maxEnd);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        check(bruteForce(nums) == expected, "bruteForce " + Arrays.toString(nums) + " got " + bruteForce(nums));
        check(prefixSuffix(nums) == expected, "prefixSuffix " + Arrays.toString(nums) + " got " + prefixSuffix(nums));
        check(minMaxDp(nums) == expected, "minMaxDp " + Arrays.toString(nums) + " got " + minMaxDp(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, -2, 4}, 6);
        verify(new int[]{-2, 0, -1}, 0);
        verify(new int[]{-2}, -2);                                         // single negative element
        verify(new int[]{-2, 3, -4}, 24);                                  // two negatives cancel
        verify(new int[]{0, 0}, 0);
        verify(new int[]{-1, -2, -3, 0, 3, 5}, 15);
        verify(new int[]{2, -5, -2, -4, 3}, 24);                           // odd number of negatives: drop one end
        verify(new int[]{-3, -1, -1}, 3);
        verify(new int[]{-2, 0}, 0);                                       // zero beats every negative
        verify(new int[]{1 << 15, 1 << 15, -1}, 1 << 30);                  // large values

        // seeded random arrays checked against brute force
        Random rnd = new Random(2759);
        for (int t = 0; t < 2000; t++) {
            int n = 1 + rnd.nextInt(12);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(9) - 4;
            int ref = bruteForce(a);
            check(prefixSuffix(a) == ref && minMaxDp(a) == ref, "mismatch on " + Arrays.toString(a));
        }
        System.out.println("OK P2759_MaxProductSubarray");
    }
}
