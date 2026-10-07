import java.util.*;

/** TUF 947 - Selection Sort. Sort an integer array in non-decreasing order by repeatedly selecting the minimum. */
public class P947_SelectionSort {

    /** Approach 1: repeatedly pick the smallest unused element into a fresh array. O(n^2) time, O(n) extra space. */
    static void selectionSortExtraSpace(int[] arr) {
        int n = arr.length;
        boolean[] used = new boolean[n];
        int[] out = new int[n];
        for (int k = 0; k < n; k++) {
            int minIdx = -1;
            for (int i = 0; i < n; i++) {
                if (!used[i] && (minIdx == -1 || arr[i] < arr[minIdx])) minIdx = i;
            }
            used[minIdx] = true;
            out[k] = arr[minIdx];
        }
        System.arraycopy(out, 0, arr, 0, n);
    }

    /** Approach 2: classic in-place selection sort. O(n^2) comparisons, at most n-1 swaps, O(1) extra space. */
    static void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            if (minIdx != i) {
                int tmp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = tmp;
            }
        }
    }

    /** Approach 3: stable variant, shift the gap instead of swapping so equal keys keep their order. O(n^2) time, O(1) extra space. */
    static void selectionSortStable(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            int min = arr[minIdx];
            for (int k = minIdx; k > i; k--) arr[k] = arr[k - 1];
            arr[i] = min;
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone();
        selectionSortExtraSpace(a);
        check(Arrays.equals(a, expected), "selectionSortExtraSpace failed on " + Arrays.toString(input) + " got " + Arrays.toString(a));
        int[] b = input.clone();
        selectionSort(b);
        check(Arrays.equals(b, expected), "selectionSort failed on " + Arrays.toString(input) + " got " + Arrays.toString(b));
        int[] c = input.clone();
        selectionSortStable(c);
        check(Arrays.equals(c, expected), "selectionSortStable failed on " + Arrays.toString(input) + " got " + Arrays.toString(c));
    }

    static void verify(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);
        verify(input, expected);
    }

    public static void main(String[] args) {
        verify(new int[]{13, 46, 24, 52, 20, 9}, new int[]{9, 13, 20, 24, 46, 52});
        verify(new int[]{5, 4, 4, 1, 1}, new int[]{1, 1, 4, 4, 5});                       // duplicates
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5});                       // already sorted
        verify(new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5});                       // reverse sorted
        verify(new int[]{-3, 0, -7, 2}, new int[]{-7, -3, 0, 2});                         // negatives
        verify(new int[]{7}, new int[]{7});                                               // single element
        verify(new int[]{}, new int[]{});                                                 // empty
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        Random rnd = new Random(42);
        int[] big = new int[2000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(2001) - 1000;
        verify(big);                                                                      // 2000 random values, fixed seed
        System.out.println("OK P947_SelectionSort");
    }
}
