import java.util.*;

/** TUF 943 - Bubble Sort. Sort an integer array in non-decreasing order by repeatedly swapping adjacent out-of-order pairs. */
public class P943_BubbleSort {

    /** Approach 1: textbook bubble sort, always n-1 passes. O(n^2) time in every case, O(1) extra space. */
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    /** Approach 2: stop as soon as a pass makes no swap. O(n) best case, O(n^2) worst case, O(1) extra space. */
    static void bubbleSortEarlyExit(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) return;
        }
    }

    /** Approach 3: remember where the last swap happened; everything after it is already final. O(n) best, O(n^2) worst, O(1) extra space. */
    static void bubbleSortLastSwap(int[] arr) {
        int end = arr.length - 1;
        while (end > 0) {
            int lastSwap = 0;
            for (int j = 0; j < end; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    lastSwap = j;
                }
            }
            end = lastSwap;
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone();
        bubbleSort(a);
        check(Arrays.equals(a, expected), "bubbleSort failed on " + Arrays.toString(input) + " got " + Arrays.toString(a));
        int[] b = input.clone();
        bubbleSortEarlyExit(b);
        check(Arrays.equals(b, expected), "bubbleSortEarlyExit failed on " + Arrays.toString(input) + " got " + Arrays.toString(b));
        int[] c = input.clone();
        bubbleSortLastSwap(c);
        check(Arrays.equals(c, expected), "bubbleSortLastSwap failed on " + Arrays.toString(input) + " got " + Arrays.toString(c));
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
        verify(new int[]{2, 1, 3, 4, 5, 6}, new int[]{1, 2, 3, 4, 5, 6});                 // one swap near the front
        verify(new int[]{-3, 0, -7, 2}, new int[]{-7, -3, 0, 2});                         // negatives
        verify(new int[]{7}, new int[]{7});                                               // single element
        verify(new int[]{}, new int[]{});                                                 // empty
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        Random rnd = new Random(42);
        int[] big = new int[2000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(2001) - 1000;
        verify(big);                                                                      // 2000 random values, fixed seed
        System.out.println("OK P943_BubbleSort");
    }
}
