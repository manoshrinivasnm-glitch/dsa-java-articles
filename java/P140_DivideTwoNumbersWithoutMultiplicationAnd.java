import java.util.*;

/** TUF 140 - Divide two numbers without multiplication and division (LeetCode 29).
 *  Return dividend / divisor truncated toward zero, using no *, / or %. The one overflowing case,
 *  MIN_VALUE / -1, is clamped to MAX_VALUE. divisor is never 0. */
public class P140_DivideTwoNumbersWithoutMultiplicationAnd {

    /** Attach the sign and clamp the magnitude into the int range. */
    static int toResult(long quotient, boolean negative) {
        if (negative) quotient = -quotient;
        if (quotient > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (quotient < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) quotient;
    }

    /** Approach 1: subtract the divisor until what is left is smaller than it. O(|quotient|) time. */
    static int bruteForce(int dividend, int divisor) {
        long a = Math.abs((long) dividend), b = Math.abs((long) divisor);
        long quotient = 0;
        while (a >= b) {
            a -= b;
            quotient++;
        }
        return toResult(quotient, (dividend < 0) != (divisor < 0));
    }

    /** Approach 2: each round takes away the largest divisor * 2^k that still fits. O(log^2 n) time. */
    static int better(int dividend, int divisor) {
        long a = Math.abs((long) dividend), b = Math.abs((long) divisor);
        long quotient = 0;
        while (a >= b) {
            long chunk = b, count = 1;
            while (a >= (chunk << 1)) {
                chunk <<= 1;
                count <<= 1;
            }
            a -= chunk;
            quotient += count;
        }
        return toResult(quotient, (dividend < 0) != (divisor < 0));
    }

    /** Approach 3: decide quotient bits from 31 down to 0; bit i is 1 exactly when b * 2^i still fits in a. O(32) time. */
    static int optimal(int dividend, int divisor) {
        long a = Math.abs((long) dividend), b = Math.abs((long) divisor);
        long quotient = 0;
        for (int i = 31; i >= 0; i--) {
            if ((a >> i) >= b) {            // same as a >= b << i, without risking a huge shift
                a -= b << i;
                quotient |= 1L << i;
            }
        }
        return toResult(quotient, (dividend < 0) != (divisor < 0));
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** All three approaches; only used where the quotient is small enough for the brute force. */
    static void verifyAll(int dividend, int divisor, int expected) {
        check(bruteForce(dividend, divisor) == expected, "bruteForce(" + dividend + ", " + divisor + ")");
        verifyFast(dividend, divisor, expected);
    }

    static void verifyFast(int dividend, int divisor, int expected) {
        check(better(dividend, divisor) == expected, "better(" + dividend + ", " + divisor + ")");
        check(optimal(dividend, divisor) == expected, "optimal(" + dividend + ", " + divisor + ")");
    }

    public static void main(String[] args) {
        verifyAll(10, 3, 3);
        verifyAll(7, -3, -2);                                  // truncation toward zero, not floor
        verifyAll(-7, 3, -2);
        verifyAll(-7, -3, 2);
        verifyAll(0, 5, 0);                                    // edge: zero dividend
        verifyAll(1, 1, 1);
        verifyAll(5, 10, 0);                                   // divisor larger than dividend
        verifyAll(-1, 1, -1);
        verifyAll(100, 7, 14);
        verifyAll(Integer.MAX_VALUE, Integer.MAX_VALUE, 1);
        verifyAll(Integer.MIN_VALUE, Integer.MIN_VALUE, 1);    // |MIN| only exists as a long
        verifyAll(Integer.MIN_VALUE, Integer.MAX_VALUE, -1);
        verifyAll(123_456_789, 12_345, 10_000);
        verifyFast(Integer.MIN_VALUE, -1, Integer.MAX_VALUE);  // the only overflow: 2^31 does not fit, clamp
        verifyFast(Integer.MIN_VALUE, 1, Integer.MIN_VALUE);
        verifyFast(Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        verifyFast(Integer.MAX_VALUE, -1, -Integer.MAX_VALUE);
        verifyFast(Integer.MIN_VALUE, 2, -1_073_741_824);
        verifyFast(Integer.MIN_VALUE, 3, -715_827_882);
        verifyFast(Integer.MAX_VALUE, 2, 1_073_741_823);
        verifyFast(1_000_000_000, 7, 142_857_142);
        verifyFast(-1_000_000_000, 3, -333_333_333);
        for (int dividend = -60; dividend <= 60; dividend++) {
            for (int divisor = -9; divisor <= 9; divisor++) {
                if (divisor != 0) verifyAll(dividend, divisor, dividend / divisor);   // Java's / truncates toward zero
            }
        }
        System.out.println("OK P140_DivideTwoNumbersWithoutMultiplicationAnd");
    }
}
