import java.util.*;

/** TUF 229 - Count Occurrences in a Sorted Array. How many times does x appear in a sorted array? */
public class P229_CountOccurrencesInASortedArray {

    /** Approach 1: count matches in one pass. O(n) time, O(1) space. */
    static int bruteForce(int[] arr, int x) {
        int count = 0;
        for (int v : arr) {
            if (v == x) count++;
        }
        return count;
    }

    /** Approach 2: last occurrence - first occurrence + 1, each found by binary search. O(log n) time, O(1) space. */
    static int optimal(int[] arr, int x) {
        int first = firstOccurrence(arr, x);
        if (first == -1) return 0;                       // not present at all
        return lastOccurrence(arr, x) - first + 1;
    }

    private static int firstOccurrence(int[] arr, int x) {
        int lo = 0, hi = arr.length - 1, ans = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] == x) {
                ans = mid;            // found one; keep looking for an earlier copy
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
                ans = mid;            // found one; keep looking for a later copy
                lo = mid + 1;
            } else if (arr[mid] < x) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return ans;
    }

    /** Approach 3: upperBound(x) - lowerBound(x), with no special case for "absent". O(log n) time, O(1) space. */
    static int optimalBounds(int[] arr, int x) {
        return upperBound(arr, x) - lowerBound(arr, x);
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

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int x, int expected) {
        String in = Arrays.toString(arr) + " x=" + x;
        check(bruteForce(arr, x) == expected, "bruteForce " + in);
        check(optimal(arr, x) == expected, "optimal " + in);
        check(optimalBounds(arr, x) == expected, "optimalBounds " + in);
    }

    public static void main(String[] args) {
        int[] a = {2, 2, 3, 3, 3, 3, 4};
        verify(a, 3, 4);                                   // run of four in the middle
        verify(a, 2, 2);                                   // run at the start
        verify(a, 4, 1);                                   // single copy at the end
        verify(a, 5, 0);                                   // larger than everything
        verify(a, 1, 0);                                   // smaller than everything
        verify(new int[]{}, 3, 0);                         // empty array
        verify(new int[]{7, 7, 7, 7, 7}, 7, 5);            // every element matches
        verify(new int[]{7, 7, 7, 7, 7}, 8, 0);
        verify(new int[]{-3, -3, -1, 0, 0, 0}, 0, 3);      // negatives and zero
        verify(new int[]{-3, -3, -1, 0, 0, 0}, -2, 0);     // absent, between existing values
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE, 2);
        // cross-check all three approaches for every x on a larger array with runs of varying length
        int[] big = new int[1200];
        for (int i = 0; i < big.length; i++) big[i] = (i / 11) * 2;  // runs of 11 equal values
        for (int x = -1; x <= 220; x++) {
            int e = bruteForce(big, x);
            check(optimal(big, x) == e && optimalBounds(big, x) == e, "mismatch at x=" + x);
        }
        System.out.println("OK P229_CountOccurrencesInASortedArray");
    }
}
