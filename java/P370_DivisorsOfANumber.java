import java.util.*;

/** TUF 370 - All divisors of a positive integer n, in increasing order. */
public class P370_DivisorsOfANumber {

    /** Approach 1: test every candidate from 1 to n. O(n) time, O(k) space for k divisors. */
    static List<Integer> bruteForce(int n) {
        List<Integer> divisors = new ArrayList<>();
        for (int d = 1; d <= n; d++) {        // every candidate from 1 to n
            if (n % d == 0) divisors.add(d);  // d divides n evenly, already in increasing order
        }
        return divisors;
    }

    /** Approach 2: divisors come in pairs (d, n / d), so scanning d up to sqrt(n) finds them all; sort at the end. O(sqrt(n) + k log k) time. */
    static List<Integer> better(int n) {
        List<Integer> divisors = new ArrayList<>();
        for (long d = 1; d * d <= n; d++) {                   // each pair (d, n / d) has d <= sqrt(n)
            if (n % d == 0) {                                 // d is a divisor, so n / d is one too
                divisors.add((int) d);                        // the small half of the pair
                if (d != n / d) divisors.add((int) (n / d));  // the large half, skipped when d * d == n
            }
        }
        Collections.sort(divisors);                           // the pairs were added out of order
        return divisors;
    }

    /** Approach 3: same scan, but the small halves are already increasing and the large halves decreasing, so merge instead of sorting. O(sqrt(n)) time. */
    static List<Integer> optimal(int n) {
        List<Integer> small = new ArrayList<>();           // divisors <= sqrt(n), found in increasing order
        List<Integer> large = new ArrayList<>();           // their partners n / d, found in decreasing order
        for (long d = 1; d * d <= n; d++) {                // each pair (d, n / d) has d <= sqrt(n)
            if (n % d == 0) {
                small.add((int) d);                        // the small half of the pair
                if (d != n / d) large.add((int) (n / d));  // the large half, skipped when d * d == n
            }
        }
        // append the large halves reversed -> fully sorted
        for (int i = large.size() - 1; i >= 0; i--) small.add(large.get(i));
        return small;
    }

    /** Variant: count the divisors without listing them. If n = p1^e1 * ... * pk^ek then d(n) = (e1 + 1) * ... * (ek + 1). O(sqrt(n)) time. */
    static int countDivisors(int n) {
        int count = 1;                       // product of (exponent + 1) over the primes found
        for (long p = 2; p * p <= n; p++) {  // smallest remaining divisor is always prime
            if (n % p == 0) {                // p is a prime factor of n
                int exponent = 0;            // how many times p divides n
                while (n % p == 0) {         // divide p out completely
                    n /= p;
                    exponent++;
                }
                count *= exponent + 1;       // e + 1 choices for the power of p in a divisor
            }
        }
        if (n > 1) count *= 2;               // one leftover prime with exponent 1
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, List<Integer> expected, boolean includeBrute) {
        if (includeBrute) check(bruteForce(n).equals(expected), "bruteForce " + n + " -> " + bruteForce(n));
        check(better(n).equals(expected), "better " + n + " -> " + better(n));
        check(optimal(n).equals(expected), "optimal " + n + " -> " + optimal(n));
        check(countDivisors(n) == expected.size(), "countDivisors " + n + " -> " + countDivisors(n));
    }

    public static void main(String[] args) {
        verify(36, List.of(1, 2, 3, 4, 6, 9, 12, 18, 36), true);          // perfect square: 6 appears once
        verify(1, List.of(1), true);                                       // smallest input
        verify(7, List.of(1, 7), true);                                    // prime
        verify(16, List.of(1, 2, 4, 8, 16), true);
        verify(100, List.of(1, 2, 4, 5, 10, 20, 25, 50, 100), true);
        verify(12, List.of(1, 2, 3, 4, 6, 12), true);
        verify(2_147_483_647, List.of(1, 2_147_483_647), false);           // Integer.MAX_VALUE is prime; d * d must not overflow

        // 735134400 = 2^6 * 3^3 * 5^2 * 7 * 11 * 13 * 17 has 7 * 4 * 3 * 2 * 2 * 2 * 2 = 1344 divisors
        List<Integer> many = optimal(735_134_400);
        check(many.size() == 1344 && many.equals(better(735_134_400)), "735134400 has 1344 divisors");
        check(countDivisors(735_134_400) == 1344, "countDivisors(735134400)");
        for (int i = 1; i < many.size(); i++) check(many.get(i - 1) < many.get(i), "divisors strictly increasing");
        System.out.println("OK P370_DivisorsOfANumber");
    }
}
