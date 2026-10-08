import java.util.*;

/** TUF 2839 - K-th largest element in an unsorted array. Return the k-th largest in sorted order (duplicates count), 1 <= k <= n. */
public class P2839_KthLargestElementInAnUnsortedArray {

    /** Approach 1: brute force, sort a copy and index from the end. O(n log n) time, O(n) space. */
    static int bruteForce(int[] nums, int k) {
        int[] a = nums.clone();
        Arrays.sort(a);
        return a[a.length - k];
    }

    /** Approach 2: better, a min-heap that never holds more than the k largest seen so far. O(n log k) time, O(k) space. */
    static int better(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();    // min-heap: root = smallest of the kept k
        for (int x : nums) {
            heap.offer(x);
            if (heap.size() > k) heap.poll();                   // throw away the smallest, it cannot be the answer
        }
        return heap.peek();
    }

    /** Approach 3: optimal, quickselect with a random pivot and three-way partition. O(n) expected time, O(n) for the copy. */
    static int optimal(int[] nums, int k) {
        int[] a = nums.clone();                                 // drop the clone if the caller allows mutation
        int target = a.length - k;                              // index of the answer in ascending order
        int lo = 0, hi = a.length - 1;
        Random rng = new Random(2839);                          // fixed seed: reproducible, still breaks sorted input
        while (true) {
            int pivot = a[lo + rng.nextInt(hi - lo + 1)];
            int lt = lo, i = lo, gt = hi;                       // a[lo..lt-1] < pivot, a[lt..i-1] == pivot, a[gt+1..hi] > pivot
            while (i <= gt) {
                if (a[i] < pivot) swap(a, lt++, i++);
                else if (a[i] > pivot) swap(a, i, gt--);
                else i++;
            }
            if (target < lt) hi = lt - 1;                       // answer is among the smaller values
            else if (target > gt) lo = gt + 1;                  // answer is among the larger values
            else return pivot;                                  // target landed in the block equal to pivot
        }
    }

    static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int expected) {
        int[] before = nums.clone();
        int[] got = {bruteForce(nums, k), better(nums, k), optimal(nums, k)};
        String[] names = {"bruteForce", "better", "optimal"};
        for (int i = 0; i < got.length; i++) {
            check(got[i] == expected, names[i] + " gave " + got[i] + ", expected " + expected + " (k=" + k + ")");
        }
        check(Arrays.equals(before, nums), "input array was modified");
    }

    public static void main(String[] args) {
        verify(new int[]{3, 2, 1, 5, 6, 4}, 2, 5);
        verify(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4, 4);         // duplicates are counted separately
        verify(new int[]{7}, 1, 7);                                 // single element
        verify(new int[]{-1, -5, -3}, 3, -5);                       // k = n gives the minimum
        verify(new int[]{-1, -5, -3}, 1, -1);                       // k = 1 gives the maximum
        verify(new int[]{2, 2, 2, 2}, 3, 2);                        // all equal
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, 2, 0);

        int n = 100_000;
        int[] ascending = new int[n];
        for (int i = 0; i < n; i++) ascending[i] = i;
        verify(ascending, 1, n - 1);
        verify(ascending, n, 0);
        verify(ascending, 50_000, 50_000);
        int[] same = new int[n];
        Arrays.fill(same, 9);
        verify(same, 777, 9);                                       // duplicates must not slow quickselect down

        Random rng = new Random(39);
        int[] mixed = new int[5000];
        for (int i = 0; i < mixed.length; i++) mixed[i] = rng.nextInt(200) - 100;
        for (int k = 1; k <= mixed.length; k += 499) verify(mixed, k, bruteForce(mixed, k));
        System.out.println("OK P2839_KthLargestElementInAnUnsortedArray");
    }
}
