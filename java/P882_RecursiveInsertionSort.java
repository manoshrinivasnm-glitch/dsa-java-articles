import java.util.*;

/** TUF 882 - Recursive Insertion Sort. Sort an integer array in non-decreasing order, replacing the outer loop of insertion sort with recursion. */
public class P882_RecursiveInsertionSort {

    /** Approach 1: arr[0..i-1] is sorted; insert arr[i] by swapping it leftwards, then recurse on i+1. O(n^2) worst, O(n) best, O(n) stack. */
    static void recursiveInsertionSort(int[] arr) {
        recursiveInsertionSort(arr, 0);
    }

    static void recursiveInsertionSort(int[] arr, int i) {
        if (i >= arr.length) return;
        int j = i;
        while (j > 0 && arr[j - 1] > arr[j]) {
            int tmp = arr[j];
            arr[j] = arr[j - 1];
            arr[j - 1] = tmp;
            j--;
        }
        recursiveInsertionSort(arr, i + 1);
    }

    /** Approach 2: head recursion, sort the first n-1 elements, then insert the last one by shifting. Same complexity, fewer writes. */
    static void recursiveInsertionSortHead(int[] arr) {
        recursiveInsertionSortHead(arr, arr.length);
    }

    static void recursiveInsertionSortHead(int[] arr, int n) {
        if (n <= 1) return;
        recursiveInsertionSortHead(arr, n - 1);
        int key = arr[n - 1];
        int j = n - 2;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }

    /** Approach 3: no loops; both the outer walk and the inner insertion are recursive. O(n^2) time, O(n) stack. */
    static void recursiveInsertionSortNoLoops(int[] arr) {
        recursiveInsertionSortNoLoops(arr, 1);
    }

    static void recursiveInsertionSortNoLoops(int[] arr, int i) {
        if (i >= arr.length) return;
        insert(arr, i);
        recursiveInsertionSortNoLoops(arr, i + 1);
    }

    static void insert(int[] arr, int j) {
        if (j <= 0 || arr[j - 1] <= arr[j]) return; // reached the front, or the left neighbour is not larger
        int tmp = arr[j];
        arr[j] = arr[j - 1];
        arr[j - 1] = tmp;
        insert(arr, j - 1);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone();
        recursiveInsertionSort(a);
        check(Arrays.equals(a, expected), "recursiveInsertionSort failed on " + Arrays.toString(input) + " got " + Arrays.toString(a));
        int[] b = input.clone();
        recursiveInsertionSortHead(b);
        check(Arrays.equals(b, expected), "recursiveInsertionSortHead failed on " + Arrays.toString(input) + " got " + Arrays.toString(b));
        int[] c = input.clone();
        recursiveInsertionSortNoLoops(c);
        check(Arrays.equals(c, expected), "recursiveInsertionSortNoLoops failed on " + Arrays.toString(input) + " got " + Arrays.toString(c));
    }

    static void verify(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);
        verify(input, expected);
    }

    public static void main(String[] args) {
        verify(new int[]{14, 9, 15, 12, 6, 8, 13}, new int[]{6, 8, 9, 12, 13, 14, 15});
        verify(new int[]{5, 4, 4, 1, 1}, new int[]{1, 1, 4, 4, 5});                       // duplicates
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5});                       // already sorted: the best case
        verify(new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5});                       // reverse sorted: the worst case
        verify(new int[]{-3, 0, -7, 2}, new int[]{-7, -3, 0, 2});                         // negatives
        verify(new int[]{7}, new int[]{7});                                               // single element
        verify(new int[]{}, new int[]{});                                                 // empty
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        Random rnd = new Random(42);
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(2001) - 1000;
        verify(big);                                                                      // 1000 random values, fixed seed: recursion depth 1000
        System.out.println("OK P882_RecursiveInsertionSort");
    }
}
