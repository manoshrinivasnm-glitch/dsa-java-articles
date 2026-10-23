import java.util.*;

/** TUF 2853 - Print the distinct prime factors of n in increasing order. */
public class P2853_PrintPrimeFactorsOfANumber {

    /** Approach 1: test every number from 2 to n; keep the ones that divide n and are prime. O(n * sqrt(n)) time. */
    static List<Integer> bruteForce(int n) {
        List<Integer> factors = new ArrayList<>();         // filled in increasing order of i
        for (int i = 2; i <= n; i++) {                     // every candidate factor of n
            if (n % i == 0 && isPrime(i)) factors.add(i);  // divides n and is prime -> a prime factor
        }
        return factors;
    }

    /** Trial-division primality test. O(sqrt(x)) time. */
    static boolean isPrime(int x) {
        if (x < 2) return false;             // 0 and 1 are not prime
        for (long d = 2; d * d <= x; d++) {  // a composite x has a divisor <= sqrt(x)
            if (x % d == 0) return false;    // found a divisor: not prime
        }
        return true;                         // no divisor up to sqrt(x)
    }

    /** Approach 2: only scan divisors up to sqrt(n); every divisor d brings its partner n / d; keep the prime ones. O(sqrt(n) * k) time for k divisors. */
    static List<Integer> better(int n) {
        List<Integer> factors = new ArrayList<>();
        for (long d = 2; d * d <= n; d++) {                  // pairs (d, n / d) with d <= sqrt(n)
            if (n % d == 0) {                                // d divides n, and so does its partner
                if (isPrime((int) d)) factors.add((int) d);  // keep the small divisor if it is prime
                int other = (int) (n / d);                   // the matching large divisor
                // keep it too if prime (skip d itself when d * d == n)
                if (other != d && isPrime(other)) factors.add(other);
            }
        }
        if (factors.isEmpty() && n >= 2) factors.add(n);     // no divisor up to sqrt(n): n itself is prime
        Collections.sort(factors);                           // partners were added out of order
        return factors;
    }

    /** Approach 3: whenever d divides n, record it and divide it out completely; composites then never divide n. O(sqrt(n)) time. */
    static List<Integer> optimal(int n) {
        List<Integer> factors = new ArrayList<>();
        for (long d = 2; d * d <= n; d++) {  // smallest remaining divisor is always prime
            if (n % d == 0) {                // d divides what is left -> d is a prime factor
                factors.add((int) d);
                while (n % d == 0) n /= d;   // strip every copy of d
            }
        }
        if (n > 1) factors.add(n);           // the leftover is one prime larger than sqrt(original n)
        return factors;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, List<Integer> expected, boolean includeBrute) {
        if (includeBrute) check(bruteForce(n).equals(expected), "bruteForce " + n + " -> " + bruteForce(n));
        check(better(n).equals(expected), "better " + n + " -> " + better(n));
        check(optimal(n).equals(expected), "optimal " + n + " -> " + optimal(n));
    }

    public static void main(String[] args) {
        verify(60, List.of(2, 3, 5), true);
        verify(1, List.of(), true);                                   // 1 has no prime factors
        verify(2, List.of(2), true);
        verify(97, List.of(97), true);                                // a prime is its own only factor
        verify(1024, List.of(2), true);                               // repeated factor listed once
        verify(1001, List.of(7, 11, 13), true);
        verify(360, List.of(2, 3, 5), true);
        verify(49, List.of(7), true);                                 // perfect square of a prime
        verify(2_147_483_647, List.of(2_147_483_647), false);         // Integer.MAX_VALUE is prime
        verify(2_147_483_646, List.of(2, 3, 7, 11, 31, 151, 331), false);
        verify(1_000_000_007, List.of(1_000_000_007), false);
        System.out.println("OK P2853_PrintPrimeFactorsOfANumber");
    }
}
