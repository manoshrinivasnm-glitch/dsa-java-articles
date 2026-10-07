import java.util.*;

/** TUF 2855 - Pow(x, n). Compute x raised to the integer power n, where n may be negative, as a double. */
public class P2855_PowXN {

    /** Approach 1: multiply x by itself |n| times. O(|n|) time, O(1) space; only usable for small |n|. */
    static double bruteForce(double x, int n) {
        long times = Math.abs((long) n);             // cast first: Math.abs(Integer.MIN_VALUE) is still negative
        double result = 1.0;
        for (long i = 0; i < times; i++) result *= x;
        return n < 0 ? 1.0 / result : result;
    }

    /** Approach 2: recursive fast exponentiation. x^n = (x^(n/2))^2, times one extra x when n is odd. O(log n) time, O(log n) stack. */
    static double optimalRecursive(double x, int n) {
        long e = n;                                   // widen before negating: -Integer.MIN_VALUE does not fit in an int
        if (e < 0) {
            x = 1.0 / x;
            e = -e;
        }
        return power(x, e);
    }

    static double power(double x, long e) {
        if (e == 0) return 1.0;                       // base case: anything to the power 0 is 1
        double half = power(x, e / 2);                // one subproblem of half the size
        return e % 2 == 0 ? half * half : half * half * x;
    }

    /** Approach 3: iterative binary exponentiation; walk the bits of the exponent from least significant upward. O(log n) time, O(1) space. */
    static double optimalIterative(double x, int n) {
        long e = n;
        if (e < 0) {
            x = 1.0 / x;
            e = -e;
        }
        double result = 1.0, base = x;                // after k rounds, base = x^(2^k)
        while (e > 0) {
            if ((e & 1) == 1) result *= base;         // this bit is set: x^(2^k) belongs in the product
            base *= base;
            e >>= 1;
        }
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Floating-point results may differ in the last bits depending on the multiplication order. */
    static boolean close(double actual, double expected) {
        if (actual == expected) return true;
        return Math.abs(actual - expected) <= 1e-9 * Math.max(1.0, Math.abs(expected));
    }

    /** Small |n|: all three approaches must agree with expected. */
    static void verify(double x, int n, double expected) {
        check(close(bruteForce(x, n), expected), "bruteForce(" + x + ", " + n + ") = " + bruteForce(x, n));
        verifyFast(x, n, expected);
    }

    /** Any n, including |n| near 2^31 where the brute force would run for minutes. */
    static void verifyFast(double x, int n, double expected) {
        check(close(optimalRecursive(x, n), expected), "optimalRecursive(" + x + ", " + n + ") = " + optimalRecursive(x, n));
        check(close(optimalIterative(x, n), expected), "optimalIterative(" + x + ", " + n + ") = " + optimalIterative(x, n));
    }

    public static void main(String[] args) {
        verify(2.0, 10, 1024.0);
        verify(2.1, 3, 9.261);
        verify(2.0, -2, 0.25);                         // negative exponent
        verify(5.0, 0, 1.0);                           // x^0
        verify(0.0, 5, 0.0);
        verify(-2.0, 31, -2147483648.0);               // odd power of a negative base stays negative
        verify(-2.0, 30, 1073741824.0);
        verify(0.5, 4, 0.0625);
        verify(3.0, 5, 243.0);
        verify(1.0, 1_000_000, 1.0);                   // a million multiplications is still fine for the loop
        verifyFast(1.0, Integer.MIN_VALUE, 1.0);       // the exponent whose absolute value overflows int
        verifyFast(-1.0, Integer.MIN_VALUE, 1.0);      // even exponent
        verifyFast(-1.0, Integer.MAX_VALUE, -1.0);     // odd exponent
        verifyFast(2.0, Integer.MIN_VALUE, 0.0);       // 2^(-2^31) underflows to 0
        verifyFast(0.00001, 2147483647, 0.0);
        System.out.println("OK P2855_PowXN");
    }
}
