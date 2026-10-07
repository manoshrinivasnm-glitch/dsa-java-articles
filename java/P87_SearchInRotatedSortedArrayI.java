import java.util.*;

/** TUF 87 - Search in rotated sorted array I. Distinct values; return the index of target, or -1 if absent. */
public class P87_SearchInRotatedSortedArrayI {

    /** Approach 1: linear scan that ignores the structure entirely. O(n) time, O(1) space. */
    static int bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) return i;
        }
        return -1;
    }

    /** Approach 2: find the rotation point, then binary search the sorted segment that can hold target. */
    static int better(int[] nums, int target) {
        int n = nums.length;
        if (n == 0) return -1;
        // step 1: index of the smallest element (the rotation point)
        int lo = 0, hi = n - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) lo = mid + 1;   // the drop is to the right of mid
            else hi = mid;                            // nums[mid] < nums[hi]: minimum is mid or left of it
        }
        int pivot = lo;
        // step 2: choose the sorted segment whose value range contains target
        if (target >= nums[pivot] && target <= nums[n - 1]) {
            lo = pivot; hi = n - 1;
        } else {
            lo = 0; hi = pivot - 1;
        }
        // step 3: ordinary binary search inside [lo, hi]
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    /** Approach 3: one binary search; at every step one half is sorted and decides where target can be. */
    static int optimal(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[lo] <= nums[mid]) {                                  // left half [lo, mid] is sorted
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;   // target fits in the left half
                else lo = mid + 1;                                        // otherwise it must be on the right
            } else {                                                      // right half [mid, hi] is sorted
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;   // target fits in the right half
                else hi = mid - 1;                                        // otherwise it must be on the left
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int target, int expected) {
        String in = Arrays.toString(nums) + " target " + target;
        check(bruteForce(nums, target) == expected, "bruteForce " + in);
        check(better(nums, target) == expected, "better " + in);
        check(optimal(nums, target) == expected, "optimal " + in);
    }

    static int[] rotated(int n, int k) {           // sorted array 0, 3, 6, ... rotated right k times
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[(i + k) % n] = 3 * i;
        return a;
    }

    public static void main(String[] args) {
        int[] a = {4, 5, 6, 7, 0, 1, 2};
        verify(a, 0, 4);                                   // in the right (smaller) segment
        verify(a, 3, -1);                                  // absent, inside the "gap" between segments
        verify(a, 4, 0);                                   // first element
        verify(a, 2, 6);                                   // last element
        verify(a, 7, 3);                                   // largest element, right before the drop
        verify(a, 8, -1);                                  // larger than everything
        verify(a, -1, -1);                                 // smaller than everything
        verify(new int[]{1, 2, 3, 4, 5}, 4, 3);            // not rotated at all
        verify(new int[]{5, 1, 2, 3, 4}, 5, 0);            // rotated once
        verify(new int[]{2, 3, 4, 5, 1}, 1, 4);            // rotated n - 1 times
        verify(new int[]{3, 1}, 1, 1);                     // two elements
        verify(new int[]{3, 1}, 3, 0);
        verify(new int[]{3, 1}, 2, -1);
        verify(new int[]{1}, 1, 0);                        // single element
        verify(new int[]{1}, 0, -1);
        verify(new int[]{}, 7, -1);                        // empty array
        verify(new int[]{-5, -3, 9, -9, -7}, -7, 4);       // negatives
        // cross-check every rotation of a few sizes against the brute force, for present and absent targets
        for (int n = 1; n <= 40; n++) {
            for (int k = 0; k < n; k++) {
                int[] r = rotated(n, k);
                for (int t = -1; t <= 3 * n; t++) {
                    int e = bruteForce(r, t);
                    check(better(r, t) == e && optimal(r, t) == e, "mismatch n=" + n + " k=" + k + " t=" + t);
                }
            }
        }
        System.out.println("OK P87_SearchInRotatedSortedArrayI");
    }
}
