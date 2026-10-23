import java.util.*;

/** TUF 94 - Koko eating bananas. Minimum integer speed k so that every pile is finished within h hours. */
public class P94_KokoEatingBananas {

    /** Hours needed at speed k: at most one pile per hour, so ceil(pile / k) per pile. long: the sum can exceed int. */
    static long hoursNeeded(int[] piles, int k) {
        long hours = 0;                                       // total hours over all piles
        for (int p : piles) hours += ((long) p + k - 1) / k;  // ceil(p / k) using integers only
        return hours;
    }

    /** Approach 1: try every speed 1, 2, 3, ... up to the largest pile. O(n * max) time, O(1) space. */
    static int bruteForce(int[] piles, int h) {
        int max = 0;
        for (int p : piles) max = Math.max(max, p);    // largest pile: speed max empties any pile in 1 hour
        for (int k = 1; k <= max; k++) {               // try speeds from slowest to fastest
            if (hoursNeeded(piles, k) <= h) return k;  // the first speed that fits is the minimum
        }
        return max;                                    // fallback; speed max always fits
    }

    /** Approach 2: binary search on the speed in [1, max]; "fits in h hours" is monotonic in k. O(n log max) time, O(1) space. */
    static int optimal(int[] piles, int h) {
        int max = 0;
        for (int p : piles) max = Math.max(max, p);  // largest pile: an upper bound on the speed
        int lo = 1, hi = max, ans = max;             // speed max always fits, so it is a safe default
        while (lo <= hi) {                           // search until the range is empty
            int mid = lo + (hi - lo) / 2;
            if (hoursNeeded(piles, mid) <= h) { ans = mid; hi = mid - 1; }   // fast enough: try slower
            else lo = mid + 1;                       // too slow: try faster
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] piles, int h, int expected) {
        String in = Arrays.toString(piles) + " h=" + h;
        check(bruteForce(piles, h) == expected, "bruteForce " + in);
        check(optimal(piles, h) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 6, 7, 11}, 8, 4);
        verify(new int[]{30, 11, 23, 4, 20}, 5, 30);        // h == n: the biggest pile must go in one hour
        verify(new int[]{30, 11, 23, 4, 20}, 6, 23);
        verify(new int[]{7}, 7, 1);                          // single pile, plenty of time
        verify(new int[]{7}, 1, 7);                          // single pile, one hour
        verify(new int[]{1_000_000}, 2, 500_000);
        verify(new int[]{1, 1, 1, 1}, 4, 1);
        verify(new int[]{805_306_368, 805_306_368, 805_306_368}, 1_000_000_000, 3); // hour sum overflows int at k = 1
        System.out.println("OK P94_KokoEatingBananas");
    }
}
