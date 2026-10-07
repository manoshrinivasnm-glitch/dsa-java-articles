import java.util.*;

/** TUF 652 - Prime factorisation of n: its prime factors with multiplicity, in non-decreasing order. */
public class P652_PrimeFactorisationOfANumber {

    /** Approach 1: for d = 2, 3, 4, ... divide n by d as many times as possible. O(n) time when n is prime. */
    static List<Integer> bruteForce(int n) {
        List<Integer> factors = new ArrayList<>();
        for (int d = 2; d <= n; d++) {
            while (n % d == 0) {
                factors.add(d);
                n /= d;
            }
        }
        return factors;
    }

    /** Approach 2: trial division only up to sqrt(remaining n); whatever is left above 1 is itself prime. O(sqrt(n)) time. */
    static List<Integer> better(int n) {
        List<Integer> factors = new ArrayList<>();
        for (long d = 2; d * d <= n; d++) {
            while (n % d == 0) {
                factors.add((int) d);
                n /= d;
            }
        }
        if (n > 1) factors.add(n);
        return factors;
    }

    /** Precompute the smallest prime factor of every number up to limit (a sieve). O(limit log log limit) time, O(limit) space. */
    static int[] smallestPrimeFactors(int limit) {
        int[] spf = new int[limit + 1];
        for (int i = 2; i <= limit; i++) {
            if (spf[i] == 0) {                                // i is prime
                spf[i] = i;
                for (long j = (long) i * i; j <= limit; j += i) {
                    if (spf[(int) j] == 0) spf[(int) j] = i;  // first prime to reach j is its smallest factor
                }
            }
        }
        return spf;
    }

    /** Approach 3: with spf precomputed, peel off the smallest prime factor until n becomes 1. O(log n) time per query. */
    static List<Integer> optimal(int n, int[] spf) {
        List<Integer> factors = new ArrayList<>();
        while (n > 1) {
            int p = spf[n];
            factors.add(p);
            n /= p;
        }
        return factors;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static final int[] SPF = smallestPrimeFactors(1_000_000);

    static void verify(int n, List<Integer> expected, boolean includeBrute) {
        if (includeBrute) check(bruteForce(n).equals(expected), "bruteForce " + n + " -> " + bruteForce(n));
        check(better(n).equals(expected), "better " + n + " -> " + better(n));
        if (n < SPF.length) check(optimal(n, SPF).equals(expected), "optimal " + n + " -> " + optimal(n, SPF));
        long product = 1;
        for (int p : expected) product *= p;
        check(product == n || (n == 1 && expected.isEmpty()), "factors multiply back to " + n);
    }

    public static void main(String[] args) {
        verify(60, List.of(2, 2, 3, 5), true);
        verify(1, List.of(), true);                                        // 1 has an empty factorisation
        verify(2, List.of(2), true);
        verify(97, List.of(97), true);                                     // prime
        verify(1024, new ArrayList<>(Collections.nCopies(10, 2)), true);   // 2^10
        verify(12246, List.of(2, 3, 13, 157), true);
        verify(999_983, List.of(999_983), true);                           // largest prime below 10^6
        List<Integer> million = new ArrayList<>(Collections.nCopies(6, 2));
        million.addAll(Collections.nCopies(6, 5));
        verify(1_000_000, million, true);                                  // 10^6 = 2^6 * 5^6
        verify(2_147_483_647, List.of(2_147_483_647), false);              // Integer.MAX_VALUE is prime; needs long d * d
        verify(2_147_483_646, List.of(2, 3, 3, 7, 11, 31, 151, 331), false);
        verify(1_000_000_007, List.of(1_000_000_007), false);

        check(SPF[2] == 2 && SPF[4] == 2 && SPF[9] == 3 && SPF[15] == 3 && SPF[999_983] == 999_983, "spf table");
        System.out.println("OK P652_PrimeFactorisationOfANumber");
    }
}
