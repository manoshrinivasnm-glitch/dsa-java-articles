import java.util.*;

/** TUF 2770 - Single element in a Sorted Array. Every value appears exactly twice except one; return that value. */
public class P2770_SingleElementInASortedArray {

    /** Approach 1: linear scan, the element that differs from both neighbours is the answer. O(n) time, O(1) space. */
    static int bruteForce(int[] arr) {
        int n = arr.length;
        if (n == 1) return arr[0];
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                if (arr[0] != arr[1]) return arr[0];
            } else if (i == n - 1) {
                if (arr[n - 1] != arr[n - 2]) return arr[n - 1];
            } else if (arr[i] != arr[i - 1] && arr[i] != arr[i + 1]) {
                return arr[i];
            }
        }
        return -1;
    }

    /** Approach 2: XOR every element; equal pairs cancel to 0 and only the single value survives. O(n) time, O(1) space. */
    static int better(int[] arr) {
        int x = 0;
        for (int v : arr) x ^= v;
        return x;
    }

    /** Approach 3: binary search using the index parity of the pairs. O(log n) time, O(1) space. */
    static int optimal(int[] arr) {
        int n = arr.length;
        if (n == 1) return arr[0];
        if (arr[0] != arr[1]) return arr[0];
        if (arr[n - 1] != arr[n - 2]) return arr[n - 1];
        int lo = 1, hi = n - 2;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] != arr[mid - 1] && arr[mid] != arr[mid + 1]) return arr[mid];
            // Left of the single element every pair starts at an even index: (0,1), (2,3), ...
            boolean inLeftHalf = (mid % 2 == 1) ? arr[mid] == arr[mid - 1] : arr[mid] == arr[mid + 1];
            if (inLeftHalf) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
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
