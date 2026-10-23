import java.util.*;

/** TUF 944 - Insertion Sort. Sort an integer array in non-decreasing order by inserting each element into the sorted prefix. */
public class P944_InsertionSorting {

    /** Approach 1: insert by swapping the new element leftwards until it is in place. O(n^2) worst, O(n) best, O(1) extra space. */
    static void insertionSortSwaps(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {                   // arr[0..i-1] is sorted; insert arr[i] into it
            int j = i;                                  // j is where the new element currently sits
            while (j > 0 && arr[j - 1] > arr[j]) {      // left neighbour is bigger: swap one step left
                int tmp = arr[j];
                arr[j] = arr[j - 1];
                arr[j - 1] = tmp;
                j--;                                    // follow the element
            }
        }
    }

    /** Approach 2: hold the new element aside, shift larger elements right, drop it in. Same complexity, about a third of the writes. */
    static void insertionSortShift(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {                   // arr[0..i-1] is sorted; insert arr[i] into it
            int key = arr[i];                           // hold the new element aside
            int j = i - 1;                              // compare from the right end of the sorted part
            while (j >= 0 && arr[j] > key) {            // larger than key: it must move right
                arr[j + 1] = arr[j];                    // shift it right, moving the gap left
                j--;
            }
            arr[j + 1] = key;                           // drop key into the gap
        }
    }

    /** Approach 3: binary search the insertion point, then shift. O(n log n) comparisons, still O(n^2) moves, O(1) extra space. */
    static void insertionSortBinary(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {             // insert arr[i] into the sorted arr[0..i-1]
            int key = arr[i];                     // the element to insert
            int lo = 0, hi = i;                   // insertion index lies in [lo, hi]
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;        // middle index, overflow-safe
                // <= moves past equal values, so key lands after them and the sort stays stable
                if (arr[mid] <= key) lo = mid + 1; else hi = mid;
            }
            for (int j = i; j > lo; j--) arr[j] = arr[j - 1];  // shift arr[lo..i-1] right by one
            arr[lo] = key;                        // place key at its insertion point
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone();
        insertionSortSwaps(a);
        check(Arrays.equals(a, expected), "insertionSortSwaps failed on " + Arrays.toString(input) + " got " + Arrays.toString(a));
        int[] b = input.clone();
        insertionSortShift(b);
        check(Arrays.equals(b, expected), "insertionSortShift failed on " + Arrays.toString(input) + " got " + Arrays.toString(b));
        int[] c = input.clone();
        insertionSortBinary(c);
        check(Arrays.equals(c, expected), "insertionSortBinary failed on " + Arrays.toString(input) + " got " + Arrays.toString(c));
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
        int[] big = new int[2000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(2001) - 1000;
        verify(big);                                                                      // 2000 random values, fixed seed
        System.out.println("OK P944_InsertionSorting");
    }
}
