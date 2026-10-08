import java.util.*;

/** TUF 2837 - Largest Subarray with K sum. Length of the longest contiguous subarray whose sum is exactly k (values may be negative); 0 if none. */
public class P2837_LargestSubarrayWithKSum {

    /** Approach 1: try every subarray and add it up from scratch. O(n^3) time, O(1) space. */
    static int bruteForce(int[] nums, int k) {
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

    /** Approach 2: grow each start one element at a time, reusing the running sum. O(n^2) time, O(1) space. */
    static int better(int[] nums, int k) {
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

    /** Approach 3: prefix sums with a map from each prefix value to its FIRST index. O(n) average time, O(n) space. */
    static int optimal(int[] nums, int k) {
        Map<Long, Integer> first = new HashMap<>();
        first.put(0L, -1);                           // empty prefix, so a subarray may start at index 0
        long prefix = 0;
        int best = 0;
        for (int j = 0; j < nums.length; j++) {
            prefix += nums[j];
            Integer i = first.get(prefix - k);       // sum(i+1..j) == k  <=>  prefix[i] == prefix[j] - k
            if (i != null) best = Math.max(best, j - i);
            first.putIfAbsent(prefix, j);            // keep the earliest index: it gives the longest subarray
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int expected) {
        int[] got = {bruteForce(nums, k), better(nums, k), optimal(nums, k)};
        for (int g : got) {
            check(g == expected, Arrays.toString(nums) + " k=" + k + ": expected " + expected + " got " + Arrays.toString(got));
        }
    }

    public static void main(String[] args) {
        verify(new int[]{10, 5, 2, 7, 1, 9}, 15, 4);                  // [5, 2, 7, 1]
        verify(new int[]{-1, 1, 1}, 1, 3);                            // the whole array
        verify(new int[]{1, -1, 5, -2, 3}, 3, 4);                     // [1, -1, 5, -2]
        verify(new int[]{-2, -1, 2, 1}, 1, 2);                        // [-1, 2]
        verify(new int[]{2, 0, 0, 3}, 3, 3);                          // zeros must be included: [0, 0, 3]
        verify(new int[]{15, -2, 2, -8, 1, 7, 10, 23}, 0, 5);         // k = 0: [-2, 2, -8, 1, 7]
        verify(new int[]{0, 0, 0}, 0, 3);
        verify(new int[]{1, 2, 3}, 7, 0);                             // no subarray works
        verify(new int[]{5}, 5, 1);                                   // single element
        verify(new int[]{}, 0, 0);                                    // empty array: the empty subarray does not count
        verify(new int[]{2_000_000_000, 2_000_000_000}, -294967296, 0);   // int addition would wrap to exactly k
        Random rnd = new Random(7);                                   // seeded cross-check against brute force
        for (int t = 0; t < 400; t++) {
            int[] a = new int[rnd.nextInt(15)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(7) - 3;
            int k = rnd.nextInt(9) - 4;
            verify(a, k, bruteForce(a, k));
        }
        System.out.println("OK P2837_LargestSubarrayWithKSum");
    }
}
