import java.util.*;

/** TUF 88 - Search in rotated sorted array II. Duplicates allowed; report whether target is present. */
public class P88_SearchInRotatedSortedArrayII {

    /** Approach 1: linear scan. O(n) time, O(1) space. */
    static boolean bruteForce(int[] nums, int target) {
        for (int v : nums) {
            if (v == target) return true;
        }
        return false;
    }

    /** Approach 2: binary search on the sorted half; shrink both ends when duplicates hide which half is sorted. */
    static boolean optimal(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return true;
            if (nums[lo] == nums[mid] && nums[mid] == nums[hi]) {   // cannot tell which half is sorted
                lo++;                                                // both ends equal nums[mid] != target,
                hi--;                                                // so dropping them loses nothing
                continue;
            }
            if (nums[lo] <= nums[mid]) {                                  // left half [lo, mid] is sorted
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else {                                                      // right half [mid, hi] is sorted
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int target, boolean expected) {
        String in = Arrays.toString(nums) + " target " + target;
        check(bruteForce(nums, target) == expected, "bruteForce " + in);
        check(optimal(nums, target) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        int[] a = {2, 5, 6, 0, 0, 1, 2};
        verify(a, 0, true);                                // duplicated value in the smaller segment
        verify(a, 3, false);                               // absent, in the gap between segments
        verify(a, 2, true);                                 // equals both ends
        verify(a, 6, true);                                 // largest element
        verify(a, 7, false);                                // larger than everything
        verify(new int[]{1, 0, 1, 1, 1}, 0, true);          // classic case: nums[lo] == nums[mid] == nums[hi]
        verify(new int[]{1, 1, 1, 0, 1}, 0, true);          // minimum hidden on the other side
        verify(new int[]{1, 1, 1, 1, 1}, 1, true);          // all equal, present
        verify(new int[]{1, 1, 1, 1, 1}, 2, false);         // all equal, absent: shrinks down to nothing
        verify(new int[]{1, 2, 3, 4, 5}, 5, true);          // not rotated
        verify(new int[]{3, 1}, 1, true);                   // two elements
        verify(new int[]{1}, 1, true);                      // single element
        verify(new int[]{1}, 2, false);
        verify(new int[]{}, 4, false);                      // empty array
        verify(new int[]{-2, -1, -1, -3, -3, -2}, -3, true); // negatives with duplicates
        // cross-check every rotation of small sorted multisets against the brute force
        for (int n = 1; n <= 24; n++) {
            int[] sorted = new int[n];
            for (int i = 0; i < n; i++) sorted[i] = (i * 5) / 7;   // non-decreasing with runs of 1 or 2
            for (int k = 0; k < n; k++) {
                int[] r = new int[n];
                for (int i = 0; i < n; i++) r[(i + k) % n] = sorted[i];
                for (int t = -1; t <= sorted[n - 1] + 1; t++) {
                    check(optimal(r, t) == bruteForce(r, t), "mismatch n=" + n + " k=" + k + " t=" + t);
                }
            }
        }
        System.out.println("OK P88_SearchInRotatedSortedArrayII");
    }
}
