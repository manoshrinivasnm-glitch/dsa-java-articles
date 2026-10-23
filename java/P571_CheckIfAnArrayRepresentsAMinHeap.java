import java.util.*;

/** TUF 571 - Check if an array represents a min heap. Every node must be <= its children 2i+1 and 2i+2. */
public class P571_CheckIfAnArrayRepresentsAMinHeap {

    /** Approach 1: straight from the definition, compare every node with every node in its subtree. O(n log n) time, O(1) space. */
    static boolean bruteForce(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {                         // compare node i with its whole subtree
            // the descendants of i on each level form one contiguous block of indices [lo, hi]
            long lo = 2L * i + 1, hi = 2L * i + 2;
            while (lo < n) {                                  // walk down one level at a time
                long last = Math.min(hi, n - 1);              // the block may run past the end of the array
                for (long j = lo; j <= last; j++) {           // every descendant of i on this level
                    if (arr[(int) j] < arr[i]) return false;  // a smaller descendant breaks the heap order
                }
                lo = 2 * lo + 1;                              // first descendant one level lower
                hi = 2 * hi + 2;                              // last descendant one level lower
            }
        }
        return true;
    }

    /** Approach 2: recursive check, node against its two children, then both subtrees. O(n) time, O(log n) stack. */
    static boolean recursive(int[] arr) {
        return isHeapFrom(arr, 0);                            // start the check at the root
    }

    static boolean isHeapFrom(int[] arr, int i) {
        int n = arr.length;
        int l = 2 * i + 1, r = 2 * i + 2;                     // children of node i
        if (l >= n) return true;                              // a leaf, or the empty array
        if (arr[l] < arr[i]) return false;                    // left child smaller than its parent
        if (r < n && arr[r] < arr[i]) return false;           // right child exists and is smaller
        return isHeapFrom(arr, l) && isHeapFrom(arr, r);      // isHeapFrom(arr, r) is true at once when r >= n
    }

    /** Approach 3: every non-root element must be >= its parent (i - 1) / 2. O(n) time, O(1) space. */
    static boolean optimal(int[] arr) {
        for (int i = 1; i < arr.length; i++) {                // every non-root index
            if (arr[(i - 1) / 2] > arr[i]) return false;      // parent bigger than child: not a min heap
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, boolean expected) {
        check(bruteForce(arr) == expected, "bruteForce " + Arrays.toString(arr));
        check(recursive(arr) == expected, "recursive " + Arrays.toString(arr));
        check(optimal(arr) == expected, "optimal " + Arrays.toString(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{10, 20, 30, 21, 23}, true);
        verify(new int[]{10, 20, 30, 25, 15}, false);          // 15 < its parent 20
        verify(new int[]{5, 6, 2}, false);                     // 2 < root
        verify(new int[]{}, true);                             // edge: empty
        verify(new int[]{7}, true);                            // edge: single element
        verify(new int[]{1, 1, 1, 1}, true);                   // duplicates are allowed
        verify(new int[]{-3, -1, -2, 0, 5}, true);             // negatives
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 0}, false);   // violation at the deepest leaf
        verify(new int[]{2, 3, 4, 5, 1}, false);               // 1 < its parent 3
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, 0}, true);

        // any ascending array is a min heap
        int[] sorted = new int[100_000];
        for (int i = 0; i < sorted.length; i++) sorted[i] = i / 3;
        verify(sorted, true);
        int[] broken = sorted.clone();
        broken[99_999] = -1;
        verify(broken, false);

        // seeded random cross-check: all three methods must agree with each other
        Random rnd = new Random(11);
        int trues = 0;
        for (int t = 0; t < 3000; t++) {
            int[] a = new int[rnd.nextInt(9)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(4) + i / 2;
            boolean expected = optimal(a);
            if (expected) trues++;
            verify(a, expected);
        }
        check(trues > 0 && trues < 3000, "random arrays should include heaps and non-heaps");
        System.out.println("OK P571_CheckIfAnArrayRepresentsAMinHeap");
    }
}
