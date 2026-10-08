import java.util.*;

/** TUF 851 - Print Longest Increasing Subsequence. Return the elements of the index-wise lexicographically smallest longest strictly increasing subsequence. */
public class P851_PrintLongestIncreasingSubsequence {

    /** Approach 1: recursion that returns the best subsequence itself (as indices). O(2^n * n) time, O(n) stack. */
    static List<Integer> bruteForce(int[] a) {
        List<Integer> res = new ArrayList<>();
        for (int i : best(0, -1, a)) res.add(a[i]);
        return res;
    }

    /** Index-wise smallest longest increasing subsequence of a[i..] using only values above a[prev]; returns indices. */
    static List<Integer> best(int i, int prev, int[] a) {
        if (i == a.length) return new ArrayList<>();
        List<Integer> skip = best(i + 1, prev, a);
        if (prev != -1 && a[i] <= a[prev]) return skip;                 // a[i] cannot be taken
        List<Integer> take = best(i + 1, i, a);
        take.add(0, i);
        return take.size() >= skip.size() ? take : skip;               // on a tie, take puts the smaller index first
    }

    /** Approach 2: dp[i] = LIS ending at i, parent[i] = previous index on that subsequence; walk back from the end. O(n^2) time, O(n) space. */
    static List<Integer> dpWithParent(int[] a) {
        int n = a.length;
        List<Integer> res = new ArrayList<>();
        if (n == 0) return res;
        int[] dp = new int[n], parent = new int[n];
        int last = 0;                                                   // first index where the maximum length ends
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            parent[i] = i;                                              // i points to itself: start of a subsequence
            for (int j = 0; j < i; j++) {
                if (a[j] < a[i] && dp[j] + 1 > dp[i]) {                 // strict '>' keeps the smallest such j
                    dp[i] = dp[j] + 1;
                    parent[i] = j;
                }
            }
            if (dp[i] > dp[last]) last = i;
        }
        res.add(a[last]);
        while (parent[last] != last) {
            last = parent[last];
            res.add(a[last]);
        }
        Collections.reverse(res);
        return res;
    }

    /** Approach 3: compute dp[i] with binary search on tails, group indices by dp value, rebuild with binary search. O(n log n) time, O(n) space. */
    static List<Integer> binarySearchLevels(int[] a) {
        int n = a.length;
        int[] tails = new int[n];                                       // tails[k] = smallest last value of an increasing subsequence of length k + 1
        List<List<Integer>> levels = new ArrayList<>();                 // levels.get(k) = indices with dp = k + 1, in index order
        int len = 0;
        for (int i = 0; i < n; i++) {
            int lo = 0, hi = len;                                       // first k with tails[k] >= a[i]
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < a[i]) lo = mid + 1; else hi = mid;
            }
            tails[lo] = a[i];
            if (lo == len) {
                len++;
                levels.add(new ArrayList<>());
            }
            levels.get(lo).add(i);                                      // dp[i] = lo + 1
        }
        List<Integer> res = new ArrayList<>();
        if (len == 0) return res;
        int cur = levels.get(len - 1).get(0);                           // first index with the maximum length
        res.add(a[cur]);
        for (int k = len - 2; k >= 0; k--) {
            List<Integer> level = levels.get(k);                        // values here never increase left to right
            int lo = 0, hi = level.size() - 1;                          // first position whose value is below a[cur]
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (a[level.get(mid)] < a[cur]) hi = mid; else lo = mid + 1;
            }
            cur = level.get(lo);
            res.add(a[cur]);
        }
        Collections.reverse(res);
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, List<Integer> expected) {
        String in = Arrays.toString(a);
        check(bruteForce(a).equals(expected), "bruteForce failed for " + in + ": " + bruteForce(a));
        check(dpWithParent(a).equals(expected), "dpWithParent failed for " + in + ": " + dpWithParent(a));
        check(binarySearchLevels(a).equals(expected), "binarySearchLevels failed for " + in + ": " + binarySearchLevels(a));
    }

    public static void main(String[] args) {
        verify(new int[]{10, 9, 2, 5, 3, 7, 101, 18}, List.of(2, 5, 7, 101));       // 4 LIS exist; indices 2,3,5,6 come first
        verify(new int[]{10, 22, 9, 33, 21, 50, 41, 60, 80, 3}, List.of(10, 22, 33, 50, 60, 80));
        verify(new int[]{1, 3, 2, 4}, List.of(1, 3, 4));                            // 3 appears before 2
        verify(new int[]{5, 4, 3}, List.of(5));                                     // every single element is an LIS; index 0 wins
        verify(new int[]{3, 3, 3}, List.of(3));                                     // strictly increasing
        verify(new int[]{0, 8, 4, 12, 2, 10, 6, 14, 1, 9}, List.of(0, 8, 12, 14));
        verify(new int[]{7}, List.of(7));                                           // edge: one element
        verify(new int[]{}, List.of());                                             // edge: empty

        Random rnd = new Random(851);
        for (int t = 0; t < 400; t++) {
            int[] a = new int[rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10);
            verify(a, bruteForce(a));
        }
        int[] big = new int[3000];
        for (int i = 0; i < big.length; i++) big[i] = (i * 7919) % 3001;
        check(dpWithParent(big).equals(binarySearchLevels(big)), "large input mismatch");
        System.out.println("OK P851_PrintLongestIncreasingSubsequence");
    }
}
