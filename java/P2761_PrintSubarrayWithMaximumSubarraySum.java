import java.util.*;

/**
 * TUF 2761 - Print the subarray with the maximum sum. Among subarrays with the maximum sum, return the one that
 * starts earliest, and among those the shortest. Returns an empty array for empty input.
 */
public class P2761_PrintSubarrayWithMaximumSubarraySum {

    /** Approach 1: every (start, end) pair, summed from scratch. O(n^3) time, O(1) extra space. */
    static int[] bruteForce(int[] nums) {
        int n = nums.length;
        long best = Long.MIN_VALUE;
        int bestStart = 0, bestEnd = -1;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                long sum = 0;
                for (int k = i; k <= j; k++) sum += nums[k];
                if (sum > best) {
                    best = sum;
                    bestStart = i;
                    bestEnd = j;
                }
            }
        }
        return Arrays.copyOfRange(nums, bestStart, bestEnd + 1);
    }

    /** Approach 2: fix the start and keep a running sum. O(n^2) time, O(1) extra space. */
    static int[] better(int[] nums) {
        int n = nums.length;
        long best = Long.MIN_VALUE;
        int bestStart = 0, bestEnd = -1;
        for (int i = 0; i < n; i++) {
            long sum = 0;
            for (int j = i; j < n; j++) {
                sum += nums[j];
                if (sum > best) {
                    best = sum;
                    bestStart = i;
                    bestEnd = j;
                }
            }
        }
        return Arrays.copyOfRange(nums, bestStart, bestEnd + 1);
    }

    /** Approach 3: Kadane with bookkeeping. Remember where the current run began; record it when a new best appears. O(n) time, O(1) extra space. */
    static int[] optimal(int[] nums) {
        long best = Long.MIN_VALUE, current = 0;
        int start = 0, bestStart = 0, bestEnd = -1;
        for (int i = 0; i < nums.length; i++) {
            current += nums[i];
            if (current > best) {
                best = current;
                bestStart = start;
                bestEnd = i;
            }
            if (current < 0) {
                current = 0;
                start = i + 1;
            }
        }
        return Arrays.copyOfRange(nums, bestStart, bestEnd + 1);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int[] expected) {
        String in = Arrays.toString(nums.length > 20 ? Arrays.copyOf(nums, 20) : nums);
        int[] a = bruteForce(nums);
        check(Arrays.equals(a, expected), "bruteForce failed on " + in + " got " + Arrays.toString(a));
        int[] b = better(nums);
        check(Arrays.equals(b, expected), "better failed on " + in + " got " + Arrays.toString(b));
        int[] c = optimal(nums);
        check(Arrays.equals(c, expected), "optimal failed on " + in + " got " + Arrays.toString(c));
    }

    public static void main(String[] args) {
        verify(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, new int[]{4, -1, 2, 1});
        verify(new int[]{1, -1, 2}, new int[]{1, -1, 2});                   // tie on sum 2: earliest start wins
        verify(new int[]{2, -2, 3, -3, 3}, new int[]{2, -2, 3});            // tie: earliest start, then shortest
        verify(new int[]{-1, 3, -3, 3}, new int[]{3});                      // negative prefix is dropped
        verify(new int[]{-3, -1, -2}, new int[]{-1});                       // all negative
        verify(new int[]{5}, new int[]{5});                                 // single element
        verify(new int[]{}, new int[]{});                                   // empty
        verify(new int[]{1, 2, 3}, new int[]{1, 2, 3});                     // all positive: whole array
        verify(new int[]{0, 0}, new int[]{0});                              // zeros: shortest among equal sums
        verify(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, -1}, new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}); // long sums
        Random rnd = new Random(42);
        int[] big = new int[300];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(201) - 100;
        verify(big, bruteForce(big));                                       // 300 random values, fixed seed
        System.out.println("OK P2761_PrintSubarrayWithMaximumSubarraySum");
    }
}
