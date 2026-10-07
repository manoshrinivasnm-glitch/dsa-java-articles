import java.util.*;

/** TUF 877 - Pow(x, n): compute x^n for a double x and a 32-bit integer n, which may be negative. */
public class P877_PowXN {

    /** Approach 1: multiply x by itself |n| times, invert at the end when n is negative. O(|n|) time, O(1) space. */
    static double bruteForce(double x, int n) {
        long times = Math.abs((long) n);          // long: Math.abs(Integer.MIN_VALUE) stays negative in int
        double result = 1.0;
        for (long i = 0; i < times; i++) result *= x;
        return n < 0 ? 1.0 / result : result;
    }

    /** Approach 2: recursive binary exponentiation: x^n = (x^(n/2))^2, times x once more when n is odd. O(log n) time and stack. */
    static double better(double x, int n) {
        long exponent = n;                        // long so that -Integer.MIN_VALUE is representable
        if (exponent < 0) {
            x = 1.0 / x;
            exponent = -exponent;
        }
        return powRecursive(x, exponent);
    }

    private static double powRecursive(double x, long n) {
        if (n == 0) return 1.0;
        double half = powRecursive(x, n / 2);
        return (n % 2 == 0) ? half * half : half * half * x;
    }

    /** Approach 3: iterative binary exponentiation over the bits of n. O(log n) time, O(1) space. */
    static double optimal(double x, int n) {
        long exponent = n;
        if (exponent < 0) {
            x = 1.0 / x;
            exponent = -exponent;
        }
        double result = 1.0;
        while (exponent > 0) {
            if ((exponent & 1) == 1) result *= x;   // this bit of n is set, so x^(2^k) belongs in the answer
            x *= x;                                 // x^(2^k) -> x^(2^(k+1))
            exponent >>= 1;
        }
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static boolean close(double got, double expected) {
        return Math.abs(got - expected) <= 1e-9 * Math.max(1.0, Math.abs(expected));
    }

    static void verify(double x, int n, double expected, boolean includeBrute) {
        String label = "pow(" + x + ", " + n + ")";
        if (includeBrute) check(close(bruteForce(x, n), expected), "bruteForce " + label + " = " + bruteForce(x, n));
        check(close(better(x, n), expected), "better " + label + " = " + better(x, n));
        check(close(optimal(x, n), expected), "optimal " + label + " = " + optimal(x, n));
    }

    public static void main(String[] args) {
        verify(2.0, 10, 1024.0, true);
        verify(2.1, 3, 9.261, true);
        verify(2.0, -2, 0.25, true);                          // negative exponent
        verify(5.0, 0, 1.0, true);                            // anything to the power 0
        verify(0.0, 0, 1.0, true);                            // 0^0 is 1 by convention here
        verify(0.0, 5, 0.0, true);
        verify(-2.0, 3, -8.0, true);                          // odd power keeps the sign
        verify(-2.0, 4, 16.0, true);                          // even power drops it
        verify(0.5, 3, 0.125, true);
        verify(2.0, 31, 2147483648.0, true);                  // past int range, fine as a double
        verify(1.0, Integer.MIN_VALUE, 1.0, false);           // -n would overflow int; brute force would take 2^31 steps
        verify(2.0, Integer.MIN_VALUE, 0.0, false);           // underflows to 0
        verify(-1.0, Integer.MAX_VALUE, -1.0, false);         // odd exponent
        verify(-1.0, Integer.MIN_VALUE, 1.0, false);          // even exponent
        System.out.println("OK P877_PowXN");
    }
}
