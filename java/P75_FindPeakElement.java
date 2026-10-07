import java.util.*;

/** TUF 75 - Find peak element. Return the index of any element strictly greater than both neighbours (the edges count as -infinity). */
public class P75_FindPeakElement {

    /** Approach 1: linear scan, return the first element greater than both neighbours. O(n) time, O(1) space. */
    static int bruteForce(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            boolean leftOk = (i == 0) || arr[i - 1] < arr[i];
            boolean rightOk = (i == n - 1) || arr[i + 1] < arr[i];
            if (leftOk && rightOk) return i;
        }
        return -1;
    }

    /** Approach 2: binary search, always move towards the larger neighbour. O(log n) time, O(1) space. */
    static int optimal(int[] arr) {
        int n = arr.length;
        if (n == 1) return 0;
        if (arr[0] > arr[1]) return 0;
        if (arr[n - 1] > arr[n - 2]) return n - 1;
        int lo = 1, hi = n - 2;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] > arr[mid - 1] && arr[mid] > arr[mid + 1]) return mid;
            if (arr[mid] > arr[mid - 1]) lo = mid + 1;   // climbing: a peak must exist to the right
            else hi = mid - 1;                            // descending or in a valley: a peak must exist to the left
        }
        return -1;
    }

    /** Approach 3: the same search with a half-open invariant and no boundary special cases. O(log n) time, O(1) space. */
    static int optimalCompact(int[] arr) {
        int lo = 0, hi = arr.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] < arr[mid + 1]) lo = mid + 1;   // the peak is strictly to the right of mid
            else hi = mid;                                // arr[mid] > arr[mid + 1]: a peak is at mid or to its left
        }
        return lo;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static boolean isPeak(int[] arr, int i) {
        if (i < 0 || i >= arr.length) return false;
        boolean leftOk = (i == 0) || arr[i - 1] < arr[i];
        boolean rightOk = (i == arr.length - 1) || arr[i + 1] < arr[i];
        return leftOk && rightOk;
    }

    /** For arrays with several peaks: every approach must return some valid peak. */
    static void verify(int[] arr) {
        String in = Arrays.toString(arr);
        check(isPeak(arr, bruteForce(arr)), "bruteForce " + in);
        check(isPeak(arr, optimal(arr)), "optimal " + in);
        check(isPeak(arr, optimalCompact(arr)), "optimalCompact " + in);
    }

    /** For arrays with exactly one peak: every approach must return that index. */
    static void verifyExact(int[] arr, int expected) {
        String in = Arrays.toString(arr);
        check(bruteForce(arr) == expected, "bruteForce " + in);
        check(optimal(arr) == expected, "optimal " + in);
        check(optimalCompact(arr) == expected, "optimalCompact " + in);
    }

    public static void main(String[] args) {
        verifyExact(new int[]{1, 2, 3, 1}, 2);
        verifyExact(new int[]{1}, 0);                      // single element is a peak
        verifyExact(new int[]{5, 4, 3, 2, 1}, 0);          // strictly decreasing: first element
        verifyExact(new int[]{1, 2, 3, 4, 5}, 4);          // strictly increasing: last element
        verifyExact(new int[]{2, 1}, 0);
        verifyExact(new int[]{1, 2}, 1);
        verifyExact(new int[]{-3, -1, -2, -5}, 1);         // negatives
        verify(new int[]{1, 2, 1, 3, 5, 6, 4});            // two valid peaks (indices 1 and 5)
        verify(new int[]{1, 5, 1, 2, 1});                  // two valid peaks (indices 1 and 3)
        verify(new int[]{10, 20, 15, 2, 23, 90, 67});
        System.out.println("OK P75_FindPeakElement");
    }
}
