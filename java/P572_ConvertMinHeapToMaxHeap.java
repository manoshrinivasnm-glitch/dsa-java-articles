import java.util.*;

/** TUF 572 - Convert Min Heap to Max Heap. Rearrange the array in place so that every parent is >= its children. */
public class P572_ConvertMinHeapToMaxHeap {

    /** Approach 1: sort in descending order; a non-increasing array is always a max heap. O(n log n) time. */
    static void bruteForce(int[] arr) {
        Arrays.sort(arr);
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) swap(arr, i, j);
    }

    /** Approach 2: top-down build. arr[0..k-1] is a max heap; insert arr[k] by sifting it up. O(n log n) time, O(1) space. */
    static void better(int[] arr) {
        for (int k = 1; k < arr.length; k++) {
            int i = k;
            while (i > 0 && arr[(i - 1) / 2] < arr[i]) {
                swap(arr, i, (i - 1) / 2);
                i = (i - 1) / 2;
            }
        }
    }

    /** Approach 3: bottom-up heapify. Sift down every internal node, from the last one back to the root. O(n) time, O(1) space. */
    static void optimal(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) siftDown(arr, n, i);
    }

    static void siftDown(int[] arr, int n, int i) {
        while (true) {
            int largest = i, l = 2 * i + 1, r = 2 * i + 2;
            if (l < n && arr[l] > arr[largest]) largest = l;
            if (r < n && arr[r] > arr[largest]) largest = r;
            if (largest == i) return;
            swap(arr, i, largest);
            i = largest;
        }
    }

    static void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static boolean isMaxHeap(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[(i - 1) / 2] < a[i]) return false;
        }
        return true;
    }

    static boolean isMinHeap(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[(i - 1) / 2] > a[i]) return false;
        }
        return true;
    }

    static int[] sortedCopy(int[] a) {
        int[] s = a.clone();
        Arrays.sort(s);
        return s;
    }

    /** Every approach must return a valid max heap holding exactly the input's elements. */
    static void verify(int[] minHeap) {
        int[] a = minHeap.clone(), b = minHeap.clone(), c = minHeap.clone();
        bruteForce(a);
        better(b);
        optimal(c);
        for (int[] r : new int[][]{a, b, c}) {
            check(isMaxHeap(r), "not a max heap: " + Arrays.toString(r) + " from " + Arrays.toString(minHeap));
            check(Arrays.equals(sortedCopy(r), sortedCopy(minHeap)), "elements changed: " + Arrays.toString(r));
        }
    }

    public static void main(String[] args) {
        int[][] inputs = {
            {1, 2, 3, 4},
            {10, 20, 30, 21, 23},
            {3, 5, 9, 6, 8, 20, 10, 12, 18, 9},
            {},                                   // edge: empty
            {5},                                  // edge: single element
            {3, 3, 3},                            // duplicates
            {-5, -2, -4, 0, 7, 1},                // negatives
            {Integer.MIN_VALUE, 0, Integer.MAX_VALUE}
        };
        for (int[] in : inputs) verify(in);

        // exact layouts for the worked examples
        int[] x = {1, 2, 3, 4};
        optimal(x);
        check(Arrays.equals(x, new int[]{4, 2, 3, 1}), "optimal [1,2,3,4] -> " + Arrays.toString(x));
        int[] y = {10, 20, 30, 21, 23};
        optimal(y);
        check(Arrays.equals(y, new int[]{30, 23, 10, 21, 20}), "optimal example 2 -> " + Arrays.toString(y));
        int[] v = {3, 5, 9, 6, 8, 20, 10, 12, 18, 9};
        optimal(v);
        check(Arrays.equals(v, new int[]{20, 18, 10, 12, 9, 9, 3, 5, 6, 8}), "optimal 10-element example -> " + Arrays.toString(v));
        int[] z = {10, 20, 30, 21, 23};
        better(z);
        check(Arrays.equals(z, new int[]{30, 23, 20, 10, 21}), "better example 2 -> " + Arrays.toString(z));
        int[] w = {10, 20, 30, 21, 23};
        bruteForce(w);
        check(Arrays.equals(w, new int[]{30, 23, 21, 20, 10}), "bruteForce example 2 -> " + Arrays.toString(w));

        // large: an ascending array is a min heap; convert it
        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big);

        // seeded random min heaps: max-heapify the negated values, then negate back
        Random rnd = new Random(5);
        for (int t = 0; t < 500; t++) {
            int[] a = new int[rnd.nextInt(30)];
            for (int i = 0; i < a.length; i++) a[i] = -(rnd.nextInt(50) - 25);
            optimal(a);
            for (int i = 0; i < a.length; i++) a[i] = -a[i];
            check(isMinHeap(a), "generator must produce a min heap");
            verify(a);
        }
        System.out.println("OK P572_ConvertMinHeapToMaxHeap");
    }
}
