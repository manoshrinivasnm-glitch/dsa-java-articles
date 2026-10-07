import java.util.*;

/** TUF 81 - Search X in sorted array. Return an index i with nums[i] == target, or -1 when target is absent. */
public class P81_SearchXInSortedArray {

    /** Approach 1: scan from the left; stop early once the values pass target. O(n) time, O(1) space. */
    static int bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) return i;
            if (nums[i] > target) return -1;     // sorted: nothing after this index can equal target
        }
        return -1;
    }

    /** Approach 2: iterative binary search. O(log n) time, O(1) space. */
    static int optimal(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;         // never overflows, unlike (lo + hi) / 2
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) lo = mid + 1; // target can only be to the right of mid
            else hi = mid - 1;                    // target can only be to the left of mid
        }
        return -1;
    }

    /** Approach 3: recursive binary search. O(log n) time, O(log n) stack space. */
    static int optimalRecursive(int[] nums, int target) {
        return search(nums, 0, nums.length - 1, target);
    }

    private static int search(int[] nums, int lo, int hi, int target) {
        if (lo > hi) return -1;                   // empty range: target is not here
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[mid] < target) return search(nums, mid + 1, hi, target);
        return search(nums, lo, mid - 1, target);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int target, int expected) {
        String in = Arrays.toString(nums) + " target " + target;
        check(bruteForce(nums, target) == expected, "bruteForce " + in);
        check(optimal(nums, target) == expected, "optimal " + in);
        check(optimalRecursive(nums, target) == expected, "optimalRecursive " + in);
    }

    static void verifyAnyIndex(int[] nums, int target) {
        for (int r : new int[]{bruteForce(nums, target), optimal(nums, target), optimalRecursive(nums, target)}) {
            check(r >= 0 && r < nums.length && nums[r] == target, "bad index " + r + " for " + Arrays.toString(nums));
        }
    }

    public static void main(String[] args) {
        int[] a = {-1, 0, 3, 5, 9, 12};
        verify(a, 9, 4);
        verify(a, 2, -1);                                       // absent, in the middle of the range
        verify(a, -1, 0);                                       // first element
        verify(a, 12, 5);                                       // last element
        verify(a, 100, -1);                                     // larger than everything
        verify(a, -50, -1);                                     // smaller than everything
        verify(new int[]{7}, 7, 0);                             // single element, present
        verify(new int[]{7}, 3, -1);                            // single element, absent
        verify(new int[]{}, 1, -1);                             // empty array
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE, 1);
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MIN_VALUE, 0);
        verifyAnyIndex(new int[]{1, 2, 2, 2, 3}, 2);           // duplicates: any matching index is acceptable
        // large array: every sampled element is found at its own index, odd numbers are never present
        int n = 100_000;
        int[] big = new int[n];
        for (int i = 0; i < n; i++) big[i] = 2 * i;
        for (int i = 0; i < n; i += 997) verify(big, 2 * i, i);
        verify(big, 1, -1);
        verify(big, 2 * n - 1, -1);
        System.out.println("OK P81_SearchXInSortedArray");
    }
}
