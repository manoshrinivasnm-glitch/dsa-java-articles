import java.util.*;

/** TUF 564 - Longest subarray with sum K where elements may be negative, zero or positive. Returns 0 if none. */
public class P564_LongestSubarrayWithSumK {

    /** Approach 1: check every (start, end) pair and re-add its elements from scratch. O(n^3) time, O(1) space. */
    static int bruteForce(int[] nums, long k) {
        int n = nums.length, best = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                long sum = 0;
                for (int t = i; t <= j; t++) sum += nums[t];
                if (sum == k) best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    /** Approach 2: fix the start, grow the end while keeping a running sum. O(n^2) time, O(1) space. */
    static int better(int[] nums, long k) {
        int n = nums.length, best = 0;
        for (int i = 0; i < n; i++) {
            long sum = 0;
            for (int j = i; j < n; j++) {
                sum += nums[j];
                if (sum == k) best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    /** Approach 3: prefix sums and a hash map of their earliest index. O(n) time, O(n) space. */
    static int optimal(int[] nums, long k) {
        Map<Long, Integer> firstIndex = new HashMap<>();   // prefix sum -> earliest index where it occurs
        firstIndex.put(0L, -1);                            // the empty prefix, so subarrays starting at 0 are found
        long prefix = 0;
        int best = 0;
        for (int i = 0; i < nums.length; i++) {
            prefix += nums[i];
            Integer j = firstIndex.get(prefix - k);
            if (j != null) best = Math.max(best, i - j);
            firstIndex.putIfAbsent(prefix, i);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, long k, int expected) {
        String in = Arrays.toString(nums.length > 20 ? Arrays.copyOf(nums, 20) : nums) + " k=" + k;
        check(bruteForce(nums, k) == expected, "bruteForce failed on " + in + " expected " + expected);
        check(better(nums, k) == expected, "better failed on " + in + " expected " + expected);
        check(optimal(nums, k) == expected, "optimal failed on " + in + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(new int[]{1, -1, 5, -2, 3}, 3, 4);                           // [1, -1, 5, -2]
        verify(new int[]{-2, -1, 2, 1}, 1, 2);                              // [-1, 2]
        verify(new int[]{3, -2, 1}, 2, 3);                                  // the case where a sliding window fails
        verify(new int[]{2, 0, 0, 3}, 3, 3);                                // needs the EARLIEST index of prefix 2
        verify(new int[]{1, 2, 3, -3, 3}, 3, 4);                            // [1, 2, 3, -3]
        verify(new int[]{1, 2, 3}, 7, 0);                                   // no answer
        verify(new int[]{}, 0, 0);                                          // empty
        verify(new int[]{0, 0, 0}, 0, 3);                                   // all zeros, k = 0
        verify(new int[]{-5}, -5, 1);                                       // single negative element
        verify(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, -1}, 4_294_967_293L, 3);   // sums overflow int
        verify(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE}, -4_294_967_296L, 2);
        int[] big = new int[6000];
        for (int i = 0; i < big.length; i++) big[i] = (i % 2 == 0) ? 1 : -1;
        verify(big, 0, 6000);                                               // alternating +1/-1, whole array sums to 0
        System.out.println("OK P564_LongestSubarrayWithSumK");
    }
}
