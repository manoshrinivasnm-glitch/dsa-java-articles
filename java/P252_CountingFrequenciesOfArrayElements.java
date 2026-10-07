import java.util.*;

/** TUF 252 - Counting Frequencies of Array Elements. For every distinct value, how many times does it occur? */
public class P252_CountingFrequenciesOfArrayElements {

    /** Approach 1: for each unvisited element, scan the rest of the array and count copies. O(n^2) time, O(n) for the visited flags. */
    static Map<Integer, Integer> bruteForce(int[] arr) {
        int n = arr.length;
        boolean[] visited = new boolean[n];
        Map<Integer, Integer> freq = new LinkedHashMap<>();        // insertion order = order of first appearance
        for (int i = 0; i < n; i++) {
            if (visited[i]) continue;                               // already counted as a copy of an earlier element
            int count = 1;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] == arr[i]) {
                    visited[j] = true;
                    count++;
                }
            }
            freq.put(arr[i], count);
        }
        return freq;
    }

    /** Approach 2: counting array, when values are non-negative and bounded by a small maxValue. O(n + maxValue) time and space. */
    static Map<Integer, Integer> countingArray(int[] arr) {
        int maxValue = 0;
        for (int v : arr) maxValue = Math.max(maxValue, v);
        int[] count = new int[maxValue + 1];
        for (int v : arr) count[v]++;                               // one pass to count
        Map<Integer, Integer> freq = new LinkedHashMap<>();
        for (int v : arr) {
            if (!freq.containsKey(v)) freq.put(v, count[v]);        // report each value once, at its first appearance
        }
        return freq;
    }

    /** Approach 3: a hash map works for any values, negative or huge. O(n) average time, O(distinct values) space. */
    static Map<Integer, Integer> hashMap(int[] arr) {
        Map<Integer, Integer> freq = new LinkedHashMap<>();
        for (int v : arr) freq.merge(v, 1, Integer::sum);           // absent -> 1, present -> old + 1
        return freq;
    }

    /** Approach 4: O(1) extra space when every value lies in [1, n]: store the counts inside the array itself. O(n) time. */
    static Map<Integer, Integer> inPlace(int[] input) {
        int[] arr = input.clone();                                  // keep the caller's array intact
        int n = arr.length;
        for (int i = 0; i < n; i++) arr[i]--;                       // shift values to [0, n - 1] so they are valid indices
        for (int i = 0; i < n; i++) arr[arr[i] % n] += n;           // arr[i] % n is the original value here; add n at its slot
        Map<Integer, Integer> freq = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            int value = input[i];
            if (!freq.containsKey(value)) freq.put(value, arr[value - 1] / n);   // slot v - 1 holds n * count(v) + junk < n
        }
        return freq;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void checkSame(Map<Integer, Integer> got, Map<Integer, Integer> expected, List<Integer> order, String name, int[] arr) {
        check(got.equals(expected), name + " wrong counts for " + Arrays.toString(arr) + ": " + got);
        check(new ArrayList<>(got.keySet()).equals(order), name + " wrong order for " + Arrays.toString(arr) + ": " + got);
    }

    static void verify(int[] arr, int[][] expectedPairs, boolean smallNonNegative, boolean valuesInOneToN) {
        Map<Integer, Integer> expected = new LinkedHashMap<>();
        for (int[] p : expectedPairs) expected.put(p[0], p[1]);
        List<Integer> order = new ArrayList<>(expected.keySet());
        int[] original = arr.clone();
        checkSame(bruteForce(arr), expected, order, "bruteForce", arr);
        checkSame(hashMap(arr), expected, order, "hashMap", arr);
        if (smallNonNegative) checkSame(countingArray(arr), expected, order, "countingArray", arr);
        if (valuesInOneToN) checkSame(inPlace(arr), expected, order, "inPlace", arr);
        check(Arrays.equals(arr, original), "input must not be modified: " + Arrays.toString(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{10, 5, 10, 15, 10, 5}, new int[][]{{10, 3}, {5, 2}, {15, 1}}, true, false);
        verify(new int[]{}, new int[][]{}, true, true);                                      // edge: empty array
        verify(new int[]{42}, new int[][]{{42, 1}}, true, false);                            // edge: single element
        verify(new int[]{1, 1, 1, 1}, new int[][]{{1, 4}}, true, true);                      // one value only
        verify(new int[]{2, 3, 2, 3, 5, 1, 4}, new int[][]{{2, 2}, {3, 2}, {5, 1}, {1, 1}, {4, 1}}, true, true);
        verify(new int[]{5, 4, 3, 2, 1}, new int[][]{{5, 1}, {4, 1}, {3, 1}, {2, 1}, {1, 1}}, true, true);   // all distinct
        verify(new int[]{-1, 2, -1, 0}, new int[][]{{-1, 2}, {2, 1}, {0, 1}}, false, false);  // negatives: no counting array
        verify(new int[]{1_000_000_000, 1_000_000_000, 7}, new int[][]{{1_000_000_000, 2}, {7, 1}}, false, false); // huge values
        System.out.println("OK P252_CountingFrequenciesOfArrayElements");
    }
}
