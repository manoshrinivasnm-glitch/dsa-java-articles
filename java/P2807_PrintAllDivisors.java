import java.util.*;

/** TUF 2807 - Print all Divisors. Return every positive divisor of n (n >= 1) in increasing order. */
public class P2807_PrintAllDivisors {

    /** Approach 1: test every candidate from 1 to n. O(n) time, O(k) space for the k divisors. */
    static List<Integer> bruteForce(int n) {
        List<Integer> result = new ArrayList<>();
        for (int d = 1; d <= n; d++) {
            if (n % d == 0) result.add(d);
        }
        return result;
    }

    /** Approach 2: only test d up to sqrt(n), adding both d and n / d, then sort. O(sqrt(n) + k log k) time, O(k) space. */
    static List<Integer> better(int n) {
        List<Integer> result = new ArrayList<>();
        for (int d = 1; (long) d * d <= n; d++) {
            if (n % d == 0) {
                result.add(d);
                if (d != n / d) result.add(n / d);
            }
        }
        Collections.sort(result);
        return result;
    }

    /** Approach 3: the same sqrt(n) loop, but the large partners go into a second list that is appended in reverse, so no sort is needed. O(sqrt(n)) time, O(k) space. */
    static List<Integer> optimal(int n) {
        List<Integer> small = new ArrayList<>();
        List<Integer> large = new ArrayList<>();
        for (int d = 1; (long) d * d <= n; d++) {
            if (n % d == 0) {
                small.add(d);
                if (d != n / d) large.add(n / d);
            }
        }
        for (int i = large.size() - 1; i >= 0; i--) small.add(large.get(i));
        return small;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, List<Integer> expected) {
        check(bruteForce(n).equals(expected), "bruteForce(" + n + ") = " + bruteForce(n));
        check(better(n).equals(expected), "better(" + n + ") = " + better(n));
        check(optimal(n).equals(expected), "optimal(" + n + ") = " + optimal(n));
    }

    public static void main(String[] args) {
        verify(1, List.of(1));                                       // smallest input
        verify(2, List.of(1, 2));
        verify(12, List.of(1, 2, 3, 4, 6, 12));
        verify(36, List.of(1, 2, 3, 4, 6, 9, 12, 18, 36));           // perfect square: 6 must appear once
        verify(13, List.of(1, 13));                                  // prime
        verify(100, List.of(1, 2, 4, 5, 10, 20, 25, 50, 100));
        verify(97, List.of(1, 97));
        for (int n = 1; n <= 2000; n++) {
            List<Integer> expected = bruteForce(n);
            check(better(n).equals(expected), "better disagrees at " + n);
            check(optimal(n).equals(expected), "optimal disagrees at " + n);
        }
        // Too large for the O(n) scan, but instant for the sqrt(n) versions.
        check(better(1_000_000_007).equals(List.of(1, 1_000_000_007)), "10^9+7 is prime");
        check(optimal(1_000_000_007).equals(List.of(1, 1_000_000_007)), "10^9+7 is prime");
        List<Integer> big = optimal(1_000_000_000);
        check(big.size() == 100 && big.equals(better(1_000_000_000)), "10^9 = 2^9 * 5^9 has 10 * 10 divisors");
        for (int i = 1; i < big.size(); i++) check(big.get(i - 1) < big.get(i), "not increasing");
        check(optimal(Integer.MAX_VALUE).equals(List.of(1, Integer.MAX_VALUE)), "2^31 - 1 is prime; d * d must be computed in long");
        check(better(Integer.MAX_VALUE).equals(List.of(1, Integer.MAX_VALUE)), "2^31 - 1 is prime; d * d must be computed in long");
        check(optimal(2_147_483_645).equals(better(2_147_483_645)) && optimal(2_147_483_645).contains(429_496_729), "2147483645 = 5 * 429496729");
        System.out.println("OK P2807_PrintAllDivisors");
    }
}
