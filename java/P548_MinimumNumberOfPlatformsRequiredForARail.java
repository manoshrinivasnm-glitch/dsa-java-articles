import java.util.*;

/** TUF 548 - Minimum platforms. Train i occupies a platform from arr[i] to dep[i] inclusive; find the fewest platforms needed. */
public class P548_MinimumNumberOfPlatformsRequiredForARail {

    /** Approach 1: at every arrival moment, count the trains standing in the station. O(n^2) time, O(1) space. */
    static int bruteForce(int[] arr, int[] dep) {
        int n = arr.length, best = 0;
        for (int i = 0; i < n; i++) {
            int present = 0;
            for (int j = 0; j < n; j++) {
                if (arr[j] <= arr[i] && arr[i] <= dep[j]) present++;   // train j is in the station at time arr[i]
            }
            best = Math.max(best, present);
        }
        return best;
    }

    /** Approach 2: sort arrivals and departures separately and sweep the events in time order. O(n log n) time, O(n) space. */
    static int optimal(int[] arr, int[] dep) {
        int[] a = arr.clone(), d = dep.clone();
        Arrays.sort(a);
        Arrays.sort(d);
        int n = a.length, i = 0, j = 0, onPlatform = 0, best = 0;
        while (i < n) {
            if (a[i] <= d[j]) {          // next event is an arrival; on a tie it goes first (both trains need a platform)
                onPlatform++;
                i++;
                best = Math.max(best, onPlatform);
            } else {                     // next event is a departure, which frees a platform
                onPlatform--;
                j++;
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int[] dep, int expected) {
        String in = Arrays.toString(arr) + " / " + Arrays.toString(dep);
        int b = bruteForce(arr, dep), o = optimal(arr, dep);
        check(b == expected, "bruteForce gave " + b + ", expected " + expected + " for " + in);
        check(o == expected, "optimal gave " + o + ", expected " + expected + " for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{900, 940, 950, 1100, 1500, 1800}, new int[]{910, 1200, 1120, 1130, 1900, 2000}, 3);
        verify(new int[]{900, 1100, 1235}, new int[]{1000, 1200, 1240}, 1);            // never two trains at once
        verify(new int[]{1000, 1030}, new int[]{1030, 1100}, 2);                       // arrival at a departure time still clashes
        verify(new int[]{1000, 1000, 1000}, new int[]{1000, 1000, 1000}, 3);           // identical trains
        verify(new int[]{100, 100, 500}, new int[]{1000, 200, 600}, 2);                // long train overlaps two that never meet
        verify(new int[]{1200, 900, 1000}, new int[]{1300, 1100, 1230}, 2);            // unsorted input
        verify(new int[]{1000}, new int[]{1030}, 1);                                   // single train
        verify(new int[]{}, new int[]{}, 0);                                           // no trains
        int[] arr = {1200, 900}, dep = {1300, 1000};
        optimal(arr, dep);
        check(arr[0] == 1200 && dep[0] == 1300, "input arrays must not be reordered");
        System.out.println("OK P548_MinimumNumberOfPlatformsRequiredForARail");
    }
}
