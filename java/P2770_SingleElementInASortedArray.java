import java.util.*;

/** TUF 2770 - Single element in a Sorted Array. Every value appears exactly twice except one; return that value. */
public class P2770_SingleElementInASortedArray {

    /** Approach 1: linear scan, the element that differs from both neighbours is the answer. O(n) time, O(1) space. */
    static int bruteForce(int[] arr) {
        int n = arr.length;
        if (n == 1) return arr[0];                                // a lone element is the single one
        for (int i = 0; i < n; i++) {                             // test each index against its neighbours
            if (i == 0) {                                         // first index has only a right neighbour
                if (arr[0] != arr[1]) return arr[0];              // differs from its only neighbour
            } else if (i == n - 1) {                              // last index has only a left neighbour
                if (arr[n - 1] != arr[n - 2]) return arr[n - 1];  // differs from its only neighbour
            } else if (arr[i] != arr[i - 1] && arr[i] != arr[i + 1]) {   // differs from both neighbours
                return arr[i];
            }
        }
        return -1;                                                // not reached for valid input
    }

    /** Approach 2: XOR every element; equal pairs cancel to 0 and only the single value survives. O(n) time, O(1) space. */
    static int better(int[] arr) {
        int x = 0;                 // XOR of everything seen so far
        for (int v : arr) x ^= v;  // each equal pair cancels itself out
        return x;                  // only the unpaired value survives
    }

    /** Approach 3: binary search using the index parity of the pairs. O(log n) time, O(1) space. */
    static int optimal(int[] arr) {
        int n = arr.length;
        if (n == 1) return arr[0];            // a lone element is the single one
        if (arr[0] != arr[1]) return arr[0];  // the single element sits at the left edge
        if (arr[n - 1] != arr[n - 2]) return arr[n - 1];   // the single element sits at the right edge
        int lo = 1, hi = n - 2;               // edges are done, so mid - 1 and mid + 1 stay in range
        while (lo <= hi) {                    // search until the range is empty
            int mid = lo + (hi - lo) / 2;
            // mid matches neither neighbour, so it is the single element
            if (arr[mid] != arr[mid - 1] && arr[mid] != arr[mid + 1]) return arr[mid];
            // Left of the single element every pair starts at an even index: (0,1), (2,3), ...
            boolean inLeftHalf = (mid % 2 == 1) ? arr[mid] == arr[mid - 1] : arr[mid] == arr[mid + 1];
            if (inLeftHalf) lo = mid + 1;     // pairs still intact: the single element is to the right
            else hi = mid - 1;                // pairing already broken: it is to the left
        }
        return -1;                            // not reached for valid input
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int expected) {
        String in = Arrays.toString(arr);
        check(bruteForce(arr) == expected, "bruteForce " + in);
        check(better(arr) == expected, "better " + in);
        check(optimal(arr) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 2, 3, 3, 4, 4, 8, 8}, 2);
        verify(new int[]{3, 3, 7, 7, 10, 11, 11}, 10);
        verify(new int[]{1}, 1);                                   // single element array
        verify(new int[]{1, 2, 2, 3, 3}, 1);                       // answer at the very start
        verify(new int[]{1, 1, 2, 2, 3}, 3);                       // answer at the very end
        verify(new int[]{-5, -5, -3, -1, -1}, -3);                 // negatives
        verify(new int[]{0, 0, 1, 1, 2, 2, 3, 3, 5}, 5);
        verify(new int[]{1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 7, 7}, 6);
        System.out.println("OK P2770_SingleElementInASortedArray");
    }
}
