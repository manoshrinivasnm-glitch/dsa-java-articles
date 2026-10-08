import java.util.*;

/** TUF 2769 - Search element in a sorted and rotated array / find the pivot where it is rotated. Distinct values. */
public class P2769_SearchElementInASortedAndRotatedArrayFin {

    /** Approach 1: linear scan. O(n) time, O(1) space. */
    static int bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) return i;
        }
        return -1;
    }

    /** Index of the minimum element (the pivot, also the number of rotations). O(log n) time. */
    static int findPivot(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) lo = mid + 1;        // the drop is somewhere right of mid
            else hi = mid;                                 // mid..hi is sorted: minimum is at mid or left of it
        }
        return lo;
    }

    /** Plain binary search for target inside the sorted range nums[lo..hi]. */
    static int binarySearch(int[] nums, int lo, int hi, int target) {
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) lo = mid + 1; else hi = mid - 1;
        }
        return -1;
    }

    /** Approach 2: find the pivot, then binary search the one sorted part that can hold target. O(log n) time, O(1) space. */
    static int better(int[] nums, int target) {
        int n = nums.length;
        if (n == 0) return -1;
        int p = findPivot(nums);
        if (target >= nums[p] && target <= nums[n - 1]) return binarySearch(nums, p, n - 1, target);
        return binarySearch(nums, 0, p - 1, target);
    }

    /** Approach 3: one binary search; one half around mid is always sorted, so test target against it. O(log n) time, O(1) space. */
    static int optimal(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[lo] <= nums[mid]) {                   // left half lo..mid is sorted
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else {                                       // right half mid..hi is sorted
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int target, int expected) {
        check(bruteForce(nums, target) == expected, "bruteForce " + Arrays.toString(nums) + " t=" + target);
        check(better(nums, target) == expected, "better " + Arrays.toString(nums) + " t=" + target);
        check(optimal(nums, target) == expected, "optimal " + Arrays.toString(nums) + " t=" + target);
    }

    public static void main(String[] args) {
        verify(new int[]{4, 5, 6, 7, 0, 1, 2}, 0, 4);
        verify(new int[]{4, 5, 6, 7, 0, 1, 2}, 3, -1);    // absent
        verify(new int[]{1}, 0, -1);                      // edge: single element, absent
        verify(new int[]{1}, 1, 0);                       // edge: single element, present
        verify(new int[]{}, 5, -1);                       // edge: empty array
        verify(new int[]{3, 1}, 1, 1);
        verify(new int[]{5, 1, 3}, 5, 0);
        verify(new int[]{1, 2, 3, 4, 5}, 4, 3);           // not rotated at all
        verify(new int[]{-7, -2, 9, -20, -15}, -15, 4);   // negatives
        check(findPivot(new int[]{4, 5, 6, 7, 0, 1, 2}) == 4, "pivot of example");
        check(findPivot(new int[]{1, 2, 3}) == 0, "pivot of unrotated array");
        check(findPivot(new int[]{2, 1}) == 1, "pivot of [2, 1]");
        check(findPivot(new int[]{9}) == 0, "pivot of single element");
        // every rotation of a 10-element array, every target in and around it
        int[] base = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};
        for (int r = 0; r < base.length; r++) {
            int[] rot = new int[base.length];
            for (int i = 0; i < base.length; i++) rot[i] = base[(i + r) % base.length];
            check(findPivot(rot) == (base.length - r) % base.length, "pivot of rotation " + r);
            for (int t = -1; t <= 19; t++) verify(rot, t, bruteForce(rot, t));
        }
        System.out.println("OK P2769_SearchElementInASortedAndRotatedArrayFin");
    }
}
