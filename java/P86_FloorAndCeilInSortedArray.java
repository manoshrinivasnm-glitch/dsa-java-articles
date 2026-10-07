import java.util.*;

/** TUF 86 - Floor and Ceil in Sorted Array. floor = largest value <= x, ceil = smallest value >= x; -1 when missing. */
public class P86_FloorAndCeilInSortedArray {

    /** Approach 1: one linear pass that tracks both answers. O(n) time, O(1) space. */
    static int[] bruteForce(int[] arr, int x) {
        int floor = -1, ceil = -1;
        for (int v : arr) {
            if (v <= x) floor = v;             // values only grow, so the last one <= x is the floor
            if (v >= x) { ceil = v; break; }   // the first value >= x is the smallest such value
        }
        return new int[]{floor, ceil};
    }

    /** Approach 2: two independent binary searches, one per answer. O(log n) time, O(1) space. */
    static int[] optimal(int[] arr, int x) {
        return new int[]{floorBinarySearch(arr, x), ceilBinarySearch(arr, x)};
    }

    private static int floorBinarySearch(int[] arr, int x) {
        int lo = 0, hi = arr.length - 1, ans = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] <= x) {
                ans = arr[mid];       // candidate; a larger qualifying value may exist on the right
                lo = mid + 1;
            } else {
                hi = mid - 1;         // arr[mid] > x, so everything from mid onward is too big
            }
        }
        return ans;
    }

    private static int ceilBinarySearch(int[] arr, int x) {
        int lo = 0, hi = arr.length - 1, ans = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] >= x) {
                ans = arr[mid];       // candidate; a smaller qualifying value may exist on the left
                hi = mid - 1;
            } else {
                lo = mid + 1;         // arr[mid] < x, so everything up to mid is too small
            }
        }
        return ans;
    }

    /** Approach 3: a single lower-bound search yields both answers. O(log n) time, O(1) space. */
    static int[] optimalLowerBound(int[] arr, int x) {
        int n = arr.length;
        int lb = 0, hi = n;                           // lower bound: first index with arr[idx] >= x, or n
        while (lb < hi) {
            int mid = lb + (hi - lb) / 2;
            if (arr[mid] >= x) hi = mid;
            else lb = mid + 1;
        }
        int ceil = lb < n ? arr[lb] : -1;
        int floor;
        if (lb < n && arr[lb] == x) floor = x;        // x itself is present, so it is its own floor
        else floor = lb > 0 ? arr[lb - 1] : -1;       // every index before lb holds a value < x
        return new int[]{floor, ceil};
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int x, int floor, int ceil) {
        String in = Arrays.toString(arr) + " x=" + x;
        int[] expected = {floor, ceil};
        check(Arrays.equals(bruteForce(arr, x), expected), "bruteForce " + in);
        check(Arrays.equals(optimal(arr, x), expected), "optimal " + in);
        check(Arrays.equals(optimalLowerBound(arr, x), expected), "optimalLowerBound " + in);
    }

    public static void main(String[] args) {
        int[] a = {3, 4, 4, 7, 8, 10};
        verify(a, 5, 4, 7);                                // strictly between two values
        verify(a, 4, 4, 4);                                // present (and duplicated): both are x
        verify(a, 8, 8, 8);                                // present once
        verify(a, 2, -1, 3);                               // below everything: no floor
        verify(a, 11, 10, -1);                             // above everything: no ceil
        verify(a, 3, 3, 3);                                // equal to the first element
        verify(a, 10, 10, 10);                             // equal to the last element
        verify(new int[]{}, 5, -1, -1);                    // empty array
        verify(new int[]{6}, 6, 6, 6);                     // single element, equal
        verify(new int[]{6}, 1, -1, 6);                    // single element, x below it
        verify(new int[]{6}, 9, 6, -1);                    // single element, x above it
        verify(new int[]{0, 0, 0}, 0, 0, 0);               // zeros: a value of 0 is a real answer, not "missing"
        verify(new int[]{1, 2, 3}, Integer.MAX_VALUE, 3, -1);
        // cross-check all three approaches on a larger array for every x in range
        int[] big = new int[600];
        for (int i = 0; i < big.length; i++) big[i] = (i / 2) * 5;   // 0,0,5,5,10,10,...,1495,1495
        for (int x = -3; x <= 1500; x++) {
            int[] e = bruteForce(big, x);
            check(Arrays.equals(optimal(big, x), e) && Arrays.equals(optimalLowerBound(big, x), e), "mismatch at x=" + x);
        }
        System.out.println("OK P86_FloorAndCeilInSortedArray");
    }
}
