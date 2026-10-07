import java.util.*;

/** TUF 367 - Count all Digits of a Number. Return how many decimal digits n has; 0 has one digit and the sign is ignored. */
public class P367_CountAllDigitsOfANumber {

    /** Approach 1: convert to a string and read its length. O(d) time, O(d) space. */
    static int bruteForce(int n) {
        long abs = Math.abs((long) n);
        return Long.toString(abs).length();
    }

    /** Approach 2: strip the last digit with integer division until nothing is left. O(d) time, O(1) space. */
    static int better(int n) {
        long x = Math.abs((long) n);
        if (x == 0) return 1;
        int count = 0;
        while (x > 0) {
            x /= 10;
            count++;
        }
        return count;
    }

    /** Approach 3: digits = floor(log10(n)) + 1. O(1) time, O(1) space. */
    static int optimal(int n) {
        long x = Math.abs((long) n);
        if (x == 0) return 1;
        return (int) Math.floor(Math.log10(x)) + 1;
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
        verify(0, 1);                          // zero still has one digit
        verify(7, 1);
        verify(10, 2);
        verify(12345, 5);
        verify(-987, 3);                       // the sign is not a digit
        verify(999_999_999, 9);
        verify(1_000_000_000, 10);             // exact power of ten: log10 must not come out as 8.999...
        verify(Integer.MAX_VALUE, 10);
        verify(Integer.MIN_VALUE, 10);         // Math.abs on the int would overflow, hence the long
        int p = 1;
        for (int d = 1; d <= 10; d++) {        // every power of ten from 1 to 10^9 ...
            verify(p, d);
            verify(p - 1, Math.max(d - 1, 1)); // ... and the number just below it
            if (d < 10) p *= 10;
        }
        System.out.println("OK P367_CountAllDigitsOfANumber");
    }
}
