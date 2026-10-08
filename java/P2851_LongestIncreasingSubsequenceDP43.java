import java.util.*;

/** TUF 2851 - Longest Increasing Subsequence (DP-43): the O(n log n) solution with binary search. Return the length of the longest strictly increasing subsequence. */
public class P2851_LongestIncreasingSubsequenceDP43 {

    /** Approach 1: baseline DP, dp[i] = LIS ending at index i. O(n^2) time, O(n) space. */
    static int quadraticDp(int[] a) {
        int n = a.length, best = 0;
        int[] dp = new int[n];
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (a[j] < a[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    /** Approach 2: one tails array, replacement position found by a linear scan. O(n * L) time, O(L) space. */
    static int linearTails(int[] a) {
        List<Integer> tails = new ArrayList<>();
        for (int x : a) {
            int k = 0;
            while (k < tails.size() && tails.get(k) < x) k++;             // first tail >= x
            if (k == tails.size()) tails.add(x);                          // x extends the longest subsequence
            else tails.set(k, x);                                         // x is a cheaper ending for length k + 1
        }
        return tails.size();
    }

    /** Approach 3: the same tails array, position found by a hand-written lower bound. O(n log n) time, O(n) space. */
    static int binarySearchTails(int[] a) {
        int[] tails = new int[a.length];
        int len = 0;
        for (int x : a) {
            int lo = 0, hi = len;                                         // search tails[0..len-1]
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < x) lo = mid + 1; else hi = mid;
            }
            tails[lo] = x;                                                // lo == len means append
            if (lo == len) len++;
        }
        return len;
    }

    /** Approach 4: the tails as a TreeSet; ceiling(x) is the tail that x replaces. O(n log n) time, O(n) space. */
    static int treeSetTails(int[] a) {
        TreeSet<Integer> tails = new TreeSet<>();
        for (int x : a) {
            Integer ceil = tails.ceiling(x);                              // smallest tail >= x
            if (ceil != null) tails.remove(ceil);
            tails.add(x);
        }
        return tails.size();
    }

    /** Variant: longest non-decreasing subsequence; equal values may follow each other, so search for the first tail > x. O(n log n). */
    static int longestNonDecreasing(int[] a) {
        int[] tails = new int[a.length];
        int len = 0;
        for (int x : a) {
            int lo = 0, hi = len;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] <= x) lo = mid + 1; else hi = mid;          // upper bound instead of lower bound
            }
            tails[lo] = x;
            if (lo == len) len++;
        }
        return len;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int expected) {
        String in = Arrays.toString(a);
        check(quadraticDp(a) == expected, "quadraticDp failed for " + in);
        check(linearTails(a) == expected, "linearTails failed for " + in);
        check(binarySearchTails(a) == expected, "binarySearchTails failed for " + in);
        check(treeSetTails(a) == expected, "treeSetTails failed for " + in);
    }

    /** Exponential reference for small inputs: longest increasing (strict) or non-decreasing subsequence. */
    static int reference(int[] a, int i, long prev, boolean strict) {
        if (i == a.length) return 0;
        int best = reference(a, i + 1, prev, strict);
        if (strict ? a[i] > prev : a[i] >= prev) best = Math.max(best, 1 + reference(a, i + 1, a[i], strict));
        return best;
    }

    public static void main(String[] args) {
        verify(new int[]{10, 9, 2, 5, 3, 7, 101, 18}, 4);
        verify(new int[]{1, 7, 8, 4, 5, 6, -1, 9}, 5);          // 1, 4, 5, 6, 9 (tails end as [-1, 4, 5, 6, 9])
        verify(new int[]{0, 1, 0, 3, 2, 3}, 4);
        verify(new int[]{2, 2, 2}, 1);                          // strict: duplicates do not chain
        verify(new int[]{5, 4, 3, 2, 1}, 1);                    // reverse sorted
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, 2);   // extremes
        verify(new int[]{3}, 1);                                // edge: one element
        verify(new int[]{}, 0);                                 // edge: empty
        check(longestNonDecreasing(new int[]{2, 2, 2}) == 3, "non-decreasing duplicates");
        check(longestNonDecreasing(new int[]{1, 3, 2, 2, 4}) == 4, "non-decreasing 1,2,2,4");

        Random rnd = new Random(2851);
        for (int t = 0; t < 400; t++) {
            int[] a = new int[rnd.nextInt(13)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(12) - 3;
            verify(a, reference(a, 0, Long.MIN_VALUE, true));
            check(longestNonDecreasing(a) == reference(a, 0, Long.MIN_VALUE, false), "non-decreasing failed for " + Arrays.toString(a));
        }
        int[] big = new int[200_000];                           // only the n log n methods at this size
        for (int i = 0; i < big.length; i++) big[i] = (int) ((i * 1_000_003L) % 200_003);
        check(binarySearchTails(big) == treeSetTails(big), "large input mismatch");
        int[] sorted = new int[100_000];
        for (int i = 0; i < sorted.length; i++) sorted[i] = i;
        check(binarySearchTails(sorted) == 100_000 && treeSetTails(sorted) == 100_000, "sorted input");
        System.out.println("OK P2851_LongestIncreasingSubsequenceDP43");
    }
}
