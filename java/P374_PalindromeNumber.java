import java.util.*;

/** TUF 374 - Palindrome Number. Return true when n reads the same forwards and backwards; negative numbers are not palindromes. */
public class P374_PalindromeNumber {

    /** Approach 1: compare the decimal string with its reverse. O(d) time, O(d) space. */
    static boolean bruteForce(int n) {
        if (n < 0) return false;    // the leading '-' has no match at the end
        String s = Integer.toString(n);
        String reversed = new StringBuilder(s).reverse().toString();   // the digits read backwards
        return s.equals(reversed);  // palindrome if both directions agree
    }

    /** Approach 2: reverse the whole number arithmetically (in a long) and compare. O(d) time, O(1) space. */
    static boolean better(int n) {
        if (n < 0) return false;      // the leading '-' has no match at the end
        long rev = 0;                 // long: reversing a large int can overflow
        int x = n;                    // work on a copy, n is needed for the final check
        while (x > 0) {               // peel digits off the right end
            rev = rev * 10 + x % 10;  // append x's last digit to rev
            x /= 10;
        }
        return rev == n;              // palindrome if reversing changes nothing
    }

    /** Approach 3: reverse only the second half and compare it with the first half. O(d) time, O(1) space, no overflow possible. */
    static boolean optimal(int n) {
        // negatives fail; so does a number ending in 0, unless it is 0 itself
        if (n < 0 || (n % 10 == 0 && n != 0)) return false;
        int rev = 0;                       // reverse of the right half only
        while (n > rev) {                  // stop once half the digits have moved to rev
            rev = rev * 10 + n % 10;       // move n's last digit onto rev
            n /= 10;
        }
        return n == rev || n == rev / 10;  // even length: halves match; odd: ignore rev's middle digit
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, boolean expected) {
        check(bruteForce(n) == expected, "bruteForce(" + n + ") expected " + expected);
        check(better(n) == expected, "better(" + n + ") expected " + expected);
        check(optimal(n) == expected, "optimal(" + n + ") expected " + expected);
    }

    public static void main(String[] args) {
        verify(121, true);
        verify(-121, false);                  // reads 121- backwards
        verify(10, false);                    // 01 is not 10
        verify(0, true);                      // single digit, and the only palindrome ending in 0
        verify(7, true);
        verify(11, true);
        verify(1221, true);                   // even length
        verify(12321, true);                  // odd length
        verify(1001, true);                   // zeros in the middle
        verify(123, false);
        verify(100, false);
        verify(1_000_021, false);
        verify(2_147_447_412, true);          // largest palindrome that fits in int; its reverse overflows nothing here
        verify(Integer.MAX_VALUE, false);     // 2147483647 reversed is 7463847412, which overflows int
        System.out.println("OK P374_PalindromeNumber");
    }
}
