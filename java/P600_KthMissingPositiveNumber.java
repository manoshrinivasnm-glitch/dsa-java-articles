import java.util.*;

/** TUF 600 - Kth Missing Positive Number. arr is strictly increasing and positive; return the k-th positive integer not in it. */
public class P600_KthMissingPositiveNumber {

    /** Approach 1: count upwards from 1, skipping values present in the array, until k numbers are missing. O(n + k) time, O(n) space. */
    static int bruteForce(int[] arr, int k) {
        Set<Integer> present = new HashSet<>();     // fast membership test for the array values
        for (int x : arr) present.add(x);
        int missing = 0, num = 0;                   // num walks 1, 2, 3, ...; missing counts the gaps
        while (missing < k) {                       // stop at the k-th missing number
            num++;
            if (!present.contains(num)) missing++;  // num is not in the array
        }
        return num;                                 // the k-th missing number
    }

    /** Approach 2: one pass; every element not larger than the running answer pushes the answer up by one. O(n) time, O(1) space. */
    static int better(int[] arr, int k) {
        for (int x : arr) {   // values in increasing order
            if (x <= k) k++;  // x sits at or below the answer, pushing it up by one
            else break;       // every later x is larger than the answer too
        }
        return k;             // k now holds the answer
    }

    /** Approach 3: binary search on the index using missing(i) = arr[i] - (i + 1). O(log n) time, O(1) space. */
    static int optimal(int[] arr, int k) {
        int lo = 0, hi = arr.length - 1;
        while (lo <= hi) {                       // search until the range is empty
            int mid = lo + (hi - lo) / 2;
            int missing = arr[mid] - (mid + 1);  // positives missing before arr[mid]
            if (missing < k) lo = mid + 1;       // fewer than k gaps so far: answer is to the right
            else hi = mid - 1;                   // k or more gaps: answer is to the left
        }
        return lo + k;                           // lo array values lie below the answer, so it is k + lo
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int k, int expected) {
        String in = Arrays.toString(arr) + " k=" + k;
        check(bruteForce(arr, k) == expected, "bruteForce " + in);
        check(better(arr, k) == expected, "better " + in);
        check(optimal(arr, k) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 4, 7, 11}, 5, 9);
        verify(new int[]{1, 2, 3, 4}, 2, 6);               // nothing missing inside: answer is past the end
        verify(new int[]{5, 6, 7}, 2, 2);                  // answer lies before the first element
        verify(new int[]{1}, 1, 2);                        // single element
        verify(new int[]{2}, 1, 1);
        verify(new int[]{1, 2, 3, 4}, 1000, 1004);
        verify(new int[]{3, 10}, 6, 7);
        verify(new int[]{1, 3, 5, 7, 9}, 3, 6);
        System.out.println("OK P600_KthMissingPositiveNumber");
    }
}
