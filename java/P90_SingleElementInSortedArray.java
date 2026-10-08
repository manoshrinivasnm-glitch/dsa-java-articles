import java.util.*;

/** TUF 90 - Single element in sorted array. Every value appears twice except one; return that one. */
public class P90_SingleElementInSortedArray {

    /** Approach 1: an element is single if it differs from both neighbours. O(n) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            boolean leftDiffers = i == 0 || nums[i - 1] != nums[i];
            boolean rightDiffers = i == n - 1 || nums[i + 1] != nums[i];
            if (leftDiffers && rightDiffers) return nums[i];
        }
        throw new IllegalArgumentException("no single element");
    }

    /** Approach 2: XOR everything; equal pairs cancel to 0. O(n) time, O(1) space. */
    static int better(int[] nums) {
        int x = 0;
        for (int v : nums) x ^= v;
        return x;
    }

    /** Approach 3: binary search on pair alignment (pairs start at even indices before the single). O(log n) time, O(1) space. */
    static int optimal(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (mid % 2 == 1) mid--;                       // look at the pair that should start at an even index
            if (nums[mid] == nums[mid + 1]) lo = mid + 2;  // pairs still aligned: single is to the right
            else hi = mid;                                 // alignment already broken: single is at mid or left
        }
        return nums[lo];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        check(bruteForce(nums) == expected, "bruteForce " + Arrays.toString(nums));
        check(better(nums) == expected, "better " + Arrays.toString(nums));
        check(optimal(nums) == expected, "optimal " + Arrays.toString(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 2, 3, 3, 4, 4, 8, 8}, 2);
        verify(new int[]{3, 3, 7, 7, 10, 11, 11}, 10);
        verify(new int[]{1}, 1);                          // edge: single element array
        verify(new int[]{1, 1, 2}, 2);                    // single at the end
        verify(new int[]{0, 1, 1}, 0);                    // single at the start
        verify(new int[]{-5, -5, -3, 0, 0}, -3);          // negatives
        // every possible position of the single element in an array of 41 values
        for (int pos = 0; pos <= 20; pos++) {
            int[] a = new int[41];
            int t = 0;
            for (int v = 0; v <= 20; v++) {
                a[t++] = v * 3;
                if (v != pos) a[t++] = v * 3;
            }
            verify(a, pos * 3);
        }
        System.out.println("OK P90_SingleElementInSortedArray");
    }
}
