import java.util.*;

/** TUF 26 - Reverse Pairs. Count pairs (i, j) with i < j and nums[i] > 2 * nums[j]. */
public class P26_ReversePairs {

    /** Approach 1: check every pair. O(n^2) time, O(1) space. */
    static long bruteForce(int[] nums) {
        long count = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if ((long) nums[i] > 2L * nums[j]) count++;
            }
        }
        return count;
    }

    /** Approach 2: merge sort with a separate counting pass before each merge. O(n log n) time, O(n) space. */
    static long optimal(int[] nums) {
        int[] a = nums.clone();                                  // sorting happens on a copy
        return mergeSortCount(a, new int[a.length], 0, a.length - 1);
    }

    static long mergeSortCount(int[] a, int[] tmp, int lo, int hi) {
        if (lo >= hi) return 0;
        int mid = lo + (hi - lo) / 2;
        long count = mergeSortCount(a, tmp, lo, mid) + mergeSortCount(a, tmp, mid + 1, hi);
        int j = mid + 1;                                         // counting pass: both halves are sorted
        for (int i = lo; i <= mid; i++) {
            while (j <= hi && (long) a[i] > 2L * a[j]) j++;      // j only moves forward because a[i] grows
            count += j - (mid + 1);                              // a[mid+1 .. j-1] all satisfy a[i] > 2 * a[j]
        }
        int i = lo, k = lo;                                      // ordinary merge
        j = mid + 1;
        while (i <= mid && j <= hi) {
            if (a[i] <= a[j]) tmp[k++] = a[i++];
            else tmp[k++] = a[j++];
        }
        while (i <= mid) tmp[k++] = a[i++];
        while (j <= hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, lo, a, lo, hi - lo + 1);
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, long expected, boolean runBrute) {
        String in = nums.length <= 10 ? Arrays.toString(nums) : "array of length " + nums.length;
        if (runBrute) check(bruteForce(nums) == expected, "bruteForce " + in + " -> " + bruteForce(nums));
        check(optimal(nums) == expected, "optimal " + in + " -> " + optimal(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 3, 2, 3, 1}, 2, true);
        verify(new int[]{2, 4, 3, 5, 1}, 3, true);
        verify(new int[]{1, 2, 3}, 0, true);                       // increasing: nothing
        verify(new int[]{}, 0, true);                              // empty input
        verify(new int[]{5}, 0, true);                             // single element
        verify(new int[]{5, 4, 3, 2, 1}, 4, true);
        verify(new int[]{2, 2, 2, 2}, 0, true);                    // equal values: 2 > 4 is false
        verify(new int[]{-5, -3, -1, 2}, 1, true);                 // negatives: -5 > 2 * (-3)
        verify(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE}, 5, true); // 2 * MIN overflows int
        int n = 100_000;                                           // descending 2n, 2n-2, ..., 2: answer exceeds int range
        int[] desc = new int[n];
        for (int i = 0; i < n; i++) desc[i] = 2 * (n - i);
        long expected = 0;
        for (int j = 0; j < n; j++) expected += Math.max(0, j - (n - j));   // indices i < j with 2(n-i) > 4(n-j), i.e. i > 2n - 2j... counted directly below
        expected = 0;
        for (int j = 1; j < n; j++) {
            int smallestI = Math.max(0, 2 * n - 2 * j + 1 - n);  // placeholder, replaced by direct count
            smallestI = 0;
            expected += 0 * smallestI;
        }
        expected = countDescending(n);
        verify(desc, expected, false);
        System.out.println("OK P26_ReversePairs");
    }

    /** Closed-form reference for desc[i] = 2(n - i): pair (i, j) is a reverse pair iff n - i > 2(n - j), i.e. i < 2j - n. */
    static long countDescending(int n) {
        long total = 0;
        for (int j = 0; j < n; j++) {
            int upper = Math.min(j, 2 * j - n);                  // i ranges over 0 .. upper-1
            if (upper > 0) total += upper;
        }
        return total;
    }
}
