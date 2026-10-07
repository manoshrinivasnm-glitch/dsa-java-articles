import java.util.*;

/** TUF 605 - Largest Subarray with Sum 0. Return the length of the longest contiguous subarray whose elements sum to zero. */
public class P605_LargestSubarrayWithSum0 {

    /** Approach 1: every starting index, extend the subarray and keep a running sum. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length, best = 0;
        for (int i = 0; i < n; i++) {
            long sum = 0;
            for (int j = i; j < n; j++) {
                sum += nums[j];
                if (sum == 0) best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    /** Approach 2: prefix sums and a map from prefix value to its first index. O(n) time, O(n) space. */
    static int optimal(int[] nums) {
        Map<Long, Integer> firstSeen = new HashMap<>();
        firstSeen.put(0L, -1);                           // the empty prefix has sum 0 and ends at index -1
        long prefix = 0;
        int best = 0;
        for (int i = 0; i < nums.length; i++) {
            prefix += nums[i];
            Integer first = firstSeen.get(prefix);
            if (first != null) best = Math.max(best, i - first);   // nums[first+1 .. i] sums to zero
            else firstSeen.put(prefix, i);                         // keep only the earliest index
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        check(bruteForce(nums) == expected, "bruteForce " + Arrays.toString(nums) + " -> " + bruteForce(nums));
        check(optimal(nums) == expected, "optimal " + Arrays.toString(nums) + " -> " + optimal(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{15, -2, 2, -8, 1, 7, 10, 23}, 5);     // -2 + 2 - 8 + 1 + 7
        verify(new int[]{1, 2, 3}, 0);                           // no zero-sum subarray
        verify(new int[]{0}, 1);                                 // a lone zero counts
        verify(new int[]{0, 0, 0}, 3);
        verify(new int[]{1, -1, 3, 2, -2, -3, 3}, 6);            // prefix 0 reappears at index 5
        verify(new int[]{}, 0);                                  // empty input
        verify(new int[]{1, 2, -3, 3}, 3);
        verify(new int[]{-1, 1, -1, 1}, 4);                      // whole array
        verify(new int[]{2_000_000_000, 2_000_000_000, 294_967_296}, 0);   // int prefix would wrap to exactly 0
        verify(new int[]{2_000_000_000, 2_000_000_000, -2_000_000_000, -2_000_000_000}, 4);
        System.out.println("OK P605_LargestSubarrayWithSum0");
    }
}
