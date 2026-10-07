import java.util.*;

/** TUF 2836 - Longest subarray with sum K when every element is non-negative. Returns 0 if no subarray has sum K. */
public class P2836_LongestSubarrayWithGivenSumKPositives {

    /** Approach 1: fix the start, extend the end while accumulating the sum. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums, long k) {
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

    /** Approach 2: prefix sums in a hash map keyed by their earliest index. O(n) time, O(n) space. Works for negatives too. */
    static int better(int[] nums, long k) {
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

    /** Approach 3: sliding window; valid because non-negative values make the window sum monotone. O(n) time, O(1) space. */
    static int optimal(int[] nums, long k) {
        int n = nums.length, left = 0, best = 0;
        long sum = 0;
        for (int right = 0; right < n; right++) {
            sum += nums[right];
            while (sum > k && left <= right) {
                sum -= nums[left];
                left++;
            }
            if (sum == k) best = Math.max(best, right - left + 1);
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
        verify(new int[]{2, 3, 5, 1, 9}, 10, 3);                            // [2, 3, 5]
        verify(new int[]{1, 2, 3, 1, 1, 1, 1, 4, 2, 3}, 3, 3);              // [1, 1, 1]
        verify(new int[]{0, 0, 1, 0, 0}, 1, 5);                             // zeros stretch the window on both sides
        verify(new int[]{1, 2, 3}, 100, 0);                                 // no subarray has sum k
        verify(new int[]{}, 0, 0);                                          // empty
        verify(new int[]{0, 0, 0}, 0, 3);                                   // k = 0 with zeros
        verify(new int[]{5}, 5, 1);                                         // single element equal to k
        verify(new int[]{5}, 4, 0);                                         // single element, no answer
        verify(new int[]{1, 1, 1, 1}, 2, 2);                                // many equal answers
        verify(new int[]{2_000_000_000, 2_000_000_000, 1}, 4_000_000_000L, 2); // sums overflow int
        int[] big = new int[5000];
        Arrays.fill(big, 1);
        verify(big, 4999, 4999);                                            // almost the whole array
        System.out.println("OK P2836_LongestSubarrayWithGivenSumKPositives");
    }
}
