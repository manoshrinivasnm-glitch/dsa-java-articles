import java.util.*;

/** TUF 93 - Find the smallest divisor. Smallest d >= 1 such that sum of ceil(nums[i] / d) is at most threshold, or -1. */
public class P93_FindTheSmallestDivisor {

    /** Sum of ceil(x / d) over the array. long: n values each up to 10^6 can exceed int when d is small. O(n). */
    static long sumWithDivisor(int[] nums, int d) {
        long sum = 0;
        for (int x : nums) sum += ((long) x + d - 1) / d;
        return sum;
    }

    /** Approach 1: try d = 1, 2, 3, ... up to the largest element. O(n * max) time, O(1) space. */
    static int bruteForce(int[] nums, int threshold) {
        int max = 0;
        for (int x : nums) max = Math.max(max, x);
        for (int d = 1; d <= max; d++) {
            if (sumWithDivisor(nums, d) <= threshold) return d;
        }
        return -1;
    }

    /** Approach 2: binary search on d in [1, max]; the sum only shrinks as d grows. O(n log max) time, O(1) space. */
    static int optimal(int[] nums, int threshold) {
        int max = 0;
        for (int x : nums) max = Math.max(max, x);
        int lo = 1, hi = max, ans = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (sumWithDivisor(nums, mid) <= threshold) { ans = mid; hi = mid - 1; }
            else lo = mid + 1;
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int threshold, int expected) {
        String in = Arrays.toString(nums) + " threshold=" + threshold;
        check(bruteForce(nums, threshold) == expected, "bruteForce " + in);
        check(optimal(nums, threshold) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 5, 9}, 6, 5);
        verify(new int[]{44, 22, 33, 11, 1}, 5, 44);          // threshold == n: every element must round to 1
        verify(new int[]{21212, 10101, 12121}, 1_000_000, 1); // threshold huge: d = 1 already works
        verify(new int[]{2, 3, 5, 7, 11}, 11, 3);
        verify(new int[]{5}, 1, 5);                           // single element
        verify(new int[]{1, 1, 1}, 2, -1);                    // threshold < n: impossible
        verify(new int[]{1, 2, 3}, 6, 1);
        verify(new int[]{1_000_000, 1_000_000, 1_000_000}, 3, 1_000_000);
        System.out.println("OK P93_FindTheSmallestDivisor");
    }
}
