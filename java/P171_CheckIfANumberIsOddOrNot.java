import java.util.*;

/** TUF 171 - Check if a Number is Odd or Not. Returns true for odd n, including negative odd numbers. */
public class P171_CheckIfANumberIsOddOrNot {

    /** Approach 1: remainder on division by 2. Compare with 0, not with 1, so negatives work. O(1). */
    static boolean usingModulo(int n) {
        return n % 2 != 0;
    }

    /** Approach 2: integer division drops the remainder; multiplying back recovers n only when it was even. O(1). */
    static boolean usingDivision(int n) {
        return n / 2 * 2 != n;
    }

    /** Approach 3: the lowest bit is the only odd power of two, so it alone decides parity. O(1). */
    static boolean usingBitwiseAnd(int n) {
        return (n & 1) == 1;
    }

    /** The classic bug: -3 % 2 is -1 in Java, so this reports negative odd numbers as even. */
    static boolean naiveModuloEqualsOne(int n) {
        return n % 2 == 1;
    }

    /** Generalisation: the low k bits are n mod 2^k, always in [0, 2^k), even for negative n. */
    static int lowBits(int n, int k) {
        return n & ((1 << k) - 1);
    }

    /** n is divisible by 2^k exactly when its low k bits are all zero. */
    static boolean isDivisibleByPowerOfTwo(int n, int k) {
        return (n & ((1 << k) - 1)) == 0;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, boolean expectedOdd) {
        check(usingModulo(n) == expectedOdd, "usingModulo(" + n + ")");
        check(usingDivision(n) == expectedOdd, "usingDivision(" + n + ")");
        check(usingBitwiseAnd(n) == expectedOdd, "usingBitwiseAnd(" + n + ")");
    }

    public static void main(String[] args) {
        verify(0, false);                          // zero is even
        verify(1, true);
        verify(2, false);
        verify(7, true);
        verify(10, false);
        verify(-3, true);                          // negative odd
        verify(-4, false);                         // negative even
        verify(1_000_000_001, true);
        verify(Integer.MAX_VALUE, true);           // 2^31 - 1 is odd
        verify(Integer.MIN_VALUE, false);          // -2^31 is even
        for (int n = -1000; n <= 1000; n++) verify(n, Math.floorMod(n, 2) == 1);
        check(naiveModuloEqualsOne(3) && !naiveModuloEqualsOne(-3), "n % 2 == 1 misses negative odd numbers");
        check(lowBits(13, 2) == 1, "1101 & 11 = 01");
        check(lowBits(-1, 3) == 7, "-1 has all low bits set");
        check(lowBits(-6, 2) == 2 && -6 % 4 == -2, "& gives the non-negative residue, % does not");
        check(lowBits(8, 0) == 0, "zero low bits");
        check(isDivisibleByPowerOfTwo(40, 3), "40 = 8 * 5");
        check(!isDivisibleByPowerOfTwo(12, 3), "12 is not a multiple of 8");
        check(isDivisibleByPowerOfTwo(0, 5), "0 is a multiple of everything");
        check(isDivisibleByPowerOfTwo(-16, 4), "-16 = 16 * -1");
        System.out.println("OK P171_CheckIfANumberIsOddOrNot");
    }
}
