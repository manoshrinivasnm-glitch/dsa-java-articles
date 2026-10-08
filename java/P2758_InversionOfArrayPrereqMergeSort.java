import java.util.*;

/** TUF 2758 - Inversion of Array. Count pairs (i, j) with i < j and a[i] > a[j]. The input is not modified. */
public class P2758_InversionOfArrayPrereqMergeSort {

    /** Approach 1: check every pair. O(n^2) time, O(1) space. */
    static long bruteForce(int[] a) {
        long count = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[i] > a[j]) count++;
            }
        }
        return count;
    }

    /** Approach 2: merge sort a copy and count cross inversions while merging. O(n log n) time, O(n) space. */
    static long optimal(int[] a) {
        int[] arr = a.clone();
        return mergeSortCount(arr, new int[arr.length], 0, arr.length - 1);
    }

    static long mergeSortCount(int[] arr, int[] tmp, int lo, int hi) {
        if (lo >= hi) return 0;
        int mid = lo + (hi - lo) / 2;
        long count = mergeSortCount(arr, tmp, lo, mid) + mergeSortCount(arr, tmp, mid + 1, hi);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            if (arr[i] <= arr[j]) {
                tmp[k++] = arr[i++];
            } else {
                count += mid - i + 1;                // arr[i..mid] are all greater than arr[j]
                tmp[k++] = arr[j++];
            }
        }
        while (i <= mid) tmp[k++] = arr[i++];
        while (j <= hi) tmp[k++] = arr[j++];
        for (k = lo; k <= hi; k++) arr[k] = tmp[k];
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
