import java.util.*;

/** TUF 73 - Aggressive Cows. Place `cows` cows in the given stalls so that the minimum pairwise distance is as large as possible. */
public class P73_AggressiveCows {

    /** Can we place all cows in sorted stalls with every adjacent pair at least `dist` apart? Greedy: take the leftmost legal stall. O(n). */
    static boolean canPlace(int[] stalls, int cows, int dist) {
        int count = 1, last = stalls[0];
        for (int i = 1; i < stalls.length; i++) {
            if (stalls[i] - last >= dist) {
                count++;
                last = stalls[i];
            }
        }
        return count >= cows;
    }

    /** Approach 1: sort, then try every distance 1, 2, 3, ... until placement fails. O(n log n + n * (max - min)) time. */
    static int bruteForce(int[] stalls, int cows) {
        int[] a = stalls.clone();
        Arrays.sort(a);
        int n = a.length;
        int best = 0;
        for (int d = 1; d <= a[n - 1] - a[0]; d++) {
            if (canPlace(a, cows, d)) best = d;
            else break;
        }
        return best;
    }

    /** Approach 2: sort, then binary search the largest feasible distance in [1, max - min]. O(n log n + n log(max - min)) time. */
    static int optimal(int[] stalls, int cows) {
        int[] a = stalls.clone();
        Arrays.sort(a);
        int n = a.length;
        int lo = 1, hi = a[n - 1] - a[0], ans = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (canPlace(a, cows, mid)) { ans = mid; lo = mid + 1; }
            else hi = mid - 1;
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] stalls, int cows, int expected) {
        String in = Arrays.toString(stalls) + " cows=" + cows;
        check(bruteForce(stalls, cows) == expected, "bruteForce " + in);
        check(optimal(stalls, cows) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{0, 3, 4, 7, 10, 9}, 4, 3);
        verify(new int[]{1, 2, 8, 4, 9}, 3, 3);
        verify(new int[]{1, 2, 3}, 3, 1);                 // cows == stalls: smallest adjacent gap
        verify(new int[]{4, 2, 1, 3, 6}, 2, 5);           // two cows: the two extreme stalls
        verify(new int[]{10, 1, 2, 7, 5}, 3, 4);
        verify(new int[]{0, 1000}, 2, 1000);              // unsorted input is fine, only two stalls
        verify(new int[]{5, 17, 100, 11}, 2, 95);
        verify(new int[]{1, 2, 4, 8, 9}, 3, 3);
        System.out.println("OK P73_AggressiveCows");
    }
}
