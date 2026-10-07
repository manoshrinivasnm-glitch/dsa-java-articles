import java.util.*;

/** TUF 2767 - Kth element of 2 sorted arrays. Return the k-th smallest (1-indexed, 1 <= k <= n + m) element of two sorted arrays taken together. */
public class P2767_KthElementOf2SortedArrays {

    /** Approach 1: merge into one sorted array and read position k-1. O(n + m) time, O(n + m) space. */
    static int bruteForce(int[] a, int[] b, int k) {
        int n = a.length, m = b.length;
        int[] merged = new int[n + m];
        int i = 0, j = 0, p = 0;
        while (i < n && j < m) merged[p++] = a[i] <= b[j] ? a[i++] : b[j++];
        while (i < n) merged[p++] = a[i++];
        while (j < m) merged[p++] = b[j++];
        return merged[k - 1];
    }

    /** Approach 2: walk the merge without storing it and stop after k elements. O(k) time, O(1) space. */
    static int better(int[] a, int[] b, int k) {
        int n = a.length, m = b.length;
        int i = 0, j = 0, taken = 0;
        while (true) {
            int next;
            if (j >= m || (i < n && a[i] <= b[j])) next = a[i++]; else next = b[j++];
            taken++;
            if (taken == k) return next;
        }
    }

    /** Approach 3: binary search on how many of the k smallest come from the shorter array. O(log(min(n, m))) time, O(1) space. */
    static int optimal(int[] a, int[] b, int k) {
        if (a.length > b.length) return optimal(b, a, k);  // always search over the shorter array
        int n = a.length, m = b.length;
        int lo = Math.max(0, k - m), hi = Math.min(k, n);   // a contributes at least k-m and at most min(k, n) elements
        while (lo <= hi) {
            int i = lo + (hi - lo) / 2;                      // i from a and j = k-i from b form the k smallest
            int j = k - i;
            int l1 = i == 0 ? Integer.MIN_VALUE : a[i - 1];
            int l2 = j == 0 ? Integer.MIN_VALUE : b[j - 1];
            int r1 = i == n ? Integer.MAX_VALUE : a[i];
            int r2 = j == m ? Integer.MAX_VALUE : b[j];
            if (l1 <= r2 && l2 <= r1) return Math.max(l1, l2);
            if (l1 > r2) hi = i - 1;                         // too many from a: move the cut left
            else lo = i + 1;                                 // too few from a: move the cut right
        }
        throw new IllegalArgumentException("inputs are not sorted or k is out of range");
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int[] b, int k, int expected) {
        String in = Arrays.toString(a) + " " + Arrays.toString(b) + " k=" + k;
        check(bruteForce(a, b, k) == expected, "bruteForce " + in);
        check(better(a, b, k) == expected, "better " + in);
        check(optimal(a, b, k) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 6, 7, 9}, new int[]{1, 4, 8, 10}, 5, 6);
        verify(new int[]{100, 112, 256, 349, 770}, new int[]{72, 86, 113, 119, 265, 445, 892}, 7, 256);
        verify(new int[]{1, 2, 3}, new int[]{4, 5, 6}, 1, 1);        // k = 1: the global minimum
        verify(new int[]{1, 2, 3}, new int[]{4, 5, 6}, 6, 6);        // k = n + m: the global maximum
        verify(new int[]{}, new int[]{1, 2, 3}, 2, 2);               // one array empty
        verify(new int[]{5}, new int[]{1, 2, 3, 4}, 5, 5);           // the single element of a comes last
        verify(new int[]{1, 1, 1}, new int[]{1, 1}, 4, 1);           // duplicates
        verify(new int[]{-10, -5, 0}, new int[]{-7, 3}, 2, -7);      // negatives
        int[] a = {1, 3, 5, 7, 9}, b = {2, 4, 6, 8, 10};             // merged order is 1..10, so the k-th element is k
        for (int k = 1; k <= 10; k++) verify(a, b, k, k);
        System.out.println("OK P2767_KthElementOf2SortedArrays");
    }
}
