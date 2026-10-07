import java.util.*;

/** TUF 881 - Recursive Bubble Sort. Sort an integer array in non-decreasing order, replacing the outer loop of bubble sort with recursion. */
public class P881_RecursiveBubbleSort {

    /** Approach 1: one pass over the first n elements parks the maximum at index n-1, then recurse on n-1. O(n^2) time, O(n) stack. */
    static void recursiveBubbleSort(int[] arr) {
        recursiveBubbleSort(arr, arr.length);
    }

    static void recursiveBubbleSort(int[] arr, int n) {
        if (n <= 1) return;
        for (int j = 0; j < n - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                int tmp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = tmp;
            }
        }
        recursiveBubbleSort(arr, n - 1);
    }

    /** Approach 2: same recursion, but stop as soon as a pass makes no swap. O(n) best case, O(n^2) worst case. */
    static void recursiveBubbleSortEarlyExit(int[] arr) {
        recursiveBubbleSortEarlyExit(arr, arr.length);
    }

    static void recursiveBubbleSortEarlyExit(int[] arr, int n) {
        if (n <= 1) return;
        boolean swapped = false;
        for (int j = 0; j < n - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                int tmp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = tmp;
                swapped = true;
            }
        }
        if (!swapped) return;
        recursiveBubbleSortEarlyExit(arr, n - 1);
    }

    /** Approach 3: no loops at all; the inner pass is itself a recursion over j. O(n^2) time, O(n) stack. */
    static void recursiveBubbleSortNoLoops(int[] arr) {
        recursiveBubbleSortNoLoops(arr, arr.length);
    }

    static void recursiveBubbleSortNoLoops(int[] arr, int n) {
        if (n <= 1) return;
        bubblePass(arr, 0, n);
        recursiveBubbleSortNoLoops(arr, n - 1);
    }

    static void bubblePass(int[] arr, int j, int n) {
        if (j >= n - 1) return;                   // compared every adjacent pair inside arr[0..n-1]
        if (arr[j] > arr[j + 1]) {
            int tmp = arr[j];
            arr[j] = arr[j + 1];
            arr[j + 1] = tmp;
        }
        bubblePass(arr, j + 1, n);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone();
        recursiveBubbleSort(a);
        check(Arrays.equals(a, expected), "recursiveBubbleSort failed on " + Arrays.toString(input) + " got " + Arrays.toString(a));
        int[] b = input.clone();
        recursiveBubbleSortEarlyExit(b);
        check(Arrays.equals(b, expected), "recursiveBubbleSortEarlyExit failed on " + Arrays.toString(input) + " got " + Arrays.toString(b));
        int[] c = input.clone();
        recursiveBubbleSortNoLoops(c);
        check(Arrays.equals(c, expected), "recursiveBubbleSortNoLoops failed on " + Arrays.toString(input) + " got " + Arrays.toString(c));
    }

    static void verify(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);
        verify(input, expected);
    }

    public static void main(String[] args) {
        verify(new int[]{13, 46, 24, 52, 20, 9}, new int[]{9, 13, 20, 24, 46, 52});
        verify(new int[]{5, 4, 4, 1, 1}, new int[]{1, 1, 4, 4, 5});                       // duplicates
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5});                       // already sorted: early exit after one pass
        verify(new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5});                       // reverse sorted: the worst case
        verify(new int[]{-3, 0, -7, 2}, new int[]{-7, -3, 0, 2});                         // negatives
        verify(new int[]{7}, new int[]{7});                                               // single element
        verify(new int[]{}, new int[]{});                                                 // empty
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        Random rnd = new Random(42);
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(2001) - 1000;
        verify(big);                                                                      // 1000 random values, fixed seed: recursion depth 1000
        System.out.println("OK P881_RecursiveBubbleSort");
    }
}
