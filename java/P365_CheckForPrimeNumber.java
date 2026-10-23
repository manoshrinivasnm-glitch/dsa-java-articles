import java.util.*;

/** TUF 365 - Check for Prime Number. Return true when n > 1 and its only positive divisors are 1 and n. */
public class P365_CheckForPrimeNumber {

    /** Approach 1: count the divisors from 1 to n; a prime has exactly two. O(n) time, O(1) space. */
    static boolean bruteForce(int n) {
        if (n < 2) return false;        // 0, 1 and negatives are not prime
        int divisors = 0;               // how many numbers divide n
        for (int d = 1; d <= n; d++) {  // test every d from 1 to n
            if (n % d == 0) divisors++;
        }
        return divisors == 2;           // prime: only 1 and n divide it
    }

    /** Approach 2: divisors come in pairs (d, n / d), so a divisor <= sqrt(n) exists whenever any exists. O(sqrt(n)) time, O(1) space. */
    static boolean better(int n) {
        if (n < 2) return false;                   // 0, 1 and negatives are not prime
        for (int d = 2; (long) d * d <= n; d++) {  // any factor pair has one member <= sqrt(n)
            if (n % d == 0) return false;          // found a divisor: not prime
        }
        return true;                               // no divisor up to sqrt(n)
    }

    /** Approach 3: deal with 2 and 3, then test only candidates of the form 6k - 1 and 6k + 1. O(sqrt(n)) time with a third of the work, O(1) space. */
    static boolean optimal(int n) {
        if (n < 2) return false;                               // 0, 1 and negatives are not prime
        if (n < 4) return true;                                // 2 and 3 are prime
        if (n % 2 == 0 || n % 3 == 0) return false;            // rule out multiples of 2 and 3
        for (int d = 5; (long) d * d <= n; d += 6) {           // other primes have the form 6k - 1 or 6k + 1
            if (n % d == 0 || n % (d + 2) == 0) return false;  // test d = 6k - 1 and d + 2 = 6k + 1
        }
        return true;                                           // no divisor up to sqrt(n)
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
        verify(0, false);                            // not prime by definition
        verify(1, false);                            // the classic trap: 1 is not prime
        verify(2, true);                             // the only even prime
        verify(3, true);
        verify(4, false);
        verify(9, false);                            // 3 * 3, catches loops that start at d = 2 and stop too early
        verify(25, false);                           // d * d == n must still be tested
        verify(29, true);
        verify(49, false);                           // 7 * 7, where 7 = 6k + 1
        verify(97, true);
        verify(121, false);                          // 11 * 11, where 11 = 6k - 1
        verify(7_919, true);                         // the 1000th prime
        int count = 0;
        for (int n = 0; n <= 10_000; n++) {
            boolean p = bruteForce(n);
            check(better(n) == p && optimal(n) == p, "approaches disagree at " + n);
            if (p) count++;
        }
        check(count == 1229, "there are 1229 primes below 10000, got " + count);
        // Too large for the O(n) scan, but instant for the sqrt(n) versions.
        check(better(1_000_000_007) && optimal(1_000_000_007), "10^9 + 7 is prime");
        check(!better(1_000_000_008) && !optimal(1_000_000_008), "10^9 + 8 is even");
        check(better(Integer.MAX_VALUE) && optimal(Integer.MAX_VALUE), "2^31 - 1 is a Mersenne prime; d * d must not overflow");
        check(!better(2_147_483_645) && !optimal(2_147_483_645), "2147483645 = 5 * 429496729");
        System.out.println("OK P365_CheckForPrimeNumber");
    }
}
