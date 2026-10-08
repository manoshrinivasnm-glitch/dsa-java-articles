import java.util.*;

/** TUF 2409 - Sort K sorted array. Every element is at most k positions away from its sorted position; sort it in place. */
public class P2409_SortKSortedArray {

    /** Approach 1: ignore the extra information and sort. O(n log n) time. */
    static void bruteForce(int[] arr, int k) {
        Arrays.sort(arr);
    }

    /** Approach 2: insertion sort. Each element only travels past a few (fewer than 2k) larger neighbours, so O(n k) time, O(1) space. */
    static void better(int[] arr, int k) {
        for (int i = 1; i < arr.length; i++) {
            int x = arr[i], j = i - 1;
            while (j >= 0 && arr[j] > x) {
                arr[j + 1] = arr[j];                     // shift the larger element one step right
                j--;
            }
            arr[j + 1] = x;
        }
    }

    /** Approach 3: sliding min-heap of k + 1 elements. The smallest of the next k + 1 unplaced values is the next output. O(n log k) time, O(k) space. */
    static void optimal(int[] arr, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        int write = 0;
        for (int read = 0; read < arr.length; read++) {
            heap.offer(arr[read]);
            if (heap.size() > k) arr[write++] = heap.poll();   // write <= read, so that slot was already read
        }
        while (!heap.isEmpty()) arr[write++] = heap.poll();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int k, int[] expected) {
        int[] a = arr.clone(), b = arr.clone(), c = arr.clone();
        bruteForce(a, k);
        better(b, k);
        optimal(c, k);
        check(Arrays.equals(a, expected), "bruteForce " + Arrays.toString(arr) + " k=" + k + " -> " + Arrays.toString(a));
        check(Arrays.equals(b, expected), "better " + Arrays.toString(arr) + " k=" + k + " -> " + Arrays.toString(b));
        check(Arrays.equals(c, expected), "optimal " + Arrays.toString(arr) + " k=" + k + " -> " + Arrays.toString(c));
    }

    public static void main(String[] args) {
        verify(new int[]{6, 5, 3, 2, 8, 10, 9}, 3, new int[]{2, 3, 5, 6, 8, 9, 10});
        verify(new int[]{10, 9, 8, 7, 4, 70, 60, 50}, 4, new int[]{4, 7, 8, 9, 10, 50, 60, 70});
        verify(new int[]{}, 0, new int[]{});                                  // edge: empty
        verify(new int[]{1}, 0, new int[]{1});                                // edge: single element
        verify(new int[]{1, 2, 3}, 0, new int[]{1, 2, 3});                    // k = 0 means already sorted
        verify(new int[]{2, 1, 4, 3, 6, 5}, 1, new int[]{1, 2, 3, 4, 5, 6});  // adjacent swaps
        verify(new int[]{3, 3, 1, 1}, 2, new int[]{1, 1, 3, 3});              // duplicates
        verify(new int[]{-1, -3, -2, 0}, 2, new int[]{-3, -2, -1, 0});        // negatives
        verify(new int[]{4, 3, 2, 1}, 3, new int[]{1, 2, 3, 4});              // k = n - 1
        verify(new int[]{4, 3, 2, 1}, 10, new int[]{1, 2, 3, 4});             // k larger than n

        // large: reverse every block of 6 in 0..n-1, so each element is at most 5 away from home
        int n = 100_000, k = 5;
        int[] big = new int[n], sorted = new int[n];
        for (int i = 0; i < n; i++) sorted[i] = i;
        for (int start = 0; start < n; start += k + 1) {
            int end = Math.min(n, start + k + 1);
            for (int i = start; i < end; i++) big[i] = end - 1 - (i - start);
        }
        verify(big, k, sorted);

        // seeded random k-sorted arrays: shuffle inside windows of length k + 1 starting at random offsets
        Random rnd = new Random(17);
        for (int t = 0; t < 1000; t++) {
            int len = rnd.nextInt(25), kk = rnd.nextInt(5);
            int[] base = new int[len];
            for (int i = 0; i < len; i++) base[i] = rnd.nextInt(20);
            Arrays.sort(base);
            int[] a = base.clone();
            for (int start = rnd.nextInt(kk + 1); start < len; start += kk + 1) {
                int end = Math.min(len, start + kk + 1);
                for (int i = end - 1; i > start; i--) {
                    int j = start + rnd.nextInt(i - start + 1);
                    int tmp = a[i];
                    a[i] = a[j];
                    a[j] = tmp;
                }
            }
            verify(a, kk, base);
        }
        System.out.println("OK P2409_SortKSortedArray");
    }
}
