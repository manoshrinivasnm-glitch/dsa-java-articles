import java.util.*;

/** TUF 91 - Find Nth root of a number. Return the integer x with x^n == m, or -1 if no such integer exists. */
public class P91_FindNthRootOfANumber {

    /** Compares x^n with m without overflow: -1 if x^n < m, 0 if equal, 1 if greater. O(n) time. */
    static int comparePower(int x, int n, int m) {
        long ans = 1;
        for (int i = 0; i < n; i++) {
            ans *= x;
            if (ans > m) return 1;    // already too big; with x >= 1 it can only grow further
        }
        return ans == m ? 0 : -1;
    }

    /** Approach 1: try x = 1, 2, 3, ... until x^n reaches or passes m. O(m^(1/n) * n) time, O(1) space. */
    static int bruteForce(int n, int m) {
        for (int x = 1; x <= m; x++) {
            int c = comparePower(x, n, m);
            if (c == 0) return x;
            if (c > 0) return -1;     // x^n already exceeds m, so no larger x can work either
        }
        return -1;
    }

    /** Approach 2: binary search on x in [1, m]; x^n grows with x, so the comparison is monotonic. O(n log m) time, O(1) space. */
    static int optimal(int n, int m) {
        int lo = 1, hi = m;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int c = comparePower(mid, n, m);
            if (c == 0) return mid;
            if (c < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int m, int expected) {
        check(bruteForce(n, m) == expected, "bruteForce n=" + n + " m=" + m);
        check(optimal(n, m) == expected, "optimal n=" + n + " m=" + m);
    }

    public static void main(String[] args) {
        verify(3, 27, 3);
        verify(4, 69, -1);                   // 2^4 = 16 < 69 < 81 = 3^4
        verify(2, 1, 1);                     // 1 is its own root for every n
        verify(5, 1, 1);
        verify(1, 14, 14);                   // n = 1: the number itself
        verify(2, 1_000_000_000, -1);        // 31622^2 = 999950884, 31623^2 = 1000014129
        verify(3, 1_000_000_000, 1000);
        verify(30, 1_073_741_824, 2);        // 2^30; naive int power would overflow for mid >= 3
        verify(2, 2_147_395_600, 46340);
        verify(9, 512, 2);
        verify(5, 3125, 5);
        verify(6, 1_000_000, 10);
        System.out.println("OK P91_FindNthRootOfANumber");
    }
}
