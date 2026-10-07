import java.util.*;

/** TUF 80 - Lower Bound. Smallest index i with arr[i] >= x in a sorted array; returns n when every element is smaller. */
public class P80_LowerBound {

    /** Approach 1: scan from the left until an element >= x shows up. O(n) time, O(1) space. */
    static int bruteForce(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= x) return i;
        }
        return arr.length;
    }

    /** Approach 2: binary search that remembers the best candidate seen so far. O(log n) time, O(1) space. */
    static int optimal(int[] arr, int x) {
        int lo = 0, hi = arr.length - 1, ans = arr.length;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] >= x) {
                ans = mid;            // mid qualifies; a smaller qualifying index may exist on the left
                hi = mid - 1;
            } else {
                lo = mid + 1;         // arr[mid] < x, so every index <= mid is too small
            }
        }
        return ans;
    }

    /** Approach 3: binary search on the half-open range [lo, hi); the pointers meet exactly at the answer. */
    static int optimalHalfOpen(int[] arr, int x) {
        int lo = 0, hi = arr.length;      // invariant: the answer lies in [lo, hi]
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] >= x) hi = mid;  // mid itself may be the answer, so keep it inside the range
            else lo = mid + 1;            // mid is too small, so exclude it
        }
        return lo;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int x, int expected) {
        String in = Arrays.toString(arr) + " x=" + x;
        check(bruteForce(arr, x) == expected, "bruteForce " + in);
        check(optimal(arr, x) == expected, "optimal " + in);
        check(optimalHalfOpen(arr, x) == expected, "optimalHalfOpen " + in);
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 2, 3, 3, 5};
        verify(a, 2, 1);                                   // first 2 is at index 1
        verify(a, 3, 3);                                   // first 3 is at index 3
        verify(a, 4, 5);                                   // 4 is absent: first element >= 4 is 5 at index 5
        verify(a, 5, 5);
        verify(a, 6, 6);                                   // larger than everything: n
        verify(a, 0, 0);                                   // smaller than everything: 0
        verify(a, 1, 0);
        verify(new int[]{}, 7, 0);                         // empty array: n == 0
        verify(new int[]{4}, 4, 0);                        // single element, equal
        verify(new int[]{4}, 9, 1);                        // single element, too small
        verify(new int[]{4}, 1, 0);                        // single element, already >= x
        verify(new int[]{-5, -2, -2, 0}, -2, 1);           // negatives
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE, 1);
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MIN_VALUE, 0);
        // cross-check all three approaches for every x on a larger array with runs of duplicates
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = (i / 3) * 2;   // 0,0,0,2,2,2,4,4,4,...
        for (int x = -1; x <= 670; x++) {
            int e = bruteForce(big, x);
            check(optimal(big, x) == e && optimalHalfOpen(big, x) == e, "mismatch at x=" + x);
        }
        System.out.println("OK P80_LowerBound");
    }
}
