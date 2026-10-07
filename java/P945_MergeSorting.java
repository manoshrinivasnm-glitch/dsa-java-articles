import java.util.*;

/** TUF 945 - Merge Sort. Sort an integer array in non-decreasing order by divide and conquer. */
public class P945_MergeSorting {

    /** Approach 1: textbook merge sort that copies each half into its own array before merging. O(n log n) time, O(n) live memory. */
    static void mergeSortCopying(int[] arr) {
        if (arr.length < 2) return;
        int mid = arr.length / 2;
        int[] left = Arrays.copyOfRange(arr, 0, mid);
        int[] right = Arrays.copyOfRange(arr, mid, arr.length);
        mergeSortCopying(left);
        mergeSortCopying(right);
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) arr[k++] = left[i++]; else arr[k++] = right[j++];
        }
        while (i < left.length) arr[k++] = left[i++];
        while (j < right.length) arr[k++] = right[j++];
    }

    /** Approach 2: standard index-based merge sort with one shared temporary buffer. O(n log n) time, O(n) extra space. */
    static void mergeSort(int[] arr) {
        if (arr.length < 2) return;
        int[] tmp = new int[arr.length];
        mergeSort(arr, tmp, 0, arr.length - 1);
    }

    static void mergeSort(int[] arr, int[] tmp, int lo, int hi) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(arr, tmp, lo, mid);
        mergeSort(arr, tmp, mid + 1, hi);
        merge(arr, tmp, lo, mid, hi);
    }

    static void merge(int[] arr, int[] tmp, int lo, int mid, int hi) {
        int i = lo, j = mid + 1, k = lo;         // left run arr[lo..mid], right run arr[mid+1..hi]
        while (i <= mid && j <= hi) {
            if (arr[i] <= arr[j]) tmp[k++] = arr[i++]; else tmp[k++] = arr[j++];
        }
        while (i <= mid) tmp[k++] = arr[i++];
        while (j <= hi) tmp[k++] = arr[j++];
        for (k = lo; k <= hi; k++) arr[k] = tmp[k];
    }

    /** Approach 3: bottom-up merge sort, merging runs of width 1, 2, 4, ... with no recursion. O(n log n) time, O(n) extra space. */
    static void mergeSortBottomUp(int[] arr) {
        int n = arr.length;
        if (n < 2) return;
        int[] tmp = new int[n];
        for (int width = 1; width < n; width *= 2) {
            for (int lo = 0; lo < n - width; lo += 2 * width) {
                int mid = lo + width - 1;
                int hi = Math.min(lo + 2 * width - 1, n - 1);
                merge(arr, tmp, lo, mid, hi);
            }
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone();
        mergeSortCopying(a);
        check(Arrays.equals(a, expected), "mergeSortCopying failed on " + Arrays.toString(input) + " got " + Arrays.toString(a));
        int[] b = input.clone();
        mergeSort(b);
        check(Arrays.equals(b, expected), "mergeSort failed on " + Arrays.toString(input) + " got " + Arrays.toString(b));
        int[] c = input.clone();
        mergeSortBottomUp(c);
        check(Arrays.equals(c, expected), "mergeSortBottomUp failed on " + Arrays.toString(input) + " got " + Arrays.toString(c));
    }

    static void verify(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);
        verify(input, expected);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 1, 2, 4, 1, 5, 2, 6, 4}, new int[]{1, 1, 2, 2, 3, 4, 4, 5, 6});
        verify(new int[]{5, 4, 4, 1, 1}, new int[]{1, 1, 4, 4, 5});                       // duplicates, odd length
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5});                       // already sorted
        verify(new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5});                       // reverse sorted
        verify(new int[]{-3, 0, -7, 2}, new int[]{-7, -3, 0, 2});                         // negatives, even length
        verify(new int[]{7}, new int[]{7});                                               // single element
        verify(new int[]{}, new int[]{});                                                 // empty
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        Random rnd = new Random(42);
        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(2_000_001) - 1_000_000;
        verify(big);                                                                      // 100000 random values, fixed seed
        int[] odd = new int[12_345];
        for (int i = 0; i < odd.length; i++) odd[i] = rnd.nextInt(100);
        verify(odd);                                                                      // length that is not a power of two, many duplicates
        System.out.println("OK P945_MergeSorting");
    }
}
