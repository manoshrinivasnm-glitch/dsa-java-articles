import java.util.*;

/** TUF 85 - First and last occurrence. Indices of the first and last x in a sorted array, or {-1, -1} if absent. */
public class P85_FirstAndLastOccurrence {

    /** Approach 1: linear scan that records the first and the last hit. O(n) time, O(1) space. */
    static int[] bruteForce(int[] arr, int x) {
        int first = -1, last = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == x) {
                if (first == -1) first = i;   // only the first hit sets this
                last = i;                     // every hit moves this forward
            }
        }
        return new int[]{first, last};
    }

    /** Approach 2: lower bound is the first index, upper bound minus one is the last. O(log n) time, O(1) space. */
    static int[] optimalBounds(int[] arr, int x) {
        int n = arr.length;
        int first = lowerBound(arr, x);
        if (first == n || arr[first] != x) return new int[]{-1, -1};   // x is not in the array at all
        int last = upperBound(arr, x) - 1;
        return new int[]{first, last};
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

    private static int upperBound(int[] arr, int x) {     // first index with arr[i] > x, or n
        int lo = 0, hi = arr.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] > x) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    /** Approach 3: two purpose-built binary searches that only remember indices equal to x. O(log n) time. */
    static int[] optimalDirect(int[] arr, int x) {
        int first = firstOccurrence(arr, x);
        if (first == -1) return new int[]{-1, -1};
        return new int[]{first, lastOccurrence(arr, x)};
    }

    private static int firstOccurrence(int[] arr, int x) {
        int lo = 0, hi = arr.length - 1, ans = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] == x) {
                ans = mid;            // found one; an earlier copy may still exist on the left
                hi = mid - 1;
            } else if (arr[mid] < x) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return ans;
    }

    private static int lastOccurrence(int[] arr, int x) {
        int lo = 0, hi = arr.length - 1, ans = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] == x) {
                ans = mid;            // found one; a later copy may still exist on the right
                lo = mid + 1;
            } else if (arr[mid] < x) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int x, int first, int last) {
        String in = Arrays.toString(arr) + " x=" + x;
        int[] expected = {first, last};
        check(Arrays.equals(bruteForce(arr, x), expected), "bruteForce " + in);
        check(Arrays.equals(optimalBounds(arr, x), expected), "optimalBounds " + in);
        check(Arrays.equals(optimalDirect(arr, x), expected), "optimalDirect " + in);
    }

    public static void main(String[] args) {
        int[] a = {5, 7, 7, 8, 8, 10};
        verify(a, 8, 3, 4);                                // a run of two
        verify(a, 7, 1, 2);
        verify(a, 6, -1, -1);                              // absent, between existing values
        verify(a, 5, 0, 0);                                // single copy at the start
        verify(a, 10, 5, 5);                               // single copy at the end
        verify(a, 11, -1, -1);                             // larger than everything
        verify(a, 0, -1, -1);                              // smaller than everything
        verify(new int[]{}, 3, -1, -1);                    // empty array
        verify(new int[]{2, 2, 2, 2}, 2, 0, 3);            // the whole array is one run
        verify(new int[]{2, 2, 2, 2}, 3, -1, -1);
        verify(new int[]{-4, -4, -1, 0}, -4, 0, 1);        // negatives
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE, 1, 2);
        // cross-check all three approaches for every x on a larger array with runs of varying length
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = (i / 7) * 3;   // runs of 7 equal values
        for (int x = -2; x <= 430; x++) {
            int[] e = bruteForce(big, x);
            check(Arrays.equals(optimalBounds(big, x), e) && Arrays.equals(optimalDirect(big, x), e), "mismatch at x=" + x);
        }
        System.out.println("OK P85_FirstAndLastOccurrence");
    }
}
