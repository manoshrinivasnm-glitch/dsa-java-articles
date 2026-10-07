import java.util.*;

/** TUF 25 - Merge two sorted arrays without extra space. After the call a holds the n smallest values and b the m largest, both sorted. */
public class P25_MergeTwoSortedArraysWithoutExtraSpace {

    /** Approach 1: merge into a third array, then copy the first n values back into a and the rest into b. O(n + m) time, O(n + m) space. */
    static void bruteForce(int[] a, int[] b) {
        int n = a.length, m = b.length;
        int[] merged = new int[n + m];
        int i = 0, j = 0, k = 0;
        while (i < n && j < m) merged[k++] = a[i] <= b[j] ? a[i++] : b[j++];
        while (i < n) merged[k++] = a[i++];
        while (j < m) merged[k++] = b[j++];
        for (k = 0; k < n + m; k++) {
            if (k < n) a[k] = merged[k];
            else b[k - n] = merged[k];
        }
    }

    /** Approach 2: swap the tail of a with the head of b while they are out of order, then sort each array. O((n + m) + n log n + m log m) time, O(1) extra space. */
    static void better(int[] a, int[] b) {
        int n = a.length, m = b.length;
        int left = n - 1, right = 0;
        while (left >= 0 && right < m && a[left] > b[right]) {
            swap(a, left, b, right);
            left--;
            right++;
        }
        Arrays.sort(a);
        Arrays.sort(b);
    }

    /** Approach 3: gap method (shell-sort style passes over the virtual array a ++ b). O((n + m) log(n + m)) time, O(1) extra space. */
    static void optimal(int[] a, int[] b) {
        int n = a.length, m = b.length;
        int gap = (n + m + 1) / 2;                              // ceil((n + m) / 2)
        while (gap > 0) {
            int left = 0, right = gap;
            while (right < n + m) {
                if (left < n && right >= n) {                    // left in a, right in b
                    if (a[left] > b[right - n]) swap(a, left, b, right - n);
                } else if (left >= n) {                          // both in b
                    if (b[left - n] > b[right - n]) swap(b, left - n, b, right - n);
                } else {                                         // both in a
                    if (a[left] > a[right]) swap(a, left, a, right);
                }
                left++;
                right++;
            }
            if (gap == 1) break;
            gap = (gap + 1) / 2;                                 // ceil(gap / 2)
        }
    }

    /** Swap x[i] with y[j]; x and y may be the same array. */
    static void swap(int[] x, int i, int[] y, int j) {
        int t = x[i];
        x[i] = y[j];
        y[j] = t;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int[] b, int[] expA, int[] expB) {
        String in = Arrays.toString(a) + " " + Arrays.toString(b);
        int[] a1 = a.clone(), b1 = b.clone();
        bruteForce(a1, b1);
        check(Arrays.equals(a1, expA) && Arrays.equals(b1, expB), "bruteForce " + in + " -> " + Arrays.toString(a1) + " " + Arrays.toString(b1));
        int[] a2 = a.clone(), b2 = b.clone();
        better(a2, b2);
        check(Arrays.equals(a2, expA) && Arrays.equals(b2, expB), "better " + in + " -> " + Arrays.toString(a2) + " " + Arrays.toString(b2));
        int[] a3 = a.clone(), b3 = b.clone();
        optimal(a3, b3);
        check(Arrays.equals(a3, expA) && Arrays.equals(b3, expB), "optimal " + in + " -> " + Arrays.toString(a3) + " " + Arrays.toString(b3));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 4, 8, 10}, new int[]{2, 3, 9}, new int[]{1, 2, 3, 4}, new int[]{8, 9, 10});
        verify(new int[]{1, 3, 5, 7}, new int[]{0, 2, 6, 8, 9}, new int[]{0, 1, 2, 3}, new int[]{5, 6, 7, 8, 9});
        verify(new int[]{1, 2, 3}, new int[]{4, 5, 6}, new int[]{1, 2, 3}, new int[]{4, 5, 6});       // already in order
        verify(new int[]{4, 5, 6}, new int[]{1, 2, 3}, new int[]{1, 2, 3}, new int[]{4, 5, 6});       // completely swapped
        verify(new int[]{1}, new int[]{0}, new int[]{0}, new int[]{1});                               // one element each
        verify(new int[]{}, new int[]{1, 2}, new int[]{}, new int[]{1, 2});                           // empty a
        verify(new int[]{1, 2}, new int[]{}, new int[]{1, 2}, new int[]{});                           // empty b
        verify(new int[]{2, 2, 5}, new int[]{2, 3}, new int[]{2, 2, 2}, new int[]{3, 5});             // duplicates
        verify(new int[]{-5, -1, 3}, new int[]{-3, 0}, new int[]{-5, -3, -1}, new int[]{0, 3});       // negatives
        verify(new int[]{10}, new int[]{1, 2, 3, 4, 5}, new int[]{1}, new int[]{2, 3, 4, 5, 10});     // very unequal sizes
        System.out.println("OK P25_MergeTwoSortedArraysWithoutExtraSpace");
    }
}
