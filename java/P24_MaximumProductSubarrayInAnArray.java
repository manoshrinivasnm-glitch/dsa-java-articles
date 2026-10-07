import java.util.*;

/** TUF 24 - Maximum Product Subarray. Return the largest product of any non-empty contiguous subarray (n >= 1, every subarray product fits in an int). */
public class P24_MaximumProductSubarrayInAnArray {

    /** Approach 1: every starting index, extend the subarray and keep a running product. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length;
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            long prod = 1;
            for (int j = i; j < n; j++) {
                prod *= nums[j];
                if (prod > best) best = (int) prod;
            }
        }
        return best;
    }

    /** Approach 2: running prefix and suffix products that restart after a zero. O(n) time, O(1) space. */
    static int optimalPrefixSuffix(int[] nums) {
        int n = nums.length;
        int best = Integer.MIN_VALUE;
        long prefix = 1, suffix = 1;
        for (int i = 0; i < n; i++) {
            if (prefix == 0) prefix = 1;                       // a zero ended the previous run: start fresh
            if (suffix == 0) suffix = 1;
            prefix *= nums[i];                                 // product of nums[(last zero)+1 .. i]
            suffix *= nums[n - 1 - i];                         // product of nums[n-1-i .. (next zero)-1]
            best = (int) Math.max(best, Math.max(prefix, suffix));
        }
        return best;
    }

    /** Approach 3: Kadane-style scan that carries both the largest and the smallest product ending here. O(n) time, O(1) space. */
    static int optimalMinMax(int[] nums) {
        int best = nums[0];
        long curMax = nums[0], curMin = nums[0];
        for (int i = 1; i < nums.length; i++) {
            long x = nums[i];
            long a = curMax * x, b = curMin * x;
            curMax = Math.max(x, Math.max(a, b));              // largest product of a subarray ending at i
            curMin = Math.min(x, Math.min(a, b));              // smallest product of a subarray ending at i
            best = (int) Math.max(best, curMax);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String in = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce " + in + " -> " + bruteForce(nums));
        check(optimalPrefixSuffix(nums) == expected, "optimalPrefixSuffix " + in + " -> " + optimalPrefixSuffix(nums));
        check(optimalMinMax(nums) == expected, "optimalMinMax " + in + " -> " + optimalMinMax(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, -2, 4}, 6);
        verify(new int[]{-2, 0, -1}, 0);                         // the zero is the best we can do
        verify(new int[]{-2}, -2);                               // single negative element
        verify(new int[]{-2, -3, 0, -4, -5}, 20);                // best run is after the zero
        verify(new int[]{1, 2, -3, 0, -4, -5}, 20);
        verify(new int[]{0, 0}, 0);
        verify(new int[]{-1, -2, -3, 4}, 24);                    // whole array
        verify(new int[]{3, -1, 4}, 4);                          // one negative splits the array
        verify(new int[]{2, -5, -2, -4, 3}, 24);                 // odd number of negatives: drop one end
        verify(new int[]{-2, 3, -4}, 24);
        verify(new int[]{Integer.MIN_VALUE, 0, 3}, 3);
        verify(new int[]{-1, -1, -1}, 1);
        System.out.println("OK P24_MaximumProductSubarrayInAnArray");
    }
}
