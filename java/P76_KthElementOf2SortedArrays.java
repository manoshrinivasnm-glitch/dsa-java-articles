import java.util.*;

/** TUF 76 - Kth element of 2 sorted arrays. Return the k-th smallest (1-based) element of the union of a and b. */
public class P76_KthElementOf2SortedArrays {

    /** Approach 1: merge both arrays into a new array and index it. O(m + n) time, O(m + n) space. */
    static int bruteForce(int[] a, int[] b, int k) {
        int[] merged = new int[a.length + b.length];
        int i = 0, j = 0, t = 0;
        while (i < a.length && j < b.length) merged[t++] = a[i] <= b[j] ? a[i++] : b[j++];
        while (i < a.length) merged[t++] = a[i++];
        while (j < b.length) merged[t++] = b[j++];
        return merged[k - 1];
    }

    /** Approach 2: run the same merge but only count, stopping at the k-th element. O(k) time, O(1) space. */
    static int better(int[] a, int[] b, int k) {
        int i = 0, j = 0;
        for (int count = 1; ; count++) {
            int next;
            if (j == b.length || (i < a.length && a[i] <= b[j])) next = a[i++];
            else next = b[j++];
            if (count == k) return next;
        }
    }

    /** Approach 3: binary search how many of the k smallest come from the shorter array. O(log min(m, n)) time, O(1) space. */
    static int optimal(int[] a, int[] b, int k) {
        if (a.length > b.length) return optimal(b, a, k);  // search over the shorter array
        int m = a.length, n = b.length;
        int lo = Math.max(0, k - n), hi = Math.min(k, m);   // cutA = how many elements we take from a
        while (lo <= hi) {
            int cutA = (lo + hi) >>> 1, cutB = k - cutA;
            int leftA = cutA == 0 ? Integer.MIN_VALUE : a[cutA - 1];
            int leftB = cutB == 0 ? Integer.MIN_VALUE : b[cutB - 1];
            int rightA = cutA == m ? Integer.MAX_VALUE : a[cutA];
            int rightB = cutB == n ? Integer.MAX_VALUE : b[cutB];
            if (leftA <= rightB && leftB <= rightA) return Math.max(leftA, leftB);
            if (leftA > rightB) hi = cutA - 1;              // took too many from a
            else lo = cutA + 1;                             // took too few from a
        }
        throw new IllegalArgumentException("k out of range");
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

    /** Checks every k against a sorted concatenation. */
    static void verifyAllK(int[] a, int[] b) {
        int[] all = new int[a.length + b.length];
        System.arraycopy(a, 0, all, 0, a.length);
        System.arraycopy(b, 0, all, a.length, b.length);
        Arrays.sort(all);
        for (int k = 1; k <= all.length; k++) verify(a, b, k, all[k - 1]);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 6, 7, 9}, new int[]{1, 4, 8, 10}, 5, 6);
        verify(new int[]{100, 112, 256, 349, 770}, new int[]{72, 86, 113, 119, 265, 445, 892}, 7, 256);
        verify(new int[]{}, new int[]{1, 2, 3}, 2, 2);              // edge: one array empty
        verify(new int[]{5}, new int[]{}, 1, 5);                    // edge: single element overall
        verify(new int[]{1, 1, 1}, new int[]{1, 1}, 4, 1);          // all duplicates
        verify(new int[]{1, 2}, new int[]{3, 4, 5}, 5, 5);          // k = m + n
        verify(new int[]{Integer.MIN_VALUE, 0}, new int[]{Integer.MAX_VALUE}, 3, Integer.MAX_VALUE); // values equal to sentinels
        verifyAllK(new int[]{2, 3, 6, 7, 9}, new int[]{1, 4, 8, 10});
        verifyAllK(new int[]{-5, -5, 0, 3}, new int[]{-6, -5, 1, 1, 2, 9, 12});
        verifyAllK(new int[]{10, 20, 30}, new int[]{1, 2, 3, 4, 5, 6});
        verifyAllK(new int[]{7}, new int[]{1, 2, 3, 8, 9});
        System.out.println("OK P76_KthElementOf2SortedArrays");
    }
}
