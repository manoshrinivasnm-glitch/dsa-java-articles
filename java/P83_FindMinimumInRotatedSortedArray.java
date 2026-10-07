import java.util.*;

/** TUF 83 - Find minimum in Rotated Sorted Array. Distinct values, n >= 1; return the smallest value. */
public class P83_FindMinimumInRotatedSortedArray {

    /** Approach 1: look at every element. O(n) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int best = nums[0];
        for (int v : nums) best = Math.min(best, v);
        return best;
    }

    /** Approach 2: binary search; at each step take the minimum of the sorted half and discard that half. */
    static int optimal(int[] nums) {
        int lo = 0, hi = nums.length - 1, ans = Integer.MAX_VALUE;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[lo] <= nums[hi]) {              // whole range sorted: its first element is the smallest
                ans = Math.min(ans, nums[lo]);
                break;
            }
            if (nums[lo] <= nums[mid]) {             // left half sorted: nums[lo] is its minimum, drop it
                ans = Math.min(ans, nums[lo]);
                lo = mid + 1;
            } else {                                 // right half sorted: nums[mid] is its minimum, drop it
                ans = Math.min(ans, nums[mid]);
                hi = mid - 1;
            }
        }
        return ans;
    }

    /** Approach 3: compare mid with the right end; lo and hi converge on the minimum. O(log n) time, O(1) space. */
    static int optimalCompareRight(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) lo = mid + 1;  // the drop is strictly right of mid
            else hi = mid;                           // nums[mid] < nums[hi]: minimum is mid or left of it
        }
        return nums[lo];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String in = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce " + in);
        check(optimal(nums) == expected, "optimal " + in);
        check(optimalCompareRight(nums) == expected, "optimalCompareRight " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 4, 5, 1, 2}, 1);
        verify(new int[]{4, 5, 6, 7, 0, 1, 2}, 0);
        verify(new int[]{11, 13, 15, 17}, 11);            // not rotated: first element
        verify(new int[]{2, 1}, 1);                       // two elements, rotated
        verify(new int[]{1, 2}, 1);                       // two elements, not rotated
        verify(new int[]{5, 1, 2, 3, 4}, 1);              // rotated once
        verify(new int[]{2, 3, 4, 5, 1}, 1);              // rotated n - 1 times: minimum at the end
        verify(new int[]{7}, 7);                          // single element
        verify(new int[]{-1, -9, -5, -3}, -9);            // negatives
        verify(new int[]{Integer.MAX_VALUE}, Integer.MAX_VALUE);     // ans starts at MAX_VALUE; still correct
        verify(new int[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE);
        // every rotation of every size up to 50 must give the same answer from all three approaches
        for (int n = 1; n <= 50; n++) {
            for (int k = 0; k < n; k++) {
                int[] r = new int[n];
                for (int i = 0; i < n; i++) r[(i + k) % n] = 4 * i - 60;   // distinct, includes negatives
                verify(r, -60);
            }
        }
        System.out.println("OK P83_FindMinimumInRotatedSortedArray");
    }
}
