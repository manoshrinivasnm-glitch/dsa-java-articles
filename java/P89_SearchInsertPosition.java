import java.util.*;

/** TUF 89 - Search insert position. Index of target in a sorted array of distinct ints, or the index where it belongs. */
public class P89_SearchInsertPosition {

    /** Approach 1: walk left to right until an element >= target. O(n) time, O(1) space. */
    static int bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] >= target) return i;
        }
        return nums.length;
    }

    /** Approach 2: binary search for the lower bound of target. O(log n) time, O(1) space. */
    static int optimal(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1, ans = nums.length;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] >= target) {
                ans = mid;            // mid works; a smaller index may also work, so look left
                hi = mid - 1;
            } else {
                lo = mid + 1;         // nums[mid] < target, so target belongs after mid
            }
        }
        return ans;
    }

    /** Approach 3: classic binary search; when it ends without a hit, lo is the insert position. */
    static int optimalClassic(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return lo;                    // here hi == lo - 1: nums[hi] < target < nums[lo]
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int target, int expected) {
        String in = Arrays.toString(nums) + " target " + target;
        check(bruteForce(nums, target) == expected, "bruteForce " + in);
        check(optimal(nums, target) == expected, "optimal " + in);
        check(optimalClassic(nums, target) == expected, "optimalClassic " + in);
    }

    public static void main(String[] args) {
        int[] a = {1, 3, 5, 6};
        verify(a, 5, 2);                                   // present
        verify(a, 2, 1);                                   // absent: goes between 1 and 3
        verify(a, 7, 4);                                   // larger than everything: n
        verify(a, 0, 0);                                   // smaller than everything: 0
        verify(a, 1, 0);                                   // equal to the first element
        verify(a, 6, 3);                                   // equal to the last element
        verify(new int[]{}, 5, 0);                         // empty array: insert at 0
        verify(new int[]{1}, 0, 0);                        // single element, insert before
        verify(new int[]{1}, 1, 0);                        // single element, present
        verify(new int[]{1}, 2, 1);                        // single element, insert after
        verify(new int[]{-10, -3, 0, 8}, -4, 1);           // negatives
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, 0, 1);
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE, 1);
        // cross-check all three approaches for every target around a larger array
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = 3 * i - 500;    // distinct, spaced by 3
        for (int t = -505; t <= 2505; t++) {
            int e = bruteForce(big, t);
            check(optimal(big, t) == e && optimalClassic(big, t) == e, "mismatch at target=" + t);
        }
        System.out.println("OK P89_SearchInsertPosition");
    }
}
