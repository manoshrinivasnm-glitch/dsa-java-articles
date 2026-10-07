import java.util.*;

/** TUF 84 - Find out how many times the array is rotated. Distinct values; the count equals the index of the minimum. */
public class P84_FindOutHowManyTimesTheArrayIsRotated {

    /** Approach 1: linear scan for the index of the smallest element. O(n) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int minIdx = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < nums[minIdx]) minIdx = i;
        }
        return minIdx;
    }

    /** Approach 2: binary search; each step records the index of the sorted half's minimum and discards that half. */
    static int optimal(int[] nums) {
        int lo = 0, hi = nums.length - 1, minIdx = 0;   // index 0 is a valid first candidate
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[lo] <= nums[hi]) {                   // whole range sorted: nums[lo] is its minimum
                if (nums[lo] < nums[minIdx]) minIdx = lo;
                break;
            }
            if (nums[lo] <= nums[mid]) {                  // left half sorted: nums[lo] is its minimum, drop it
                if (nums[lo] < nums[minIdx]) minIdx = lo;
                lo = mid + 1;
            } else {                                      // right half sorted: nums[mid] is its minimum, drop it
                if (nums[mid] < nums[minIdx]) minIdx = mid;
                hi = mid - 1;
            }
        }
        return minIdx;
    }

    /** Approach 3: compare mid with the right end; lo and hi converge on the minimum's index. O(log n) time. */
    static int optimalCompareRight(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) lo = mid + 1;       // the drop is strictly right of mid
            else hi = mid;                                // nums[mid] < nums[hi]: minimum is mid or left of it
        }
        return lo;
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
        verify(new int[]{3, 4, 5, 1, 2}, 3);
        verify(new int[]{4, 5, 6, 7, 0, 1, 2}, 4);
        verify(new int[]{1, 2, 3, 4, 5}, 0);              // not rotated
        verify(new int[]{2, 1}, 1);                       // two elements, rotated once
        verify(new int[]{1, 2}, 0);                       // two elements, not rotated
        verify(new int[]{5, 1, 2, 3, 4}, 1);              // rotated once
        verify(new int[]{2, 3, 4, 5, 1}, 4);              // rotated n - 1 times: minimum at the end
        verify(new int[]{7}, 0);                          // single element
        verify(new int[]{-1, -9, -5, -3}, 1);             // negatives
        verify(new int[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE}, 2);
        // every rotation count k of every size up to 50 must be recovered exactly
        for (int n = 1; n <= 50; n++) {
            for (int k = 0; k < n; k++) {
                int[] r = new int[n];
                for (int i = 0; i < n; i++) r[(i + k) % n] = 4 * i - 60;   // distinct, includes negatives
                verify(r, k);
            }
        }
        System.out.println("OK P84_FindOutHowManyTimesTheArrayIsRotated");
    }
}
