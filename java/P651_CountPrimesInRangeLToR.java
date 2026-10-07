import java.util.*;

/** TUF 651 - Count the primes in [L, R], for one range and for many ranges at once. */
public class P651_CountPrimesInRangeLToR {

    /** Approach 1: trial-divide every number in the range. O((R - L + 1) * sqrt(R)) time, O(1) space. */
    static int bruteForce(int l, int r) {
        int count = 0;
        for (int x = l; x <= r; x++) {
            if (isPrime(x)) count++;
        }
        return count;
    }

    /** Trial-division primality test. O(sqrt(x)) time. */
    static boolean isPrime(int x) {
        if (x < 2) return false;
        for (long d = 2; d * d <= x; d++) {
            if (x % d == 0) return false;
        }
        return true;
    }

    /** Approach 2: sieve of Eratosthenes up to R, then count the unmarked numbers in [L, R]. O(R log log R) time, O(R) space. */
    static int better(int l, int r) {
        boolean[] composite = sieve(r);
        int count = 0;
        for (int x = Math.max(l, 2); x <= r; x++) {
            if (!composite[x]) count++;
        }
        return count;
    }

    /** composite[x] is true when x is not prime, for 0 <= x <= n (0 and 1 are marked as not prime). */
    static boolean[] sieve(int n) {
        boolean[] composite = new boolean[Math.max(n, 1) + 1];
        composite[0] = true;
        composite[1] = true;
        for (long p = 2; p * p <= n; p++) {
            if (!composite[(int) p]) {
                for (long multiple = p * p; multiple <= n; multiple += p) composite[(int) multiple] = true;
            }
        }
        return composite;
    }

    /** Approach 3: sieve once up to the largest R, build prefix counts, then answer every query in O(1). O(N log log N + Q) time. */
    static int[] optimal(int[][] queries) {
        int maxR = 0;
        for (int[] q : queries) maxR = Math.max(maxR, q[1]);
        boolean[] composite = sieve(maxR);
        int[] primesUpTo = new int[maxR + 1];              // primesUpTo[x] = number of primes in [0, x]
        for (int x = 1; x <= maxR; x++) primesUpTo[x] = primesUpTo[x - 1] + (composite[x] ? 0 : 1);
        int[] answers = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int l = queries[i][0], r = queries[i][1];
            answers[i] = primesUpTo[r] - (l > 0 ? primesUpTo[l - 1] : 0);
        }
        return answers;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int l, int r, int expected, boolean includeBrute) {
        String label = "[" + l + ", " + r + "]";
        if (includeBrute) check(bruteForce(l, r) == expected, "bruteForce " + label);
        check(better(l, r) == expected, "better " + label);
        check(optimal(new int[][]{{l, r}})[0] == expected, "optimal " + label);
    }

    public static void main(String[] args) {
        verify(1, 10, 4, true);                 // 2, 3, 5, 7
        verify(10, 20, 4, true);                // 11, 13, 17, 19
        verify(2, 2, 1, true);                  // L == R and prime
        verify(1, 1, 0, true);                  // 1 is not prime
        verify(0, 1, 0, true);
        verify(14, 16, 0, true);                // a range with no primes
        verify(1, 100, 25, true);
        verify(1, 1_000, 168, true);
        verify(1, 100_000, 9592, true);
        verify(1, 1_000_000, 78498, false);     // too slow for trial division, fine for a sieve
        verify(999_900, 1_000_000, 8, false);   // 999907 999917 999931 999953 999959 999961 999979 999983

        // many ranges share one sieve
        int[][] queries = {{1, 10}, {10, 20}, {1, 100}, {50, 60}, {0, 0}};
        check(Arrays.equals(optimal(queries), new int[]{4, 4, 25, 2, 0}), "batched queries");
        System.out.println("OK P651_CountPrimesInRangeLToR");
    }
}
