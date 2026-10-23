import java.util.*;

/** TUF 20 - Count Inversions. Count pairs (i, j) with i < j and nums[i] > nums[j]. */
public class P20_CountInversions {

    /** Approach 1: check every pair. O(n^2) time, O(1) space. */
    static long bruteForce(int[] nums) {
        long count = 0;                                          // long: up to n(n-1)/2 pairs
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {          // every j to the right of i
                if (nums[i] > nums[j]) count++;                  // larger value before a smaller one
            }
        }
        return count;
    }

    /** Approach 2: merge sort, counting cross pairs while merging. O(n log n) time, O(n) space. */
    static long optimal(int[] nums) {
        int[] a = nums.clone();                                  // sorting happens on a copy
        return mergeSortCount(a, new int[a.length], 0, a.length - 1);   // one shared buffer for every merge
    }

    static long mergeSortCount(int[] a, int[] tmp, int lo, int hi) {
        if (lo >= hi) return 0;                                  // 0 or 1 element: no pairs
        int mid = lo + (hi - lo) / 2;
        // inversions inside each half; both halves come back sorted
        long count = mergeSortCount(a, tmp, lo, mid) + mergeSortCount(a, tmp, mid + 1, hi);
        int i = lo, j = mid + 1, k = lo;                         // left head, right head, write head
        while (i <= mid && j <= hi) {
            if (a[i] <= a[j]) {                                  // equal values are not an inversion
                tmp[k++] = a[i++];
            } else {
                count += mid - i + 1;                            // a[i..mid] are all > a[j]
                tmp[k++] = a[j++];
            }
        }
        while (i <= mid) tmp[k++] = a[i++];                      // copy whatever is left of either half
        while (j <= hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, lo, a, lo, hi - lo + 1);           // a[lo..hi] is now sorted
        return count;
    }

    /** Approach 3: scan from the right with a Fenwick tree over value ranks, counting smaller values already seen. O(n log n) time, O(n) space. */
    static long optimalFenwick(int[] nums) {
        int n = nums.length;
        int[] sorted = nums.clone();
        Arrays.sort(sorted);                                     // maps each value to a rank in 0..n-1
        int[] tree = new int[n + 1];                             // 1-based Fenwick tree over ranks
        long count = 0;
        for (int i = n - 1; i >= 0; i--) {                       // right to left: the tree holds nums[i+1..]
            int rank = lowerBound(sorted, nums[i]);              // how many values in the array are < nums[i]
            // elements to the right that are smaller: sum of the tree over ranks 1..rank
            for (int idx = rank; idx > 0; idx -= idx & -idx) count += tree[idx];
            // record nums[i] at position rank + 1
            for (int idx = rank + 1; idx <= n; idx += idx & -idx) tree[idx]++;
        }
        return count;
    }

    /** Index of the first element of sorted that is >= v (so also the count of elements < v). */
    static int lowerBound(int[] sorted, int v) {
        int lo = 0, hi = sorted.length;                          // answer lies in [lo, hi]
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;                           // unsigned shift: no overflow
            if (sorted[mid] < v) lo = mid + 1;                   // mid is too small: answer is to the right
            else hi = mid;                                       // mid could be the answer
        }
        return lo;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, long expected, boolean runBrute) {
        String in = nums.length <= 10 ? Arrays.toString(nums) : "array of length " + nums.length;
        if (runBrute) check(bruteForce(nums) == expected, "bruteForce " + in + " -> " + bruteForce(nums));
        check(optimal(nums) == expected, "optimal " + in + " -> " + optimal(nums));
        check(optimalFenwick(nums) == expected, "optimalFenwick " + in + " -> " + optimalFenwick(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{5, 3, 2, 4, 1}, 8, true);
        verify(new int[]{1, 2, 3, 4, 5}, 0, true);                 // sorted: no inversions
        verify(new int[]{5, 4, 3, 2, 1}, 10, true);                // reversed: every pair
        verify(new int[]{2, 4, 1, 3, 5}, 3, true);
        verify(new int[]{}, 0, true);                              // empty input
        verify(new int[]{1}, 0, true);                             // single element
        verify(new int[]{3, 3, 3}, 0, true);                       // equal values are not inversions
        verify(new int[]{2, 1, 2, 1}, 3, true);                    // duplicates mixed with inversions
        verify(new int[]{-1, -3, 2, -2}, 3, true);                 // negatives
        int n = 100_000;                                           // answer n(n-1)/2 does not fit in an int
        int[] desc = new int[n];
        for (int i = 0; i < n; i++) desc[i] = n - i;
        verify(desc, (long) n * (n - 1) / 2, false);
        System.out.println("OK P20_CountInversions");
    }
}
