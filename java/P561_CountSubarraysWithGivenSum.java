import java.util.*;

/** TUF 561 - Count subarrays with given sum. Number of contiguous subarrays whose elements add up to exactly k. */
public class P561_CountSubarraysWithGivenSum {

    /** Approach 1: enumerate every (i, j) and add up the elements between them. O(n^3) time, O(1) space. */
    static int bruteForce(int[] nums, int k) {
        int n = nums.length, count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                long sum = 0;
                for (int p = i; p <= j; p++) sum += nums[p];
                if (sum == k) count++;
            }
        }
        return count;
    }

    /** Approach 2: fix the start, extend the end, keep a running sum. O(n^2) time, O(1) space. */
    static int better(int[] nums, int k) {
        int n = nums.length, count = 0;
        for (int i = 0; i < n; i++) {
            long sum = 0;
            for (int j = i; j < n; j++) {
                sum += nums[j];
                if (sum == k) count++;
            }
        }
        return count;
    }

    /** Approach 3: prefix sums in a hash map. The subarray ending here sums to k exactly when an earlier prefix equals (currentPrefix - k). O(n) time, O(n) space. */
    static int optimal(int[] nums, int k) {
        Map<Long, Integer> freq = new HashMap<>();   // prefix sum -> how many times it has occurred so far
        freq.put(0L, 1);                             // the empty prefix, so subarrays starting at index 0 are counted
        long prefix = 0;
        int count = 0;
        for (int x : nums) {
            prefix += x;
            count += freq.getOrDefault(prefix - k, 0);
            freq.merge(prefix, 1, Integer::sum);
        }
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int expected) {
        check(bruteForce(nums, k) == expected, "bruteForce " + Arrays.toString(nums) + " k=" + k + " -> " + bruteForce(nums, k));
        check(better(nums, k) == expected, "better " + Arrays.toString(nums) + " k=" + k + " -> " + better(nums, k));
        check(optimal(nums, k) == expected, "optimal " + Arrays.toString(nums) + " k=" + k + " -> " + optimal(nums, k));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 1}, 2, 2);
        verify(new int[]{1, 2, 3}, 3, 2);                                       // [1,2] and [3]
        verify(new int[]{3, 1, 2, 4}, 6, 2);                                    // [3,1,2] and [2,4]
        verify(new int[]{1, -1, 0}, 0, 3);                                      // [1,-1], [0], [1,-1,0]
        verify(new int[]{0, 0, 0, 0}, 0, 10);                                   // every subarray: 4*5/2
        verify(new int[]{}, 0, 0);                                              // empty: the empty prefix alone is not a subarray
        verify(new int[]{5}, 5, 1);                                             // single element hit
        verify(new int[]{5}, 3, 0);                                             // single element miss
        verify(new int[]{-1, -1, 1}, 0, 1);                                     // [-1,1]
        verify(new int[]{1_000_000_000, 1_000_000_000, -1_000_000_000}, 1_000_000_000, 3); // prefix sums exceed int
        System.out.println("OK P561_CountSubarraysWithGivenSum");
    }
}
