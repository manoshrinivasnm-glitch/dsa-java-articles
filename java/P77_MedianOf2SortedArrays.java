import java.util.*;

/** TUF 77 - Median of 2 sorted arrays. Return the median of all elements of two sorted arrays taken together (n + m >= 1). */
public class P77_MedianOf2SortedArrays {

    /** Approach 1: merge into one sorted array and read the middle. O(n + m) time, O(n + m) space. */
    static double bruteForce(int[] a, int[] b) {
        int n = a.length, m = b.length;
        int[] merged = new int[n + m];
        int i = 0, j = 0, k = 0;
        while (i < n && j < m) merged[k++] = a[i] <= b[j] ? a[i++] : b[j++];
        while (i < n) merged[k++] = a[i++];
        while (j < m) merged[k++] = b[j++];
        int total = n + m;
        if (total % 2 == 1) return merged[total / 2];
        return ((double) merged[total / 2 - 1] + merged[total / 2]) / 2.0;
    }

    /** Approach 2: walk the merge without storing it, keeping only the two middle elements. O(n + m) time, O(1) space. */
    static double better(int[] a, int[] b) {
        int n = a.length, m = b.length, total = n + m;
        int idx2 = total / 2, idx1 = idx2 - 1;           // positions of the middle element(s) in merged order
        int el1 = 0, el2 = 0;
        int i = 0, j = 0, pos = 0;
        while (i < n || j < m) {
            int next;
            if (j >= m || (i < n && a[i] <= b[j])) next = a[i++]; else next = b[j++];
            if (pos == idx1) el1 = next;
            if (pos == idx2) { el2 = next; break; }
            pos++;
        }
        if (total % 2 == 1) return el2;
        return ((double) el1 + el2) / 2.0;
    }

    /** Approach 3: binary search on how many elements of the shorter array belong to the left half. O(log(min(n, m))) time, O(1) space. */
    static double optimal(int[] a, int[] b) {
        if (a.length > b.length) return optimal(b, a);  // always search over the shorter array
        int n = a.length, m = b.length, total = n + m;
        int leftSize = (total + 1) / 2;                  // size of the left half; it gets the extra element when total is odd
        int lo = 0, hi = n;
        while (lo <= hi) {
            int i = lo + (hi - lo) / 2;                  // i elements of a go to the left half ...
            int j = leftSize - i;                        // ... so j elements of b must go there too
            int l1 = i == 0 ? Integer.MIN_VALUE : a[i - 1];
            int l2 = j == 0 ? Integer.MIN_VALUE : b[j - 1];
            int r1 = i == n ? Integer.MAX_VALUE : a[i];
            int r2 = j == m ? Integer.MAX_VALUE : b[j];
            if (l1 <= r2 && l2 <= r1) {
                if (total % 2 == 1) return Math.max(l1, l2);
                return ((double) Math.max(l1, l2) + Math.min(r1, r2)) / 2.0;
            }
            if (l1 > r2) hi = i - 1;                     // too many from a: move the cut left
            else lo = i + 1;                             // too few from a: move the cut right
        }
        throw new IllegalArgumentException("inputs are not sorted");
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int[] b, double expected) {
        String in = Arrays.toString(a) + " " + Arrays.toString(b);
        check(Math.abs(bruteForce(a, b) - expected) < 1e-9, "bruteForce " + in);
        check(Math.abs(better(a, b) - expected) < 1e-9, "better " + in);
        check(Math.abs(optimal(a, b) - expected) < 1e-9, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 3}, new int[]{2}, 2.0);
        verify(new int[]{1, 2}, new int[]{3, 4}, 2.5);
        verify(new int[]{1, 4, 7, 10, 12}, new int[]{2, 3, 6, 15}, 6.0);
        verify(new int[]{-5, 3, 6, 12, 15}, new int[]{-12, -10, -6, -3, 4, 10}, 3.0);     // negatives
        verify(new int[]{}, new int[]{1}, 1.0);                                           // one array empty
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, new int[]{}, 5.5);               // the other array empty
        verify(new int[]{2, 2, 2}, new int[]{2, 2}, 2.0);                                 // duplicates
        verify(new int[]{1}, new int[]{2}, 1.5);
        verify(new int[]{1, 2, 3, 4, 5, 6}, new int[]{7, 8}, 4.5);                        // all of a before all of b
        verify(new int[]{7, 8}, new int[]{1, 2, 3, 4, 5, 6}, 4.5);                        // same, arrays swapped
        verify(new int[]{Integer.MIN_VALUE}, new int[]{Integer.MAX_VALUE}, -0.5);         // the two middles overflow int when added
        System.out.println("OK P77_MedianOf2SortedArrays");
    }
}
