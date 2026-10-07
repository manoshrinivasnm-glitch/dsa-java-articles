import java.util.*;

/** TUF 48 - Union of two sorted arrays. Return every distinct value that appears in a or in b, in ascending order. */
public class P48_UnionOfTwoSortedArrays {

    /** Approach 1: pour both arrays into a sorted set, which de-duplicates and orders for free. O((n + m) log(n + m)) time, O(n + m) space. */
    static int[] bruteForce(int[] a, int[] b) {
        TreeSet<Integer> set = new TreeSet<>();
        for (int x : a) set.add(x);
        for (int x : b) set.add(x);
        int[] result = new int[set.size()];
        int i = 0;
        for (int x : set) result[i++] = x;
        return result;
    }

    /** Approach 2: merge like merge sort, writing a value only when it differs from the last one written. O(n + m) time, O(1) extra space beyond the output. */
    static int[] optimal(int[] a, int[] b) {
        int n = a.length, m = b.length;
        int[] buf = new int[n + m];                  // upper bound: no value shared, no duplicates
        int i = 0, j = 0, k = 0;
        while (i < n && j < m) {
            int x;
            if (a[i] <= b[j]) x = a[i++];            // ties take a's copy; b's copy is skipped on the next round
            else x = b[j++];
            if (k == 0 || buf[k - 1] != x) buf[k++] = x;
        }
        while (i < n) {
            if (k == 0 || buf[k - 1] != a[i]) buf[k++] = a[i];
            i++;
        }
        while (j < m) {
            if (k == 0 || buf[k - 1] != b[j]) buf[k++] = b[j];
            j++;
        }
        return Arrays.copyOf(buf, k);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int[] b, int[] expected) {
        String s = Arrays.toString(a) + " U " + Arrays.toString(b);
        int[] r1 = bruteForce(a, b);
        check(Arrays.equals(r1, expected), "bruteForce " + s + " -> " + Arrays.toString(r1));
        int[] r2 = optimal(a, b);
        check(Arrays.equals(r2, expected), "optimal " + s + " -> " + Arrays.toString(r2));
        int[] r3 = optimal(b, a);
        check(Arrays.equals(r3, expected), "optimal must be symmetric " + s + " -> " + Arrays.toString(r3));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{2, 3, 4, 4, 5}, new int[]{1, 2, 3, 4, 5});
        verify(new int[]{1, 1, 2, 2, 3}, new int[]{2, 3, 3, 4}, new int[]{1, 2, 3, 4});        // duplicates inside each array
        verify(new int[]{1, 2, 3}, new int[]{4, 5, 6}, new int[]{1, 2, 3, 4, 5, 6});              // disjoint, a entirely before b
        verify(new int[]{}, new int[]{}, new int[]{});                                           // both empty
        verify(new int[]{}, new int[]{3, 3, 7}, new int[]{3, 7});                                // one empty
        verify(new int[]{5, 5, 5}, new int[]{5}, new int[]{5});                                  // every value equal
        verify(new int[]{-5, -1, 0}, new int[]{-3, 0, 2}, new int[]{-5, -3, -1, 0, 2});           // negatives, interleaved
        verify(new int[]{Integer.MIN_VALUE, 0}, new int[]{0, Integer.MAX_VALUE}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        System.out.println("OK P48_UnionOfTwoSortedArrays");
    }
}
