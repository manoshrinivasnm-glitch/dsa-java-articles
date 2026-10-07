import java.util.*;

/** TUF 222 - Count Good Numbers. Count digit strings of length n whose even indices hold even digits and odd indices hold prime digits, modulo 1e9+7. */
public class P222_CountGoodNumbers {

    static final long MOD = 1_000_000_007L;

    /** Approach 1: enumerate every digit string of length n and test each one. O(10^n * n) time; only to confirm the formula for tiny n. */
    static long bruteForce(long n) {
        if (n > 7) throw new IllegalArgumentException("brute force is only for n <= 7");
        int len = (int) n;
        long total = 1;
        for (int i = 0; i < len; i++) total *= 10;    // 10^len strings
        long count = 0;
        for (long v = 0; v < total; v++) {
            if (isGood(v, len)) count++;
        }
        return count % MOD;
    }

    /** v written with exactly len digits (leading zeros allowed): even index -> even digit, odd index -> 2, 3, 5 or 7. */
    static boolean isGood(long v, int len) {
        for (int index = len - 1; index >= 0; index--) {   // the last digit of v sits at the last index
            int d = (int) (v % 10);
            v /= 10;
            if (index % 2 == 0) {
                if (d % 2 != 0) return false;
            } else if (d != 2 && d != 3 && d != 5 && d != 7) {
                return false;
            }
        }
        return true;
    }

    /** Approach 2: multiply the number of choices position by position. O(n) time, O(1) space; fine for n up to about 10^7. */
    static long better(long n) {
        long result = 1;
        for (long index = 0; index < n; index++) {
            result = result * (index % 2 == 0 ? 5 : 4) % MOD;
        }
        return result;
    }

    /** Approach 3: closed form 5^ceil(n/2) * 4^floor(n/2) with recursive fast exponentiation. O(log n) time, O(log n) stack. */
    static long optimal(long n) {
        long evenIndices = (n + 1) / 2;               // indices 0, 2, 4, ...: 5 even digits each
        long oddIndices = n / 2;                      // indices 1, 3, 5, ...: 4 prime digits each
        return modPow(5, evenIndices) * modPow(4, oddIndices) % MOD;
    }

    /** base^exp mod MOD. Halve the exponent, square the result, multiply by base once more if exp is odd. */
    static long modPow(long base, long exp) {
        if (exp == 0) return 1;                        // base case: anything to the power 0 is 1
        long half = modPow(base, exp / 2);
        long result = half * half % MOD;
        if (exp % 2 == 1) result = result * base % MOD;
        return result;
    }

    /** Variant: pair each even index with the odd index after it (5 * 4 = 20 choices per pair), one more factor 5 if n is odd. */
    static long optimalPaired(long n) {
        long pairs = n / 2;
        long result = modPow(20, pairs);
        if (n % 2 == 1) result = result * 5 % MOD;
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(long n, long expected) {
        if (n <= 6) check(bruteForce(n) == expected, "bruteForce(" + n + ") = " + bruteForce(n));
        if (n <= 2_000_000) check(better(n) == expected, "better(" + n + ") = " + better(n));
        check(optimal(n) == expected, "optimal(" + n + ") = " + optimal(n));
        check(optimalPaired(n) == expected, "optimalPaired(" + n + ") = " + optimalPaired(n));
    }

    public static void main(String[] args) {
        verify(0, 1);                                   // the empty string is the only string of length 0
        verify(1, 5);
        verify(2, 20);
        verify(3, 100);
        verify(4, 400);
        verify(5, 2000);
        verify(10, 3_200_000);
        verify(50, 564908303);
        verify(100, 564490093);
        verify(1000, 36020987);
        verify(1_000_000, 171395901);
        verify(806166225460393L, 643535977);            // a large n: only the logarithmic methods run
        verify(1_000_000_000_000_000L, 711414395);      // n = 10^15, the largest allowed
        System.out.println("OK P222_CountGoodNumbers");
    }
}
