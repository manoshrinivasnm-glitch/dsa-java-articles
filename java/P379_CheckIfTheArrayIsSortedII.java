import java.util.*;

/** TUF 379 - Check if the Array is Sorted II. Is nums some rotation (possibly by zero) of a non-decreasing array? */
public class P379_CheckIfTheArrayIsSortedII {

    /** Warm-up: is the array sorted in non-decreasing order? O(n) time, O(1) space. */
    static boolean isSorted(int[] nums) {
        for (int i = 1; i < nums.length; i++) {      // compare each element with the one before it
            if (nums[i] < nums[i - 1]) return false; // a descent: not sorted
        }
        return true;
    }

    /** Approach 1: try every rotation amount and test whether that rotation is sorted. O(n^2) time, O(1) space. */
    static boolean bruteForce(int[] nums) {
        int n = nums.length;
        if (n == 0) return true;                     // an empty array counts as sorted
        for (int shift = 0; shift < n; shift++) {    // try the rotation that starts at index shift
            boolean ok = true;
            for (int i = 1; i < n && ok; i++) {      // walk that rotation in order; % n wraps around
                // this rotation has a descent
                if (nums[(shift + i) % n] < nums[(shift + i - 1) % n]) ok = false;
            }
            if (ok) return true;                     // found a sorted rotation
        }
        return false;                                // no rotation is sorted
    }

    /** Approach 2: find the single descent, then verify the wrap-around edge. O(n) time, O(1) space. */
    static boolean better(int[] nums) {
        int n = nums.length;
        int drop = -1;                               // index i with nums[i] < nums[i - 1]
        for (int i = 1; i < n; i++) {
            if (nums[i] < nums[i - 1]) {             // a descent between i - 1 and i
                if (drop != -1) return false;        // a second descent cannot come from one rotation
                drop = i;
            }
        }
        if (drop == -1) return true;                 // already sorted: rotation by zero
        return nums[n - 1] <= nums[0];               // the edge from last back to first must not descend either
    }

    /** Approach 3: count the descents of the circular sequence; a rotated sorted array has at most one. O(n) time, O(1) space. */
    static boolean optimal(int[] nums) {
        int n = nums.length, drops = 0;              // drops counts descents around the circle
        for (int i = 0; i < n; i++) {                // i = n - 1 also checks the wrap edge back to 0
            if (nums[i] > nums[(i + 1) % n]) drops++;
        }
        return drops <= 1;                           // a rotated sorted array has at most one descent
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, boolean expected) {
        String s = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce " + s);
        check(better(nums) == expected, "better " + s);
        check(optimal(nums) == expected, "optimal " + s);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 4, 5, 1, 2}, true);          // [1, 2, 3, 4, 5] rotated left by 2
        verify(new int[]{2, 1, 3, 4}, false);            // two descents
        verify(new int[]{1, 2, 3}, true);                // rotation by zero
        verify(new int[]{1, 1, 1}, true);                // all equal
        verify(new int[]{2, 1, 2}, true);                // rotation of [1, 2, 2]: duplicates across the wrap
        verify(new int[]{6, 10, 6}, true);               // rotation of [6, 6, 10]
        verify(new int[]{1, 3, 2}, false);               // one descent, but the wrap edge 2 -> 1 descends too
        verify(new int[]{1, 2, 3, 4, 3}, false);         // descends at the end and again on the wrap
        verify(new int[]{5}, true);                      // single element
        verify(new int[]{}, true);                       // empty
        verify(new int[]{2, 1}, true);                   // rotation of [1, 2]
        verify(new int[]{-2, -1, -5, -3}, true);         // negatives, rotation of [-5, -3, -2, -1]
        // warm-up
        check(isSorted(new int[]{1, 2, 2, 3}), "isSorted ascending with duplicate");
        check(!isSorted(new int[]{3, 4, 5, 1, 2}), "isSorted: rotated is not sorted");
        check(isSorted(new int[]{}), "isSorted empty");
        check(isSorted(new int[]{9}), "isSorted single");
        check(!isSorted(new int[]{2, 1}), "isSorted descending pair");
        System.out.println("OK P379_CheckIfTheArrayIsSortedII");
    }
}
