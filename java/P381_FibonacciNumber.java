import java.util.*;

/** TUF 381 - Fibonacci Number (LeetCode 509). F(0) = 0, F(1) = 1, F(n) = F(n-1) + F(n-2). */
public class P381_FibonacciNumber {

    /** Approach 1: recursion straight from the definition. O(2^n) time, O(n) stack. */
    static long naiveRecursion(int n) {
        if (n <= 1) return n;                                   // base cases F(0) = 0, F(1) = 1
        return naiveRecursion(n - 1) + naiveRecursion(n - 2);   // two branches per call: exponential work
    }

    /** Approach 2: memoised recursion, every F(k) is computed once and cached. O(n) time, O(n) space. */
    static long memoised(int n) {
        long[] memo = new long[Math.max(n + 1, 2)];             // memo[k] caches F(k)
        Arrays.fill(memo, -1);                                  // -1 marks "not computed yet"
        return fibMemo(n, memo);
    }

    private static long fibMemo(int n, long[] memo) {
        if (n <= 1) return n;                                   // base cases F(0) = 0, F(1) = 1
        if (memo[n] != -1) return memo[n];                      // already computed: reuse it
        memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);  // compute once and store
        return memo[n];
    }

    /** Approach 3: iterate with two running values. O(n) time, O(1) space. */
    static long iterative(int n) {
        if (n <= 1) return n;                                   // F(0) = 0, F(1) = 1
        long prev = 0, cur = 1;                                 // F(0), F(1)
        for (int i = 2; i <= n; i++) {                          // build F(2), F(3), ..., F(n) in order
            long next = prev + cur;                             // F(i) = F(i-1) + F(i-2)
            prev = cur;                                         // slide the window forward by one
            cur = next;
        }
        return cur;                                             // cur now holds F(n)
    }

    /** Approach 4: fast doubling, F(2k) = F(k)(2F(k+1) - F(k)) and F(2k+1) = F(k)^2 + F(k+1)^2. O(log n) time, O(log n) stack. */
    static long fastDoubling(int n) {
        return fibPair(n)[0];                                   // first half of {F(n), F(n+1)}
    }

    /** Returns {F(n), F(n+1)}. */
    private static long[] fibPair(int n) {
        if (n == 0) return new long[]{0, 1};                    // base case: {F(0), F(1)}
        long[] half = fibPair(n / 2);                           // solve for k = n / 2, halving n each call
        long a = half[0], b = half[1];                          // a = F(k), b = F(k+1) with k = n / 2
        long c = a * (2 * b - a);                               // F(2k)
        long d = a * a + b * b;                                 // F(2k+1)
        if (n % 2 == 0) return new long[]{c, d};                // n = 2k:     {F(2k), F(2k+1)}
        return new long[]{d, c + d};                            // n = 2k + 1: {F(2k+1), F(2k+2)}
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyAll(int n, long expected) {
        check(naiveRecursion(n) == expected, "naiveRecursion failed for n = " + n);
        verifyFast(n, expected);
    }

    static void verifyFast(int n, long expected) {
        check(memoised(n) == expected, "memoised failed for n = " + n);
        check(iterative(n) == expected, "iterative failed for n = " + n);
        check(fastDoubling(n) == expected, "fastDoubling failed for n = " + n);
    }

    public static void main(String[] args) {
        verifyAll(0, 0);                        // edge: F(0)
        verifyAll(1, 1);                        // edge: F(1)
        verifyAll(2, 1);
        verifyAll(3, 2);
        verifyAll(4, 3);
        verifyAll(10, 55);
        verifyAll(20, 6_765);
        verifyAll(30, 832_040);                 // about 1.6 million naive calls, still fast
        // Beyond n = 30 the naive version is far too slow; the other three agree on the big values.
        verifyFast(50, 12_586_269_025L);
        verifyFast(70, 190_392_490_709_135L);
        verifyFast(90, 2_880_067_194_370_816_120L);   // F(92) is the last Fibonacci number that fits in long
        System.out.println("OK P381_FibonacciNumber");
    }
}
