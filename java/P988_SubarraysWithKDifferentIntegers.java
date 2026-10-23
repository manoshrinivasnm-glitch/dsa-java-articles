import java.util.*;

/** TUF 988 - Subarrays with K Different Integers. Count subarrays that contain exactly k distinct values. */
public class P988_SubarraysWithKDifferentIntegers {

    /** Approach 1: every start, extend while the distinct count is at most k. O(n^2) time, O(n) space. */
    static int bruteForce(int[] nums, int k) {
        int n = nums.length, count = 0;                // count = subarrays with exactly k distinct values
        for (int i = 0; i < n; i++) {                  // try every start index i
            Set<Integer> seen = new HashSet<>();       // distinct values in nums[i..j]
            for (int j = i; j < n; j++) {
                seen.add(nums[j]);
                if (seen.size() == k) count++;         // exactly k distinct: count it
                else if (seen.size() > k) break;       // distinct count never drops as j grows
            }
        }
        return count;
    }

    /** Approach 2: exactly(k) = atMostDistinct(k) - atMostDistinct(k - 1). O(n) time, O(n) space. */
    static int optimal(int[] nums, int k) {
        // exactly k = (at most k) minus (at most k - 1)
        return atMostDistinct(nums, k) - atMostDistinct(nums, k - 1);
    }

    static int atMostDistinct(int[] nums, int k) {
        if (k < 0) return 0;                           // no subarray has fewer than 0 distinct values
        Map<Integer, Integer> freq = new HashMap<>();  // value -> its count in the window nums[l..r]
        int l = 0, count = 0;
        for (int r = 0; r < nums.length; r++) {        // grow the window by one on the right
            freq.merge(nums[r], 1, Integer::sum);      // add nums[r] to the window
            while (freq.size() > k) {                  // too many distinct values: shrink from the left
                int x = nums[l];
                if (freq.merge(x, -1, Integer::sum) == 0) freq.remove(x);   // x fully left the window
                l++;
            }
            count += r - l + 1;                        // starts l..r all give at most k distinct values
        }
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int expected) {
        String tag = Arrays.toString(nums) + " k=" + k + " expected " + expected;
        check(bruteForce(nums, k) == expected, "bruteForce " + tag);
        check(optimal(nums, k) == expected, "optimal " + tag);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 1, 2, 3}, 2, 7);
        verify(new int[]{1, 2, 1, 3, 4}, 3, 3);
        verify(new int[]{1, 1, 1}, 1, 6);              // every subarray has exactly one value
        verify(new int[]{2, 1, 2, 1, 2}, 2, 10);       // every subarray of length >= 2
        verify(new int[]{1, 2, 3}, 4, 0);              // k larger than the number of values
        verify(new int[]{}, 1, 0);                     // empty array
        System.out.println("OK P988_SubarraysWithKDifferentIntegers");
    }
}
