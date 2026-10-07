import java.util.*;

/** TUF 344 - Highest Occurring Element in an Array. Return the most frequent value; on a tie, the smallest such value. */
public class P344_HighestOccurringElementInAnArray {

    /** Approach 1: count every element by rescanning the array. O(n^2) time, O(1) space. */
    static int bruteForce(int[] arr) {
        int n = arr.length;
        int best = arr[0], bestCount = 0;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) if (arr[j] == arr[i]) count++;
            if (count > bestCount || (count == bestCount && arr[i] < best)) {
                best = arr[i];
                bestCount = count;
            }
        }
        return best;
    }

    /** Approach 2: sort a copy so equal values sit together, then measure each run. O(n log n) time, O(n) for the copy. */
    static int better(int[] arr) {
        int[] a = arr.clone();
        Arrays.sort(a);
        int best = a[0], bestCount = 0;
        int i = 0;
        while (i < a.length) {
            int j = i;
            while (j < a.length && a[j] == a[i]) j++;         // a[i..j-1] is one run of equal values
            int count = j - i;
            if (count > bestCount) {                          // strict >, so an earlier (smaller) value keeps a tie
                best = a[i];
                bestCount = count;
            }
            i = j;
        }
        return best;
    }

    /** Approach 3: one pass to count with a hash map, one pass over the map to pick the winner. O(n) average time, O(distinct values) space. */
    static int optimal(int[] arr) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int v : arr) freq.merge(v, 1, Integer::sum);
        int best = arr[0], bestCount = 0;
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            int value = e.getKey(), count = e.getValue();
            if (count > bestCount || (count == bestCount && value < best)) {
                best = value;
                bestCount = count;
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int expected) {
        int[] original = arr.clone();
        check(bruteForce(arr) == expected, "bruteForce failed for " + Arrays.toString(arr));
        check(better(arr) == expected, "better failed for " + Arrays.toString(arr));
        check(optimal(arr) == expected, "optimal failed for " + Arrays.toString(arr));
        check(Arrays.equals(arr, original), "input must not be modified: " + Arrays.toString(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 2, 3, 3, 3}, 3);
        verify(new int[]{1, 1, 2, 2}, 1);                                   // tie: smallest value wins
        verify(new int[]{2, 2, 1, 1, 3, 3, 3, 1}, 1);                       // 1 and 3 both occur 3 times
        verify(new int[]{4, 4, -1, -1, -1}, -1);                            // negatives
        verify(new int[]{3, 1, 2}, 1);                                      // all distinct: the smallest
        verify(new int[]{5}, 5);                                            // edge: single element
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE);
        verify(new int[]{7, 7, 7, 7}, 7);
        System.out.println("OK P344_HighestOccurringElementInAnArray");
    }
}
