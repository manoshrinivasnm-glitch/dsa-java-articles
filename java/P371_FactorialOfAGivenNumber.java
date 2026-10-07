import java.util.*;

/** TUF 371 - Factorial of a given number. Return N! = 1 * 2 * ... * N, with 0! = 1. */
public class P371_FactorialOfAGivenNumber {

    /** Approach 1: multiply up in a loop. O(n) time, O(1) space. */
    static long iterative(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }

    /** Approach 2: functional recursion, n! = n * (n-1)!. O(n) time, O(n) stack. */
    static long recursive(int n) {
        if (n <= 1) return 1;                   // base case: 0! = 1! = 1
        return n * recursive(n - 1);
    }

    /** Approach 3: parameterised (tail) recursion carrying the product so far. O(n) time, O(n) stack. */
    static long tailRecursive(int n) {
        return multiplyDown(n, 1);
    }

    private static long multiplyDown(int i, long product) {
        if (i <= 1) return product;             // base case: everything has been multiplied in
        return multiplyDown(i - 1, product * i);
    }

    /** Variant asked on some sheets: every factorial number 1!, 2!, 3!, ... that is <= limit. O(k) time for k results. */
    static List<Long> factorialsUpTo(long limit) {
        List<Long> out = new ArrayList<>();
        long fact = 1;
        for (int i = 2; fact <= limit; i++) {
            out.add(fact);
            if (fact > Long.MAX_VALUE / i) break;   // the next factorial would overflow long
            fact *= i;
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, long expected) {
        check(iterative(n) == expected, "iterative failed for n = " + n);
        check(recursive(n) == expected, "recursive failed for n = " + n);
        check(tailRecursive(n) == expected, "tailRecursive failed for n = " + n);
    }

    public static void main(String[] args) {
        verify(0, 1);                               // edge: 0! = 1
        verify(1, 1);
        verify(5, 120);
        verify(10, 3_628_800L);
        verify(12, 479_001_600L);                   // the largest factorial that fits in int
        verify(13, 6_227_020_800L);                 // already needs long
        verify(20, 2_432_902_008_176_640_000L);     // the largest factorial that fits in long

        check(factorialsUpTo(0).isEmpty(), "no factorial number is <= 0");
        check(factorialsUpTo(1).equals(List.of(1L)), "limit 1");
        check(factorialsUpTo(6).equals(List.of(1L, 2L, 6L)), "limit 6");
        check(factorialsUpTo(100).equals(List.of(1L, 2L, 6L, 24L)), "limit 100");
        check(factorialsUpTo(Long.MAX_VALUE).size() == 20, "exactly 1! .. 20! fit in long");
        System.out.println("OK P371_FactorialOfAGivenNumber");
    }
}
