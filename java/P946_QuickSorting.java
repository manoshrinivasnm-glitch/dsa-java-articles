import java.util.*;

/** TUF 946 - Quick Sort. Sort an integer array in non-decreasing order by partitioning around a pivot. */
public class P946_QuickSorting {

    /** Approach 1: pivot = first element, two pointers walk inward and swap misplaced pairs. O(n log n) average, O(n^2) worst, O(log n) average stack. */
    static void quickSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    static void quickSort(int[] arr, int low, int high) {
        if (low >= high) return;
        int p = partition(arr, low, high);
        quickSort(arr, low, p - 1);
        quickSort(arr, p + 1, high);
    }

    static int partition(int[] arr, int low, int high) {
        int pivot = arr[low];
        int i = low, j = high;
        while (i < j) {
            while (i <= high - 1 && arr[i] <= pivot) i++;   // first element from the left that is > pivot
            while (j >= low + 1 && arr[j] > pivot) j--;     // first element from the right that is <= pivot
            if (i < j) swap(arr, i, j);
        }
        swap(arr, low, j);                                  // pivot lands at its final index j
        return j;
    }

    /** Approach 2: Lomuto partition, pivot = last element, one pointer grows the "<= pivot" region. Same complexity, simplest invariant. */
    static void quickSortLomuto(int[] arr) {
        quickSortLomuto(arr, 0, arr.length - 1);
    }

    static void quickSortLomuto(int[] arr, int low, int high) {
        if (low >= high) return;
        int p = partitionLomuto(arr, low, high);
        quickSortLomuto(arr, low, p - 1);
        quickSortLomuto(arr, p + 1, high);
    }

    static int partitionLomuto(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low;                                        // arr[low..i-1] <= pivot
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, high);
        return i;
    }

    /** Approach 3: three-way partition around the middle element; equal keys are finished in one shot. O(n log n) average, O(n) on all-equal input. */
    static void quickSortThreeWay(int[] arr) {
        quickSortThreeWay(arr, 0, arr.length - 1);
    }

    static void quickSortThreeWay(int[] arr, int low, int high) {
        if (low >= high) return;
        int pivot = arr[low + (high - low) / 2];
        int lt = low, i = low, gt = high;                   // arr[low..lt-1] < pivot, arr[lt..i-1] == pivot, arr[gt+1..high] > pivot
        while (i <= gt) {
            if (arr[i] < pivot) swap(arr, lt++, i++);
            else if (arr[i] > pivot) swap(arr, i, gt--);
            else i++;
        }
        quickSortThreeWay(arr, low, lt - 1);
        quickSortThreeWay(arr, gt + 1, high);
    }

    static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone();
        quickSort(a);
        check(Arrays.equals(a, expected), "quickSort failed on " + Arrays.toString(input) + " got " + Arrays.toString(a));
        int[] b = input.clone();
        quickSortLomuto(b);
        check(Arrays.equals(b, expected), "quickSortLomuto failed on " + Arrays.toString(input) + " got " + Arrays.toString(b));
        int[] c = input.clone();
        quickSortThreeWay(c);
        check(Arrays.equals(c, expected), "quickSortThreeWay failed on " + Arrays.toString(input) + " got " + Arrays.toString(c));
    }

    static void verify(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);
        verify(input, expected);
    }

    public static void main(String[] args) {
        verify(new int[]{4, 6, 2, 5, 7, 9, 1, 3}, new int[]{1, 2, 3, 4, 5, 6, 7, 9});
        verify(new int[]{5, 4, 4, 1, 1}, new int[]{1, 1, 4, 4, 5});                       // duplicates
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5});                       // already sorted: worst case for approaches 1 and 2
        verify(new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5});                       // reverse sorted
        verify(new int[]{-3, 0, -7, 2}, new int[]{-7, -3, 0, 2});                         // negatives
        verify(new int[]{7}, new int[]{7});                                               // single element
        verify(new int[]{}, new int[]{});                                                 // empty
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        int[] same = new int[300];
        Arrays.fill(same, 8);
        verify(same);                                                                     // all equal: quadratic for approaches 1 and 2, linear for 3
        int[] sorted = new int[2000];
        for (int i = 0; i < sorted.length; i++) sorted[i] = i;
        verify(sorted);                                                                   // sorted: recursion depth 2000 for approaches 1 and 2
        Random rnd = new Random(42);
        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(2_000_001) - 1_000_000;
        verify(big);                                                                      // 100000 random values, fixed seed
        System.out.println("OK P946_QuickSorting");
    }
}
