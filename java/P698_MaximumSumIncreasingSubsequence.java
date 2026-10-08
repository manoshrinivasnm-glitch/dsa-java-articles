import java.util.*;

/** TUF 698 - Maximum Sum Increasing Subsequence. Positive integers; return the largest sum of a strictly increasing subsequence. */
public class P698_MaximumSumIncreasingSubsequence {

    /** Approach 1: recursion. At each index either skip it or take it if it is larger than the last taken value. O(2^n) time, O(n) stack. */
    static int recursion(int[] arr) {
        return solve(0, -1, arr);
    }

    /** Best sum obtainable from arr[i..] when the last chosen index is prev (-1 means nothing chosen yet). */
    static int solve(int i, int prev, int[] arr) {
        if (i == arr.length) return 0;
        int skip = solve(i + 1, prev, arr);
        int take = 0;
        if (prev == -1 || arr[i] > arr[prev]) take = arr[i] + solve(i + 1, i, arr);
        return Math.max(skip, take);
    }

    /** Approach 2: memoization on (i, prev); prev is shifted by one so -1 fits in the table. O(n^2) time, O(n^2) space. */
    static int memoization(int[] arr) {
        int n = arr.length;
        int[][] memo = new int[n][n + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solveMemo(0, -1, arr, memo);
    }

    static int solveMemo(int i, int prev, int[] arr, int[][] memo) {
        if (i == arr.length) return 0;
        if (memo[i][prev + 1] != -1) return memo[i][prev + 1];
        int skip = solveMemo(i + 1, prev, arr, memo);
        int take = 0;
        if (prev == -1 || arr[i] > arr[prev]) take = arr[i] + solveMemo(i + 1, i, arr, memo);
        return memo[i][prev + 1] = Math.max(skip, take);
    }

    /** Approach 3: tabulation. dp[i] = best sum of an increasing subsequence that ends at index i. O(n^2) time, O(n) space. */
    static int tabulation(int[] arr) {
        int n = arr.length, best = 0;
        int[] dp = new int[n];
        for (int i = 0; i < n; i++) {
            dp[i] = arr[i];                                    // the subsequence containing only arr[i]
            for (int j = 0; j < i; j++) {
                if (arr[j] < arr[i]) dp[i] = Math.max(dp[i], dp[j] + arr[i]);
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    /** Approach 4: same recurrence, but "max dp[j] over smaller values" comes from a Fenwick tree over value ranks. O(n log n) time, O(n) space. */
    static int fenwick(int[] arr) {
        int[] sorted = Arrays.stream(arr).distinct().sorted().toArray();
        int[] tree = new int[sorted.length + 1];               // tree over ranks 1..m, storing prefix maxima of dp
        int best = 0;
        for (int x : arr) {
            int rank = Arrays.binarySearch(sorted, x) + 1;     // 1-based rank of x among distinct values
            int bestBefore = 0;
            for (int r = rank - 1; r > 0; r -= r & -r) bestBefore = Math.max(bestBefore, tree[r]);   // values strictly below x
            int cur = bestBefore + x;                          // dp for this position
            for (int r = rank; r < tree.length; r += r & -r) tree[r] = Math.max(tree[r], cur);
            best = Math.max(best, cur);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int expected) {
        check(recursion(arr) == expected, "recursion " + Arrays.toString(arr) + " got " + recursion(arr));
        check(memoization(arr) == expected, "memoization " + Arrays.toString(arr) + " got " + memoization(arr));
        check(tabulation(arr) == expected, "tabulation " + Arrays.toString(arr) + " got " + tabulation(arr));
        check(fenwick(arr) == expected, "fenwick " + Arrays.toString(arr) + " got " + fenwick(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 101, 2, 3, 100}, 106);
        verify(new int[]{1, 2, 3}, 6);
        verify(new int[]{4}, 4);                                           // single element
        verify(new int[]{}, 0);                                            // empty array
        verify(new int[]{10, 5, 4, 3}, 10);                                // decreasing: best single element
        verify(new int[]{1, 1, 1}, 1);                                     // equal values are not increasing
        verify(new int[]{5, 5, 6}, 11);
        verify(new int[]{4, 6, 1, 3, 8, 4, 6}, 18);                        // 4 + 6 + 8
        verify(new int[]{1, 2, 3, 100, 4, 5, 6, 7}, 106);                  // the longest increasing run is not the best

        // seeded random arrays: all four must agree
        Random rnd = new Random(698);
        for (int t = 0; t < 500; t++) {
            int n = rnd.nextInt(14);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = 1 + rnd.nextInt(20);
            verify(a, recursion(a));
        }
        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = 1 + (int) ((i * 7919L) % 10_000);
        check(fenwick(big) > 0, "fenwick should handle 10^5 elements quickly");
        System.out.println("OK P698_MaximumSumIncreasingSubsequence");
    }
}
