import java.util.*;

/** TUF 805 - Painter's Partition. k painters each take a contiguous block of boards and paint one unit of length per unit of time; minimise the time until every board is painted. */
public class P805_PaintersPartition {

    /** Painters needed when nobody may paint more than maxLength units, handing out boards greedily from the left. O(n). */
    static int paintersNeeded(int[] boards, long maxLength) {
        int painters = 1;
        long load = 0;
        for (int b : boards) {
            if (load + b > maxLength) { painters++; load = 0; }
            load += b;
        }
        return painters;
    }

    /** Approach 1: try every time limit from the longest board up to the total length. O(n * (sum - max)) time, O(1) space. */
    static long bruteForce(int[] boards, int k) {
        long max = 0, sum = 0;
        for (int b : boards) { max = Math.max(max, b); sum += b; }
        for (long limit = max; limit <= sum; limit++) {
            if (paintersNeeded(boards, limit) <= k) return limit;
        }
        return sum;
    }

    /** Approach 2: binary search on the time limit in [max, sum]; paintersNeeded only falls as the limit grows. O(n log(sum)) time, O(1) space. */
    static long optimal(int[] boards, int k) {
        long max = 0, sum = 0;
        for (int b : boards) { max = Math.max(max, b); sum += b; }
        long lo = max, hi = sum, ans = sum;
        while (lo <= hi) {
            long mid = lo + (hi - lo) / 2;
            if (paintersNeeded(boards, mid) <= k) { ans = mid; hi = mid - 1; }
            else lo = mid + 1;
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] boards, int k, long expected) {
        String in = Arrays.toString(boards) + " k=" + k;
        check(bruteForce(boards, k) == expected, "bruteForce " + in);
        check(optimal(boards, k) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{5, 5, 5, 5}, 2, 10);
        verify(new int[]{10, 20, 30, 40}, 2, 60);
        verify(new int[]{2, 1, 5, 6, 2, 3}, 2, 11);
        verify(new int[]{10, 10, 10, 10}, 3, 20);
        verify(new int[]{48, 90}, 1, 138);                  // one painter: the total length
        verify(new int[]{48, 90}, 2, 90);                   // one board each: the longest board
        verify(new int[]{1, 2, 3, 4, 5}, 10, 5);            // more painters than boards: the extra painters idle
        verify(new int[]{7}, 1, 7);                         // single board
        // the total overflows int here; only the binary search is fast enough to try these
        check(optimal(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000}, 1) == 3_000_000_000L, "optimal overflow k=1");
        check(optimal(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000}, 2) == 2_000_000_000L, "optimal overflow k=2");
        System.out.println("OK P805_PaintersPartition");
    }
}
