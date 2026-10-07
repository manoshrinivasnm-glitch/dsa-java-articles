import java.util.*;

/** TUF 376 - Reverse a number. Reverse the decimal digits of n keeping its sign; return 0 if the reversed value does not fit in a 32-bit int. */
public class P376_ReverseANumber {

    /** Approach 1: write the digits as a string, reverse it, parse it back. O(d) time, O(d) space. */
    static int bruteForce(int n) {
        long abs = Math.abs((long) n);
        String reversed = new StringBuilder(Long.toString(abs)).reverse().toString();
        long result = Long.parseLong(reversed);
        if (n < 0) result = -result;
        if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) return 0;
        return (int) result;
    }

    /** Approach 2: peel digits with % 10 and push them onto a long accumulator, check the range at the end. O(d) time, O(1) space. */
    static int better(int n) {
        long x = Math.abs((long) n);
        long rev = 0;
        while (x > 0) {
            rev = rev * 10 + x % 10;
            x /= 10;
        }
        if (n < 0) rev = -rev;
        if (rev < Integer.MIN_VALUE || rev > Integer.MAX_VALUE) return 0;
        return (int) rev;
    }

    /** Approach 3: int arithmetic only, checking for overflow before each multiply by 10. O(d) time, O(1) space. */
    static int optimal(int n) {
        int rev = 0;
        while (n != 0) {
            int digit = n % 10;                      // negative when n is negative, so the sign takes care of itself
            n /= 10;
            if (rev > Integer.MAX_VALUE / 10 || (rev == Integer.MAX_VALUE / 10 && digit > 7)) return 0;
            if (rev < Integer.MIN_VALUE / 10 || (rev == Integer.MIN_VALUE / 10 && digit < -8)) return 0;
            rev = rev * 10 + digit;
        }
        return rev;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int expected) {
        check(bruteForce(n) == expected, "bruteForce(" + n + ") = " + bruteForce(n) + ", expected " + expected);
        check(better(n) == expected, "better(" + n + ") = " + better(n) + ", expected " + expected);
        check(optimal(n) == expected, "optimal(" + n + ") = " + optimal(n) + ", expected " + expected);
    }

    public static void main(String[] args) {
        verify(123, 321);
        verify(-123, -321);                        // sign is kept
        verify(120, 21);                           // trailing zeros disappear
        verify(0, 0);
        verify(7, 7);                              // single digit
        verify(1_000_000_000, 1);
        verify(1_000_000_003, 0);                  // 3000000001 does not fit in int
        verify(1_534_236_469, 0);                  // the classic LeetCode overflow case, 9646324351
        verify(Integer.MAX_VALUE, 0);              // 7463847412
        verify(Integer.MIN_VALUE, 0);              // -8463847412
        verify(1_463_847_412, 2_147_483_641);      // largest input whose reverse still fits
        verify(-1_463_847_412, -2_147_483_641);
        verify(-100, -1);
        System.out.println("OK P376_ReverseANumber");
    }
}
