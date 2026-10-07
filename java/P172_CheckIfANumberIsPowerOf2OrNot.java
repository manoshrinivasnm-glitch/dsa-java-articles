import java.util.*;

/** TUF 172 - Check if a Number is Power of 2 or Not. True for 1, 2, 4, 8, ...; false for 0 and negatives. */
public class P172_CheckIfANumberIsPowerOf2OrNot {

    /** Approach 1: keep halving while even; a power of two ends at exactly 1. O(log n) time. */
    static boolean bruteForce(int n) {
        if (n <= 0) return false;
        while (n % 2 == 0) n /= 2;
        return n == 1;
    }

    /** Approach 2: a power of two has exactly one set bit; count them. O(number of set bits) time. */
    static boolean better(int n) {
        if (n <= 0) return false;
        int count = 0;
        while (n != 0) {
            n &= n - 1;
            count++;
        }
        return count == 1;
    }

    /** Approach 3: n - 1 flips the lowest set bit and everything below it, so n & (n - 1) is 0 only for one set bit. O(1). */
    static boolean optimal(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /** Approach 3, variant: n & -n isolates the lowest set bit; it equals n only when that is the sole bit. O(1). */
    static boolean optimalLowestBit(int n) {
        return n > 0 && (n & -n) == n;
    }

    /** Approach 3, variant: 2^30 is the largest power of two in int range, and its divisors are exactly the powers of two. O(1). */
    static boolean optimalDivisor(int n) {
        return n > 0 && (1 << 30) % n == 0;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, boolean expected) {
        check(bruteForce(n) == expected, "bruteForce(" + n + ")");
        check(better(n) == expected, "better(" + n + ")");
        check(optimal(n) == expected, "optimal(" + n + ")");
        check(optimalLowestBit(n) == expected, "optimalLowestBit(" + n + ")");
        check(optimalDivisor(n) == expected, "optimalDivisor(" + n + ")");
    }

    public static void main(String[] args) {
        verify(1, true);                           // 2^0
        verify(2, true);
        verify(16, true);
        verify(1024, true);
        verify(1 << 30, true);                     // largest power of two that fits in an int
        verify(0, false);                          // edge: zero has no set bits, and 0 & -1 == 0 would fool a check without n > 0
        verify(-2, false);                         // negatives are never powers of two
        verify(-8, false);
        verify(Integer.MIN_VALUE, false);          // one set bit, but negative
        verify(3, false);
        verify(6, false);
        verify(12, false);
        verify((1 << 30) + 1, false);
        verify(Integer.MAX_VALUE, false);          // all 31 low bits set
        for (int n = -64; n <= 5000; n++) verify(n, n > 0 && Integer.bitCount(n) == 1);
        System.out.println("OK P172_CheckIfANumberIsPowerOf2OrNot");
    }
}
