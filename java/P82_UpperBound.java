import java.util.*;

/** TUF 82 - Upper Bound. Smallest index i with arr[i] > x in a sorted array; returns n when no element is larger. */
public class P82_UpperBound {

    /** Approach 1: scan from the left until an element > x shows up. O(n) time, O(1) space. */
    static int bruteForce(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > x) return i;
        }
        return arr.length;
    }

    /** Approach 2: binary search that remembers the best candidate seen so far. O(log n) time, O(1) space. */
    static int optimal(int[] arr, int x) {
        int lo = 0, hi = arr.length - 1, ans = arr.length;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] > x) {
                ans = mid;            // mid qualifies; a smaller qualifying index may exist on the left
                hi = mid - 1;
            } else {
                lo = mid + 1;         // arr[mid] <= x, so every index <= mid is ruled out
            }
        }
        return ans;
    }

    /** Approach 3: for integers, upperBound(x) == lowerBound(x + 1). O(log n) time, O(1) space. */
    static int viaLowerBound(int[] arr, int x) {
        if (x == Integer.MAX_VALUE) return arr.length;   // no int is greater than x, and x + 1 would overflow
        return lowerBound(arr, x + 1);
    }

    private static int lowerBound(int[] arr, int x) {     // first index with arr[i] >= x, or n
        int lo = 0, hi = arr.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] >= x) hi = mid;
            else lo = mid + 1;
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
        check(viaLowerBound(arr, x) == expected, "viaLowerBound " + in);
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 2, 3, 3, 5};
        verify(a, 2, 3);                                   // the 2s occupy indices 1..2, so the answer is 3
        verify(a, 3, 5);                                   // first element > 3 is 5 at index 5
        verify(a, 4, 5);                                   // 4 is absent: same answer as for 3
        verify(a, 5, 6);                                   // nothing is larger than 5: n
        verify(a, 0, 0);                                   // everything is larger: 0
        verify(a, 1, 1);
        verify(new int[]{}, 7, 0);                         // empty array: n == 0
        verify(new int[]{4}, 4, 1);                        // single element, equal is not enough
        verify(new int[]{4}, 9, 1);
        verify(new int[]{4}, 1, 0);
        verify(new int[]{-5, -2, -2, 0}, -2, 3);           // negatives
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE, 2);   // x + 1 would overflow
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MIN_VALUE, 1);
        // cross-check all three approaches for every x on a larger array with runs of duplicates
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = (i / 3) * 2;   // 0,0,0,2,2,2,4,4,4,...
        for (int x = -1; x <= 670; x++) {
            int e = bruteForce(big, x);
            check(optimal(big, x) == e && viaLowerBound(big, x) == e, "mismatch at x=" + x);
        }
        System.out.println("OK P82_UpperBound");
    }
}
