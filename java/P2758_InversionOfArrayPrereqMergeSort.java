import java.util.*;

/** TUF 2758 - Inversion of Array. Count pairs (i, j) with i < j and a[i] > a[j]. The input is not modified. */
public class P2758_InversionOfArrayPrereqMergeSort {

    /** Approach 1: check every pair. O(n^2) time, O(1) space. */
    static long bruteForce(int[] a) {
        long count = 0;                              // long: up to n(n-1)/2 pairs can overflow an int
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {     // every pair with i < j
                if (a[i] > a[j]) count++;                // out of order: one inversion
            }
        }
        return count;
    }

    /** Approach 2: merge sort a copy and count cross inversions while merging. O(n log n) time, O(n) space. */
    static long optimal(int[] a) {
        int[] arr = a.clone();                       // sort a copy so the input stays unchanged
        return mergeSortCount(arr, new int[arr.length], 0, arr.length - 1);   // tmp is shared by every merge
    }

    static long mergeSortCount(int[] arr, int[] tmp, int lo, int hi) {
        if (lo >= hi) return 0;                      // zero or one element: no inversions
        int mid = lo + (hi - lo) / 2;
        // inversions inside each half; both halves come back sorted
        long count = mergeSortCount(arr, tmp, lo, mid) + mergeSortCount(arr, tmp, mid + 1, hi);
        int i = lo, j = mid + 1, k = lo;             // i walks the left half, j the right, k the output
        while (i <= mid && j <= hi) {                // merge while both halves have elements
            if (arr[i] <= arr[j]) {
                tmp[k++] = arr[i++];                 // left value is not bigger: no inversion
            } else {
                count += mid - i + 1;                // arr[i..mid] are all greater than arr[j]
                tmp[k++] = arr[j++];
            }
        }
        while (i <= mid) tmp[k++] = arr[i++];        // copy whatever is left of either half
        while (j <= hi) tmp[k++] = arr[j++];
        for (k = lo; k <= hi; k++) arr[k] = tmp[k];  // write the merged run back
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, long expected) {
        int[] copy = a.clone();
        check(bruteForce(a) == expected, "bruteForce " + Arrays.toString(a));
        check(optimal(a) == expected, "optimal " + Arrays.toString(a));
        check(Arrays.equals(a, copy), "input must not be modified");
    }

    public static void main(String[] args) {
        verify(new int[]{5, 3, 2, 4, 1}, 8);
        verify(new int[]{1, 20, 6, 4, 5}, 5);
        verify(new int[]{1, 2, 3, 4, 5}, 0);                  // sorted: no inversions
        verify(new int[]{5, 4, 3, 2, 1}, 10);                 // reversed: every pair
        verify(new int[]{2, 2, 2}, 0);                        // equal values are not inversions
        verify(new int[]{}, 0);                               // edge: empty array
        verify(new int[]{42}, 0);
        verify(new int[]{-1, -5, 3, -2}, 3);                  // negatives

        Random rnd = new Random(2758);
        for (int trial = 0; trial < 300; trial++) {
            int[] a = new int[rnd.nextInt(40)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10) - 5;
            check(bruteForce(a) == optimal(a), "random " + Arrays.toString(a));
        }
        int n = 100_000;
        int[] desc = new int[n];
        for (int i = 0; i < n; i++) desc[i] = n - i;
        check(optimal(desc) == (long) n * (n - 1) / 2, "large reversed input needs long");
        System.out.println("OK P2758_InversionOfArrayPrereqMergeSort");
    }
}
