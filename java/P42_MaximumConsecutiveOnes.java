import java.util.*;

/** TUF 42 - Maximum Consecutive Ones. Length of the longest run of 1s in a binary array. */
public class P42_MaximumConsecutiveOnes {

    /** Approach 1: from every index, walk right while the value is 1. O(n^2) time in the worst case, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length, best = 0;
        for (int i = 0; i < n; i++) {
            int j = i;
            while (j < n && nums[j] == 1) j++;
            best = Math.max(best, j - i);
        }
        return best;
    }

    /** Approach 2: one pass with a running counter that resets on every 0. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        int best = 0, run = 0;
        for (int x : nums) {
            if (x == 1) {
                run++;
                if (run > best) best = run;
            } else {
                run = 0;
            }
        }
        return best;
    }

    /** Approach 3: the same sweep phrased as "distance from the last zero". O(n) time, O(1) space. */
    static int optimalLastZero(int[] nums) {
        int best = 0, lastZero = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) lastZero = i;
            else best = Math.max(best, i - lastZero);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String in = Arrays.toString(nums.length > 20 ? Arrays.copyOf(nums, 20) : nums);
        check(bruteForce(nums) == expected, "bruteForce failed on " + in + " expected " + expected);
        check(optimal(nums) == expected, "optimal failed on " + in + " expected " + expected);
        check(optimalLastZero(nums) == expected, "optimalLastZero failed on " + in + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 0, 1, 1, 1}, 3);
        verify(new int[]{1, 0, 1, 1, 0, 1}, 2);
        verify(new int[]{0, 0, 0}, 0);                              // no ones at all
        verify(new int[]{1, 1, 1, 1}, 4);                           // the whole array is one run
        verify(new int[]{}, 0);                                     // empty
        verify(new int[]{1}, 1);                                    // single one
        verify(new int[]{0}, 0);                                    // single zero
        verify(new int[]{0, 1, 1, 1, 0, 1, 1, 1, 1, 0}, 4);         // longest run is in the middle
        verify(new int[]{1, 1, 0, 1, 1}, 2);                        // two equal runs
        int[] big = new int[20_000];
        Arrays.fill(big, 1);
        big[12_345] = 0;                                            // runs of 12345 and 7654
        verify(big, 12_345);
        System.out.println("OK P42_MaximumConsecutiveOnes");
    }
}
