import java.util.*;

/** TUF 29 - Kadane's Algorithm. Maximum sum of a non-empty contiguous subarray. */
public class P29_KadanesAlgorithm {

    /** Approach 1: for every (start, end) pair, add up its elements from scratch. O(n^3) time, O(1) space. */
    static long bruteForce(int[] nums) {
        int n = nums.length;
        long best = Long.MIN_VALUE;                  // below any real sum, so all-negative input works
        for (int i = 0; i < n; i++) {                // i = start of the subarray
            for (int j = i; j < n; j++) {            // j = end of the subarray
                long sum = 0;
                for (int k = i; k <= j; k++) sum += nums[k];   // re-add nums[i..j] from scratch
                best = Math.max(best, sum);          // keep the largest sum seen
            }
        }
        return best;
    }

    /** Approach 2: fix the start and keep a running sum while the end moves right. O(n^2) time, O(1) space. */
    static long better(int[] nums) {
        int n = nums.length;
        long best = Long.MIN_VALUE;                  // below any real sum, so all-negative input works
        for (int i = 0; i < n; i++) {                // i = start of the subarray
            long sum = 0;                            // sum of nums[i..j]
            for (int j = i; j < n; j++) {            // j = end, moving right
                sum += nums[j];                      // extend by one element, no re-adding
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    /** Approach 3: Kadane. Carry a running sum, and drop it the moment it turns negative. O(n) time, O(1) space. */
    static long optimal(int[] nums) {
        long best = Long.MIN_VALUE, current = 0;     // current = sum of the run ending here
        for (int x : nums) {
            current += x;                            // extend the run with x
            best = Math.max(best, current);          // record before any reset, so negatives count
            if (current < 0) current = 0;            // a negative run only hurts what follows: drop it
        }
        return best;
    }

    /** Approach 3 (variant): the same algorithm written as the DP recurrence endingHere = max(x, endingHere + x). */
    static long optimalRecurrence(int[] nums) {
        long best = nums[0], endingHere = nums[0];   // best subarray ending at index 0 is nums[0]
        for (int i = 1; i < nums.length; i++) {
            endingHere = Math.max(nums[i], endingHere + nums[i]);   // start fresh at i, or extend the run
            best = Math.max(best, endingHere);       // best over every end position
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, long expected) {
        String in = Arrays.toString(nums.length > 20 ? Arrays.copyOf(nums, 20) : nums);
        check(bruteForce(nums) == expected, "bruteForce failed on " + in + " expected " + expected);
        check(better(nums) == expected, "better failed on " + in + " expected " + expected);
        check(optimal(nums) == expected, "optimal failed on " + in + " expected " + expected);
        check(optimalRecurrence(nums) == expected, "optimalRecurrence failed on " + in + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, 6);               // [4, -1, 2, 1]
        verify(new int[]{5, 4, -1, 7, 8}, 23);                              // whole array
        verify(new int[]{-3, -1, -2}, -1);                                  // all negative: best is the largest element
        verify(new int[]{5}, 5);                                            // single element
        verify(new int[]{-5}, -5);                                          // single negative element
        verify(new int[]{1, -1, 1, -1, 1}, 1);                              // sum keeps returning to zero
        verify(new int[]{0, 0, 0}, 0);                                      // all zeros
        verify(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}, 4_294_967_294L);   // overflows int
        verify(new int[]{-1, -2, Integer.MIN_VALUE}, -1);
        Random rnd = new Random(42);
        int[] big = new int[300];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(201) - 100;
        verify(big, bruteForce(big));                                       // 300 random values, fixed seed
        System.out.println("OK P29_KadanesAlgorithm");
    }
}
