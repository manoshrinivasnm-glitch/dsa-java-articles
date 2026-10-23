import java.util.*;

/** TUF 2771 - The N-th root of an integer. Return the integer x with x^n == m, or -1 if none exists. */
public class P2771_TheNthRootOfAnInteger {

    /** Compares x^n with m without overflow: -1 if x^n < m, 0 if x^n == m, 1 if x^n > m. */
    static int comparePower(long x, int n, long m) {
        long result = 1;                              // running product x^i
        for (int i = 0; i < n; i++) {                 // multiply in x, n times
            result *= x;
            if (result > m) return 1;                 // stop early: one more multiplication could overflow
        }
        return result == m ? 0 : -1;                  // exact match, or still below m
    }

    /** Approach 1: try x = 1, 2, 3, ... until x^n reaches or passes m. O(n * m^(1/n)) time, O(1) space. */
    static int bruteForce(int n, int m) {
        for (long x = 1; ; x++) {                     // no upper bound needed: the loop always returns
            int c = comparePower(x, n, m);            // how x^n compares with m
            if (c == 0) return (int) x;               // found the exact root
            if (c > 0) return -1;                     // x^n jumped past m, so no integer root exists
        }
    }

    /** Approach 2: binary search x in [1, m]; x^n is increasing in x. O(n log m) time, O(1) space. */
    static int optimal(int n, int m) {
        long lo = 1, hi = m;                          // the root, if any, lies in [1, m]
        while (lo <= hi) {
            long mid = lo + (hi - lo) / 2;            // middle without overflow
            int c = comparePower(mid, n, m);
            if (c == 0) return (int) mid;             // mid^n == m: mid is the root
            if (c < 0) lo = mid + 1; else hi = mid - 1;   // too small: go right; too big: go left
        }
        return -1;                                    // no integer root exists
    }

    /** Variant: the real n-th root of m, by bisection on doubles. 100 halvings, each costing O(n). */
    static double realRoot(int n, int m) {
        double lo = 0, hi = Math.max(1.0, m);         // the root of m >= 0 lies in [0, max(1, m)]
        for (int iter = 0; iter < 100; iter++) {      // each step halves the interval
            double mid = (lo + hi) / 2, p = 1;        // test the midpoint
            for (int i = 0; i < n; i++) p *= mid;     // p = mid^n
            if (p < m) lo = mid; else hi = mid;       // keep the half that still contains the root
        }
        return hi;                                    // interval is now far below double precision
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int m, int expected) {
        check(bruteForce(n, m) == expected, "bruteForce n=" + n + " m=" + m + " got " + bruteForce(n, m));
        check(optimal(n, m) == expected, "optimal n=" + n + " m=" + m + " got " + optimal(n, m));
        double r = realRoot(n, m);
        check(Math.abs(r - Math.pow(m, 1.0 / n)) < 1e-6, "realRoot n=" + n + " m=" + m + " got " + r);
        if (expected != -1) check(Math.abs(r - expected) < 1e-6, "realRoot should agree with the integer root");
    }

    public static void main(String[] args) {
        verify(3, 27, 3);
        verify(4, 69, -1);                 // 2^4 = 16 < 69 < 81 = 3^4
        verify(1, 14, 14);                 // first root is the number itself
        verify(2, 1, 1);                   // edge: m = 1
        verify(30, 1, 1);                  // edge: large n, m = 1
        verify(10, 1024, 2);
        verify(29, 536_870_912, 2);        // 2^29
        verify(30, 1_000_000_000, -1);     // 2^30 > 10^9, so even x = 2 overshoots
        verify(2, 999_950_884, 31622);     // 31622^2
        verify(2, 1_000_000_000, -1);      // 31622^2 < 10^9 < 31623^2
        verify(3, 1_000_000_000, 1000);
        verify(9, 1_000_000_000, 10);
        verify(2, Integer.MAX_VALUE, -1);  // mid * mid would overflow int without long arithmetic
        check(Math.abs(realRoot(2, 2) - 1.41421356) < 1e-6, "sqrt(2)");
        check(Math.abs(realRoot(3, 10) - 2.15443469) < 1e-6, "cbrt(10)");
        System.out.println("OK P2771_TheNthRootOfAnInteger");
    }
}
