import java.util.*;

/** TUF 2840 - Kth smallest element in an array [use priority queue]. Return the k-th smallest value, 1 <= k <= n. */
public class P2840_KthSmallestElementInAnArrayUsePriorityQu {

    /** Approach 1: sort a copy and index it. O(n log n) time, O(n) space. */
    static int bruteForce(int[] arr, int k) {
        int[] a = arr.clone();
        Arrays.sort(a);
        return a[k - 1];
    }

    /** Approach 2: heapify everything into a min-heap, then remove the smallest k - 1 times. O(n + k log n) time, O(n) space. */
    static int better(int[] arr, int k) {
        List<Integer> all = new ArrayList<>();
        for (int x : arr) all.add(x);
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(all);    // built from a collection in O(n)
        for (int i = 1; i < k; i++) minHeap.poll();
        return minHeap.peek();
    }

    /** Approach 3: max-heap holding the k smallest values seen so far; its root is the answer. O(n log k) time, O(k) space. */
    static int optimal(int[] arr, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int x : arr) {
            if (maxHeap.size() < k) {
                maxHeap.offer(x);
            } else if (x < maxHeap.peek()) {             // x beats the largest of the current k smallest
                maxHeap.poll();
                maxHeap.offer(x);
            }
        }
        return maxHeap.peek();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int k, int expected) {
        int[] copy = arr.clone();
        check(bruteForce(arr, k) == expected, "bruteForce " + Arrays.toString(arr) + " k=" + k);
        check(better(arr, k) == expected, "better " + Arrays.toString(arr) + " k=" + k);
        check(optimal(arr, k) == expected, "optimal " + Arrays.toString(arr) + " k=" + k);
        check(Arrays.equals(arr, copy), "input must not be modified");
    }

    public static void main(String[] args) {
        verify(new int[]{7, 10, 4, 3, 20, 15}, 3, 7);
        verify(new int[]{7, 10, 4, 20, 15}, 4, 15);
        verify(new int[]{1}, 1, 1);                           // edge: single element
        verify(new int[]{5, 5, 5, 1}, 2, 5);                  // duplicates count separately
        verify(new int[]{-2, 0, -7, 3}, 1, -7);               // k = 1 is the minimum
        verify(new int[]{-2, 0, -7, 3}, 4, 3);                // k = n is the maximum
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, 1, Integer.MIN_VALUE);
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, 3, Integer.MAX_VALUE);

        // large: a permutation of 0..n-1, so the k-th smallest is k - 1
        int[] perm = new int[100_000];
        for (int i = 0; i < perm.length; i++) perm[i] = (int) ((long) i * 7919 % 100_000);
        verify(perm, 1, 0);
        verify(perm, 77_777, 77_776);
        verify(perm, 100_000, 99_999);

        // seeded random cross-check against sorting
        Random rnd = new Random(8);
        for (int t = 0; t < 2000; t++) {
            int[] a = new int[1 + rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10) - 5;
            int k = 1 + rnd.nextInt(a.length);
            verify(a, k, bruteForce(a, k));
        }
        System.out.println("OK P2840_KthSmallestElementInAnArrayUsePriorityQu");
    }
}
