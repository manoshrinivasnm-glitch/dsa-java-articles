import java.util.*;

/** TUF 161 - Capacity to Ship Packages Within D Days. Minimum ship capacity so that the packages, in order, ship within `days` days. */
public class P161_CapacityToShipPackagesWithinDDays {

    /** Days needed with capacity cap, loading greedily: a new day starts only when the next package does not fit. O(n). */
    static int daysNeeded(int[] weights, int cap) {
        int days = 1;
        long load = 0;
        for (int w : weights) {
            if (load + w > cap) { days++; load = 0; }
            load += w;
        }
        return days;
    }

    /** Approach 1: try every capacity from the heaviest package up to the total weight. O(n * (sum - max)) time, O(1) space. */
    static int bruteForce(int[] weights, int days) {
        int max = 0, sum = 0;
        for (int w : weights) { max = Math.max(max, w); sum += w; }
        for (int cap = max; cap <= sum; cap++) {
            if (daysNeeded(weights, cap) <= days) return cap;
        }
        return sum;
    }

    /** Approach 2: binary search on the capacity in [max, sum]; daysNeeded only decreases as cap grows. O(n log(sum)) time, O(1) space. */
    static int optimal(int[] weights, int days) {
        int max = 0, sum = 0;
        for (int w : weights) { max = Math.max(max, w); sum += w; }
        int lo = max, hi = sum, ans = sum;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (daysNeeded(weights, mid) <= days) { ans = mid; hi = mid - 1; }
            else lo = mid + 1;
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] weights, int days, int expected) {
        String in = Arrays.toString(weights) + " days=" + days;
        check(bruteForce(weights, days) == expected, "bruteForce " + in);
        check(optimal(weights, days) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5, 15);
        verify(new int[]{3, 2, 2, 4, 1, 4}, 3, 6);
        verify(new int[]{1, 2, 3, 1, 1}, 4, 3);
        verify(new int[]{10}, 1, 10);                                 // single package
        verify(new int[]{5, 5, 5, 5}, 4, 5);                          // one package per day: answer is the max
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 1, 55);      // one day: answer is the sum
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 10, 10);
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 20, 10);     // more days than packages still needs the max
        System.out.println("OK P161_CapacityToShipPackagesWithinDDays");
    }
}
