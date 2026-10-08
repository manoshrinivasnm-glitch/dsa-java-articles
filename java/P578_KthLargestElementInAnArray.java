import java.util.*;

/** TUF 578 - K-th Largest element in an array. Return the k-th largest value (duplicates count separately), 1 <= k <= n. */
public class P578_KthLargestElementInAnArray {

    /** Approach 1: sort ascending; the k-th largest sits at index n - k. O(n log n) time. */
    static int bruteForce(int[] nums, int k) {
        int[] a = nums.clone();
        Arrays.sort(a);
        return a[a.length - k];
    }

    /** Approach 2: min-heap holding the k largest values seen so far; its root is the answer. O(n log k) time, O(k) space. */
    static int better(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int x : nums) {
            heap.offer(x);
            if (heap.size() > k) heap.poll();          // drop the smallest of k + 1 candidates
        }
        return heap.peek();
    }

    /** Approach 3: quickselect with a random pivot and a three-way partition. Average O(n) time, O(1) extra space. */
    static int optimal(int[] nums, int k) {
        int[] a = nums.clone();
        int target = a.length - k;                       // index of the answer in ascending order
        int lo = 0, hi = a.length - 1;
        Random rnd = new Random(12345);                  // fixed seed: reproducible, still a spread of pivots
        while (true) {
            int pivot = a[lo + rnd.nextInt(hi - lo + 1)];
            int lt = lo, i = lo, gt = hi;                // a[lo..lt-1] < pivot, a[lt..i-1] == pivot, a[gt+1..hi] > pivot
            while (i <= gt) {
                if (a[i] < pivot) swap(a, lt++, i++);
                else if (a[i] > pivot) swap(a, i, gt--);
                else i++;
            }
            if (target < lt) hi = lt - 1;                // answer is among the smaller values
            else if (target > gt) lo = gt + 1;           // answer is among the larger values
            else return pivot;                           // answer lands inside the block equal to pivot
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
        int[] copy = nums.clone();
        check(bruteForce(nums, k) == expected, "bruteForce " + Arrays.toString(nums) + " k=" + k);
        check(better(nums, k) == expected, "better " + Arrays.toString(nums) + " k=" + k);
        check(optimal(nums, k) == expected, "optimal " + Arrays.toString(nums) + " k=" + k);
        check(Arrays.equals(nums, copy), "input must not be modified");
    }

    public static void main(String[] args) {
        verify(new int[]{3, 2, 1, 5, 6, 4}, 2, 5);
        verify(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4, 4);   // duplicates count separately
        verify(new int[]{7}, 1, 7);                           // edge: single element
        verify(new int[]{2, 2, 2, 2}, 3, 2);                  // all equal
        verify(new int[]{-1, -5, -3}, 1, -1);                 // negatives, k = 1 is the maximum
        verify(new int[]{-1, -5, -3}, 3, -5);                 // k = n is the minimum
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, 2, 0);

        // large inputs: many duplicates (three-way partition keeps this linear) and a permutation
        int[] same = new int[100_000];
        Arrays.fill(same, 9);
        verify(same, 50_000, 9);
        int[] perm = new int[100_000];
        for (int i = 0; i < perm.length; i++) perm[i] = (int) ((long) i * 7919 % 100_000);
        verify(perm, 1, 99_999);
        verify(perm, 100_000, 0);
        verify(perm, 31_337, 100_000 - 31_337);

        // seeded random cross-check against sorting
        Random rnd = new Random(3);
        for (int t = 0; t < 2000; t++) {
            int[] a = new int[1 + rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10) - 5;
            int k = 1 + rnd.nextInt(a.length);
            verify(a, k, bruteForce(a, k));
        }
        System.out.println("OK P578_KthLargestElementInAnArray");
    }
}
