import java.util.*;

/** TUF 285 - Number of Greater Elements to the Right. For each query index i, count j > i with arr[j] > arr[i]. */
public class P285_NumberOfGreaterElementsToTheRight {

    /** Approach 1: answer every query by scanning the suffix after its index. O(n * q) time, O(1) extra space. */
    static int[] bruteForce(int[] arr, int[] queries) {
        int[] res = new int[queries.length];         // res[k] = answer for the k-th query
        for (int k = 0; k < queries.length; k++) {   // answer each query independently
            int idx = queries[k], count = 0;         // count = elements after idx that beat arr[idx]
            for (int j = idx + 1; j < arr.length; j++) {   // scan the whole suffix after idx
                if (arr[j] > arr[idx]) count++;      // strictly greater only
            }
            res[k] = count;
        }
        return res;
    }

    /** Approach 2: Fenwick tree over value ranks, filled right to left, then O(1) per query. O(n log n + q) time, O(n) space. */
    static int[] optimal(int[] arr, int[] queries) {
        int n = arr.length;
        int[] vals = arr.clone();                    // sorted copy, used to turn values into ranks
        Arrays.sort(vals);
        int m = 0;                                   // number of distinct values, kept in vals[0..m-1]
        for (int i = 0; i < n; i++) {
            if (m == 0 || vals[i] != vals[m - 1]) vals[m++] = vals[i];   // keep each distinct value once
        }
        int[] tree = new int[m + 1];                 // 1-based Fenwick tree: how many seen elements have each rank
        int[] greater = new int[n];                  // greater[i] = how many later elements beat arr[i]
        for (int i = n - 1; i >= 0; i--) {           // right to left: the tree holds only elements after i
            int rank = Arrays.binarySearch(vals, 0, m, arr[i]) + 1;   // rank of arr[i], from 1 to m
            int notGreater = 0;                      // seen elements with rank <= rank
            for (int r = rank; r > 0; r -= r & -r) notGreater += tree[r];   // prefix sum over ranks 1..rank
            greater[i] = (n - 1 - i) - notGreater;   // everything to the right minus the ones that are not greater
            for (int r = rank; r <= m; r += r & -r) tree[r]++;   // record arr[i] for the indices to its left
        }
        int[] res = new int[queries.length];
        for (int k = 0; k < queries.length; k++) res[k] = greater[queries[k]];   // each query is now a lookup
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int[] queries, int[] expected) {
        check(Arrays.equals(bruteForce(arr, queries), expected), "bruteForce failed on " + Arrays.toString(arr));
        check(Arrays.equals(optimal(arr, queries), expected), "optimal failed on " + Arrays.toString(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{3, 4, 2, 7, 5, 8, 10, 6}, new int[]{0, 5}, new int[]{6, 1});
        verify(new int[]{3, 4, 2, 7, 5, 8, 10, 6}, new int[]{2, 2, 7}, new int[]{5, 5, 0});   // repeated query, last index
        verify(new int[]{1, 2, 3, 4, 1}, new int[]{0, 1, 4}, new int[]{3, 2, 0});
        verify(new int[]{1, 2, 3, 4}, new int[]{0, 1, 2, 3}, new int[]{3, 2, 1, 0});
        verify(new int[]{4, 3, 2, 1}, new int[]{0, 3}, new int[]{0, 0});                   // nothing greater anywhere
        verify(new int[]{5, 5, 5}, new int[]{0, 1, 2}, new int[]{0, 0, 0});                // equal is not greater
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, 0}, new int[]{0, 2}, new int[]{2, 0});   // extreme values
        verify(new int[]{7}, new int[]{0}, new int[]{0});                                  // single element
        verify(new int[]{2, 1}, new int[]{}, new int[]{});                                 // no queries

        Random rnd = new Random(285);                                                      // cross-check on random data
        for (int t = 0; t < 300; t++) {
            int n = 1 + rnd.nextInt(20);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(11) - 5;
            int[] q = new int[n];
            for (int i = 0; i < n; i++) q[i] = i;                                          // every index
            check(Arrays.equals(bruteForce(a, q), optimal(a, q)), "random mismatch on " + Arrays.toString(a));
        }
        System.out.println("OK P285_NumberOfGreaterElementsToTheRight");
    }
}
