import java.util.*;

/** TUF 562 - Count subarrays with given xor K. Count contiguous subarrays whose xor of elements equals k. */
public class P562_CountSubarraysWithGivenXorK {

    /** Approach 1: every starting index, extend and keep a running xor. O(n^2) time, O(1) space. */
    static long bruteForce(int[] nums, int k) {
        long count = 0;
        for (int i = 0; i < nums.length; i++) {
            int xr = 0;
            for (int j = i; j < nums.length; j++) {
                xr ^= nums[j];
                if (xr == k) count++;
            }
        }
        return count;
    }

    /** Approach 2: prefix xor and a frequency map of earlier prefixes. O(n) time, O(n) space. */
    static long optimal(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        freq.put(0, 1);                                  // the empty prefix has xor 0
        int xr = 0;
        long count = 0;
        for (int x : nums) {
            xr ^= x;
            count += freq.getOrDefault(xr ^ k, 0);       // earlier prefixes p with p ^ xr == k
            freq.merge(xr, 1, Integer::sum);
        }
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, long expected) {
        String in = Arrays.toString(nums) + " k=" + k;
        check(bruteForce(nums, k) == expected, "bruteForce " + in + " -> " + bruteForce(nums, k));
        check(optimal(nums, k) == expected, "optimal " + in + " -> " + optimal(nums, k));
    }

    public static void main(String[] args) {
        verify(new int[]{4, 2, 2, 6, 4}, 6, 4);
        verify(new int[]{5, 6, 7, 8, 9}, 5, 2);
        verify(new int[]{}, 0, 0);                       // empty input
        verify(new int[]{6}, 6, 1);                      // single element equal to k
        verify(new int[]{6}, 7, 0);                      // single element different from k
        verify(new int[]{0, 0, 0}, 0, 6);                // every subarray has xor 0
        verify(new int[]{1, 2, 3}, 0, 1);                // only the whole array
        verify(new int[]{1, 2, 3}, 3, 2);                // [3] and [1, 2]
        verify(new int[]{1, 1, 1, 1}, 0, 4);             // every even-length subarray
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, -1}, -1, 2);   // [MAX, MIN] and [-1]
        System.out.println("OK P562_CountSubarraysWithGivenXorK");
    }
}
