import java.util.*;

/** TUF 95 - Minimum days to make M bouquets. Each bouquet needs k adjacent bloomed flowers; return the earliest day, or -1. */
public class P95_MinimumDaysToMakeMBouquets {

    /** Can we cut m bouquets of k adjacent flowers if we wait until `day`? Greedy left-to-right scan. O(n). */
    static boolean possible(int[] bloom, int day, int m, int k) {
        int bouquets = 0, run = 0;
        for (int b : bloom) {
            if (b <= day) {
                run++;
                if (run == k) { bouquets++; run = 0; }
            } else {
                run = 0;
            }
        }
        return bouquets >= m;
    }

    /** Approach 1: try every day from the earliest to the latest bloom day. O((max - min) * n) time, O(1) space. */
    static int bruteForce(int[] bloom, int m, int k) {
        int n = bloom.length;
        if ((long) m * k > n) return -1;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int b : bloom) { min = Math.min(min, b); max = Math.max(max, b); }
        for (int day = min; day <= max; day++) {
            if (possible(bloom, day, m, k)) return day;
        }
        return -1;
    }

    /** Approach 2: binary search on the day in [min, max]; "possible" is monotonic in the day. O(n log(max - min)) time, O(1) space. */
    static int optimal(int[] bloom, int m, int k) {
        int n = bloom.length;
        if ((long) m * k > n) return -1;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int b : bloom) { min = Math.min(min, b); max = Math.max(max, b); }
        int lo = min, hi = max, ans = max;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (possible(bloom, mid, m, k)) { ans = mid; hi = mid - 1; }
            else lo = mid + 1;
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] bloom, int m, int k, int expected) {
        String in = Arrays.toString(bloom) + " m=" + m + " k=" + k;
        check(bruteForce(bloom, m, k) == expected, "bruteForce " + in);
        check(optimal(bloom, m, k) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 10, 3, 10, 2}, 3, 1, 3);
        verify(new int[]{1, 10, 3, 10, 2}, 3, 2, -1);                 // 6 flowers needed, only 5 exist
        verify(new int[]{7, 7, 7, 7, 12, 7, 7}, 2, 3, 12);
        verify(new int[]{1, 10, 2, 9, 3, 8, 4, 7, 5, 6}, 4, 2, 9);
        verify(new int[]{1_000_000_000, 1_000_000_000}, 1, 1, 1_000_000_000);
        verify(new int[]{5}, 1, 1, 5);                                // single flower
        verify(new int[]{3, 1, 2}, 1, 3, 3);                          // one bouquet from the whole garden
        verify(new int[]{1, 2, 3}, 1_000_000, 1_000_000, -1);         // m * k overflows int, must use long
        System.out.println("OK P95_MinimumDaysToMakeMBouquets");
    }
}
